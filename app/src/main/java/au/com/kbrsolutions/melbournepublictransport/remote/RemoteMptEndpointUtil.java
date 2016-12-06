package au.com.kbrsolutions.melbournepublictransport.remote;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.DisruptionsDetails;
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.data.MptContract;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopsNearbyDetails;
import au.com.kbrsolutions.melbournepublictransport.utilities.JodaDateTimeUtility;
import au.com.kbrsolutions.melbournepublictransport.utilities.SharedPreferencesUtility;

public class RemoteMptEndpointUtil {

    private final static int DEVELOPER_ID = 1000796;

    private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
    private final static String PRIVATE_KEY = "8d3c3f43-4244-11e6-a0ce-06f54b901f07";
    private final static String PTV_BASE_URL = "http://timetableapi.ptv.vic.gov.au";

    private static final String V2_HEALTHCHECK = "/v2/healthcheck?timestamp=";
    private static final String V2_LINES = "/v2/lines/mode/";

    private static final String DATABASE_OK = "databaseOK";
//    private static final String SECURITY_TOKEN_OK = "securityTokenOK";
//    private static final String CLIENT_CLOCK_OK = "clientClockOK";
//    private static final String MEM_CACHE_OK = "memcacheOK";
    private static final String ROUTE_TYPE = "route_type";
    private static final String LINE_ID = "line_id";
    private static final String LINE_NAME = "line_name";
    private static final String LINE_NAME_SHORT = "line_name_short";
    private static final String V2_MODE = "/v2/mode/";
    private static final String LINE = "/line/";
    private static final String STOPS_FOR_LINE = "/stops-for-line";
    private static final String STOP_ID = "stop_id";
    private static final String LOCATION_NAME = "location_name";
    private static final String SUBURB = "suburb";
    private static final String LAT = "lat";
    private static final String LON = "lon";
    private static final String STOP = "/stop/";
    private static final String DEPARTURES_BY_DESTINATION = "/departures/by-destination/limit/";
    private static final String VALUES = "values";
    private static final String NULL = "null";
    private static final String TIME_REALTIME_UTC = "time_realtime_utc";
    private static final String TIME_TIMETABLE_UTC = "time_timetable_utc";
    private static final String PLATFORM = "platform";
    private static final String DIRECTION = "direction";
    private static final String DIRECTION_ID = "direction_id";
    private static final String RUN_ID = "run_id";
    private static final String RUN = "run";
    private static final String DESTINATION_ID = "destination_id";
    private static final String NUM_SKIPPED = "num_skipped";
    private static final String V2_DISRUPTIONS_MODES = "/v2/disruptions/modes/";
    private static final String METRO_TRAIN = "metro-train";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String RESULT = "result";
    private static final String DISTANCE = "distance";
    private static final String V2_NEARME_LATITUDE = "/v2/nearme/latitude/";
    private static final String LONGITUDE = "/longitude/";
    private static final String GET = "GET";

    private static final Map<Integer, String> directionMap = new ArrayMap<>();

    static {
        directionMap.put(0,  "To City");
        directionMap.put(1,  "To City (Flinders Street)");
        directionMap.put(2,  "To Craigieburn");
        directionMap.put(3,  "To Belgrave");
        directionMap.put(4,  "To Cranbourne");
        directionMap.put(5,  "To South Morang");
        directionMap.put(6,  "To Frankston");
        directionMap.put(7,  "To Glen Waverly");
        directionMap.put(8,  "To Hurstbridge");
        directionMap.put(9,  "To Lilydale");
        directionMap.put(10, "To Pakenham");
        directionMap.put(11, "To Sandringham");
        directionMap.put(12, "To Stony Point");
        directionMap.put(13, "To Sunbury");
        directionMap.put(14, "To Upfield");
        directionMap.put(15, "To Werribee");
        directionMap.put(16, "To Williamstown");
        directionMap.put(17, "To Showgrounds/Flemington");
    }

    private final static String TAG = RemoteMptEndpointUtil.class.getSimpleName();

