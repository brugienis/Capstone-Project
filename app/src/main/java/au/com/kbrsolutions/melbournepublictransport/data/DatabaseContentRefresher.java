package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.ContentProviderOperation;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

import au.com.kbrsolutions.melbournepublictransport.remote.RemoteMptEndpointUtil;

public class DatabaseContentRefresher {

    private final String TAG = ((Object) this).getClass().getSimpleName();

    void refreshDatabase() {
        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        Uri dirUri = MptContract.LineDetailEntry.CONTENT_URI;

        // Delete all items
        cpo.add(ContentProviderOperation.newDelete(dirUri).build());
        boolean databaseOK = RemoteMptEndpointUtil.performHealthCheck();
        Log.v(TAG, "refreshDatabase - databaseOK: " + databaseOK);
    }
}
