package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * Show the selected train stop on map.
 *
 * A simple {@link Fragment} subclass.
 * Use the {@link StationOnMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StationOnMapFragment extends BaseFragment implements OnMapReadyCallback {

    private static final String ARG_STOP_NAME = "arg_stop_name";
    private static final String ARG_LATITUDE = "arg_latitude";
    private static final String ARG_LONGITUDE = "arg_longitude";
    private static final String ARG_TWO_PAIN_SCREEN = "arg_two_pain_screen";

    private String mStopName;
    private double mLatitude;
    private double mLongitude;
    private boolean mTwoPainScreen;
    private MapView mMapView;
    private boolean newInstanceArgsRetrieved;
    private GoogleMap mGoogleMap;

    private static final String TAG = StationOnMapFragment.class.getSimpleName();

    public StationOnMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param latitude Parameter 1.
     * @param longitude Parameter 2.
     * @return A new instance of fragment StationOnMapFragment.
     */
    public static StationOnMapFragment newInstance(
            String stopName,
            double latitude,
            double longitude,
            boolean twoPainScreen) {
        StationOnMapFragment fragment = new StationOnMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STOP_NAME, stopName);
        args.putDouble(ARG_LATITUDE, latitude);
        args.putDouble(ARG_LONGITUDE, longitude);
        args.putBoolean(ARG_TWO_PAIN_SCREEN, twoPainScreen);
        fragment.setArguments(args);
        return fragment;
    }

    public void setLatLon(String stopName, double latitude, double longitude) {
        mStopName = stopName;
        mLatitude = latitude;
        mLongitude = longitude;
        if (!mTwoPainScreen) {
            return;
        }
        if (mGoogleMap == null) {
            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showStopOnMap();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleMap = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && !newInstanceArgsRetrieved) {
            mStopName = getArguments().getString(ARG_STOP_NAME);
            mLatitude = getArguments().getDouble(ARG_LATITUDE);
            mLongitude = getArguments().getDouble(ARG_LONGITUDE);
            mTwoPainScreen = getArguments().getBoolean(ARG_TWO_PAIN_SCREEN);
            newInstanceArgsRetrieved = true;
        }
        setRetainInstance(true);
    }

    /**
     *
     * http://stackoverflow.com/questions/19353255/how-to-put-google-maps-v2-on-a-fragment-using-viewpager
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_station_on_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        showStopOnMap();
    }
    private void showStopOnMap() {
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(mLatitude, mLongitude));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        mGoogleMap.moveCamera(center);
        mGoogleMap.animateCamera(zoom);

        addMarker(mGoogleMap, mLatitude, mLongitude,
                mStopName, mStopName);
    }

    private void addMarker(GoogleMap map, double lat, double lon,
                           String title, String snippet) {
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                .title(title)
                .snippet(snippet));
    }

    public boolean handleVerticalDpadKeys(boolean upKeyPressed) {
        return false;
    }

    public boolean handleHorizontalDpadKeys(boolean rightKeyPressed) {
        return false;
    }
}
