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
import au.com.kbrsolutions.melbournepublictransport.data.NearbyStopsDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.fragments.StopsNearbyFragment;

import static au.com.kbrsolutions.melbournepublictransport.R.id.departuresImageId;
import static au.com.kbrsolutions.melbournepublictransport.R.id.mapImageId;
import static au.com.kbrsolutions.melbournepublictransport.R.id.stopAddress;
import static au.com.kbrsolutions.melbournepublictransport.R.id.stopName;

/**
 * Created by business on 14/11/2016.
 */

public class StopsNearbyAdapter<T> extends ArrayAdapter<NearbyStopsDetails> {

    private final List<NearbyStopsDetails> mValues;
    private final StopsNearbyFragment.OnNearbyStopsFragmentInteractionListener mListener;

    private static final String TAG = StopsNearbyAdapterRv.class.getSimpleName();

    public StopsNearbyAdapter(Activity activity, List<NearbyStopsDetails> items, StopsNearbyFragment.OnNearbyStopsFragmentInteractionListener listener) {
        super(activity.getApplicationContext(), -1, items);
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
            v = inflater.inflate(R.layout.fragment_nearby_stops_list, parent, false);

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
                        mListener.onNearbyStopsFragmentMapClicked(holder.nearbyStopsDetails);
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
                        // fragment is attached to one) that a map image was touched.
                        mListener.startNextDeparturesSearch(new StopDetails(
                                -1,
                                holder.nearbyStopsDetails.route_type,
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

        NearbyStopsDetails nearbyStopsDetails = mValues.get(position);
        if (nearbyStopsDetails.route_type == NearbyStopsDetails.TRAIN_ROUTE_TYPE) {
            holder.stopName.setText(nearbyStopsDetails.stopName);
            holder.stopAddress.setText(nearbyStopsDetails.suburb);
            holder.transportImage.setImageResource(R.drawable.ic_stock_train_blue_500_48dp);
        } else {
            holder.stopName.setText(nearbyStopsDetails.stopName);
            holder.stopAddress.setText(nearbyStopsDetails.stopAddress);
            if (nearbyStopsDetails.route_type == NearbyStopsDetails.TRAM_ROUTE_TYPE) {
                holder.transportImage.setImageResource(R.drawable.ic_stock_tram_amber_500_48dp);
            } else {
                holder.transportImage.setImageResource(R.drawable.ic_stock_directions_bus_green_500_48dp);
            }

        }
        holder.nearbyStopsDetails = mValues.get(position);

        return v;
    }

    public void swap(List<NearbyStopsDetails> nearbyStopsDetails){
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
        private NearbyStopsDetails nearbyStopsDetails;
    }
}
