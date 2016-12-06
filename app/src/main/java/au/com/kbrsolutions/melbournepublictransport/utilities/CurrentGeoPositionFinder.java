package au.com.kbrsolutions.melbournepublictransport.utilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;

import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.events.MainActivityEvents;

/**
 *
 * Find current latitude and longitude of the device.
 *
 */
public class CurrentGeoPositionFinder implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private final Context mContext;
    private boolean mTrainOnly;

    @SuppressWarnings("unused")
    private final String TAG = ((Object) this).getClass().getSimpleName();

    public CurrentGeoPositionFinder(Context context, boolean trainOnly) {
        mContext = context;
        mTrainOnly = trainOnly;
        buildGoogleApiClient();
        connectToGoogleApiClient(trainOnly);
    }

    public void connectToGoogleApiClient(boolean trainOnly) {
        mTrainOnly = trainOnly;
        mGoogleApiClient.connect();
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    
    @Override
    public void onLocationChanged(Location location) {
        EventBus.getDefault().post(new MainActivityEvents.Builder(
                MainActivityEvents.MainEvents.CURR_LOCATION_DETAILS)
                .setLatLonDetails(
                        new LatLngDetails(
                                location.getLatitude(),
                                location.getLongitude()))
                .setForTrainsStopsNearby(mTrainOnly)
                .build());
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest,this);

    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
    }
}
