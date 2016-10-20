package au.com.kbrsolutions.melbournepublictransport.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.NearbyStopsDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.fragments.StopsNearbyFragment.OnNearbyStopsFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a
 * {@link au.com.kbrsolutions.melbournepublictransport.data.NearbyStopsDetails}.
 */
public class StopsNearbyAdapter extends RecyclerView.Adapter<StopsNearbyAdapter.ViewHolder> {

    private final List<NearbyStopsDetails> mValues;
    private final OnNearbyStopsFragmentInteractionListener mListener;

    private static final String TAG = StopsNearbyAdapter.class.getSimpleName();

    public StopsNearbyAdapter(
            List<NearbyStopsDetails> items,
            OnNearbyStopsFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public StopsNearbyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_nearby_stops_list, parent, false);
        return new StopsNearbyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StopsNearbyAdapter.ViewHolder holder, int position) {
        NearbyStopsDetails nearbyStopsDetails = mValues.get(position);
//        Log.v(TAG, "onBindViewHolder - nearbyStopsDetails: " + nearbyStopsDetails);
//        if (nearbyStopsDetails.route_type.equals("train")) {
        if (nearbyStopsDetails.route_type == NearbyStopsDetails.TRAIN_ROUTE_TYPE) {
            holder.stopName.setText(nearbyStopsDetails.stopName);
            holder.stopAddress.setText(nearbyStopsDetails.suburb);
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
    }

    public void swap(List<NearbyStopsDetails> nearbyStopsDetails){
        mValues.clear();
        mValues.addAll(nearbyStopsDetails);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView transportImage;
        public final TextView stopName;
        public final TextView stopAddress;
        public final ImageView mapImageId;
        public final ImageView departuresImageId;
        private NearbyStopsDetails nearbyStopsDetails;

        public ViewHolder(View view) {
            super(view);
            transportImage = (ImageView) view.findViewById(R.id.transportImageId);
            stopName = (TextView) view.findViewById(R.id.stopName);
            stopAddress = (TextView) view.findViewById(R.id.stopAddress);
            mapImageId = (ImageView) view.findViewById(R.id.mapImageId);
            mapImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that a map image was touched.
                        mListener.onNearbyStopsFragmentMapClicked(nearbyStopsDetails);
                    }
                }
            });
            departuresImageId = (ImageView) view.findViewById(R.id.departuresImageId);
            departuresImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.v(TAG, "ViewHolder - onClick");
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that a map image was touched.
                        mListener.startNextDeparturesSearch(new StopDetails(
                                -1,
                                nearbyStopsDetails.route_type,
                                nearbyStopsDetails.stopId,
                                nearbyStopsDetails.stopName,
                                nearbyStopsDetails.latitude,
                                nearbyStopsDetails.longitude,
                                "n"
                        ));
                    }
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + stopName + "/" + stopAddress;
        }
    }
}
