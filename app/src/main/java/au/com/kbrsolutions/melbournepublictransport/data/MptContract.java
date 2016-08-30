package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by business on 24/08/2016.
 */
public class MptContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "au.com.kbrsolutions.melbournepublictransport";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_STOPS_DETAILS = "stops_details";

    /* Inner class that defines the table contents of the AvailableStopsEntry table */
    public static final class StopDetailsEntry implements BaseColumns {

        // CONTENT_URI points to a table available_stops
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STOPS_DETAILS).build();

        // CONTENT_TYPE - indicates that the content is a collection of URIs. Used in Provider.getType(...).
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOPS_DETAILS;

        // CONTENT_TYPE - indicates that the content is an instance of URI. Used in Provider.getType(...).
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOPS_DETAILS;

        // Table name
        public static final String TABLE_NAME = "available_stops";

        // column names
        public static final String LINE_NAME = "line_name";

        public static final String STOP_NAME = "stop_name";

        public static final String LATITUDE = "latitude";

        public static final String LONGITUDE = "longitude";

        public static final String FAVORITE = "favorite";

        // column names

        // FIXME: 26/08/2016 add columns names
    }

}

