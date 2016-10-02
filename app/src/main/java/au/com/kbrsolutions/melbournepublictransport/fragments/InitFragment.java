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
 * Use the {@link InitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InitFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean mIsTargetSet;
    private int mTarget;

    private OnInitFragmentInteractionListener mListener;

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public InitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InitFragment newInstance(String param1, String param2) {
        InitFragment fragment = new InitFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    private ProgressBar mProgressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_init, container, false);
        // FIXME: 2/10/2016 - ProgressBar: http://www.materialdoc.com/linear-progress/
        // FIXME: 2/10/2016  - and         https://material.google.com/components/progress-activity.html#progress-activity-behavior
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.loadProgressBar);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.databaseLoaded(uri);
//        }
//    }

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

    public void setDatabaseLoadTarget(int target) {
        Log.v(TAG, "setDatabaseLoadTarget - target: " + target);
        mTarget = target;
    }

    public void updateDatabaseLoadProgress(int progress) {
        Log.v(TAG, "updateDatabaseLoadProgress - progress/mIsTargetSet: " + progress + "/" + mIsTargetSet);
        if (!mIsTargetSet) {
            mIsTargetSet = true;
            mProgressBar.setIndeterminate(false);
            mProgressBar.setMax(mTarget);
        }
        mProgressBar.setIndeterminate(false);
        mProgressBar.setProgress(progress);
        if (mTarget == progress) {
            Log.v(TAG, "updateDatabaseLoadProgress - database loaded");
            mListener.databaseLoaded();
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