    /**
     * Check if MPT site is available working.
     *
     * @param context
     * @return
     */
    public static boolean performHealthCheck(Context context) {
        boolean databaseOK;
        try {
            DateTime dateTime = getCurrentDateTime();
            final String uri = V2_HEALTHCHECK + JodaDateTimeUtility.getUtcTime(dateTime);
            String jsonString = processRemoteRequest(uri);

            JSONObject jsonObject = new JSONObject(jsonString);
//            boolean securityTokenOk = forecastJson.getBoolean(SECURITY_TOKEN_OK);
//            boolean clientClockOK = forecastJson.getBoolean(CLIENT_CLOCK_OK);
//            boolean memcacheOK = forecastJson.getBoolean(MEM_CACHE_OK);
            databaseOK = jsonObject.getBoolean(DATABASE_OK);
        } catch (Exception e) {
            if (!SharedPreferencesUtility.isReleaseVersion(context)) {
                e.printStackTrace();
            }
            throw new RuntimeException(TAG + context.getString
                    (R.string.perform_health_check_exception_text, e));
        }
        return databaseOK;
    }

    /**
     * Retrieve train lines details.
     *
     * @param mode
     * @param context
     * @return
     */
    public static List<ContentValues> getLineDetails(int mode, Context context) {
        List<ContentValues> lineDetailsContentValuesList = new ArrayList<>();
        final String uri = V2_LINES + mode;

        int routeType;
        String lineId;
        String lineName;
        String lineNameShort;
        JSONArray lineArray;
        try {
            String jsonString = processRemoteRequest(uri);
            lineArray = new JSONArray(jsonString);
            for(int i = 0; i < lineArray.length(); i++) {
                JSONObject oneLineObject = lineArray.getJSONObject(i);
                routeType = oneLineObject.getInt(ROUTE_TYPE);
                lineId = oneLineObject.getString(LINE_ID);
                lineName = oneLineObject.getString(LINE_NAME_SHORT);
                lineNameShort = oneLineObject.getString(LINE_NAME);

                ContentValues values = new ContentValues();
                values.put(MptContract.LineDetailEntry.COLUMN_ROUTE_TYPE, routeType);
                values.put(MptContract.LineDetailEntry.COLUMN_LINE_ID, lineId);
                values.put(MptContract.LineDetailEntry.COLUMN_LINE_NAME, lineName);
                values.put(MptContract.LineDetailEntry.COLUMN_LINE_NAME_SHORT, lineNameShort);
                lineDetailsContentValuesList.add(values);
            }
        } catch (Exception e) {
            if (!SharedPreferencesUtility.isReleaseVersion(context)) {
                e.printStackTrace();
            }
            throw new RuntimeException(TAG + context.getString
                    (R.string.get_line_details_exception_text, e));
        }

        return lineDetailsContentValuesList;
    }

    /**
     * Retrieve train stops details.
     *
     * @param mode
     * @param lineId
     * @param context
     * @return
     */
    public static List<ContentValues> getStopDetailsForLine(int mode, String lineId,
                                                            Context context) {
        List<ContentValues> stopDetailsContentValuesList = new ArrayList<>();
        final String uri = V2_MODE + mode + LINE + lineId + STOPS_FOR_LINE;

        int routeType;
        String stopId;
        String locationName;
        String suburb;
        double latitude;
        double longitude;
        try {
            String jsonString = processRemoteRequest(uri);
            JSONArray stopsArray = new JSONArray(jsonString);
            for(int i = 0; i < stopsArray.length(); i++) {
                JSONObject oneLineObject = stopsArray.getJSONObject(i);
                routeType = oneLineObject.getInt(ROUTE_TYPE);
                stopId = oneLineObject.getString(STOP_ID);
                locationName = oneLineObject.getString(LOCATION_NAME);
                suburb = oneLineObject.getString(SUBURB);
                latitude = oneLineObject.getDouble(LAT);
                longitude = oneLineObject.getDouble(LON);
                ContentValues values = new ContentValues();
                values.put(MptContract.StopDetailEntry.COLUMN_ROUTE_TYPE, routeType);
                values.put(MptContract.StopDetailEntry.COLUMN_STOP_ID, stopId);
                values.put(MptContract.StopDetailEntry.COLUMN_LOCATION_NAME, locationName);
                values.put(MptContract.StopDetailEntry.COLUMN_SUBURB, suburb);
                values.put(MptContract.StopDetailEntry.COLUMN_LATITUDE, latitude);
                values.put(MptContract.StopDetailEntry.COLUMN_LONGITUDE, longitude);
                values.put(MptContract.StopDetailEntry.COLUMN_FAVORITE,
//                        context.getString(R.string.favorite_stop_false_value));
                        MptContract.StopDetailEntry.NON_FAVORITE_FLAG);
                stopDetailsContentValuesList.add(values);
            }
        } catch (Exception e) {
            if (!SharedPreferencesUtility.isReleaseVersion(context)) {
                e.printStackTrace();
            }
            throw new RuntimeException(TAG + context.getString
                    (R.string.get_stop_details_for_line_exception_text, e));
        }

        return stopDetailsContentValuesList;
    }

