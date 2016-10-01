package au.com.kbrsolutions.melbournepublictransport.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.DisruptionsDetails;
import au.com.kbrsolutions.melbournepublictransport.data.LatLonDetails;
import au.com.kbrsolutions.melbournepublictransport.data.NearbyStopsDetails;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.data.RequestProcessorService;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.events.MainActivityEvents;
import au.com.kbrsolutions.melbournepublictransport.fragments.BaseFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.DisruptionsFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.FavoriteStopsFragmentRv;
import au.com.kbrsolutions.melbournepublictransport.fragments.NearbyStopsFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.NextDeparturesFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.StationOnMapFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.StopDetailFragment;
import au.com.kbrsolutions.melbournepublictransport.utilities.CurrentGeoPositionFinder;

// git push -u origin
// ^((?!GLHudOverlay).)*$

public class MainActivity extends AppCompatActivity
        implements
//        FavoriteStopsFragmentRv.FavoriteStopsFragmentRvCallbacks,
        FavoriteStopsFragmentRv.OnFavoriteStopsFragmentInteractionListener,
        StopDetailFragment.AddStopFragmentCallbacks,
        NearbyStopsFragment.OnNearbyStopsFragmentInteractionListener {

    private FavoriteStopsFragmentRv mFavoriteStopsFragmentRv;
    private StationOnMapFragment mStationOnMapFragment;
    private StopDetailFragment mStopDetailFragment;
    private NextDeparturesFragment mNextDeparturesFragment;
    private DisruptionsFragment mDisruptionsFragment;
    private NearbyStopsFragment mNearbyStopsFragment;
    private Fragment mCurrFragment;
    ActionBar actionBar;
    private View mCoordinatorlayout;
    private FloatingActionButton fab;
    private EventBus eventBus;
    private String mSelectedStopName;
    private CurrentGeoPositionFinder mCurrentGeoPositionFinder;

    private final String FAVORITE_STOPS_TAG = "favorite_stops_tag";
    static final String TAG_ERROR_DIALOG_FRAGMENT = "errorDialog";
    private static final String STATION_ON_MAP_TAG = "station_on_map_tag";
    private static final String STOP_TAG = "stop_tag";
    private static final String NEXT_DEPARTURES_TAG = "next_departures_tag";
    private static final String DISRUPTION_TAG = "disruption_tag";
    private static final String NEARBY_TAG = "nearby_tag";
    private final static String TRANSPORT_MODE_METRO_TRAIN = "metro-train";

    public enum FragmentsId {
        FAVORITE_STOPS,
        STOPS_NEARBY,
        NEXT_DEPARTURES
    }

    private final String TAG = ((Object) this).getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (eventBus == null) {
            eventBus = EventBus.getDefault();
            eventBus.register(this);
        }
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
        mCoordinatorlayout = findViewById(R.id.coordinatedLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        showViewIfRequired();

                        int cnt = getSupportFragmentManager().getBackStackEntryCount();
                        if (cnt == 1) {      /* we came back to Favorite Stops */
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            showFavoriteStops();
                        } else {
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        }
                    }
                });

        mFavoriteStopsFragmentRv =
                (FavoriteStopsFragmentRv) getSupportFragmentManager().findFragmentByTag(FAVORITE_STOPS_TAG);


        if (mFavoriteStopsFragmentRv == null) {
            mFavoriteStopsFragmentRv = new FavoriteStopsFragmentRv();
            mFavoriteStopsFragmentRv.setFragmentId(FragmentsId.FAVORITE_STOPS);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.left_dynamic_fragments_frame, mFavoriteStopsFragmentRv, FAVORITE_STOPS_TAG)
                    .addToBackStack(FAVORITE_STOPS_TAG)
                    .commit();
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFabClicked();
            }
        });

        Intent intent = new Intent(this, RequestProcessorService.class);
        intent.putExtra(RequestProcessorService.ACTION, RequestProcessorService.REFRESH_DATA_IF_TABLES_EMPTY);
