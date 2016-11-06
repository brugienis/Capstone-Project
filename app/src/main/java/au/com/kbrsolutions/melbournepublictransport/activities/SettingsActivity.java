package au.com.kbrsolutions.melbournepublictransport.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.utilities.Utility;

import static au.com.kbrsolutions.melbournepublictransport.R.id.fab;
import static au.com.kbrsolutions.melbournepublictransport.activities.WidgetStopsActivity.WIDGET_LOCATION_NAME;

public class SettingsActivity extends AppCompatActivity {

    private ImageView mAttribution;
    private SettingsFragment mSettingsFragment;
    private CoordinatorLayout mCoordinatorlayout;
    private ActionBar actionBar;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private ImageView collapsingToolbarImage;
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
    private static final String FIXED_LOCATION_PREFERENCE_SUMMARY_NOT_UPDATED = "fixed_location_preference_summary_not_updated";
    private static final String WIDGET_STOP_PREFERENCE_SUMMARY_NOT_UPDATED = "widget_stop_preference_summary_not_updated";

    private final String TAG = ((Object) this).getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.v(TAG, "onCreate - start: ");
        setContentView(R.layout.activity_main);

        mCoordinatorlayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            Log.v(TAG, "onCreate - mVerticalOffset: " + mVerticalOffset);
//            mAppBarLayout.setExpanded(true);
//        } else {
            mAppBarLayout.setExpanded(false);
        }
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                Log.v(TAG, "onOffsetChanged - verticalOffset: " + verticalOffset);
                saveAppBarVerticalOffset(verticalOffset);
            }
        });

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mCollapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);

        mToolbar.setTitle(getResources().getString(R.string.title_settings));

        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
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

//        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
        mSettingsFragment = (SettingsFragment) getFragmentManager().findFragmentByTag(SETTINGS_TAG);
        if (mSettingsFragment == null) {
            mSettingsFragment = new SettingsFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.left_dynamic_fragments_frame, mSettingsFragment, SETTINGS_TAG)
//                    .addToBackStack(SETTINGS_TAG)
                    .commit();

            // If we are using a PlacePicker location, we need to show attributions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mAttribution = new ImageView(this);
                mAttribution.setImageResource(R.drawable.powered_by_google_light);

                if (!Utility.isLocationLatLonAvailable(this)) {
                    mAttribution.setVisibility(View.GONE);
                }

                // FIXME: 19/10/2016 - check how to set attributions
//                setListFooter(mAttribution);
            }
        }
//        Log.v(TAG, "onCreate - end: ");
    }

    private void saveAppBarVerticalOffset(int verticalOffset) {
        mVerticalOffset = verticalOffset;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utility.setAppBarVerticalOffset(getApplicationContext(), mVerticalOffset);
//        Log.v(TAG, "onPause - mVerticalOffset: " + mVerticalOffset);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int verticalOffset = Utility.getAppBarVerticalOffset(getApplicationContext());
//        Log.v(TAG, "onResume - mVerticalOffset/verticalOffset: " + mVerticalOffset + "/" + verticalOffset);
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
     * change switch to 'use device location.
     *
     * It has to be done before SettingsFragment is added.
     *
     */
    private void validatePreferenceSettings() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDeviceLocationValue = sharedPreferences.getBoolean(getString(R.string.pref_key_use_device_location), true);
        float currFixedLocationLatitude = sharedPreferences.getFloat(getString(R.string.pref_key_location_latitude), Float.NaN);
        float currFixedLocationLongitude = sharedPreferences.getFloat(getString(R.string.pref_key_location_longitude), Float.NaN);
//        Log.v(TAG, "onCreate - useDeviceLocationValue: " + useDeviceLocationValue + "/" + currFixedLocationLatitude + "/" + currFixedLocationLongitude);
        if (!useDeviceLocationValue && (Float.isNaN(currFixedLocationLatitude) || Float.isNaN(currFixedLocationLongitude))) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.pref_key_use_device_location), true);
            editor.apply();
