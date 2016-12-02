package au.com.kbrsolutions.melbournepublictransport.remote;

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

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.DisruptionsDetails;
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopsNearbyDetails;
import au.com.kbrsolutions.melbournepublictransport.events.MainActivityEvents;
import au.com.kbrsolutions.melbournepublictransport.events.RequestProcessorServiceRequestEvents;
import au.com.kbrsolutions.melbournepublictransport.utilities.DatabaseContentLoader;
import au.com.kbrsolutions.melbournepublictransport.utilities.DbUtility;
import au.com.kbrsolutions.melbournepublictransport.utilities.SharedPreferencesUtility;

import static au.com.kbrsolutions.melbournepublictransport.utilities.DatabaseContentLoader.testProgressBar;

/**
 * Perform asynch tasks: get data from PTV web site and access internal app database.
 */
public class RequestProcessorService extends IntentService {

    private EventBus eventBus;
    private DbUtility dbUtility;
    private boolean mTestDatabaseEmpty = true;

    private final static boolean REFRESH_TEST = false;

    public final static String REQUEST = "request";

    public final static String ACTION_LOAD_OR_REFRESH_DATA = "action_refresh_data";
    public final static String ACTION_GET_DATABASE_STATUS = "action_get_dadabase_status";
    public static final String ACTION_GET_DISRUPTIONS_DETAILS = "get_disruptions_details";
    public final static String ACTION_SHOW_NEXT_DEPARTURES = "show_next_departures";
    public final static String ACTION_GET_STOPS_NEARBY_DETAILS = "get_nearby_details";
    public final static String ACTION_GET_TRAIN_STOPS_NEARBY_DETAILS =
            "get_train_nearby_stops_details";
    public final static String ACTION_UPDATE_STOPS_DETAILS = "update_stops_details";

