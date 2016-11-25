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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.adapters.FavoriteStopsAdapter;
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.data.MptContract;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;

/**
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoriteStopsFragment.OnFavoriteStopsFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FavoriteStopsFragment
        extends BaseFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private NestedScrollingListView mListView;
    private List<StopDetails> mStopDetailsList;
    private TextView mEmptyView;
    private View mRootView;
    private boolean isVisible;
    private FavoriteStopsAdapter mFavoriteStopDetailAdapter;
    private OnFavoriteStopsFragmentInteractionListener mListener;
    private boolean mIsInSettingsActivityFlag;
    private int mCursorRowCnt;
    private View mCurrentSelectedView;
    private int mCurrentSelectedRow;
    private int selectableViewsCnt = 3;
    private int selectedViewNo = -1;

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

    public FavoriteStopsFragment() {
        // Required empty public constructor
    }

    /*
        Call invalidateOptionsMenu() only if mCallbacks is not null.
     */
    public void hideView() {
        isVisible = false;
        if (mRootView != null) {
            mRootView.setVisibility(View.INVISIBLE);
        }
        if (mListener != null) {
            ((Activity) mListener).invalidateOptionsMenu();
        }
    }

    public void showView() {
        isVisible = true;
        if (mRootView != null) {
            mRootView.setVisibility(View.VISIBLE);
        }
        if (mListener != null) {
            ((Activity) mListener).invalidateOptionsMenu();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        isVisible = true;
    }

    private boolean mDatabaseIsEmpty;

    public void databaseIsEmpty(boolean databaseIsEmpty) {
        mDatabaseIsEmpty = databaseIsEmpty;
        // FIXME: 15/11/2016 - move to strings. Check the usage.
        setEmptyViewText("databaseIsEmpty");
    }

    private void setEmptyViewText(String source) {
        if (mDatabaseIsEmpty) {
            mEmptyView.setText(getActivity().getResources().getString(R.string.database_is_empty));
        } else {
            if (mIsInSettingsActivityFlag) {
                mEmptyView.setText(getActivity().getResources().getString(R.string.no_favorite_stops_selected_in_settings));
            } else {
                mEmptyView.setText(getActivity().getResources().getString(R.string.no_favorite_stops_selected));
            }
        }
    }

    private void clearEmptyViewText() {
        mEmptyView.setText("");
    }

    /**
     *
     * Followed henry74918's advice of how to navigate between items on a list's row.
     *
     * see:
     *
     *      http://stackoverflow.com/questions/14392356/how-to-use-d-pad-navigate-switch-between-listviews-row-and-its-decendants-goo
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    // FIXME: 8/11/2016 http://stackoverflow.com/questions/14392356/how-to-use-d-pad-navigate-switch-between-listviews-row-and-its-decendants-goo
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFavoriteStopDetailAdapter = new FavoriteStopsAdapter(
                getActivity().getApplicationContext(),
                null,
                0,
                mListener,
                mIsInSettingsActivityFlag);

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_favorite_stops, container, false);

        if (mStopDetailsList == null) {
            mStopDetailsList = new ArrayList<>();
        }
        mListView = (NestedScrollingListView) mRootView.findViewById(R.id.favoriteStopsListView);
        mListView.setAdapter(mFavoriteStopDetailAdapter);
        mListView.setNestedScrollingEnabled(true);
        mListView.setItemsCanFocus(true);

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
        return mRootView;
    }

    private void handleItemSelected(View view, int position) {
        if (view.getTag() != null) {
            mCurrentSelectedView = view;
            mCurrentSelectedRow = position;
            selectedViewNo = 0;
            FavoriteStopsAdapter.ViewHolder holder = (FavoriteStopsAdapter.ViewHolder) mCurrentSelectedView.getTag();
            mListView.clearFocus();
            holder.departuresImageId.setFocusable(true);
            holder.departuresImageId.requestFocus();
        }
    }

    public boolean handleVerticalDpadKeys(boolean upKeyPressed) {
        if (upKeyPressed && mCurrentSelectedRow == 0 ||
                !upKeyPressed && mCurrentSelectedRow == mCursorRowCnt - 1) {
//            mCurrentSelectedRow = -1;
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
            int prevSelectedViewNo = selectedViewNo;
            if (rightKeyPressed) {
                selectedViewNo = selectedViewNo == (selectableViewsCnt - 1) ? 0 : selectedViewNo + 1;
            } else {
                selectedViewNo = selectedViewNo < 1 ? selectableViewsCnt - 1 : selectedViewNo - 1;
            }
            FavoriteStopsAdapter.ViewHolder holder = (FavoriteStopsAdapter.ViewHolder) mCurrentSelectedView.getTag();
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

                case 2:
                    holder.garbageInfoImage.setFocusable(true);
                    holder.garbageInfoImage.requestFocus();
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

    private int loaderId;
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.
        // Sort order:  Ascending, by location_name.
        loaderId = i;
        String sortOrder = MptContract.StopDetailEntry.COLUMN_LOCATION_NAME + " ASC";

        Uri nonFavoriteStopDetailUri = MptContract.StopDetailEntry.buildFavoriteStopsUri(MptContract.StopDetailEntry.FAVORITE_FLAG);

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
        mFavoriteStopDetailAdapter.swapCursor(data);
        mCursorRowCnt = data.getCount();
        if (mCursorRowCnt > 0) {
            clearEmptyViewText();
            mEmptyView.setVisibility(View.GONE);
//            Log.v(TAG, "onLoadFinished - after GONE");
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
        // FIXME: 30/08/2016 below add correct code
//        mForecastAdapter.swapCursor(data);
//        if (mPosition != ListView.INVALID_POSITION) {
//            // If we don't need to restart the loader, and there's a desired position to restore
//            // to, do so now.
//            mListView.smoothScrollToPosition(mPosition);
//        }
    }

    public void reloadLoader() {
        Log.v(TAG, "reloadLoader - start: ");
        getLoaderManager().restartLoader(loaderId, null, this);  //id = 0
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavoriteStopDetailAdapter.swapCursor(null);
    }

    public void setIsInSettingsActivityFlag(boolean value) {
        mIsInSettingsActivityFlag = value;
//        if (mFavoriteStopDetailAdapter != null) {
//            mFavoriteStopDetailAdapter.setIsInSettingsActivityFlag(value);
//        }
    }

    private void handleRowSelected(AdapterView<?> adapterView, int position) {
        // CursorAdapter returns a cursor at the correct position for getItem(), or null
        // if it cannot seek to that position.
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        if (cursor != null) {
            if (mIsInSettingsActivityFlag) {
                mListener.updateWidgetStopDetails(
                        cursor.getString(COL_STOP_DETAILS_STOP_ID),
                        cursor.getString(COL_STOP_DETAILS_LOCATION_NAME));
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FavoriteStopsFragment.OnFavoriteStopsFragmentInteractionListener) {
            mListener = (FavoriteStopsFragment.OnFavoriteStopsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (isVisible) {
            menu.findItem(R.id.action_train_stops_nearby).setVisible(true);
            menu.findItem(R.id.action_stops_nearby).setVisible(true);
            menu.findItem(R.id.action_disruptions).setVisible(true);
            menu.findItem(R.id.action_reload_database).setVisible(true);
        } else {
            menu.findItem(R.id.action_train_stops_nearby).setVisible(false);
            menu.findItem(R.id.action_stops_nearby).setVisible(false);
            menu.findItem(R.id.action_disruptions).setVisible(false);
            menu.findItem(R.id.action_reload_database).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favorite_stops, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_train_stops_nearby) {
            mListener.startStopsNearbySearch(true);
            return true;
        } else if (id == R.id.action_stops_nearby) {
            mListener.startStopsNearbySearch(false);
            return true;
        } else if (id == R.id.action_disruptions) {
            mListener.getDisruptionsDetails();
            return true;
        } else if (id == R.id.action_reload_database) {
            mListener.reloadDatabase();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    public interface OnFavoriteStopsFragmentInteractionListener {
        void startNextDeparturesSearch(StopDetails stopDetails);
        void showStopOnMap(LatLngDetails latLonDetails);
        void startStopsNearbySearch(boolean trainsOnly);
        void getDisruptionsDetails();
        void updateStopDetailRow(int id, String favoriteColumnValue);
        void reloadDatabase();
        void updateWidgetStopDetails(String stopId, String locationName);
    }


}
