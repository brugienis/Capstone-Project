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
import au.com.kbrsolutions.melbournepublictransport.data.LatLonDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.fragments.FavoriteStopsFragmentRv;

import static au.com.kbrsolutions.melbournepublictransport.fragments.FavoriteStopsFragmentRv.COL_STOP_DETAILS_LOCATION_NAME;

//import au.com.kbrsolutions.melbournepublictransport.fragments.FavoriteStopAdapterRv;

/**
 * Created by business on 1/10/2016.
 */

public class FavoriteStopsAdapterRv extends CursorAdapter {

    //    private FavoriteStopFragmentRv mFavoriteStopFragmentRv;
    private static FavoriteStopsFragmentRv.OnFavoriteStopsFragmentInteractionListener mListener;

    private static final String TAG = FavoriteStopsAdapterRv.class.getSimpleName();

    public static class ViewHolder {
        public final TextView locationNameView;
        public StopDetails stopDetails;
//        FavoriteStopFragmentRv mFavoriteStopFragmentRv;

        public ViewHolder(View view) {
            locationNameView = (TextView) view.findViewById(R.id.locationNameId);

            ImageView mapImageId = (ImageView) view.findViewById(R.id.mapImageId);
            mapImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSelectedStopOnMap(new LatLonDetails(stopDetails.latitude, stopDetails.longitude));
                }
            });

            ImageView departuresImageId = (ImageView) view.findViewById(R.id.departuresImageId);
            departuresImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startNextDeparturesSearch(stopDetails);
                }
            });

            ImageView garbageInfoImage = (ImageView) view.findViewById(R.id.garbageImageId);
            garbageInfoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateStopDetailRow(stopDetails);
                }
            });
        }
    }

    private static void showSelectedStopOnMap(LatLonDetails latLonDetails) {
        mListener.showSelectedStopOnMap(latLonDetails);
    }

    private static void startNextDeparturesSearch(StopDetails stopDetails) {
//        Log.v(TAG, "startNextDeparturesSearch - mListener: " + mListener);
        mListener.startNextDeparturesSearch(stopDetails);
    }

    private static final String NON_FAVORITE_VALUE = "n";
    private static void updateStopDetailRow(StopDetails stopDetails) {
        mListener.updateStopDetailRow(stopDetails.id, NON_FAVORITE_VALUE);
    }

    public FavoriteStopsAdapterRv(Context context, Cursor c, int flags, FavoriteStopsFragmentRv.OnFavoriteStopsFragmentInteractionListener listener) {
        super(context, c, flags);
        mListener = listener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_favorite_stops_list_view, parent, false);
        FavoriteStopsAdapterRv.ViewHolder viewHolder = new FavoriteStopsAdapterRv.ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        FavoriteStopsAdapterRv.ViewHolder viewHolder = (FavoriteStopsAdapterRv.ViewHolder) view.getTag();

        String locationName = cursor.getString(COL_STOP_DETAILS_LOCATION_NAME);

        StopDetails stopDetails = new StopDetails(
                cursor.getInt(FavoriteStopsFragmentRv.COL_STOP_DETAILS_ID),
                cursor.getInt(FavoriteStopsFragmentRv.COL_STOP_DETAILS_ROUTE_TYPE),
                cursor.getString(FavoriteStopsFragmentRv.COL_STOP_DETAILS_STOP_ID),
                cursor.getString(FavoriteStopsFragmentRv.COL_STOP_DETAILS_LOCATION_NAME),
                cursor.getDouble(FavoriteStopsFragmentRv.COL_STOP_DETAILS_LATITUDE),
                cursor.getDouble(FavoriteStopsFragmentRv.COL_STOP_DETAILS_LONGITUDE),
                cursor.getString(FavoriteStopsFragmentRv.COL_STOP_DETAILS_FAVORITE));

        // FIXME: 7/09/2016 - add description
//        viewHolder.descriptionView.setText(description);

        viewHolder.locationNameView.setText(locationName);
        viewHolder.stopDetails = stopDetails;
    }
}
