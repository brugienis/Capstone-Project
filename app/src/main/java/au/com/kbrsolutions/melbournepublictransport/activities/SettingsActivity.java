package au.com.kbrsolutions.melbournepublictransport.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.utilities.Utility;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static au.com.kbrsolutions.melbournepublictransport.R.id.fab;
import static au.com.kbrsolutions.melbournepublictransport.activities.WidgetStopsActivity.WIDGET_LOCATION_NAME;

public class SettingsActivity extends AppCompatActivity {

    private SettingsFragment mSettingsFragment;
    private CoordinatorLayout mCoordinatorlayout;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AppBarLayout mAppBarLayout;
    private int mVerticalOffset;
    private String mFixedLocationAddress;
    private LatLng mLatLng;
    private String mStopId;
    private String mStopName;
    private boolean mFixedLocationPreferenceSummaryNotUpdated;
    private boolean mWidgetStopPreferenceSummaryNotUpdated;

    protected static final int PLACE_PICKER_REQUEST = 1000;
    protected static final int WIDGET_STOP_REQUEST = 2000;
    public static final String WIDGET_STOP_UPDATED =
            "au.com.kbrsolutions.melbournepublictransport.WIDGET_STOP_UPDATED";
    private static final String SETTINGS_TAG = "favorite_stops_tag";

