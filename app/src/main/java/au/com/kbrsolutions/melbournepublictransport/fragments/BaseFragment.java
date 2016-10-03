package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.support.v4.app.Fragment;

import au.com.kbrsolutions.melbournepublictransport.activities.MainActivity;

/**
 * Created by business on 30/09/2016.
 */

public abstract class BaseFragment extends Fragment {

    private MainActivity.FragmentsId mFragmentsId;

    public void hideView() {

    }

    public void showView() {

    }

    public MainActivity.FragmentsId getFragmentId() {
        return  mFragmentsId;
    }
    public void setFragmentId(MainActivity.FragmentsId fragmentd) {
        this.mFragmentsId = fragmentd;
    }

}
