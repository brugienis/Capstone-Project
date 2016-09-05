package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.ContentProviderOperation;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.remote.LineDetails;
import au.com.kbrsolutions.melbournepublictransport.remote.RemoteMptEndpointUtil;

public class DatabaseContentRefresher {

    private final String TAG = ((Object) this).getClass().getSimpleName();

    boolean performHealthCheck() {
        boolean databaseOK = RemoteMptEndpointUtil.performHealthCheck();
        return databaseOK;
    }

    void refreshDatabase() {
        ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();

        Uri dirUri = MptContract.LineDetailEntry.CONTENT_URI;

        // Delete all items
        cpo.add(ContentProviderOperation.newDelete(dirUri).build());
        int trainMode = 0;
        List<LineDetails> lineDetailsList = RemoteMptEndpointUtil.getLineDetails(trainMode);
        Log.v(TAG, "refreshDatabase - performing refresh action");
    }
}
