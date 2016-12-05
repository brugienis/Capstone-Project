package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.kbrsolutions.melbournepublictransport.R;

/**
 * This class shows information about app.
 */
public class AboutFragment extends BaseFragment {

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_about, container, false);
        return rootView;
    }
}
