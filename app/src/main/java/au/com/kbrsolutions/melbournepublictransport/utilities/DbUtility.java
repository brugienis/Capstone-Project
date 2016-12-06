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
import au.com.kbrsolutions.melbournepublictransport.data.StopsNearbyDetails;

/**
 *
 * CThis class contains utility methods used to access database.
 *
 */
public class DbUtility {

    private final String TAG = ((Object) this).getClass().getSimpleName();

    private static final String EQUAL_QUESTION_MARK = "= ?";

    /**
     * Update the StopDetail favorite stop flag.
     *
     * @param rowId
     * @param context
     * @param favoriteColumnValue
     */
    public void updateStopDetails(long rowId, Context context, String favoriteColumnValue) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(MptContract.StopDetailEntry.COLUMN_FAVORITE, favoriteColumnValue);
        context.getContentResolver().update(
                MptContract.StopDetailEntry.CONTENT_URI,
                updatedValues,
                MptContract.StopDetailEntry._ID + EQUAL_QUESTION_MARK,
                new String [] { String.valueOf(rowId)});
    }

    /**
     * Helper class - use for testing.
     *
     * @param cursor
     */
    @SuppressWarnings("unused")
    private void printContents(Cursor cursor) {
        int stopIdIdx;
        int locationNameIdx;
        int favoriteIdx;
        while (cursor.moveToNext()) {
            stopIdIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_STOP_ID);
            locationNameIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_LOCATION_NAME);
            favoriteIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_FAVORITE);
            Log.v(TAG, cursor.getString(stopIdIdx) + "/" +
                    cursor.getString(locationNameIdx) + "/" +
                    cursor.getString(favoriteIdx));
        }
    }

    /**
     * Find ten train stops that are closest to the passed GEO position.
     *
     * Result list is sorted by distanceKm.
     *
     * @param latLngDetails
     * @param context
     * @return
     */
    public List<StopsNearbyDetails> getNearbyTrainDetails(LatLngDetails latLngDetails,
                                                          Context context) {
        Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri(
                MptContract.StopDetailEntry.ANY_FAVORITE_FLAG);
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
        Map<Double, StopsNearbyDetails> map = new TreeMap<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                stopIdIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_STOP_ID);
                locationNameIdx = cursor.getColumnIndex(
                        MptContract.StopDetailEntry.COLUMN_LOCATION_NAME);
                suburbIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_SUBURB);
                latIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_LATITUDE);
                lonIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_LONGITUDE);
                distance = Miscellaneous.distanceKm(
                        latLngDetails.latitude,
                        latLngDetails.longitude,
                        cursor.getDouble(latIdx),
                        cursor.getDouble(lonIdx));
                map.put(distance, new StopsNearbyDetails(
                        cursor.getString(locationNameIdx),
                        null,
                        cursor.getString(suburbIdx),
                        StopsNearbyDetails.TRAIN_ROUTE_TYPE,
                        cursor.getString(stopIdIdx),
                        cursor.getDouble(latIdx),
                        cursor.getDouble(lonIdx),
                        distance
                ));
            }
            cursor.close();
        }
        List<StopsNearbyDetails> stopsNearbyDetailsList = new ArrayList<>(map.size());
        StopsNearbyDetails stopsNearbyDetails;
        // In the future move the value below to settings
        int nearStopsLimitCnt = 10;
        int cnt = 0;
        for (Double key : map.keySet()) {
            stopsNearbyDetails = map.get(key);
            stopsNearbyDetailsList.add(stopsNearbyDetails);
            if (cnt++ > nearStopsLimitCnt) {
                break;
            }
        }
        return stopsNearbyDetailsList;
    }

    private static final String IN = " IN (";
    private static final String QUESTION_MARK = "?";
    private static final String RIGHT_RIGHT_PARENTH = ")";

    /**
     * Try to fill in missing values in the 'stops nearby' list:
     *
     *      location name
     *      suburb name
     *
     * @param stopsNearbyDetailsList
     * @param context
     */
    public void fillInMissingDetails(List<StopsNearbyDetails> stopsNearbyDetailsList,
                                     Context context) {
        String[] stopIds = new String[stopsNearbyDetailsList.size()];
        int cnt = 0;
        for (StopsNearbyDetails details : stopsNearbyDetailsList) {
            stopIds[cnt++] = details.stopId;
        }
        String[] colNames = {
                MptContract.StopDetailEntry.COLUMN_STOP_ID,
                MptContract.StopDetailEntry.COLUMN_LOCATION_NAME,
                MptContract.StopDetailEntry.COLUMN_SUBURB};
        /* build IN clause */
        String whereClause = MptContract.StopDetailEntry.COLUMN_STOP_ID +
                IN +
                TextUtils.join(",", Collections.nCopies(stopIds.length, QUESTION_MARK))
                + RIGHT_RIGHT_PARENTH;

        Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri(
                MptContract.StopDetailEntry.ANY_FAVORITE_FLAG);
        Cursor cursor = context.getContentResolver().query(
                uri,
                colNames,
                whereClause,
                stopIds,
                null
        );
//        Log.v(TAG, "cursor count: " + cursor.getCount());
        Map<String, MissingDetails> missingDetailsMap = new HashMap<>();

        int stopIdIdx;
        int locationNameIdx;
        int suburbIdx;
        String locationName;
        String suburb;
        if (cursor != null) {
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
        }

        String stopId;
        StopsNearbyDetails stopsNearbyDetails;
        for (int i = 0; i < stopsNearbyDetailsList.size(); i++) {
            stopId = stopsNearbyDetailsList.get(i).stopId;
            stopsNearbyDetails = stopsNearbyDetailsList.get(i);
            if (missingDetailsMap.containsKey(stopId)) {
                stopsNearbyDetailsList.set(i, new StopsNearbyDetails(
                        missingDetailsMap.get(stopId).locationName,
                        stopsNearbyDetails.stopAddress,
                        missingDetailsMap.get(stopId).suburb,
                        stopsNearbyDetails.routeType,
                        stopsNearbyDetails.stopId,
                        stopsNearbyDetails.latitude,
                        stopsNearbyDetails.longitude,
                        stopsNearbyDetails.distance
                ));
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
