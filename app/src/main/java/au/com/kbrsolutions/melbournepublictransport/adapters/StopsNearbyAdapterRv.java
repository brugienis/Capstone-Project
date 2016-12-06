package au.com.kbrsolutions.melbournepublictransport.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.LatLngDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopsNearbyDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopDetails;
import au.com.kbrsolutions.melbournepublictransport.fragments.StopsNearbyFragment.OnNearbyStopsFragmentInteractionListener;

/**
 *
 * This adapter is currently replaced with ArrayAdapter. It maybe used again in the future.
 *
 * {@link RecyclerView.Adapter} that can display a
 * {@link StopsNearbyDetails}.
 */
public class StopsNearbyAdapterRv extends RecyclerView.Adapter<StopsNearbyAdapterRv.ViewHolder> {

    private final List<StopsNearbyDetails> mValues;
    private final OnNearbyStopsFragmentInteractionListener mListener;

    @SuppressWarnings("unused")
    private static final String TAG = StopsNearbyAdapterRv.class.getSimpleName();

    public StopsNearbyAdapterRv(
            List<StopsNearbyDetails> items,
            OnNearbyStopsFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public StopsNearbyAdapterRv.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_stops_nearby_list, parent, false);
        return new StopsNearbyAdapterRv.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StopsNearbyAdapterRv.ViewHolder holder, int position) {
        StopsNearbyDetails nearbyStopsDetails = mValues.get(position);
        if (nearbyStopsDetails.routeType == StopsNearbyDetails.TRAIN_ROUTE_TYPE) {
            holder.stopName.setText(nearbyStopsDetails.stopName);
            holder.stopAddress.setText(nearbyStopsDetails.suburb);
            holder.transportImage.setImageResource(R.drawable.ic_stock_train_blue_500);
        } else {
            holder.stopName.setText(nearbyStopsDetails.stopName);
            holder.stopAddress.setText(nearbyStopsDetails.stopAddress);
            if (nearbyStopsDetails.routeType == StopsNearbyDetails.TRAM_ROUTE_TYPE) {
                holder.transportImage.setImageResource(R.drawable.ic_stock_tram_amber_500);
            } else {
                holder.transportImage.setImageResource(R.drawable.ic_stock_directions_bus_green_500);
            }

        }
        holder.nearbyStopsDetails = mValues.get(position);
    }

    @SuppressWarnings("unused")
    public void swap(List<StopsNearbyDetails> nearbyStopsDetails){
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
        private StopsNearbyDetails nearbyStopsDetails;

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
                            mListener.showStopOnMap(
                                nearbyStopsDetails.stopName,
                                new LatLngDetails(nearbyStopsDetails.latitude,
                                        nearbyStopsDetails.longitude));
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
                        // fragment is attached to one) that departure image was touched.
                        mListener.startNextDeparturesSearch(new StopDetails(
                                -1,
                                nearbyStopsDetails.routeType,
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
