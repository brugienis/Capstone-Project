package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;


@RunWith(AndroidJUnit4.class)
public class MptDbHelperTest {

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MptDbHelper.DATABASE_NAME);
    }

    private final String TAG = ((Object) this).getClass().getSimpleName();

    Context mContext;

    @Before
    public void setUp() throws Exception {
//        mContext = new RenamingDelegatingContext(InstrumentationRegistry.getTargetContext(), "test_");
//        InstrumentationRegistry.getTargetContext();
        mContext = InstrumentationRegistry.getTargetContext();
        deleteTheDatabase();
    }

//    @After
//    public void tearDown() throws Exception {
//
//    }

//    @Test
//    public void testOnCreate() throws Exception {
//
//    }

//    @Test
//    public void testOnUpgrade() throws Exception {
//
//    }

    @Test
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MptContract.StopDetailEntry.TABLE_NAME);

//        mContext.deleteDatabase(MptDbHelper.DATABASE_NAME);
        deleteTheDatabase();
        SQLiteDatabase db = new MptDbHelper(
                this.mContext).getWritableDatabase();
        Assert.assertEquals("DB should be open", true, db.isOpen());
        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        Assert.assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            Log.v(TAG, "testCreateDb - name: " + c.getString(0));
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain stop_detail entry
        Assert.assertTrue("Error: Your database was created without the stop_detail table",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MptContract.StopDetailEntry.TABLE_NAME + ")",
                null);

        Assert.assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> stopDetailsColumnHashSet = new HashSet<String>();
        stopDetailsColumnHashSet.add(MptContract.StopDetailEntry._ID);
        stopDetailsColumnHashSet.add(MptContract.StopDetailEntry.COLUMN_LINE_KEY);
        stopDetailsColumnHashSet.add(MptContract.StopDetailEntry.COLUMN_LOCATION_NAME);
        stopDetailsColumnHashSet.add(MptContract.StopDetailEntry.COLUMN_LATITUDE);
        stopDetailsColumnHashSet.add(MptContract.StopDetailEntry.COLUMN_LONGITUDE);
        stopDetailsColumnHashSet.add(MptContract.StopDetailEntry.COLUMN_FAVORITE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            stopDetailsColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required stop_detail
        // entry columns
        Assert.assertTrue("Error: The database doesn't contain all of the required stop_detail entry columns",
                stopDetailsColumnHashSet.isEmpty());
        db.close();
        Assert.assertNotEquals("DB should be closed", true, db.isOpen());
    }

    // FIXME: 6/09/2016 test below is not working - exception is thrown but does not go into catch block - even when db.setForeignKeyConstraintsEnabled(false);
//    @Test(expected = SQLiteConstraintException.class)
    @Test
    public void testForeignException() {
        SQLiteDatabase db = new MptDbHelper(this.mContext).getWritableDatabase();

        // insert row into stop_detail
        try {
            ContentValues testValues = TestUtilities.createFrankstonLineStopDetailsValues(1234, MptContract.StopDetailEntry.NON_FAVORITE_FLAG);
            db.insert(MptContract.StopDetailEntry.TABLE_NAME, null, testValues);
        } catch (SQLiteConstraintException e) {
            Assert.fail("Should not allow to insert stop_detail when c");
        }
    }

    @Test
    public void testInsertLineAndStopDetails() throws Throwable {
        deleteTheDatabase();
        SQLiteDatabase db = new MptDbHelper(this.mContext).getWritableDatabase();

        // insert row into line_detail
        ContentValues testValues = TestUtilities.createFrankstonLineDetailsValues();
        long line_detailRowId = db.insert(MptContract.LineDetailEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        Assert.assertTrue("row Id cannot be -1", line_detailRowId != -1);
        Cursor cursor = db.query(
                MptContract.LineDetailEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        Assert.assertTrue( "Error: No Records returned from stop_detail query - cnt: " + cursor.getCount(), cursor.moveToFirst() );

        // insert row into stop_detail
        testValues = TestUtilities.createFrankstonLineStopDetailsValues(line_detailRowId, MptContract.StopDetailEntry.NON_FAVORITE_FLAG);
        long stop_detailRowId = db.insert(MptContract.StopDetailEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        Assert.assertTrue(stop_detailRowId != -1);
        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        //Query the database and receive a Cursor back.
        // A cursor is your primary interface to the query results.
        cursor = db.query(
                MptContract.StopDetailEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        Assert.assertTrue( "Error: No Records returned from stop_detail query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        Assert.assertFalse( "Error: More than one record returned from stop_detail query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        Assert.assertNotEquals("DB should be closed", true, db.isOpen());
    }

//    @Test
//    public void silyTest() {
//        Log.v(TAG, "running Unit Test");
//        Assert.assertTrue("Should be true", false);
//    }
}