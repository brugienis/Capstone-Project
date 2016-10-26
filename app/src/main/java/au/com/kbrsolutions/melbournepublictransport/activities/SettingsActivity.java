package au.com.kbrsolutions.melbournepublictransport.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.utilities.Utility;

import static au.com.kbrsolutions.melbournepublictransport.activities.WidgetStopsActivity.WIDGET_LOCATION_NAME;
import static au.com.kbrsolutions.melbournepublictransport.activities.WidgetStopsActivity.WIDGET_STOP_ID;

public class SettingsActivity extends AppCompatActivity
        implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private ImageView mAttribution;
    private SettingsFragment mSettingsFragment;
//    private PreferencesFragment mPreferencesFragment;

    private boolean currUseDeviceLocationOn;
    private float currFixedLocationLatitude;
    private float currFixedLocationLongitude;

    protected static final int PLACE_PICKER_REQUEST = 1000;
    protected static final int WIDGET_STOP_REQUEST = 2000;
    public static final String WIDGET_STOP_UPDATED =
            "au.com.kbrsolutions.melbournepublictransport.WIDGET_STOP_UPDATED";

    private final String TAG = ((Object) this).getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate - start: ");

        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
            mSettingsFragment = new SettingsFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mSettingsFragment)
                    .commit();