    public final static String MODES = "modes";
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

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            sendMessageToMainActivity(new MainActivityEvents.Builder(
                    MainActivityEvents.MainEvents.REMOTE_ACCESS_PROBLEMS)
                        .setMsg(getResources().getString(R.string.no_network_connection))
                        .build());
            return;
        }

        if (request != null) {
            try {
                if (!RemoteMptEndpointUtil.performHealthCheck(getApplicationContext())) {
                    sendMessageToMainActivity(new MainActivityEvents.Builder(
                            MainActivityEvents.MainEvents.REMOTE_ACCESS_PROBLEMS)
                                .setMsg(getResources().getString(R.string.can_not_access_ptv_site))
                                .build());
                } else {
                    LatLngDetails latLonDetails;
                    List<StopsNearbyDetails> stopsNearbyDetailsList;
                    switch (request) {
                        case ACTION_GET_DATABASE_STATUS:
                            if (REFRESH_TEST) {
                                sendMessageToMainActivity(new MainActivityEvents.Builder(
                                        MainActivityEvents.MainEvents.DATABASE_STATUS)
                                        .setDatabaseEmpty(mTestDatabaseEmpty)
                                        .build());
                            } else {
                                boolean databaseEmpty = DatabaseContentLoader.
                                        isDatabaseEmpty(getContentResolver(),
                                                getApplicationContext());
                                sendMessageToMainActivity(new MainActivityEvents.Builder(
                                        MainActivityEvents.MainEvents.DATABASE_STATUS)
                                        .setDatabaseEmpty(databaseEmpty)
                                        .build());
                            }
                            break;

                        case ACTION_LOAD_OR_REFRESH_DATA:
                            if (REFRESH_TEST) {
                                testProgressBar();
                            } else {
                                boolean databaseEmpty = DatabaseContentLoader.
                                        isDatabaseEmpty(getContentResolver(),
                                                getApplicationContext());

                                if (databaseEmpty ||
                                        !extras.getBoolean(REFRESH_DATA_IF_TABLES_EMPTY)) {
                                    DatabaseContentLoader.loadOrRefreshDatabase(getContentResolver(),
                                            getApplicationContext());
                                } else {
                                /* database is not empty - pretend the load has finished */
                                    sendMessageToMainActivity(new MainActivityEvents.Builder(
                                            MainActivityEvents.MainEvents.DATABASE_LOAD_PROGRESS)
                                            .setDatabaseLoadTarget(1)
                                            .setDatabaseLoadProgress(1)
                                            .build());
                                }
                            }
                            break;

                        case ACTION_SHOW_NEXT_DEPARTURES:
                            StopDetails stopDetails = extras.getParcelable(STOP_DETAILS);
                            List<NextDepartureDetails> nextDepartureDetailsList =
                                    RemoteMptEndpointUtil.getBroadNextDepartures(
                                            stopDetails.routeType,
                                            stopDetails.stopId,
                                            extras.getInt(LIMIT),
                                            getApplicationContext());

//                        List<NextDepartureDetails> nextDepartureDetailsList =
//                          buildSimulatedDepartureDetails();
//                        Log.v(TAG, "onHandleIntent - stopId: " + nextDepartureDetailsList.get(0));
                            sendMessageToMainActivity(new MainActivityEvents.Builder(
                                    MainActivityEvents.MainEvents.NEXT_DEPARTURES_DETAILS)
                                    .setNextDepartureDetailsList(nextDepartureDetailsList)
                                    .setStopDetails(stopDetails)
                                    .build());
                            break;

                        case ACTION_GET_DISRUPTIONS_DETAILS:
                            List<DisruptionsDetails> disruptionsDetailsList =
                                    RemoteMptEndpointUtil.getDisruptions(extras.getString(MODES),
                                            getApplicationContext());
//                        List<DisruptionsDetails> disruptionsDetailsList =
//                          buildSimulatedDisruptionsDetails();
                            sendMessageToMainActivity(new MainActivityEvents.Builder(
                                    MainActivityEvents.MainEvents.DISRUPTIONS_DETAILS)
                                    .setDisruptionsDetailsList(disruptionsDetailsList)
                                    .build());
                            break;

                        case ACTION_GET_TRAIN_STOPS_NEARBY_DETAILS:
                            latLonDetails = extras.getParcelable(LAT_LON);
                            if (dbUtility == null) {
                                dbUtility = new DbUtility();
                            }
                            stopsNearbyDetailsList = dbUtility.getNearbyTrainDetails(
                                    latLonDetails,
                                    getApplicationContext());
                            sendMessageToMainActivity(new MainActivityEvents.Builder(
                                    MainActivityEvents.MainEvents.NEARBY_LOCATION_DETAILS)
                                    .setNearbyStopsDetailsList(stopsNearbyDetailsList)
                                    .setForTrainsStopsNearby(true)
                                    .build());
                            break;

                        case ACTION_GET_STOPS_NEARBY_DETAILS:
                            latLonDetails = extras.getParcelable(LAT_LON);
                            stopsNearbyDetailsList =
                                    RemoteMptEndpointUtil.getStopsNearby(latLonDetails,
                                    getApplicationContext());
                            if (dbUtility == null) {
                                dbUtility = new DbUtility();
                            }
                            dbUtility.fillInMissingDetails(stopsNearbyDetailsList,
                                    getApplicationContext());
//                        List<NearbyStopsDetails> stopsNearbyDetailsList =
//                                buildSimulatedNearbyDetails();
                            sendMessageToMainActivity(new MainActivityEvents.Builder(
                                    MainActivityEvents.MainEvents.NEARBY_LOCATION_DETAILS)
                                    .setNearbyStopsDetailsList(stopsNearbyDetailsList)
                                    .build());
                            break;

                        case ACTION_UPDATE_STOPS_DETAILS:
                            if (dbUtility == null) {
                                dbUtility = new DbUtility();
                            }
                            dbUtility.updateStopDetails(
                                    extras.getInt(ROW_ID),
                                    getApplicationContext(),
                                    extras.getString(FAVORITE_COLUMN_VALUE));
                            sendMessageToMainActivity(new MainActivityEvents.Builder(
                                    MainActivityEvents.MainEvents.REFRESH_FAVORITE_STOPS_VIEW)
                                    .build());
                            break;

                        default:
                            throw new Exception(TAG +
                                    getString(R.string.no_code_to_handle_request_exception_text,
                                            request));
                    }
                }
            } catch (Exception e) {
//                Log.v(TAG, "onHandleIntent - got exception: " + e);
                String msg;
                if (SharedPreferencesUtility.isReleaseVersion(getApplicationContext())) {
                    msg = getResources().getString(R.string.can_not_access_ptv_site);
                } else {
                    msg = getResources().getString(R.string.can_not_access_ptv_site, e);
                }
                sendMessageToMainActivity(new MainActivityEvents.Builder(
                        MainActivityEvents.MainEvents.REMOTE_ACCESS_PROBLEMS)
                            .setMsg(msg)
                            .build());
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
                nextDetCnt,
                "to city " + nextDetCnt,
                101,
                0,
                "All stops",
                6,
                "09:50");
        nextDepartureDetailsList.add(nextDepartureDetails);
        nextDepartureDetails = new NextDepartureDetails(
                nextDetCnt,
                nextDetCnt,
                "to frankston " + nextDetCnt,
                101,
                5,
                "Express",
                6,
                "10:05");
        nextDepartureDetailsList.add(nextDepartureDetails);
        nextDetCnt++;
        return nextDepartureDetailsList;
    }

    private static int nextNearCnt;
    private List<StopsNearbyDetails> buildSimulatedNearbyDetails() {
        List<StopsNearbyDetails> nextNearbyStopsDetailsList = new ArrayList<>();
        StopsNearbyDetails nearbyStopsDetails = new StopsNearbyDetails(
                "name" + nextNearCnt,
                "adr " + nextDetCnt,
                "suburb",
                0,
                "transport type",
                (double) nextNearCnt,
                (double) nextNearCnt,
                (double) nextNearCnt);
        nextNearbyStopsDetailsList.add(nearbyStopsDetails);
        nextNearCnt++;
        nearbyStopsDetails = new StopsNearbyDetails(
                "name" + nextNearCnt,
                "adr " + nextDetCnt,
                "suburb",
                0,
                "transport type",
                (double) nextNearCnt,
                (double) nextNearCnt,
                (double) nextNearCnt);
        nextNearbyStopsDetailsList.add(nearbyStopsDetails);
        nextNearCnt++;
        return nextNearbyStopsDetailsList;
    }
}
