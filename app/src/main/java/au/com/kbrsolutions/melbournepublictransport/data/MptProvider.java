package au.com.kbrsolutions.melbournepublictransport.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import au.com.kbrsolutions.melbournepublictransport.data.MptContract.LineDetailEntry;
import au.com.kbrsolutions.melbournepublictransport.data.MptContract.StopDetailEntry;


public class MptProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MptDbHelper mOpenHelper;

    static final int ALL_STOP_DETAIL = 100;
    static final int ALL_LINE_DETAIL = 200;

    private static final SQLiteQueryBuilder sLineDetailQueryBuilder;

    static{
        sLineDetailQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sLineDetailQueryBuilder.setTables(LineDetailEntry.TABLE_NAME);

//        sStopDetailForFavoriteFlagQueryBuilder.setTables(
//                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
//                        WeatherContract.LocationEntry.TABLE_NAME +
//                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
//                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
//                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
//                        "." + WeatherContract.LocationEntry._ID);
    }

    private static final SQLiteQueryBuilder sStopDetailForFavoriteFlagQueryBuilder;

    static{
        sStopDetailForFavoriteFlagQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sStopDetailForFavoriteFlagQueryBuilder.setTables(StopDetailEntry.TABLE_NAME);

//        sStopDetailForFavoriteFlagQueryBuilder.setTables(
//                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
//                        WeatherContract.LocationEntry.TABLE_NAME +
//                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
//                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
//                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
//                        "." + WeatherContract.LocationEntry._ID);
    }

    // stop_detail.favorite = ?
    private static final String sStopDetailForOneFavoriteFlagSelection =
            StopDetailEntry.TABLE_NAME +
                    "." + StopDetailEntry.COLUMN_FAVORITE + " = ?";

    // stop_detail.favorite = ? OR stop_detail.favorite = ?
    private static final String sStopDetailForTwoFavoriteFlagSelection =
            StopDetailEntry.TABLE_NAME +
                    "." + StopDetailEntry.COLUMN_FAVORITE + " = ? OR " +
            StopDetailEntry.TABLE_NAME +
                    "." + StopDetailEntry.COLUMN_FAVORITE + " = ?";

    private final String TAG = ((Object) this).getClass().getSimpleName();

    @Override
    public boolean onCreate() {
        mOpenHelper = new MptDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ALL_STOP_DETAIL:
                return StopDetailEntry.CONTENT_TYPE;
//            case STOPS_DETAILS_WITH_FAVORITE_FLAG:
//                return StopDetailEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // line_detail
            case ALL_LINE_DETAIL: {
                retCursor = sLineDetailQueryBuilder(uri, projection, sortOrder);
                break;
            }

            // stop_detail?favorite_stops=value   // value is 'y', 'n' or 'a'
            case ALL_STOP_DETAIL: {
                retCursor = getStopDetailCursorForFavoriteFlagCursor(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri + " ; match value: " + sUriMatcher.match(uri));
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor sLineDetailQueryBuilder(Uri uri, String[] projection, String sortOrder) {
        return sLineDetailQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getStopDetailCursorForFavoriteFlagCursor(
            Uri uri, String[] projection, String sortOrder) {
        String favoriteFlag = StopDetailEntry.getFavoriteFlagFromUri(uri);
        String selectionClause;
        String[] selectionArgs;
        if (favoriteFlag.equals(StopDetailEntry.ANY_FAVORITE_FLAG)) {
//            Log.v(TAG, "getStopDetailCursorForFavoriteFlagCursor - sStopDetailForTwoFavoriteFlagSelection: " + sStopDetailForTwoFavoriteFlagSelection);
            selectionClause = sStopDetailForTwoFavoriteFlagSelection;
            selectionArgs = new String[]{StopDetailEntry.NON_FAVORITE_FLAG, StopDetailEntry.FAVORITE_FLAG};
        } else {
            selectionClause = sStopDetailForOneFavoriteFlagSelection;
            selectionArgs = new String[]{favoriteFlag};
        }

        return sStopDetailForFavoriteFlagQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selectionClause,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
//        Log.v(TAG, "insert - start");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;

        switch (match) {
            case ALL_LINE_DETAIL:
                _id = db.insert(LineDetailEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = LineDetailEntry.buildUriWithId(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            case ALL_STOP_DETAIL:
                _id = db.insert(StopDetailEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = StopDetailEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
//        Log.v(TAG, "insert - end");
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows and return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case ALL_LINE_DETAIL:
                rowsDeleted = db.delete(
                        LineDetailEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case ALL_STOP_DETAIL:
                rowsDeleted = db.delete(
                        StopDetailEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ALL_STOP_DETAIL:
                rowsUpdated = db.update(StopDetailEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ALL_STOP_DETAIL:
//                Log.v(TAG, "bulkInsert - ALL_STOP_DETAIL");
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(StopDetailEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            // FIXME: 2/09/2016 - investigate if we should throw exception or call super
            default:
//                Log.v(TAG, "bulkInsert - default");
                return super.bulkInsert(uri, values);
        }
    }

    /*
        Creating the the UriMatcher. This UriMatcher will match each URI to the STOPS_DETAILS_WITH_FAVORITE_FLAG
        integer constants defined above.
    */
    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MptContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MptContract.PATH_LINE_DETAIL, ALL_LINE_DETAIL);
        matcher.addURI(authority, MptContract.PATH_STOP_DETAIL, ALL_STOP_DETAIL);
//        matcher.addURI(authority, MptContract.StopDetailEntry.MATCHER_KEY_STOPS_DEATILS_WITH_FAVORITE_FLAG, STOPS_DETAILS_WITH_FAVORITE_FLAG);
        return matcher;
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
