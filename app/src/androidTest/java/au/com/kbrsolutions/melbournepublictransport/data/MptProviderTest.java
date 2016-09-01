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
        int deletedRowsCnt = mContext.getContentResolver().delete(
                StopDetailsEntry.CONTENT_URI,
                null,
                null
        );
//        Assert.assertEquals("Error: should delete 0 rows", 0, deletedRowsCnt);

        Uri uri = StopDetailsEntry.buildFavoriteStopsUri(StopDetailsEntry.ANY_FAVORITE_FLAG);
        Cursor cursor = mContext.getContentResolver().query(
//                StopDetailsEntry.CONTENT_URI,
                uri,
                null,
                null,
                null,
                null
        );
//        if (cursor != null) {
        Assert.assertEquals("Error: Records not deleted from stop_details table during delete", 0, cursor.getCount());
        cursor.close();
//        }
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
        ContentValues testValues = TestUtilities.createFrankstonLineStopDetailsValues(StopDetailsEntry.NON_FAVORITE_FLAG);
//        long locationRowId = TestUtilities.insertFrankstonLineStopDetailsValues(mContext);

        Uri resultUri = mContext.getContentResolver().insert(
        StopDetailsEntry.CONTENT_URI,
                testValues
        );

        Log.v(TAG, "testInsert - resultUri: " + resultUri);

        // Test the basic content provider query
//        Uri uri = MptContract.StopDetailsEntry.buildFavoriteStopsUri(StopDetailsEntry.ANY_FAVORITE_FLAG);
        Uri uri = MptContract.StopDetailsEntry.buildFavoriteStopsUri(StopDetailsEntry.NON_FAVORITE_FLAG);
        Cursor cursor = mContext.getContentResolver().query(
//                StopDetailsEntry.CONTENT_URI,
                uri,
                null,
                null,
                null,
                null
        );

        Assert.assertNotNull("Error: cursor can not be null", cursor);
        Assert.assertNotEquals("Error: cursor can not be empty", 0, cursor.getCount());
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testInsert, location query", cursor, testValues);
        testValues = TestUtilities.createFrankstonLineStopDetailsValues(StopDetailsEntry.FAVORITE_FLAG);
//        long locationRowId = TestUtilities.insertFrankstonLineStopDetailsValues(mContext);

        resultUri = mContext.getContentResolver().insert(
        StopDetailsEntry.CONTENT_URI,
                testValues
        );

        Log.v(TAG, "testInsert - resultUri: " + resultUri);

        // Test the basic content provider query
//        Uri uri = MptContract.StopDetailsEntry.buildFavoriteStopsUri(StopDetailsEntry.ANY_FAVORITE_FLAG);
        uri = MptContract.StopDetailsEntry.buildFavoriteStopsUri(StopDetailsEntry.FAVORITE_FLAG);
        cursor = mContext.getContentResolver().query(
//                StopDetailsEntry.CONTENT_URI,
                uri,
                null,
                null,
                null,
                null
        );

        Assert.assertNotNull("Error: cursor can not be null", cursor);
        Assert.assertNotEquals("Error: cursor can not be empty", 0, cursor.getCount());
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testInsert, location query", cursor, testValues);

        uri = MptContract.StopDetailsEntry.buildFavoriteStopsUri(StopDetailsEntry.ANY_FAVORITE_FLAG);
        cursor = mContext.getContentResolver().query(
//                StopDetailsEntry.CONTENT_URI,
                uri,
                null,
                null,
                null,
                null
        );

        Assert.assertNotNull("Error: cursor can not be null", cursor);
        Assert.assertNotEquals("Error: there must be 2 rows", 0, cursor.getCount());
    }

    // FIXME: 1/09/2016 - clarify comments below
    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.

    @Test
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createFrankstonLineStopDetailsValues(StopDetailsEntry.NON_FAVORITE_FLAG);

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(StopDetailsEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(StopDetailsEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        Assert.assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Uri uri = StopDetailsEntry.buildFavoriteStopsUri(StopDetailsEntry.NON_FAVORITE_FLAG);
        Cursor cursor = mContext.getContentResolver().query(
//                StopDetailsEntry.CONTENT_URI,
                uri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.",
                cursor, testValues);
    }

    @Test
    public void testUpdate() throws Exception {

    }

    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.
     */
    @Test
    public void testBasicStopDetailsQueries() {
        ContentValues testValues = TestUtilities.createFrankstonLineStopDetailsValues(StopDetailsEntry.NON_FAVORITE_FLAG);
        long locationRowId = TestUtilities.insertFrankstonLineStopDetailsValues(mContext);

        // Test the basic content provider query
        Cursor cursor = mContext.getContentResolver().query(
                StopDetailsEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        Assert.assertNotNull("Error: cursor can not be null", cursor);
        Assert.assertNotEquals("Error: cursor can not be empty", 0, cursor.getCount());
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicStopDetailsQueries, location query", cursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
//        if (Build.VERSION.SDK_INT >= 19) {
//            Assert.assertEquals("Error: Location Query did not properly set NotificationUri",
//                    cursor.getNotificationUri(), StopDetailsEntry.CONTENT_URI);
//        }
    }

    public void deleteAllRecordsFromProvider() {
        Uri uri = MptContract.StopDetailsEntry.buildFavoriteStopsUri(StopDetailsEntry.ANY_FAVORITE_FLAG);
        int deletedRowsCnt = mContext.getContentResolver().delete(
                StopDetailsEntry.CONTENT_URI,
                null,
                null
        );

        uri = MptContract.StopDetailsEntry.buildFavoriteStopsUri(StopDetailsEntry.ANY_FAVORITE_FLAG);
        Cursor cursor = mContext.getContentResolver().query(
//                StopDetailsEntry.CONTENT_URI,
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