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
import au.com.kbrsolutions.melbournepublictransport.adapters.DisruptionsAdapter;
import au.com.kbrsolutions.melbournepublictransport.data.DisruptionsDetails;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class DisruptionsFragment extends Fragment {

    private static final String ARG_DISRUPTION_DATA = "disruption_data";
    private List<DisruptionsDetails> mDisruptionDetailsList;
    private DisruptionsAdapter mRecyclerViewAdapter;
    private boolean newInstanceArgsRetrieved;

    private static final String TAG = StopDetailAdapter.class.getSimpleName();

    public DisruptionsFragment() {
        // Required empty public constructor
    }

    public static DisruptionsFragment newInstance(List<DisruptionsDetails> disruptionDetailsList) {
        DisruptionsFragment fragment = new DisruptionsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_DISRUPTION_DATA, (ArrayList) disruptionDetailsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && !newInstanceArgsRetrieved) {
            mDisruptionDetailsList = (ArrayList)getArguments().getParcelableArrayList(ARG_DISRUPTION_DATA);
            newInstanceArgsRetrieved = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_disruptions, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.disruptionsList);

        Context context = recyclerView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerViewAdapter = new DisruptionsAdapter(mDisruptionDetailsList);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.requestLayout();
        return view;
    }

    public void setNewContent(List<DisruptionsDetails> disruptionDetailsList) {
        mRecyclerViewAdapter.swap(disruptionDetailsList);
    }

}
