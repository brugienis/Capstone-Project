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

    public static float getLocationLatitude(Context context) {
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getFloat(context.getString(R.string.pref_key_location_latitude),
                DEFAULT_LATLONG);
    }

    public static float getLocationLongitude(Context context) {
        SharedPreferences prefs
                = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getFloat(context.getString(R.string.pref_key_location_longitude),
                DEFAULT_LATLONG);
    }

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_fixed_location),
                context.getString(R.string.pref_fixed_location_default));
    }
}
