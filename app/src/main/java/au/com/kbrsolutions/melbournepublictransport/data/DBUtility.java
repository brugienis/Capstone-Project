package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by business on 20/08/2016.
 */
public class DBUtility extends SQLiteOpenHelper {

    //	public static final String AUTHORITY = "SpeedMonitorContentProvider";
    public static final String AUTHORITY = "DBUtility";

    private static final String DATABASE_NAME = "mpt.db";

    private static final int DATABASE_VERSION = 1;

    private static final UriMatcher sUriMatcher;
    private static final int AVAILABLE_STOPS = 1;
//    private static final int SPEED = 2;
//    private static final int STATS_SUMMARY = 3;
//    private static final int TRACE = 4;
//    private static final int TEST_SPEED = 5;

    private final String TAG = ((Object) this).getClass().getSimpleName();

    private static Context context;
    private static DBUtility _INSTANCE = null;

    // FIXME: 20/08/2016 use enum?
    public static DBUtility getDBUtility() {
        if (_INSTANCE == null) {
            synchronized (DBUtility.class) {
                if (_INSTANCE == null) {
                    _INSTANCE = new DBUtility(context);
                }
            }
        }
        return _INSTANCE;
    }

    // INSTANTIATE AND SET STATIC VARIABLES
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//        sUriMatcher.addURI(AUTHORITY, SpeedLimitTable.TABLE_NAME, SPEED_LIMIT);
        sUriMatcher.addURI(AUTHORITY, AvailableStopsTable.TABLE_NAME, AVAILABLE_STOPS);
//        sUriMatcher.addURI(AUTHORITY, StatsSummaryTable.TABLE_NAME, STATS_SUMMARY);
//        sUriMatcher.addURI(AUTHORITY, TestSpeedTable.TABLE_NAME, TEST_SPEED);
//        sUriMatcher.addURI(AUTHORITY, TraceTable.TABLE_NAME, TRACE);
    }

    public static void init(Context context) {
//		Log.i(LOC_CAT_TAG, "init - context: " + context);
        DBUtility.context = context;
    }

    private DBUtility(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//		Log.i(LOC_CAT_TAG, "constructor called - context: " + context);
    }

    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int count;

        switch (sUriMatcher.match(uri)) {
            case AVAILABLE_STOPS:
                count = db.delete(AvailableStopsTable.TABLE_NAME, where, whereArgs);
                break;
//            case SPEED:
//                count = db.delete(SpeedTable.TABLE_NAME, where, whereArgs);
//                break;
//            case STATS_SUMMARY:
//                count = db.delete(StatsSummaryTable.TABLE_NAME, where, whereArgs);
//                break;
//            case TEST_SPEED:
//                count = db.delete(TestSpeedTable.TABLE_NAME, where, whereArgs);
//                break;
//            case TRACE:
//                count = db.delete(TraceTable.TABLE_NAME, where, whereArgs);
//                break;
            default:
//        		Log.i(LOC_CAT_TAG, "SpeedMonitorContentProvider - delete - WHAT THE HELL IS THAT - URI: " + uri);
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return count;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case AVAILABLE_STOPS:
//    		Log.i(SpeedLOC_CAT_TAG, "SpeedMonitorContentProvider - query - SPEED_LIMIT");
                qb.setTables(AvailableStopsTable.TABLE_NAME);
                qb.setProjectionMap(AvailableStopsTable.projectionMap);
                break;
//            case SPEED:
//                qb.setTables(SpeedTable.TABLE_NAME);
////    		Log.i(SpeedLOC_CAT_TAG, "SpeedMonitorContentProvider - query - SPEED");
//                break;
//            case STATS_SUMMARY:
//                qb.setTables(StatsSummaryTable.TABLE_NAME);
////    		Log.i(SpeedLOC_CAT_TAG, "SpeedMonitorContentProvider - query - SPEED");
//                break;
//            case TEST_SPEED:
//                qb.setTables(TestSpeedTable.TABLE_NAME);
////    		Log.i(SpeedLOC_CAT_TAG, "SpeedMonitorContentProvider - query - SPEED");
//                break;
//            case TRACE:
//                qb.setTables(TraceTable.TABLE_NAME);
////    		Log.i(SpeedLOC_CAT_TAG, "SpeedMonitorContentProvider - query - TRACE");
//                break;

            default:
//    		Log.i(LOC_CAT_TAG, "SpeedMonitorContentProvider - query - WHAT THE HELL IS THAT - URI: " + uri);
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        return c;
    }

    public Uri insert(Uri uri, ContentValues initialValues) {
        String tableName = null;
//    	Log.i(SpeedLOC_CAT_TAG, "SpeedMonitorContentProvider - insert - uri: " + uri + " - " + "; sUriMatcher.match(uri): " + sUriMatcher.match(uri));

        switch (sUriMatcher.match(uri)) {
            case AVAILABLE_STOPS:
//    		Log.i(SpeedLOC_CAT_TAG, "SpeedMonitorContentProvider - SPEED_LIMIT");
                tableName = AvailableStopsTable.TABLE_NAME;
                break;
//            case SPEED:
//                tableName = SpeedTable.TABLE_NAME;
////    		Log.i(SpeedLOC_CAT_TAG, "SpeedMonitorContentProvider - SPEED");
//                break;
//            case STATS_SUMMARY:
//                tableName = StatsSummaryTable.TABLE_NAME;
////    		Log.i(SpeedLOC_CAT_TAG, "SpeedMonitorContentProvider - SPEED");
//                break;
//            case TEST_SPEED:
//                tableName = TestSpeedTable.TABLE_NAME;
////    		Log.i(SpeedLOC_CAT_TAG, "SpeedMonitorContentProvider - SPEED");
//                break;
//            case TRACE:
//                tableName = TraceTable.TABLE_NAME;
////    		Log.i(SpeedLOC_CAT_TAG, "SpeedMonitorContentProvider - TRACE");
//                break;

            default:
//    		Log.i(LOC_CAT_TAG, "SpeedMonitorContentProvider - insert - WHAT THE HELL IS THAT - URI: " + uri);
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = getWritableDatabase();
        long rowId = db.insert(tableName, null, values);
        if (rowId > 0) {
            Uri someUri = ContentUris.withAppendedId(uri, rowId);
            return someUri;
        }

        throw new SQLException("SpeedMonitorContentProvider - insert - failed to insert row into " + uri);
    }

    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case AVAILABLE_STOPS:
                count = db.update(AvailableStopsTable.TABLE_NAME, values, where, whereArgs);
                break;
//            case STATS_SUMMARY:

//                count = db.update(StatsSummaryTable.TABLE_NAME, values, where, whereArgs);
//                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return count;
    }

