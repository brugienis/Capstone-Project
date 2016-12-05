package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import au.com.kbrsolutions.melbournepublictransport.utilities.Miscellaneous;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(AndroidJUnit4.class)
public class MptProviderTest_NoDeleteTest {

    private final String TAG = ((Object) this).getClass().getSimpleName();

    Context mContext;

    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
    }

//    @After
//    public void tearDown() throws Exception {
//
//    }

    @Test
    public void testFindingTrainStopsNearby() throws Exception {

        Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri(MptContract.StopDetailEntry.ANY_FAVORITE_FLAG);
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        // current location: lat/lon: -38.0811845/145.2030305
        double currLat = -38.0811845;
        double currLon = 145.2030305;

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
            distance = Miscellaneous.distanceKm(
                    currLat,
                    currLon,
                    cursor.getDouble(latIdx),
                    cursor.getDouble(lonIdx)
            );
            map.put(distance, new NearbyTrainsDetails(
                    cursor.getString(stopIdIdx),
                    cursor.getString(locationNameIdx),
                    cursor.getDouble(latIdx),
                    cursor.getDouble(lonIdx),
                    distance
                    ));
//            Log.v(TAG, "distanceKm/stopId/stopName: " + distanceKm + " - " + cursor.getString(stopIdIdx) + "/" + cursor.getString(locationNameIdx));
        }
        cursor.close();
        NearbyTrainsDetails nearbyTrainsDetails;
        for (Double key : map.keySet()) {
            nearbyTrainsDetails = map.get(key);
            Log.v(TAG, "distanceKm/stopId/stopName: " +
                    nearbyTrainsDetails.distanceMeters + " - " +
                    nearbyTrainsDetails.stopId + "/" +
                    nearbyTrainsDetails.stopName);
        }
    }

    class NearbyTrainsDetails {
        public final String stopId;
        public final String stopName;
        public final double latitude;
        public final double longitude;
        public final double distanceMeters;

        NearbyTrainsDetails(String stopId, String stopName, double latitude, double longitude, double distanceMeters) {
            this.stopId = stopId;
            this.stopName = stopName;
            this.latitude = latitude;
            this.longitude = longitude;
            this.distanceMeters = distanceMeters;
        }

    }

    @Test
    public void testQueryWithInClause() throws Exception {

        Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri(MptContract.StopDetailEntry.ANY_FAVORITE_FLAG);
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        TestUtilities.printContents(cursor);

        assertNotNull("Error: cursor can not be null", cursor);
        assertNotEquals("Error: cursor can not be empty", 0, cursor.getCount());
        assertEquals("wrong inserted rows count", 218, cursor.getCount());

        String[] stopIdArray = getStopIdArray();
        String[] colNames = {MptContract.StopDetailEntry.COLUMN_STOP_ID, MptContract.StopDetailEntry.COLUMN_LOCATION_NAME};
        String whereClause = MptContract.StopDetailEntry.COLUMN_STOP_ID +
                " IN (" +
                TextUtils.join(",", Collections.nCopies(stopIdArray.length, "?"))
                + ")";
//        for (int i = 0; i < stopIdArray.length; i++) {
//            Log.v(TAG, "stopIds: " + i + " - " + stopIdArray[i]);
//        }
        Log.v(TAG, "whereClause: " + whereClause);

//        Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri(MptContract.StopDetailEntry.ANY_FAVORITE_FLAG);
        uri = MptContract.StopDetailEntry.buildFavoriteStopsUri(MptContract.StopDetailEntry.ANY_FAVORITE_FLAG);
        cursor = mContext.getContentResolver().query(
                uri,
                colNames,
                whereClause,
                stopIdArray,
                null
        );
        Log.v(TAG, "cursor count: " + cursor.getCount());
    }
    /*
            SELECT stop_id, location_name
            FROM stop_detail
            WHERE (stop_id IN (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            AND (stop_detail.favorite = ?)) ORDER BY X

            SELECT stop_id, location_name
            FROM stop_detail
            WHERE (stop_id IN (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            AND (stop_detail.favorite = ? OR stop_detail.favorite = ?))
            ORDER BY X
            {
   "result":{
      "distanceKm":5.96205355E-5,
      "suburb":"Carrum Downs",
      "transport_type":"bus",
      "routeType":2,
      "stop_id":27751,
      "location_name":"Mccormicks Rd\/Wedge Rd ",
      "lat":-38.08783,
      "lon":145.199127
   },
   "type":"stop"
}
{
   "distanceKm":0,
   "suburb":"Carrum",
   "transport_type":"train",
   "routeType":0,
   "stop_id":1035,
   "location_name":"Carrum Station",
   "lat":-38.0748978,
   "lon":145.122421
}
     */

    @Test
    public void testExecQueryWithInClause() throws Exception {
        SQLiteDatabase db = new MptDbHelper(this.mContext).getWritableDatabase();
        Cursor cursor = db.rawQuery(buildSimpleRowQuery(), null);
//        Cursor cursor = db.rawQuery(buildInRowQuery(), null);
        assertNotNull("Error: cursor can not be null", cursor);
        assertNotEquals("Error: cursor can not be empty", 0, cursor.getCount());
    }

    private String buildSimpleRowQuery() {
        return "SELECT " +
            "stop_id, location_name" +
            " FROM stop_detail" +
            " WHERE stop_id = 18995";
    }

    private String buildInRowQuery() {
        return "SELECT " +
                "stop_id, location_name" +
                " FROM stop_detail" +
                " WHERE (stop_id IN " +
                    "(27751," +
                    "18995," +
                    "19481," +
                    "18996," +
                    "23916," +
                    "18994," +
                    "12129," +
                    "27749," +
                    "18997," +
                    "27739," +
                    "19975," +
                    "21562," +
                    "18992," +
                    "27738," +
                    "18998," +
                    "27746," +
                    "14069," +
                    "27670," +
                    "18999," +
                    "19000," +
                    "19971," +
                    "27243," +
                    "12731," +
                    "19978," +
                    "15846," +
                    "19970," +
                    "12732," +
                    "15845," +
                    "12734," +
                    "12803)" +
//                " AND (stop_detail.favorite = ? OR stop_detail.favorite = ?)"
              ")";
    }

    private String[] getStopIdArray() {
        return new String[]{
                "27751",
                "18995",
                "19481",
                "18996",
                "23916",
                "18994",
                "12129",
                "27749",
                "18997",
                "27739",
                "19975",
                "21562",
                "18992",
                "27738",
                "18998",
                "27746",
                "14069",
                "27670",
                "18999",
                "19000",
                "19971",
                "27243",
                "12731",
                "19978",
                "15846",
                "19970",
                "12732",
                "15845",
                "12734",
                "12803"
        };
    }
}