    private static final String WIDGET_STOP_ID = "widget_stop_id";
    private static final String WIDGET_STOP_NAME = "widget_stop_name";
    private static final String FIXED_ADDRESS = "fixed_address";
    private static final String FIXED_LOCATION_PREFERENCE_SUMMARY_NOT_UPDATED =
            "fixed_location_preference_summary_not_updated";
    private static final String WIDGET_STOP_PREFERENCE_SUMMARY_NOT_UPDATED =
            "widget_stop_preference_summary_not_updated";

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

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mToolbar.setTitle(getResources().getString(R.string.title_settings));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                     @Override
                                                     public void onRefresh() {
                                                         handleRefresh();
                                                     }
                                                 }
        );
        ((FloatingActionButton) findViewById(fab)).hide();

        validatePreferenceSettings();

        mSettingsFragment = (SettingsFragment) getFragmentManager().findFragmentByTag(SETTINGS_TAG);
        if (mSettingsFragment == null) {
            mSettingsFragment = new SettingsFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.left_dynamic_fragments_frame, mSettingsFragment, SETTINGS_TAG)
                    .commit();
        }
    }

    private void saveAppBarVerticalOffset(int verticalOffset) {
        mVerticalOffset = verticalOffset;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utility.setAppBarVerticalOffset(getApplicationContext(), mVerticalOffset);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int verticalOffset = Utility.getAppBarVerticalOffset(getApplicationContext());
        if (mVerticalOffset != verticalOffset) {
            mVerticalOffset = verticalOffset;
            adjustAppBarVertivalOffset(verticalOffset * -1);
        }
    }

    /**
     * Based on http://stackoverflow.com/questions/33058496/set-starting-height-of-collapsingtoolbarlayout
     *
     * DmitryArc
     *
     * @param verticalOffset
     */
    private void adjustAppBarVertivalOffset(final int verticalOffset) {
        mAppBarLayout.post(new Runnable() {
            @Override
            public void run() {
                setAppBarOffset(verticalOffset);
            }
        });
    }

    /**
     * Based on http://stackoverflow.com/questions/33058496/set-starting-height-of-collapsingtoolbarlayout
     *
     * @param offsetPx
     */
    private void setAppBarOffset(int offsetPx) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        behavior.onNestedPreScroll(mCoordinatorlayout, mAppBarLayout, null, 0, offsetPx, new int[]{0, 0});
    }

    private void handleRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     *
     * If 'use fixed location' enabled and its latitude and longitude do not contain valid values,
     * change switch to 'use device location'.
     *
     * It has to be done before SettingsFragment is added.
     *
     */
    private void validatePreferenceSettings() {
        SharedPreferences sharedPreferences =
                getDefaultSharedPreferences(this);
        boolean useDeviceLocationValue = sharedPreferences.getBoolean(getString(R.string.pref_key_use_device_location), true);
        float currFixedLocationLatitude = sharedPreferences.getFloat(getString(R.string.pref_key_location_latitude), Float.NaN);
        float currFixedLocationLongitude = sharedPreferences.getFloat(getString(R.string.pref_key_location_longitude), Float.NaN);
        if (!useDeviceLocationValue && (Float.isNaN(currFixedLocationLatitude) || Float.isNaN(currFixedLocationLongitude))) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.pref_key_use_device_location), true);
            editor.apply();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                processPlacePickerData(data);
            } else {
                showSnackBar(R.string.fixed_location_could_not_select, false);
            }
        } else if (requestCode == WIDGET_STOP_REQUEST) {
            if (resultCode == RESULT_OK) {
                processWidgetStopDetails(data);
            } else {
                showSnackBar(R.string.widget_stop_could_not_select, false);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void processPlacePickerData(Intent data) {
        Place place = PlacePicker.getPlace(this, data);
        mFixedLocationAddress = place.getAddress().toString();
        mLatLng = place.getLatLng();

        // If the provided place doesn't have an address, we'll form a display-friendly
        // string from the latlng values.
        if (TextUtils.isEmpty(mFixedLocationAddress)) {
            mFixedLocationAddress = String.format("(%.2f, %.2f)", mLatLng.latitude, mLatLng.longitude);
        }

        SharedPreferences sharedPreferences =
                getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.pref_key_fixed_location), mFixedLocationAddress);

        // Also store the latitude and longitude so that we can use these to get a precise
        // result from our stops nearby search.
        editor.putFloat(getString(R.string.pref_key_location_latitude),
                (float) mLatLng.latitude);
        editor.putFloat(getString(R.string.pref_key_location_longitude),
                (float) mLatLng.longitude);
        editor.apply();
        mFixedLocationPreferenceSummaryNotUpdated = true;

        if (mSettingsFragment != null) {
            Preference locationPreference = mSettingsFragment.findPreference(getString(R.string.pref_key_fixed_location));
            if (locationPreference != null) {
                mSettingsFragment.setPreferenceSummary(locationPreference, mFixedLocationAddress);
                mFixedLocationPreferenceSummaryNotUpdated = false;
            }
        }

        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, getString(R.string.attribution_text),
                Snackbar.LENGTH_LONG).show();
    }

    private void processWidgetStopDetails(Intent data) {
        mStopId = data.getStringExtra(WIDGET_STOP_ID);
        mStopName = data.getStringExtra(WIDGET_LOCATION_NAME);
        SharedPreferences sharedPreferences =
                getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.pref_key_widget_stop_id), mStopId);
        editor.putString(getString(R.string.pref_key_widget_stop_name), mStopName);
        editor.apply();
        mWidgetStopPreferenceSummaryNotUpdated = true;
        if (mSettingsFragment != null) {
            Preference widgetStopPreference = mSettingsFragment.findPreference(getString(R.string.pref_key_widget_stop_name));
            if (widgetStopPreference != null) {
                mSettingsFragment.setPreferenceSummary(widgetStopPreference, mStopName);
                mWidgetStopPreferenceSummaryNotUpdated = false;
            }
        }
        sendBroadcastMessageToNextDeparturesWidget();
    }

    /**
     *
     * When widget stop change send broadcast message to the Next Departures widget.
     *
     */
    private void sendBroadcastMessageToNextDeparturesWidget() {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(WIDGET_STOP_UPDATED)
                .setPackage(getApplicationContext().getPackageName());
        sendBroadcast(dataUpdatedIntent);
    }

    /**
     *
     * Save 'fixed' location and 'widget' stop details.
     *
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFixedLocationPreferenceSummaryNotUpdated) {
            outState.putBoolean(FIXED_LOCATION_PREFERENCE_SUMMARY_NOT_UPDATED, mFixedLocationPreferenceSummaryNotUpdated);
            outState.putString(FIXED_ADDRESS, mFixedLocationAddress);
        }
        if (mWidgetStopPreferenceSummaryNotUpdated) {
            outState.putBoolean(WIDGET_STOP_PREFERENCE_SUMMARY_NOT_UPDATED, mWidgetStopPreferenceSummaryNotUpdated);
            outState.putString(WIDGET_STOP_ID, mStopId);
            outState.putString(WIDGET_STOP_NAME, mStopName);
        }
    }

    /**
     *
     * Retrieves saved data. If necessary create up to date summaries.
     *
     * The SettingsFragment's onCreate(...) is called before SettingsActivity's onCreate(...).
     *
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        Log.v(TAG, "onRestoreInstanceState - start");
        mFixedLocationPreferenceSummaryNotUpdated = savedInstanceState.getBoolean(FIXED_LOCATION_PREFERENCE_SUMMARY_NOT_UPDATED);
        mFixedLocationAddress = savedInstanceState.getString(FIXED_ADDRESS);
        if (mFixedLocationPreferenceSummaryNotUpdated && mSettingsFragment != null) {
            Preference fixedLocationPreference = mSettingsFragment.findPreference(getString(R.string.pref_key_fixed_location));
            if (fixedLocationPreference != null) {
                mSettingsFragment.setPreferenceSummary(fixedLocationPreference, mFixedLocationAddress);
                mFixedLocationPreferenceSummaryNotUpdated = false;
            }
        }
        mWidgetStopPreferenceSummaryNotUpdated = savedInstanceState.getBoolean(WIDGET_STOP_PREFERENCE_SUMMARY_NOT_UPDATED);
        mStopId = savedInstanceState.getString(WIDGET_STOP_ID);
        mStopName = savedInstanceState.getString(WIDGET_STOP_NAME);
        if (mWidgetStopPreferenceSummaryNotUpdated && mSettingsFragment != null && mStopName != null) {
            Preference widgetStopPreference = mSettingsFragment.findPreference(getString(R.string.pref_key_widget_stop_name));
            if (widgetStopPreference != null) {
                mSettingsFragment.setPreferenceSummary(widgetStopPreference, mStopName);
                mWidgetStopPreferenceSummaryNotUpdated = false;
            }
        }
    }

    /**
     * Up button was pressed - remove the top entry Back Stack
     */
    @Override
    public boolean onSupportNavigateUp() {
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
//        Log.v(TAG, "onSupportNavigateUp - start - cnt: " + cnt);
        getSupportFragmentManager().popBackStack();
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        int cnt = getSupportFragmentManager().getBackStackEntryCount();
//        Log.v(TAG, "onBackPressed - start - cnt: " + cnt);
        super.onBackPressed();
    }

    public void showSnackBar(int msg, boolean showIndefinite) {
        Snackbar
                .make(mCoordinatorlayout, msg, (showIndefinite ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_LONG))
                .setActionTextColor(Color.RED)
                .show(); // Don’t forget to show!
    }

    public static class SettingsFragment
            extends PreferenceFragment
            implements  Preference.OnPreferenceChangeListener {

        private float currFixedLocationLatitude;
        private float currFixedLocationLongitude;

        private final String TAG = ((Object) this).getClass().getSimpleName();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            
            // Get a reference to the application default shared preferences.
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

            currFixedLocationLatitude = sp.getFloat(getString(R.string.pref_key_location_latitude), Float.NaN);
            currFixedLocationLongitude = sp.getFloat(getString(R.string.pref_key_location_longitude), Float.NaN);

            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_fixed_location)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_widget_stop_name)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_use_device_location)));
        }

        /**
         * Attaches a listener so the summary is always updated with the preference value.
         * Also fires the listener once, to initialize the summary (so it shows up before the value
         * is changed.)
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(this);

            // Set the preference summaries
            if (preference instanceof SwitchPreference) {
                String key = preference.getKey();
                if (key.equals(getString(R.string.pref_key_use_device_location))) {
                    boolean useDeviceLocationValue = getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), true);
                    String summaryText;
                    if (useDeviceLocationValue) {
                        summaryText = getString(R.string.pref_summary_use_device_location);
                    } else {
                        summaryText = getString(R.string.pref_summary_use_fixed_location);
                    }
                    setPreferenceSummary(preference, summaryText);
                }
            } else {
                setPreferenceSummary(preference,
                        getDefaultSharedPreferences(preference.getContext())
                                .getString(preference.getKey(), ""));
            }
        }

        /**
         *
         * This gets called before the preference is changed - do all required validation in this method.
         *
         * Return 'false' if the new preference value is not correct. If 'true' is returned, the new
         * value will be accepted and onSharedPreferenceChanged(...) will be called.
         *
         * @param preference
         * @param value
         * @return true if the new value is acceptable and false if not.
         *
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String key = preference.getKey();
            if ((key == getString(R.string.pref_key_use_device_location))) {
                boolean newUseDeviceSwitchValue = (boolean) value;
                boolean validationOk = validateUseDeviceFixedLocationValue(newUseDeviceSwitchValue);
                if (validationOk) {
                    if (newUseDeviceSwitchValue) {
                        setPreferenceSummary(preference, getString(R.string.pref_summary_use_device_location));
                    } else {
                        setPreferenceSummary(preference, getString(R.string.pref_summary_use_fixed_location));
                    }
                }
                return validationOk;
            } else if ((key == getString(R.string.pref_key_fixed_location))) {
                setPreferenceSummary(preference, value);
            } else if ((key == getString(R.string.pref_key_widget_stop_name))) {
                setPreferenceSummary(preference, value);
            }
            return true;
        }

        private boolean validateUseDeviceFixedLocationValue(boolean newUseDeviceLocationOn) {
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            currFixedLocationLatitude = sp.getFloat(getString(R.string.pref_key_location_latitude), Float.NaN);
            currFixedLocationLongitude = sp.getFloat(getString(R.string.pref_key_location_longitude), Float.NaN);
            boolean currUseDeviceLocationOn = sp.getBoolean(getString(R.string.pref_key_use_device_location), true);
            if (currUseDeviceLocationOn && !newUseDeviceLocationOn &&
                    (Float.isNaN(currFixedLocationLatitude) || Float.isNaN(currFixedLocationLongitude) )) {
                Snackbar.make(getView(), getString(R.string.pref_err_use_device_location),
                        Snackbar.LENGTH_LONG).show();
                return false;
            }
            return true;
        }

        private void setPreferenceSummary(Preference preference, Object value) {
            String stringValue = value.toString();
//            String key = preference.getKey();
            preference.setSummary(stringValue);
        }
    }

}