    /**
     * Retrieve departuers details.
     *
     * @param mode
     * @param stopId
     * @param limit
     * @param context
     * @return
     */
    public static List<NextDepartureDetails> getBroadNextDepartures (
            int mode,
            String stopId,
            int limit,
            Context context) {
        final String uri = V2_MODE + mode + STOP + stopId + DEPARTURES_BY_DESTINATION + limit;
        List<NextDepartureDetails> nextDepartureDetailsList = new ArrayList<>();
        try {
            String jsonString = processRemoteRequest(uri);

            JSONObject broadDeparturesObject = new JSONObject(jsonString);
            JSONArray broadDeparturesValuesArray = broadDeparturesObject.getJSONArray(VALUES);
            String directionName;
            for(int i = 0; i < broadDeparturesValuesArray.length(); i++) {
                JSONObject oneBroadDeparturesValueObject = broadDeparturesValuesArray.getJSONObject(i);
                String departureTimeUtc = oneBroadDeparturesValueObject.getString(TIME_REALTIME_UTC);
                if (departureTimeUtc.equals(NULL)) {
                    departureTimeUtc = oneBroadDeparturesValueObject.getString(TIME_TIMETABLE_UTC);
                }
                String str = fmt.print(JodaDateTimeUtility.getLocalTimeFromUtcString(departureTimeUtc));

                JSONObject platform = oneBroadDeparturesValueObject.getJSONObject(PLATFORM);
                JSONObject direction = platform.getJSONObject(DIRECTION);
                int directionId = direction.getInt(DIRECTION_ID);

                JSONObject run = oneBroadDeparturesValueObject.getJSONObject(RUN);
                int routeType = run.getInt(ROUTE_TYPE);
                int runId = run.getInt(RUN_ID);
                int destinationId = run.getInt(DESTINATION_ID);
                int numSkipped = run.getInt(NUM_SKIPPED);
                if (directionMap.containsKey(directionId)) {
                    directionName = directionMap.get(directionId);
                } else {
                    directionName = String.valueOf(directionId);
                }
                nextDepartureDetailsList.add(new NextDepartureDetails(
                        directionId,
                        routeType,
                        directionName,
                        runId,
                        numSkipped,
                        numSkipped == 0 ?
                                context.getString(R.string.all_stops_train) :
                                context.getString(R.string.express_train),
                        destinationId,
                        str));
            }
        } catch (Exception e) {
            if (!SharedPreferencesUtility.isReleaseVersion(context)) {
                e.printStackTrace();
            }
            throw new RuntimeException(TAG + context.getString
                    (R.string.get_broad_next_departures_exception_text, e));
        }
        return nextDepartureDetailsList;
    }

    /**
     * Retrieve disruptions details.
     *
     * @param modes
     * @param context
     * @return
     */
    public static List<DisruptionsDetails> getDisruptions(String modes, Context context) {
        final String uri = V2_DISRUPTIONS_MODES + modes;

        List<DisruptionsDetails> nextDisruptionsDetailsList = new ArrayList<>();

        try {
            String jsonString = processRemoteRequest(uri);
            JSONObject disruptionsObject = new JSONObject(jsonString);
            JSONArray disruptionsArray = disruptionsObject.getJSONArray(METRO_TRAIN);
            String title;
            String description;
            DisruptionsDetails disruptionsDetails;
            for(int i = 0; i < disruptionsArray.length(); i++) {
                JSONObject oneDisruptionsObject = disruptionsArray.getJSONObject(i);
                title = oneDisruptionsObject.getString(TITLE);
                description = oneDisruptionsObject.getString(DESCRIPTION);
                disruptionsDetails = new DisruptionsDetails(title, description);
                nextDisruptionsDetailsList.add(disruptionsDetails);
            }
        } catch (Exception e) {
            if (!SharedPreferencesUtility.isReleaseVersion(context)) {
                e.printStackTrace();
            }
            throw new RuntimeException(TAG + context.getString
                    (R.string.get_disruptions_exception_text, e));
        }
        return nextDisruptionsDetailsList;
    }

