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
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;

public class StopDetailAdapter extends CursorAdapter {

    private AddStopFragment mAddStopFragment;

    private static final String TAG = StopDetailAdapter.class.getSimpleName();

//    public static class ViewHolder {
    public class ViewHolder {
        public final TextView locationNameView;
        public StopDetails mStopDetails;

        public ViewHolder(View view) {
//            mStopDetails = stopDetails;
            locationNameView = (TextView) view.findViewById(R.id.locationNameId);

            ImageView mapImageId = (ImageView) view.findViewById(R.id.mapImageId);
            mapImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Log.v(TAG, "ViewHolder - locationName: " + mStopDetails.locationName);
                    showSelectedRowOnMap(mStopDetails);
                }
            });
        }
    }

    public StopDetailAdapter(AddStopFragment addStopFragment, Cursor c, int flags) {
        super(addStopFragment.getActivity().getApplicationContext(), c, flags);
        mAddStopFragment = addStopFragment;
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

//        String locationName = cursor.getString(AddStopFragment.COL_STOP_DETAILS_ID) + " - "
//                + cursor.getString(AddStopFragment.COL_STOP_DETAILS_STOP_ID) + " - "
//                + cursor.getString(AddStopFragment.COL_STOP_DETAILS_LOCATION_NAME);
        String locationName = cursor.getString(AddStopFragment.COL_STOP_DETAILS_LOCATION_NAME);

        StopDetails stopDetails = new StopDetails(
                cursor.getInt(AddStopFragment.COL_STOP_DETAILS_ID),
                cursor.getInt(AddStopFragment.COL_STOP_DETAILS_ROUTE_TYPE),
                cursor.getString(AddStopFragment.COL_STOP_DETAILS_STOP_ID),
                cursor.getString(AddStopFragment.COL_STOP_DETAILS_LOCATION_NAME),
                cursor.getDouble(AddStopFragment.COL_STOP_DETAILS_LATITUDE),
                cursor.getDouble(AddStopFragment.COL_STOP_DETAILS_LONGITUDE),
                cursor.getString(AddStopFragment.COL_STOP_DETAILS_FAVORITE));

        // FIXME: 7/09/2016 - add description
//        viewHolder.descriptionView.setText(description);

        viewHolder.locationNameView.setText(locationName);
        viewHolder.mStopDetails = stopDetails;
    }

    private void showSelectedRowOnMap(StopDetails stopDetails) {
//        Log.v(TAG, "showSelectedRowOnMap - position: " + position);
        mAddStopFragment.handleMapClicked(stopDetails);
    }
}
