package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.remote.RemoteMptEndpointUtil;

public class DatabaseContentRefresher {

    private static final String TAG = DatabaseContentRefresher.class.getSimpleName();

    static boolean performHealthCheck() {
        boolean databaseOK = RemoteMptEndpointUtil.performHealthCheck();
        return databaseOK;
    }

    static void refreshDatabase(ContentResolver contentResolver, boolean runIfTablesAreEmpty) {
        if (runIfTablesAreEmpty) {
            if (tablesNotEmpty(contentResolver)) {
                return;
            }
        }
        // Delete all rows from stop_detail and line_detail table
        deleteLineAndStopDetailRows(contentResolver);

        int trainMode = 0;
        List<ContentValues> lineDetailsContentValuesList = RemoteMptEndpointUtil.getLineDetails(trainMode);
        long locationId;
        for (ContentValues values: lineDetailsContentValuesList) {
            Uri insertedUri = contentResolver.insert(
                    MptContract.LineDetailEntry.CONTENT_URI,
                    values
            );
            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            locationId = ContentUris.parseId(insertedUri);
            List<ContentValues> stopDetailsContentValuesList =
                    RemoteMptEndpointUtil.getStopDetailsForLine(
                            trainMode,
                            values.getAsString(MptContract.LineDetailEntry.COLUMN_LINE_ID));
            ContentValues[] returnContentValues = new ContentValues[stopDetailsContentValuesList.size()];
            int cnt = 0;
            for (ContentValues contentValues : stopDetailsContentValuesList) {
                // add value of the line_detail._ID to the StopDetailEntry.COLUMN_LINE_KEY
                // to avoid SQLiteConstraintException
                contentValues.put(MptContract.StopDetailEntry.COLUMN_LINE_KEY, locationId);
                returnContentValues[cnt++] = contentValues;
            }
            if (returnContentValues.length > 0) {
                contentResolver.bulkInsert(MptContract.StopDetailEntry.CONTENT_URI, returnContentValues);
            }

            Cursor lineCursor = contentResolver.query(
                    MptContract.LineDetailEntry.CONTENT_URI,
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null  // no sort order
            );
            int lineDetailsRowsCnt = lineCursor.getCount();
            lineCursor.close();
//
            Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri("a");
            Cursor stopCursor = contentResolver.query(
                    uri,
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null  // no sort order
            );
            Log.v(TAG, "refreshDatabase - line_detail/stop_detail cnt: " + lineDetailsRowsCnt + "/" + stopCursor.getCount());
            stopCursor.close();
        }
//        printLineDetailContent(contentResolver);
        Log.v(TAG, "refreshDatabase - performing refresh action");
    }

    private static boolean tablesNotEmpty(ContentResolver contentResolver) {
        Cursor lineCursor = contentResolver.query(
                MptContract.LineDetailEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // no sort order
        );
        int lineDetailsRowsCnt = lineCursor.getCount();
        lineCursor.close();
//
        Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri("a");
        Cursor stopCursor = contentResolver.query(
                uri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // no sort order
        );
        int stopDetailsRowsCnt = stopCursor.getCount();
//        Log.v(TAG, "tablesNotEmpty - line_detail/stop_detail cnt: " + lineDetailsRowsCnt + "/" + stopDetailsRowsCnt);
        stopCursor.close();

        return (lineDetailsRowsCnt + stopDetailsRowsCnt) != 0;
    }

    private static void deleteLineAndStopDetailRows(ContentResolver contentResolver) {
        // First delete all rows from stop_detail table
        contentResolver.delete(
                MptContract.StopDetailEntry.CONTENT_URI,
                null,
                null);

        // and then elete all rows from line_detail table. If you do it in a reverse order you will
        // get Foreign Key problem - SQLiteConstraintException
        contentResolver.delete(
                MptContract.LineDetailEntry.CONTENT_URI,
                null,
                null);

    }

    private static void printLineDetailContent(ContentResolver contentResolver) {
        Cursor cursor = contentResolver.query(
                MptContract.LineDetailEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        for ( int i = 0; i < cursor.getCount(); i++, cursor.moveToNext() ) {
            Log.v(TAG, "printLineDetailContent - lineId/lineName: " + cursor.getString(0) + "/" + cursor.getString(1));
        }
        cursor.close();
    }
}
