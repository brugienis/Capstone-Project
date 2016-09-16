package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.fragments.dummy.DummyContent;
import au.com.kbrsolutions.melbournepublictransport.fragments.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnItemFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
//    private int mColumnCount = 1;
    private List<NextDepartureDetails> mNextDepartureDetailsList;
    private OnItemFragmentInteractionListener mListener;

    private static final String TAG = StopDetailAdapter.class.getSimpleName();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
//    public static ItemFragment newInstance(int columnCount) {
    public static ItemFragment newInstance(List<NextDepartureDetails> nextDepartureDetailsList) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelableArrayList(ARG_COLUMN_COUNT, (ArrayList)nextDepartureDetailsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mNextDepartureDetailsList = (ArrayList)getArguments().getParcelableArrayList(ARG_COLUMN_COUNT);
        }
    }

    private MyItemRecyclerViewAdapter mRecyclerViewAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
//            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            }
//            mRecyclerViewAdapter = new MyItemRecyclerViewAdapter(DummyContent.ITEMS, mListener);
            mRecyclerViewAdapter = new MyItemRecyclerViewAdapter(mNextDepartureDetailsList, mListener);
            recyclerView.setAdapter(mRecyclerViewAdapter);
            Log.v(TAG, "onCreateView - mRecyclerViewAdapter: " + mRecyclerViewAdapter);
        }
        return view;
    }

    public void setNewContent() {
        DummyContent.createNewDataSet();
        mRecyclerViewAdapter.notifyDataSetChanged();
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
