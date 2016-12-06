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
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.fragments.StopsFragment;

/**
 *
 * Adapter used by StopsFragment.
 *
 */
public class StopsAdapter extends CursorAdapter {

    private static StopsFragment.OnStopFragmentInteractionListener mListener;

    @SuppressWarnings("unused")
    private static final String TAG = StopsAdapter.class.getSimpleName();

    public static class ViewHolder {
        public final TextView locationNameView;
        public final ImageView departuresImageId;
        public final ImageView mapImageId;
        public StopDetails stopDetails;

        public ViewHolder(View view) {
            locationNameView = (TextView) view.findViewById(R.id.locationNameId);

            departuresImageId = (ImageView) view.findViewById(R.id.departuresImageId);
            departuresImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that a departure image was touched.
                        startNextDeparturesSearch(stopDetails);
                    }
                }
            });

            mapImageId = (ImageView) view.findViewById(R.id.mapImageId);
            mapImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSelectedStopOnMap(stopDetails.locationName, new LatLngDetails(stopDetails.latitude, stopDetails.longitude));
                }
            });
        }
    }

    private static void startNextDeparturesSearch(StopDetails stopDetails) {
        mListener.startNextDeparturesSearch(stopDetails);
    }

    private static void showSelectedStopOnMap(String stopName, LatLngDetails latLonDetails) {
        mListener.showStopOnMap(stopName, latLonDetails);
    }

    public StopsAdapter(
            Context context,
            StopsFragment.OnStopFragmentInteractionListener listener) {
        super(context, null, 0);
        mListener = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_stops_list, parent, false);
        StopsAdapter.ViewHolder viewHolder = new StopsAdapter.ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        StopsAdapter.ViewHolder viewHolder = (StopsAdapter.ViewHolder) view.getTag();

        String locationName = cursor.getString(StopsFragment.COL_STOP_DETAILS_LOCATION_NAME);

        StopDetails stopDetails = new StopDetails(
                cursor.getInt(StopsFragment.COL_STOP_DETAILS_ID),
                cursor.getInt(StopsFragment.COL_STOP_DETAILS_ROUTE_TYPE),
                cursor.getString(StopsFragment.COL_STOP_DETAILS_STOP_ID),
                cursor.getString(StopsFragment.COL_STOP_DETAILS_LOCATION_NAME),
                cursor.getDouble(StopsFragment.COL_STOP_DETAILS_LATITUDE),
                cursor.getDouble(StopsFragment.COL_STOP_DETAILS_LONGITUDE),
                cursor.getString(StopsFragment.COL_STOP_DETAILS_FAVORITE));

        viewHolder.locationNameView.setText(locationName);
        viewHolder.stopDetails = stopDetails;
    }
}
