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

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.events.MainActivityEvents;
import au.com.kbrsolutions.melbournepublictransport.events.RequestProcessorServiceRequestEvents;
import au.com.kbrsolutions.melbournepublictransport.remote.RemoteMptEndpointUtil;
import au.com.kbrsolutions.melbournepublictransport.utilities.DbUtility;

import static au.com.kbrsolutions.melbournepublictransport.data.DatabaseContentRefresher.testProgressBar;


public class RequestProcessorService extends IntentService {

    private EventBus eventBus;
    private DbUtility dbUtility;
    boolean mTestDatabaseLoaded = false;
    boolean mTestDatabaseEmpty = true;

    private final static boolean REFRESH_TEST = false;

    public final static String REQUEST = "request";
    // FIXME: 10/10/2016 add ACTION_prefix to all action constants
    public final static String ACTION_REFRESH_DATA = "action_refresh_data";
    public final static String ACTION_GET_DATABASE_STATUS = "action_get_dadabase_status";
    public static final String GET_DISRUPTIONS_DETAILS = "get_disruptions_details";
    public final static String SHOW_NEXT_DEPARTURES = "show_next_departures";
    public final static String GET_NEARBY_STOPS_DETAILS = "get_nearby_details";
    public final static String GET_TRAIN_NEARBY_STOPS_DETAILS = "get_train_nearby_stops_details";
    public final static String UPDATE_STOPS_DETAILS = "update_stops_details";

