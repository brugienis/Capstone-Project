package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.remote.RemoteMptEndpointUtil;

public class DatabaseContentRefresher {

    private static final String TAG = DatabaseContentRefresher.class.getSimpleName();

    static boolean performHealthCheck() {
        boolean databaseOK = RemoteMptEndpointUtil.performHealthCheck();
        return databaseOK;
    }

    static void refreshDatabase(ContentResolver contentResolver) {
        // Delete all rows from stop_detail table
        contentResolver.delete(
                MptContract.StopDetailEntry.CONTENT_URI,
                null,
                null);

        // Delete all rows from line_detail table
        contentResolver.delete(
                MptContract.LineDetailEntry.CONTENT_URI,
                null,
                null);

//        Uri dirUri = MptContract.LineDetailEntry.CONTENT_URI;

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
            List<ContentValues> stopDetailsContentValuesList = RemoteMptEndpointUtil.getStopDetailsForLine(trainMode, values.getAsString(MptContract.LineDetailEntry.COLUMN_LINE_ID));
            ContentValues[] returnContentValues = new ContentValues[stopDetailsContentValuesList.size()];
            int cnt = 0;
            for (ContentValues contentValues : stopDetailsContentValuesList) {
                contentValues.put(MptContract.StopDetailEntry.COLUMN_LINE_KEY, locationId);
                returnContentValues[cnt++] = contentValues;
            }
            if (returnContentValues.length > 0) {
                contentResolver.bulkInsert(MptContract.StopDetailEntry.CONTENT_URI, returnContentValues);
            }

            Cursor cursor = contentResolver.query(
                    MptContract.LineDetailEntry.CONTENT_URI,
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null
            );
            Log.v(TAG, "refreshDatabase - line_detail cnt: " + cursor.getCount());
            cursor.close();

            Uri uri = MptContract.StopDetailEntry.buildFavoriteStopsUri("a");
            cursor = contentResolver.query(
                    uri,
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null
            );
            Log.v(TAG, "refreshDatabase - stop_detail cnt: " + cursor.getCount());
            cursor.close();
        }

//        try {
//            contentResolver.applyBatch(MptContract.CONTENT_AUTHORITY, cpo);
            printLineDetailContent(contentResolver);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        } catch (OperationApplicationException e) {
//            // FIXME: 6/09/2016 handle exception
//            e.printStackTrace();
//        }
        Log.v(TAG, "refreshDatabase - performing refresh action");
    }

    static void refreshDatabaseNotGoodSolution(ContentResolver contentResolver) {
//        we need the line_detail key before we can insert related stop_detail rows
        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        Uri dirUri = MptContract.LineDetailEntry.CONTENT_URI;

        // Delete all items
        cpo.add(ContentProviderOperation.newDelete(dirUri).build());
        int trainMode = 0;
//        List<LineDetails> lineDetailsList = RemoteMptEndpointUtil.getLineDetails(trainMode);
        int cnt = 0;
//        for (LineDetails lineDetails: lineDetailsList) {
//            lineDetails.toString();
//            ContentValues values = new ContentValues();
//            values.put(MptContract.LineDetailEntry.COLUMN_ROUTE_TYPE, lineDetails.routeType);
//            values.put(MptContract.LineDetailEntry.COLUMN_LINE_ID, lineDetails.lineId);
//            values.put(MptContract.LineDetailEntry.COLUMN_LINE_NAME, lineDetails.lineName);
//            values.put(MptContract.LineDetailEntry.COLUMN_LINE_NAME_SHORT, lineDetails.lineNameShort);
//            cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
//            cnt++;
//        }

        try {
            contentResolver.applyBatch(MptContract.CONTENT_AUTHORITY, cpo);
            printLineDetailContent(contentResolver);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // FIXME: 6/09/2016 handle exception
            e.printStackTrace();
        }
        Log.v(TAG, "refreshDatabase - performing refresh action - inserted: " + cnt);
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
            Log.v(TAG, "printLineDetailContent - lineId/lineName: " + cursor.getString(0) + "/" + cursor.getString(0));
        }
        cursor.close();
    }
}
