package au.com.kbrsolutions.melbournepublictransport.utilities;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.data.MptContract;
import au.com.kbrsolutions.melbournepublictransport.data.StopsNearbyDetails;
import au.com.kbrsolutions.melbournepublictransport.events.MainActivityEvents;
import au.com.kbrsolutions.melbournepublictransport.events.RequestProcessorServiceRequestEvents;
import au.com.kbrsolutions.melbournepublictransport.remote.RemoteMptEndpointUtil;

public class DatabaseContentRefresher {

    private static final String TAG = DatabaseContentRefresher.class.getSimpleName();

    /**
     * Test progress bar.
     */
    public static void testProgressBar() {
        int cnt = 10;
        for (int i = 0; i < cnt; i++) {
            sendMessageToMainActivity(new MainActivityEvents.Builder(
                    MainActivityEvents.MainEvents.DATABASE_LOAD_PROGRESS)
                    .setDatabaseLoadTarget(cnt - 1)
                    .setDatabaseLoadProgress(i)
                    .build());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadOrRefreshDatabase(ContentResolver contentResolver, Context context) {
        // Delete all rows from stop_detail and line_detail table
        deleteLineAndStopDetailRows(contentResolver);

        int trainMode = StopsNearbyDetails.TRAIN_ROUTE_TYPE;
        List<ContentValues> lineDetailsContentValuesList =
                RemoteMptEndpointUtil.getLineDetails(trainMode, context);
        long locationId;
        int lineNo = 0;
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
                            values.getAsString(MptContract.LineDetailEntry.COLUMN_LINE_ID),
                            context);
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
            if (lineCursor != null) {
                lineCursor.close();
            }
//
            Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri("a");
            Cursor stopCursor = contentResolver.query(
                    uri,
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null  // no sort order
            );
            sendMessageToMainActivity(new MainActivityEvents.Builder(
                    MainActivityEvents.MainEvents.DATABASE_LOAD_PROGRESS)
                    .setDatabaseLoadTarget(lineDetailsContentValuesList.size() - 1)
                    .setDatabaseLoadProgress(lineNo++)
                    .build());
            if (stopCursor != null) {
                stopCursor.close();
            }
        }
//        printLineDetailContent(contentResolver);
//        Log.v(TAG, "loadOrRefreshDatabase - performing refresh action");
    }

    /**
     * Check if database is empty.
     *
     * @param contentResolver
     * @param context
     * @return
     */
    public static boolean isDatabaseEmpty(ContentResolver contentResolver, Context context) {
        Cursor lineCursor = contentResolver.query(
                MptContract.LineDetailEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // no sort order
        );
        int lineDetailsRowsCnt = lineCursor == null ? 0 : lineCursor.getCount();
        if (lineCursor != null) {
            lineCursor.close();
        }
//
        Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri("a");
        Cursor stopCursor = contentResolver.query(
                uri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // no sort order
        );
        int stopDetailsRowsCnt = stopCursor == null ? 0 : stopCursor.getCount();
        if (stopCursor != null) {
            stopCursor.close();
        }

        return (lineDetailsRowsCnt + stopDetailsRowsCnt) == 0;
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

    /**
     * Helper class - use for testing.
     *
     * @param contentResolver
     */
    private static void printLineDetailContent(ContentResolver contentResolver) {
        Cursor cursor = contentResolver.query(
                MptContract.LineDetailEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
            for ( int i = 0; i < cursor.getCount(); i++, cursor.moveToNext() ) {
                Log.v(TAG, "printLineDetailContent - lineId/lineName: " + cursor.getString(0) + "/" + cursor.getString(1));
            }
            cursor.close();
        }
    }

    private static void sendMessageToMainActivity(MainActivityEvents event) {
        EventBus.getDefault().post(event);
    }

    @SuppressWarnings("EmptyMethod")
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(RequestProcessorServiceRequestEvents event) {
    }
}
