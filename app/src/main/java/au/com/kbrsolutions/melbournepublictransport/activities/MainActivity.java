package au.com.kbrsolutions.melbournepublictransport.activities;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
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
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopsNearbyDetails;
import au.com.kbrsolutions.melbournepublictransport.events.MainActivityEvents;
import au.com.kbrsolutions.melbournepublictransport.fragments.AboutFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.BaseFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.DisruptionsFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.FavoriteStopsFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.InitFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.NextDeparturesFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.StationOnMapFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.StopsFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.StopsNearbyFragment;
import au.com.kbrsolutions.melbournepublictransport.remote.RequestProcessorService;
import au.com.kbrsolutions.melbournepublictransport.utilities.CurrentGeoPositionFinder;
import au.com.kbrsolutions.melbournepublictransport.utilities.ProgressBarHandler;
import au.com.kbrsolutions.melbournepublictransport.utilities.SharedPreferencesUtility;

import static au.com.kbrsolutions.melbournepublictransport.activities.MainActivity.FragmentsId.FAVORITE_STOPS;
import static au.com.kbrsolutions.melbournepublictransport.activities.MainActivity.FragmentsId.NEXT_DEPARTURES;

// git push -u origin

public class MainActivity extends AppCompatActivity implements
        InitFragment.OnInitFragmentInteractionListener,
        FavoriteStopsFragment.OnFavoriteStopsFragmentInteractionListener,
        StopsFragment.OnStopFragmentInteractionListener,
        StopsNearbyFragment.OnNearbyStopsFragmentInteractionListener {

    private AboutFragment mAboutFragment;
    private InitFragment mInitFragment;
    private FavoriteStopsFragment mFavoriteStopsFragment;
    private StationOnMapFragment mStationOnMapFragment;
    private StopsFragment mStopsFragment;
    private NextDeparturesFragment mNextDeparturesFragment;
    private DisruptionsFragment mDisruptionsFragment;
    private StopsNearbyFragment mStopsNearbyFragment;
    private ProgressBarHandler mProgressBarHandler;
    private AppBarLayout mAppBarLayout;
    private CoordinatorLayout mCoordinatorlayout;
    private boolean mTwoPane;
    private int mVerticalOffset;

    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FloatingActionButton fab;
    private EventBus eventBus;
    private CurrentGeoPositionFinder mCurrentGeoPositionFinder;
    private static final String STATE_IN_PERMISSION_CHECKING = "inPermission";
    private static final String STATE_IN_SEARCH_FOR_STOPS_NEARBY_FOR_TRAINS_ONLY = "for_trains_only";
    private boolean isInPermissionChecking = false;

    private static final int REQUEST_PERMS_ACCESS_FINE_LOCATION = 2000;
    private static final String FAVORITE_STOPS_TAG = "favorite_stops_tag";
    private static final String ERROR_DIALOG_FRAGMENT_TAG = "errorDialog";
    private static final String STATION_ON_MAP_TAG = "station_on_map_tag";
    private static final String STOP_TAG = "stop_tag";
    private static final String NEXT_DEPARTURES_TAG = "next_departures_tag";
    private static final String DISRUPTION_TAG = "disruption_tag";
    private static final String NEARBY_TAG = "nearby_tag";
    private static final String ABOUT_TAG = "about_tag";
    private static final String INIT_TAG = "init_tag";
    private static final String TRANSPORT_MODE_METRO_TRAIN = "metro-train";

    public enum FragmentsId {
        DISRUPTIONS,
        ABOUT,
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (eventBus == null) {
            eventBus = EventBus.getDefault();
            eventBus.register(this);
        }
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
        mCoordinatorlayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mAppBarLayout.setExpanded(false);
        }
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                saveAppBarVerticalOffset(verticalOffset);
            }
        });

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);

        mToolbar.setTitle(getResources().getString(R.string.title_favorite_stops));

        boolean isRightToLeft = getResources().getBoolean(R.bool.is_right_to_left);

        /* Nico's solution. Not required in Android 7.1. */
        /* https://discussions.udacity.com/t/layout-mirroring-rtl-isnt-supported-correctly-for-app-bar/177336/4 */
        if (isRightToLeft) {
            mToolbar.setNavigationIcon(R.mipmap.ic_arrow_right_white_24dp);
        } else {
            mToolbar.setNavigationIcon(R.mipmap.ic_arrow_left_white_24dp);
        }

        setSupportActionBar(mToolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                     @Override
                                                     public void onRefresh() {
                                                         handleRefresh();
                                                     }
                                                 }
        );

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {

                        showTopViewOnBackStackChanged();

                        int cnt = getSupportFragmentManager().getBackStackEntryCount();
                        if (!mTwoPane && cnt == 0 || mTwoPane && cnt == 1) {
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            }
                            if (mFavoriteStopsFragment != null) {
                                mFavoriteStopsFragment.setShowOptionsMenuFlg(true);
                            }
                        } else {
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            }
                            if (mFavoriteStopsFragment != null) {
                                mFavoriteStopsFragment.setShowOptionsMenuFlg(false);
                            }
                        }
                    }
                });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFabClicked();
            }
        });
        fab.setContentDescription(getString(R.string.pref_desc_main_add_favorite_stop));

        findFragments();

        collapsingToolbar.setTitle(getResources().getString(R.string.title_favorite_stops));

        String topFragmentTag = getTopFragmentTag();

        // The detail container view will be present only in the large-screen layouts
        // (res/layout-sw600dp). If this view is present, then the activity should be
        // in two-pane mode.
        mTwoPane = findViewById(R.id.primary_dynamic_fragments_frame) != null;
        if (savedInstanceState != null) {   /* configuration changed */
//            printBackStackFragments();
            if (topFragmentTag.equals(FAVORITE_STOPS_TAG) ||
                    topFragmentTag.equals(NEXT_DEPARTURES_TAG)) {
                fab.show();
            } else {
                fab.hide();
            }
            int cnt = getSupportFragmentManager().getBackStackEntryCount();
            if (!mTwoPane && cnt == 0 || mTwoPane && cnt == 1) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            } else {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
            isInPermissionChecking = savedInstanceState.getBoolean(STATE_IN_PERMISSION_CHECKING,
                    false);
        } else {
            if (topFragmentTag == null || !topFragmentTag.equals(INIT_TAG)) {
                if (SharedPreferencesUtility.isDatabaseLoaded(getApplicationContext())) {
                    showFavoriteStops();
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }
                    mFavoriteStopsFragment.setShowOptionsMenuFlg(true);
                    if (mTwoPane) {
                        showStopsFragment();
                    }
                } else {
                    loadDatabase();
                }
            }
        }
        mProgressBarHandler = new ProgressBarHandler(this);

    }

    private void saveAppBarVerticalOffset(int verticalOffset) {
        mVerticalOffset = verticalOffset;
    }

    /**
     * Save vertical offset value.
     */
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferencesUtility.setAppBarVerticalOffset(getApplicationContext(), mVerticalOffset);
    }

    /**
     * Retrieve vertical offset value.
     */
    @Override
    protected void onResume() {
        super.onResume();
        int verticalOffset =
                SharedPreferencesUtility.getAppBarVerticalOffset(getApplicationContext());
        if (mVerticalOffset != verticalOffset) {
            mVerticalOffset = verticalOffset;
            adjustAppBarVerticalOffset(verticalOffset * -1);
        }
    }

    /**
     * Based on http://stackoverflow.com/questions/33058496/set-starting-height-of-collapsingtoolbarlayout
     *
     * DmitryArc
     *
     * @param verticalOffset
     */
    private void adjustAppBarVerticalOffset(final int verticalOffset) {
        mAppBarLayout.post(new Runnable() {
            @Override
            public void run() {
                setAppBarOffset(verticalOffset);
            }
        });
    }

    private void setAppBarOffset(int offsetPx){
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.onNestedPreScroll(mCoordinatorlayout, mAppBarLayout, null, 0, offsetPx,
                    new int[]{0, 0});
        }
    }

    private void handleRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Start initial database load process.
     */
    private void loadDatabase() {
        if (mInitFragment == null) {
            mInitFragment = new InitFragment();
            mInitFragment.setFragmentId(FragmentsId.INIT);
            mInitFragment.setActionBarTitle(getResources().getString(R.string.title_data_load));
            mInitFragment.setActionBarTitle(mInitFragment.getActionBarTitle());
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.secondary_dynamic_fragments_frame, mInitFragment, INIT_TAG)
                .addToBackStack(INIT_TAG)
                .commit();
        Intent intent = new Intent(this, RequestProcessorService.class);
        intent.putExtra(RequestProcessorService.REQUEST,
                RequestProcessorService.ACTION_LOAD_OR_REFRESH_DATA);
        intent.putExtra(RequestProcessorService.REFRESH_DATA_IF_TABLES_EMPTY, false);
        startService(intent);
    }

    /**
     * This method is needed for testing.
     *
     * Remove before publishing on Google Play.
     */
    @Override
    public void reloadDatabase() {
        if (mTwoPane) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(mStopsFragment)
                    .commit();
            getSupportFragmentManager().popBackStack();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(mFavoriteStopsFragment)
                    .commit();
            getSupportFragmentManager().popBackStack();
        }

        SharedPreferencesUtility.setDatabaseLoadedFlg(getApplicationContext(), false);

        loadDatabase();
    }

    public void databaseLoadFinished() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(mInitFragment)
                .commit();
        getSupportFragmentManager().popBackStack();

        SharedPreferencesUtility.setDatabaseLoadedFlg(getApplicationContext(), true);
        if (mTwoPane) {
            showStopsFragment();
        } else {
            showFavoriteStops();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_IN_PERMISSION_CHECKING, isInPermissionChecking);
        outState.putBoolean(STATE_IN_SEARCH_FOR_STOPS_NEARBY_FOR_TRAINS_ONLY,
                mSearchForStopsNearbyForTrainsOnly);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        showTopFragment();
        if (mTwoPane) {
            mFavoriteStopsFragment.showView();
        }
    }

    private boolean hasLocationPermission() {
        return(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        isInPermissionChecking =false;

        if (requestCode == REQUEST_PERMS_ACCESS_FINE_LOCATION) {
            if (hasLocationPermission()) {
                startStopsNearbySearch(mSearchForStopsNearbyForTrainsOnly);
            }
        }
    }

    private void handleFabClicked() {
        BaseFragment topFragment = getTopFragment();
        String topFragmentTag = getTopFragmentTag();
        FragmentsId fragmentsId;
        if (topFragment != null) {
            fragmentsId = topFragment.getFragmentId();
            if (fragmentsId != null &&
                    fragmentsId != FAVORITE_STOPS &&
                    fragmentsId != NEXT_DEPARTURES
                    ) {
                return;
            }
        } else {
            fragmentsId = FAVORITE_STOPS;
        }
        switch (topFragmentTag) {
            case FAVORITE_STOPS_TAG:
                showStopsFragment();
                fab.hide();
            break;

            case NEXT_DEPARTURES_TAG:
                startNextDeparturesSearch(mNextDeparturesFragment.getSearchStopDetails());
            break;

            default:
                if (!SharedPreferencesUtility.isReleaseVersion(getApplicationContext())) {
                    throw new RuntimeException(
                            "LOC_CAT_TAG - handleFabClicked - no code to handle fragmentsId: " +
                                    "" + fragmentsId);
                }

        }
    }

    /**
     * Show all train stops that are not 'favorite' once.
     */
    private void showStopsFragment() {
        if (mStopsFragment == null) {
            mStopsFragment = new StopsFragment();
            mStopsFragment.setFragmentId(FragmentsId.STOPS);
        }
        if (!mTwoPane) {
            mFavoriteStopsFragment.hideView();
        }
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.secondary_dynamic_fragments_frame, mStopsFragment, STOP_TAG)
                    .addToBackStack(STOP_TAG)
                    .commit();
//        }
        mStopsFragment.setActionBarTitle(getResources().getString(R.string.title_stops));
    }

    /**
     * Make the fragment that is at the top of the Back Stack visible.
     */
    private void showTopViewOnBackStackChanged() {
        BaseFragment baseFragment = getTopFragment();
        boolean showFab = false;
        if (baseFragment != null) {     /*  */
            FragmentsId fragmentsId = baseFragment.getFragmentId();
                if (fragmentsId == FAVORITE_STOPS ||
                        fragmentsId == FragmentsId.STOPS ||
                        fragmentsId == FragmentsId.STOPS_NEARBY) {
                    baseFragment.showView();
                }
                if (fragmentsId == FAVORITE_STOPS ||
                        fragmentsId == NEXT_DEPARTURES) {
                    showFab = true;
                }
                mToolbar.setTitle(baseFragment.getActionBarTitle());
        }
        if (showFab) {
            fab.show();
        } else {
            fab.hide();
        }
    }

    /**
     * Hide some views.
     */
    private void hideViewIfRequired() {
        BaseFragment baseFragment = getTopFragment();
            FragmentsId fragmentsId = baseFragment.getFragmentId();
            if (fragmentsId == FAVORITE_STOPS ||
                            fragmentsId == FragmentsId.STOPS ||
                            fragmentsId == FragmentsId.STOPS_NEARBY) {
                baseFragment.hideView();
            }
    }

    /**
     *
     * Show results of the 'next departures' search.
     *
     * @param nextDepartureDetailsList
     * @param stopDetails
     */
    private void showNextDepartures(
            List<NextDepartureDetails> nextDepartureDetailsList,
            StopDetails stopDetails) {
        if (mNextDeparturesFragment == null) {
            mNextDeparturesFragment = NextDeparturesFragment.newInstance(
                    stopDetails.locationName,
                    nextDepartureDetailsList,
                    stopDetails);
            mNextDeparturesFragment.setFragmentId(NEXT_DEPARTURES);
        } else {
            mNextDeparturesFragment.setNewContent(
                    stopDetails.locationName,
                    nextDepartureDetailsList,
                    stopDetails);
        }
        String topFragmentTag = getTopFragmentTag();
        hideProgress();
        if (topFragmentTag.equals(NEXT_DEPARTURES_TAG)) {
            return;
        } else if (mTwoPane && getSupportFragmentManager().getBackStackEntryCount() > 1) {
            showSnackBar(R.string.touch_up_button_first, false);
            return;
        }
        hideViewIfRequired();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.secondary_dynamic_fragments_frame,
                        mNextDeparturesFragment, NEXT_DEPARTURES_TAG)
                .addToBackStack(NEXT_DEPARTURES_TAG)
                .commit();
        mNextDeparturesFragment.setActionBarTitle(getResources().getString(R.string.title_next_departures));
        fab.setImageResource(R.drawable.ic_stock_refresh_white);
        fab.setContentDescription(getString(R.string.pref_desc_main_refresh_next_departures));
    }

    /**
     * Initiate search for train 'disruptions' information.
     */
    public void getDisruptionsDetails() {
        showProgress();
        String trainMode = TRANSPORT_MODE_METRO_TRAIN;
        Intent intent = new Intent(this, RequestProcessorService.class);
        intent.putExtra(RequestProcessorService.REQUEST,
                RequestProcessorService.ACTION_GET_DISRUPTIONS_DETAILS);
        intent.putExtra(RequestProcessorService.MODES, trainMode);
        startService(intent);
    }

    /**
     * Send request to update selected train's stop 'favorite' flag.
     *
     * @param id
     * @param favoriteColumnValue
     */
    @Override
    public void updateStopDetailRow(int id, String favoriteColumnValue) {
        showProgress();
        Intent intent = new Intent(this, RequestProcessorService.class);
        intent.putExtra(RequestProcessorService.REQUEST,
                RequestProcessorService.ACTION_UPDATE_STOPS_DETAILS);
        intent.putExtra(RequestProcessorService.ROW_ID, id);
        intent.putExtra(RequestProcessorService.FAVORITE_COLUMN_VALUE, favoriteColumnValue);
        startService(intent);
    }

    private boolean mSearchForStopsNearbyForTrainsOnly;
    /**
     * Start search of stops 'nearby' - relative to device or 'fixed' (depending on settings
     * values) location.
     *
     * @param forTrainsOnly
     */
    @Override
    public void startStopsNearbySearch(boolean forTrainsOnly) {
        mSearchForStopsNearbyForTrainsOnly = forTrainsOnly;
        boolean showExplainingMsg = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showExplainingMsg =
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (hasLocationPermission()) {
            if (SharedPreferencesUtility.useDeviceLocation(this)) {
                if (mCurrentGeoPositionFinder == null) {
                    mCurrentGeoPositionFinder = new CurrentGeoPositionFinder(getApplicationContext(),
                            forTrainsOnly);
                } else {
                    mCurrentGeoPositionFinder.connectToGoogleApiClient(forTrainsOnly);
                }
            } else {
                getStopsNearbyDetails(SharedPreferencesUtility.getFixedLocationLatLng(this),
                        forTrainsOnly);
            }
        } else {
            if (showExplainingMsg) {
                showSnackBar(getString(R.string.see_menu_about), false);
            }
            if (!isInPermissionChecking) {
                isInPermissionChecking = true;

                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMS_ACCESS_FINE_LOCATION);
            }
        }
    }

    /**
     * Initiate search for stops 'nearby'.
     *
     * @param latLonDetails
     * @param forTrainsOnly
     */
    private void getStopsNearbyDetails(LatLngDetails latLonDetails, boolean forTrainsOnly) {
        showProgress();
        Intent intent = new Intent(this, RequestProcessorService.class);
        if (forTrainsOnly) {
            intent.putExtra(RequestProcessorService.REQUEST,
                    RequestProcessorService.ACTION_GET_TRAIN_STOPS_NEARBY_DETAILS);
        } else {
            intent.putExtra(RequestProcessorService.REQUEST,
                    RequestProcessorService.ACTION_GET_STOPS_NEARBY_DETAILS);
        }
        intent.putExtra(RequestProcessorService.LAT_LON, latLonDetails);
        startService(intent);
    }

    /**
     * Show information about this app.
     */
    private void showAboutFragment() {
        if (mAboutFragment == null) {
            mAboutFragment = new AboutFragment();
            mAboutFragment.setFragmentId(FragmentsId.ABOUT);
        }
        if (!mTwoPane) {
            mFavoriteStopsFragment.hideView();
        }
        if (!mTwoPane) {
            mFavoriteStopsFragment.hideView();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.secondary_dynamic_fragments_frame, mAboutFragment, ABOUT_TAG)
                .addToBackStack(ABOUT_TAG)
                .commit();
        mAboutFragment.setActionBarTitle(getResources().getString(R.string.title_about));
        hideProgress();
    }

    /**
     * Show results of the search for disruptions on train network.
     * @param disruptionsDetailsList
     */
    private void showDisruptions(List<DisruptionsDetails> disruptionsDetailsList) {
        if (mDisruptionsFragment == null) {
            mDisruptionsFragment = DisruptionsFragment.newInstance(disruptionsDetailsList);
            mDisruptionsFragment.setFragmentId(FragmentsId.DISRUPTIONS);
        } else {
            mDisruptionsFragment.setNewContent(disruptionsDetailsList);
        }
        if (!mTwoPane) {
            mFavoriteStopsFragment.hideView();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.secondary_dynamic_fragments_frame, mDisruptionsFragment, DISRUPTION_TAG)
                .addToBackStack(DISRUPTION_TAG)
                .commit();
        mDisruptionsFragment.setActionBarTitle(getResources().getString(R.string.title_disruptions));
        hideProgress();
    }

    /**
     * Up button was pressed - remove to top entry Back Stack
     */
    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        fab.setImageResource(R.drawable.ic_stock_add_circle_white);
        fab.setContentDescription(getString(R.string.pref_desc_main_add_favorite_stop));
        return true;
    }

    /**
     * Send request to find 'next' departures for the selected train station.
     * @param stopDetails
     */
    public void startNextDeparturesSearch(StopDetails stopDetails) {
        showProgress();
        Intent intent = new Intent(this, RequestProcessorService.class);
        intent.putExtra(RequestProcessorService.REQUEST,
                RequestProcessorService.ACTION_SHOW_NEXT_DEPARTURES);
        Bundle mBundle = new Bundle();
        mBundle.putParcelable(RequestProcessorService.STOP_DETAILS, stopDetails);
        intent.putExtras(mBundle);
        intent.putExtra(RequestProcessorService.LIMIT, 5);
        startService(intent);
    }

    @Override
    public void updateWidgetStopDetails(String stopId, String locationName) {
        // this method is used in SettingsActivity
    }

    /**
     * Show favorite stops.
     */
    private void showFavoriteStops() {
        Log.v(TAG, "showFavoriteStops - start");
        if (mFavoriteStopsFragment == null) {
            mFavoriteStopsFragment = new FavoriteStopsFragment();
            mFavoriteStopsFragment.setFragmentId(FAVORITE_STOPS);
            mFavoriteStopsFragment.setActionBarTitle(getResources().getString(R.string.title_favorite_stops));
        }
        if (mTwoPane) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.primary_dynamic_fragments_frame, mFavoriteStopsFragment,
                            FAVORITE_STOPS_TAG)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.secondary_dynamic_fragments_frame, mFavoriteStopsFragment,
                            FAVORITE_STOPS_TAG)
                    .commit();
        }
    }

    /**
     * Show 'favorite' stops after new one was added.
     */
    public void showUpdatedFavoriteStops() {
        if (mFavoriteStopsFragment != null) {
            mFavoriteStopsFragment.showView();
            mFavoriteStopsFragment.reloadLoader();
            mFavoriteStopsFragment.setFragmentId(FAVORITE_STOPS);
        }
        StopsFragment stopsFragment =
                (StopsFragment) getSupportFragmentManager().findFragmentByTag(STOP_TAG);
        if (!mTwoPane && stopsFragment != null)  {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(stopsFragment)
                    .commit();

            getSupportFragmentManager().popBackStack();
        }
        hideProgress();
    }

    /**
     * Show selected train on the map.
     *
     * @param stopName
     * @param latLonDetails
     */
    @Override
    public void showStopOnMap(String stopName, LatLngDetails latLonDetails) {
        if (readyToGo()) {
            fab.hide();
            if (mStationOnMapFragment == null) {
                mStationOnMapFragment = StationOnMapFragment.newInstance(
                        stopName,
                        latLonDetails.latitude,
                        latLonDetails.longitude,
                        mTwoPane);
                mStationOnMapFragment.setFragmentId(FragmentsId.STATION_ON_MAP);
            } else {
                mStationOnMapFragment.setLatLon(
                        stopName,
                        latLonDetails.latitude,
                        latLonDetails.longitude);
            }
            if (!mTwoPane) {
                mFavoriteStopsFragment.hideView();
            }
            String topFragmentTag = getTopFragmentTag();
            if (topFragmentTag.equals(STATION_ON_MAP_TAG)) {
                return;
            } else if (mTwoPane && getSupportFragmentManager().getBackStackEntryCount() > 1) {
                showSnackBar(R.string.touch_up_button_first, false);
                return;
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.secondary_dynamic_fragments_frame, mStationOnMapFragment,
                            STATION_ON_MAP_TAG)
                    .addToBackStack(STATION_ON_MAP_TAG)
                    .commit();
            mStationOnMapFragment.setActionBarTitle(getResources().getString(R.string.title_stop_on_map));
        }
    }

    /**
     * Show search result for the 'stops' nearby - relative to the device or 'fixed' position.
     *
     * @param nearbyStopsDetailsList
     * @param forTrainsStopsNearby
     */
    private void showStopsNearby(
            List<StopsNearbyDetails> nearbyStopsDetailsList,
            boolean forTrainsStopsNearby) {

        if (mStopsNearbyFragment == null) {
            mStopsNearbyFragment = StopsNearbyFragment.newInstance(nearbyStopsDetailsList);
            mStopsNearbyFragment.setFragmentId(FragmentsId.STOPS_NEARBY);
        } else {
            mStopsNearbyFragment.setNewContent(nearbyStopsDetailsList);
        }
        if (!mTwoPane) {
            mFavoriteStopsFragment.hideView();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.secondary_dynamic_fragments_frame, mStopsNearbyFragment, NEARBY_TAG)
                .addToBackStack(NEARBY_TAG)
                .commit();
        mStopsNearbyFragment.setActionBarTitle(forTrainsStopsNearby ?
                getResources().getString(R.string.title_train_stops_nearby) :
                getResources().getString(R.string.title_stops_nearby)
        );
        fab.hide();
        hideProgress();
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

        if (id == R.id.action_about) {
            showAboutFragment();
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } else {
                startActivity(intent);
            }
            return true;
        }
        // FIXME: 28/10/2016  - remove action_clear_settings after testings done
        else if (id == R.id.action_clear_settings) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(getString(R.string.pref_key_location_latitude));
            editor.remove(getString(R.string.pref_key_location_longitude));
            editor.remove(getString(R.string.pref_key_fixed_location));
            editor.remove(getString(R.string.pref_key_widget_stop_name));
            editor.remove(getString(R.string.pref_key_widget_stop_id));
            editor.apply();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Get messages through Event Bus from Green Robot.
     *
     * This method will be called when a MainActivityEvents is posted (in the UI thread)
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MainActivityEvents event) {
        MainActivityEvents.MainEvents requestEvent = event.event;
        switch (requestEvent) {

            case REMOTE_ACCESS_PROBLEMS:
                showSnackBar(event.msg, true);
                hideProgress();
                break;

            case NEXT_DEPARTURES_DETAILS:
                showNextDepartures(event.nextDepartureDetailsList, event.stopDetails);
                break;

            case DISRUPTIONS_DETAILS:
                showDisruptions(event.disruptionsDetailsList);
                break;

            case CURR_LOCATION_DETAILS:
                getStopsNearbyDetails(event.latLonDetails, event.forTrainsStopsNearby);
                break;

            case REFRESH_FAVORITE_STOPS_VIEW:
                showUpdatedFavoriteStops();
                break;

            case NEARBY_LOCATION_DETAILS:
                showStopsNearby(event.nearbyStopsDetailsList, event.forTrainsStopsNearby);
                break;

            case DATABASE_LOAD_PROGRESS:
                handleDatabaseLoadProgress(event.databaseLoadProgress, event.databaseLoadTarget);
                break;

            default:
                if (!SharedPreferencesUtility.isReleaseVersion(getApplicationContext())) {
                    throw new RuntimeException("LOC_CAT_TAG - onEvent - no code to handle requestEvent: "
                            + requestEvent);
                }
        }
    }

    /**
     * Update progress bar.
     *
     * @param databaseLoadProgress
     * @param databaseLoadTarget
     */
    private void handleDatabaseLoadProgress(int databaseLoadProgress, int databaseLoadTarget) {
        if (mInitFragment != null) {
            mInitFragment.updateDatabaseLoadProgress(databaseLoadProgress, databaseLoadTarget);
        }
    }

    /**
     * Show message at the bottom of the screen.
     *
     * @param msg
     * @param showIndefinite
     */
    private void showSnackBar(String msg, boolean showIndefinite) {
        Snackbar
                .make(mCoordinatorlayout, msg,
                        (showIndefinite ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_LONG))
                .setActionTextColor(Color.RED)
                .show(); // Don’t forget to show!
    }

    private void showSnackBar(int msg, boolean showIndefinite) {
        Snackbar
                .make(mCoordinatorlayout, msg,
                        (showIndefinite ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_LONG))
                .setActionTextColor(Color.RED)
                .show(); // Don’t forget to show!
    }

    /**
     * Make progress bar visible
     */
    private void showProgress() {
        mProgressBarHandler.show();
    }


    /**
     * Make progress bar invisible
     */
    private void hideProgress() {
        mProgressBarHandler.hide();
    }

    /**
     * Make fragment at the top of Back Stack visible.
     */
    private void showTopFragment() {
        Fragment topFragment = null;
        if (mFavoriteStopsFragment != null) {
            mFavoriteStopsFragment.hideView();
            topFragment = mFavoriteStopsFragment;
        }
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        String tag;
        for (int i = 0; i < cnt; i++) {
            tag = getSupportFragmentManager().getBackStackEntryAt(i).getName();
            topFragment = getSupportFragmentManager().findFragmentByTag(tag);
            ((BaseFragment)topFragment).hideView();
        }
        if (topFragment != null) {
            ((BaseFragment)topFragment).showView();
            mToolbar.setTitle(((BaseFragment)topFragment).getActionBarTitle());
        }
    }

    /**
     * Use for testing - remove before publishing on Google Play.
     */
    private void printBackStackFragments() {
        BaseFragment topFragment;
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        String tag;
        for (int i = 0; i < cnt; i++) {
            tag = getSupportFragmentManager().getBackStackEntryAt(i).getName();
            topFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
//            Log.v(TAG, "printBackStackFragments - fragment: " + i + " - " + topFragment + "/" +
//                    topFragment.getFragmentId());
        }
    }

    /**
     * Use for testing - remove before publishing on Google Play.
     */
    @SuppressWarnings("unused")
    private void showFragmentsOnBackStackVisibility() {
        BaseFragment topFragment;
        if (mFavoriteStopsFragment != null) {
            mFavoriteStopsFragment.isRootViewVisible();
        }
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        String tag;
        for (int i = 0; i < cnt; i++) {
            tag = getSupportFragmentManager().getBackStackEntryAt(i).getName();
            topFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
            topFragment.isRootViewVisible();
//            Log.v(TAG, "showFragmentsOnBackStackVisibility - fragment: " + i + " - " +
//              opFragment + "/" + topFragment.getFragmentId());
        }
    }

    /**
     * Return fragment that is at the top of Back Stack.
     */
    private BaseFragment getTopFragment() {
        BaseFragment topFragment;
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        if (cnt == 0) {
            topFragment = mFavoriteStopsFragment;
        } else  {
            String tag = getSupportFragmentManager().getBackStackEntryAt(cnt - 1).getName();
            topFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
        }
        return topFragment;
    }


    /**
     * Return tah of the fragment that is at the top of Back Stack.
     */
    private String getTopFragmentTag() {
        String tag;
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        if (cnt > 0) {
            tag = getSupportFragmentManager().getBackStackEntryAt(cnt - 1).getName();
        } else {
            tag = FAVORITE_STOPS_TAG;
        }
        return tag;
    }

    /**
     * Restore all fragments that were tracked by the FragmentManager.
     */
    private void findFragments() {
        BaseFragment topFragment = mFavoriteStopsFragment =
                (FavoriteStopsFragment) getSupportFragmentManager().
                        findFragmentByTag(FAVORITE_STOPS_TAG);
        if (topFragment != null) {
            topFragment.hideView();
        }
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
        String tag;
        for (int i = 0; i < cnt; i++) {
            tag = getSupportFragmentManager().getBackStackEntryAt(i).getName();
            topFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
            topFragment.hideView();
            switch (tag) {

                case STATION_ON_MAP_TAG:
                    mStationOnMapFragment = (StationOnMapFragment) topFragment;
                    break;

                case STOP_TAG:
                    mStopsFragment = (StopsFragment) topFragment;
                    break;

                case NEXT_DEPARTURES_TAG:
                    mNextDeparturesFragment = (NextDeparturesFragment) topFragment;
                    break;

                case DISRUPTION_TAG:
                    mDisruptionsFragment = (DisruptionsFragment) topFragment;
                    break;

                case NEARBY_TAG:
                    mStopsNearbyFragment = (StopsNearbyFragment) topFragment;
                    break;

                case INIT_TAG:
                    mInitFragment = (InitFragment) topFragment;
                    break;

                default:
                    if (!SharedPreferencesUtility.isReleaseVersion(getApplicationContext())) {
                        throw new RuntimeException(TAG + ".findFragments - no code to handle tag: " + tag);
                    }
            }
        }
        if (topFragment != null) {
            topFragment.showView();
        }
    }

    /**
     *
     * Handle D-pad/keyboard keys.
     *
     * Based on henry74918
     *
     *  http://stackoverflow.com/questions/14392356/how-to-use-d-pad-navigate-switch-between-listviews-row-and-its-decendants-goo
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        BaseFragment baseFragment = getTopFragment();
        boolean resultOk;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                resultOk = baseFragment.handleHorizontalDpadKeys(false);
                if (resultOk) {
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                resultOk = baseFragment.handleHorizontalDpadKeys(true);
                if (resultOk) {
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_DPAD_UP:
                resultOk = baseFragment.handleVerticalDpadKeys(true);
                if (resultOk) {
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                resultOk = baseFragment.handleVerticalDpadKeys(false);
                if (resultOk) {
                    return true;
                }
                break;

            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
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

        @NonNull
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

    // The source of the code below - The Busy Coder's Guide
    private boolean readyToGo() {
        GoogleApiAvailability checker =
                GoogleApiAvailability.getInstance();

        int status = checker.isGooglePlayServicesAvailable(this);

        if (status == ConnectionResult.SUCCESS) {
            if (getVersionFromPackageManager(this) >= 2) {
                return (true);
            } else {
                showSnackBar(R.string.no_maps, true);
                finish();
            }
        } else if (checker.isUserResolvableError(status)) {
            FragmentManager fm = getSupportFragmentManager();
            ErrorDialogFragment.newInstance(status)
                    .show(fm, ERROR_DIALOG_FRAGMENT_TAG);
        } else {
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
