package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.remote.LineDetails;
import au.com.kbrsolutions.melbournepublictransport.remote.RemoteMptEndpointUtil;

public class DatabaseContentRefresher {

    private static final String TAG = DatabaseContentRefresher.class.getSimpleName();

    static boolean performHealthCheck() {
        boolean databaseOK = RemoteMptEndpointUtil.performHealthCheck();
        return databaseOK;
    }

    static void refreshDatabase(ContentResolver contentResolver) {
        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        Uri dirUri = MptContract.LineDetailEntry.CONTENT_URI;

        // Delete all items
        cpo.add(ContentProviderOperation.newDelete(dirUri).build());
        int trainMode = 0;
        List<LineDetails> lineDetailsList = RemoteMptEndpointUtil.getLineDetails(trainMode);
        int cnt = 0;
        for (LineDetails lineDetails: lineDetailsList) {
            lineDetails.toString();
            ContentValues values = new ContentValues();
            values.put(MptContract.LineDetailEntry.COLUMN_ROUTE_TYPE, lineDetails.routeType);
            values.put(MptContract.LineDetailEntry.COLUMN_LINE_ID, lineDetails.lineId);
            values.put(MptContract.LineDetailEntry.COLUMN_LINE_NAME, lineDetails.lineName);
            values.put(MptContract.LineDetailEntry.COLUMN_LINE_NAME_SHORT, lineDetails.lineNameShort);
            cpo.add(ContentProviderOperation.newInsert(dirUri).withValues(values).build());
            cnt++;
        }

        try {
            contentResolver.applyBatch(MptContract.CONTENT_AUTHORITY, cpo);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // FIXME: 6/09/2016 handle exception
            e.printStackTrace();
        }
        Log.v(TAG, "refreshDatabase - performing refresh action - inserted: " + cnt);
    }
}
