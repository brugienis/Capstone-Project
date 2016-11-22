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
import au.com.kbrsolutions.melbournepublictransport.data.NearbyStopsDetails;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.utilities.JodaDateTimeUtility;
import au.com.kbrsolutions.melbournepublictransport.utilities.Utility;

public class RemoteMptEndpointUtil {

    private DateTime mSelectedTime;
    private final static int DEVELOPER_ID = 1000796;

    private static DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");;
    private final static String PRIVATE_KEY = "8d3c3f43-4244-11e6-a0ce-06f54b901f07";
    private final static String PTV_BASE_URL = "http://timetableapi.ptv.vic.gov.au";

    private static Map<Integer, String> directionMap = new ArrayMap<>();

    static {
        directionMap.put(0, "To City");
        directionMap.put(1, "To City (Flinders Street)");
        directionMap.put(6, "To Frankston");
        directionMap.put(11, "To Sandringham");
        directionMap.put(14, "To Upfield");
        directionMap.put(15, "To Werribee");
    }

    private final static String TAG = RemoteMptEndpointUtil.class.getSimpleName();

    public static boolean performHealthCheck(Context context) {
        boolean databaseOK = false;
        final String SECURITY_TOKEN_OK = "securityTokenOK";
        final String CLIENT_CLOCK_OK = "clientClockOK";
        final String MEM_CACHE_OK = "memcacheOK";
        final String DATABASE_OK = "databaseOK";

        try {
            DateTime dateTime = getCurrentDateTime();

//            Log.v(TAG, "performHealthCheck - before throw isReleaseVersion: " + Utility.isReleaseVersion(context));
//            if (true) throw new Exception("BR");

            final String uri = "/v2/healthcheck?timestamp=" + JodaDateTimeUtility.getUtcTime(dateTime);
            String jsonString = processRemoteRequest(uri);

            JSONObject forecastJson;
            forecastJson = new JSONObject(jsonString);
//            boolean securityTokenOk = forecastJson.getBoolean(SECURITY_TOKEN_OK);
//            boolean clientClockOK = forecastJson.getBoolean(CLIENT_CLOCK_OK);
//            boolean memcacheOK = forecastJson.getBoolean(MEM_CACHE_OK);
            databaseOK = forecastJson.getBoolean(DATABASE_OK);
        } catch (Exception e) {
            if (!Utility.isReleaseVersion(context)) {
                e.printStackTrace();
            }
            throw new RuntimeException(TAG + ".performHealthCheck - exception: " + e);
        }

        return databaseOK;
    }

