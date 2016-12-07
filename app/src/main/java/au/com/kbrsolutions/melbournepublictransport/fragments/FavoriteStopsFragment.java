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
 * Handles favorite train stops.
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoriteStopsFragment.OnFavoriteStopsFragmentInteractionListener} interface
 * to handle interaction events.
 */
@SuppressWarnings("ALL")
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
    private boolean mDatabaseIsEmpty;
    private boolean mIsShowOptionsMenu;
    private int loaderId;
    private static final String ASCENDING_SORT_ORDER = " ASC";
    private static final String DOT = ".";

    private static final int STOP_DETAILS_LOADER = 0;

    private static final String[] STOP_DETAILS_COLUMNS = {
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

    public FavoriteStopsFragment() {
        // Required empty public constructor
    }

    /**
     *
     * Call invalidateOptionsMenu() to change the available menu options.
     *
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


    /**
     *
     * Call invalidateOptionsMenu() to change the available menu options.
     *
     */
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

    public void databaseIsEmpty(boolean databaseIsEmpty) {
        mDatabaseIsEmpty = databaseIsEmpty;
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

    public void setShowOptionsMenuFlg(boolean showOptionsMenuFlg) {
        if (mIsShowOptionsMenu != showOptionsMenuFlg) {
            mIsShowOptionsMenu = showOptionsMenuFlg;
            if (mListener != null) {
                ((Activity) mListener).invalidateOptionsMenu();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFavoriteStopDetailAdapter = new FavoriteStopsAdapter(
                getActivity().getApplicationContext(),
                mListener,
                mIsInSettingsActivityFlag);
        Log.v(TAG, "onCreateView - mFavoriteStopDetailAdapter/mListener: " + mFavoriteStopDetailAdapter + "/" + mListener);

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

        mEmptyView = (TextView) mRootView.findViewById(R.id.emptyFavoriteStopsView);

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
            FavoriteStopsAdapter.ViewHolder holder = (FavoriteStopsAdapter.ViewHolder)
                    mCurrentSelectedView.getTag();
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
            FavoriteStopsAdapter.ViewHolder holder = (FavoriteStopsAdapter.ViewHolder)
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created. Sort order:  Ascending, by
        // location_name.
        loaderId = i;
        String sortOrder = MptContract.StopDetailEntry.COLUMN_LOCATION_NAME +
                ASCENDING_SORT_ORDER;

        Uri nonFavoriteStopDetailUri = MptContract.StopDetailEntry.buildFavoriteStopsUri(
                MptContract.StopDetailEntry.FAVORITE_FLAG);

        return new CursorLoader(getActivity(),
                nonFavoriteStopDetailUri,
                STOP_DETAILS_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFavoriteStopDetailAdapter.swapCursor(data);
        mCursorRowCnt = data.getCount();
        if (mCursorRowCnt == 0) {
            mListView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            setEmptyViewText(TAG);
        } else {
            clearEmptyViewText();
            mListView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    public void reloadLoader() {
        getLoaderManager().restartLoader(loaderId, null, this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavoriteStopDetailAdapter.swapCursor(null);
    }

    public void setIsInSettingsActivityFlag() {
        mIsInSettingsActivityFlag = true;
    }

    /**
     * Passes selected stop details to the parent activity.
     *
     * @param adapterView
     * @param position
     */
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
            Log.v(TAG, "onAttach - : mListener" + mListener);
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
        if (!isVisible || !mIsShowOptionsMenu) {
            menu.findItem(R.id.action_train_stops_nearby).setVisible(false);
            menu.findItem(R.id.action_stops_nearby).setVisible(false);
            menu.findItem(R.id.action_disruptions).setVisible(false);
            menu.findItem(R.id.action_about).setVisible(false);
            menu.findItem(R.id.action_reload_database).setVisible(false);
        } else {
            menu.findItem(R.id.action_train_stops_nearby).setVisible(true);
            menu.findItem(R.id.action_stops_nearby).setVisible(true);
            menu.findItem(R.id.action_disruptions).setVisible(true);
            menu.findItem(R.id.action_about).setVisible(true);
            menu.findItem(R.id.action_reload_database).setVisible(true);
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
        void showStopOnMap(String stopName, LatLngDetails latLonDetails);
        void startStopsNearbySearch(boolean trainsOnly);
        void getDisruptionsDetails();
        void updateStopDetailRow(int id, String favoriteColumnValue);
        void reloadDatabase();
        void updateWidgetStopDetails(String stopId, String locationName);
    }


}
