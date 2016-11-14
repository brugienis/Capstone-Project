package au.com.kbrsolutions.melbournepublictransport.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import au.com.kbrsolutions.melbournepublictransport.utilities.Utility;

import static au.com.kbrsolutions.melbournepublictransport.R.id.disruptionsList;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class DisruptionsFragment extends BaseFragment {

    private static final String ARG_DISRUPTION_DATA = "disruption_data";
    private RecyclerView recyclerView;
    private List<DisruptionsDetails> mDisruptionDetailsList;
    private DisruptionsAdapter mDisruptionsAdapter;
    private NestedScrollingListView mListView;
    private TextView mEmptyView;
    private DisruptionsAdapterRv mRecyclerViewAdapter;
    private boolean newInstanceArgsRetrieved;

    private static final String TAG = DisruptionsFragment.class.getSimpleName();

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
        setRetainInstance(true);
    }

    // FIXME: 12/11/2016  - see https://github.com/vganin/dpad-aware-recycler-view/blob/master/app/src/main/java/net/ganin/darv/sample/SampleActivity.java


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mRootView = inflater.inflate(R.layout.fragment_disruptions, container, false);

        mDisruptionsAdapter = new DisruptionsAdapter(getActivity(), mDisruptionDetailsList);
        mListView = (NestedScrollingListView) mRootView.findViewById(R.id.disruptionsList);
        mListView.setAdapter(mDisruptionsAdapter);
        mEmptyView = (TextView) mRootView.findViewById(R.id.emptyView);
        mEmptyView = (TextView) mRootView.findViewById(R.id.emptyView);
        mEmptyView.setText(getActivity().getResources()
                .getString(R.string.no_favorite_stops_selected));

        mDisruptionDetailsCnt = mDisruptionDetailsList.size();

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v(TAG, "onItemSelected - position/view: " + position + "/" + Utility.getClassHashCode(view));
                handleItemSelected(view, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.v(TAG, "onNothingSelected - position: " + parent);
            }
        });
        return mRootView;
    }

    private int mDisruptionDetailsCnt;
    private View mCurrentSelectedView;
    private int mCurrentSelectedRow;
    private int selectableViewsCnt = 2;
    private int selectedViewNo = -1;

    private void handleItemSelected(View view, int position) {
        if (view.getTag() != null) {
            mCurrentSelectedView = view;
            mCurrentSelectedRow = position;
            selectedViewNo = 0;
            DisruptionsAdapter.ViewHolder holder = (DisruptionsAdapter.ViewHolder) mCurrentSelectedView.getTag();
            mListView.clearFocus();
            holder.title.setFocusable(true);
            holder.title.requestFocus();
            Log.v(TAG, "handleItemSelected - title in focus");
        }
    }

    public boolean handleVerticalDpadKeys(boolean upKeyPressed) {
        Log.v(TAG, "handleVerticalDpadKeys - start - mCurrentSelectedRow/mNextDepartureDetailsCnt: " + mCurrentSelectedRow + "/" + mDisruptionDetailsCnt);
        if (upKeyPressed && mCurrentSelectedRow == 0 ||
                !upKeyPressed && mCurrentSelectedRow == mDisruptionDetailsCnt - 1) {
            mCurrentSelectedRow = -1;
            mCurrentSelectedView = null;
            Log.v(TAG, "handleVerticalDpadKeys - moved above 1st. row");
        }
        Log.v(TAG, "handleVerticalDpadKeys - end - mCurrentSelectedRow/mNextDepartureDetailsCnt: " + mCurrentSelectedRow + "/" + mDisruptionDetailsCnt);
        return false;
    }
    @Override
    public boolean handleHorizontalDpadKeys(boolean rightKeyPressed) {
        Log.v(TAG, "handleHorizontalDpadKeys - : ");
        return false;
    }

//    @Override
    public View onCreateView_UseWithRececleViewAdapter(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_disruptions_rv, container, false);

        recyclerView = (RecyclerView) view.findViewById(disruptionsList);

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
        mRecyclerViewAdapter = new DisruptionsAdapterRv(mDisruptionDetailsList);
        recyclerView.setAdapter(mRecyclerViewAdapter);
        recyclerView.requestLayout();
//        mCursorRowCnt = mDisruptionDetailsList.size();
        return view;
    }

    public void setNewContent(List<DisruptionsDetails> disruptionDetailsList) {
//        mRecyclerViewAdapter.swap(disruptionDetailsList);
        mDisruptionsAdapter.swap(disruptionDetailsList);
    }

//    @Override
//    public boolean handleVerticalDpadKeys(boolean upKeyPressed) {
//        Log.v(TAG, "handleVerticalDpadKeys - has focus/currPosition: " + currPosition + "/" + mDisruptionDetailsList.size() + "/" + recyclerView.hasFocus());
////        if (recyclerView.hasFocus() && currPosition == 0) {
////            mLinearLayoutManager.scrollToPosition(currPosition++);
////        }
////        if (recyclerView.hasFocus()) {
//            if (upKeyPressed) {
//                if (currPosition > 0) {
//                    mLinearLayoutManager.scrollToPosition(currPosition--);
//                }
//            } else {
//                if (currPosition < mDisruptionDetailsList.size()) {
//                    mLinearLayoutManager.scrollToPosition(currPosition++);
//                }
//            }
////        }
//        return false;
//    }

}
