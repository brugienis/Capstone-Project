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
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.adapters.StopsNearbyAdapter;
import au.com.kbrsolutions.melbournepublictransport.adapters.StopsNearbyAdapterRv;
import au.com.kbrsolutions.melbournepublictransport.data.NearbyStopsDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.utilities.HorizontalDividerItemDecoration;
import au.com.kbrsolutions.melbournepublictransport.utilities.Utility;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link
 * OnNearbyStopsFragmentInteractionListener} interface.
 */
public class StopsNearbyFragment extends BaseFragment {

    private static final String ARG_NEARBY_DATA = "arg_nearby_data";
    private List<NearbyStopsDetails> mNearbyStopsDetailsList;
    private StopsNearbyAdapterRv mRecyclerViewAdapter;
    private StopsNearbyAdapter mStopsNearbyAdapter;
    private boolean newInstanceArgsRetrieved;
    private View mRootView;
    private OnNearbyStopsFragmentInteractionListener mListener;
    private NestedScrollingListView mListView;
    private TextView mEmptyView;

    private static final String TAG = StopsNearbyFragment.class.getSimpleName();

    public StopsNearbyFragment() {
        // Required empty public constructor
    }

    public static StopsNearbyFragment newInstance(List<NearbyStopsDetails> nearbyStopsDetailsList) {
        StopsNearbyFragment fragment = new StopsNearbyFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_NEARBY_DATA, (ArrayList) nearbyStopsDetailsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && !newInstanceArgsRetrieved) {
            mNearbyStopsDetailsList = (ArrayList)getArguments().getParcelableArrayList(ARG_NEARBY_DATA);
            newInstanceArgsRetrieved = true;
        }
        setRetainInstance(true);
    }

    public void hideView() {
        mRootView.setVisibility(View.INVISIBLE);
    }

    public void showView() {
        mRootView.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_nearby_stops, container, false);

//        View rootView = (RecyclerView) mRootView.findViewById(R.id.nearbyStopsList);

        mStopsNearbyAdapter = new StopsNearbyAdapter(getActivity(),
                mNearbyStopsDetailsList, mListener);
        mListView = (NestedScrollingListView) mRootView.findViewById(R.id.stopsNearbyList);
        mListView.setAdapter(mStopsNearbyAdapter);
        mEmptyView = (TextView) mRootView.findViewById(R.id.emptyView);
        mEmptyView = (TextView) mRootView.findViewById(R.id.emptyView);
        mEmptyView.setText(getActivity().getResources()
                .getString(R.string.no_favorite_stops_selected));

        mNextDepartureDetailsCnt = mNearbyStopsDetailsList.size();

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
        return mRootView;
    }

    private int mNextDepartureDetailsCnt;
    private View mCurrentSelectedView;
    private int mCurrentSelectedRow;
    private int selectableViewsCnt = 2;
    private int selectedViewNo = -1;

    private void handleItemSelected(View view, int position) {
        if (view.getTag() != null) {
            mCurrentSelectedView = view;
            mCurrentSelectedRow = position;
            selectedViewNo = 0;
            StopsNearbyAdapter.ViewHolder holder = (StopsNearbyAdapter.ViewHolder) mCurrentSelectedView.getTag();
            mListView.clearFocus();
            holder.departuresImageId.setFocusable(true);
            holder.departuresImageId.requestFocus();
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
        boolean resultOk = false;
        if (mCurrentSelectedView != null) { // && mCurrentSelectedView.hasFocus()) {
            int prevSelectedViewNo = selectedViewNo;
            if (rightKeyPressed) {
                Log.v(TAG, "handleHorizontalDpadKeys - > before prev/selectedViewNo: " + prevSelectedViewNo + "/" + selectedViewNo);
                selectedViewNo = selectedViewNo == (selectableViewsCnt - 1) ? 0 : selectedViewNo + 1;
                Log.v(TAG, "handleHorizontalDpadKeys - > after  prev/selectedViewNo: " + prevSelectedViewNo + "/" + selectedViewNo);
            } else {
                Log.v(TAG, "handleHorizontalDpadKeys - < before prev/selectedViewNo: " + prevSelectedViewNo + "/" + selectedViewNo);
                selectedViewNo = selectedViewNo < 1 ? selectableViewsCnt - 1 : selectedViewNo - 1;
                Log.v(TAG, "handleHorizontalDpadKeys - < after  prev/selectedViewNo: " + prevSelectedViewNo + "/" + selectedViewNo);
            }
            Log.v(TAG, "handleHorizontalDpadKeys - prevSelectedViewNo/selectedViewNo: " + prevSelectedViewNo + "/" + selectedViewNo);
            StopsNearbyAdapter.ViewHolder holder = (StopsNearbyAdapter.ViewHolder) mCurrentSelectedView.getTag();
            mListView.clearFocus();
            switch (selectedViewNo) {
                case 0:
                    holder.departuresImageId.setFocusable(true);
                    holder.departuresImageId.requestFocus();
                    Log.v(TAG, "handleHorizontalDpadKeys - departuresImageId in focus");
                    break;

                case 1:
                    holder.mapImageId.setFocusable(true);
                    holder.mapImageId.requestFocus();
                    Log.v(TAG, "handleHorizontalDpadKeys - mapImageId in focus");
                    break;

                default:
                    throw new RuntimeException(TAG + ".handleHorizontalDpadKeys - case '" +
                            selectedViewNo + "' not handled");
            }
            resultOk = true;
        } else {
            if (mCurrentSelectedView == null) {
                Log.v(TAG, "handleHorizontalDpadKeys - mCurrentSelectedView is null");
            } else {
                Log.v(TAG, "handleHorizontalDpadKeys - mCurrentSelectedView NOT in focus");
            }
        }
        return resultOk;
    }

//    @Override
    public View onCreateView_UseWithRececleViewAdapter(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_nearby_stops_rv, container, false);

        RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.nearbyStopsList);

        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        Drawable divider = getResources().getDrawable(R.drawable.item_divider);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration(divider));
        mRecyclerViewAdapter = new StopsNearbyAdapterRv(mNearbyStopsDetailsList, mListener);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.requestLayout();
        return mRootView;
    }

    public void setNewContent(List<NearbyStopsDetails> nearbyStopsDetailsList) {
//        mRecyclerViewAdapter.swap(nearbyStopsDetailsList);
        mStopsNearbyAdapter.swap(nearbyStopsDetailsList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNearbyStopsFragmentInteractionListener) {
            mListener = (OnNearbyStopsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNearbyStopsFragmentInteractionListener");
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
    public interface OnNearbyStopsFragmentInteractionListener {
        // TODO: Update argument type and name
        void onNearbyStopsFragmentMapClicked(NearbyStopsDetails nearbyStopsDetails);
        void startNextDeparturesSearch(StopDetails stopDetails);
    }

}