//    ------------------------------------    utility methods - start  ------------------------------------

    public Cursor execRawQuery (String sql, String[] selectionArgs) {
//		Log.i(LOC_CAT_TAG, "execRawQuery - sql: " + sql);
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, selectionArgs);
        return c;
    }

    public void execRawNOTQuery (String sql) {
//		Log.i(LOC_CAT_TAG, "execRawNOTQuery - sql: " + sql);
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(sql);
    }
//  ------------------------------------    utility methods - end      ------------------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w("LOG_TAG", "Creating database - start");
        // CREATE tables
//        db.execSQL(SpeedLimitTable.CREATE_TABLE_STMT);
        db.execSQL(AvailableStopsTable.CREATE_TABLE_STMT);
//        db.execSQL(StatsSummaryTable.CREATE_TABLE_STMT);
//        db.execSQL(TestSpeedTable.CREATE_TABLE_STMT);
//        db.execSQL(TraceTable.CREATE_TABLE_STMT);
        Log.w("LOG_TAG", "Creating database - end");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("LOG_TAG", "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");

        // KILL PREVIOUS TABLES IF UPGRADED
        db.execSQL("DROP TABLE IF EXISTS " + AvailableStopsTable.TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + SpeedTable.TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + StatsSummaryTable.TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + TestSpeedTable.TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + TraceTable.TABLE_NAME);

        // CREATE NEW INSTANCE OF SCHEMA
        onCreate(db);
    }
}