//        intent.putExtra(RequestProcessorService.ACTION, RequestProcessorService.REFRESH_DATA);
        startService(intent);
    }

    private BaseFragment getTopFragment() {
        BaseFragment topFragmment = null;
        Fragment fragment;
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        if (cnt > 0) {
            String tag = getSupportFragmentManager().getBackStackEntryAt(cnt - 1).getName();
//            Log.v(TAG, "getTopFragment - cnt/tag: " + cnt + "/" + tag);
            fragment = getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment instanceof BaseFragment) {
                topFragmment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
            }
//            Log.v(TAG, "getTopFragment - fragment/topFragmment: " + fragment + "/" + topFragmment);
        }
        return topFragmment;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(TAG, "onSaveInstanceState");
    }

    // FIXME: 17/08/2016
    private void handleFabClicked() {
//        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        BaseFragment baseFragment = getTopFragment();
        if (baseFragment != null) {
            FragmentsId fragmentsId = baseFragment.getFragmentId();
            Log.v(TAG, "handleFabClicked - baseFragment/fragmentsId: " + baseFragment + "/" + fragmentsId);
            if (fragmentsId != null && fragmentsId != FragmentsId.FAVORITE_STOPS) {
                showSnackBar("No logic to handle FAB touched in BaseFragmment", true);
                return;
            }
        } else {
            showSnackBar("No logic to handle FAB touched (not in BaseFragmment)", true);
            return;
        }
        actionBar.setTitle(getResources().getString(R.string.title_stops));
        if (mStopDetailFragment == null) {
            mStopDetailFragment = new StopDetailFragment();
        }
        mFavoriteStopsFragmentRv.hideView();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.left_dynamic_fragments_frame, mStopDetailFragment, STOP_TAG)
                .addToBackStack(STOP_TAG)     // it will also show 'Up' button in the action bar
                .commit();
        fab.hide();
    }

    private void showViewIfRequired() {
        BaseFragment baseFragment = getTopFragment();
//        Log.v(TAG, "showViewIfRequired - baseFragment: " + baseFragment);
        boolean showFab = false;
        if (baseFragment != null) {
            FragmentsId fragmentsId = baseFragment.getFragmentId();
//            Log.v(TAG, "showViewIfRequired - fragmentsId: " + fragmentsId);
            if (fragmentsId != null) {
                if (fragmentsId == FragmentsId.FAVORITE_STOPS ||
                        fragmentsId == FragmentsId.STOPS_NEARBY) {
                    baseFragment.showView();
                }
                if (fragmentsId == FragmentsId.FAVORITE_STOPS ||
                        fragmentsId == FragmentsId.NEXT_DEPARTURES) {
                    showFab = true;
                }
            }
        }
//        Log.v(TAG, "showViewIfRequired - showFab: " + showFab);
        if (showFab) {
            fab.show();
        } else {
            fab.hide();
        }
    }

    private void hideViewIfRequired() {
        BaseFragment baseFragment = getTopFragment();
//        Log.v(TAG, "hideViewIfRequired - baseFragment: " + baseFragment);
        if (baseFragment != null) {
            FragmentsId fragmentsId = baseFragment.getFragmentId();
            if (fragmentsId != null && fragmentsId == FragmentsId.FAVORITE_STOPS || fragmentsId == FragmentsId.STOPS_NEARBY) {
                baseFragment.hideView();
            }
        }
    }

    private void showNextDepartures(List<NextDepartureDetails> nextDepartureDetailsList) {
//        Log.v(TAG, "showNextDepartures");
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        actionBar.setTitle(getResources().getString(R.string.title_next_departures));
        if (mNextDeparturesFragment == null) {
            mNextDeparturesFragment = NextDeparturesFragment.newInstance(mSelectedStopName, nextDepartureDetailsList);
            mNextDeparturesFragment.setFragmentId(FragmentsId.NEXT_DEPARTURES);
        } else {
            mNextDeparturesFragment.setNewContent(mSelectedStopName, nextDepartureDetailsList);
        }
        Fragment topFragment = getTopFragment();
//        Log.v(TAG, "showNextDepartures - topFragment: " + topFragment);
        hideViewIfRequired();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.left_dynamic_fragments_frame, mNextDeparturesFragment, NEXT_DEPARTURES_TAG)
                .addToBackStack(NEXT_DEPARTURES_TAG)     // it will also show 'Up' button in the action bar
                .commit();
        fab.setImageResource(R.drawable.ic_autorenew_pink_48dp);
        Log.v(TAG, "showNextDepartures: before fab.show()");
