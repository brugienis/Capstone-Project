package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.adapters.NearbyStopsAdapter;
import au.com.kbrsolutions.melbournepublictransport.data.NearbyStopsDetails;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class NearbyStopsFragment extends Fragment {

    private static final String ARG_NEARBY_DATA = "arg_nearby_data";
    private List<NearbyStopsDetails> mNearbyStopsDetailsList;
    private NearbyStopsAdapter mRecyclerViewAdapter;
    private boolean newInstanceArgsRetrieved;

    private static final String TAG = StopDetailAdapter.class.getSimpleName();

    public NearbyStopsFragment() {
        // Required empty public constructor
    }

    public static NearbyStopsFragment newInstance(List<NearbyStopsDetails> nearbyStopsDetailsList) {
        NearbyStopsFragment fragment = new NearbyStopsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_NEARBY_DATA, (ArrayList) nearbyStopsDetailsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && !newInstanceArgsRetrieved) {
            mNearbyStopsDetailsList = (ArrayList)getArguments().getParcelableArrayList(ARG_NEARBY_DATA);
            newInstanceArgsRetrieved = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nearby_stops, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.nearbyStopsList);

        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerViewAdapter = new NearbyStopsAdapter(mNearbyStopsDetailsList);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.requestLayout();
        return view;
    }

    public void setNewContent(List<NearbyStopsDetails> nearbyStopsDetailsList) {
        mRecyclerViewAdapter.swap(nearbyStopsDetailsList);
    }

}
