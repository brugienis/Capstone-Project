package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.adapters.NextDeparturesAdapter;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.data.RequestProcessorService;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class NextDeparturesFragment extends BaseFragment {

    private static final String ARG_SELECTED_STOP_NAME = "arg_selected_stop_name";
    private static final String ARG_NEXT_DEPARTURE_DATA = "next_departure_data";
    private List<NextDepartureDetails> mNextDepartureDetailsList;
    private NextDeparturesAdapter mRecyclerViewAdapter;
    private String mSelectedStopName;
    private StopDetails mSearchStopDetails;
    private TextView selectedStopNameTv;
    private boolean newInstanceArgsRetrieved;

    private static final String TAG = NextDeparturesFragment.class.getSimpleName();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NextDeparturesFragment() {
    }

    public static NextDeparturesFragment newInstance(
            String stopName,
            List<NextDepartureDetails> nextDepartureDetailsList,
            StopDetails stopDetails) {
        NextDeparturesFragment fragment = new NextDeparturesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SELECTED_STOP_NAME, stopName);
        args.putParcelableArrayList(ARG_NEXT_DEPARTURE_DATA, (ArrayList)nextDepartureDetailsList);
        Bundle mBundle = new Bundle();
        mBundle.putParcelable(RequestProcessorService.STOP_DETAILS, stopDetails);
        args.putAll(mBundle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && !newInstanceArgsRetrieved) {
            mNextDepartureDetailsList = (ArrayList)getArguments().getParcelableArrayList(ARG_NEXT_DEPARTURE_DATA);
            mSelectedStopName = getArguments().getString(ARG_SELECTED_STOP_NAME);
            mSearchStopDetails = getArguments().getParcelable(RequestProcessorService.STOP_DETAILS);
//            Log.v(TAG, "onCreate - stopDetails: " + mSearchStopDetails);
            newInstanceArgsRetrieved = true;
        }
        setRetainInstance(true);
    }

    public StopDetails  getSearchStopDetails() {
        return mSearchStopDetails;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_next_departure, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.disruptionsList);

        selectedStopNameTv = (TextView) view.findViewById(R.id.selectedStopName);
        selectedStopNameTv.setText(mSelectedStopName);

        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerViewAdapter = new NextDeparturesAdapter(mNextDepartureDetailsList);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.requestLayout();
        return view;
    }

    public void setNewContent(
            String selectedStopName,
            List<NextDepartureDetails> nextDepartureDetailsList,
            StopDetails stopDetails) {
        mSelectedStopName = selectedStopName;
        selectedStopNameTv.setText(selectedStopName);
        mRecyclerViewAdapter.swap(nextDepartureDetailsList);
        mSearchStopDetails = stopDetails;
    }

    @Override
    public void hideView() {

    }

    @Override
    public void showView() {

    }
}
