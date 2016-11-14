package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.adapters.NextDeparturesAdapter;
import au.com.kbrsolutions.melbournepublictransport.adapters.NextDeparturesAdapterRv;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.data.RequestProcessorService;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.utilities.HorizontalDividerItemDecoration;
import au.com.kbrsolutions.melbournepublictransport.utilities.Utility;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class NextDeparturesFragment extends BaseFragment
        implements ViewTreeObserver.OnGlobalFocusChangeListener {

    private static final String ARG_SELECTED_STOP_NAME = "arg_selected_stop_name";
    private static final String ARG_NEXT_DEPARTURE_DATA = "next_departure_data";
    private List<NextDepartureDetails> mNextDepartureDetailsList;
    private NestedScrollingListView mListView;
    private TextView mEmptyView;
    private NextDeparturesAdapterRv mRecyclerViewAdapter;
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

    public StopDetails getSearchStopDetails() {
        return mSearchStopDetails;
    }

    private NextDeparturesAdapter mNextDeparturesAdapter;
//    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_next_departure, container, false);

        mNextDeparturesAdapter = new NextDeparturesAdapter(getActivity(), mNextDepartureDetailsList);
        mListView = (NestedScrollingListView) rootView.findViewById(R.id.nextDeparturesList);
        mListView.setAdapter(mNextDeparturesAdapter);
        mEmptyView = (TextView) rootView.findViewById(R.id.emptyView);
        mEmptyView = (TextView) rootView.findViewById(R.id.emptyView);
        mEmptyView.setText(getActivity().getResources()
                .getString(R.string.no_favorite_stops_selected));

        mNextDepartureDetailsCnt = mNextDepartureDetailsList.size();

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v(TAG, "onItemSelected - position/view: " + position + "/" + Utility.getClassHashCode(view));
                handleItemSelected(view, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.v(TAG, "onNothingSelected - position: " + parent);
            }
        });

        selectedStopNameTv = (TextView) rootView.findViewById(R.id.selectedStopName);
        selectedStopNameTv.setText(mSelectedStopName);

        return rootView;
    }

    private int mNextDepartureDetailsCnt;
    private View mCurrentSelectedView;
    private int mCurrentSelectedRow;
    private int selectableViewsCnt = 0;
    private int selectedViewNo = -1;

    private void handleItemSelected(View view, int position) {
        if (view.getTag() != null) {
            mCurrentSelectedView = view;
            mCurrentSelectedRow = position;
            selectedViewNo = 0;
            NextDeparturesAdapter.ViewHolder holder = (NextDeparturesAdapter.ViewHolder) mCurrentSelectedView.getTag();
            mListView.clearFocus();
            holder.departureTimeId.setFocusable(true);
            holder.departureTimeId.requestFocus();
            Log.v(TAG, "handleItemSelected - departureTimeId in focus");
        }
    }

    public boolean handleVerticalDpadKeys(boolean upKeyPressed) {
        Log.v(TAG, "handleVerticalDpadKeys - start - mCurrentSelectedRow/mNextDepartureDetailsCnt: " + mCurrentSelectedRow + "/" + mNextDepartureDetailsCnt);
        if (upKeyPressed && mCurrentSelectedRow == 0 ||
                !upKeyPressed && mCurrentSelectedRow == mNextDepartureDetailsCnt - 1) {
            mCurrentSelectedRow = -1;
            mCurrentSelectedView = null;
            Log.v(TAG, "handleVerticalDpadKeys - moved above 1st. row");
        }
        Log.v(TAG, "handleVerticalDpadKeys - end - mCurrentSelectedRow/mNextDepartureDetailsCnt: " + mCurrentSelectedRow + "/" + mNextDepartureDetailsCnt);
        return false;
    }

    /**
     *
     * Based on henry74918
     *
     *  http://stackoverflow.com/questions/14392356/how-to-use-d-pad-navigate-switch-between-listviews-row-and-its-decendants-goo
     *
     * @param rightKeyPressed
     * @return
     */
    public boolean handleHorizontalDpadKeys(boolean rightKeyPressed) {
        Log.v(TAG, "handleHorizontalDpadKeys - start - mCurrentSelectedView: " + mCurrentSelectedView);
        return false;
    }

    // https://www.bignerdranch.com/blog/recyclerview-part-1-fundamentals-for-listview-experts/
    // https://github.com/vganin/dpad-aware-recycler-view/blob/master/lib/src/main/java/net/ganin/darv/DpadAwareRecyclerView.java
//    @Override
    public View onCreateView_UseWithRececleViewAdapter(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_next_departure_rv, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.disruptionsList);

//        recyclerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view,
//                                       int position, long id) {
//                Log.v(TAG, "onItemSelected - position/view: " + position + "/" + Utility.getClassHashCode(view));
//                handleItemSelected(view, position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                Log.v(TAG, "onNothingSelected - position: " + parent);
//            }
//        });

        selectedStopNameTv = (TextView) view.findViewById(R.id.selectedStopName);
        selectedStopNameTv.setText(mSelectedStopName);

        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        Drawable divider = getResources().getDrawable(R.drawable.item_divider);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration(divider));
        mRecyclerViewAdapter = new NextDeparturesAdapterRv(mNextDepartureDetailsList);
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
//        mRecyclerViewAdapter.swapNextDeparturesDetails(nextDepartureDetailsList);
        Log.v(TAG, "setNewContent - size: " + nextDepartureDetailsList.size());
        swapNextDeparturesDetails(nextDepartureDetailsList);
        mSearchStopDetails = stopDetails;
    }


    /**
     * Show departures details or show empty view if no data found.
     */
    private void swapNextDeparturesDetails(List<NextDepartureDetails> nextDepartureDetailsList) {
        mNextDepartureDetailsCnt = nextDepartureDetailsList.size();
        int cnt0 = mNextDeparturesAdapter.getCount();
        mNextDeparturesAdapter.clear();
        int cnt1 = mNextDeparturesAdapter.getCount();
        Log.v(TAG, "swapNextDeparturesDetails - cnt0/cnt1: " + cnt0 + "/" + cnt1);
        if (mNextDepartureDetailsList != null) {
            mNextDeparturesAdapter.addAll(nextDepartureDetailsList);
            Log.v(TAG, "swapNextDeparturesDetails - added rows cnt: " + nextDepartureDetailsList.size());
//            mListView.clearChoices();    /* will clear previously selected artist row */
            int cntB = mNextDeparturesAdapter.getCount();
            mNextDeparturesAdapter.notifyDataSetChanged(); /* call after clearChoices above */
            int cntA = mNextDeparturesAdapter.getCount();
            Log.v(TAG, "swapNextDeparturesDetails - cntB/cntA: " + cntA + "/" + cntA);
//            mNextDeparturesAdapter.
//            mListView.setSelection(0);
            if (mNextDeparturesAdapter.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
//                mEmptyView.setText();
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void hideView() {

    }

    @Override
    public void showView() {

    }

    @Override
    public void onGlobalFocusChanged(View view, View view1) {

    }

    AdapterView.OnItemSelectedListener mOnItemSelectedListener;

}