    public final static String MODES = "modes";
    public final static String STOP_ID = "stop_id";
    public final static String LIMIT = "limit";
    public final static String LAT_LON = "lat_lon";
    public final static String ROW_ID = "row_id";
    public final static String STOP_DETAILS = "stop_details";
    public final static String FAVORITE_COLUMN_VALUE = "favorite_column_value";
    public final static String REFRESH_DATA_IF_TABLES_EMPTY = "refresh_data_if_tables_empty";

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
        String request = null;
        if (extras != null) {
            request = extras.getString(REQUEST);
        }
        Log.v(TAG, "onHandleIntent - request: " + request);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            sendMessageToMainActivity(new MainActivityEvents.Builder(MainActivityEvents.MainEvents.NETWORK_STATUS)
                    .setMsg("NO NETWORK CONNECTION")
                    .build());
            return;
        }

        if (request != null) {
            boolean databaseOK = DatabaseContentRefresher.performHealthCheck();
            if (!databaseOK) {
                sendMessageToMainActivity(new MainActivityEvents.Builder(MainActivityEvents.MainEvents.NETWORK_STATUS)
                        .setMsg("CANNOT ACCESS MPT site")
                        .build());
            } else {
                LatLonDetails latLonDetails;
                List<NearbyStopsDetails> nearbyStopsDetailsList;
                switch (request) {
                    case ACTION_GET_DATABASE_STATUS:
                        if (REFRESH_TEST) {
                            Log.v(TAG, "onHandleIntent - test database status check");
                            sendMessageToMainActivity(new MainActivityEvents.Builder(
                                    MainActivityEvents.MainEvents.DATABASE_STATUS)
                                    .setDatabaseEmpty(mTestDatabaseEmpty)
                                    .build());
                        } else {
                            Log.v(TAG, "onHandleIntent - real database status check");
                            boolean databaseEmpty = DatabaseContentRefresher.databaseEmpty(getContentResolver());
                            sendMessageToMainActivity(new MainActivityEvents.Builder(
                                    MainActivityEvents.MainEvents.DATABASE_STATUS)
                                    .setDatabaseEmpty(databaseEmpty)
                                    .build());
                        }
                        break;

                    case ACTION_REFRESH_DATA:
                        if (REFRESH_TEST) {
                            Log.v(TAG, "onHandleIntent - test load");
//                            sendMessageToMainActivity(new MainActivityEvents.Builder(
//                                    MainActivityEvents.MainEvents.DATABASE_STATUS)
//                                    .setDatabaseEmpty(mTestDatabaseLoaded)
//                                    .build());
                            testProgressBar();
                            mTestDatabaseLoaded = true;
                        } else {
                            Log.v(TAG, "onHandleIntent - real load");
                            boolean databaseEmpty = DatabaseContentRefresher.databaseEmpty(getContentResolver());
//                            sendMessageToMainActivity(new MainActivityEvents.Builder(
//                                    MainActivityEvents.MainEvents.DATABASE_STATUS)
//                                    .setDatabaseEmpty(databaseEmpty)
//                                    .build());
                            if (databaseEmpty || !extras.getBoolean(REFRESH_DATA_IF_TABLES_EMPTY)) {
                                DatabaseContentRefresher.refreshDatabase(getContentResolver());
                            }
                        }
                        break;

                    case SHOW_NEXT_DEPARTURES:
                        StopDetails stopDetails = extras.getParcelable(STOP_DETAILS);
//                        Log.v(TAG, "onHandleIntent - stopDetails: " + stopDetails);
                        List<NextDepartureDetails> nextDepartureDetailsList =
                                RemoteMptEndpointUtil.getBroadNextDepartures(
                                        stopDetails.routeType,
                                        stopDetails.stopId,
                                        extras.getInt(LIMIT));

//                        List<NextDepartureDetails> nextDepartureDetailsList =
//                          buildSimulatedDepartureDetails();

                        sendMessageToMainActivity(new MainActivityEvents.Builder(
                                MainActivityEvents.MainEvents.NEXT_DEPARTURES_DETAILS)
                                .setNextDepartureDetailsList(nextDepartureDetailsList)
                                .setStopDetails(stopDetails)
                                .build());
                        break;

                    case GET_DISRUPTIONS_DETAILS:
                        List<DisruptionsDetails> disruptionsDetailsList =
                                RemoteMptEndpointUtil.getDisruptions(extras.getString(MODES));
//                        List<DisruptionsDetails> disruptionsDetailsList =
//                          buildSimulatedDisruptionsDetails();
                        sendMessageToMainActivity(new MainActivityEvents.Builder(
                                MainActivityEvents.MainEvents.DISRUPTIONS_DETAILS)
                                .setDisruptionsDetailsList(disruptionsDetailsList)
                                .build());
                        break;

                    case GET_TRAIN_NEARBY_STOPS_DETAILS:
                        latLonDetails = extras.getParcelable(LAT_LON);
                        if (dbUtility == null) {
                            dbUtility = new DbUtility();
                        }
                        nearbyStopsDetailsList = dbUtility.getNearbyTrainDetails(
                                latLonDetails,
                                getApplicationContext());
                        sendMessageToMainActivity(new MainActivityEvents.Builder(
                                MainActivityEvents.MainEvents.NEARBY_LOCATION_DETAILS)
                                .setNearbyStopsDetailsList(nearbyStopsDetailsList)
                                .setForTrainsStopsNearby(true)
                                .build());
                        break;

                    case GET_NEARBY_STOPS_DETAILS:
                        latLonDetails = extras.getParcelable(LAT_LON);
//                        Log.v(TAG, "onHandleIntent - latLonDetails: " + latLonDetails);
                        nearbyStopsDetailsList =
                                RemoteMptEndpointUtil.getNearbyStops(latLonDetails);
                        if (dbUtility == null) {
                            dbUtility = new DbUtility();
                        }
                        dbUtility.fillInStopNames(nearbyStopsDetailsList, getApplicationContext());
//                        List<NearbyStopsDetails> nearbyStopsDetailsList =
//                                buildSimulatedNearbyDetails();
                        sendMessageToMainActivity(new MainActivityEvents.Builder(
                                MainActivityEvents.MainEvents.NEARBY_LOCATION_DETAILS)
                                .setNearbyStopsDetailsList(nearbyStopsDetailsList)
                                .build());
                        break;

                    case UPDATE_STOPS_DETAILS:
                        if (dbUtility == null) {
                            dbUtility = new DbUtility();
                        }
                        dbUtility.updateStopDetails(
                                extras.getInt(ROW_ID),
                                getApplicationContext(),
                                extras.getString(FAVORITE_COLUMN_VALUE));
//                        sendMessageToMainActivity(new MainActivityEvents.Builder(
//                                MainActivityEvents.MainEvents.NEARBY_LOCATION_DETAILS)
//                                .setNearbyStopsDetailsList(nearbyStopsDetailsList)
//                                .build());
                        break;

                    default:
                        throw new RuntimeException(TAG + ".onHandleIntent - no code to handle request: " + request);
                }
            }
        }
    }

    private void sendMessageToMainActivity(MainActivityEvents event) {
        EventBus.getDefault().post(event);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(RequestProcessorServiceRequestEvents event) {
    }

    // -----------------------------

    private static int disruptCnt;
    private List<DisruptionsDetails> buildSimulatedDisruptionsDetails() {
        List<DisruptionsDetails> buildDisruptionsDetailsList = new ArrayList<>();
        DisruptionsDetails disruptionsDetails = new DisruptionsDetails(
                "Title " + disruptCnt,
                "\n" +
                        "    private List<DisruptionsDetails> buildSimulatedDisruptionsDetails() {\n" +
                        "        List<DisruptionsDetails> buildDisruptionsDetailsList = new ArrayList<>();\n" +
                        "        DisruptionsDetails disruptionsDetails = new DisruptionsDetails( " + nextDetCnt
        );
        buildDisruptionsDetailsList.add(disruptionsDetails);
        disruptCnt++;
        disruptionsDetails = new DisruptionsDetails(
                "Title " + disruptCnt,
                "Description " + disruptCnt
        );
        buildDisruptionsDetailsList.add(disruptionsDetails);
        disruptCnt++;
        return buildDisruptionsDetailsList;
    }

    private static int nextDetCnt;
    private List<NextDepartureDetails> buildSimulatedDepartureDetails() {
        List<NextDepartureDetails> nextDepartureDetailsList = new ArrayList<>();
        NextDepartureDetails
                nextDepartureDetails = new NextDepartureDetails(
                nextDetCnt,
                "to city " + nextDetCnt,
                101,
                0,
                6,
                "09:50");
        nextDepartureDetailsList.add(nextDepartureDetails);
        nextDepartureDetails = new NextDepartureDetails(
                nextDetCnt,
                "to frankston " + nextDetCnt,
                101,
                5,
                6,
                "10:05");
        nextDepartureDetailsList.add(nextDepartureDetails);
        nextDetCnt++;
        return nextDepartureDetailsList;
    }

    private static int nextNearCnt;
//    private List<NearbyStopsDetails> buildSimulatedNearbyDetails() {
//        List<NearbyStopsDetails> nextNearbyStopsDetailsList = new ArrayList<>();
//        NearbyStopsDetails nearbyStopsDetails = new NearbyStopsDetails(
//                "name" + nextNearCnt,
//                "adr " + nextDetCnt,
//                "transport type",
//                String.valueOf(nextNearCnt),
//                (double) nextNearCnt,
//                (double) nextNearCnt);
//        nextNearbyStopsDetailsList.add(nearbyStopsDetails);
//        nextNearCnt++;
//        nearbyStopsDetails = new NearbyStopsDetails(
//                "name" + nextNearCnt,
//                "adr " + nextDetCnt,
//                "transport type",
//                String.valueOf(nextNearCnt),
//                (double) nextNearCnt,
//                (double) nextNearCnt);
//        nextNearbyStopsDetailsList.add(nearbyStopsDetails);
//        nextNearCnt++;
//        return nextNearbyStopsDetailsList;
//    }
}
