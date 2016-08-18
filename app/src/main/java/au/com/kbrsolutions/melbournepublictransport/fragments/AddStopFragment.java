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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddStopFragment.AddStopFragmentCallbacks} interface
 * to handle interaction events.
 * Use the {@link AddStopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddStopFragment extends Fragment {

    /**
     * Declares callback methods that have to be implemented by parent Activity
     */
    public interface AddStopFragmentCallbacks {
        void addStop(StopDetails stopDetails);
    }


    AddStopFragmentCallbacks mCallbacks;
    private ListView mListView;
    //    private ArrayAdapter<String> adapter;
    private StopsDetailsArrayAdapter<StopDetails> stopsDetailsArrayAdapter;
    private static List<StopDetails> mFolderItemList = new ArrayList<>();
    private static List<String> favoriteStations = new ArrayList<>();
    private TextView mEmptyView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public AddStopFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddStopFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddStopFragment newInstance(String param1, String param2) {
        AddStopFragment fragment = new AddStopFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_stop, container, false);

        mListView = (ListView) rootView.findViewById(R.id.addStopsListView);
        stopsDetailsArrayAdapter = new StopsDetailsArrayAdapter<>(getActivity(), mFolderItemList);
        Log.v(TAG, "onCreateView - stopsArrayAdapter/mListView: " + stopsDetailsArrayAdapter + "/" + mListView);
        mListView.setAdapter(stopsDetailsArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                handleRowClicked(position);
            }
        });

        mEmptyView = (TextView) rootView.findViewById(R.id.emptyView);
        Log.v(TAG, "onCreateView - mEmptyView: " + mEmptyView);
        mEmptyView.setText(getActivity().getResources()
                .getString(R.string.no_stops_to_add));
        Log.v(TAG, "onCreateView - showing empty list");
        return rootView;
    }

    private void handleRowClicked(int position) {
        StopDetails stopDetails = stopsDetailsArrayAdapter.getItem(position);
        mCallbacks.addStop(stopDetails);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddStopFragmentCallbacks) {
            mCallbacks = (AddStopFragmentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AddStopFragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}

class StopsDetailsArrayAdapter<T> extends ArrayAdapter<StopDetails> {

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

    public StopsDetailsArrayAdapter(Activity activity, List<StopDetails> objects) {
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

        fileNameTv = (TextView) v.findViewById(R.id.fileNameId);
//        fileUpdateTsTv = (TextView) v.findViewById(R.id.fileUpdateTsId);

        StopDetails folderItem = objects.get(position);
        fileNameTv.setText(folderItem.stopName);
//		Log.i(LOC_CAT_TAG, "getView - name/mIsTrashed: " + folderItem.fileName + "/" + folderItem.mIsTrashed);
        return v;
    }
}