//        fab.show();
    }

    public void getDisruptionsDetails() {
        String trainMode = TRANSPORT_MODE_METRO_TRAIN;
        Intent intent = new Intent(this, RequestProcessorService.class);
        intent.putExtra(RequestProcessorService.ACTION, RequestProcessorService.GET_DISRUPTIONS_DETAILS);
        intent.putExtra(RequestProcessorService.MODES, trainMode);
        startService(intent);
    }

    @Override
    public void updateStopDetailRow(StopDetails stopDetails, String favoriteColumnValue) {
        Log.v(TAG, "updateStopDetailRow - got request - nonFavorite: " + favoriteColumnValue);
        Intent intent = new Intent(this, RequestProcessorService.class);
        intent.putExtra(RequestProcessorService.ACTION, RequestProcessorService.UPDATE_STOPS_DETAILS);
        intent.putExtra(RequestProcessorService.ROW_ID, stopDetails.id);
        startService(intent);
    }

    /**
     * Find current latitude and longitude of the device.
     *
     * @param trainsOnly
     */
    @Override
    public void startStopsNearbySearch(boolean trainsOnly) {
        Log.v(TAG, "getCurrLatLon - trainOnly: " + trainsOnly);
        if (mCurrentGeoPositionFinder == null) {
            mCurrentGeoPositionFinder = new CurrentGeoPositionFinder(getApplicationContext(), trainsOnly);
        } else {
            mCurrentGeoPositionFinder.connectToGoogleApiClient(trainsOnly);
        }
    }

    private void getNearbyDetails(LatLonDetails latLonDetails, boolean forTrainsOnly) {
        Intent intent = new Intent(this, RequestProcessorService.class);
        if (forTrainsOnly) {
            intent.putExtra(RequestProcessorService.ACTION, RequestProcessorService.GET_TRAIN_NEARBY_STOPS_DETAILS);
        } else {
            intent.putExtra(RequestProcessorService.ACTION, RequestProcessorService.GET_NEARBY_STOPS_DETAILS);
        }
        intent.putExtra(RequestProcessorService.LAT_LON, latLonDetails);
        startService(intent);
    }

    private void showDisruptions(List<DisruptionsDetails> disruptionsDetailsList) {
        actionBar.setTitle(getResources().getString(R.string.title_disruptions));
        if (mDisruptionsFragment == null) {
            mDisruptionsFragment = DisruptionsFragment.newInstance(disruptionsDetailsList);
        } else {
            mDisruptionsFragment.setNewContent(disruptionsDetailsList);
        }
        // FIXME: 21/09/2016 - below has to hide current fragment
        mFavoriteStopsFragmentRv.hideView();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.left_dynamic_fragments_frame, mDisruptionsFragment, DISRUPTION_TAG)
                .addToBackStack(DISRUPTION_TAG)     // it will also show 'Up' button in the action bar
                .commit();
        fab.hide();
    }

    /**
     * Up button was pressed - remove to top entry Back Stack
     */
    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
//        Log.v(TAG, "onSupportNavigateUp - after popBackStack");
//        fab.setImageResource(R.drawable.ic_autorenew_pink_48dp);
        fab.setImageResource(android.R.drawable.ic_input_add);
        return true;
    }

    /**
     * Show favorite stops after user pressed Back or Up button.
     */
    private void showFavoriteStops() {
        actionBar.setTitle(getResources().getString(R.string.title_favorite_stops));
        mFavoriteStopsFragmentRv.showFavoriteStops();
//        fab.show();
    }

    public void startNextDeparturesSearch(StopDetails stopDetails) {
//        Log.v(TAG, "startNextDeparturesSearch");
        Intent intent = new Intent(this, RequestProcessorService.class);
        mSelectedStopName = stopDetails.locationName;
        intent.putExtra(RequestProcessorService.ACTION, RequestProcessorService.SHOW_NEXT_DEPARTURES);
        intent.putExtra(RequestProcessorService.MODE, stopDetails.routeType);
        intent.putExtra(RequestProcessorService.STOP_ID, stopDetails.stopId);
        intent.putExtra(RequestProcessorService.LIMIT, 5);
        startService(intent);
    }

    public void addStop() {
        if (mFavoriteStopsFragmentRv != null) {
            mFavoriteStopsFragmentRv.showView();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .remove(mStopDetailFragment)
                .commit();

        getSupportFragmentManager().popBackStack();
//        fab.show();
    }

    @Override
    public void showSelectedStopOnMap(LatLonDetails latLonDetails) {
        if (readyToGo()) {
            fab.hide();
            if (mStationOnMapFragment == null) {
                mStationOnMapFragment = StationOnMapFragment.newInstance(
                        latLonDetails.latitude,
                        latLonDetails.longitude);
            } else {
                mStationOnMapFragment.setLatLon(
                        latLonDetails.latitude,
                        latLonDetails.longitude);
            }
            mFavoriteStopsFragmentRv.hideView();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.left_dynamic_fragments_frame, mStationOnMapFragment, STATION_ON_MAP_TAG)
                    .addToBackStack(STATION_ON_MAP_TAG)     // it will also show 'Up' button in the action bar
                    .commit();
        }
    }

    public void showNearbyStops(List<NearbyStopsDetails> nearbyStopsDetailsList) {
        Log.v(TAG, "showNearbyStops - start");
//        mCurrFragment
        if (mNearbyStopsFragment == null) {
            mNearbyStopsFragment = NearbyStopsFragment.newInstance(nearbyStopsDetailsList);
            mNearbyStopsFragment.setFragmentId(FragmentsId.STOPS_NEARBY);
        } else {
            mNearbyStopsFragment.setNewContent(nearbyStopsDetailsList);
        }
        mFavoriteStopsFragmentRv.hideView();
//        Log.v(TAG, "showNearbyStops - adding mNearbyStopsFragment");
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.left_dynamic_fragments_frame, mNearbyStopsFragment, NEARBY_TAG)
                .addToBackStack(NEARBY_TAG)     // it will also show 'Up' button in the action bar
                .commit();
        fab.hide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
