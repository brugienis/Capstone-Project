package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by business on 30/08/2016.
 */
public class MptProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MptDbHelper mOpenHelper;

    static final int STOPS_DETAILS = 100;
    static final int ALL_FAVORITE_STOPS = 102;
    static final int STOP_LAT_LON = 101;
//    static final int WEATHER_WITH_LOCATION_AND_DATE = 103;

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    /*
        Creating the the UriMatcher. This UriMatcher will match each URI to the STOPS_DETAILS
        integer constants defined above.
    */
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MptContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MptContract.PATH_STOPS_DETAILS, STOPS_DETAILS);
//        matcher.addURI(authority, MptContract.PATH_STOPS_DETAILS + "/*", ALL_FAVORITE_STOPS);
//        matcher.addURI(authority, MptContract.PATH_STOPS_DETAILS + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MptDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
//            case STOPS_DETAILS:
//                return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
            case STOPS_DETAILS:
                return MptContract.StopDetailsEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
