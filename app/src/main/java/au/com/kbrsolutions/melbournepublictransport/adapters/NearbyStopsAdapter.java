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
import au.com.kbrsolutions.melbournepublictransport.fragments.NearbyStopsFragment.OnNearbyStopsFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a
 * {@link au.com.kbrsolutions.melbournepublictransport.data.NearbyStopsDetails}.
 */
public class NearbyStopsAdapter extends RecyclerView.Adapter<NearbyStopsAdapter.ViewHolder> {

    private final List<NearbyStopsDetails> mValues;
    private final OnNearbyStopsFragmentInteractionListener mListener;

    private static final String TAG = NearbyStopsAdapter.class.getSimpleName();

    public NearbyStopsAdapter(List<NearbyStopsDetails> items, OnNearbyStopsFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public NearbyStopsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_nearby_stops_list, parent, false);
        return new NearbyStopsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NearbyStopsAdapter.ViewHolder holder, int position) {
        NearbyStopsDetails nearbyStopsDetails = mValues.get(position);
//        Log.v(TAG, "onBindViewHolder - nearbyStopsDetails: " + nearbyStopsDetails);
        if (nearbyStopsDetails.transportType.equals("train")) {
            holder.stopName.setText(nearbyStopsDetails.stopName);
            holder.stopAddress.setText(nearbyStopsDetails.suburb);

        } else {
            holder.stopName.setText(nearbyStopsDetails.stopName);
            holder.stopAddress.setText(nearbyStopsDetails.stopAddress);

        }
        holder.mearbyStopsDetails = mValues.get(position);
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
        public final TextView stopName;
        public final TextView stopAddress;
        public final ImageView mapImageId;
        private NearbyStopsDetails mearbyStopsDetails;

        public ViewHolder(View view) {
            super(view);
            stopName = (TextView) view.findViewById(R.id.stopName);
            stopAddress = (TextView) view.findViewById(R.id.stopAddress);
            mapImageId = (ImageView) view.findViewById(R.id.mapImageId);
            mapImageId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that a map image was touched.
                        mListener.onNearbyStopsFragmentMapClicked(mearbyStopsDetails);
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
