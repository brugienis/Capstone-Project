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
import au.com.kbrsolutions.melbournepublictransport.fragments.FavoriteStopsFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.InitFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.NextDeparturesFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.StationOnMapFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.StopsFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.StopsNearbyFragment;
import au.com.kbrsolutions.melbournepublictransport.utilities.CurrentGeoPositionFinder;

// git push -u origin
// ^((?!GLHudOverlay).)*$
// ^((?!OpenGLRenderer).)*$

public class MainActivity extends AppCompatActivity implements
        InitFragment.OnInitFragmentInteractionListener,
        FavoriteStopsFragment.OnFavoriteStopsFragmentInteractionListener,
        StopsFragment.OnStopFragmentInteractionListener,
        StopsNearbyFragment.OnNearbyStopsFragmentInteractionListener {

    private InitFragment mInitFragment;
    private FavoriteStopsFragment mFavoriteStopsFragment;
    private StationOnMapFragment mStationOnMapFragment;
    private StopsFragment mStopsFragment;
    private NextDeparturesFragment mNextDeparturesFragment;
    private DisruptionsFragment mDisruptionsFragment;
    private StopsNearbyFragment mStopsNearbyFragment;
    ActionBar actionBar;
    private View mCoordinatorlayout;
    private FloatingActionButton fab;
    private EventBus eventBus;
//    private String mSelectedStopName;
    private CurrentGeoPositionFinder mCurrentGeoPositionFinder;
    private int mPrevBackStackEntryCount;

    private static final String FAVORITE_STOPS_TAG = "favorite_stops_tag";
    private static final String ERROR_DIALOG_FRAGMENT_TAG = "errorDialog";
    private static final String STATION_ON_MAP_TAG = "station_on_map_tag";
    private static final String STOP_TAG = "stop_tag";
    private static final String NEXT_DEPARTURES_TAG = "next_departures_tag";
    private static final String DISRUPTION_TAG = "disruption_tag";
    private static final String NEARBY_TAG = "nearby_tag";
    private static final String INIT_TAG = "init_tag";
    private final static String TRANSPORT_MODE_METRO_TRAIN = "metro-train";

    public enum FragmentsId {
        DISRUPTIONS,
        FAVORITE_STOPS,
        INIT,
        NEXT_DEPARTURES,
        STATION_ON_MAP,
        STOPS,
        STOPS_NEARBY
    }

    private final String TAG = ((Object) this).getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate - start");
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

                        showTopViewAfterBackOrUpPressed();

//                        printBackStackFragments();
                        int cnt = getSupportFragmentManager().getBackStackEntryCount();
                        BaseFragment bf = getTopFragment();
                        boolean backButtonPressed;
                        if (cnt > mPrevBackStackEntryCount) {
//                            Log.v(TAG, "onCreate.onBackStackChanged - going forward  - cnt/top: " + cnt + "/" + bf.getFragmentId());
//                            backButtonPressed = false;
                        } else {
//                            Log.v(TAG, "onCreate.onBackStackChanged - going backward - cnt/top: " + cnt + "/" + (bf == null ? "null" : bf.getFragmentId()));
//                            backButtonPressed = true;
                        }
                        mPrevBackStackEntryCount = cnt;
//                        if (cnt == 1 && bf.getFragmentId() == FragmentsId.FAVORITE_STOPS) {
                        if (cnt == 1) {
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        } else if (cnt > 1) {
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        }
                    }
                });

//        mFavoriteStopsFragment =
//                (FavoriteStopsFragment) getSupportFragmentManager().findFragmentByTag(FAVORITE_STOPS_TAG);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFabClicked();
            }
        });

        findFragments();
//        printBackStackFragments();

