package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import au.com.kbrsolutions.melbournepublictransport.data.MptContract;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddStopFragment.AddStopFragmentCallbacks} interface
 * to handle interaction events.
 * Use the {@link AddStopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddStopFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // FIXME: 25/08/2016 check Running a Query with a CursorLoader - https://developer.android.com/training/load-data-background/setup-loader.html

    /**
     * Declares callback methods that have to be implemented by parent Activity
     */
    public interface AddStopFragmentCallbacks {
        void addStop(StopDetails stopDetails);
    }


    AddStopFragmentCallbacks mCallbacks;
    private ListView mListView;
    //    private ArrayAdapter<String> adapter;
    private AvailableStopsDetailsArrayAdapter<StopDetails> availableStopsDetailsArrayAdapter;
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

    private static final int STOP_DETAILS_LOADER = 0;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] STOP_DETAILS_COLUMNS = {
            // FIXME: 30/08/2016 - make sure comments below are correct
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MptContract.StopDetailEntry.TABLE_NAME + "." + MptContract.StopDetailEntry._ID,
            MptContract.StopDetailEntry.COLUMN_STOP_NAME,
            MptContract.StopDetailEntry.COLUMN_FAVORITE,
            MptContract.StopDetailEntry.COLUMN_LATITUDE,
            MptContract.StopDetailEntry.COLUMN_LONGITUDE
    };

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
        getAvailableStopsList();
        availableStopsDetailsArrayAdapter = new AvailableStopsDetailsArrayAdapter<>(getActivity(), mFolderItemList);
        Log.v(TAG, "onCreateView - stopsArrayAdapter/mListView: " + availableStopsDetailsArrayAdapter + "/" + mListView);
        mListView.setAdapter(availableStopsDetailsArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                handleRowClicked(position);
            }
        });

        if (mFolderItemList.size() == 0) {
            mEmptyView = (TextView) rootView.findViewById(R.id.emptyView);
            Log.v(TAG, "onCreateView - mEmptyView: " + mEmptyView);
            mEmptyView.setText(getActivity().getResources()
                    .getString(R.string.no_stops_to_add));
            Log.v(TAG, "onCreateView - showing empty list");
        }
        return rootView;
    }

    private void getAvailableStopsList() {
        mFolderItemList = new ArrayList<>();
        StopDetails stopDetails;
        for (String stopName : stopNames) {
            stopDetails = new StopDetails(stopName);
            mFolderItemList.add(stopDetails);
        }
        return;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(STOP_DETAILS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        // Sort order:  Ascending, by date.
        String sortOrder = MptContract.StopDetailEntry.COLUMN_STOP_NAME + " ASC";

//        String locationSetting = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = MptContract.StopDetailEntry.buildFavoriteStopsUri(MptContract.StopDetailEntry.NON_FAVORITE_FLAG);

        Log.v(TAG, "onCreateLoader - weatherForLocationUri: " + weatherForLocationUri);

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                STOP_DETAILS_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "onLoadFinished -start");
        // FIXME: 30/08/2016 below add correct code
//        mForecastAdapter.swapCursor(data);
//        if (mPosition != ListView.INVALID_POSITION) {
//            // If we don't need to restart the loader, and there's a desired position to restore
//            // to, do so now.
//            mListView.smoothScrollToPosition(mPosition);
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(TAG, "onLoadFinished -start");
        // FIXME: 30/08/onLoaderReset below add correct code
//        mForecastAdapter.swapCursor(null);
    }

    private void handleRowClicked(int position) {
        StopDetails stopDetails = availableStopsDetailsArrayAdapter.getItem(position);
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

    String[] stopNames = new String[] { "Armadale",
            "Aspendale",
            "Bentleigh",
            "Bonbeach",
            "Carrum",
            "Caulfield",
            "Chelsea",
            "Cheltenham",
            "Edithvale",
            "Flagstaff",
            "Flinders Street",
            "Frankston",
            "Glenhuntly",
            "Hawksburn",
            "Highett",
            "Kananook",
            "Malvern",
            "McKinnon",
            "Melbourne Central",
            "Mentone",
            "Moorabbin",
            "Mordialloc",
            "Ormond",
            "Parkdale",
            "Parliament",
            "Patterson",
            "Richmond",
            "Seaford",
            "South Yarra",
            "Southern Cross",
            "Toorak"
    };
}

class AvailableStopsDetailsArrayAdapter<T> extends ArrayAdapter<StopDetails> {

    private TextView stopNameTv;
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

    public AvailableStopsDetailsArrayAdapter(Activity activity, List<StopDetails> objects) {
        super(activity.getApplicationContext(), -1, objects);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //		Log.i(LOC_CAT_TAG, "getView - start");
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.fragment_add_stops_list_view, parent, false);
        }
//            fileImage = (ImageView) v.findViewById(R.id.folderFileImageId);

//        infoImage = (ImageView) v.findViewById(R.id.infoImageId);
//        infoImage.setOnClickListener(folderOnClickListener);

        stopNameTv = (TextView) v.findViewById(R.id.stopNameId);
//        fileUpdateTsTv = (TextView) v.findViewById(R.id.fileUpdateTsId);

        StopDetails folderItem = objects.get(position);
        stopNameTv.setText(folderItem.stopName);
//		Log.i(LOC_CAT_TAG, "getView - name/mIsTrashed: " + folderItem.fileName + "/" + folderItem.mIsTrashed);
        return v;
    }

    String[] stopNames = new String[] { "Armadale",
            "Aspendale",
            "Bentleigh",
            "Bonbeach",
            "Carrum",
            "Caulfield",
            "Chelsea",
            "Cheltenham",
            "Edithvale",
            "Flagstaff",
            "Flinders Street",
            "Frankston",
            "Glenhuntly",
            "Hawksburn",
            "Highett",
            "Kananook",
            "Malvern",
            "McKinnon",
            "Melbourne Central",
            "Mentone",
            "Moorabbin",
            "Mordialloc",
            "Ormond",
            "Parkdale",
            "Parliament",
            "Patterson",
            "Richmond",
            "Seaford",
            "South Yarra",
            "Southern Cross",
            "Toorak"
    };
}