//            mPreferencesFragment = new PreferencesFragment();
//            getFragmentManager()
//                    .beginTransaction()
//                    .add(android.R.id.content, mPreferencesFragment)
//                    .commit();

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
        Log.v(TAG, "onCreate - end: ");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "onSaveInstanceState - after super");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.v(TAG, "onRestoreInstanceState - start");
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     *
     * Show toolbar with Up button.
     *
     * From David Passmore
     *
     * http://stackoverflow.com/questions/17849193/how-to-add-action-bar-from-support-library-into-preferenceactivity?rq=1
     *
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    void bindPreferencesSummaryToValue() {
//        bindPreferenceSummaryToValueAndSetListener(mSettingsFragment.findPreference(getString(R.string.pref_key_location)));
//        bindPreferenceSummaryToValueAndSetListener(mSettingsFragment.findPreference(getString(R.string.pref_key_use_device_location)));
//        bindPreferenceSummaryToValueAndSetListener(mSettingsFragment.findPreference(getString(R.string.pref_key_widget_stop_name)));
//
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//        currFixedLocationLatitude = sp.getFloat(getString(R.string.pref_key_location_latitude), Float.NaN);
//        currFixedLocationLongitude = sp.getFloat(getString(R.string.pref_key_location_longitude), Float.NaN);
//    }

    /**
     * Attaches a listener, so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
//    private void bindPreferenceSummaryToValueAndSetListener(Preference preference) {
//        String key = preference.getKey();
////        Log.v(TAG, "bindPreferenceSummaryToValueAndSetListener - key: " + key);
//        // Set the listener to watch for value changes.
//        preference.setOnPreferenceChangeListener(this);
//
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//        if ((key == getString(R.string.pref_key_use_device_location))) {
//            currUseDeviceLocationOn = sp.getBoolean(key, false);
//        } else if ((key == getString(R.string.pref_key_widget_stop_name))) {
////            Log.v(TAG, "bindPreferenceSummaryToValueAndSetListener - value: " + sp.getString(getString(R.string.pref_key_widget_stop_name), ""));
//            setPreferenceSummary(preference, sp.getString(getString(R.string.pref_key_widget_stop_name), ""));
//        } else {
//            setPreferenceSummary(preference, sp.getString(getString(R.string.pref_key_location), ""));
//        }
//    }

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
        Log.v(TAG, "onPreferenceChange - key: " + preference.getKey());
        if ((key == getString(R.string.pref_key_use_device_location))) {
//            Log.v(TAG, "onPreferenceChange - currUseDeviceLocationOn: " + currUseDeviceLocationOn);
            boolean validationOk = validateUseDeviceFixedLocationValue((boolean) value);
            if (validationOk) {
                currUseDeviceLocationOn = (boolean) value;
//                Log.v(TAG, "onPreferenceChange - validation successful - currUseDeviceLocationOn: " + currUseDeviceLocationOn);
            }
            return validationOk;
        } else if ((key == getString(R.string.pref_key_location))) {
            setPreferenceSummary(preference, value);
        } else if ((key == getString(R.string.pref_key_widget_stop_name))) {
            setPreferenceSummary(preference, value);
        }
        return true;
    }

    private boolean validateUseDeviceFixedLocationValue(boolean newUseDeviceLocationOn) {
        Log.v(TAG, "validateUseDeviceFixedLocationValue - currUseDeviceLocationOn/newUseDeviceLocationOn: " +
                currUseDeviceLocationOn + "/" +
                newUseDeviceLocationOn + "/" +
                currFixedLocationLatitude + "/" +
                currFixedLocationLongitude);

//        currFixedLocationLatitude = Float.NaN;

        if (currUseDeviceLocationOn && !newUseDeviceLocationOn &&
                (Float.isNaN(currFixedLocationLatitude) ||Float.isNaN(currFixedLocationLongitude) )) {
            View rootView = findViewById(android.R.id.content);
            Snackbar.make(rootView, getString(R.string.pref_err_use_device_location),
                    Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();
        String key = preference.getKey();
        Log.v(TAG, "setPreferenceSummary - key/stringValue: " + key + "/" + stringValue);

        if (preference instanceof ListPreference) {
//            Log.v(TAG, "setPreferenceSummary - on ListPreference");
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if (key.equals(getString(R.string.pref_key_location))) {
//            Log.v(TAG, "setPreferenceSummary - on location");
            preference.setSummary(stringValue);
//            }
        } else if (key.equals(getString(R.string.pref_key_widget_stop_name))) {
//            Log.v(TAG, "setPreferenceSummary - on location");
            preference.setSummary(stringValue);
//            }
        } else {
//            Log.v(TAG, "setPreferenceSummary - on other");
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
    }

    // This gets called after the preference is changed.
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.v(TAG, "onSharedPreferenceChanged - key: " + key);
        if ( key.equals(getString(R.string.pref_key_location)) ) {
//            Log.v(TAG, "onSharedPreferenceChanged - in 'location'");
            // we've changed the location
            // Wipe out any potential PlacePicker latlng values so that we can use this text entry.
            // FIXME: 25/10/2016 do I need the code below?
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(getString(R.string.pref_key_location_latitude));
            editor.remove(getString(R.string.pref_key_location_longitude));
            editor.commit();

            // Remove attributions for our any PlacePicker locations.
            if (mAttribution != null) {
                mAttribution.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.v(TAG, "onActivityResult - start - requestCode/resultCode: " + requestCode + "/" + resultCode);
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
                editor.putString(getString(R.string.pref_key_location), address);

                // Also store the latitude and longitude so that we can use these to get a precise
                // result from our stops nearby search.
                editor.putFloat(getString(R.string.pref_key_location_latitude),
                        (float) latLong.latitude);
                editor.putFloat(getString(R.string.pref_key_location_longitude),
                        (float) latLong.longitude);
                editor.commit();
                currFixedLocationLatitude = (float) latLong.latitude;

//                Preference locationPreference = mSettingsFragment.getPreference(getString(R.string.pref_key_location));
                Preference locationPreference = mSettingsFragment.findPreference(getString(R.string.pref_key_location));
                //findPreference(key);
//                Log.v(TAG, "onActivityResult - locationPreference: " + locationPreference);
                if (locationPreference != null) {
                    setPreferenceSummary(locationPreference, address);
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
                // FIXME: 25/10/2016 - handle the resulot not OK
            }
        } else if (requestCode == WIDGET_STOP_REQUEST) {
//            Log.v(TAG, "onActivityResult - processing WIDGET_STOP_REQUEST");
            if (resultCode == RESULT_OK) {
                // Make sure the request was successful
                String stopId = data.getStringExtra(WIDGET_STOP_ID);
                String locationName = data.getStringExtra(WIDGET_LOCATION_NAME);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.pref_key_widget_stop_id), stopId);
                editor.putString(getString(R.string.pref_key_widget_stop_name), locationName);
                editor.commit();
//                Log.v(TAG, "onActivityResult - stopId: " + stopId + "/" + locationName);
                Preference widgetStopPreference = mSettingsFragment.findPreference(getString(R.string.pref_key_widget_stop_name));
//                Log.v(TAG, "onActivityResult - widgetStopPreference: " + widgetStopPreference);
                if (widgetStopPreference != null) {
                    setPreferenceSummary(widgetStopPreference, locationName);
                }
                sendBroadcastMessageToNextDeparturesWidget();
            } else {
                // FIXME: 25/10/2016 - handle the resulot not OK
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        Log.v(TAG, "onActivityResult - end");
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

    // Registers a shared preference change listener that gets notified when preferences change
    @Override
    protected void onResume() {
        Log.v(TAG, "onResume - start: ");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    // Unregisters a shared preference change listener
    @Override
    protected void onPause() {
        Log.v(TAG, "onPause - start: ");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    public static class SettingsFragment
            extends PreferenceFragment
            implements  Preference.OnPreferenceChangeListener,
            SharedPreferences.OnSharedPreferenceChangeListener {

        private boolean currUseDeviceLocationOn;
        private float currFixedLocationLatitude;
        private float currFixedLocationLongitude;

        private final String TAG = ((Object) this).getClass().getSimpleName();

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

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            Log.v(TAG, "onCreate - start: ");
            addPreferencesFromResource(R.xml.pref_general);
            
            // Get a reference to the application default shared preferences.
            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

            String fixedLocationValue = sp.getString(getString(R.string.pref_key_location), "-1");
            Preference prefFixedLocation = findPreference(getString(R.string.pref_key_location));
            prefFixedLocation.setSummary(fixedLocationValue);

            String stopNameValue = sp.getString(getString(R.string.pref_key_widget_stop_name), "-1");
            Preference prefStopName = findPreference(getString(R.string.pref_key_widget_stop_name));
            prefStopName.setSummary(stopNameValue);

            Preference prefUseDeviceLocation = findPreference(getString(R.string.pref_key_use_device_location));
            // Set the listener to watch for value changes.
            prefUseDeviceLocation.setOnPreferenceChangeListener(this);
            boolean useDeviceLocationValue = sp.getBoolean(getString(R.string.pref_key_use_device_location), true);
            if (useDeviceLocationValue) {
                // FIXME: 26/10/2016 - shoud set that in parent activity or what?
                currUseDeviceLocationOn = true;
                prefUseDeviceLocation.setSummary(getString(R.string.pref_summary_use_device_location));
            } else {
                prefUseDeviceLocation.setSummary(getString(R.string.pref_summary_use_fixed_location));
            }

//            Log.v(TAG, "onCreate - end: ");
            // FIXME: 25/10/2016 - make sure the parent activity implements this fragment interface method
            // to test change the device orientation
//            mListener.bindPreferencesSummaryToValue();
//            setListFooter(mAttribution);
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
            Log.v(TAG, "onPreferenceChange - key: " + preference.getKey());
            if ((key == getString(R.string.pref_key_use_device_location))) {
//            Log.v(TAG, "onPreferenceChange - currUseDeviceLocationOn: " + currUseDeviceLocationOn);
                boolean validationOk = validateUseDeviceFixedLocationValue((boolean) value);
                if (validationOk) {
                    currUseDeviceLocationOn = (boolean) value;
                Log.v(TAG, "onPreferenceChange - validation successful - currUseDeviceLocationOn: " + currUseDeviceLocationOn);
                }
                return validationOk;
            } else if ((key == getString(R.string.pref_key_location))) {
                setPreferenceSummary(preference, value);
            } else if ((key == getString(R.string.pref_key_widget_stop_name))) {
                setPreferenceSummary(preference, value);
            }
            return true;
        }

        private boolean validateUseDeviceFixedLocationValue(boolean newUseDeviceLocationOn) {
//        Log.v(TAG, "validateUseDeviceFixedLocationValue - currUseDeviceLocationOn/newUseDeviceLocationOn: " +
//                currUseDeviceLocationOn + "/" +
//                newUseDeviceLocationOn + "/" +
//                currFixedLocationLatitude + "/" +
//                currFixedLocationLongitude);

//            currFixedLocationLatitude = Float.NaN;

            if (currUseDeviceLocationOn && !newUseDeviceLocationOn &&
                    (Float.isNaN(currFixedLocationLatitude) ||Float.isNaN(currFixedLocationLongitude) )) {
//                View rootView = findViewById(android.R.id.content);
                Snackbar.make(getView(), getString(R.string.pref_err_use_device_location),
                        Snackbar.LENGTH_LONG).show();
                Log.v(TAG, "validateUseDeviceFixedLocationValue - fix location not assigned: ");
                return false;
            }
            return true;
        }

        private void setPreferenceSummary(Preference preference, Object value) {
            String stringValue = value.toString();
            String key = preference.getKey();
        Log.v(TAG, "setPreferenceSummary - key/stringValue: " + key + "/" + stringValue);

            if (preference instanceof ListPreference) {
//            Log.v(TAG, "setPreferenceSummary - on ListPreference");
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list (since they have separate labels/values).
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            } else if (key.equals(getString(R.string.pref_key_location))) {
//            Log.v(TAG, "setPreferenceSummary - on location");
                preference.setSummary(stringValue);
//            }
            } else if (key.equals(getString(R.string.pref_key_widget_stop_name))) {
//            Log.v(TAG, "setPreferenceSummary - on location");
                preference.setSummary(stringValue);
//            }
            } else {
//            Log.v(TAG, "setPreferenceSummary - on other");
                // For other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }
        }
        
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.v(TAG, "onSharedPreferenceChanged - key: " + key);
            Preference pref = findPreference(key);
            if (key.equals(getString(R.string.pref_key_use_device_location)) ) {
//                boolean useDeviceLocationValue = sp.getBoolean(getString(R.string.pref_key_use_device_location), true);
                if (currUseDeviceLocationOn) {
                    // FIXME: 26/10/2016 - shoud set that in parent activity or what?
//                currUseDeviceLocationOn = sp.getBoolean(key, false);
                    pref.setSummary(getString(R.string.pref_summary_use_device_location));
                } else {
                    pref.setSummary(getString(R.string.pref_summary_use_fixed_location));
                }
            }
        }

//        Preference getPreference(String key) {
////            Log.v(TAG, "findPreference - key: " + key);
//            return findPreference(key);
//        }

        @Override
        public Preference findPreference(CharSequence key) {
            return super.findPreference(key);
        }

    }

}