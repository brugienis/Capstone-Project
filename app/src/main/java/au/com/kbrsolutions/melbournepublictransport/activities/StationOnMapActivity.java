package au.com.kbrsolutions.melbournepublictransport.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import au.com.kbrsolutions.melbournepublictransport.R;

public class StationOnMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = ((Object) this).getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationsonmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v(TAG, "onMapReady called");
    }
}