//            Log.v(TAG, "onCreate - pref_key_use_device_location changed to true");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.v(TAG, "onActivityResult - start - requestCode/resultCode: " + requestCode + "/" + resultCode);
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
//        Log.v(TAG, "onActivityResult - end");
    }

    private void processPlacePickerData(Intent data) {
//        Place place = PlacePicker.getPlace(data, this);
        Place place = PlacePicker.getPlace(this, data);
        mFixedLocationAddress = place.getAddress().toString();
        mLatLng = place.getLatLng();

        // If the provided place doesn't have an address, we'll form a display-friendly
        // string from the latlng values.
        if (TextUtils.isEmpty(mFixedLocationAddress)) {
            mFixedLocationAddress = String.format("(%.2f, %.2f)", mLatLng.latitude, mLatLng.longitude);
        }

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
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
//                currFixedLocationLatitude = (float) latLong.latitude;

        if (mSettingsFragment != null) {
            Preference locationPreference = mSettingsFragment.findPreference(getString(R.string.pref_key_fixed_location));
            if (locationPreference != null) {
                mSettingsFragment.setPreferenceSummary(locationPreference, mFixedLocationAddress);
                mFixedLocationPreferenceSummaryNotUpdated = false;
            }
        }

        // Add attributions for our new PlacePicker location.
        if (mAttribution != null) {
            mAttribution.setVisibility(View.VISIBLE);
        } else {
            // For pre-Honeycomb devices, we cannot add a footer, so we will use a snackbar
            View rootView = findViewById(android.R.id.content);
            Snackbar.make(rootView, getString(R.string.attribution_text),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private void processWidgetStopDetails(Intent data) {
        mStopId = data.getStringExtra(WIDGET_STOP_ID);
        mStopName = data.getStringExtra(WIDGET_LOCATION_NAME);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.pref_key_widget_stop_id), mStopId);
        editor.putString(getString(R.string.pref_key_widget_stop_name), mStopName);
        editor.apply();
        mWidgetStopPreferenceSummaryNotUpdated = true;
//        Log.v(TAG, "onActivityResult - after commit - mSettingsFragment: " + mSettingsFragment);
        if (mSettingsFragment != null) {
            Preference widgetStopPreference = mSettingsFragment.findPreference(getString(R.string.pref_key_widget_stop_name));
            if (widgetStopPreference != null) {
                mSettingsFragment.setPreferenceSummary(widgetStopPreference, mStopName);
                mWidgetStopPreferenceSummaryNotUpdated = false;
            }
        }
//        Log.v(TAG, "onActivityResult - before sendBroadcastMessageToNextDeparturesWidget: ");
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
//        Log.v(TAG, "onSaveInstanceState - after super");
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
    // FIXME: 7/11/2016 test it
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(TAG, "onRestoreInstanceState - start");
        mFixedLocationPreferenceSummaryNotUpdated = savedInstanceState.getBoolean(FIXED_LOCATION_PREFERENCE_SUMMARY_NOT_UPDATED);
        mFixedLocationAddress = savedInstanceState.getString(FIXED_ADDRESS);
        if (mFixedLocationPreferenceSummaryNotUpdated && mSettingsFragment != null) {
            Preference fixedLocationPreference = mSettingsFragment.findPreference(getString(R.string.pref_key_fixed_location));
            if (fixedLocationPreference != null) {
                Log.v(TAG, "onRestoreInstanceState - calling setPreferenceSummary");
                mSettingsFragment.setPreferenceSummary(fixedLocationPreference, mFixedLocationAddress);
                mFixedLocationPreferenceSummaryNotUpdated = false;
            }
        }
        mWidgetStopPreferenceSummaryNotUpdated = savedInstanceState.getBoolean(WIDGET_STOP_PREFERENCE_SUMMARY_NOT_UPDATED);
        mStopId = savedInstanceState.getString(WIDGET_STOP_ID);
        mStopName = savedInstanceState.getString(WIDGET_STOP_NAME);
        Log.v(TAG, "onRestoreInstanceState - mStopName: " + mStopName + "/" + mSettingsFragment);
        if (mWidgetStopPreferenceSummaryNotUpdated && mSettingsFragment != null && mStopName != null) {
            Preference widgetStopPreference = mSettingsFragment.findPreference(getString(R.string.pref_key_widget_stop_name));
            if (widgetStopPreference != null) {
                Log.v(TAG, "onRestoreInstanceState - calling setPreferenceSummary");
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
//            SharedPreferences.OnSharedPreferenceChangeListener {

//        private boolean currUseDeviceLocationOn;
        private float currFixedLocationLatitude;
        private float currFixedLocationLongitude;

        private final String TAG = ((Object) this).getClass().getSimpleName();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            Log.v(TAG, "onCreate - start: ");
            addPreferencesFromResource(R.xml.pref_general);
            
            // Get a reference to the application default shared preferences.
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

            currFixedLocationLatitude = sp.getFloat(getString(R.string.pref_key_location_latitude), Float.NaN);
            currFixedLocationLongitude = sp.getFloat(getString(R.string.pref_key_location_longitude), Float.NaN);

            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_fixed_location)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_widget_stop_name)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_use_device_location)));

