package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.support.v4.app.Fragment;
import android.util.Log;

import au.com.kbrsolutions.melbournepublictransport.activities.MainActivity;

/**
 * Every fragment extends this class.
 */
public abstract class BaseFragment extends Fragment {

    private MainActivity.FragmentsId mFragmentsId;
    private String mActionBarTitle;

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public void hideView() {
    }

    public void showView() {
    }

    /**
     * Use for testing - remove before publishing on Google Play.
     */
    public void isRootViewVisible() {
        Log.v(TAG, "isVisible - no code");
    }

    public String getActionBarTitle() {
        return mActionBarTitle;
    }

    public void setActionBarTitle(String actionBarTitle) {
        mActionBarTitle = actionBarTitle;
    }

    public MainActivity.FragmentsId getFragmentId() {
        return  mFragmentsId;
    }
    public void setFragmentId(MainActivity.FragmentsId fragmentId) {
        this.mFragmentsId = fragmentId;
    }

    public boolean handleVerticalDpadKeys(boolean upKeyPressed) {
        throw new RuntimeException(TAG + ".handleVerticalDpadKeys(...) method not implemented");
    }

    public boolean handleHorizontalDpadKeys(boolean rightKeyPressed) {
        throw new RuntimeException(TAG + ".handleHorizontalDpadKeys(...) method not implemented");
    }

}
