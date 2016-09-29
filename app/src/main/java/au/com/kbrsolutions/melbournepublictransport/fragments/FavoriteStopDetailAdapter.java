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

/**
 * Created by business on 10/09/2016.
 */
public class FavoriteStopDetailAdapter extends CursorAdapter {

    private FavoriteStopsFragment mFavoriteStopsFragment;

    private static final String TAG = StopDetailAdapter.class.getSimpleName();

    public static class ViewHolder {
        public final TextView locationNameView;
        public StopDetails mStopDetails;
        FavoriteStopsFragment mFavoriteStopsFragment;

        public ViewHolder(View view, FavoriteStopsFragment favoriteStopsFragment) {
            mFavoriteStopsFragment = favoriteStopsFragment;
            locationNameView = (TextView) view.findViewById(R.id.locationNameId);

            ImageView mapImageId = (ImageView) view.findViewById(R.id.mapImageId);
            mapImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFavoriteStopsFragment.handleMapClicked(mStopDetails);
                }
            });

            ImageView departuresImageId = (ImageView) view.findViewById(R.id.departuresImageId);
            departuresImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFavoriteStopsFragment.handleNextDeparturesClicked(mStopDetails);
                }
            });

            ImageView garbageInfoImage = (ImageView) view.findViewById(R.id.garbageImageId);
            garbageInfoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFavoriteStopsFragment.removeSelectedStop(mStopDetails);
                }
            });
        }
    }

    public FavoriteStopDetailAdapter(FavoriteStopsFragment favoriteStopsFragment, Cursor c, int flags) {
        super(favoriteStopsFragment.getActivity().getApplicationContext(), c, flags);
        mFavoriteStopsFragment = favoriteStopsFragment;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_favorite_stops_list_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mFavoriteStopsFragment);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String locationName = cursor.getString(StopDetailFragment.COL_STOP_DETAILS_LOCATION_NAME);

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
        viewHolder.mStopDetails = stopDetails;
    }
}
