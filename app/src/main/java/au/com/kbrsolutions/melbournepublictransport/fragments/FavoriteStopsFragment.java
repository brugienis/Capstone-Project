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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
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
        void handleSelectedStop(StopDetails stopDetails);
        void showSelectedStopOnMap(StopDetails stopDetails);
    }

    private FavoriteStopsFragmentCallbacks mCallbacks;
    private ListView mListView;
    private StopDetailsArrayAdapter<StopDetails> mStopDetailsArrayAdapter;
    private List<StopDetails> mStopDetailsList;
    private TextView mEmptyView;
    private View rootView;
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
//        Log.v(TAG, "hideView");
        rootView.setVisibility(View.INVISIBLE);
    }

    public void showView() {
        // FIXME: 8/09/2016 - start CursorLoader
//        Log.v(TAG, "showView");
        rootView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // FIXME: 19/08/2016 use Holder pattern
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
        mStopDetailsArrayAdapter = new StopDetailsArrayAdapter<>(getActivity(), mStopDetailsList);
//        Log.v(TAG, "onCreateView - mStopDetailsArrayAdapter/mListView: " + mStopDetailsArrayAdapter + "/" + mListView);
//        mListView.setAdapter(mStopDetailsArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                handleRowClicked(position);
            }
        });

        mEmptyView = (TextView) rootView.findViewById(R.id.emptyView);
        Log.v(TAG, "onCreateView - mEmptyView: " + mEmptyView);
        mEmptyView.setText(getActivity().getResources()
                .getString(R.string.no_favorite_stops_selected));
        Log.v(TAG, "onCreateView - showing empty list");
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

//        Log.v(TAG, "onCreateLoader - nonFavoriteStopDetailUri: " + nonFavoriteStopDetailUri);

            // FIXME: 8/09/2016 try to get distinct columns to get rid of duplicates - see
            // http://stackoverflow.com/questions/24877815/distinct-query-for-cursorloader
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

    public void addStop(StopDetails stopDetails) {
        if (mStopDetailsList != null) {
            mStopDetailsArrayAdapter.add(stopDetails);
            mListView.clearChoices();    /* will clear previously selected artist row */
            mStopDetailsArrayAdapter.notifyDataSetChanged(); /* call after clearChoices above */
            // FIXME: 18/08/2016 handle the line below
//            mListView.setSelection(mArtistsListViewFirstVisiblePosition);
            if (mStopDetailsArrayAdapter.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }

    private void handleRowClicked(int position) {
        StopDetails stopDetails = mStopDetailsArrayAdapter.getItem(position);
        mCallbacks.handleSelectedStop(stopDetails);
    }

    void handleMapClicked(StopDetails stopDetails) {
//        Log.v(TAG, "handleMapClicked - locationName: " + stopDetails.locationName);
        mCallbacks.showSelectedStopOnMap(stopDetails);
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

    private void processSelectedStop(int position) {
        Log.v(TAG, "processSelectedStop - position: " + position);
    }

    private void showSelectedStopOnMap(int position) {
        // FIXME: 8/09/2016 - get lt and lon and show on StationOnMapFragment view
        Log.v(TAG, "showSelectedStopOnMap - position: " + position);
        StopDetails stopDetails = mStopDetailsArrayAdapter.getItem(position);
        mCallbacks.showSelectedStopOnMap(stopDetails);
        Log.v(TAG, "showSelectedStopOnMap - position/lat/lon: " + position + "/" + stopDetails.latitude + "/" + stopDetails.longitude);
    }

    public void removeSelectedStop(int position) {
        Log.v(TAG, "removeSelectedStop - position: " + position);
        // FIXME: 8/09/2016 update table - set favorite flag to 'n'
        mStopDetailsArrayAdapter.remove(mStopDetailsArrayAdapter.getItem(position));
        mStopDetailsArrayAdapter.notifyDataSetChanged();
        if (mStopDetailsArrayAdapter.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    public void removeSelectedStop(StopDetails stopDetails) {
        Log.v(TAG, "removeSelectedStop - stopDetails: " + stopDetails);
        // FIXME: 8/09/2016 update table - set favorite flag to 'n'
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(MptContract.StopDetailEntry.COLUMN_FAVORITE, "n");
        int count = getActivity().getContentResolver().update(
                MptContract.StopDetailEntry.CONTENT_URI, updatedValues, MptContract.StopDetailEntry._ID + "= ?",
                new String [] { String.valueOf(stopDetails.id)});
    }

    class StopDetailsArrayAdapter<T> extends ArrayAdapter<StopDetails> {

        private TextView stopNameTv;
        private List<StopDetails> objects;

        private final String TAG = ((Object) this).getClass().getSimpleName();

        public StopDetailsArrayAdapter(Activity activity, List<StopDetails> objects) {
            super(activity.getApplicationContext(), -1, objects);
            this.objects = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //		Log.i(LOC_CAT_TAG, "getView - start");
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.fragment_favorite_stops_list_view, parent, false);
            }

            stopNameTv = (TextView) v.findViewById(R.id.locationNameId);
            StopDetails folderItem = objects.get(position);
            stopNameTv.setText(folderItem.locationName);
            stopNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    processSelectedStop(position);
                }
            });

            ImageView mapImageId = (ImageView) v.findViewById(R.id.mapImageId);
            mapImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSelectedRowOnMap(position);
                }
            });

            ImageView garbageInfoImage = (ImageView) v.findViewById(R.id.garbageImageId);
            garbageInfoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeSelectedRow(position);
                }
            });

            return v;
        }

        private void processSelectedStop(int position) {
            Log.v(TAG, "processSelectedStop");
            FavoriteStopsFragment.this.processSelectedStop(position);
        }

        private void removeSelectedRow(int position) {
            Log.v(TAG, "removeSelectedRow");
            FavoriteStopsFragment.this.removeSelectedStop(position);
        }

        private void showSelectedRowOnMap(int position) {
            Log.v(TAG, "showSelectedRowOnMap");
            FavoriteStopsFragment.this.showSelectedStopOnMap(position);
        }

    }
}
