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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.adapters.StopsAdapter;
import au.com.kbrsolutions.melbournepublictransport.data.LatLonDetails;
import au.com.kbrsolutions.melbournepublictransport.data.MptContract;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StopsFragment.OnStopFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class StopsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // FIXME: 25/08/2016 check Running a Query with a CursorLoader - https://developer.android.com/training/load-data-background/setup-loader.html

    private ListView mListView;
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mStopDetailAdapter = new StopsAdapter(getActivity().getApplicationContext(), null, 0, mListener);
        mRootView = inflater.inflate(R.layout.fragment_add_stop, container, false);

        mListView = (ListView) mRootView.findViewById(R.id.addStopsListView);
        mListView.setAdapter(mStopDetailAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // FIXME: 8/09/2016 - run update to change 'favorite' flag to 'y'
                handleRowSelected(adapterView, position);
            }
        });

        if (mFolderItemList.size() == 0) {
            mEmptyView = (TextView) mRootView.findViewById(R.id.emptyView);
            mEmptyView.setText(getActivity().getResources()
                    .getString(R.string.no_stops_to_add));
        }
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
        mStopDetailAdapter.swapCursor(data);
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
        mStopDetailAdapter.swapCursor(null);
    }

    private void handleRowSelected(AdapterView<?> adapterView, int position) {
        // CursorAdapter returns a cursor at the correct position for getItem(), or null
        // if it cannot seek to that position.
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        if (cursor != null) {
            // FIXME: 2/10/2016 - call below to update row
            String favoriteValue = "y";
            mListener.updateStopDetailRow(cursor.getInt(COL_STOP_DETAILS_ID), favoriteValue);
//            ContentValues updatedValues = new ContentValues();
//            updatedValues.put(MptContract.StopDetailEntry.COLUMN_FAVORITE, "y");
//            int count = getActivity().getContentResolver().update(
//                    MptContract.StopDetailEntry.CONTENT_URI, updatedValues, MptContract.StopDetailEntry._ID + "= ?",
//                    new String [] { String.valueOf(cursor.getInt(COL_STOP_DETAILS_ID))});
            mListener.showUpdatedFavoriteStops();
        }
    }

    public void handleMapClicked(StopDetails stopDetails) {
        mListener.showSelectedStopOnMap(new LatLonDetails(stopDetails.latitude, stopDetails.longitude));
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
        void showSelectedStopOnMap(LatLonDetails latLonDetails);
        void updateStopDetailRow(int id, String favoriteColumnValue);  // activity should send the removeSelectedStop() below
        void startNextDeparturesSearch(StopDetails stopDetails);
        // to IntentService
    }
}
