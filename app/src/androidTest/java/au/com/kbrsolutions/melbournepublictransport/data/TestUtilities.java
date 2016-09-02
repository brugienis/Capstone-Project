package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.Set;

import au.com.kbrsolutions.melbournepublictransport.utils.PollingCheck;


@RunWith(AndroidJUnit4.class)
public class TestUtilities {

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            Assert.assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            Assert.assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createFrankstonLineStopDetailsValues(String favoriteFlag) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MptContract.StopDetailEntry.COLUMN_LINE_NAME, "Frankston");
        testValues.put(MptContract.StopDetailEntry.COLUMN_STOP_NAME, "Carrum");
        testValues.put(MptContract.StopDetailEntry.COLUMN_LATITUDE, 64.7488);
        testValues.put(MptContract.StopDetailEntry.COLUMN_LONGITUDE, -147.353);
        testValues.put(MptContract.StopDetailEntry.COLUMN_FAVORITE, favoriteFlag);

        return testValues;
    }
    
    static long insertFrankstonLineStopDetailsValues(Context context) {
        // insert our test records into the database
        SQLiteDatabase db = new MptDbHelper(context).getWritableDatabase();
        ContentValues testValues = TestUtilities.createFrankstonLineStopDetailsValues(MptContract.StopDetailEntry.NON_FAVORITE_FLAG);

        long stop_detailRowId;
        stop_detailRowId = db.insert(MptContract.StopDetailEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        Assert.assertTrue("Error: Failure to insert Frankston StopDetails Values", stop_detailRowId != -1);

        db.close();

        return stop_detailRowId;
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        Assert.assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    /*
        The functions inside of TestProvider use this utility class to test the ContentObserver
        callbacks using the PollingCheck class taken from the Android CTS tests.

        This only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

}
