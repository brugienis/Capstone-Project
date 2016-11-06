package au.com.kbrsolutions.melbournepublictransport.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;

/**
 * Created by business on 19/10/2016.
 */

public class Utility {
    public static float DEFAULT_LATLONG = 0F;

    public static boolean isLocationLatLonAvailable(Context context) {
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.contains(context.getString(R.string.pref_key_location_latitude))
                && prefs.contains(context.getString(R.string.pref_key_location_longitude));
    }

    public static LatLngDetails getLatLng(Context context) {
        LatLngDetails latLngDetails = null;
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
        boolean useDeviceLocationOn =
                prefs.getBoolean(context.getString(R.string.pref_key_use_device_location), true);
        if (!useDeviceLocationOn) {
            latLngDetails = new LatLngDetails(
                    prefs.getFloat(context.getString(R.string.pref_key_location_latitude), Float.NaN),
                    prefs.getFloat(context.getString(R.string.pref_key_location_longitude), Float.NaN)

            );
        }
        return latLngDetails;
    }

    public static String getWidgetStopName(Context context) {
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
        String stopName = prefs.getString(context.getString(R.string.pref_key_widget_stop_name),
                context.getString(R.string.pref_default_widget_stop_name));
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
