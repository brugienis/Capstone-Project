package au.com.kbrsolutions.melbournepublictransport.fragments;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.kbrsolutions.melbournepublictransport.R;

/**
 * Created by business on 10/09/2016.
 */
public class FavoriteStopDetailAdapter extends CursorAdapter {

    private final String TAG = ((Object) this).getClass().getSimpleName();

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
//        public final TextView locationNameView;
        TextView stopNameTv;

        public ViewHolder(View view) {
//            locationNameView = (TextView) view.findViewById(R.id.locationNameId);
            stopNameTv = (TextView) view.findViewById(R.id.locationNameId);
//            StopDetails folderItem = objects.get(position);
//            stopNameTv.setText(folderItem.locationName);
            stopNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    processSelectedStop(position);
                }
            });

            ImageView mapImageId = (ImageView) view.findViewById(R.id.mapImageId);
            mapImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    showSelectedRowOnMap(position);
                }
            });

            ImageView garbageInfoImage = (ImageView) view.findViewById(R.id.garbageImageId);
            garbageInfoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    removeSelectedRow(position);
                }
            });
        }
    }

    public FavoriteStopDetailAdapter(Context context, Cursor c, int flags) {
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

        String locationName = cursor.getString(AddStopFragment.COL_STOP_DETAILS_ID) + " - "
                + cursor.getString(AddStopFragment.COL_STOP_DETAILS_STOP_ID) + " - "
                + cursor.getString(AddStopFragment.COL_STOP_DETAILS_LOCATION_NAME);

        // FIXME: 7/09/2016 - add description
//        viewHolder.descriptionView.setText(description);

//        viewHolder.locationNameView.setText(locationName);
        viewHolder.stopNameTv.setText(cursor.getString(AddStopFragment.COL_STOP_DETAILS_LOCATION_NAME));
    }
}