//            Log.v(TAG, "onCreate - end: ");
            // FIXME: 25/10/2016 - research how to setListFooter(mAttribution); for PlacePicker
//            setListFooter(mAttribution);
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
            setPreferenceSummary(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
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
//            Log.v(TAG, "onPreferenceChange - key: " + preference.getKey());
            if ((key == getString(R.string.pref_key_use_device_location))) {
//            Log.v(TAG, "onPreferenceChange - currUseDeviceLocationOn: " + currUseDeviceLocationOn);
                boolean newUseDeviceSwitchValue = (boolean) value;
                boolean validationOk = validateUseDeviceFixedLocationValue(newUseDeviceSwitchValue);
                if (validationOk) {
//                    currUseDeviceLocationOn = (boolean) value;
                    if (newUseDeviceSwitchValue) {
                        setPreferenceSummary(preference, getString(R.string.pref_summary_use_device_location));
                    } else {
                        setPreferenceSummary(preference, getString(R.string.pref_summary_use_fixed_location));
                    }
//                Log.v(TAG, "onPreferenceChange - validation successful - newUseDeviceSwitchValue: " + newUseDeviceSwitchValue);
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
                    (Float.isNaN(currFixedLocationLatitude) ||Float.isNaN(currFixedLocationLongitude) )) {
                Snackbar.make(getView(), getString(R.string.pref_err_use_device_location),
                        Snackbar.LENGTH_LONG).show();
//                Log.v(TAG, "validateUseDeviceFixedLocationValue - fix location not assigned: ");
                return false;
            }
            return true;
        }

        private void setPreferenceSummary(Preference preference, Object value) {
            String stringValue = value.toString();
            String key = preference.getKey();
//            Log.v(TAG, "setPreferenceSummary - key/stringValue: " + key + "/" + stringValue);

            if (preference instanceof SwitchPreference) {
                if ( key.equals(getString(R.string.pref_key_use_device_location))) {
                    boolean useDeviceLocationValue = Boolean.parseBoolean(stringValue);
//            Log.v(TAG, "onCreate - useDeviceLocationValue: " + useDeviceLocationValue + "/" + currFixedLocationLatitude + "/" + currFixedLocationLongitude);
                    if (useDeviceLocationValue) {
                        // FIXME: 26/10/2016 - shoud set that in parent activity or what?
//                        currUseDeviceLocationOn = true;
                        preference.setSummary(getString(R.string.pref_summary_use_device_location));
                    } else {
                        preference.setSummary(getString(R.string.pref_summary_use_fixed_location));
                    }
                }
            } else {
//            Log.v(TAG, "setPreferenceSummary - on other");
                // For other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }
        }

//        @Override
//        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
////        Log.v(TAG, "onSharedPreferenceChanged - key: " + key);
//            Preference pref = findPreference(key);
//        }

        // Registers a shared preference change listener that gets notified when preferences change
//        @Override
//        public void onResume() {
////            Log.v(TAG, "onResume - start: ");
//            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
//            sp.registerOnSharedPreferenceChangeListener(this);
//            super.onResume();
//        }

        // Unregisters a shared preference change listener
//        @Override
//        public void onPause() {
////            Log.v(TAG, "onPause - start: ");
//            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
//            sp.unregisterOnSharedPreferenceChangeListener(this);
//            super.onPause();
//        }
    }

}