    /**
     * Retrieve stops nearby details.
     *
     * @param latLonDetails
     * @param context
     * @return
     */
    public static List<StopsNearbyDetails> getStopsNearby(LatLngDetails latLonDetails,
                                                          Context context) {
        final String uri = V2_NEARME_LATITUDE + latLonDetails.latitude + LONGITUDE +
                latLonDetails.longitude;

        List<StopsNearbyDetails> stopsNearbyDetailsList = new ArrayList<>();

        try {
            String jsonString = processRemoteRequest(uri);
            JSONArray stopArray = new JSONArray(jsonString);
            double distance;
            String locationName;
            int routeType;
            String stopId;
            double latitude;
            double longitude;
            for(int i = 0; i < stopArray.length(); i++) {
                JSONObject oneStringObject = stopArray.getJSONObject(i);
                JSONObject resultObject = oneStringObject.getJSONObject(RESULT);
                distance = resultObject.getDouble(DISTANCE);
                locationName = resultObject.getString(LOCATION_NAME);
                routeType = resultObject.getInt(ROUTE_TYPE);
                stopId = resultObject.getString(STOP_ID);
                latitude = resultObject.getDouble(LAT);
                longitude = resultObject.getDouble(LON);
                stopsNearbyDetailsList.add(new StopsNearbyDetails(
                        stopId,
                        locationName,
                        null,
                        routeType,
                        stopId,
                        latitude,
                        longitude,
                        distance));
            }
        } catch (Exception e) {
            if (!SharedPreferencesUtility.isReleaseVersion(context)) {
                e.printStackTrace();
            }
            throw new RuntimeException(TAG + context.getString
                    (R.string.get_stops_nearby_exception_text, e));
        }
        return stopsNearbyDetailsList;
    }

    /**
     * Send request encoded in the uri to MPT site.
     *
     * @param uri
     * @return
     * @throws Exception
     */
    @Nullable
    private static String processRemoteRequest(String uri) throws Exception {

        String urlString;
//            urlString = buildTTAPIURL(PTV_BASE_URL, PRIVATE_KEY, uri, DEVELOPER_ID);
            urlString = buildTTAPIURL(uri);


        Uri.Builder uriBuilder = Uri.parse(urlString).buildUpon();
        Uri builtUri = uriBuilder.build();
        URL url;
            url = new URL(builtUri.toString());

        URL urlToProcess = url;
        String jsonString = null;

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            urlConnection = (HttpURLConnection) urlToProcess.openConnection();
            urlConnection.setRequestMethod(GET);
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
//                setLocationStatus(getContext(), LOCATION_STATUS_SERVER_DOWN);
                return null;
            }
            jsonString = buffer.toString();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException ignored) {
                }
            }
        }

        return jsonString;
    }


    private static DateTime getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        int selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
        int selectedMinute = calendar.get(Calendar.MINUTE);
        return new DateTime().withHourOfDay(selectedHour).withMinuteOfHour(selectedMinute);
    }

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private static final String DEV_ID = "devid=";
    private static final String SIGNATURE = "&signature=";
    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String ZERO = "0";
    // FIXME: 2/12/2016 - put proper method description
    /**
     * Method to demonstrate building of Timetable API URL
     *
     * @return Complete URL with signature
     * @throws Exception
     *
     */
    @NonNull
//    public static String buildTTAPIURL(final String baseURL, final String privateKey, final String uri,
//                                       final int developerId) throws Exception {
    private static String buildTTAPIURL(final String uri) throws Exception {

        String uriWithDeveloperID = uri + (uri.contains(QUESTION_MARK) ? AMPERSAND : QUESTION_MARK) +
                DEV_ID + DEVELOPER_ID;
        byte[] keyBytes = PRIVATE_KEY.getBytes();
        byte[] uriBytes = uriWithDeveloperID.getBytes();
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        byte[] signatureBytes = mac.doFinal(uriBytes);
        StringBuilder signature = new StringBuilder(signatureBytes.length * 2);
        for (byte signatureByte : signatureBytes)
        {
            int intVal = signatureByte & 0xff;
            if (intVal < 0x10){
                signature.append(ZERO);
            }
            signature.append(Integer.toHexString(intVal));
        }
        return PTV_BASE_URL +
                uri + (uri.contains(QUESTION_MARK) ? AMPERSAND : QUESTION_MARK) +
                DEV_ID + DEVELOPER_ID + SIGNATURE + signature.toString().toUpperCase();
    }
}
