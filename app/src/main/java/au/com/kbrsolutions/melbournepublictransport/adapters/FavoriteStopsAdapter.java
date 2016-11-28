package au.com.kbrsolutions.melbournepublictransport.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.fragments.FavoriteStopsFragment;

import static au.com.kbrsolutions.melbournepublictransport.fragments.FavoriteStopsFragment.COL_STOP_DETAILS_LOCATION_NAME;

/**
 * Created by business on 1/10/2016.
 */

public class FavoriteStopsAdapter extends CursorAdapter {

    private static boolean mIsInSettingsActivityFlag;
    private static FavoriteStopsFragment.OnFavoriteStopsFragmentInteractionListener mListener;

    private static final String TAG = FavoriteStopsAdapter.class.getSimpleName();

    public static class ViewHolder {
        public final TextView locationNameView;
        public ImageView mapImageId;
        public ImageView departuresImageId;
        public ImageView garbageInfoImage;
        public StopDetails stopDetails;
        private int selectedImagePos = 0;

        public ViewHolder(View view) {
            locationNameView = (TextView) view.findViewById(R.id.locationNameId);
            departuresImageId = (ImageView) view.findViewById(R.id.departuresImageId);
            mapImageId = (ImageView) view.findViewById(R.id.mapImageId);
            garbageInfoImage = (ImageView) view.findViewById(R.id.garbageImageId);

//            Log.v(TAG, "ViewHolder - mIsInSettingsActivityFlag: " + mIsInSettingsActivityFlag);
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

        public void selectImageView(boolean next) {

        }
    }

    private static void showSelectedStopOnMap(String stopName, LatLngDetails latLonDetails) {
        mListener.showStopOnMap(stopName, latLonDetails);
    }

    private static void startNextDeparturesSearch(StopDetails stopDetails) {
        Log.v(TAG, "startNextDeparturesSearch - stopId/locationName : " + stopDetails.stopId + "/" + stopDetails.locationName);
        mListener.startNextDeparturesSearch(stopDetails);
    }

    private static final String NON_FAVORITE_VALUE = "n";
    private static void updateStopDetailRow(StopDetails stopDetails) {
        mListener.updateStopDetailRow(stopDetails.id, NON_FAVORITE_VALUE);
    }

    public FavoriteStopsAdapter(
            Context context,
            Cursor c,
            int flags,
            FavoriteStopsFragment.OnFavoriteStopsFragmentInteractionListener listener,
            boolean isInSettingsActivityFlag) {
        super(context, c, flags);

        /* if parent is WidgetStopsActivity, the listener is not needed. mListener is static and */
        /* points to MainActivity - do not change it.                                            */
        if (!isInSettingsActivityFlag) {
            mListener = listener;
        }
        mIsInSettingsActivityFlag = isInSettingsActivityFlag;
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

        // FIXME: 7/09/2016 - add description
//        viewHolder.descriptionView.setText(description);

        viewHolder.locationNameView.setText(locationName);
        viewHolder.stopDetails = stopDetails;
    }
}
