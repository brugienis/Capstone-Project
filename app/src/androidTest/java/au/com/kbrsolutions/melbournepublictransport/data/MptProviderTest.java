package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.data.MptContract.LineDetailEntry;
import au.com.kbrsolutions.melbournepublictransport.data.MptContract.StopDetailEntry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class MptProviderTest {

    private final String TAG = ((Object) this).getClass().getSimpleName();

    Context mContext;

    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
        deleteAllRecordsFromProvider();
    }

//    @After
//    public void tearDown() throws Exception {
//
//    }

    @Test
    public void testDelete() throws Exception {
        // insert a row into line_detail table
        ContentValues lineDetailetailValues = TestUtilities.createFrankstonLineDetailsValues();

        Uri resultUri = mContext.getContentResolver().insert(
                LineDetailEntry.CONTENT_URI,
                lineDetailetailValues
        );

        long lineDetailRowId = ContentUris.parseId(resultUri);

        // insert 2 rows
        ContentValues stopDetailvalues = TestUtilities.createFrankstonLineStopDetailsValues(lineDetailRowId, StopDetailEntry.NON_FAVORITE_FLAG);

        mContext.getContentResolver().insert(
                StopDetailEntry.CONTENT_URI,
                stopDetailvalues
        );

        stopDetailvalues = TestUtilities.createFrankstonLineStopDetailsValues(lineDetailRowId, StopDetailEntry.FAVORITE_FLAG);
        // FIXME: 8/09/2016 - below added because 'UNIQUE' constraint in table definition would replace the row with the same stop_id and Location_name
        // fix the TestUtilities.createFrankstonLineStopDetailsValues(...) and remove the line below
        stopDetailvalues.put(StopDetailEntry.COLUMN_STOP_ID, 109);

        mContext.getContentResolver().insert(
                StopDetailEntry.CONTENT_URI,
                stopDetailvalues
        );

        // delete all (2) rows

        // Register a content observer for our stop_detail delete.
        TestUtilities.TestContentObserver stopDetails = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(StopDetailEntry.CONTENT_URI, true, stopDetails);

        Uri uri = StopDetailEntry.buildFavoriteStopsUri(StopDetailEntry.ANY_FAVORITE_FLAG);
        int deletedRowsCnt = mContext.getContentResolver().delete(
                uri,
                null,
                null
        );
        assertEquals("Error: should delete 2 rows", 2, deletedRowsCnt);

        // If either of these fail, most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        stopDetails.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(stopDetails);

        // verify both rows deleted
        // FIXME: 6/09/2016 should not use the StopDetailEntry.CONTENT_URI?
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from stop_detail table during delete", 0, cursor.getCount());
        cursor.close();
    }

//    @Test
//    public void testBuildUriMatcher() throws Exception {
//
//    }

//    @Test
//    public void testOnCreate() throws Exception {
//
//    }

//    @Test
//    public void testQuery() throws Exception {
//
//    }

    @Test
    public void testQueryWithInClause() throws Exception {
        // insert a row into line_detail table
        ContentValues lineDetailValues = TestUtilities.createFrankstonLineDetailsValues();

        Uri resultUri = mContext.getContentResolver().insert(
                LineDetailEntry.CONTENT_URI,
                lineDetailValues
        );

        long locationRowId = ContentUris.parseId(resultUri);
        // insert the first row into stop_detail table

        List<ContentValues> contentValuesList =
                TestUtilities.createManyFrankstonLineStopDetailsValues(
                        locationRowId,
                        StopDetailEntry.NON_FAVORITE_FLAG,
                        10);

        Uri stopDetailsUri;
        // FIXME: 26/09/2016 replace below with the batch insert
        for (ContentValues cv : contentValuesList) {
            stopDetailsUri = mContext.getContentResolver().insert(
                    StopDetailEntry.CONTENT_URI,
                    cv
            );
            Log.v(TAG, "inserted id: " + ContentUris.parseId(stopDetailsUri));
        }

        Uri uri = StopDetailEntry.buildFavoriteStopsUri(StopDetailEntry.NON_FAVORITE_FLAG);
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
        assertEquals("wrong inserted rows count", 10, cursor.getCount());

        String[] colNames = { StopDetailEntry.COLUMN_STOP_ID, StopDetailEntry.COLUMN_LOCATION_NAME };
        String[] stopIds = { "101", "102", "103", "104", "107", "108" };
        String whereClause = StopDetailEntry.COLUMN_STOP_ID + " IN (" + TextUtils.join(",", Collections.nCopies(stopIds.length, "?")) + ")";

        uri = StopDetailEntry.buildFavoriteStopsUri(StopDetailEntry.NON_FAVORITE_FLAG);
        cursor = mContext.getContentResolver().query(
                uri,
                colNames,
                whereClause,
                stopIds,
                null
        );

        TestUtilities.printContents(cursor);
//        while (cursor.moveToNext()) {
//            Log.v(TAG, cursor.getString(1) + "/" + cursor.getString(2));
//        }
        assertNotNull("Error: cursor can not be null", cursor);
        assertEquals("Error: cursor can not be empty", 6, cursor.getCount());

//        assertEquals("should", "", whereClause);


//        String[] names = { "name1", "name2" };
//        String query = "SELECT * FROM table"
//                + " WHERE name IN (" + TextUtils.join(",", Collections.nCopies(names.length, "?"))  + ")";
//        Cursor cursor = mDb.rawQuery(query, names);
    }

