package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import au.com.kbrsolutions.melbournepublictransport.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnInitFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class InitFragment extends BaseFragment {

    private boolean mIsTargetSet;
    private int mTarget;

    private OnInitFragmentInteractionListener mListener;

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public InitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
    }

    private ProgressBar mProgressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_init, container, false);
        // FIXME: 2/10/2016 - ProgressBar: http://www.materialdoc.com/linear-progress/
        // FIXME: 2/10/2016  - and         https://material.google.com/components/progress-activity.html#progress-activity-behavior
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
        Log.v(TAG, "onAttach - mListener: " + String.format("0x%08X", mListener.hashCode()));
    }

    public void setListener(OnInitFragmentInteractionListener listener) {
        String old = null;
        if (mListener != null) {
            old = String.format("0x%08X", mListener.hashCode());
        }
        mListener = listener;
        Log.v(TAG, "setListener - new listener set - old/new: " +
                old + "/" +
                String.format("0x%08X", mListener.hashCode()));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setDatabaseLoadTarget(int target) {
        mTarget = target;
    }

    public void updateDatabaseLoadProgress(int progress, int target) {
        Log.v(TAG, "updateDatabaseLoadProgress - progress/mTarget: " + progress + "/" + target);
        if (!mIsTargetSet) {
            mIsTargetSet = true;
            mProgressBar.setIndeterminate(false);
            mProgressBar.setMax(mTarget);
        }
        mProgressBar.setIndeterminate(false);
        mProgressBar.setMax(target);
        mProgressBar.setProgress(progress);
        if (target == progress) {
            Log.v(TAG, "updateDatabaseLoadProgress - listener: " + String.format("0x%08X", mListener.hashCode()));
            mListener.databaseLoaded();
            Log.v(TAG, "updateDatabaseLoadProgress - databaseLoaded");
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
        // TODO: Update argument type and name
        void databaseLoaded();
    }
}
