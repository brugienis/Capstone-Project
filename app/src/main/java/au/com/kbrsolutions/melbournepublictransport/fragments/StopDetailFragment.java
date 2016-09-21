package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.content.ContentValues;
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
import au.com.kbrsolutions.melbournepublictransport.data.MptContract;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StopDetailFragment.AddStopFragmentCallbacks} interface
 * to handle interaction events.
 */
public class StopDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // FIXME: 25/08/2016 check Running a Query with a CursorLoader - https://developer.android.com/training/load-data-background/setup-loader.html

    /**
     * Declares callback methods that have to be implemented by parent Activity
     */
    public interface AddStopFragmentCallbacks {
//        void addStop(StopDetails stopDetails);
        void addStop();
        void showSelectedStopOnMap(StopDetails stopDetails);
    }

    AddStopFragmentCallbacks mCallbacks;
    private ListView mListView;
    private StopDetailAdapter mStopDetailAdapter;
    private static List<StopDetails> mFolderItemList = new ArrayList<>();
    private TextView mEmptyView;

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

    public StopDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        mStopDetailAdapter = new StopDetailAdapter(getActivity(), null, 0);
        mStopDetailAdapter = new StopDetailAdapter(this, null, 0);
        View rootView = inflater.inflate(R.layout.fragment_add_stop, container, false);

        mListView = (ListView) rootView.findViewById(R.id.addStopsListView);
        mListView.setAdapter(mStopDetailAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // FIXME: 8/09/2016 - run update to change 'favorite' flag to 'y'
                handleRowSelected(adapterView, position);
            }
        });

        if (mFolderItemList.size() == 0) {
            mEmptyView = (TextView) rootView.findViewById(R.id.emptyView);
//            Log.v(TAG, "onCreateView - mEmptyView: " + mEmptyView);
            mEmptyView.setText(getActivity().getResources()
                    .getString(R.string.no_stops_to_add));
//            Log.v(TAG, "onCreateView - showing empty list");
        }
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
            ContentValues updatedValues = new ContentValues();
            updatedValues.put(MptContract.StopDetailEntry.COLUMN_FAVORITE, "y");
            int count = getActivity().getContentResolver().update(
                    MptContract.StopDetailEntry.CONTENT_URI, updatedValues, MptContract.StopDetailEntry._ID + "= ?",
                    new String [] { String.valueOf(cursor.getInt(COL_STOP_DETAILS_ID))});
            mCallbacks.addStop();
        }
    }

    void handleMapClicked(StopDetails stopDetails) {
//        Log.v(TAG, "handleMapClicked - locationName: " + stopDetails.locationName);
        mCallbacks.showSelectedStopOnMap(stopDetails);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddStopFragmentCallbacks) {
            mCallbacks = (AddStopFragmentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AddStopFragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}