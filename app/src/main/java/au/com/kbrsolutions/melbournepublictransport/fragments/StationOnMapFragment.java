package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StationOnMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StationOnMapFragment extends Fragment implements OnMapReadyCallback {

    /**
     * Declares callback methods that have to be implemented by parent Activity
     */
    public interface StationOnMapCallbacks {
        StopDetails getCurrSelectedStopDetails();
    }

    private StationOnMapCallbacks mCallbacks;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LATITUDE = "arg_latitude";
    private static final String ARG_LONGITUDE = "arg_longitude";

    // TODO: Rename and change types of parameters
    private double mLatitude;
    private double mLongitude;

    private boolean needsInit=false;
    private MapView mMapView;
    private GoogleMap googleMap;

    private static final String TAG = StationOnMapFragment.class.getSimpleName();

    /* callback if needed */
//    private OnFragmentInteractionListener mListener;

    public StationOnMapFragment() {
        // Required empty public constructor
    }

    public void setLatLon(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
        Log.v(TAG, "setLatLon - lat/lon: " + mLatitude + "/" + mLongitude);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param latitude Parameter 1.
     * @param longitude Parameter 2.
     * @return A new instance of fragment StationOnMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StationOnMapFragment newInstance(double latitude, double longitude) {
        Log.v(TAG, "newInstance - lat/lon: " + latitude + "/" + longitude);
        StationOnMapFragment fragment = new StationOnMapFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StationOnMapCallbacks) {
            mCallbacks = (StationOnMapCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement StationOnMapFragmentCallbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mLatitude = getArguments().getDouble(ARG_LATITUDE);
//            mLongitude = getArguments().getDouble(ARG_LONGITUDE);
            setLatitude(getArguments().getDouble(ARG_LATITUDE));
            setLongitude(getArguments().getDouble(ARG_LONGITUDE));
        }
    }

    /**
     *
     * http://stackoverflow.com/questions/19353255/how-to-put-google-maps-v2-on-a-fragment-using-viewpager
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_station_on_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        needsInit = true;
        mMapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        double latitude = getLatitude();
//        double longitude = getLongitude();
        StopDetails stopDetails = mCallbacks.getCurrSelectedStopDetails();
        double latitude = stopDetails.latitude;
        double longitude = stopDetails.longitude;
        Log.v(TAG, "onMapReady called - lat/lon: " + latitude + "/" + longitude);
//        if (needsInit) {
            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
            CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

            googleMap.moveCamera(center);
            googleMap.animateCamera(zoom);
//        }

        addMarker(googleMap, latitude, longitude,
                R.string.un, R.string.united_nations);
    }

    private void addMarker(GoogleMap map, double lat, double lon,
                           int title, int snippet) {
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                .title(getString(title))
                .snippet(getString(snippet)));
    }

    public synchronized double getLongitude() {
        return mLongitude;
    }

    public synchronized void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public synchronized double getLatitude() {
        return mLatitude;
    }

    public synchronized void setLatitude(double latitude) {
        mLatitude = latitude;
    }
}
