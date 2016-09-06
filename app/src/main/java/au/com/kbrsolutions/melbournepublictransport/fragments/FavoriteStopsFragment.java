package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;

/**
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoriteStopsFragment.FavoriteStopsFragmentCallbacks} interface
 * to handle interaction events.
 */
public class FavoriteStopsFragment extends Fragment {

    /**
     * Declares callback methods that have to be implemented by parent Activity
     */
    public interface FavoriteStopsFragmentCallbacks {
        void handleSelectedStop(StopDetails stopDetails);
    }

    private FavoriteStopsFragmentCallbacks mCallbacks;
    private ListView mListView;
    private StopDetailsArrayAdapter<StopDetails> mStopDetailsArrayAdapter;
    private List<StopDetails> mStopDetailsList;
    private TextView mEmptyView;
    private View rootView;

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public FavoriteStopsFragment() {
        // Required empty public constructor
    }

    public void hideView() {
//        Log.v(TAG, "hideView");
        rootView.setVisibility(View.INVISIBLE);
    }

    public void showView() {
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
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_favorite_stops_list_view, container, false);

        if (mStopDetailsList == null) {
            mStopDetailsList = new ArrayList<>();
        }
        mListView = (ListView) rootView.findViewById(R.id.favoriteStopsListView);
        mStopDetailsArrayAdapter = new StopDetailsArrayAdapter<>(getActivity(), mStopDetailsList);
        Log.v(TAG, "onCreateView - mStopDetailsArrayAdapter/mListView: " + mStopDetailsArrayAdapter + "/" + mListView);
        mListView.setAdapter(mStopDetailsArrayAdapter);
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

    private void showSelectedRowOnMap(int position) {
        Log.v(TAG, "showSelectedRowOnMap - position: " + position);
    }

    public void removeSelectedStop(int position) {
//        Log.v(TAG, "removeSelectedStop - position: " + position);
        mStopDetailsArrayAdapter.remove(mStopDetailsArrayAdapter.getItem(position));
        mStopDetailsArrayAdapter.notifyDataSetChanged();
        if (mStopDetailsArrayAdapter.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        }
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
                v = inflater.inflate(R.layout.favorite_stops_list_view, parent, false);
            }

            stopNameTv = (TextView) v.findViewById(R.id.stopNameId);
            StopDetails folderItem = objects.get(position);
            stopNameTv.setText(folderItem.locationName);
            stopNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    processSelectedStop(position);
                }
            });

            ImageView mapImageId = (ImageView) v.findViewById(R.id.garbageImageId);
            mapImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeSelectedRow(position);
                }
            });

            ImageView garbageInfoImage = (ImageView) v.findViewById(R.id.mapImageId);
            garbageInfoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSelectedRowOnMap(position);
                }
            });

            return v;
        }

        private void processSelectedStop(int position) {
            FavoriteStopsFragment.this.processSelectedStop(position);
        }

        private void removeSelectedRow(int position) {
            FavoriteStopsFragment.this.removeSelectedStop(position);
        }

        private void showSelectedRowOnMap(int position) {
            FavoriteStopsFragment.this.showSelectedRowOnMap(position);
        }

    }
}
