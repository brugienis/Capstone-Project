package au.com.kbrsolutions.melbournepublictransport.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.LatLonDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.fragments.StopDetailFragment;

/**
 * Created by business on 2/10/2016.
 */

public class StopDetailAdapterRv extends CursorAdapter {

//    private StopDetailFragment mAddStopFragment;
    private static StopDetailFragment.OnStopFragmentInteractionListener mListener;

    private static final String TAG = StopDetailAdapterRv.class.getSimpleName();

    public static class ViewHolder {
        public final TextView locationNameView;
        public StopDetails stopDetails;
//        StopDetailFragment mAddStopFragment;

        public ViewHolder(View view) {
//            mAddStopFragment = addStopFragment;
            locationNameView = (TextView) view.findViewById(R.id.locationNameId);

            ImageView mapImageId = (ImageView) view.findViewById(R.id.mapImageId);
            mapImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSelectedStopOnMap(new LatLonDetails(stopDetails.latitude, stopDetails.longitude));
                }
            });
        }
    }

    private static void showSelectedStopOnMap(LatLonDetails latLonDetails) {
        mListener.showSelectedStopOnMap(latLonDetails);
    }

    public StopDetailAdapterRv(
            Context context,
            Cursor c,
            int flags,
            StopDetailFragment.OnStopFragmentInteractionListener listener) {
        super(context, c, flags);
//        mAddStopFragment = addStopFragment;
        mListener = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_add_stops_list_view, parent, false);
        StopDetailAdapterRv.ViewHolder viewHolder = new StopDetailAdapterRv.ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        StopDetailAdapterRv.ViewHolder viewHolder = (StopDetailAdapterRv.ViewHolder) view.getTag();

        String locationName = cursor.getString(StopDetailFragment.COL_STOP_DETAILS_LOCATION_NAME);

        StopDetails stopDetails = new StopDetails(
                cursor.getInt(StopDetailFragment.COL_STOP_DETAILS_ID),
                cursor.getInt(StopDetailFragment.COL_STOP_DETAILS_ROUTE_TYPE),
                cursor.getString(StopDetailFragment.COL_STOP_DETAILS_STOP_ID),
                cursor.getString(StopDetailFragment.COL_STOP_DETAILS_LOCATION_NAME),
                cursor.getDouble(StopDetailFragment.COL_STOP_DETAILS_LATITUDE),
                cursor.getDouble(StopDetailFragment.COL_STOP_DETAILS_LONGITUDE),
                cursor.getString(StopDetailFragment.COL_STOP_DETAILS_FAVORITE));

        // FIXME: 7/09/2016 - add description
//        viewHolder.descriptionView.setText(description);

        viewHolder.locationNameView.setText(locationName);
        viewHolder.stopDetails = stopDetails;
    }
}