//    @Test
//    public void testGetType() throws Exception {
//
//    }

    @Test
    public void testInsert() throws Exception {
        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(StopDetailEntry.CONTENT_URI, true, tco);

        // insert a row into line_detail table
        ContentValues lineDetailValues = TestUtilities.createFrankstonLineDetailsValues();

        Uri resultUri = mContext.getContentResolver().insert(
                LineDetailEntry.CONTENT_URI,
                lineDetailValues
        );

        long locationRowId = ContentUris.parseId(resultUri);

        // Did our content observer get called?  If this fails, your insert stop_detail
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
//        tco.waitForNotificationOrFail();
//        mContext.getContentResolver().unregisterContentObserver(tco);

        // insert the first row into stop_detail table
        ContentValues stop_detailValues = TestUtilities.createFrankstonLineStopDetailsValues(locationRowId, StopDetailEntry.NON_FAVORITE_FLAG);

        mContext.getContentResolver().insert(
        StopDetailEntry.CONTENT_URI,
                stop_detailValues
        );

        // Did our content observer get called?  If this fails, your insert stop_detail
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

//        Log.v(TAG, "testInsert - resultUri: " + resultUri);

        // Test the basic content provider query
        Uri uri = StopDetailEntry.buildFavoriteStopsUri(StopDetailEntry.NON_FAVORITE_FLAG);
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        assertNotNull("Error: cursor can not be null", cursor);
        assertNotEquals("Error: cursor can not be empty", 0, cursor.getCount());
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testInsert, stop_detail query", cursor, stop_detailValues);

        // insert the second row into stop_detail table
        stop_detailValues = TestUtilities.createFrankstonLineStopDetailsValues(locationRowId, StopDetailEntry.FAVORITE_FLAG);

        mContext.getContentResolver().insert(
        StopDetailEntry.CONTENT_URI,
                stop_detailValues
        );

//        Log.v(TAG, "testInsert - resultUri: " + resultUri);

        // Test the basic content provider query
        uri = StopDetailEntry.buildFavoriteStopsUri(StopDetailEntry.FAVORITE_FLAG);
        cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        assertNotNull("Error: cursor can not be null", cursor);
        assertNotEquals("Error: cursor can not be empty", 0, cursor.getCount());
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testInsert, stop_detail query", cursor, stop_detailValues);

        // Verify there are 2 rows in the DB
        uri = StopDetailEntry.buildFavoriteStopsUri(StopDetailEntry.ANY_FAVORITE_FLAG);
        cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        assertNotNull("Error: cursor can not be null", cursor);
        assertNotEquals("Error: there must be 2 rows", 0, cursor.getCount());
    }

    @Test
    public void testInsertDuplicates() throws Exception {
        // insert a row into line_detail table
        ContentValues lineDetailValues = TestUtilities.createFrankstonLineDetailsValues();

        Uri resultUri = mContext.getContentResolver().insert(
                LineDetailEntry.CONTENT_URI,
                lineDetailValues
        );

        long locationRowId = ContentUris.parseId(resultUri);

        // insert the first row into stop_detail table
        ContentValues stopDetailsValues = TestUtilities.createFrankstonLineStopDetailsValues(locationRowId, StopDetailEntry.NON_FAVORITE_FLAG);

        resultUri = mContext.getContentResolver().insert(
        StopDetailEntry.CONTENT_URI,
                stopDetailsValues
        );

        long stopRowId0 = ContentUris.parseId(resultUri);
        Log.v(TAG, "testInsert - stopRowId0: " + stopRowId0);

        // Test the basic content provider query
        Uri uri = StopDetailEntry.buildFavoriteStopsUri(StopDetailEntry.NON_FAVORITE_FLAG);
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        assertNotNull("Error: cursor can not be null", cursor);
        assertEquals("Error: there must be 1 row", 1, cursor.getCount());
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testInsert, stop_detail query", cursor, stopDetailsValues);

        // try to insert the the same row again
        resultUri = mContext.getContentResolver().insert(
        StopDetailEntry.CONTENT_URI,
                stopDetailsValues
        );

        long stopRowId1 = ContentUris.parseId(resultUri);
        Log.v(TAG, "testInsert - stopRowId1: " + stopRowId1);



        // Test the basic content provider query
        uri = StopDetailEntry.buildFavoriteStopsUri(StopDetailEntry.NON_FAVORITE_FLAG);
        cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        assertNotNull("Error: cursor can not be null", cursor);
        assertEquals("Error: there still must be 1 row", 1, cursor.getCount());
        // Make sure we get the correct cursor out of the database

        // Verify there is 1 rows in the table
        uri = StopDetailEntry.buildFavoriteStopsUri(StopDetailEntry.NON_FAVORITE_FLAG);
        cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        assertNotNull("Error: cursor can not be null", cursor);
        assertNotEquals("Error: there must be 1 row", 0, cursor.getCount());
    }

    @Test
    public void testInsertReadProvider() {
        // insert a row into line_detail table
        ContentValues lineDetailetailValues = TestUtilities.createFrankstonLineDetailsValues();

        Uri resultUri = mContext.getContentResolver().insert(
                LineDetailEntry.CONTENT_URI,
                lineDetailetailValues
        );

        long lineDetailRowId = ContentUris.parseId(resultUri);

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(StopDetailEntry.CONTENT_URI, true, tco);

        // Insert a row
        String favoriteFlag = StopDetailEntry.NON_FAVORITE_FLAG;
        ContentValues stop_detailValues = TestUtilities.createFrankstonLineStopDetailsValues(lineDetailRowId, favoriteFlag);
        Uri stopDetailsUri = mContext.getContentResolver().insert(StopDetailEntry.CONTENT_URI, stop_detailValues);

        // Did our content observer get called?  If this fails, your insert stop_detail
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long stopDetailsRowId = ContentUris.parseId(stopDetailsUri);

        // Verify we got a row back.
        assertTrue(stopDetailsRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is a primary interface to the query results.
        Uri uri = StopDetailEntry.buildFavoriteStopsUri(favoriteFlag);
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating StopDetailsEntry.",
                cursor, stop_detailValues);
    }

    @Test
    public void testUpdate() throws Exception {
        // insert a row into line_detail table
        ContentValues lineDetailetailValues = TestUtilities.createFrankstonLineDetailsValues();

        Uri resultUri = mContext.getContentResolver().insert(
                LineDetailEntry.CONTENT_URI,
                lineDetailetailValues
        );

        long lineDetailRowId = ContentUris.parseId(resultUri);

        String favoriteFlag = StopDetailEntry.FAVORITE_FLAG;
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createFrankstonLineStopDetailsValues(lineDetailRowId, favoriteFlag);

        Uri locationUri = mContext.getContentResolver().
                insert(StopDetailEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue("Error: row was not inserted", locationRowId != -1);
        Log.d(TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(StopDetailEntry._ID, locationRowId);
        updatedValues.put(StopDetailEntry.COLUMN_LOCATION_NAME, "Carrum");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Uri uri = StopDetailEntry.buildFavoriteStopsUri(favoriteFlag);
        Cursor stopDetailsCursor = mContext.getContentResolver().query(uri, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        stopDetailsCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                StopDetailEntry.CONTENT_URI, updatedValues, StopDetailEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals("Error: no row was updated", 1, count);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // If the code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        stopDetailsCursor.unregisterContentObserver(tco);
        stopDetailsCursor.close();

        // A cursor is your primary interface to the query results.
        uri = StopDetailEntry.buildFavoriteStopsUri(favoriteFlag);
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,   // projection
                StopDetailEntry._ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateLocation.  Error validating stop_detail entry update.",
                cursor, updatedValues);

        cursor.close();
    }
    
    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;

    private static double latitude = 1.0;    //64.7488;
    private static double longitude = 10.0;  //-147.353;
    private static double increase = 1.0;

    static ContentValues[] createBulkInsertStopDetailsValues(long lineDetailRowId, String favoriteFlag) {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];
        /*

        testValues.put(MptContract.StopDetailEntry.COLUMN_ROUTE_TYPE, 0);
        testValues.put(MptContract.StopDetailEntry.COLUMN_STOP_ID, "101");
        testValues.put(MptContract.StopDetailEntry.COLUMN_LOCATION_NAME, "Carrum");
         */

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues stop_detailValues = new ContentValues();            
            stop_detailValues.put(StopDetailEntry.COLUMN_LINE_KEY, lineDetailRowId);
            stop_detailValues.put(StopDetailEntry.COLUMN_ROUTE_TYPE, 0);
            stop_detailValues.put(StopDetailEntry.COLUMN_STOP_ID, "102");
            stop_detailValues.put(StopDetailEntry.COLUMN_LOCATION_NAME, "Carrum" + " - " + i);
            stop_detailValues.put(StopDetailEntry.COLUMN_LATITUDE, latitude);
            stop_detailValues.put(StopDetailEntry.COLUMN_LONGITUDE, longitude);
            stop_detailValues.put(StopDetailEntry.COLUMN_FAVORITE, favoriteFlag);
            returnContentValues[i] = stop_detailValues;

            latitude += increase;
            longitude += increase;
        }
        return returnContentValues;
    }

    // This test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.
    @Test
    public void testBulkInsert() {
        Log.v(TAG, "testBulkInsert - start");
        // insert a row into line_detail table
        ContentValues lineDetailetailValues = TestUtilities.createFrankstonLineDetailsValues();

        Uri resultUri = mContext.getContentResolver().insert(
                LineDetailEntry.CONTENT_URI,
                lineDetailetailValues
        );

        long lineDetailRowId = ContentUris.parseId(resultUri);

        // insert into stop_detail
        String favoriteFlag = StopDetailEntry.NON_FAVORITE_FLAG;
        ContentValues[] bulkInsertContentValues = createBulkInsertStopDetailsValues(lineDetailRowId, favoriteFlag);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver stopDetailsObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(StopDetailEntry.CONTENT_URI, true, stopDetailsObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(StopDetailEntry.CONTENT_URI, bulkInsertContentValues);

        Log.v(TAG, "testBulkInsert - insertCount: " + insertCount);
        // If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        stopDetailsObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(stopDetailsObserver);

        assertEquals(BULK_INSERT_RECORDS_TO_INSERT, insertCount);

        // A cursor is your primary interface to the query results.
        Uri uri = StopDetailEntry.buildFavoriteStopsUri(favoriteFlag);
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                StopDetailEntry.COLUMN_LOCATION_NAME + " ASC"  // sort order == by COLUMN_LOCATION_NAME ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating StopDetailsEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    // FIXME: 4/09/2016 comment below is not true - think about it. Really?
    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.
     */
    @Test
    public void testBasicStopDetailsQueries() {
        // insert a row into line_detail table
        ContentValues lineDetailetailValues = TestUtilities.createFrankstonLineDetailsValues();

        Uri resultUri = mContext.getContentResolver().insert(
                LineDetailEntry.CONTENT_URI,
                lineDetailetailValues
        );

        long lineDetailRowId = ContentUris.parseId(resultUri);

        String favoriteFlags = StopDetailEntry.NON_FAVORITE_FLAG;
        ContentValues stopDetailValues = TestUtilities.createFrankstonLineStopDetailsValues(lineDetailRowId, favoriteFlags);
        TestUtilities.insertFrankstonLineStopDetailsValues(lineDetailRowId, mContext, stopDetailValues);

        // Test the basic content provider query
        Uri uri = StopDetailEntry.buildFavoriteStopsUri(favoriteFlags);
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        assertNotNull("Error: cursor can not be null", cursor);
        assertNotEquals("Error: cursor can not be empty", 0, cursor.getCount());
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicStopDetailsQueries, stop_detail query", cursor, stopDetailValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
//        if (Build.VERSION.SDK_INT >= 19) {
//            Assert.assertEquals("Error: Location Query did not properly set NotificationUri",
//                    cursor.getNotificationUri(), StopDetailsEntry.CONTENT_URI);
//        }
    }

    public void deleteAllRecordsFromProvider() {
        // Delete all rows from stop_detail
        mContext.getContentResolver().delete(
                StopDetailEntry.CONTENT_URI,
                null,
                null
        );

        Uri uri = StopDetailEntry.buildFavoriteStopsUri(StopDetailEntry.ANY_FAVORITE_FLAG);
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from stop_detail table during delete", 0, cursor.getCount());
        cursor.close();

        // Delete all rows from line_detail
        mContext.getContentResolver().delete(
                LineDetailEntry.CONTENT_URI,
                null,
                null
        );

        cursor = mContext.getContentResolver().query(
                LineDetailEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from stop_detail table during delete", 0, cursor.getCount());
        cursor.close();
    }
}