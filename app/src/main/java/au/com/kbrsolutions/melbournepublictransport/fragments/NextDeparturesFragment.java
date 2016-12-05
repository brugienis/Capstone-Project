package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.adapters.NextDeparturesAdapter;
import au.com.kbrsolutions.melbournepublictransport.adapters.NextDeparturesAdapterRv;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.remote.RequestProcessorService;
import au.com.kbrsolutions.melbournepublictransport.utilities.HorizontalDividerItemDecoration;

/**
 * A fragment representing a list of NextDepartureDetails.
 *
 * <p/>
 */
public class NextDeparturesFragment extends BaseFragment {
//        implements ViewTreeObserver.OnGlobalFocusChangeListener {

    private static final String ARG_SELECTED_STOP_NAME = "arg_selected_stop_name";
    private static final String ARG_NEXT_DEPARTURE_DATA = "next_departure_data";
    private List<NextDepartureDetails> mNextDepartureDetailsList;
    private NestedScrollingListView mListView;
    private String mSelectedStopName;
    private StopDetails mSearchStopDetails;
    private TextView selectedStopNameTv;
    private boolean newInstanceArgsRetrieved;
    private int mNextDepartureDetailsCnt;
    private View mCurrentSelectedView;
    private int mCurrentSelectedRow;

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
        TextView emptyView = (TextView) rootView.findViewById(R.id.emptyView);
        emptyView.setText(getActivity().getResources()
                .getString(R.string.no_favorite_stops_selected));

        mNextDepartureDetailsCnt = mNextDepartureDetailsList.size();

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                handleItemSelected(view, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        selectedStopNameTv = (TextView) rootView.findViewById(R.id.selectedStopName);
        selectedStopNameTv.setText(mSelectedStopName);

        return rootView;
    }

    /**
     * This is called when D-pad navigation keys are used.
     *
     * @param view
     * @param position
     */
    private void handleItemSelected(View view, int position) {
        if (view.getTag() != null) {
            mCurrentSelectedView = view;
            mCurrentSelectedRow = position;
            NextDeparturesAdapter.ViewHolder holder = (NextDeparturesAdapter.ViewHolder) mCurrentSelectedView.getTag();
            mListView.clearFocus();
            holder.departureTimeId.setFocusable(true);
            holder.departureTimeId.requestFocus();
        }
    }

    public boolean handleVerticalDpadKeys(boolean upKeyPressed) {
        if (upKeyPressed && mCurrentSelectedRow == 0 ||
                !upKeyPressed && mCurrentSelectedRow == mNextDepartureDetailsCnt - 1) {
            mCurrentSelectedRow = -1;
            mCurrentSelectedView = null;
        }
        return false;
    }

    /**
     *
     * Ignore horizontal scrolling.
     */
    public boolean handleHorizontalDpadKeys(boolean rightKeyPressed) {
        return false;
    }

    /**
     *
     * This onCreateView is using RecyclerView.Adapter.
     *
     * Use this method in the future when you have more information about how to handle D-pad
     * navigation.
     *
     * see:
     *      https://github.com/vganin/dpad-aware-recycler-view/blob/master/app/src/main/java/net/ganin/darv/sample/SampleActivity.java
     *      https://www.bignerdranch.com/blog/recyclerview-part-1-fundamentals-for-listview-experts/
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */


    /**
     *
     * This onCreateView is using RecyclerView.Adapter. NOT IN USE now.
     *
     * Use this method in the future when you have more information about how to handle D-pad
     * navigation.
     *
     * see:
     *      https://github.com/vganin/dpad-aware-recycler-view/blob/master/app/src/main/java/net/ganin/darv/sample/SampleActivity.java
     *      https://www.bignerdranch.com/blog/recyclerview-part-1-fundamentals-for-listview-experts/
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
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
        NextDeparturesAdapterRv recyclerViewAdapter = new NextDeparturesAdapterRv(mNextDepartureDetailsList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.requestLayout();
        return view;
    }

    /**
     *
     * Show the latest departure details.
     *
     * @param selectedStopName
     * @param nextDepartureDetailsList
     * @param stopDetails
     */
    public void setNewContent(
            String selectedStopName,
            List<NextDepartureDetails> nextDepartureDetailsList,
            StopDetails stopDetails) {
        mSelectedStopName = selectedStopName;
        selectedStopNameTv.setText(selectedStopName);
//        mRecyclerViewAdapter.swapNextDeparturesDetails(nextDepartureDetailsList);
        mNextDeparturesAdapter.swap(nextDepartureDetailsList);
        mSearchStopDetails = stopDetails;
    }

//    AdapterView.OnItemSelectedListener mOnItemSelectedListener;

}
