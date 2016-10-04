package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.app.Activity;
import android.content.ContentValues;
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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.adapters.FavoriteStopsAdapter;
import au.com.kbrsolutions.melbournepublictransport.data.LatLonDetails;
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

//    private OnFavoriteStopsFragmentInteractionListener mCallbacks;
    private ListView mListView;
    private List<StopDetails> mStopDetailsList;
    private TextView mEmptyView;
    private View mRootView;
    private boolean isVisible;
    private FavoriteStopsAdapter mFavoriteStopDetailAdapter;
    private OnFavoriteStopsFragmentInteractionListener mListener;

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
        mRootView.setVisibility(View.INVISIBLE);
        if (mListener != null) {
            ((Activity) mListener).invalidateOptionsMenu();
        }
    }

    public void showView() {
        // FIXME: 8/09/2016 - start CursorLoader
//        Log.v(TAG, "showView");
        isVisible = true;
        mRootView.setVisibility(View.VISIBLE);
        if (mListener != null) {
            ((Activity) mListener).invalidateOptionsMenu();
        }
    }

    // FIXME: 3/10/2016 - remove below
    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume - isVisible: " + isVisible);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        isVisible = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFavoriteStopDetailAdapter = new FavoriteStopsAdapter(
                getActivity().getApplicationContext(),
                null,
                0,
                mListener);

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_favorite_stops, container, false);

        if (mStopDetailsList == null) {
            mStopDetailsList = new ArrayList<>();
        }
        mListView = (ListView) mRootView.findViewById(R.id.favoriteStopsListView);
        mListView.setAdapter(mFavoriteStopDetailAdapter);

        mEmptyView = (TextView) mRootView.findViewById(R.id.emptyView);
        mEmptyView.setText(getActivity().getResources()
                .getString(R.string.no_favorite_stops_selected));
        return mRootView;
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
        if (data.getCount() > 0) {
            mEmptyView.setVisibility(View.GONE);
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

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavoriteStopDetailAdapter.swapCursor(null);
    }

    public void showFavoriteStops() {
        // FIXME: 18/08/2016 add code
        showView();
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
//        Log.v(TAG, "onPrepareOptionsMenu - isVisible: " + isVisible);
        if (isVisible) {
            menu.findItem(R.id.action_train_stops_nearby).setVisible(true);
            menu.findItem(R.id.action_stops_nearby).setVisible(true);
            menu.findItem(R.id.action_disruptions).setVisible(true);
        } else {
            menu.findItem(R.id.action_train_stops_nearby).setVisible(false);
            menu.findItem(R.id.action_stops_nearby).setVisible(false);
            menu.findItem(R.id.action_disruptions).setVisible(false);
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
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Declares callback methods that have to be implemented by parent Activity
     */
//    public interface FavoriteStopsFragmentCallbacks {
//        void startNextDeparturesSearch(StopDetails stopDetails);
//        void showSelectedStopOnMap(LatLonDetails latLonDetails);
//        void startStopsNearbySearch(boolean trainsOnly);
//        void getDisruptionsDetails();
//    }

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
        // TODO: Update argument type and name
        void startNextDeparturesSearch(StopDetails stopDetails);
        void showSelectedStopOnMap(LatLonDetails latLonDetails);
        void startStopsNearbySearch(boolean trainsOnly);
        void getDisruptionsDetails();
        void updateStopDetailRow(int id, String favoriteColumnValue);  // activity should send the removeSelectedStop() below
                                                            // to IntentService
    }


    public void removeSelectedStop(StopDetails stopDetails) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(MptContract.StopDetailEntry.COLUMN_FAVORITE, "n");
        int count = getActivity().getContentResolver().update(
                MptContract.StopDetailEntry.CONTENT_URI, updatedValues, MptContract.StopDetailEntry._ID + "= ?",
                new String [] { String.valueOf(stopDetails.id)});
    }

}
