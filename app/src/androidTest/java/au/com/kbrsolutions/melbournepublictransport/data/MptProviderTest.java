package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import au.com.kbrsolutions.melbournepublictransport.data.MptContract.StopDetailsEntry;


@RunWith(AndroidJUnit4.class)
public class MptProviderTest {

    private final String TAG = ((Object) this).getClass().getSimpleName();

    Context mContext;

    @Before
    public void setUp() throws Exception {
        mContext = InstrumentationRegistry.getTargetContext();
        deleteAllRecordsFromProvider();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDelete() throws Exception {
        // insert 2 rows
        ContentValues weatherValues = TestUtilities.createFrankstonLineStopDetailsValues(StopDetailsEntry.NON_FAVORITE_FLAG);

        mContext.getContentResolver().insert(
                StopDetailsEntry.CONTENT_URI,
                weatherValues
        );

        weatherValues = TestUtilities.createFrankstonLineStopDetailsValues(StopDetailsEntry.FAVORITE_FLAG);

        mContext.getContentResolver().insert(
                StopDetailsEntry.CONTENT_URI,
                weatherValues
        );

        // delete all (2) rows

        // Register a content observer for our location delete.
        TestUtilities.TestContentObserver stopDetails = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(StopDetailsEntry.CONTENT_URI, true, stopDetails);

        Uri uri = StopDetailsEntry.buildFavoriteStopsUri(StopDetailsEntry.ANY_FAVORITE_FLAG);
        int deletedRowsCnt = mContext.getContentResolver().delete(
                uri,
                null,
                null
        );
        Assert.assertEquals("Error: should delete 2 rows", 2, deletedRowsCnt);

        // If either of these fail, most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        stopDetails.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(stopDetails);

        // verify both rows deleted
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );
        Assert.assertEquals("Error: Records not deleted from stop_details table during delete", 0, cursor.getCount());
        cursor.close();
    }

    @Test
    public void testBuildUriMatcher() throws Exception {

    }

    @Test
    public void testOnCreate() throws Exception {

    }

    @Test
    public void testQuery() throws Exception {

    }

    @Test
    public void testGetType() throws Exception {

    }

    @Test
    public void testInsert() throws Exception {
        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(StopDetailsEntry.CONTENT_URI, true, tco);
        
        ContentValues weatherValues = TestUtilities.createFrankstonLineStopDetailsValues(StopDetailsEntry.NON_FAVORITE_FLAG);

        Uri resultUri = mContext.getContentResolver().insert(
        StopDetailsEntry.CONTENT_URI,
                weatherValues
        );

        // Did our content observer get called?  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

//        Log.v(TAG, "testInsert - resultUri: " + resultUri);

        // Test the basic content provider query
        Uri uri = MptContract.StopDetailsEntry.buildFavoriteStopsUri(StopDetailsEntry.NON_FAVORITE_FLAG);
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        Assert.assertNotNull("Error: cursor can not be null", cursor);
        Assert.assertNotEquals("Error: cursor can not be empty", 0, cursor.getCount());
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testInsert, location query", cursor, weatherValues);
        weatherValues = TestUtilities.createFrankstonLineStopDetailsValues(StopDetailsEntry.FAVORITE_FLAG);

        resultUri = mContext.getContentResolver().insert(
        StopDetailsEntry.CONTENT_URI,
                weatherValues
        );

//        Log.v(TAG, "testInsert - resultUri: " + resultUri);

        // Test the basic content provider query
        uri = MptContract.StopDetailsEntry.buildFavoriteStopsUri(StopDetailsEntry.FAVORITE_FLAG);
        cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        Assert.assertNotNull("Error: cursor can not be null", cursor);
        Assert.assertNotEquals("Error: cursor can not be empty", 0, cursor.getCount());
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testInsert, location query", cursor, weatherValues);

        // Verify there are 2 rows in the DB
        uri = MptContract.StopDetailsEntry.buildFavoriteStopsUri(StopDetailsEntry.ANY_FAVORITE_FLAG);
        cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        Assert.assertNotNull("Error: cursor can not be null", cursor);
        Assert.assertNotEquals("Error: there must be 2 rows", 0, cursor.getCount());
    }

    @Test
    public void testInsertReadProvider() {
        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(StopDetailsEntry.CONTENT_URI, true, tco);

        // Insert a row
        String favoriteFlag = StopDetailsEntry.NON_FAVORITE_FLAG;
        ContentValues weatherValues = TestUtilities.createFrankstonLineStopDetailsValues(favoriteFlag);
        Uri stopDetailsUri = mContext.getContentResolver().insert(StopDetailsEntry.CONTENT_URI, weatherValues);

        // Did our content observer get called?  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long stopDetailsRowId = ContentUris.parseId(stopDetailsUri);

        // Verify we got a row back.
        Assert.assertTrue(stopDetailsRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is a primary interface to the query results.
        Uri uri = StopDetailsEntry.buildFavoriteStopsUri(favoriteFlag);
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating StopDetailsEntry.",
                cursor, weatherValues);
    }

