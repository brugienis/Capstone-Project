package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import au.com.kbrsolutions.melbournepublictransport.R;

/**
 *
 * This class handles the initial load of data into the database.
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnInitFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class InitFragment extends BaseFragment {

    private boolean mIsTargetSet;
    private ProgressBar mProgressBar;

    private OnInitFragmentInteractionListener mListener;

    @SuppressWarnings("unused")
    private final String TAG = ((Object) this).getClass().getSimpleName();

    public InitFragment() {
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

        View rootView =  inflater.inflate(R.layout.fragment_init, container, false);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.loadProgressBar);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnInitFragmentInteractionListener) {
            mListener = (OnInitFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInitFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Update the progress bar. Call parent activity when all data loaded.
     *
     * @param progress
     * @param target
     */
    public void updateDatabaseLoadProgress(int progress, int target) {
        if (!mIsTargetSet) {
            mIsTargetSet = true;
            mProgressBar.setIndeterminate(false);
//            mProgressBar.setMax(mTarget);
        }
        mProgressBar.setIndeterminate(false);
        mProgressBar.setMax(target);
        mProgressBar.setProgress(progress);
        if (target == progress) {
            mListener.databaseLoadFinished();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnInitFragmentInteractionListener {
        void databaseLoadFinished();
    }
}
