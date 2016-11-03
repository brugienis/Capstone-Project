package au.com.kbrsolutions.melbournepublictransport.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
//        implements Preference.OnPreferenceChangeListener,
//        SharedPreferences.OnSharedPreferenceChangeListener {

    private ImageView mAttribution;
    private SettingsFragment mSettingsFragment;
    ActionBar actionBar;
    private Toolbar mToolbar;
    CollapsingToolbarLayout mCollapsingToolbar;
    private ImageView collapsingToolbarImage;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    protected static final int PLACE_PICKER_REQUEST = 1000;
    protected static final int WIDGET_STOP_REQUEST = 2000;
    public static final String WIDGET_STOP_UPDATED =
            "au.com.kbrsolutions.melbournepublictransport.WIDGET_STOP_UPDATED";
    private static final String SETTINGS_TAG = "favorite_stops_tag";

    private final String TAG = ((Object) this).getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.v(TAG, "onCreate - start: ");
        setContentView(R.layout.activity_main);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(false);

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
//        FloatingActionButton fab = (FloatingActionButton) findViewById(fab);
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

    private void handleRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     *
     * I 'use fixed location' enabled and its latitude and longitude do not contain valid values,
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
        Log.v(TAG, "onActivityResult - start - requestCode/resultCode: " + requestCode + "/" + resultCode);
        // Check to see if the result is from our Place Picker intent
        if (requestCode == PLACE_PICKER_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String address = place.getAddress().toString();
                LatLng latLong = place.getLatLng();

                // If the provided place doesn't have an address, we'll form a display-friendly
                // string from the latlng values.
                if (TextUtils.isEmpty(address)) {
                    address = String.format("(%.2f, %.2f)", latLong.latitude, latLong.longitude);
                }

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.pref_key_fixed_location), address);

                // Also store the latitude and longitude so that we can use these to get a precise
                // result from our stops nearby search.
                editor.putFloat(getString(R.string.pref_key_location_latitude),
                        (float) latLong.latitude);
                editor.putFloat(getString(R.string.pref_key_location_longitude),
                        (float) latLong.longitude);
                editor.apply();
                // FIXME: 28/10/2016 - address and latLon should be handled as widget's stopId and name
                fixedLocationPreferenceSummaryNoUpdated = true;
