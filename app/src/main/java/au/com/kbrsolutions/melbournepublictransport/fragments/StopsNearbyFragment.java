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
import au.com.kbrsolutions.melbournepublictransport.adapters.StopsNearbyAdapter;
import au.com.kbrsolutions.melbournepublictransport.adapters.StopsNearbyAdapterRv;
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopsNearbyDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.utilities.HorizontalDividerItemDecoration;

/**
 * This class handles retrieving information about stops nearby.
 *
 * <p/>
 * Activities containing this fragment MUST implement the {@link
 * OnNearbyStopsFragmentInteractionListener} interface.
 */
@SuppressWarnings("ALL")
public class StopsNearbyFragment extends BaseFragment {

    private List<StopsNearbyDetails> mNearbyStopsDetailsList;
    private StopsNearbyAdapter mStopsNearbyAdapter;
    private boolean newInstanceArgsRetrieved;
    private View mRootView;
    private OnNearbyStopsFragmentInteractionListener mListener;
    private NestedScrollingListView mListView;
    private int mNextDepartureDetailsCnt;
    private View mCurrentSelectedView;
    private int mCurrentSelectedRow;
    private int selectedViewNo = -1;
    private static final String ARG_NEARBY_DATA = "arg_nearby_data";

    private static final String TAG = StopsNearbyFragment.class.getSimpleName();

    public StopsNearbyFragment() {
        // Required empty public constructor
    }

    public static StopsNearbyFragment newInstance(List<StopsNearbyDetails> nearbyStopsDetailsList) {
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
            mNearbyStopsDetailsList = (ArrayList)getArguments().getParcelableArrayList(
                    ARG_NEARBY_DATA);
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

        mRootView = inflater.inflate(R.layout.fragment_stops_nearby, container, false);

        mStopsNearbyAdapter = new StopsNearbyAdapter(getActivity(),
                mNearbyStopsDetailsList, mListener);
        mListView = (NestedScrollingListView) mRootView.findViewById(R.id.stopsNearbyList);
        mListView.setAdapter(mStopsNearbyAdapter);
        TextView emptyView = (TextView) mRootView.findViewById(R.id.emptyView);
        emptyView = (TextView) mRootView.findViewById(R.id.emptyView);
        emptyView.setText(getActivity().getResources()
                .getString(R.string.no_favorite_stops_selected));

        mNextDepartureDetailsCnt = mNearbyStopsDetailsList.size();

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
        return mRootView;
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
            selectedViewNo = 0;
            StopsNearbyAdapter.ViewHolder holder = (StopsNearbyAdapter.ViewHolder)
                    mCurrentSelectedView.getTag();
            mListView.clearFocus();
            holder.departuresImageId.setFocusable(true);
            holder.departuresImageId.requestFocus();
        }
    }

    public boolean handleVerticalDpadKeys(boolean upKeyPressed) {
        if (upKeyPressed && mCurrentSelectedRow == 0 ||
                !upKeyPressed && mCurrentSelectedRow == mNextDepartureDetailsCnt - 1) {
            mCurrentSelectedRow = -1;
        } else {
            mCurrentSelectedView.setFocusable(true);
            mCurrentSelectedView.requestFocus();
        }
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
        boolean resultOk = false;
        if (mCurrentSelectedView != null && mCurrentSelectedView.hasFocus()) {
            int selectableViewsCnt = 2;
            if (rightKeyPressed) {
                selectedViewNo = selectedViewNo ==
                        (selectableViewsCnt - 1) ? 0 : selectedViewNo + 1;
            } else {
                selectedViewNo = selectedViewNo < 1 ? selectableViewsCnt - 1 : selectedViewNo - 1;
            }
            StopsNearbyAdapter.ViewHolder holder = (StopsNearbyAdapter.ViewHolder)
                    mCurrentSelectedView.getTag();
            mListView.clearFocus();
            switch (selectedViewNo) {
                case 0:
                    holder.departuresImageId.setFocusable(true);
                    holder.departuresImageId.requestFocus();
                    break;

                case 1:
                    holder.mapImageId.setFocusable(true);
                    holder.mapImageId.requestFocus();
                    break;

                default:
                    throw new RuntimeException(TAG + ".handleHorizontalDpadKeys - case '" +
                            selectedViewNo + "' not handled");
            }
            resultOk = true;
        }
        return resultOk;
    }

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

        mRootView = inflater.inflate(R.layout.fragment_nearby_stops_rv, container, false);

        RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.nearbyStopsList);

        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        Drawable divider = getResources().getDrawable(R.drawable.item_divider);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration(divider));
        StopsNearbyAdapterRv recyclerViewAdapter = new StopsNearbyAdapterRv(mNearbyStopsDetailsList, mListener);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.requestLayout();
        return mRootView;
    }

    /**
     *
     * Show the latest disruptions details.
     *
     * @param nearbyStopsDetailsList
     */
    public void setNewContent(List<StopsNearbyDetails> nearbyStopsDetailsList) {
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
//        void onNearbyStopsFragmentMapClicked(NearbyStopsDetails nearbyStopsDetails);
        void showStopOnMap(String stopName, LatLngDetails latLonDetails);
        void startNextDeparturesSearch(StopDetails stopDetails);
    }

}
