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

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        // Read weather forecast from cursor
        String locationName = cursor.getString(AddStopFragment.COL_STOP_DETAILS_LOCATION_NAME);
        // Find TextView and set weather forecast on it
        // FIXME: 7/09/2016 - add description
//        viewHolder.descriptionView.setText(description);

        viewHolder.locationNameView.setText(locationName);
    }
}
