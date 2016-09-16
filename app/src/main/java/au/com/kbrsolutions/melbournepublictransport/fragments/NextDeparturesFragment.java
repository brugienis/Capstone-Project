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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;

/**
 * Created by business on 12/09/2016.
 */
public class NextDeparturesFragment extends Fragment {

    private ListView mListView;
    private NextDepartureDetailsArrayAdapter<NextDepartureDetails> mNextDeparturesArrayAdapter;
    private List<NextDepartureDetails> mNextDepartureDetailsList;
    private TextView mEmptyView;
    private View rootView;

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public NextDeparturesFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_next_departures, container, false);

        if (mNextDepartureDetailsList == null) {
            mNextDepartureDetailsList = new ArrayList<>();
        }
        mListView = (ListView) rootView.findViewById(R.id.nextDeparturesListView);
        mNextDeparturesArrayAdapter = new NextDepartureDetailsArrayAdapter<>(getActivity(), mNextDepartureDetailsList);
        Log.v(TAG, "onCreateView - mNextDeparturesArrayAdapter/mListView: " + mNextDeparturesArrayAdapter + "/" + mListView);
        mListView.setAdapter(mNextDeparturesArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                handleRowClicked(position);
            }
        });

        mEmptyView = (TextView) rootView.findViewById(R.id.nextDeparturesEmptyView);
        Log.v(TAG, "onCreateView - mEmptyView: " + mEmptyView);
        mEmptyView.setText(getActivity().getResources()
                .getString(R.string.no_next_departures_available));
        Log.v(TAG, "onCreateView - showing empty list");
        return rootView;
    }

    public void addStop(NextDepartureDetails nextDepartureDetails) {
        Log.v(TAG, "addStop - mNextDeparturesArrayAdapter count: " + mNextDeparturesArrayAdapter.getCount());
        if (mNextDepartureDetailsList != null) {
            mNextDeparturesArrayAdapter.add(nextDepartureDetails);
            mListView.clearChoices();    /* will clear previously selected artist row */
            mNextDeparturesArrayAdapter.notifyDataSetChanged(); /* call after clearChoices above */
            Log.v(TAG, "addStop - mNextDeparturesArrayAdapter count: " + mNextDeparturesArrayAdapter.getCount());
            // FIXME: 18/08/2016 handle the line below
//            mListView.setSelection(mArtistsListViewFirstVisiblePosition);
            if (mNextDeparturesArrayAdapter.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }

    private void handleRowClicked(int position) {
        NextDepartureDetails nextDepartureDetails = mNextDeparturesArrayAdapter.getItem(position);
//        mCallbacks.handleSelectedFavoriteStop(nextDepartureDetails);
    }

    public void showFavoriteStops() {
        // FIXME: 18/08/2016 add code
        showView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof FavoriteStopsFragmentCallbacks) {
//            mCallbacks = (FavoriteStopsFragmentCallbacks) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mCallbacks = null;
    }

    private void processSelectedStop(int position) {
        Log.v(TAG, "processSelectedStop - position: " + position);
    }

    private void showSelectedStopOnMap(int position) {
        // FIXME: 8/09/2016 - get lt and lon and show on StationOnMapFragment view
        Log.v(TAG, "showSelectedStopOnMap - position: " + position);
        NextDepartureDetails nextDepartureDetails = mNextDeparturesArrayAdapter.getItem(position);
//        mCallbacks.showSelectedStopOnMap(nextDepartureDetails);
//        Log.v(TAG, "showSelectedStopOnMap - position/lat/lon: " + position + "/" + nextDepartureDetails.latitude + "/" + nextDepartureDetails.longitude);
    }

    public void removeSelectedStop(int position) {
//        Log.v(TAG, "removeSelectedStop - position: " + position);
        // FIXME: 8/09/2016 update table - set favorite flag to 'n'
        mNextDeparturesArrayAdapter.remove(mNextDeparturesArrayAdapter.getItem(position));
        mNextDeparturesArrayAdapter.notifyDataSetChanged();
        if (mNextDeparturesArrayAdapter.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    class NextDepartureDetailsArrayAdapter<T> extends ArrayAdapter<NextDepartureDetails> {

        private TextView destinationTv;
        private TextView departureTimeTv;
        private List<NextDepartureDetails> objects;

        private final String TAG = ((Object) this).getClass().getSimpleName();

        public NextDepartureDetailsArrayAdapter(Activity activity, List<NextDepartureDetails> objects) {
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

            destinationTv = (TextView) v.findViewById(R.id.directionName);
            NextDepartureDetails nextDepartureDetails = objects.get(position);
            destinationTv.setText(nextDepartureDetails.destinationId);
            destinationTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    processSelectedStop(position);
                }
            });

            departureTimeTv = (TextView) v.findViewById(R.id.departureTimeId);
            nextDepartureDetails = objects.get(position);
            departureTimeTv.setText(nextDepartureDetails.utcDepartureTime.toString());
            departureTimeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    processSelecdepartureTimeTvtedStop(position);
                }
            });

            return v;
        }

        private void processSelectedStop(int position) {
            Log.v(TAG, "processSelectedStop");
//            FavoriteStopsFragmentOld.this.processSelectedStop(position);
        }

        private void removeSelectedRow(int position) {
            Log.v(TAG, "removeSelectedRow");
//            FavoriteStopsFragmentOld.this.removeSelectedStop(position);
        }

        private void showSelectedRowOnMap(int position) {
            Log.v(TAG, "showSelectedRowOnMap");
//            FavoriteStopsFragmentOld.this.showSelectedStopOnMap(position);
        }

    }
}
