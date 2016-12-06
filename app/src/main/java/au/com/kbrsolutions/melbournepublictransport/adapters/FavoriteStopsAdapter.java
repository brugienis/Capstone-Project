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
import au.com.kbrsolutions.melbournepublictransport.data.MptContract;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.fragments.FavoriteStopsFragment;

import static au.com.kbrsolutions.melbournepublictransport.fragments.FavoriteStopsFragment.COL_STOP_DETAILS_LOCATION_NAME;

/**
 *
 * Adapter used by FavoriteStopsFragment.
 *
 */
public class FavoriteStopsAdapter extends CursorAdapter {

    private static boolean mIsInSettingsActivityFlag;
    private static FavoriteStopsFragment.OnFavoriteStopsFragmentInteractionListener mListener;
    private static String sFavoriteStopFalseValue;

    @SuppressWarnings("unused")
    private static final String TAG = FavoriteStopsAdapter.class.getSimpleName();

    public static class ViewHolder {
        public final TextView locationNameView;
        public final ImageView mapImageId;
        public final ImageView departuresImageId;
        public final ImageView garbageInfoImage;
        public StopDetails stopDetails;

        public ViewHolder(View view) {
            locationNameView = (TextView) view.findViewById(R.id.locationNameId);
            departuresImageId = (ImageView) view.findViewById(R.id.departuresImageId);
            mapImageId = (ImageView) view.findViewById(R.id.mapImageId);
            garbageInfoImage = (ImageView) view.findViewById(R.id.garbageImageId);

            if (mIsInSettingsActivityFlag) {
                mapImageId.setVisibility(View.GONE);
                departuresImageId.setVisibility(View.GONE);
                garbageInfoImage.setVisibility(View.GONE);
            } else {
                mapImageId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showSelectedStopOnMap(stopDetails.locationName, new LatLngDetails(stopDetails.latitude, stopDetails.longitude));
                    }
                });

                departuresImageId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startNextDeparturesSearch(stopDetails);
                    }
                });

                garbageInfoImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateStopDetailRow(stopDetails);
                    }
                });
            }
        }
    }

    private static void showSelectedStopOnMap(String stopName, LatLngDetails latLonDetails) {
        mListener.showStopOnMap(stopName, latLonDetails);
    }

    private static void startNextDeparturesSearch(StopDetails stopDetails) {
        mListener.startNextDeparturesSearch(stopDetails);
    }

    private static void updateStopDetailRow(StopDetails stopDetails) {
        mListener.updateStopDetailRow(stopDetails.id, sFavoriteStopFalseValue);
    }

    public FavoriteStopsAdapter(
            Context context,
            FavoriteStopsFragment.OnFavoriteStopsFragmentInteractionListener listener,
            boolean isInSettingsActivityFlag) {
        super(context, null, 0);

        /* if parent is WidgetStopsActivity, the listener is not needed. mListener is static and */
        /* points to MainActivity - do not change it.                                            */
        if (!isInSettingsActivityFlag) {
            mListener = listener;
        }
        mIsInSettingsActivityFlag = isInSettingsActivityFlag;
        sFavoriteStopFalseValue = MptContract.StopDetailEntry.NON_FAVORITE_FLAG;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_favorite_stops_list, parent, false);
        FavoriteStopsAdapter.ViewHolder viewHolder = new FavoriteStopsAdapter.ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        FavoriteStopsAdapter.ViewHolder viewHolder = (FavoriteStopsAdapter.ViewHolder) view.getTag();

        String locationName = cursor.getString(COL_STOP_DETAILS_LOCATION_NAME);

        StopDetails stopDetails = new StopDetails(
                cursor.getInt(FavoriteStopsFragment.COL_STOP_DETAILS_ID),
                cursor.getInt(FavoriteStopsFragment.COL_STOP_DETAILS_ROUTE_TYPE),
                cursor.getString(FavoriteStopsFragment.COL_STOP_DETAILS_STOP_ID),
                cursor.getString(FavoriteStopsFragment.COL_STOP_DETAILS_LOCATION_NAME),
                cursor.getDouble(FavoriteStopsFragment.COL_STOP_DETAILS_LATITUDE),
                cursor.getDouble(FavoriteStopsFragment.COL_STOP_DETAILS_LONGITUDE),
                cursor.getString(FavoriteStopsFragment.COL_STOP_DETAILS_FAVORITE));

        viewHolder.locationNameView.setText(locationName);
        viewHolder.stopDetails = stopDetails;
    }
}
