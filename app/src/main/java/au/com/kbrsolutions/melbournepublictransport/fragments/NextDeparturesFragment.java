package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.adapters.NextDepartureAdapter;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.fragments.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnItemFragmentInteractionListener}
 * interface.
 */
public class NextDeparturesFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_SELECTED_STOP_NAME = "arg_selected_stop_name";
    private static final String ARG_NEXT_DEPARTURE_DATA = "next_departure_data";
    private List<NextDepartureDetails> mNextDepartureDetailsList;
    private OnItemFragmentInteractionListener mListener;
    private NextDepartureAdapter mRecyclerViewAdapter;
    private String mSelectedStopName;
    private TextView selectedStopNameTv;
    private boolean newInstanceArgsRetrieved;

    private static final String TAG = StopDetailAdapter.class.getSimpleName();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NextDeparturesFragment() {
    }

    public static NextDeparturesFragment newInstance(String stopName, List<NextDepartureDetails> nextDepartureDetailsList) {
        Log.v(TAG, "newInstance");
        NextDeparturesFragment fragment = new NextDeparturesFragment();
        Bundle args = new Bundle();;
        args.putString(ARG_SELECTED_STOP_NAME, stopName);
        args.putParcelableArrayList(ARG_NEXT_DEPARTURE_DATA, (ArrayList)nextDepartureDetailsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && !newInstanceArgsRetrieved) {
            mNextDepartureDetailsList = (ArrayList)getArguments().getParcelableArrayList(ARG_NEXT_DEPARTURE_DATA);
            mSelectedStopName = getArguments().getString(ARG_SELECTED_STOP_NAME);
            Log.v(TAG, "onCreate - selectedStopName/tv: " + mSelectedStopName);
            newInstanceArgsRetrieved = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_next_departure, container, false);
        View viewR = view.findViewById(R.id.list);
//        if (selectedStopNameTv == null) {
            selectedStopNameTv = (TextView) view.findViewById(R.id.selectedStopName);
//        }
        selectedStopNameTv.setText(mSelectedStopName);
        Log.v(TAG, "onCreateView - selectedStopName/tv: " + selectedStopNameTv.hashCode() + "/" + mSelectedStopName + "/" + selectedStopNameTv.getText());

        // Set the adapter
        if (viewR instanceof RecyclerView) {
            Context context = viewR.getContext();
            RecyclerView recyclerView = (RecyclerView) viewR;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerViewAdapter = new NextDepartureAdapter(mNextDepartureDetailsList, mListener);
            recyclerView.setAdapter(mRecyclerViewAdapter);
            recyclerView.requestLayout();
        }
        return view;
    }

    public void setNewContent(String selectedStopName, List<NextDepartureDetails> nextDepartureDetailsList) {
        mSelectedStopName = selectedStopName;
        selectedStopNameTv.setText(selectedStopName);
        Log.v(TAG, "setNewContent - selectedStopName/tv: " + selectedStopNameTv.hashCode() + "/" + selectedStopName + "/" + selectedStopNameTv.getText());
        mRecyclerViewAdapter.swap(nextDepartureDetailsList);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemFragmentInteractionListener) {
            mListener = (OnItemFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnItemFragmentInteractionListener {
        // TODO: Update argument type and name
        void onItemFragmentInteractionListener(DummyItem item);
    }
}
