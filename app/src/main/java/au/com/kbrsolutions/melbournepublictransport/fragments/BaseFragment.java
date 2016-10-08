package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.support.v4.app.Fragment;
import android.util.Log;

import au.com.kbrsolutions.melbournepublictransport.activities.MainActivity;

/**
 * Created by business on 30/09/2016.
 */

public abstract class BaseFragment extends Fragment {

    private MainActivity.FragmentsId mFragmentsId;

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public void hideView() {

    }

    public void showView() {

    }

    public void isRootViewVisible() {
        Log.v(TAG, "isVisible - no code");
    }

    public MainActivity.FragmentsId getFragmentId() {
        return  mFragmentsId;
    }
    public void setFragmentId(MainActivity.FragmentsId fragmentd) {
        this.mFragmentsId = fragmentd;
//        Log.v(TAG, "setFragmentId - fragmentd: " + fragmentd);
    }

}
