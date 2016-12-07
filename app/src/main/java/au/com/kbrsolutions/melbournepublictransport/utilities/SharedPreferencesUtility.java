package au.com.kbrsolutions.melbournepublictransport.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;

/**
 *
 * This class allows to access SharedPreferences settings.
 *
 */
public class SharedPreferencesUtility {

    @SuppressWarnings("unused")
    private static final String TAG = SharedPreferencesUtility.class.getSimpleName();

    /**
     *
     * Set default values - will be applied the first time app starts.
     *
     * @param context
     * @return
     */
//    public static void initSettings(Context context) {
//        SharedPreferences prefs
//                = PreferenceManager.getDefaultSharedPreferences(context);
//
//        if (Double.isNaN(prefs.getFloat(context.getString(R.string.pref_key_location_latitude),
//                Float.NaN)) ||
//                Double.isNaN(prefs.getFloat(context.getString(R.string.pref_key_location_latitude),
//                        Float.NaN))) {
//            TypedValue typedValue = new TypedValue();
//            context.getResources().getValue(R.dimen.default_fixed_location_latitude, typedValue, true);
//            float latitude = typedValue.getFloat();
//            context.getResources().getValue(R.dimen.default_fixed_location_longitude, typedValue, true);
//            float longitude = typedValue.getFloat();
//            SharedPreferences.Editor editor = prefs.edit();
//            /* Store default address - Flinders Street, Melbourne.                            */
//            editor.putString(context.getString(R.string.pref_key_fixed_location),
//                    context.getString(R.string.pref_default_fixed_location_address));
//            /* Also store the latitude and longitude so that we can use these to get a precise */
//            /* result from our stops nearby search.                                            */
//            editor.putFloat(context.getString(R.string.pref_key_location_latitude),
//                    latitude);
//            editor.putFloat(context.getString(R.string.pref_key_location_longitude),
//                    longitude);
//
//            /* Store default stopId and locationName : 1071, Flinders Street Station           */
//            editor.putString(context.getString(R.string.pref_key_widget_stop_id),
//                    context.getString(R.string.pref_default_widget_stop_id));
//            editor.putString(context.getString(R.string.pref_key_widget_stop_name),
//                    context.getString(R.string.pref_default_widget_stop_name));
//
//            editor.apply();
//        }
//    }

    public static String getWidgetStopName(Context context) {
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
        String stopName = prefs.getString(context.getString(R.string.pref_key_widget_stop_name), "");
        if (stopName.length() == 0) {
            SharedPreferences.Editor editor = prefs.edit();
            /* Store default stopId and locationName : 1071, Flinders Street Station           */
            editor.putString(context.getString(R.string.pref_key_widget_stop_id),
                    context.getString(R.string.pref_default_widget_stop_id));
            stopName = context.getString(R.string.pref_default_widget_stop_name);
            editor.putString(context.getString(R.string.pref_key_widget_stop_name),
                    stopName);

            editor.apply();
            Log.v(TAG, "getWidgetStopName - default stop set: : + stopName");
        }
        return stopName;
    }

    /**
     *
     * Return 'fixed' location position. If it is not assigned, return Flinders station latitude and
     * longitude.
     *
     * @param context
     * @return
     */
    public static LatLngDetails getFixedLocationLatLng(Context context) {
        LatLngDetails latLngDetails = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        float latitude = prefs.getFloat(context.getString(R.string.pref_key_location_latitude), Float.NaN);
        float longitude = prefs.getFloat(context.getString(R.string.pref_key_location_longitude), Float.NaN);
        if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
            TypedValue typedValue = new TypedValue();
            context.getResources().getValue(R.dimen.default_fixed_location_latitude, typedValue, true);
            latitude = typedValue.getFloat();
            context.getResources().getValue(R.dimen.default_fixed_location_longitude, typedValue, true);
            longitude = typedValue.getFloat();
            SharedPreferences.Editor editor = prefs.edit();
            /* Store default address - Flinders Street, Melbourne.                            */
            editor.putString(context.getString(R.string.pref_key_fixed_location),
                    context.getString(R.string.pref_default_fixed_location_address));
            /* Also store the latitude and longitude so that we can use these to get a precise */
            /* result from our stops nearby search.                                            */
            editor.putFloat(context.getString(R.string.pref_key_location_latitude), latitude);
            editor.putFloat(context.getString(R.string.pref_key_location_longitude), longitude);
            editor.apply();
            Log.v(TAG, "getFixedLocationLatLng - default fixed location set: :");
        }
        latLngDetails = new LatLngDetails(latitude, longitude);
        return latLngDetails;
    }

    public static boolean isReleaseVersion(Context context) {
        return context.getResources().getBoolean(R.bool.release_version);
    }

    public static boolean isDatabaseLoaded(Context context) {
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.database_load_status), false);
    }

    public static void setDatabaseLoadedFlg(Context context, boolean value) {
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.database_load_status), value);
        editor.apply();
    }

    /**
     *
     * Returns value of the 'use device location' setting.
     *
     * @param context
     * @return
     */
    public static boolean useDeviceLocation(Context context) {
        LatLngDetails latLngDetails = null;
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_use_device_location), true);
    }

    public static String getWidgetStopId(Context context) {
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_widget_stop_id),
                context.getString(R.string.pref_default_widget_stop_id));
    }

    public static int getAppBarVerticalOffset(Context context) {
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
         return prefs.getInt(context.getString(R.string.pref_key_appbar_vertical_offset),
                Integer.parseInt(context.getString(R.string.pref_default_appbar_vertical_offset)));
    }

    public static void setAppBarVerticalOffset(Context context, int verticalOffset) {
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(String.valueOf(
                context.getString(R.string.pref_key_appbar_vertical_offset)),
                verticalOffset);
        editor.apply();
    }

}
