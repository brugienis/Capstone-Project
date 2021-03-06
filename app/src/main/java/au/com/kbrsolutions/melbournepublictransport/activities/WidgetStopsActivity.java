package au.com.kbrsolutions.melbournepublictransport.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.fragments.FavoriteStopsFragment;
import au.com.kbrsolutions.melbournepublictransport.utilities.SharedPreferencesUtility;

import static au.com.kbrsolutions.melbournepublictransport.activities.MainActivity.FragmentsId.FAVORITE_STOPS;

public class WidgetStopsActivity
        extends AppCompatActivity
        implements FavoriteStopsFragment.OnFavoriteStopsFragmentInteractionListener {

    private CoordinatorLayout mCoordinatorlayout;
    private AppBarLayout mAppBarLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int mVerticalOffset;

    public static final String WIDGET_STOP_ID = "widget_stop_id";
    public static final String WIDGET_STOP_NAME = "widget_location_name";
    private static final String FAVORITE_STOPS_TAG = "favorite_stops_tag";

    @SuppressWarnings("unused")
    private final String TAG = ((Object) this).getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(getResources().getString(R.string.title_widget_stops));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                     @Override
                                                     public void onRefresh() {
                                                         handleRefresh();
                                                     }
                                                 }
        );
        ((FloatingActionButton) findViewById(R.id.fab)).hide();

        FavoriteStopsFragment favoriteStopsFragment = (FavoriteStopsFragment)
                getSupportFragmentManager().findFragmentByTag(FAVORITE_STOPS_TAG);
        if (favoriteStopsFragment == null) {
            favoriteStopsFragment = new FavoriteStopsFragment();
            favoriteStopsFragment.setFragmentId(FAVORITE_STOPS);
        }
        favoriteStopsFragment.setIsInSettingsActivityFlag();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.secondary_dynamic_fragments_frame, favoriteStopsFragment,
                        FAVORITE_STOPS_TAG)
                .commit();
    }

    private void saveAppBarVerticalOffset(int verticalOffset) {
        mVerticalOffset = verticalOffset;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferencesUtility.setAppBarVerticalOffset(getApplicationContext(), mVerticalOffset);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int verticalOffset = SharedPreferencesUtility.getAppBarVerticalOffset(
                getApplicationContext());
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

    private void setAppBarOffset(int offsetPx) {
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.onNestedPreScroll(mCoordinatorlayout, mAppBarLayout, null, 0, offsetPx,
                    new int[]{0, 0});
        }
    }

    /**
     * Hide the progress bar.
     */
    private void handleRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Set details of the selected stop into the Intent so SettingsActivity.onActivityResult(...)
     * can access them.
     *
     * @param stopId
     * @param locationName
     */
    @Override
    public void updateWidgetStopDetails(String stopId, String locationName) {
        Intent intent = new Intent();
        intent.putExtra(WIDGET_STOP_ID, stopId);
        intent.putExtra(WIDGET_STOP_NAME, locationName);
        setResult(Activity.RESULT_OK, intent);
        onSupportNavigateUp();
    }

    /**
     * Up button was pressed - remove to top entry Back Stack
     */
    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        onBackPressed();
        return true;
    }

    public void startNextDeparturesSearch(StopDetails stopDetails) {}
    public void showStopOnMap(String stopName, LatLngDetails latLonDetails) {}
    public void startStopsNearbySearch(boolean trainsOnly) {}
    public void getDisruptionsDetails() {}
    public void updateStopDetailRow(int id, String favoriteColumnValue) {}
    public void reloadDatabase() {}

}
