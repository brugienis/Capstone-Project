package au.com.kbrsolutions.melbournepublictransport.data;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.events.MainActivityEvents;
import au.com.kbrsolutions.melbournepublictransport.events.RequestProcessorServiceRequestEvents;
import au.com.kbrsolutions.melbournepublictransport.remote.RemoteMptEndpointUtil;


public class RequestProcessorService extends IntentService {

    private EventBus eventBus;
    public final static String  ACTION = "action";
    public final static String  REFRESH_DATA = "refresh_data";
    public final static String  REFRESH_DATA_IF_TABLES_EMPTY = "refresh_data_if_tables_empty";
    public final static String  SHOW_NEXT_DEPARTURES = "show_next_departures";
    public final static String  MODE = "mode";
    public final static String  STOP_ID = "stop_id";
    public final static String  LIMIT = "limit";

    private static final String TAG = RequestProcessorService.class.getSimpleName();

    public RequestProcessorService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (eventBus == null) {
            eventBus = EventBus.getDefault();
            eventBus.register(this);
        }

        Bundle extras = intent.getExtras();
        String action = null;
        if (extras != null) {
            action = extras.getString(ACTION);
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
//            Log.v(TAG, "onHandleIntent - ni is null: " + ni);
            sendMessageToMainrActivity(new MainActivityEvents.Builder(MainActivityEvents.MainEvents.NETWORK_STATUS)
                    .setMsg("NO NETWORK CONNECTION")
                    .build());
            return;
        }

//        Log.v(TAG, "onHandleIntent - action: " + action);
        if (action != null) {
            boolean databaseOK = DatabaseContentRefresher.performHealthCheck();
            if (!databaseOK) {
                sendMessageToMainrActivity(new MainActivityEvents.Builder(MainActivityEvents.MainEvents.NETWORK_STATUS)
                        .setMsg("CANNOT ACCESS MPT site")
                        .build());
            } else {
                int mode;
                String stopId;
                int limit;
                switch (action) {
                    case REFRESH_DATA:
                        DatabaseContentRefresher.refreshDatabase(getContentResolver(), false);
                        break;

                    case REFRESH_DATA_IF_TABLES_EMPTY:
                        DatabaseContentRefresher.refreshDatabase(getContentResolver(), true);
                        break;

                    case SHOW_NEXT_DEPARTURES:
                        mode = extras.getInt(MODE);
                        stopId = extras.getString(STOP_ID);
                        limit = extras.getInt(LIMIT);
//                        Log.v(TAG, "onHandleIntent - action/mode/stopId/limit : " + mode + "/" + stopId + "/" + limit);
                        List<NextDepartureDetails> nextDepartureDetailsList = RemoteMptEndpointUtil.getBroadNextDepartures(mode, stopId, limit);
//                        List<NextDepartureDetails> nextDepartureDetailsList = buildSimulatedDepartureDetails();

                        sendMessageToMainrActivity(new MainActivityEvents.Builder(MainActivityEvents.MainEvents.NEXT_DEPARTURES_DETAILS)
                                .setNextDepartureDetailsList(nextDepartureDetailsList)
                                .build());
                        break;

                    default:
                        throw new RuntimeException(TAG + ".onHandleIntent - no code to handle action: " + action);
                }
            }
        }
    }

    private void sendMessageToMainrActivity(MainActivityEvents event) {
        EventBus.getDefault().post(event);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(RequestProcessorServiceRequestEvents event) {
    }

    private static int cnt;
    private List<NextDepartureDetails> buildSimulatedDepartureDetails() {
        List<NextDepartureDetails> nextDepartureDetailsList = new ArrayList<>();
        NextDepartureDetails
                nextDepartureDetails = new NextDepartureDetails(
                cnt,
                "to city " + cnt,
                101,
                0,
                6,
                "09:50");
        nextDepartureDetailsList.add(nextDepartureDetails);
        nextDepartureDetails = new NextDepartureDetails(
                cnt,
                "to frankston " + cnt,
                101,
                5,
                6,
                "10:05");
        nextDepartureDetailsList.add(nextDepartureDetails);
        cnt++;
        return nextDepartureDetailsList;
    }
}
