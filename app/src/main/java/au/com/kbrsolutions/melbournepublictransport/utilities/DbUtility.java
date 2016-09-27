package au.com.kbrsolutions.melbournepublictransport.utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import au.com.kbrsolutions.melbournepublictransport.data.LatLonDetails;
import au.com.kbrsolutions.melbournepublictransport.data.MptContract;
import au.com.kbrsolutions.melbournepublictransport.data.NearbyStopsDetails;
import au.com.kbrsolutions.melbournepublictransport.data.NearbyTrainsDetails;

/**
 * Created by business on 26/09/2016.
 */

public class DbUtility {

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public Map<Double, NearbyTrainsDetails> getNearbyTrainDetails(LatLonDetails latLonDetails, Context context) {
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
        int latIdx;
        int lonIdx;
        double distance;
        Map<Double, NearbyTrainsDetails> map = new TreeMap<>();
        while (cursor.moveToNext()) {
            stopIdIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_STOP_ID);
            locationNameIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_LOCATION_NAME);
            latIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_LATITUDE);
            lonIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_LONGITUDE);
            distance = Miscellaneous.distance(
                    latLonDetails.latitude,
                    latLonDetails.longitude,
                    cursor.getDouble(latIdx),
                    cursor.getDouble(lonIdx),
                    "K");
            map.put(distance, new NearbyTrainsDetails(
                    cursor.getString(stopIdIdx),
                    cursor.getString(locationNameIdx),
                    cursor.getDouble(latIdx),
                    cursor.getDouble(lonIdx),
                    distance
            ));
//            Log.v(TAG, "distance/stopId/stopName: " + distance + " - " + cursor.getString(stopIdIdx) + "/" + cursor.getString(locationNameIdx));
        }
        cursor.close();
        NearbyTrainsDetails nearbyTrainsDetails;
        for (Double key : map.keySet()) {
            nearbyTrainsDetails = map.get(key);
            Log.v(TAG, "distance/stopId/stopName: " +
                    nearbyTrainsDetails.distanceMeters + " - " +
                    nearbyTrainsDetails.stopId + "/" +
                    nearbyTrainsDetails.stopName);
        }
        return map;
    }

    public void fillInStopNames(List<NearbyStopsDetails> nearbyStopsDetailsList, Context context) {
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
        for (int i = 0; i < stopIds.length; i++) {
            Log.v(TAG, "stopIds: " + i + " - " + stopIds[i]);
        }
        Log.v(TAG, "whereClause: " + whereClause);

//        Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri(MptContract.StopDetailEntry.ANY_FAVORITE_FLAG);
        Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri(MptContract.StopDetailEntry.NON_FAVORITE_FLAG);
        Cursor cursor = context.getContentResolver().query(
                uri,
                colNames,
                whereClause,
                stopIds,
                null
        );
        Log.v(TAG, "cursor count: " + cursor.getCount());
        Map<String, String> map = new HashMap<>();

        int stopIdIdx;
        int locationNameIdx;
        while (cursor.moveToNext()) {
            stopIdIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_STOP_ID);
            locationNameIdx = cursor.getColumnIndex(MptContract.StopDetailEntry.COLUMN_LOCATION_NAME);
            Log.v(TAG, cursor.getString(stopIdIdx) + "/" + cursor.getString(locationNameIdx));
            map.put(cursor.getString(stopIdIdx), cursor.getString(locationNameIdx));
        }

        cursor.close();

        for (NearbyStopsDetails nearbyStopsDetails : nearbyStopsDetailsList) {
            nearbyStopsDetails.stopName = map.get(nearbyStopsDetails.stopId);
        }
        Log.v(TAG, "after stopName updated");
        for (NearbyStopsDetails nearbyStopsDetails : nearbyStopsDetailsList) {
            Log.v(TAG, "details: " + nearbyStopsDetails);
        }
    }
}
