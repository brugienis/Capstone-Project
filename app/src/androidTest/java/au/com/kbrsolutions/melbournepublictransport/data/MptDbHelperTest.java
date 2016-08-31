package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
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

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testOnCreate() throws Exception {

    }

    @Test
    public void testOnUpgrade() throws Exception {

    }

    @Test
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MptContract.StopDetailsEntry.TABLE_NAME);

        mContext.deleteDatabase(MptDbHelper.DATABASE_NAME);
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

        // if this fails, it means that your database doesn't contain stop_details entry
        Assert.assertTrue("Error: Your database was created without the stop_details table",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MptContract.StopDetailsEntry.TABLE_NAME + ")",
                null);

        Assert.assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> stopDetailsColumnHashSet = new HashSet<String>();
        stopDetailsColumnHashSet.add(MptContract.StopDetailsEntry._ID);
        stopDetailsColumnHashSet.add(MptContract.StopDetailsEntry.COLUMN_LINE_NAME);
        stopDetailsColumnHashSet.add(MptContract.StopDetailsEntry.COLUMN_STOP_NAME);
        stopDetailsColumnHashSet.add(MptContract.StopDetailsEntry.COLUMN_LATITUDE);
        stopDetailsColumnHashSet.add(MptContract.StopDetailsEntry.COLUMN_LONGITUDE);
        stopDetailsColumnHashSet.add(MptContract.StopDetailsEntry.COLUMN_FAVORITE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            stopDetailsColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        Assert.assertTrue("Error: The database doesn't contain all of the required location entry columns",
                stopDetailsColumnHashSet.isEmpty());
        db.close();
        Assert.assertNotEquals("DB should be closed", true, db.isOpen());
    }

//    @Test
//    public void silyTest() {
//        Log.v(TAG, "running Unit Test");
//        Assert.assertTrue("Should be true", false);
//    }
}