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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;

/**
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoriteStopsFragment.FavoriteStopsFragmentCallbacks} interface
 * to handle interaction events.
 * Use the {@link FavoriteStopsFragment#newInstance} factory method to
 * create an instance of this fragment.
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
    //    private ArrayAdapter<String> adapter;
    private FolderArrayAdapter<StopDetails> mStopDetailsArrayAdapter;
    private List<StopDetails> mStopDetailsList;
//    private static List<String> favoriteStations = new ArrayList<>();
    private TextView mEmptyView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public FavoriteStopsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoriteStopsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoriteStopsFragment newInstance(String param1, String param2) {
        FavoriteStopsFragment fragment = new FavoriteStopsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void hideView() {
        Log.v(TAG, "hideView");
        rootView.setVisibility(View.INVISIBLE);
    }

    public void showView() {
        Log.v(TAG, "showView");
        rootView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_favorite_stops_list_view, container, false);

        if (mStopDetailsList == null) {
            mStopDetailsList = new ArrayList<>();
        }
        mListView = (ListView) rootView.findViewById(R.id.favoriteStopsListView);
        mStopDetailsArrayAdapter = new FolderArrayAdapter<>(getActivity(), mStopDetailsList);
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
        Log.v(TAG, "addStop - stopDetails: " + stopDetails.stopName);
        Log.v(TAG, "addStop - mStopDetailsList after : " + mStopDetailsList.size());
        if (mStopDetailsList != null) {
            mStopDetailsArrayAdapter.addAll(stopDetails);
            Log.v(TAG, "addStop - mStopDetailsList: " + mStopDetailsList.size() + "/" + mStopDetailsArrayAdapter.isEmpty());
            mListView.clearChoices();    /* will clear previously selected artist row */
            mStopDetailsArrayAdapter.notifyDataSetChanged(); /* call after clearChoices above */
            // FIXME: 18/08/2016 handle the line below
//            mListView.setSelection(mArtistsListViewFirstVisiblePosition);
            if (mStopDetailsArrayAdapter.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
//                mEmptyView.setText();
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

}

//class StopDetails {
//
//    public final String fileName;
//    public final Date fileUpdateTime;
//    public final String mimeType;
//    public final boolean isTrashed;
//    public final int itemIdxInList;
//
//    public StopDetails(String fileName, Date fileUpdateTime, String mimeType, boolean isTrashed, int itemIdxInList) {
//        this.fileName = fileName;
//        this.fileUpdateTime = fileUpdateTime;
//        this.mimeType = mimeType;
//        this.isTrashed = isTrashed;
//        this.itemIdxInList = itemIdxInList;
//    }
//
//}

class FolderArrayAdapter<T> extends ArrayAdapter<StopDetails> {

    private TextView fileNameTv;
    private TextView fileUpdateTsTv;
    //        private ImageView fileImage;
    private ImageView infoImage;
    private List<StopDetails> objects;
    //        private HomeActivity mActivity;
    private View.OnClickListener folderOnClickListener;
    //	@SuppressLint("SimpleDateFormat")
//	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MMM.d hh:mm:ss", Locale.getDefault());
    private final static String LOC_CAT_TAG = "FolderArrayAdapter";

    public FolderArrayAdapter(Activity activity, List<StopDetails> objects) {
        super(activity.getApplicationContext(), -1, objects);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //		Log.i(LOC_CAT_TAG, "getView - start");
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.favorite_stops_list_view, parent, false);
        }
//            fileImage = (ImageView) v.findViewById(R.id.folderFileImageId);

        infoImage = (ImageView) v.findViewById(R.id.infoImageId);
        infoImage.setOnClickListener(folderOnClickListener);

        fileNameTv = (TextView) v.findViewById(R.id.stopNameId);
//        fileUpdateTsTv = (TextView) v.findViewById(R.id.fileUpdateTsId);

        StopDetails folderItem = objects.get(position);
        fileNameTv.setText(folderItem.stopName);
//		Log.i(LOC_CAT_TAG, "getView - name/mIsTrashed: " + folderItem.fileName + "/" + folderItem.mIsTrashed);
        return v;
    }
}
