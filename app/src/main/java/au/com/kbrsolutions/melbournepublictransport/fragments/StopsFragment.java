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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.adapters.StopsAdapter;
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.data.MptContract;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;

/**
 * Show list of all train stops that are not flagged as 'favorite' one,
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StopsFragment.OnStopFragmentInteractionListener} interface
 * to handle interaction events.
 */
@SuppressWarnings("ALL")
public class StopsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private NestedScrollingListView mListView;
    private StopsAdapter mStopDetailAdapter;
    private OnStopFragmentInteractionListener mListener;
//    private static final List<StopDetails> mFolderItemList = new ArrayList<>();
    private TextView mEmptyView;
    private View mRootView;
    private int mCursorRowCnt;
    private View mCurrentSelectedView;
    private int mCurrentSelectedRow;
    private int selectedViewNo = -1;
    private static final String ASCENDING_SORT_ORDER = " ASC";
    private static final String DOT = ".";

    private static final int STOP_DETAILS_LOADER = 0;

    public static final String[] STOP_DETAILS_COLUMNS = {
            MptContract.StopDetailEntry.TABLE_NAME + DOT + MptContract.StopDetailEntry._ID,
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

    /**
     *
     * Call invalidateOptionsMenu() to change the available menu options.
     *
     */
    public void hideView() {
        mRootView.setVisibility(View.INVISIBLE);
        if (mListener != null) {
            ((Activity) mListener).invalidateOptionsMenu();
        }
    }

    /**
     *
     * Call invalidateOptionsMenu() to change the available menu options.
     *
     */
    public void showView() {
        mRootView.setVisibility(View.VISIBLE);
        if (mListener != null) {
            ((Activity) mListener).invalidateOptionsMenu();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mStopDetailAdapter = new StopsAdapter(getActivity().getApplicationContext(), mListener);
        mRootView = inflater.inflate(R.layout.fragment_stops, container, false);

        mListView = (NestedScrollingListView) mRootView.findViewById(R.id.addStopsListView);
        mListView.setAdapter(mStopDetailAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                handleRowSelected(adapterView, position);
            }
        });

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

        mEmptyView = (TextView) mRootView.findViewById(R.id.emptyView);
        // FIXME: 2/12/2016 remove commented lines
//        if (mFolderItemList.size() == 0) {
//            mEmptyView = (TextView) mRootView.findViewById(R.id.emptyView);
//            mEmptyView.setText(getActivity().getResources()
//                    .getString(R.string.no_stops_to_add));
//        }
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
            StopsAdapter.ViewHolder holder = (StopsAdapter.ViewHolder)
                    mCurrentSelectedView.getTag();
            mListView.clearFocus();
            holder.departuresImageId.setFocusable(true);
            holder.departuresImageId.requestFocus();
        }
    }

    public boolean handleVerticalDpadKeys(boolean upKeyPressed) {
        if (upKeyPressed && mCurrentSelectedRow == 0 ||
                !upKeyPressed && mCurrentSelectedRow == mCursorRowCnt - 1) {
            mCurrentSelectedRow = -1;
            mCurrentSelectedView = null;
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
        if (mCurrentSelectedView != null) {
            int selectableViewsCnt = 2;
            if (rightKeyPressed) {
                selectedViewNo = selectedViewNo ==
                        (selectableViewsCnt - 1) ? 0 : selectedViewNo + 1;
            } else {
                selectedViewNo = selectedViewNo < 1 ? selectableViewsCnt - 1 : selectedViewNo - 1;
            }
            StopsAdapter.ViewHolder holder = (StopsAdapter.ViewHolder)
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
        String sortOrder = MptContract.StopDetailEntry.COLUMN_LOCATION_NAME +
                ASCENDING_SORT_ORDER;

        Uri nonFavoriteStopDetailUri = MptContract.StopDetailEntry.buildFavoriteStopsUri(
                MptContract.StopDetailEntry.NON_FAVORITE_FLAG);

        return new CursorLoader(getActivity(),
                nonFavoriteStopDetailUri,
                STOP_DETAILS_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorRowCnt = data.getCount();
        mStopDetailAdapter.swapCursor(data);
        if (mCursorRowCnt > 0) {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStopDetailAdapter.swapCursor(null);
    }

    /**
     *
     * @param adapterView
     * @param position
     */
    private void handleRowSelected(AdapterView<?> adapterView, int position) {
        // CursorAdapter returns a cursor at the correct position for getItem(), or null
        // if it cannot seek to that position.
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        if (cursor != null) {
            mListener.updateStopDetailRow(cursor.getInt(COL_STOP_DETAILS_ID),
                    MptContract.StopDetailEntry.FAVORITE_FLAG);
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
        void updateStopDetailRow(int id, String favoriteColumnValue);
        void startNextDeparturesSearch(StopDetails stopDetails);
    }
}