    @Test
    public void testUpdate() throws Exception {
        String favoriteFlag = StopDetailsEntry.FAVORITE_FLAG;
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createFrankstonLineStopDetailsValues(favoriteFlag);

        Uri locationUri = mContext.getContentResolver().
                insert(StopDetailsEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        Assert.assertTrue("Error: row was not inserted", locationRowId != -1);
        Log.d(TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(StopDetailsEntry._ID, locationRowId);
        updatedValues.put(StopDetailsEntry.COLUMN_STOP_NAME, "Carrum");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Uri uri = MptContract.StopDetailsEntry.buildFavoriteStopsUri(favoriteFlag);
        Cursor stopDetailsCursor = mContext.getContentResolver().query(uri, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        stopDetailsCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                StopDetailsEntry.CONTENT_URI, updatedValues, StopDetailsEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        Assert.assertEquals("Error: no row was updated", 1, count);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // If the code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        stopDetailsCursor.unregisterContentObserver(tco);
        stopDetailsCursor.close();

        // A cursor is your primary interface to the query results.
        uri = StopDetailsEntry.buildFavoriteStopsUri(favoriteFlag);
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,   // projection
                StopDetailsEntry._ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateLocation.  Error validating location entry update.",
                cursor, updatedValues);

        cursor.close();
    }
    
    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;

    static ContentValues[] createBulkInsertStopDetailsValues(String favoriteFlag) {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues weatherValues = new ContentValues();            
            weatherValues.put(StopDetailsEntry.COLUMN_LINE_NAME, "Frankston");
            weatherValues.put(StopDetailsEntry.COLUMN_STOP_NAME, "Carrum" + " - " + i);
            weatherValues.put(StopDetailsEntry.COLUMN_LATITUDE, 64.7488);
            weatherValues.put(StopDetailsEntry.COLUMN_LONGITUDE, -147.353);
            weatherValues.put(StopDetailsEntry.COLUMN_FAVORITE, favoriteFlag);
            returnContentValues[i] = weatherValues;
        }
        return returnContentValues;
    }

    // This test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.
    @Test
    public void testBulkInsert() {
        Log.v(TAG, "testBulkInsert - start");
        String favoriteFlag = StopDetailsEntry.NON_FAVORITE_FLAG;
        ContentValues[] bulkInsertContentValues = createBulkInsertStopDetailsValues(favoriteFlag);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver stopDetailsObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(StopDetailsEntry.CONTENT_URI, true, stopDetailsObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(StopDetailsEntry.CONTENT_URI, bulkInsertContentValues);

        Log.v(TAG, "testBulkInsert - insertCount: " + insertCount);
        // If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        stopDetailsObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(stopDetailsObserver);

        Assert.assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Uri uri = StopDetailsEntry.buildFavoriteStopsUri(favoriteFlag);
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                StopDetailsEntry.COLUMN_STOP_NAME + " ASC"  // sort order == by COLUMN_STOP_NAME ASCENDING
        );

        // we should have as many records in the database as we've inserted
        Assert.assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating StopDetailsEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.
     */
    @Test
    public void testBasicStopDetailsQueries() {
        String favoriteFlags = StopDetailsEntry.NON_FAVORITE_FLAG;
        ContentValues weatherValues = TestUtilities.createFrankstonLineStopDetailsValues(favoriteFlags);
        TestUtilities.insertFrankstonLineStopDetailsValues(mContext);

        // Test the basic content provider query
        Uri uri = MptContract.StopDetailsEntry.buildFavoriteStopsUri(favoriteFlags);
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        Assert.assertNotNull("Error: cursor can not be null", cursor);
        Assert.assertNotEquals("Error: cursor can not be empty", 0, cursor.getCount());
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicStopDetailsQueries, location query", cursor, weatherValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
//        if (Build.VERSION.SDK_INT >= 19) {
//            Assert.assertEquals("Error: Location Query did not properly set NotificationUri",
//                    cursor.getNotificationUri(), StopDetailsEntry.CONTENT_URI);
//        }
    }

    public void deleteAllRecordsFromProvider() {
        // Delete all rows
        mContext.getContentResolver().delete(
                StopDetailsEntry.CONTENT_URI,
                null,
                null
        );

        Uri uri = MptContract.StopDetailsEntry.buildFavoriteStopsUri(StopDetailsEntry.ANY_FAVORITE_FLAG);
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );
        Assert.assertEquals("Error: Records not deleted from stop_details table during delete", 0, cursor.getCount());
        cursor.close();
    }
}