    public static List<ContentValues> getLineDetails(int mode, Context context) {
        List<ContentValues> lineDetailsContentValuesList = new ArrayList<>();
        final String uri = "/v2/lines/mode/" + mode;

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
                routeType = oneLineObject.getInt("route_type");
                lineId = oneLineObject.getString("line_id");
                lineName = oneLineObject.getString("line_name_short");
                lineNameShort = oneLineObject.getString("line_name");

                ContentValues values = new ContentValues();
                values.put(MptContract.LineDetailEntry.COLUMN_ROUTE_TYPE, routeType);
                values.put(MptContract.LineDetailEntry.COLUMN_LINE_ID, lineId);
                values.put(MptContract.LineDetailEntry.COLUMN_LINE_NAME, lineName);
                values.put(MptContract.LineDetailEntry.COLUMN_LINE_NAME_SHORT, lineNameShort);
                lineDetailsContentValuesList.add(values);
            }
        } catch (Exception e) {
            if (!Utility.isReleaseVersion(context)) {
                e.printStackTrace();
            }
            throw new RuntimeException(TAG + ".getLineDetails - exception: " + e);
        }

        return lineDetailsContentValuesList;
    }

    public static List<ContentValues> getStopDetailsForLine(int mode, String lineId,
                                                            Context context) {
        List<ContentValues> stopDetailsContentValuesList = new ArrayList<>();
        final String uri = "/v2/mode/" + mode + "/line/" + lineId + "/stops-for-line";

        int routeType;
        String stopId;
        String locationName;
        String suburb;
        double latitude;
        double longitude;
        String favorite = "n";
        try {
            String jsonString = processRemoteRequest(uri);
            JSONArray stopsArray = new JSONArray(jsonString);
            for(int i = 0; i < stopsArray.length(); i++) {
                JSONObject oneLineObject = stopsArray.getJSONObject(i);
                routeType = oneLineObject.getInt("route_type");
                stopId = oneLineObject.getString("stop_id");
                locationName = oneLineObject.getString("location_name");
                suburb = oneLineObject.getString("suburb");
                latitude = oneLineObject.getDouble("lat");
                longitude = oneLineObject.getDouble("lon");
                ContentValues values = new ContentValues();
                values.put(MptContract.StopDetailEntry.COLUMN_ROUTE_TYPE, routeType);
                values.put(MptContract.StopDetailEntry.COLUMN_STOP_ID, stopId);
                values.put(MptContract.StopDetailEntry.COLUMN_LOCATION_NAME, locationName);
                values.put(MptContract.StopDetailEntry.COLUMN_SUBURB, suburb);
                values.put(MptContract.StopDetailEntry.COLUMN_LATITUDE, latitude);
                values.put(MptContract.StopDetailEntry.COLUMN_LONGITUDE, longitude);
                values.put(MptContract.StopDetailEntry.COLUMN_FAVORITE, favorite);
                stopDetailsContentValuesList.add(values);
            }
        } catch (Exception e) {
            if (!Utility.isReleaseVersion(context)) {
                e.printStackTrace();
            }
            throw new RuntimeException(TAG + ".getStopDetailsForLine - exception: " + e);
        }

        return stopDetailsContentValuesList;
    }

    public static List<NextDepartureDetails> getBroadNextDepartures (
            int mode,
            String stopId,
            int limit,
            Context context) {
        final String uri = "/v2/mode/" + mode + "/stop/" + stopId + "/departures/by-destination/limit/" + limit;
        List<NextDepartureDetails> nextDepartureDetailsList = new ArrayList<>();
        try {
            String jsonString = processRemoteRequest(uri);

            JSONObject broadDeparturesObject = new JSONObject(jsonString);
            JSONArray broadDeparturesValuesArray = broadDeparturesObject.getJSONArray("values");
            String directionName;
            for(int i = 0; i < broadDeparturesValuesArray.length(); i++) {
                JSONObject oneBroadDeparturesValueObject = broadDeparturesValuesArray.getJSONObject(i);
                String departureTimeUtc = oneBroadDeparturesValueObject.getString("time_realtime_utc");
                if (departureTimeUtc.equals("null")) {
                    departureTimeUtc = oneBroadDeparturesValueObject.getString("time_timetable_utc");
                }
                String str = fmt.print(JodaDateTimeUtility.getLocalTimeFromUtcString(departureTimeUtc));

                JSONObject platform = oneBroadDeparturesValueObject.getJSONObject("platform");
                JSONObject direction = platform.getJSONObject("direction");
                int directionId = direction.getInt("direction_id");

                JSONObject run = oneBroadDeparturesValueObject.getJSONObject("run");
                int routeType = run.getInt("route_type");
                int runId = run.getInt("run_id");
                int destinationId = run.getInt("destination_id");
                int numSkipped = run.getInt("num_skipped");
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
            if (!Utility.isReleaseVersion(context)) {
                e.printStackTrace();
            }
            throw new RuntimeException(TAG + ".getBroadNextDepartures - exception: " + e);
        }
        return nextDepartureDetailsList;
    }

    public static List<DisruptionsDetails> getDisruptions(String modes, Context context) {
        final String uri = "/v2/disruptions/modes/" + modes;

        List<DisruptionsDetails> nextDisruptionsDetailsList = new ArrayList<>();

        try {
            String jsonString = processRemoteRequest(uri);
            JSONObject disruptionsObject = new JSONObject(jsonString);
            JSONArray disruptionsArray = disruptionsObject.getJSONArray("metro-train");
            String title;
            String description;
            DisruptionsDetails disruptionsDetails;
            for(int i = 0; i < disruptionsArray.length(); i++) {
                JSONObject oneDisruptionsObject = disruptionsArray.getJSONObject(i);
                title = oneDisruptionsObject.getString("title");
                description = oneDisruptionsObject.getString("description");
                disruptionsDetails = new DisruptionsDetails(title, description);
                nextDisruptionsDetailsList.add(disruptionsDetails);
            }
        } catch (Exception e) {
            if (!Utility.isReleaseVersion(context)) {
                e.printStackTrace();
            }
            throw new RuntimeException(TAG + ".getDisruptions - exception: " + e);
        }
        return nextDisruptionsDetailsList;
    }

    public static List<NearbyStopsDetails> getNearbyStops(LatLngDetails latLonDetails,
            Context context) {
        final String uri = "/v2/nearme/latitude/" + latLonDetails.latitude + "/longitude/" + latLonDetails.longitude;

        List<NearbyStopsDetails> nearbyStopsDetailsList = new ArrayList<>();

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
                JSONObject resultObject = oneStringObject.getJSONObject("result");
                distance = resultObject.getDouble("distance");
                locationName = resultObject.getString("location_name");
                routeType = resultObject.getInt("route_type");
                stopId = resultObject.getString("stop_id");
                latitude = resultObject.getDouble("lat");
                longitude = resultObject.getDouble("lon");
                nearbyStopsDetailsList.add(new NearbyStopsDetails(
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
            if (!Utility.isReleaseVersion(context)) {
                e.printStackTrace();
            }
            throw new RuntimeException(TAG + ".getNearbyStops - exception: " + e);
        }
        return nearbyStopsDetailsList;
    }

    @Nullable
    private static String processRemoteRequest(String uri) throws Exception {

        String urlString = null;
//        try {
            urlString = buildTTAPIURL(PTV_BASE_URL, PRIVATE_KEY, uri, DEVELOPER_ID);
//            Log.v(TAG, "performHealthCheck - urlString: " + urlString);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Uri.Builder uriBuilder = Uri.parse(urlString).buildUpon();
        Uri builtUri = uriBuilder.build();
        URL url = null;
//        try {
            url = new URL(builtUri.toString());
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }

//        URL urlToProcess = urls[0];
        URL urlToProcess = url;
        String jsonString = null;
//        Log.v(TAG, mSource + " - doInBackground - urlToProcess: " + urlToProcess);
//        if (true) return "test";

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) urlToProcess.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
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
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
//                setLocationStatus(getContext(), LOCATION_STATUS_SERVER_DOWN);
                return null;
            }
            jsonString = buffer.toString();
