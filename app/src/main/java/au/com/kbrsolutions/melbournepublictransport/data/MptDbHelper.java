package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import au.com.kbrsolutions.melbournepublictransport.data.MptContract.StopDetailsEntry;

/**
 * Manages a local database for MPT data.
 */
public class MptDbHelper extends SQLiteOpenHelper {

        // If you change the database schema, you must increment the database version.
        private static final int DATABASE_VERSION = 1;

        static final String DATABASE_NAME = "mpt.db";

        public MptDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            final String CREATE_STOPS_DETAILS_TABLE = "CREATE TABLE " +
                    StopDetailsEntry.TABLE_NAME + " ("
                    + StopDetailsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + StopDetailsEntry.LINE_NAME + " STRING,"
                    + StopDetailsEntry.STOP_NAME + " STRING,"
                    + StopDetailsEntry.LATITUDE + " REAL,"
                    + StopDetailsEntry.LONGITUDE + " REAL,"
                    + StopDetailsEntry.FAVORITE + " INTEGER,"
                    + ");";
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            // Note that this only fires if you change the version number for your database.
            // It does NOT depend on the version number for your application.
            // If you want to update the schema without wiping data, commenting out the next 2 lines
            // should be your top priority before modifying this method.
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StopDetailsEntry.TABLE_NAME);
            onCreate(sqLiteDatabase);
        }

}
