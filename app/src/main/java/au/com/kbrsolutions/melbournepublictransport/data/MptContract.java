package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.ContentResolver;
import android.content.ContentUris;
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
    public static final String PATH_LINE_DETAIL = "line_detail";
    public static final String PATH_STOP_DETAIL = "stop_detail";

    /* Inner class that defines the table contents of the line_detail table */
    public static final class LineDetailEntry implements BaseColumns {

        // CONTENT_URI points to a table line_detail
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LINE_DETAIL).build();

        // CONTENT_TYPE - indicates that the content is a collection of URIs. Used in Provider.getType(...).
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LINE_DETAIL;

        // CONTENT_TYPE - indicates that the content is an instance of URI. Used in Provider.getType(...).
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LINE_DETAIL;

        // Table name
        public static final String TABLE_NAME = "line_detail";

        // column names
        // route_type - Integer. 0 Train, etc.
        public static final String COLUMN_ROUTE_TYPE = "route_type";

        // stop_id - numeric string
        public static final String COLUMN_STOP_ID = "stop_id";

        // line_id - numeric string
        public static final String COLUMN_LINE_ID = "line_id";

        // line_name - string
        public static final String COLUMN_LINE_NAME = "line_name";

        // line_name - string
        public static final String COLUMN_LINE_NAME_SHORT = "line_name_short";

        public static Uri buildUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the stop_detail table */
    public static final class StopDetailEntry implements BaseColumns {

        // CONTENT_URI points to a table stop_detail
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STOP_DETAIL).build();

        // CONTENT_TYPE - indicates that the content is a collection of URIs. Used in Provider.getType(...).
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOP_DETAIL;

        // CONTENT_TYPE - indicates that the content is an instance of URI. Used in Provider.getType(...).
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOP_DETAIL;

        public static final String ANY_FAVORITE_FLAG = "a";
        public static final String NON_FAVORITE_FLAG = "n";
        public static final String FAVORITE_FLAG = "y";

        // Table name
        public static final String TABLE_NAME = "stop_detail";

        // column names
        public static final String COLUMN_LINE_KEY = "line_key";

        public static final String COLUMN_ROUTE_TYPE = "route_type";

        public static final String COLUMN_STOP_ID = "stop_id";

        public static final String COLUMN_LOCATION_NAME = "location_name";

        public static final String COLUMN_SUBURB = "column_suburb";

        public static final String COLUMN_LATITUDE = "latitude";

        public static final String COLUMN_LONGITUDE = "longitude";

        public static final String COLUMN_FAVORITE = "favorite";

        public static Uri buildFavoriteStopsUri(String favoriteFlag) {
            if (favoriteFlag != ANY_FAVORITE_FLAG && favoriteFlag != NON_FAVORITE_FLAG && favoriteFlag != FAVORITE_FLAG) {
                throw new RuntimeException("MptContract.StopDetailsEntry - incorrect value of favoriteFlag: " + favoriteFlag);
            }
            return CONTENT_URI.buildUpon().appendQueryParameter
                    (COLUMN_FAVORITE, favoriteFlag).build();
        }

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getFavoriteFlagFromUri(Uri uri) {
            String favoriteFlag = uri.getQueryParameter(COLUMN_FAVORITE);
            return favoriteFlag;
        }
    }

}

