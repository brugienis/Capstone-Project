package au.com.kbrsolutions.melbournepublictransport.remote;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import au.com.kbrsolutions.melbournepublictransport.data.MptContract;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.utilities.JodaDateTimeUtility;

public class RemoteMptEndpointUtil {

    private DateTime mSelectedTime;
    private final static int DEVELOPER_ID = 1000796;
    private final static String PRIVATE_KEY = "8d3c3f43-4244-11e6-a0ce-06f54b901f07";
    private final static String PTV_BASE_URL = "http://timetableapi.ptv.vic.gov.au";

    private final static String TAG = RemoteMptEndpointUtil.class.getSimpleName();

    public static boolean performHealthCheck() {
        final String SECURITY_TOKEN_OK = "securityTokenOK";
        final String CLIENT_CLOCK_OK = "clientClockOK";
        final String MEM_CACHE_OK = "memcacheOK";
        final String DATABASE_OK = "databaseOK";
        DateTime dateTime = getCurrentDateTime();
        final String uri = "/v2/healthcheck?timestamp=" + JodaDateTimeUtility.getUtcTime(dateTime);
        String jsonString = processRemoteRequest(uri);

        JSONObject forecastJson = null;
        boolean databaseOK = false;
        try {
            forecastJson = new JSONObject(jsonString);
            boolean securityTokenOk = forecastJson.getBoolean(SECURITY_TOKEN_OK);
            boolean clientClockOK = forecastJson.getBoolean(CLIENT_CLOCK_OK);
            boolean memcacheOK = forecastJson.getBoolean(MEM_CACHE_OK);
            databaseOK = forecastJson.getBoolean(DATABASE_OK);
//            Log.v(TAG, "performHealthCheck - securityTokenOk/clientClockOK/memcacheOK/databaseOK: " + securityTokenOk + "/" + clientClockOK + "/" + memcacheOK + "/" + databaseOK);

        } catch (JSONException e) {
            // FIXME: 5/09/2016 - handle exception properly
            e.printStackTrace();
        }

        return databaseOK;
    }
    /*

    RemoteMptEndpointUtil.java:52 - pointing to line forecastJson = new JSONObject(jsonString); in performHealthCheck()
    Error


                                                                                                     --------- beginning of crash
09-12 08:40:11.400 10671-10985/au.com.kbrsolutions.melbournepublictransport E/AndroidRuntime: FATAL EXCEPTION: IntentService[RequestProcessorService]
                                                                                              Process: au.com.kbrsolutions.melbournepublictransport, PID: 10671
                                                                                              java.lang.NullPointerException: Attempt to invoke virtual method 'int java.lang.String.length()' on a null object reference
                                                                                                  at org.json.JSONTokener.nextCleanInternal(JSONTokener.java:116)
                                                                                                  at org.json.JSONTokener.nextValue(JSONTokener.java:94)
                                                                                                  at org.json.JSONObject.<init>(JSONObject.java:156)
                                                                                                  at org.json.JSONObject.<init>(JSONObject.java:173)
                                                                                                  at au.com.kbrsolutions.melbournepublictransport.remote.RemoteMptEndpointUtil.performHealthCheck(RemoteMptEndpointUtil.java:52)
                                                                                                  at au.com.kbrsolutions.melbournepublictransport.data.DatabaseContentRefresher.performHealthCheck(DatabaseContentRefresher.java:19)
                                                                                                  at au.com.kbrsolutions.melbournepublictransport.data.RequestProcessorService.onHandleIntent(RequestProcessorService.java:60)
                                                                                                  at android.app.IntentService$ServiceHandler.handleMessage(IntentService.java:65)
     */

