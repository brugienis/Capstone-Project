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
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.fragments.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnItemFragmentInteractionListener}
 * interface.
 */
public class NextDeparturesFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_NEXT_DEPARTURE_DATA = "column-count";
    private List<NextDepartureDetails> mNextDepartureDetailsList;
    private OnItemFragmentInteractionListener mListener;
    private NextDepartureAdapter mRecyclerViewAdapter;

    private static final String TAG = StopDetailAdapter.class.getSimpleName();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NextDeparturesFragment() {
    }

    public static NextDeparturesFragment newInstance(List<NextDepartureDetails> nextDepartureDetailsList) {
        NextDeparturesFragment fragment = new NextDeparturesFragment();
        Bundle args = new Bundle();;
        args.putParcelableArrayList(ARG_NEXT_DEPARTURE_DATA, (ArrayList)nextDepartureDetailsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mNextDepartureDetailsList = (ArrayList)getArguments().getParcelableArrayList(ARG_NEXT_DEPARTURE_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_next_departure, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerViewAdapter = new NextDepartureAdapter(mNextDepartureDetailsList, mListener);
            recyclerView.setAdapter(mRecyclerViewAdapter);
        }
        return view;
    }

    public void setNewContent(List<NextDepartureDetails> nextDepartureDetailsList) {
        mRecyclerViewAdapter.swap(nextDepartureDetailsList);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemFragmentInteractionListener) {
            mListener = (OnItemFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
    public interface OnItemFragmentInteractionListener {
        // TODO: Update argument type and name
        void onItemFragmentInteractionListener(DummyItem item);
    }
}
