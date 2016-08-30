package au.com.kbrsolutions.melbournepublictransport.data;

import android.net.Uri;

import java.util.HashMap;

/**
 * Created by business on 20/08/2016.
 */
public class AvailableStopsTable {

    public static final String TABLE_NAME = "available_stops";

    public static final String LINE_NAME = "line_name";

    public static final String STOP_NAME = "stop_name";

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String FAVORITE = "favorite";

    public static HashMap<String, String> projectionMap;

    public static final String CREATE_TABLE_STMT = "CREATE TABLE " + TABLE_NAME
            + " ("
            + LINE_NAME + " STRING PRIMARY KEY, "
            + STOP_NAME + " STRING,"
            + LATITUDE + " REAL,"
            + LONGITUDE + " REAL,"
            + FAVORITE + " INTEGER,"
            + ");";

    // THE CONTENT URI TO OUR PROVIDER
    public static final Uri CONTENT_URI = Uri.parse("content://" + DBUtility.AUTHORITY + "/" + TABLE_NAME);

    // MIME TYPE FOR GROUP OF AVAILABLE_STOPS - for multiple rows
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.kbrsolutions.available_stops";

    // MIME TYPE FOR SINGLE AVAILABLE_STOPS - for a single row
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.kbrsolutions.available_stops";

    public static final String[] availableStopsColumnsArray = new String[] {LINE_NAME, STOP_NAME, LATITUDE, LONGITUDE, FAVORITE};
    static {
        projectionMap = new HashMap<String, String>();
        projectionMap.put(LINE_NAME, LINE_NAME);
        projectionMap.put(STOP_NAME, STOP_NAME);
        projectionMap.put(LATITUDE, LATITUDE);
        projectionMap.put(LONGITUDE, LONGITUDE);
        projectionMap.put(FAVORITE, FAVORITE);
    }
}
