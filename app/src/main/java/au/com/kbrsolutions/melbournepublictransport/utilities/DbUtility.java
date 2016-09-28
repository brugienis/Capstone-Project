package au.com.kbrsolutions.melbournepublictransport.utilities;

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

import au.com.kbrsolutions.melbournepublictransport.data.LatLonDetails;
import au.com.kbrsolutions.melbournepublictransport.data.MptContract;
import au.com.kbrsolutions.melbournepublictransport.data.NearbyStopsDetails;

/**
 * Created by business on 26/09/2016.
 */

public class DbUtility {

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public List<NearbyStopsDetails> getNearbyTrainDetails(LatLonDetails latLonDetails, Context context) {
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
                    "train",
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

    public void fillInStopNames(List<NearbyStopsDetails> nearbyStopsDetailsList, Context context) {
        Log.v(TAG, "fillInStopNames start");
        String[] stopIds = new String[nearbyStopsDetailsList.size()];
        int cnt = 0;
        for (NearbyStopsDetails details : nearbyStopsDetailsList) {
            stopIds[cnt++] = details.stopId;
        }
        String[] colNames = {MptContract.StopDetailEntry.COLUMN_STOP_ID, MptContract.StopDetailEntry.COLUMN_LOCATION_NAME};
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
        Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri(MptContract.StopDetailEntry.NON_FAVORITE_FLAG);
        Cursor cursor = context.getContentResolver().query(
                uri,
                colNames,
                whereClause,
                stopIds,
                null
        );
//        Log.v(TAG, "cursor count: " + cursor.getCount());
        Map<String, String> map = new HashMap<>();

        int stopIdIdx;
        int locationNameIdx;
        String locationName;
        while (cursor.moveToNext()) {
            stopIdIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_STOP_ID);
            locationNameIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_LOCATION_NAME);
            locationName = cursor.getString(locationNameIdx);
//            Log.v(TAG, cursor.getString(stopIdIdx) + "/" + cursor.getString(locationNameIdx));
            if (locationName != null) {
                map.put(cursor.getString(stopIdIdx), locationName);
            }
        }

        cursor.close();

//        for (NearbyStopsDetails nearbyStopsDetails : nearbyStopsDetailsList) {
//            nearbyStopsDetails.stopName = map.get(nearbyStopsDetails.stopId);
//        }
//        Log.v(TAG, "after stopName updated");
//        for (NearbyStopsDetails nearbyStopsDetails : nearbyStopsDetailsList) {
//            Log.v(TAG, "details: " + nearbyStopsDetails);
//        }
        Log.v(TAG, "fillInStopNames end");
    }
}
