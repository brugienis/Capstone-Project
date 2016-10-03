package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.content.Context;
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
 * A simple {@link Fragment} subclass.
 * Use the {@link StationOnMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StationOnMapFragment extends BaseFragment implements OnMapReadyCallback {

    private static final String ARG_LATITUDE = "arg_latitude";
    private static final String ARG_LONGITUDE = "arg_longitude";

    private double mLatitude;
    private double mLongitude;
    private MapView mMapView;
    private boolean newInstanceArgsRetrieved;

    private static final String TAG = StationOnMapFragment.class.getSimpleName();

    public StationOnMapFragment() {
        // Required empty public constructor
    }

    public void setLatLon(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param latitude Parameter 1.
     * @param longitude Parameter 2.
     * @return A new instance of fragment StationOnMapFragment.
     */
    public static StationOnMapFragment newInstance(double latitude, double longitude) {
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && !newInstanceArgsRetrieved) {
            mLatitude = getArguments().getDouble(ARG_LATITUDE);
            mLongitude = getArguments().getDouble(ARG_LONGITUDE);
            newInstanceArgsRetrieved = true;
        }
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
        CameraUpdate center =
                CameraUpdateFactory.newLatLng(new LatLng(mLatitude, mLongitude));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);

        addMarker(googleMap, mLatitude, mLongitude,
                R.string.un, R.string.united_nations);
    }

    private void addMarker(GoogleMap map, double lat, double lon,
                           int title, int snippet) {
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                .title(getString(title))
                .snippet(getString(snippet)));
    }
}