//        Log.v(TAG, "onCreate - this/mInitFragment: " + String.format("0x%08X", this.hashCode()) + "/" + mInitFragment);

        BaseFragment topFragmment = getTopFragment();
        String topFragmentTag = getTopFragmentTag();

        if (savedInstanceState != null) {   /* configuration changed */
            printBackStackFragments();
            FragmentsId fragmentsId = topFragmment.getFragmentId();
            if (topFragmentTag != null &&
                    (fragmentsId == FragmentsId.FAVORITE_STOPS ||
                            fragmentsId == FragmentsId.NEXT_DEPARTURES)) {
                fab.show();
            } else {
                fab.hide();
            }
            int cnt = getSupportFragmentManager().getBackStackEntryCount();
            if (cnt > 1) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        } else {
            if (topFragmentTag == null || !topFragmentTag.equals(INIT_TAG)) {
                Intent intent = new Intent(this, RequestProcessorService.class);
                intent.putExtra(RequestProcessorService.REQUEST, RequestProcessorService.ACTION_REFRESH_DATA);
                intent.putExtra(RequestProcessorService.REFRESH_DATA_IF_TABLES_EMPTY, true);
                Log.v(TAG, "onCreate - load database request sent");
                showFavoriteStops();
                startService(intent);
            }
        }
        Log.v(TAG, "onCreate - end");
    }

    private void handleDatabaseLoadStatus(boolean databaseLoaded) {
        Log.v(TAG, "handleDatabaseLoadStatus - databaseLoadFinished: " + databaseLoaded);
        if (!databaseLoaded) {
            if (mInitFragment == null) {
                mInitFragment = new InitFragment();
                mInitFragment.setFragmentId(FragmentsId.INIT);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.left_dynamic_fragments_frame, mInitFragment, INIT_TAG)
                        .addToBackStack(INIT_TAG)
                        .commit();
                mInitFragment.setActionBarTitle(getResources().getString(R.string.title_data_load));
//                actionBar.setTitle(mInitFragment.getActionBarTitle());
                mFavoriteStopsFragment.hideView();
            }
        }
    }

    public void databaseLoadFinished() {
        Log.v(TAG, "databaseLoadFinished - start");
//        if (mFavoriteStopsFragment == null) {
//            mFavoriteStopsFragment = new FavoriteStopsFragment();
//            mFavoriteStopsFragment.setFragmentId(FragmentsId.FAVORITE_STOPS);
//        }
        getSupportFragmentManager()
                .beginTransaction()
                .remove(mInitFragment)
                .commit();
        getSupportFragmentManager().popBackStack();
        // FIXME: 9/10/2016 - line below is probably no required - title will be set onBackstackListener
//        actionBar.setTitle(getResources().getString(R.string.title_favorite_stops));
        Log.v(TAG, "databaseLoadFinished - end");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        Log.v(TAG, "onSaveInstanceState - after super");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        Log.v(TAG, "onRestoreInstanceState - start");
//        showFragmentsOnBackStackVisibility();
        super.onRestoreInstanceState(savedInstanceState);
        showTopFragment();
//        Log.v(TAG, "onRestoreInstanceState - after showTopFragment()");
//        showFragmentsOnBackStackVisibility();
    }

    private void handleFabClicked() {
        BaseFragment baseFragment = getTopFragment();
        FragmentsId fragmentsId;
        if (baseFragment != null) {
            fragmentsId = baseFragment.getFragmentId();
//            Log.v(TAG, "handleFabClicked - baseFragment/fragmentsId: " + baseFragment + "/" + fragmentsId);
            if (fragmentsId != null &&
                    fragmentsId != FragmentsId.FAVORITE_STOPS &&
                    fragmentsId != FragmentsId.NEXT_DEPARTURES
                    ) {
                showSnackBar("No logic to handle FAB touched in: " + fragmentsId, true);
                return;
            }
        } else {
            showSnackBar("No logic to handle FAB touched (not in BaseFragmment)", true);
            return;
        }
        switch (fragmentsId) {
            case FAVORITE_STOPS:
//            actionBar.setTitle(getResources().getString(R.string.title_stops));
                if (mStopsFragment == null) {
                    mStopsFragment = new StopsFragment();
                    mStopsFragment.setFragmentId(FragmentsId.STOPS);
                }
                mFavoriteStopsFragment.hideView();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.left_dynamic_fragments_frame, mStopsFragment, STOP_TAG)
                        .addToBackStack(STOP_TAG)     // it will also show 'Up' button in the action bar
                        .commit();
                mStopsFragment.setActionBarTitle(getResources().getString(R.string.title_stops));
                fab.hide();
            break;

            case NEXT_DEPARTURES:
//            actionBar.setTitle(getResources().getString(R.string.title_next_departures));
                startNextDeparturesSearch(mNextDeparturesFragment.getSearchStopDetails());
            break;

            default:
                throw new RuntimeException(
                        "LOC_CAT_TAG - handleFabClicked - no code to handle fragmentsId: " +
                                "" + fragmentsId);

        }
    }

    private void showTopViewAfterBackOrUpPressed() {
        BaseFragment baseFragment = getTopFragment();
//        Log.v(TAG, "showTopViewAfterBackOrUpPressed - baseFragment: " + baseFragment);
        boolean showFab = false;
        if (baseFragment != null) {
            FragmentsId fragmentsId = baseFragment.getFragmentId();
            Log.v(TAG, "showTopViewAfterBackOrUpPressed - fragmentsId: " + fragmentsId);
            if (fragmentsId != null) {
                if (fragmentsId == FragmentsId.FAVORITE_STOPS ||
                        fragmentsId == FragmentsId.STOPS ||
                        fragmentsId == FragmentsId.STOPS_NEARBY) {
                    baseFragment.showView();
                }
                if (fragmentsId == FragmentsId.FAVORITE_STOPS ||
                        fragmentsId == FragmentsId.NEXT_DEPARTURES) {
                    showFab = true;
                }
                actionBar.setTitle(baseFragment.getActionBarTitle());
                Log.v(TAG, "showTopViewAfterBackOrUpPressed - fragmentsId/title: " + fragmentsId + "/" + baseFragment.getActionBarTitle());
            }
        }
//        Log.v(TAG, "showTopViewAfterBackOrUpPressed - showFab: " + showFab);
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
            if (fragmentsId != null &&
                    (fragmentsId == FragmentsId.FAVORITE_STOPS ||
                            fragmentsId == FragmentsId.STOPS ||
                            fragmentsId == FragmentsId.STOPS_NEARBY)) {
                baseFragment.hideView();
//                Log.v(TAG, "hideViewIfRequired - DID     hide " + fragmentsId);
//                Log.v(TAG, "hideViewIfRequired - hiding " + fragmentsId);
            } else {
//                Log.v(TAG, "hideViewIfRequired - DID NOT hide " + fragmentsId);
            }
        } else {
            // FIXME: 8/10/2016 - remove before releasing. Also the 'if (baseFragment != null)' above
            throw new RuntimeException(TAG + ".hideViewIfRequired - baseFragment cannot be null");
        }
    }

    // FIXME: 8/10/2016 - get selected stopName from stopDetails, not from mSelectedStopName
    private void showNextDepartures(
            List<NextDepartureDetails> nextDepartureDetailsList,
            StopDetails stopDetails) {
//        Log.v(TAG, "showNextDepartures - start");
//        actionBar.setTitle(getResources().getString(R.string.title_next_departures));
//        showFragmentsOnBackStackVisibility();
        if (mNextDeparturesFragment == null) {
            mNextDeparturesFragment = NextDeparturesFragment.newInstance(
//                    mSelectedStopName,
                    stopDetails.locationName,
                    nextDepartureDetailsList,
                    stopDetails);
            mNextDeparturesFragment.setFragmentId(FragmentsId.NEXT_DEPARTURES);
        } else {
            mNextDeparturesFragment.setNewContent(
//                    mSelectedStopName,
                    stopDetails.locationName,
                    nextDepartureDetailsList,
                    stopDetails);
        }
        BaseFragment topFragment = getTopFragment();
        if (topFragment.getFragmentId() == FragmentsId.NEXT_DEPARTURES) {
            return;
        }
        hideViewIfRequired();
//        Log.v(TAG, "showNextDepartures - after hideViewIfRequired() ");
//        showFragmentsOnBackStackVisibility();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.left_dynamic_fragments_frame, mNextDeparturesFragment, NEXT_DEPARTURES_TAG)
                .addToBackStack(NEXT_DEPARTURES_TAG)     // it will also show 'Up' button in the action bar
                .commit();
        mNextDeparturesFragment.setActionBarTitle(getResources().getString(R.string.title_next_departures));
        fab.setImageResource(R.drawable.ic_autorenew_pink_48dp);
    }

    public void getDisruptionsDetails() {
        String trainMode = TRANSPORT_MODE_METRO_TRAIN;
        Intent intent = new Intent(this, RequestProcessorService.class);
        intent.putExtra(RequestProcessorService.REQUEST, RequestProcessorService.GET_DISRUPTIONS_DETAILS);
        intent.putExtra(RequestProcessorService.MODES, trainMode);
        startService(intent);
    }

    @Override
    public void updateStopDetailRow(int id, String favoriteColumnValue) {
        Intent intent = new Intent(this, RequestProcessorService.class);
        intent.putExtra(RequestProcessorService.REQUEST, RequestProcessorService.UPDATE_STOPS_DETAILS);
        intent.putExtra(RequestProcessorService.ROW_ID, id);
        intent.putExtra(RequestProcessorService.FAVORITE_COLUMN_VALUE, favoriteColumnValue);
        startService(intent);
    }

    /**
     * Find current latitude and longitude of the device.
     *
     * @param trainsOnly
     */
    @Override
    public void startStopsNearbySearch(boolean trainsOnly) {
        if (mCurrentGeoPositionFinder == null) {
            mCurrentGeoPositionFinder = new CurrentGeoPositionFinder(getApplicationContext(), trainsOnly);
        } else {
            mCurrentGeoPositionFinder.connectToGoogleApiClient(trainsOnly);
        }
    }

    private void getNearbyDetails(LatLonDetails latLonDetails, boolean forTrainsOnly) {
        Intent intent = new Intent(this, RequestProcessorService.class);
        if (forTrainsOnly) {
            intent.putExtra(RequestProcessorService.REQUEST, RequestProcessorService.GET_TRAIN_NEARBY_STOPS_DETAILS);
        } else {
            intent.putExtra(RequestProcessorService.REQUEST, RequestProcessorService.GET_NEARBY_STOPS_DETAILS);
        }
        intent.putExtra(RequestProcessorService.LAT_LON, latLonDetails);
        startService(intent);
    }

    private void showDisruptions(List<DisruptionsDetails> disruptionsDetailsList) {
//        actionBar.setTitle(getResources().getString(R.string.title_disruptions));
        if (mDisruptionsFragment == null) {
            mDisruptionsFragment = DisruptionsFragment.newInstance(disruptionsDetailsList);
            mDisruptionsFragment.setFragmentId(FragmentsId.DISRUPTIONS);
        } else {
            mDisruptionsFragment.setNewContent(disruptionsDetailsList);
        }
        // FIXME: 21/09/2016 - below has to hide current fragment
        mFavoriteStopsFragment.hideView();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.left_dynamic_fragments_frame, mDisruptionsFragment, DISRUPTION_TAG)
                .addToBackStack(DISRUPTION_TAG)     // it will also show 'Up' button in the action bar
                .commit();
        mDisruptionsFragment.setActionBarTitle(getResources().getString(R.string.title_disruptions));
        fab.hide();
    }

    /**
     * Up button was pressed - remove to top entry Back Stack
     */
    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        fab.setImageResource(android.R.drawable.ic_input_add);
        return true;
    }

    public void startNextDeparturesSearch(StopDetails stopDetails) {
        Intent intent = new Intent(this, RequestProcessorService.class);
        intent.putExtra(RequestProcessorService.REQUEST, RequestProcessorService.SHOW_NEXT_DEPARTURES);
        Bundle mBundle = new Bundle();
        mBundle.putParcelable(RequestProcessorService.STOP_DETAILS, stopDetails);
        intent.putExtras(mBundle);
        intent.putExtra(RequestProcessorService.LIMIT, 5);
        startService(intent);
    }

    /**
     * Show favorite stops after user pressed Back or Up button.
     */
    private void showFavoriteStops() {
        Log.v(TAG, "showFavoriteStops - start");
//        actionBar.setTitle(getResources().getString(R.string.title_favorite_stops));
        if (mFavoriteStopsFragment == null) {
            Log.v(TAG, "showFavoriteStops - adding new mFavoriteStopsFragment");
            mFavoriteStopsFragment = new FavoriteStopsFragment();
            mFavoriteStopsFragment.setFragmentId(FragmentsId.FAVORITE_STOPS);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.left_dynamic_fragments_frame, mFavoriteStopsFragment, FAVORITE_STOPS_TAG)
                    .addToBackStack(FAVORITE_STOPS_TAG)
                    .commit();
            mFavoriteStopsFragment.setActionBarTitle(getResources().getString(R.string.title_favorite_stops));
        } else {
            Log.v(TAG, "showFavoriteStops - did not added mFavoriteStopsFragment - it was not null");
        }
    }

    /**
     * Remove StopFragment from the top of the BackStack.
     */
    public void showUpdatedFavoriteStops() {
        if (mFavoriteStopsFragment != null) {
            mFavoriteStopsFragment.showView();
            mFavoriteStopsFragment.setFragmentId(FragmentsId.FAVORITE_STOPS);
        }
        printBackStackFragments();
        getSupportFragmentManager()
                .beginTransaction()
                .remove(mStopsFragment)
                .commit();

        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showStopOnMap(LatLonDetails latLonDetails) {
        Log.v(TAG, "showStopOnMap - start");
        if (readyToGo()) {
            fab.hide();
            if (mStationOnMapFragment == null) {
                mStationOnMapFragment = StationOnMapFragment.newInstance(
                        latLonDetails.latitude,
                        latLonDetails.longitude);
                mStationOnMapFragment.setFragmentId(FragmentsId.STATION_ON_MAP);
            } else {
                mStationOnMapFragment.setLatLon(
                        latLonDetails.latitude,
                        latLonDetails.longitude);
            }
            mFavoriteStopsFragment.hideView();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.left_dynamic_fragments_frame, mStationOnMapFragment, STATION_ON_MAP_TAG)
                    .addToBackStack(STATION_ON_MAP_TAG)
                    .commit();
            mStationOnMapFragment.setActionBarTitle(getResources().getString(R.string.title_stop_on_map));
        }
    }

    public void showStopsNearby(List<NearbyStopsDetails> nearbyStopsDetailsList) {
        if (mStopsNearbyFragment == null) {
            mStopsNearbyFragment = StopsNearbyFragment.newInstance(nearbyStopsDetailsList);
            mStopsNearbyFragment.setFragmentId(FragmentsId.STOPS_NEARBY);
        } else {
            mStopsNearbyFragment.setNewContent(nearbyStopsDetailsList);
        }
        mFavoriteStopsFragment.hideView();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.left_dynamic_fragments_frame, mStopsNearbyFragment, NEARBY_TAG)
                .addToBackStack(NEARBY_TAG)
                .commit();
        mStopsNearbyFragment.setActionBarTitle(getResources().getString(R.string.title_stops_nearby));
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
     * This method will be called when a MainActivityEvents is posted (in the UI thread)
     */
    // FIXME: 21/09/2016 - read this when you get java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
    // FIXME:  4/10/2016 - written by Alex Lockwood
    //    http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MainActivityEvents event) {
        MainActivityEvents.MainEvents requestEvent = event.event;
        switch (requestEvent) {

            case NETWORK_STATUS:
                showSnackBar(event.msg, true);
                break;

            case NEXT_DEPARTURES_DETAILS:
                showNextDepartures(event.nextDepartureDetailsList, event.stopDetails);
                break;

            case DISRUPTIONS_DETAILS:
                showDisruptions(event.disruptionsDetailsList);
                break;

            case CURR_LOCATION_DETAILS:
                getNearbyDetails(event.latLonDetails, event.forTrainsStopsNearby);
                break;

            case NEARBY_LOCATION_DETAILS:
                showStopsNearby(event.nearbyStopsDetailsList);
                break;

            case DATABASE_STATUS:
                handleDatabaseLoadStatus(event.databaseLoaded);
                break;

            case DATABASE_LOAD_TARGET:
                handleDatabaseLoadedTarget(event.databaseLoadTarget);
                break;

            case DATABASE_LOAD_PROGRESS:
                handleDatabaseLoadProgress(event.databaseLoadProgress, event.databaseLoadTarget);
                break;

            default:
                throw new RuntimeException("LOC_CAT_TAG - onEvent - no code to handle requestEvent: " + requestEvent);
        }
    }

    private void handleDatabaseLoadedTarget(int databaseLoadTarget) {
        if (mInitFragment != null) {
            mInitFragment.setDatabaseLoadTarget(databaseLoadTarget);
        } else {
            Log.v(TAG, "handleDatabaseLoadedTarget - mInitFragment - is null");
        }
    }

    private void handleDatabaseLoadProgress(int databaseLoadProgress, int databaseLoadTarget) {
        if (mInitFragment != null) {
            mInitFragment.updateDatabaseLoadProgress(databaseLoadProgress, databaseLoadTarget);
        } else {
            Log.v(TAG, "handleDatabaseLoadedTarget - mInitFragment - is null");
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
        showStopOnMap(new LatLonDetails(nearbyStopsDetails.stopLat, nearbyStopsDetails.stopLon));
    }

    private void showTopFragment() {
//        Log.v(TAG, "showTopFragment - start");
        Fragment topFragmment = null;
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        String tag;
        for (int i = 0; i < cnt; i++) {
            tag = getSupportFragmentManager().getBackStackEntryAt(i).getName();
            topFragmment = getSupportFragmentManager().findFragmentByTag(tag);
            ((BaseFragment)topFragmment).hideView();
//            Log.v(TAG, "showTopFragment - fragment: " + i + " - " + topFragmment + "/" + topFragmment.getFragmentId());
        }
        if (topFragmment != null) {
            ((BaseFragment)topFragmment).showView();
            actionBar.setTitle(((BaseFragment)topFragmment).getActionBarTitle());
        }
//        showFragmentsOnBackStackVisibility();
//        Log.v(TAG, "showTopFragment - end");
    }

    private void printBackStackFragments() {
        Log.v(TAG, "printBackStackFragments - start");
        BaseFragment topFragmment;
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        String tag;
        for (int i = 0; i < cnt; i++) {
            tag = getSupportFragmentManager().getBackStackEntryAt(i).getName();
            topFragmment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
            Log.v(TAG, "printBackStackFragments - fragment: " + i + " - " + topFragmment + "/" + topFragmment.getFragmentId());
        }
        Log.v(TAG, "printBackStackFragments - end");
    }

    private void showFragmentsOnBackStackVisibility() {
//        Log.v(TAG, "showFragmentsOnBackStackVisibility - start");
        BaseFragment topFragmment;
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        String tag;
        for (int i = 0; i < cnt; i++) {
            tag = getSupportFragmentManager().getBackStackEntryAt(i).getName();
            topFragmment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
            topFragmment.isRootViewVisible();
//            Log.v(TAG, "showFragmentsOnBackStackVisibility - fragment: " + i + " - " + topFragmment + "/" + topFragmment.getFragmentId());
        }
//        Log.v(TAG, "showFragmentsOnBackStackVisibility - end");
    }

    private BaseFragment getTopFragment() {
        BaseFragment topFragmment = null;
        Fragment fragment;
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        if (cnt > 0) {
            String tag = getSupportFragmentManager().getBackStackEntryAt(cnt - 1).getName();
            fragment = getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment instanceof BaseFragment) {
                topFragmment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
            }
        }
        return topFragmment;
    }

    private String getTopFragmentTag() {
        String tag = null;
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        if (cnt > 0) {
            tag = getSupportFragmentManager().getBackStackEntryAt(cnt - 1).getName();
        }
        return tag;
    }

    private void findFragments() {
//        Log.v(TAG, "findFragments - start");
        Fragment topFragmment = null;
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        String tag;
        for (int i = 0; i < cnt; i++) {
            tag = getSupportFragmentManager().getBackStackEntryAt(i).getName();
            topFragmment = getSupportFragmentManager().findFragmentByTag(tag);
            ((BaseFragment)topFragmment).hideView();
            switch (tag) {
                case FAVORITE_STOPS_TAG:
                    mFavoriteStopsFragment = (FavoriteStopsFragment) topFragmment;
                    break;

                case STATION_ON_MAP_TAG:
                    mStationOnMapFragment = (StationOnMapFragment) topFragmment;
                    break;

                case STOP_TAG:
                    mStopsFragment = (StopsFragment) topFragmment;
                    break;

                case NEXT_DEPARTURES_TAG:
                    mNextDeparturesFragment = (NextDeparturesFragment) topFragmment;
                    break;

                case DISRUPTION_TAG:
                    mDisruptionsFragment = (DisruptionsFragment) topFragmment;
                    break;

                case NEARBY_TAG:
                    mStopsNearbyFragment = (StopsNearbyFragment) topFragmment;
                    break;

                case INIT_TAG:
                    mInitFragment = (InitFragment) topFragmment;
                    break;

                default:
                    throw new RuntimeException(TAG + ".findFragments - no code to handle tag: " + tag);
            }
//            Log.v(TAG, "printBackStackFragments - fragment: " + i + " - " + topFragmment + "/" + topFragmment.getFragmentId());
        }
        if (topFragmment != null) {
            ((BaseFragment)topFragmment).showView();
        }
//        showFragmentsOnBackStackVisibility();
//        Log.v(TAG, "findFragments - end");
    }

    @Override
    protected void onDestroy() {
        eventBus.unregister(this);
        super.onDestroy();
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
                    .show(fm, ERROR_DIALOG_FRAGMENT_TAG);
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