//            processJsonString(mTaskId, jsonString);
            // FIXME: 21/11/2016 - for all exceptions show a message in SnackBar and ask to try later
        }
//        catch (Exception e) {
//            if (!Utility.isReleaseVersion(context)) {
//                e.printStackTrace();
//            }
//            throw new RuntimeException(TAG + ".processRemoteRequest - exception: " + e);
//        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException nothingCanBeDone) {
//                    Log.e(TAG, "processRemoteRequest - Error closing stream", e);
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

//    private DateTime getSelectedDateTime() {
//        if (mSelectedTime == null) {
//            int selectedHour;
//            int selectedMinute;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                selectedHour = mTimePicker.getHour();
//                selectedMinute = mTimePicker.getMinute();
//            } else {
//                selectedHour = mTimePicker.getCurrentHour();
//                selectedMinute = mTimePicker.getCurrentMinute();
//            }
//            mSelectedTime = new DateTime().withHourOfDay(selectedHour).withMinuteOfHour(selectedMinute);
//        }
//        return mSelectedTime;
//    }
    /**
     * Method to demonstrate building of Timetable API URL
     *
     * @param baseURL - Timetable API base URL without slash at the end ( Example :http://timetableapi.ptv.vic.gov.au )
     * @param privateKey - Developer Key supplied by PTV (((Example :"9c132d31-6a30-4cac-8d8b-8a1970834799")
     * @param uri - Request URI with parameters(Example :/v2/mode/0/line/8/stop/1104/directionid/0/departures/all/limit/5?for_utc=2014-08-15T06:18:08Z)
     * @param developerId- Developer ID supplied by PTV
     * @return Complete URL with signature
     * @throws Exception
     *
     */
    @NonNull
    public static String buildTTAPIURL(final String baseURL, final String privateKey, final String uri,
                                       final int developerId) throws Exception {

        String HMAC_SHA1_ALGORITHM = "HmacSHA1";
        StringBuffer uriWithDeveloperID = new StringBuffer().append(uri).append(uri.contains("?") ? "&" : "?")
                .append("devid=" + developerId);
        byte[] keyBytes = privateKey.getBytes();
        byte[] uriBytes = uriWithDeveloperID.toString().getBytes();
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        byte[] signatureBytes = mac.doFinal(uriBytes);
        StringBuffer signature = new StringBuffer(signatureBytes.length * 2);
        for (byte signatureByte : signatureBytes)
        {
            int intVal = signatureByte & 0xff;
            if (intVal < 0x10){
                signature.append("0");
            }
            signature.append(Integer.toHexString(intVal));
        }
        StringBuffer url = new StringBuffer(baseURL).append(uri).append(uri.contains("?") ? "&" : "?")
                .append("devid=" + developerId).append("&signature=" + signature.toString().toUpperCase());
        return url.toString();
    }
}
