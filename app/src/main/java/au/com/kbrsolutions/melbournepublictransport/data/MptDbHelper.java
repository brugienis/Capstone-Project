package au.com.kbrsolutions.melbournepublictransport.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import au.com.kbrsolutions.melbournepublictransport.data.MptContract.LineDetailEntry;
import au.com.kbrsolutions.melbournepublictransport.data.MptContract.StopDetailEntry;

/**
 * Manages a local database for MPT data.
 */
class MptDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "mpt.db";

    private final String TAG = ((Object) this).getClass().getSimpleName();

    public MptDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*
        table line_detail has no column named line_name (code 1): , while compiling: INSERT INTO line_detail(routeType,line_name_short,line_name,line_id) VALUES (?,?,?,?)
         */
        // Create a table to hold lines details.
        final String CREATE_LINE_DETAIL_TABLE = "CREATE TABLE " +
                  LineDetailEntry.TABLE_NAME + " ("
                + LineDetailEntry._ID + " INTEGER PRIMARY KEY,"
                + LineDetailEntry.COLUMN_ROUTE_TYPE + " INTEGER NOT NULL, "
                + LineDetailEntry.COLUMN_LINE_ID + " STRING UNIQUE NOT NULL, "
                + LineDetailEntry.COLUMN_LINE_NAME + " STRING UNIQUE NOT NULL, "
                + LineDetailEntry.COLUMN_LINE_NAME_SHORT + " STRING UNIQUE NOT NULL "
                + ");";

        // Create a table to hold stops details.
        final String CREATE_STOP_DETAIL_TABLE = "CREATE TABLE " +
                  StopDetailEntry.TABLE_NAME + " ("
                + StopDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + StopDetailEntry.COLUMN_LINE_KEY + " INTEGER,"
                + StopDetailEntry.COLUMN_ROUTE_TYPE + " INTEGER NOT NULL, "
                + StopDetailEntry.COLUMN_STOP_ID + " STRING NOT NULL, "
                + StopDetailEntry.COLUMN_LOCATION_NAME + " STRING NULL, "
                + StopDetailEntry.COLUMN_SUBURB + " STRING NULL, "
                + StopDetailEntry.COLUMN_LATITUDE + " REAL NOT NULL, "
                + StopDetailEntry.COLUMN_LONGITUDE + " REAL NOT NULL, "
                + StopDetailEntry.COLUMN_FAVORITE + " STRING, "

                // Set up the column_line_key column as a foreign key to line_detail table.
                + " FOREIGN KEY (" + StopDetailEntry.COLUMN_LINE_KEY + ") REFERENCES "
                + LineDetailEntry.TABLE_NAME + " (" + LineDetailEntry._ID + ")"

                // To assure the application have just one stop_detail entry stop_id
                // per location_name, it's created a UNIQUE constraint with REPLACE strategy
                + " UNIQUE (" + StopDetailEntry.COLUMN_STOP_ID + ", "
//                + StopDetailEntry.COLUMN_LOCATION_NAME + ") ON CONFLICT IGNORE);";
                + StopDetailEntry.COLUMN_LOCATION_NAME + ") ON CONFLICT REPLACE);";


        sqLiteDatabase.execSQL(CREATE_LINE_DETAIL_TABLE);
        sqLiteDatabase.execSQL(CREATE_STOP_DETAIL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LineDetailEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StopDetailEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
