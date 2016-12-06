package au.com.kbrsolutions.melbournepublictransport.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.adapters.DisruptionsAdapter;
import au.com.kbrsolutions.melbournepublictransport.adapters.DisruptionsAdapterRv;
import au.com.kbrsolutions.melbournepublictransport.data.DisruptionsDetails;

import static au.com.kbrsolutions.melbournepublictransport.R.id.disruptionsList;

/**
 * This class handles retrieving information about disruptions on the train network.
 *
 * A simple {@link Fragment} subclass.
 *
 */
public class DisruptionsFragment extends BaseFragment {

    private static final String ARG_DISRUPTION_DATA = "disruption_data";
    private List<DisruptionsDetails> mDisruptionDetailsList;
    private DisruptionsAdapter mDisruptionsAdapter;
    private NestedScrollingListView mListView;
    private boolean newInstanceArgsRetrieved;
    private int mDisruptionDetailsCnt;
    private View mCurrentSelectedView;
    private int mCurrentSelectedRow;

    @SuppressWarnings("unused")
    private static final String TAG = DisruptionsFragment.class.getSimpleName();

    public DisruptionsFragment() {
        // Required empty public constructor
    }

    public static DisruptionsFragment newInstance(List<DisruptionsDetails> disruptionDetailsList) {
        DisruptionsFragment fragment = new DisruptionsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_DISRUPTION_DATA, (ArrayList<DisruptionsDetails>) disruptionDetailsList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && !newInstanceArgsRetrieved) {
            mDisruptionDetailsList = (ArrayList)(getArguments().getParcelableArrayList(ARG_DISRUPTION_DATA));
            newInstanceArgsRetrieved = true;
        }
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mRootView = inflater.inflate(R.layout.fragment_disruptions, container, false);

        mDisruptionsAdapter = new DisruptionsAdapter(getActivity(), mDisruptionDetailsList);
        mListView = (NestedScrollingListView) mRootView.findViewById(R.id.disruptionsList);
        mListView.setAdapter(mDisruptionsAdapter);
        TextView emptyView = (TextView) mRootView.findViewById(R.id.emptyView);
        emptyView.setText(getActivity().getResources()
                .getString(R.string.no_disruptions));

        mDisruptionDetailsCnt = mDisruptionDetailsList.size();

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                handleItemSelected(view, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return mRootView;
    }

    /**
     * This is called when D-pad navigation keys are used.
     *
     * @param view
     * @param position
     */
    private void handleItemSelected(View view, int position) {
        if (view.getTag() != null) {
            mCurrentSelectedView = view;
            mCurrentSelectedRow = position;
            DisruptionsAdapter.ViewHolder holder = (DisruptionsAdapter.ViewHolder) mCurrentSelectedView.getTag();
            mListView.clearFocus();
            holder.title.setFocusable(true);
            holder.title.requestFocus();
        }
    }

    public boolean handleVerticalDpadKeys(boolean upKeyPressed) {
        if (upKeyPressed && mCurrentSelectedRow == 0 ||
                !upKeyPressed && mCurrentSelectedRow == mDisruptionDetailsCnt - 1) {
            mCurrentSelectedRow = -1;
            mCurrentSelectedView = null;
        }
        return false;
    }

    /**
     * Ignore horizontal navigation keys.
     * @param rightKeyPressed
     * @return
     */
    @Override
    public boolean handleHorizontalDpadKeys(boolean rightKeyPressed) {
        return false;
    }

    /**
     *
     * This onCreateView is using RecyclerView.Adapter. NOT IN USE now.
     *
     * Use this method in the future when you have more information about how to handle D-pad
     * navigation.
     *
     * see:
     *      https://github.com/vganin/dpad-aware-recycler-view/blob/master/app/src/main/java/net/ganin/darv/sample/SampleActivity.java
     *      https://www.bignerdranch.com/blog/recyclerview-part-1-fundamentals-for-listview-experts/
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
//    @Override
    public View onCreateView_UseWithRecycleViewAdapter(LayoutInflater inflater, ViewGroup container,
                                                       @SuppressWarnings("UnusedParameters") Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_disruptions_rv, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(disruptionsList);

//        recyclerView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Log.i(TAG, "focus has changed I repeat the focus has changed!s - view: " + v);
////                if(currFocus != RECVIEW1){
////                    currFocus = RECVIEW1;
//                    recyclerView.getChildAt(0).requestFocus();
////                }
//            }
//        });

        Context context = recyclerView.getContext();

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(context);
//        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setLayoutManager(mLinearLayoutManager);
        DisruptionsAdapterRv recyclerViewAdapter = new DisruptionsAdapterRv(mDisruptionDetailsList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.requestLayout();
//        mCursorRowCnt = mDisruptionDetailsList.size();
        return view;
    }

    /**
     *
     * Show the latest disruptions details.
     *
     * @param disruptionDetailsList
     */
    public void setNewContent(List<DisruptionsDetails> disruptionDetailsList) {
        mDisruptionsAdapter.swap(disruptionDetailsList);
    }

}
