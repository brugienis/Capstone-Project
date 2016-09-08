package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import au.com.kbrsolutions.melbournepublictransport.R;

/**
 * Created by business on 7/09/2016.
 */
public class StopDetailAdapter extends CursorAdapter {

    private final String TAG = ((Object) this).getClass().getSimpleName();

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView locationNameView;

        public ViewHolder(View view) {
            locationNameView = (TextView) view.findViewById(R.id.locationNameId);
        }
    }

    public StopDetailAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_add_stops_list_view, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    private String prevCursorLocationName;

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
//        String currCursorLocationName =
//        cursor.getString(AddStopFragment.COL_STOP_DETAILS_STOP_ID) + " - "
//                + cursor.getString(AddStopFragment.COL_STOP_DETAILS_LOCATION_NAME);
//
//        Log.v(TAG, "bindView - prev/curr: " + prevCursorLocationName + "/" + currCursorLocationName);

//        if (prevCursorLocationName == null || !prevCursorLocationName.equals(currCursorLocationName)) {
//            prevCursorLocationName = currCursorLocationName;
//        } else {
//            Log.v(TAG, "bindView - ignoring curr: " + currCursorLocationName);
//            return;
//        }
//        Log.v(TAG, "bindView - adding curr  : " + currCursorLocationName);

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String locationName = cursor.getString(AddStopFragment.COL_STOP_DETAILS_ID) + " - "
                + cursor.getString(AddStopFragment.COL_STOP_DETAILS_STOP_ID) + " - "
                + cursor.getString(AddStopFragment.COL_STOP_DETAILS_LOCATION_NAME);

        // FIXME: 7/09/2016 - add description
//        viewHolder.descriptionView.setText(description);

        viewHolder.locationNameView.setText(locationName);
    }
}
