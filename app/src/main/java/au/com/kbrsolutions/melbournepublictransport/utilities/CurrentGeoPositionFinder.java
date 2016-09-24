package au.com.kbrsolutions.melbournepublictransport.utilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;

import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.events.MainActivityEvents;

/**
 * Created by business on 24/09/2016.
 */

public class CurrentGeoPositionFinder implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
//    private TextView mLatitudeText;
//    private TextView mLongitudeText;
    private Context mContext;

    private LocationRequest mLocationRequest;

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public CurrentGeoPositionFinder(Context context) {
        mContext = context;
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    
    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, location.toString());
        //txtOutput.setText(location.toString());

//        mLatitudeText.setText(String.valueOf(location.getLatitude()));
//        mLongitudeText.setText(String.valueOf(location.getLongitude()));
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
    /*
    * Called by Google Play services if the connection to GoogleApiClient drops because of an
    * error.
    */
    public void onDisconnected() {
        Log.i(TAG, "Disconnected");
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     *
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     */
    // FIXME: 24/09/2016 - handle case when recent location is null
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "onConnected - start");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.i(TAG, "onConnected - lat/lon: " + mLastLocation.getLatitude() + "/" + mLastLocation.getLongitude());
        EventBus.getDefault().post(new MainActivityEvents.Builder(
                MainActivityEvents.MainEvents.CURR_LOCATION_DETAILS)
                .setStopDetails(
                        new StopDetails(
                                -1,
                                -1,
                                null,
                                null,
                                mLastLocation.getLatitude(),
                                mLastLocation.getLongitude(),
                                null))
                .build());
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
//    @Override
//    public void onConnected(Bundle connectionHint) {
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(1000);
//        if ( Build.VERSION.SDK_INT >= 23 &&
//                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
//
//    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
}
