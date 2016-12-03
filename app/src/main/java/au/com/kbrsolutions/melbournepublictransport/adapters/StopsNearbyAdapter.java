package au.com.kbrsolutions.melbournepublictransport.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopsNearbyDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.fragments.StopsNearbyFragment;

import static au.com.kbrsolutions.melbournepublictransport.R.id.departuresImageId;
import static au.com.kbrsolutions.melbournepublictransport.R.id.mapImageId;
import static au.com.kbrsolutions.melbournepublictransport.R.id.stopAddress;
import static au.com.kbrsolutions.melbournepublictransport.R.id.stopName;

/**
 *
 * Adapter used by StopsNearbyFragment.
 *
 */
public class StopsNearbyAdapter<T> extends ArrayAdapter<StopsNearbyDetails> {

    private final Activity mActivity;
    private final List<StopsNearbyDetails> mValues;
    private final StopsNearbyFragment.OnNearbyStopsFragmentInteractionListener mListener;

    private static final String TAG = StopsNearbyAdapterRv.class.getSimpleName();

    public StopsNearbyAdapter(Activity activity, List<StopsNearbyDetails> items, StopsNearbyFragment.OnNearbyStopsFragmentInteractionListener listener) {
        super(activity.getApplicationContext(), -1, items);
        mActivity = activity;
        mValues = items;
        mListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final ViewHolder holder;
        if (v == null) {
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.fragment_stops_nearby_list, parent, false);

            holder = new ViewHolder();
            holder.transportImage = (ImageView) v.findViewById(R.id.transportImageId);
            holder.stopName = (TextView) v.findViewById(stopName);
            holder.stopAddress = (TextView) v.findViewById(stopAddress);
            holder.mapImageId = (ImageView) v.findViewById(mapImageId);
            holder.mapImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that a map image was touched.
                        mListener.showStopOnMap(
                                holder.nearbyStopsDetails.stopName,
                                new LatLngDetails(holder.nearbyStopsDetails.latitude,
                                        holder.nearbyStopsDetails.longitude));
                    }
                }
            });
            holder.departuresImageId = (ImageView) v.findViewById(departuresImageId);
            holder.departuresImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.v(TAG, "ViewHolder - onClick");
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that a departure image was touched.
                        mListener.startNextDeparturesSearch(new StopDetails(
                                -1,
                                holder.nearbyStopsDetails.routeType,
                                holder.nearbyStopsDetails.stopId,
                                holder.nearbyStopsDetails.stopName,
                                holder.nearbyStopsDetails.latitude,
                                holder.nearbyStopsDetails.longitude,
                                "n"
                        ));
                    }
                }
            });

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        StopsNearbyDetails nearbyStopsDetails = mValues.get(position);
        holder.stopName.setText(nearbyStopsDetails.stopName);

        if (nearbyStopsDetails.routeType == StopsNearbyDetails.TRAIN_ROUTE_TYPE) {
            holder.stopAddress.setText(nearbyStopsDetails.suburb);
            holder.transportImage.setImageResource(R.drawable.ic_stock_train_blue_500);
            holder.transportImage.setContentDescription(mActivity.getResources().
                    getString(R.string.content_desc_train_transport_type));
        } else {
            holder.stopAddress.setText(nearbyStopsDetails.stopAddress);
            if (nearbyStopsDetails.routeType == StopsNearbyDetails.TRAM_ROUTE_TYPE) {
                holder.transportImage.setImageResource(R.drawable.ic_stock_tram_amber_500);
                holder.transportImage.setContentDescription(mActivity.getResources().
                        getString(R.string.content_desc_tram_transport_type));
            } else {
                holder.transportImage.setImageResource(R.drawable.ic_stock_directions_bus_green_500);
                holder.transportImage.setContentDescription(mActivity.getResources().
                        getString(R.string.content_desc_bus_transport_type));
            }

        }
        holder.nearbyStopsDetails = mValues.get(position);

        return v;
    }

    public void swap(List<StopsNearbyDetails> nearbyStopsDetails){
        mValues.clear();
        mValues.addAll(nearbyStopsDetails);
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        public ImageView transportImage;
        public TextView stopName;
        public TextView stopAddress;
        public ImageView mapImageId;
        public ImageView departuresImageId;
        private StopsNearbyDetails nearbyStopsDetails;
    }
}
