package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import au.com.kbrsolutions.melbournepublictransport.data.MptContract.StopDetailEntry;

/**
 * Manages a local database for MPT data.
 */
public class MptDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "mpt.db";

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public MptDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.v(TAG, "onCreate - start");

        final String CREATE_STOPS_DETAILS_TABLE = "CREATE TABLE " +
                StopDetailEntry.TABLE_NAME + " ("
                + StopDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + StopDetailEntry.COLUMN_LINE_NAME + " STRING,"
                + StopDetailEntry.COLUMN_STOP_NAME + " STRING,"
                + StopDetailEntry.COLUMN_LATITUDE + " REAL,"
                + StopDetailEntry.COLUMN_LONGITUDE + " REAL,"
                + StopDetailEntry.COLUMN_FAVORITE + " INTEGER"
                + ");";

        sqLiteDatabase.execSQL(CREATE_STOPS_DETAILS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.v(TAG, "onUpgrade - start");
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "available_stops");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StopDetailEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
