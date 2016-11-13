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
import android.widget.ListView;
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

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class NextDeparturesFragment extends BaseFragment {

    private static final String ARG_SELECTED_STOP_NAME = "arg_selected_stop_name";
    private static final String ARG_NEXT_DEPARTURE_DATA = "next_departure_data";
    private List<NextDepartureDetails> mNextDepartureDetailsList;
    private ListView mListView;
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_next_departure_rv, container, false);
        View rootView = inflater.inflate(R.layout.fragment_next_departure, container, false);

//        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.disruptionsList);
//        List<NextDepartureDetails> artistsItemsList = new ArrayList<>();
        mNextDeparturesAdapter = new NextDeparturesAdapter(getActivity(), mNextDepartureDetailsList);
        mListView = (ListView) rootView.findViewById(R.id.nextDeparturesList);
        mListView.setAdapter(mNextDeparturesAdapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                handleArtistRowClicked(position);
//            }
//        });
        mEmptyView = (TextView) rootView.findViewById(R.id.emptyView);
        mEmptyView = (TextView) rootView.findViewById(R.id.emptyView);
        mEmptyView.setText(getActivity().getResources()
                .getString(R.string.no_favorite_stops_selected));

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

        selectedStopNameTv = (TextView) rootView.findViewById(R.id.selectedStopName);
        selectedStopNameTv.setText(mSelectedStopName);

        return rootView;
    }

    /**
     * Show artists details or empty view if no data found.
     */
//    public void showArtistsDetails() {
////        mSearchText.setText(mArtistName);
////        mSearchText.requestFocus();
////        if (mArtistName != null) {
////            mSearchText.setSelection(mArtistName.length());
////        }
//        mNextDeparturesAdapter.clear();
//        if (mArtistsDetailsList != null) {
//            mArtistArrayAdapter.addAll(mArtistsDetailsList);
//            mListView.clearChoices();    /* will clear previously selected artist row */
//            mArtistArrayAdapter.notifyDataSetChanged(); /* call after clearChoices above */
//            mListView.setSelection(mArtistsListViewFirstVisiblePosition);
//            if (mArtistArrayAdapter.isEmpty()) {
//                mEmptyView.setVisibility(View.VISIBLE);
////                mEmptyView.setText();
//            } else {
//                mEmptyView.setVisibility(View.GONE);
//            }
//        }
//    }

//    @Override
    public View onCreateViewOld(LayoutInflater inflater, ViewGroup container,
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

    @Override
    public boolean handleHorizontalDpadKeys(boolean rightKeyPressed) {
        Log.v(TAG, "handleHorizontalDpadKeys - : ");
        return false;
    }

    @Override
    public boolean handleVerticalDpadKeys(boolean upKeyPressed) {
        Log.v(TAG, "handleVerticalDpadKeys - : ");
        return false;
    }

    public void setNewContent(
            String selectedStopName,
            List<NextDepartureDetails> nextDepartureDetailsList,
            StopDetails stopDetails) {
        mSelectedStopName = selectedStopName;
        selectedStopNameTv.setText(selectedStopName);
//        mRecyclerViewAdapter.swap(nextDepartureDetailsList);
        swap(nextDepartureDetailsList);
        mSearchStopDetails = stopDetails;
    }

    private void swap(List<NextDepartureDetails> nextDepartureDetailsList) {
        mNextDeparturesAdapter.clear();
        if (mNextDepartureDetailsList != null) {
//            mNextDeparturesAdapter.addAll(mNextDepartureDetailsList);
            mListView.clearChoices();    /* will clear previously selected artist row */
            mNextDeparturesAdapter.addAll(mNextDepartureDetailsList);
            mNextDeparturesAdapter.notifyDataSetChanged(); /* call after clearChoices above */
            mListView.setSelection(0);
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
}
