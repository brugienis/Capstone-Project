package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.adapters.StopsAdapter;
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.data.MptContract;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.utilities.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StopsFragment.OnStopFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class StopsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // FIXME: 25/08/2016 check Running a Query with a CursorLoader - https://developer.android.com/training/load-data-background/setup-loader.html

    private NestedScrollingListView mListView;
    private StopsAdapter mStopDetailAdapter;
    private OnStopFragmentInteractionListener mListener;
    private static List<StopDetails> mFolderItemList = new ArrayList<>();
    private TextView mEmptyView;
    private View mRootView;
    private boolean isVisible;

    private static final int STOP_DETAILS_LOADER = 0;

    public static final String[] STOP_DETAILS_COLUMNS = {
            MptContract.StopDetailEntry.TABLE_NAME + "." + MptContract.StopDetailEntry._ID,
            MptContract.StopDetailEntry.COLUMN_ROUTE_TYPE,
            MptContract.StopDetailEntry.COLUMN_STOP_ID,
            MptContract.StopDetailEntry.COLUMN_LOCATION_NAME,
            MptContract.StopDetailEntry.COLUMN_LATITUDE,
            MptContract.StopDetailEntry.COLUMN_LONGITUDE,
            MptContract.StopDetailEntry.COLUMN_FAVORITE
    };

    // These indices are tied to stop_details columns specified above.
    public static final int COL_STOP_DETAILS_ID = 0;
    public static final int COL_STOP_DETAILS_ROUTE_TYPE = 1;
    public static final int COL_STOP_DETAILS_STOP_ID = 2;
    public static final int COL_STOP_DETAILS_LOCATION_NAME = 3;
    public static final int COL_STOP_DETAILS_LATITUDE = 4;
    public static final int COL_STOP_DETAILS_LONGITUDE = 5;
    public static final int COL_STOP_DETAILS_FAVORITE = 6;

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public StopsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /*
        Call invalidateOptionsMenu() only if mCallbacks is not null.
     */
    public void hideView() {
        isVisible = false;
        mRootView.setVisibility(View.INVISIBLE);
        if (mListener != null) {
            ((Activity) mListener).invalidateOptionsMenu();
        }
//        Log.v(TAG, "hideView: " + String.format("0x%08X", this.hashCode()));
    }

    public void showView() {
        isVisible = true;
        mRootView.setVisibility(View.VISIBLE);
        if (mListener != null) {
            ((Activity) mListener).invalidateOptionsMenu();
        }
//        Log.v(TAG, "showView: " + String.format("0x%08X", this.hashCode()));
    }

    public void isRootViewVisible() {
        if (mRootView.getVisibility() == View.VISIBLE) {
            Log.v(TAG, "isVisible - is visible");
        } else {
            Log.v(TAG, "isVisible - is NOT visible");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mStopDetailAdapter = new StopsAdapter(getActivity().getApplicationContext(), null, 0, mListener);
        mRootView = inflater.inflate(R.layout.fragment_stops, container, false);

        mListView = (NestedScrollingListView) mRootView.findViewById(R.id.addStopsListView);
        mListView.setAdapter(mStopDetailAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // FIXME: 8/09/2016 - run update to change 'favorite' flag to 'y'
                handleRowSelected(adapterView, position);
            }
        });

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

        if (mFolderItemList.size() == 0) {
            mEmptyView = (TextView) mRootView.findViewById(R.id.emptyView);
            mEmptyView.setText(getActivity().getResources()
                    .getString(R.string.no_stops_to_add));
        }
        return mRootView;
    }

    private int mCursorRowCnt;
    private View mCurrentSelectedView;
    private int mCurrentSelectedRow;
    private int selectableViewsCnt = 2;
    private int selectedViewNo = -1;

    private void handleItemSelected(View view, int position) {
        if (view.getTag() != null) {
            mCurrentSelectedView = view;
            mCurrentSelectedRow = position;
            selectedViewNo = 0;
            StopsAdapter.ViewHolder holder = (StopsAdapter.ViewHolder) mCurrentSelectedView.getTag();
            mListView.clearFocus();
            holder.departuresImageId.setFocusable(true);
            holder.departuresImageId.requestFocus();
            Log.v(TAG, "handleItemSelected - departuresImageId in focus");
        }
    }

    public boolean handleVerticalDpadKeys(boolean upKeyPressed) {
        Log.v(TAG, "handleVerticalDpadKeys - start - mCurrentSelectedRow/mCursorRowCnt: " + mCurrentSelectedRow + "/" + mCursorRowCnt);
        if (upKeyPressed && mCurrentSelectedRow == 0 ||
                !upKeyPressed && mCurrentSelectedRow == mCursorRowCnt - 1) {
            mCurrentSelectedRow = -1;
            mCurrentSelectedView = null;
            Log.v(TAG, "handleVerticalDpadKeys - moved above 1st. row");
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
            StopsAdapter.ViewHolder holder = (StopsAdapter.ViewHolder) mCurrentSelectedView.getTag();
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(STOP_DETAILS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.
        // Sort order:  Ascending, by location_name.
        String sortOrder = MptContract.StopDetailEntry.COLUMN_LOCATION_NAME + " ASC";

        Uri nonFavoriteStopDetailUri = MptContract.StopDetailEntry.buildFavoriteStopsUri(MptContract.StopDetailEntry.NON_FAVORITE_FLAG);

        return new CursorLoader(getActivity(),
                nonFavoriteStopDetailUri,
                STOP_DETAILS_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        Log.v(TAG, "onLoadFinished - start - rows cnt: " + data.getCount());
        mCursorRowCnt = data.getCount();
        mStopDetailAdapter.swapCursor(data);
        if (mCursorRowCnt > 0) {
            mEmptyView.setVisibility(View.GONE);
        }
        // FIXME: 30/08/2016 below add correct code
//        mForecastAdapter.swapCursor(data);
//        if (mPosition != ListView.INVALID_POSITION) {
//            // If we don't need to restart the loader, and there's a desired position to restore
//            // to, do so now.
//            mListView.smoothScrollToPosition(mPosition);
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStopDetailAdapter.swapCursor(null);
    }

    private void handleRowSelected(AdapterView<?> adapterView, int position) {
        // CursorAdapter returns a cursor at the correct position for getItem(), or null
        // if it cannot seek to that position.
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        if (cursor != null) {
            String favoriteValue = "y";
            mListener.updateStopDetailRow(cursor.getInt(COL_STOP_DETAILS_ID), favoriteValue);
//            mListener.showUpdatedFavoriteStops();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStopFragmentInteractionListener) {
            mListener = (OnStopFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStopFragmentInteractionListener");
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
    public interface OnStopFragmentInteractionListener {
        void showUpdatedFavoriteStops();
        void showStopOnMap(String stopName, LatLngDetails latLonDetails);
        void updateStopDetailRow(int id, String favoriteColumnValue);  // activity should send the removeSelectedStop() below
        void startNextDeparturesSearch(StopDetails stopDetails);
        // to IntentService
    }
}
