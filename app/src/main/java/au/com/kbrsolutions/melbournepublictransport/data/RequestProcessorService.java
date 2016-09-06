package au.com.kbrsolutions.melbournepublictransport.data;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import au.com.kbrsolutions.melbournepublictransport.events.MainActivityEvents;


public class RequestProcessorService extends IntentService {

    private EventBus eventBus;
    public final static String  ACTION = "action";
    public final static String  REFRESH_DATA = "refresh_data";

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
            Log.v(TAG, "onHandleIntent - ni is null: " + ni);

            sendMessageToSpotifyStreamerActivity(new MainActivityEvents.Builder(MainActivityEvents.MainEvents.NETWORK_STATUS)
                    .setMsg("NO NETWORK CONNECTION")
                    .build());
            return;
        }

        Log.v(TAG, "onHandleIntent - action: " + action);
        if (action != null) {
            boolean databaseOK = DatabaseContentRefresher.performHealthCheck();
            if (!databaseOK) {
                sendMessageToSpotifyStreamerActivity(new MainActivityEvents.Builder(MainActivityEvents.MainEvents.NETWORK_STATUS)
                        .setMsg("CANNOT ACCESS MPT site")
                        .build());
            } else {
                switch (action) {
                    case REFRESH_DATA:
                        DatabaseContentRefresher.refreshDatabase(getContentResolver());
                        break;

                    default:
                        throw new RuntimeException(TAG + ".onHandleIntent - no code to handle action: " + action);
                }
            }
        }
    }

    private void sendMessageToSpotifyStreamerActivity(MainActivityEvents event) {
        EventBus.getDefault().post(event);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(MainActivityEvents event) {
        MainActivityEvents.MainEvents requestEvent = event.event;
        switch (requestEvent) {

            case NETWORK_STATUS:
//                showSnackBar(event.msg, true);
                break;

            default:
                throw new RuntimeException("LOC_CAT_TAG - onEvent - no code to handle requestEvent: " + requestEvent);
        }
    }
}
