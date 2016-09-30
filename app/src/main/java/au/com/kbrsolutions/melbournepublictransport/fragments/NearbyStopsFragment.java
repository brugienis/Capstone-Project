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
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link
 * OnNearbyStopsFragmentInteractionListener} interface.
 */
public class NearbyStopsFragment extends Fragment {

    private static final String ARG_NEARBY_DATA = "arg_nearby_data";
    private List<NearbyStopsDetails> mNearbyStopsDetailsList;
    private NearbyStopsAdapter mRecyclerViewAdapter;
    private boolean newInstanceArgsRetrieved;
    private View mRootView;
    private OnNearbyStopsFragmentInteractionListener mListener;

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

    public void hideView() {
        mRootView.setVisibility(View.INVISIBLE);
    }

    public void showView() {
        mRootView.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_nearby_stops, container, false);

        RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.nearbyStopsList);

        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerViewAdapter = new NearbyStopsAdapter(mNearbyStopsDetailsList, mListener);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.requestLayout();
        return mRootView;
    }

    public void setNewContent(List<NearbyStopsDetails> nearbyStopsDetailsList) {
        mRecyclerViewAdapter.swap(nearbyStopsDetailsList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNearbyStopsFragmentInteractionListener) {
            mListener = (OnNearbyStopsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNearbyStopsFragmentInteractionListener");
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
    public interface OnNearbyStopsFragmentInteractionListener {
        // TODO: Update argument type and name
        void onNearbyStopsFragmentMapClicked(NearbyStopsDetails nearbyStopsDetails);
        void startNextDeparturesSearch(StopDetails stopDetails);
    }

}
