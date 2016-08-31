package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.Set;


@RunWith(AndroidJUnit4.class)
//public class TestUtilities extends AndroidTestCase {
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

    static ContentValues createFrankstonLineStopDetailsValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MptContract.StopDetailsEntry.COLUMN_LINE_NAME, "Frankston");
        testValues.put(MptContract.StopDetailsEntry.COLUMN_STOP_NAME, "Carrum");
        testValues.put(MptContract.StopDetailsEntry.COLUMN_LATITUDE, 64.7488);
        testValues.put(MptContract.StopDetailsEntry.COLUMN_LONGITUDE, -147.353);
        testValues.put(MptContract.StopDetailsEntry.COLUMN_FAVORITE, "n");

        return testValues;
    }
    
    static long insertFrankstonLineStopDetailsValues(Context context) {
        // insert our test records into the database
        SQLiteDatabase db = new MptDbHelper(context).getWritableDatabase();
        ContentValues testValues = TestUtilities.createFrankstonLineStopDetailsValues();

        long locationRowId;
        locationRowId = db.insert(MptContract.StopDetailsEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        Assert.assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        Assert.assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

}