    public static List<ContentValues> getLineDetails(int mode) {
        List<ContentValues> lineDetailsContentValuesList = new ArrayList<>();
        final String uri = "/v2/lines/mode/" + mode;
        String jsonString = processRemoteRequest(uri);

        int routeType;
        String lineId;
        String lineName;
        String lineNameShort;
        JSONArray lineArray = null;
        try {
            lineArray = new JSONArray(jsonString);
//            Log.v(TAG, "processJsonString - lineArray: " + lineArray);
//            Log.v(TAG, "processJsonString - lineArray length: " + lineArray.length());
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return lineDetailsContentValuesList;
    }

    public static List<ContentValues> getStopDetailsForLine(int mode, String  lineId) {
        List<ContentValues> stopDetailsContentValuesList = new ArrayList<>();
        final String uri = "/v2/mode/" + mode + "/line/" + lineId + "/stops-for-line";
        String jsonString = processRemoteRequest(uri);

        int routeType;
        String stopId;
        String locationName;
        double latitude;
        double longitude;
        String favorite = "n";  // non favorite stop
        JSONArray lineArray = null;
        try {
            JSONArray stopsArray = new JSONArray(jsonString);
//            Log.v(TAG, "processJsonString - lineArray length: " + stopsArray.length());
//            Log.v(TAG, "processJsonString - lineArray: " + stopsArray);
//            Log.v(TAG, "processJsonString - lineArray length: " + stopsArray.length());
//            String prevStopIdAndLocationName = null;
//            String currStopIdAndLocationName;
//            int ignoredCnt = 0;
            for(int i = 0; i < stopsArray.length(); i++) {
                JSONObject oneLineObject = stopsArray.getJSONObject(i);
                routeType = oneLineObject.getInt("route_type");
                stopId = oneLineObject.getString("stop_id");
                locationName = oneLineObject.getString("location_name");
                latitude = oneLineObject.getDouble("lat");
                longitude = oneLineObject.getDouble("lon");
//                lineDetailsList.add(new StopDetails(routeType, stopId, locationName, latitude, longitude, favorite));
                ContentValues values = new ContentValues();
                values.put(MptContract.StopDetailEntry.COLUMN_ROUTE_TYPE, routeType);
                values.put(MptContract.StopDetailEntry.COLUMN_STOP_ID, stopId);
                values.put(MptContract.StopDetailEntry.COLUMN_LOCATION_NAME, locationName);
                values.put(MptContract.StopDetailEntry.COLUMN_LATITUDE, latitude);
                values.put(MptContract.StopDetailEntry.COLUMN_LONGITUDE, longitude);
                values.put(MptContract.StopDetailEntry.COLUMN_FAVORITE, favorite);
                stopDetailsContentValuesList.add(values);
            }
//            Log.v(TAG, "getStopDetailsForLine - ignoredCnt: " + ignoredCnt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return stopDetailsContentValuesList;
    }

    public static List<NextDepartureDetails> getBroadNextDepartures(int mode, String stopId, int limit) {
        final String uri = "/v2/mode/" + mode + "/stop/" + stopId + "/departures/by-destination/limit/" + limit;
        String jsonString = processRemoteRequest(uri);

        NextDepartureDetails nextDepartureDetails = null;
        List<NextDepartureDetails> nextDepartureDetailsList = new ArrayList<>();
        try {
            JSONObject broadDeparturesObject = new JSONObject(jsonString);
            Log.v(TAG, "processJsonString - broadDeparturesObject: " + broadDeparturesObject);
            JSONArray broadDeparturesValuesArray = broadDeparturesObject.getJSONArray("values");
            Log.v(TAG, "processJsonString - valuesArray length: " + broadDeparturesValuesArray.length());
            for(int i = 0; i < broadDeparturesValuesArray.length(); i++) {
                JSONObject oneBroadDeparturesValueObject = broadDeparturesValuesArray.getJSONObject(i);
                String timeTimetableUtc = oneBroadDeparturesValueObject.getString("time_timetable_utc");
                JSONObject platform = oneBroadDeparturesValueObject.getJSONObject("platform");
                JSONObject direction = platform.getJSONObject("direction");
                int directionId = direction.getInt("direction_id");

                JSONObject run = oneBroadDeparturesValueObject.getJSONObject("run");
//                        Log.v(TAG, "processJsonString - run: " + run);
                int runId = run.getInt("run_id");
                int destinationId = run.getInt("destination_id");
                int numSkipped = run.getInt("num_skipped");
                Log.v(TAG, "processJsonString - directionId/runId/numSkipped/destinationId/timeTimetableUtc: " +
                        directionId + "/" +
                        runId + "/" +
                        numSkipped + "/" +
                        destinationId + "/" +
                        JodaDateTimeUtility.getLocalTimeFromUtcString(timeTimetableUtc));
                nextDepartureDetailsList.add(nextDepartureDetails = new NextDepartureDetails(
                        directionId,
                        runId,
                        numSkipped,
                        destinationId,
                        JodaDateTimeUtility.getLocalTimeFromUtcString(timeTimetableUtc).toString()));
                if (directionId == 1) { //City (Flinders Street)
//                eventBus.post(new PtvTimeTableControllerEvents.Builder((PtvTimeTableControllerEvents.PtvTimeTableEvents.GOT_BROAD_NEXT_DEPARTURES))
//                        .setDestinationId(destinationId)
//                        .setRunId(runId)
//                        .build());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nextDepartureDetailsList;
    }

    @Nullable
    private static String processRemoteRequest(String uri) {

        String urlString = null;
        try {
            urlString = buildTTAPIURL(PTV_BASE_URL, PRIVATE_KEY, uri, DEVELOPER_ID);
//            Log.v(TAG, "performHealthCheck - urlString: " + urlString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri.Builder uriBuilder = Uri.parse(urlString).buildUpon();
        Uri builtUri = uriBuilder.build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

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
        } catch (IOException e) {
            Log.e(TAG, "processRemoteRequest - Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
//            setLocationStatus(getContext(), LOCATION_STATUS_SERVER_DOWN);
        }
//        catch (JSONException e) {
//            Log.e(TAG, e.getMessage(), e);
//            e.printStackTrace();
////            setLocationStatus(getContext(), LOCATION_STATUS_SERVER_INVALID);
//        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "processRemoteRequest - Error closing stream", e);
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
