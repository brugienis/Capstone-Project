package au.com.kbrsolutions.melbournepublictransport.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.fragments.FavoriteStopsFragment;

import static au.com.kbrsolutions.melbournepublictransport.activities.MainActivity.FragmentsId.FAVORITE_STOPS;

public class WidgetStopsActivity
        extends AppCompatActivity
        implements FavoriteStopsFragment.OnFavoriteStopsFragmentInteractionListener {

    private FavoriteStopsFragment mFavoriteStopsFragment;

    public static final String WIDGET_STOP_ID = "widget_stop_id";
    public static final String WIDGET_LOCATION_NAME = "widget_location_name";
    private static final String FAVORITE_STOPS_TAG = "favorite_stops_tag";

    private final String TAG = ((Object) this).getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_widget_stops);
        setContentView(R.layout.activity_main);

        if (mFavoriteStopsFragment == null) {
//            Log.v(TAG, "showFavoriteStops - adding new mFavoriteStopsFragment");
            mFavoriteStopsFragment = new FavoriteStopsFragment();
            mFavoriteStopsFragment.setFragmentId(FAVORITE_STOPS);
            mFavoriteStopsFragment.setActionBarTitle(getResources().getString(R.string.title_favorite_stops));
            mFavoriteStopsFragment.setIsInSettingsActivity();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.left_dynamic_fragments_frame, mFavoriteStopsFragment, FAVORITE_STOPS_TAG)
//                    .addToBackStack(FAVORITE_STOPS_TAG)
                .commit();
    }
    public void startNextDeparturesSearch(StopDetails stopDetails) {};
    public void showStopOnMap(LatLngDetails latLonDetails) {};
    public void startStopsNearbySearch(boolean trainsOnly) {};
    public void getDisruptionsDetails() {};
    public void updateStopDetailRow(int id, String favoriteColumnValue) {};
    public void reloadDatabase() {};

    @Override
    public void updateWidgetStopDetails(String stopId, String locationName) {
//        Log.v(TAG, "updateWidgetStopDetails - stopId/locationName: " + stopId + "/" + locationName);
        Intent intent = new Intent();
        intent.putExtra(WIDGET_STOP_ID, stopId);
        intent.putExtra(WIDGET_LOCATION_NAME, locationName);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
