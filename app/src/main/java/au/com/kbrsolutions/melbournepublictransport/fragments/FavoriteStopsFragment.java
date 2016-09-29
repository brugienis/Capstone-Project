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
import au.com.kbrsolutions.melbournepublictransport.data.LatLonDetails;
import au.com.kbrsolutions.melbournepublictransport.data.MptContract;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;

/**
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoriteStopsFragment.FavoriteStopsFragmentCallbacks} interface
 * to handle interaction events.
 */
public class FavoriteStopsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Declares callback methods that have to be implemented by parent Activity
     */
    public interface FavoriteStopsFragmentCallbacks {
        void handleSelectedFavoriteStop(StopDetails stopDetails);
        void showSelectedStopOnMap(LatLonDetails latLonDetails);
        void startStopsNearbySearch(boolean trainsOnly);
        void getDisruptionsDetails();
    }

    private FavoriteStopsFragmentCallbacks mCallbacks;
    private ListView mListView;
    private List<StopDetails> mStopDetailsList;
    private TextView mEmptyView;
    private View rootView;
    private boolean isVisible;
    private FavoriteStopDetailAdapter mFavoriteStopDetailAdapter;

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
    static final int COL_STOP_DETAILS_ID = 0;
    static final int COL_STOP_DETAILS_ROUTE_TYPE = 1;
    static final int COL_STOP_DETAILS_STOP_ID = 2;
    static final int COL_STOP_DETAILS_LOCATION_NAME = 3;
    static final int COL_STOP_DETAILS_LATITUDE = 4;
    static final int COL_STOP_DETAILS_LONGITUDE = 5;
    static final int COL_STOP_DETAILS_FAVORITE = 6;

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public FavoriteStopsFragment() {
        // Required empty public constructor
    }

    public void hideView() {
        isVisible = false;
        rootView.setVisibility(View.INVISIBLE);
        ((Activity) mCallbacks).invalidateOptionsMenu();
    }

    public void showView() {
        // FIXME: 8/09/2016 - start CursorLoader
//        Log.v(TAG, "showView");
        isVisible = true;
        rootView.setVisibility(View.VISIBLE);
        ((Activity) mCallbacks).invalidateOptionsMenu();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        isVisible = true;
    }

    // FIXME: 19/08/2016 use Holder pattern or use RecyclerView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFavoriteStopDetailAdapter = new FavoriteStopDetailAdapter(this, null, 0);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_favorite_stops, container, false);

        if (mStopDetailsList == null) {
            mStopDetailsList = new ArrayList<>();
        }
        mListView = (ListView) rootView.findViewById(R.id.favoriteStopsListView);
        mListView.setAdapter(mFavoriteStopDetailAdapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                handleRowClicked(adapterView, position);
//            }
//        });

        mEmptyView = (TextView) rootView.findViewById(R.id.emptyView);
//        Log.v(TAG, "onCreateView - mEmptyView: " + mEmptyView);
        mEmptyView.setText(getActivity().getResources()
                .getString(R.string.no_favorite_stops_selected));
//        Log.v(TAG, "onCreateView - showing empty list");
        return rootView;
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
//        Log.v(TAG, "onLoadFinished - start");
            mFavoriteStopDetailAdapter.swapCursor(null);
        }

//    public void handleRowClicked(AdapterView<?> adapterView, int position) {
//        // CursorAdapter returns a cursor at the correct position for getItem(), or null
//        // if it cannot seek to that position.
//        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
//        if (cursor != null) {
//            mCallbacks.handleSelectedFavoriteStop(new StopDetails(
//                    cursor.getInt(COL_STOP_DETAILS_ID),
//                    cursor.getInt(COL_STOP_DETAILS_ROUTE_TYPE),
//                    cursor.getString(COL_STOP_DETAILS_STOP_ID),
//                    cursor.getString(COL_STOP_DETAILS_LOCATION_NAME),
//                    cursor.getDouble(COL_STOP_DETAILS_LATITUDE),
//                    cursor.getDouble(COL_STOP_DETAILS_LONGITUDE),
//                    cursor.getString(COL_STOP_DETAILS_FAVORITE)));
//        }
//    }

    public void handleNextDeparturesClicked(StopDetails stopDetails) {
            mCallbacks.handleSelectedFavoriteStop(stopDetails);
    }

    void handleMapClicked(StopDetails stopDetails) {
//        Log.v(TAG, "handleMapClicked - locationName: " + stopDetails.locationName);
//        mCallbacks.showSelectedStopOnMap(stopDetails);
        mCallbacks.showSelectedStopOnMap(new LatLonDetails(stopDetails.latitude, stopDetails.longitude));
    }

    public void showFavoriteStops() {
        // FIXME: 18/08/2016 add code
        showView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FavoriteStopsFragmentCallbacks) {
            mCallbacks = (FavoriteStopsFragmentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

//    private void processSelectedStop(int position) {
//        Log.v(TAG, "processSelectedStop - position: " + position);
//    }

    public void removeSelectedStop(StopDetails stopDetails) {
//        Log.v(TAG, "removeSelectedStop - stopDetails: " + stopDetails);
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(MptContract.StopDetailEntry.COLUMN_FAVORITE, "n");
        int count = getActivity().getContentResolver().update(
                MptContract.StopDetailEntry.CONTENT_URI, updatedValues, MptContract.StopDetailEntry._ID + "= ?",
                new String [] { String.valueOf(stopDetails.id)});
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.v(TAG, "onPrepareOptionsMenu - isVisible: " + isVisible);
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
        Log.v(TAG, "onCreateOptionsMenu - isVisible: " + isVisible);
        inflater.inflate(R.menu.menu_favorite_stops, menu);
//        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(TAG, "onOptionsItemSelected - isVisible: " + isVisible);
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_train_stops_nearby) {
            mCallbacks.startStopsNearbySearch(true);
//            startActivity(new Intent(this, StopsNearbyActivity.class));
            return true;
        } else if (id == R.id.action_stops_nearby) {
            mCallbacks.startStopsNearbySearch(false);
//            startActivity(new Intent(this, StopsNearbyActivity.class));
            return true;
        } else if (id == R.id.action_disruptions) {
            mCallbacks.getDisruptionsDetails();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