//            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Get messages through Event Bus from Green Robot.
     * This method will be called when a MainActivityEvents is posted (in the UI thread
     */
    // FIXME: 21/09/2016 - read this when you get java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
    //    http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MainActivityEvents event) {
//        Log.v(TAG, "onMessageEvent - start");
        MainActivityEvents.MainEvents requestEvent = event.event;
        switch (requestEvent) {

            case NETWORK_STATUS:
                showSnackBar(event.msg, true);
                break;

            case NEXT_DEPARTURES_DETAILS:
//                Log.v(TAG, "onMessageEvent - NEXT_DEPARTURES_DETAILS: " + event.nextDepartureDetailsList);
                showNextDepartures(event.nextDepartureDetailsList);
                break;

            case DISRUPTIONS_DETAILS:
                showDisruptions(event.disruptionsDetailsList);
                break;

            case CURR_LOCATION_DETAILS:
                getNearbyDetails(event.latLonDetails, event.forTrainsStopsNearby);
                break;

            case NEARBY_LOCATION_DETAILS:
                showNearbyStops(event.nearbyStopsDetailsList);
                break;

            default:
                throw new RuntimeException("LOC_CAT_TAG - onEvent - no code to handle requestEvent: " + requestEvent);
        }
    }

    public void showSnackBar(String msg, boolean showIndefinite) {
        Snackbar
                .make(mCoordinatorlayout, msg, (showIndefinite ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_LONG))
                .setActionTextColor(Color.RED)
                .show(); // Don’t forget to show!
    }

    public void showSnackBar(int msg, boolean showIndefinite) {
        Snackbar
                .make(mCoordinatorlayout, msg, (showIndefinite ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_LONG))
                .setActionTextColor(Color.RED)
                .show(); // Don’t forget to show!
    }

    @Override
    public void onNearbyStopsFragmentMapClicked(NearbyStopsDetails nearbyStopsDetails) {
        showSelectedStopOnMap(new LatLonDetails(nearbyStopsDetails.stopLat, nearbyStopsDetails.stopLon));
    }

    public static class ErrorDialogFragment extends DialogFragment {
        static final String ARG_ERROR_CODE = "errorCode";

        static ErrorDialogFragment newInstance(int errorCode) {
            Bundle args = new Bundle();
            ErrorDialogFragment result = new ErrorDialogFragment();

            args.putInt(ARG_ERROR_CODE, errorCode);
            result.setArguments(args);

            return (result);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle args = getArguments();
            GoogleApiAvailability checker =
                    GoogleApiAvailability.getInstance();

            return (checker.getErrorDialog(getActivity(),
                    args.getInt(ARG_ERROR_CODE), 0));
        }

        @Override
        public void onDismiss(DialogInterface dlg) {
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    // FIXME: 25/09/2016 find source of the code below - The Busy ...
    protected boolean readyToGo() {
        GoogleApiAvailability checker =
                GoogleApiAvailability.getInstance();

        int status = checker.isGooglePlayServicesAvailable(this);

        if (status == ConnectionResult.SUCCESS) {
            if (getVersionFromPackageManager(this) >= 2) {
                return (true);
            } else {
//                Toast.makeText(this, R.string.no_maps, Toast.LENGTH_LONG).show();
                showSnackBar(R.string.no_maps, true);
                finish();
            }
        } else if (checker.isUserResolvableError(status)) {
            FragmentManager fm = getSupportFragmentManager();
            ErrorDialogFragment.newInstance(status)
                    .show(fm, TAG_ERROR_DIALOG_FRAGMENT);
        } else {
//            Toast.makeText(this, R.string.no_maps, Toast.LENGTH_LONG).show();
            showSnackBar(R.string.no_maps, true);
            finish();
        }

        return (false);
    }

    private static int getVersionFromPackageManager(Context context) {
        PackageManager packageManager = context.getPackageManager();
        FeatureInfo[] featureInfos =
                packageManager.getSystemAvailableFeatures();
        if (featureInfos != null && featureInfos.length > 0) {
            for (FeatureInfo featureInfo : featureInfos) {
                // Null feature name means this feature is the open
                // gl es version feature.
                if (featureInfo.name == null) {
                    if (featureInfo.reqGlEsVersion != FeatureInfo.GL_ES_VERSION_UNDEFINED) {
                        return getMajorVersion(featureInfo.reqGlEsVersion);
                    } else {
                        return 1; // Lack of property means OpenGL ES
                        // version 1
                    }
                }
            }
        }
        return 1;
    }

    /**
     * @see FeatureInfo#getGlEsVersion()
     */
    private static int getMajorVersion(int glEsVersion) {
        return ((glEsVersion & 0xffff0000) >> 16);
    }

}
