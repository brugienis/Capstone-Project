package au.com.kbrsolutions.melbournepublictransport.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.NearbyStopsDetails;
/**
 * {@link RecyclerView.Adapter} that can display a
 * {@link au.com.kbrsolutions.melbournepublictransport.data.NearbyStopsDetails}.
 */
public class NearbyStopsAdapter extends RecyclerView.Adapter<NearbyStopsAdapter.ViewHolder> {

    private final List<NearbyStopsDetails> mValues;

    private static final String TAG = NearbyStopsAdapter.class.getSimpleName();

    public NearbyStopsAdapter(List<NearbyStopsDetails> items) {
        mValues = items;
    }

    @Override
    public NearbyStopsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_nearby_stops_list, parent, false);
        return new NearbyStopsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NearbyStopsAdapter.ViewHolder holder, int position) {
        NearbyStopsDetails disruptionsDetails = mValues.get(position);
        holder.stopName.setText(disruptionsDetails.stopName);
        holder.stopAddress.setText(disruptionsDetails.stopAddress);
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

        public ViewHolder(View view) {
            super(view);
            stopName = (TextView) view.findViewById(R.id.stopName);
            stopAddress = (TextView) view.findViewById(R.id.stopAddress);
        }

        @Override
        public String toString() {
            return super.toString() + stopName + "/" + stopAddress;
        }
    }
}
