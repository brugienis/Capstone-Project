package au.com.kbrsolutions.melbournepublictransport.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.data.MptContract;
import au.com.kbrsolutions.melbournepublictransport.data.NearbyStopsDetails;

/**
 * Created by business on 26/09/2016.
 */

public class DbUtility {

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public void updateStopDetails(long rowId, Context context, String favoriteColumnValue) {
//        Log.v(TAG, "updateStopDetails - start - favoriteColumnValue: " + favoriteColumnValue);
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(MptContract.StopDetailEntry.COLUMN_FAVORITE, favoriteColumnValue);
        int count = context.getContentResolver().update(
                MptContract.StopDetailEntry.CONTENT_URI,
                updatedValues,
                MptContract.StopDetailEntry._ID + "= ?",
                new String [] { String.valueOf(rowId)});
//        Log.v(TAG, "updateStopDetails - end");
    }

    private void printContents(Cursor cursor) {
        Log.v(TAG, "printContents - start");
        int stopIdIdx;
        int locationNameIdx;
        int lfavoriteIdx;
        while (cursor.moveToNext()) {
            stopIdIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_STOP_ID);
            locationNameIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_LOCATION_NAME);
            lfavoriteIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_FAVORITE);
            Log.v(TAG, cursor.getString(stopIdIdx) + "/" +
                    cursor.getString(locationNameIdx) + "/" +
                    cursor.getString(lfavoriteIdx));
        }
        Log.v(TAG, "printContents - start");
    }

    public List<NearbyStopsDetails> getNearbyTrainDetails(LatLngDetails latLonDetails, Context context) {
        Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri(MptContract.StopDetailEntry.ANY_FAVORITE_FLAG);
        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        int stopIdIdx;
        int locationNameIdx;
        int suburbIdx;
        int latIdx;
        int lonIdx;
        double distance;
        Map<Double, NearbyStopsDetails> map = new TreeMap<>();
        while (cursor.moveToNext()) {
            stopIdIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_STOP_ID);
            locationNameIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_LOCATION_NAME);
            suburbIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_SUBURB);
            latIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_LATITUDE);
            lonIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_LONGITUDE);
            distance = Miscellaneous.distance(
                    latLonDetails.latitude,
                    latLonDetails.longitude,
                    cursor.getDouble(latIdx),
                    cursor.getDouble(lonIdx),
                    "K");
            map.put(distance, new NearbyStopsDetails(
                    cursor.getString(locationNameIdx),
                    null,
                    cursor.getString(suburbIdx),
                    NearbyStopsDetails.TRAIN_ROUTE_TYPE,
                    cursor.getString(stopIdIdx),
                    cursor.getDouble(latIdx),
                    cursor.getDouble(lonIdx),
                    distance
            ));
//            Log.v(TAG, "distance/stopId/stopName: " + distance + " - " + cursor.getString(stopIdIdx) + "/" + cursor.getString(locationNameIdx));
        }
        cursor.close();
        List<NearbyStopsDetails> nearbyStopsDetailsList = new ArrayList<>(map.size());
        NearbyStopsDetails nearbyStopsDetails;
        // FIXME: 28/09/2016 - get below from settings
        int nearStopsLimitCnt = 10;
        int cnt = 0;
        for (Double key : map.keySet()) {
            nearbyStopsDetails = map.get(key);
            nearbyStopsDetailsList.add(nearbyStopsDetails);
//            Log.v(TAG, "distance/stopId/stopName: " +
//                    nearbyStopsDetails.distance + " - " +
//                    nearbyStopsDetails.stopId + "/" +
//                    nearbyStopsDetails.stopName);
            if (cnt++ > nearStopsLimitCnt) {
                break;
            }
        }
        return nearbyStopsDetailsList;
    }

    private final static String STOP_ID = "Stop iD ";
    public void fillInMissingDetails(List<NearbyStopsDetails> nearbyStopsDetailsList, Context context) {
//    public List<NearbyStopsDetails> fillInMissingDetails(Map<Double, NearbyStopsDetails> nearbyStopsDetailsMap, Context context) {
//        Log.v(TAG, "fillInMissingDetails start");
        String[] stopIds = new String[nearbyStopsDetailsList.size()];
        int cnt = 0;
        for (NearbyStopsDetails details : nearbyStopsDetailsList) {
            stopIds[cnt++] = details.stopId;
        }
        String[] colNames = {
                MptContract.StopDetailEntry.COLUMN_STOP_ID,
                MptContract.StopDetailEntry.COLUMN_LOCATION_NAME,
                MptContract.StopDetailEntry.COLUMN_SUBURB};
//        String[] stopIds = {"101", "102", "103", "104", "107", "108"};
        String whereClause = MptContract.StopDetailEntry.COLUMN_STOP_ID +
                " IN (" +
                TextUtils.join(",", Collections.nCopies(stopIds.length, "?"))
                + ")";
//        for (int i = 0; i < stopIds.length; i++) {
//            Log.v(TAG, "stopIds: " + i + " - " + stopIds[i]);
//        }
//        Log.v(TAG, "whereClause: " + whereClause);

//        Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri(MptContract.StopDetailEntry.ANY_FAVORITE_FLAG);
        Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri(MptContract.StopDetailEntry.ANY_FAVORITE_FLAG);
        Cursor cursor = context.getContentResolver().query(
                uri,
                colNames,
                whereClause,
                stopIds,
                null
        );
        Log.v(TAG, "cursor count: " + cursor.getCount());
        Map<String, MissingDetails> missingDetailsMap = new HashMap<>();

        int stopIdIdx;
        int locationNameIdx;
        int suburbIdx;
        String locationName;
        String suburb;
        while (cursor.moveToNext()) {
            stopIdIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_STOP_ID);
            locationNameIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_LOCATION_NAME);
            suburbIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_SUBURB);
            locationName = cursor.getString(locationNameIdx);
            suburb = cursor.getString(suburbIdx);
//            Log.v(TAG, cursor.getString(stopIdIdx) + "/" + cursor.getString(locationNameIdx));
            if (locationName != null && suburb != null) {
                missingDetailsMap.put(cursor.getString(stopIdIdx), new MissingDetails(
                        cursor.getString(locationNameIdx),
                        cursor.getString(suburbIdx)
                ));
            }
        }
        cursor.close();
        for (String key : missingDetailsMap.keySet()) {
            MissingDetails missingDetails = missingDetailsMap.get(key);
            Log.v(TAG, "fillInMissingDetails - locationName/suburb: " + missingDetails.locationName + "/" + missingDetails.suburb);
        }

        String stopId;
        NearbyStopsDetails nearbyStopsDetails;
        for (int i = 0; i < nearbyStopsDetailsList.size(); i++) {
            stopId = nearbyStopsDetailsList.get(i).stopId;
            nearbyStopsDetails = nearbyStopsDetailsList.get(i);
            if (missingDetailsMap.containsKey(stopId)) {
                nearbyStopsDetailsList.set(i, new NearbyStopsDetails(
                        missingDetailsMap.get(stopId).locationName,
                        nearbyStopsDetails.stopAddress,
                        missingDetailsMap.get(stopId).suburb,
                        nearbyStopsDetails.routeType,
                        nearbyStopsDetails.stopId,
                        nearbyStopsDetails.latitude,
                        nearbyStopsDetails.longitude,
                        nearbyStopsDetails.distance
                ));
            } else {

            }
        }
    }

    class MissingDetails {
        final String locationName;
        final String suburb;

        MissingDetails(String locationName, String suburb) {
            this.locationName = locationName;
            this.suburb = suburb;
        }

    }
}
