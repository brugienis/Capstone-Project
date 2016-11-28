package au.com.kbrsolutions.melbournepublictransport.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;

/**
 * Created by business on 19/10/2016.
 */

public class Utility {
//    public static float DEFAULT_LATLONG = 0F;

    private static final String TAG = Utility.class.getSimpleName();

    public static boolean isLocationLatLonAvailable(Context context) {
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.contains(context.getString(R.string.pref_key_location_latitude))
                && prefs.contains(context.getString(R.string.pref_key_location_longitude));
    }

    /**
     *
     * Set default values.
     *
     * @param context
     * @return
     */
    public static void initSettings(Context context) {
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);

        if (Double.isNaN(prefs.getFloat(context.getString(R.string.pref_key_location_latitude),
                Float.NaN)) ||
                Double.isNaN(prefs.getFloat(context.getString(R.string.pref_key_location_latitude),
                        Float.NaN))) {
            TypedValue typedValue = new TypedValue();
            context.getResources().getValue(R.dimen.default_fixed_location_latitude, typedValue, true);
            float latitude = typedValue.getFloat();
            context.getResources().getValue(R.dimen.default_fixed_location_longitude, typedValue, true);
            float longitude = typedValue.getFloat();
            SharedPreferences.Editor editor = prefs.edit();
            /* Store default address - Flinders Street, Melbourne.                            */
            editor.putString(context.getString(R.string.pref_key_fixed_location),
                    context.getString(R.string.pref_default_fixed_location_address));
            /* Also store the latitude and longitude so that we can use these to get a precise */
            /* result from our stops nearby search.                                            */
            editor.putFloat(context.getString(R.string.pref_key_location_latitude),
                    (float) latitude);
            editor.putFloat(context.getString(R.string.pref_key_location_longitude),
                    (float) longitude);

            /* Store default stopId and locationName : 1071, Flinders Street Station           */
            editor.putString(context.getString(R.string.pref_key_widget_stop_id),
                    context.getString(R.string.pref_default_widget_stop_id));
            editor.putString(context.getString(R.string.pref_key_widget_stop_name),
                    context.getString(R.string.pref_default_widget_stop_name));

            editor.apply();
//        processPlacePickerData - : -37.817682500000004/144.96726171874994
        }
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
        Log.v(TAG, "setDatabaseLoadedFlg - status set to : " + value);
    }

    /**
     *
     * If 'fixed' location is not assigned, return Flinders station latitude and longitude.
     *
     * @param context
     * @return
     */
    public static LatLngDetails getLatLng(Context context) {
        LatLngDetails latLngDetails = null;
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
        boolean useDeviceLocationOn =
                prefs.getBoolean(context.getString(R.string.pref_key_use_device_location), true);
        if (!useDeviceLocationOn) {
            latLngDetails = new LatLngDetails(
                    prefs.getFloat(context.getString(R.string.pref_key_location_latitude),
                            Float.NaN),
                    prefs.getFloat(context.getString(R.string.pref_key_location_longitude),
                            Float.NaN)
            );
        }
        return latLngDetails;
//        processPlacePickerData - : -37.817682500000004/144.96726171874994
    }

    public static String getWidgetStopName(Context context) {
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
//        String stopName = prefs.getString(context.getString(R.string.pref_key_widget_stop_name), context.getString(R.string.pref_default_widget_stop_name));
        String stopName = prefs.getString(context.getString(R.string.pref_key_widget_stop_name), "");
        if (stopName.length() == 0) {
            initSettings(context);
            stopName = prefs.getString(context.getString(R.string.pref_key_widget_stop_name), "");
        }
        return stopName;
    }

    public static String getWidgetStopId(Context context) {
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
        String stopId = prefs.getString(context.getString(R.string.pref_key_widget_stop_id),
                context.getString(R.string.pref_default_widget_stop_id));
        return stopId;
    }

    public static String getWidgetStopInstructions(Context context) {
        return context.getString(R.string.pref_value_widget_stop_set_up_instructions);
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

    public static String getClassHashCode(Object o) {
        return String.format("0x%08X", o.hashCode());
    }

    // FIXME: 29/10/2016 - add methods to get widget stop details and get/set 'database loaded'
//    public static float getLocationLatitude(Context context) {
//        SharedPreferences prefs
//                = PreferenceManager.getDefaultSharedPreferences(context);
//        return prefs.getFloat(context.getString(R.string.pref_key_location_latitude),
//                DEFAULT_LATLONG);
//    }
//
//    public static float getLocationLongitude(Context context) {
//        SharedPreferences prefs
//                = PreferenceManager.getDefaultSharedPreferences(context);
//        return prefs.getFloat(context.getString(R.string.pref_key_location_longitude),
//                DEFAULT_LATLONG);
//    }

//    public static String getPreferredLocation(Context context) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        return prefs.getString(context.getString(R.string.pref_key_fixed_location),
//                context.getString(R.string.pref_default_fixed_location));
//    }
}