//                currFixedLocationLatitude = (float) latLong.latitude;

                if (mSettingsFragment != null) {
                    Preference locationPreference = mSettingsFragment.findPreference(getString(R.string.pref_key_fixed_location));
                    if (locationPreference != null) {
                        mSettingsFragment.setPreferenceSummary(locationPreference, address);
                        fixedLocationPreferenceSummaryNoUpdated = false;
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
            } else {
                // FIXME: 25/10/2016 - handle the result not OK
            }
        } else if (requestCode == WIDGET_STOP_REQUEST) {
//            Log.v(TAG, "onActivityResult - processing WIDGET_STOP_REQUEST");
            if (resultCode == RESULT_OK) {
                // Make sure the request was successful
                mStopId = data.getStringExtra(WIDGET_STOP_ID);
                mStopName = data.getStringExtra(WIDGET_LOCATION_NAME);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.pref_key_widget_stop_id), mStopId);
                editor.putString(getString(R.string.pref_key_widget_stop_name), mStopName);
                editor.apply();
                widgetStopPreferenceSummaryNoUpdated = true;
                Log.v(TAG, "onActivityResult - after commit - mSettingsFragment: " + mSettingsFragment);
                if (mSettingsFragment != null) {
                    Preference widgetStopPreference = mSettingsFragment.findPreference(getString(R.string.pref_key_widget_stop_name));
                    if (widgetStopPreference != null) {
                        mSettingsFragment.setPreferenceSummary(widgetStopPreference, mStopName);
                        widgetStopPreferenceSummaryNoUpdated = false;
                    }
                }
                Log.v(TAG, "onActivityResult - before sendBroadcastMessageToNextDeparturesWidget: ");
                sendBroadcastMessageToNextDeparturesWidget();
            } else {
                // FIXME: 25/10/2016 - handle the result not OK
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        Log.v(TAG, "onActivityResult - end");
    }

    private String mStopId;
    private String mStopName;
    private boolean widgetStopPreferenceSummaryNoUpdated;
    private boolean fixedLocationPreferenceSummaryNoUpdated;

    private static final String WIDGET_STOP_ID = "widget_stop_id";
    private static final String WIDGET_STOP_NAME = "widget_stop_name";
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        Log.v(TAG, "onSaveInstanceState - after super");
        if (fixedLocationPreferenceSummaryNoUpdated) {
            outState.putString(WIDGET_STOP_ID, mStopId);
            outState.putString(WIDGET_STOP_NAME, mStopName);
        }
    }

    /**
     * Up button was pressed - remove to top entry Back Stack
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


    /**
     * Retrieves saved data. If search for artist's data is not in progress, show retrieved
     * on the screen.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(TAG, "onRestoreInstanceState - start");

        mStopId = savedInstanceState.getString(WIDGET_STOP_ID);
        mStopName = savedInstanceState.getString(WIDGET_STOP_NAME);
        Log.v(TAG, "onRestoreInstanceState - mStopName: " + mStopName + "/" + mSettingsFragment);
        if (mSettingsFragment != null && mStopName != null) {
            Preference widgetStopPreference = mSettingsFragment.findPreference(getString(R.string.pref_key_widget_stop_name));
            if (widgetStopPreference != null) {
                Log.v(TAG, "onRestoreInstanceState - calling setPreferenceSummary");
                mSettingsFragment.setPreferenceSummary(widgetStopPreference, mStopName);
                widgetStopPreferenceSummaryNoUpdated = false;
            }
        }
    }

    public static class SettingsFragment
            extends PreferenceFragment
            implements  Preference.OnPreferenceChangeListener,
            SharedPreferences.OnSharedPreferenceChangeListener {

        private boolean currUseDeviceLocationOn;
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

            String fixedLocationValue = sp.getString(getString(R.string.pref_key_fixed_location),
                    getString(R.string.pref_default_fixed_location));
            Preference prefFixedLocation = findPreference(getString(R.string.pref_key_fixed_location));
            // Set the listener to watch for value changes.
            prefFixedLocation.setOnPreferenceChangeListener(this);
            prefFixedLocation.setSummary(fixedLocationValue);

            String stopNameValue = sp.getString(getString(R.string.pref_key_widget_stop_name),
                    getString(R.string.pref_default_widget_stop_name));
            Preference prefStopName = findPreference(getString(R.string.pref_key_widget_stop_name));
            Log.v(TAG, "onCreate - prev summary: " + prefStopName.getSummary());

            prefStopName.setSummary(stopNameValue);

            Preference prefUseDeviceLocation = findPreference(getString(R.string.pref_key_use_device_location));
            // Set the listener to watch for value changes.
            prefUseDeviceLocation.setOnPreferenceChangeListener(this);
            boolean useDeviceLocationValue = sp.getBoolean(getString(R.string.pref_key_use_device_location), true);
//            Log.v(TAG, "onCreate - useDeviceLocationValue: " + useDeviceLocationValue + "/" + currFixedLocationLatitude + "/" + currFixedLocationLongitude);
            if (useDeviceLocationValue) {
                // FIXME: 26/10/2016 - shoud set that in parent activity or what?
                currUseDeviceLocationOn = true;
                prefUseDeviceLocation.setSummary(getString(R.string.pref_summary_use_device_location));
            } else {
                prefUseDeviceLocation.setSummary(getString(R.string.pref_summary_use_fixed_location));
            }


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
//            Log.v(TAG, "validateUseDeviceFixedLocationValue - currUseDeviceLocationOn/newUseDeviceLocationOn: " +
//                currUseDeviceLocationOn + "/" +
//                newUseDeviceLocationOn + "/" +
//                currFixedLocationLatitude + "/" +
//                currFixedLocationLongitude);

            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            currFixedLocationLatitude = sp.getFloat(getString(R.string.pref_key_location_latitude), Float.NaN);
            currFixedLocationLongitude = sp.getFloat(getString(R.string.pref_key_location_longitude), Float.NaN);
            currUseDeviceLocationOn = sp.getBoolean(getString(R.string.pref_key_use_device_location), true);

//            TOMORROW  - instead of using currUseDeviceLocationOn get appropriate value from SharedPreferences
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

            if (preference instanceof ListPreference) {
//            Log.v(TAG, "setPreferenceSummary - on ListPreference");
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list (since they have separate labels/values).
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else if (key.equals(getString(R.string.pref_key_fixed_location))) {
//            Log.v(TAG, "setPreferenceSummary - on location");
                preference.setSummary(stringValue);
//            }
            } else if (key.equals(getString(R.string.pref_key_widget_stop_name))) {
//            Log.v(TAG, "setPreferenceSummary - on location");
                Log.v(TAG, "onCreate - prev summary: " + preference.getSummary());
                preference.setSummary(stringValue);
//            }
            } else {
//            Log.v(TAG, "setPreferenceSummary - on other");
                // For other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }
        }

        // FIXME: 2/11/2016 - do I need onSharedPreferenceChanged
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        Log.v(TAG, "onSharedPreferenceChanged - key: " + key);
            Preference pref = findPreference(key);
//            if (key.equals(getString(R.string.pref_key_use_device_location)) ) {
////                boolean useDeviceLocationValue = sp.getBoolean(getString(R.string.pref_key_use_device_location), true);
//                if (currUseDeviceLocationOn) {
//                    // FIXME: 26/10/2016 - shoud set that in parent activity or what?
////                currUseDeviceLocationOn = sp.getBoolean(key, false);
//                    pref.setSummary(getString(R.string.pref_summary_use_device_location));
//                } else {
//                    pref.setSummary(getString(R.string.pref_summary_use_fixed_location));
//                }
//            }
        }

        // Registers a shared preference change listener that gets notified when preferences change
        @Override
        public void onResume() {
//            Log.v(TAG, "onResume - start: ");
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sp.registerOnSharedPreferenceChangeListener(this);
            super.onResume();
        }

        // Unregisters a shared preference change listener
        @Override
        public void onPause() {
//            Log.v(TAG, "onPause - start: ");
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sp.unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

//        @Override
//        public Preference findPreference(CharSequence key) {
//            return super.findPreference(key);
//        }

    }

}