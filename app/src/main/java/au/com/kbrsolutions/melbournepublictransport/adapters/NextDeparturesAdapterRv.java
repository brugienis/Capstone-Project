package au.com.kbrsolutions.melbournepublictransport.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopsNearbyDetails;

/**
 *
 * This adapter is currently replaced with ArrayAdapter. It maybe used again in the future.
 *
 * {@link RecyclerView.Adapter} that can display a {@link NextDepartureDetails}.
 */
public class NextDeparturesAdapterRv
        extends RecyclerView.Adapter<NextDeparturesAdapterRv.ViewHolder>
        implements ViewTreeObserver.OnGlobalFocusChangeListener {

    private final List<NextDepartureDetails> mValues;

    @SuppressWarnings("unused")
    private static final String TAG = NextDeparturesAdapterRv.class.getSimpleName();

    @Override
    public void onGlobalFocusChanged(View view, View view1) {

    }

    public NextDeparturesAdapterRv(List<NextDepartureDetails> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_next_departure_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        NextDepartureDetails nextDepartureDetails = mValues.get(position);
        String directionName = String.valueOf(nextDepartureDetails.directionName);
        String departureTime = String.valueOf(nextDepartureDetails.utcDepartureTime);
        String runTypeText = nextDepartureDetails.runType;
        holder.directionName.setText(directionName);
        holder.runType.setText(runTypeText);
        holder.departureTimeId.setText(departureTime);

        if (nextDepartureDetails.routeType == StopsNearbyDetails.TRAM_ROUTE_TYPE) {
            holder.transportImage.setImageResource(R.drawable.ic_stock_tram_amber_500);
        } else if (nextDepartureDetails.routeType  == StopsNearbyDetails.BUS_ROUTE_TYPE) {
            holder.transportImage.setImageResource(R.drawable.ic_stock_directions_bus_green_500);
        }
    }

    @SuppressWarnings("unused")
    public void swap(List<NextDepartureDetails> nextDepartureDetailsList){
        mValues.clear();
        mValues.addAll(nextDepartureDetailsList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView transportImage;
        public final TextView directionName;
        public final TextView runType;
        public final TextView departureTimeId;

        public ViewHolder(View view) {
            super(view);
            transportImage = (ImageView) view.findViewById(R.id.transportImageId);
            directionName = (TextView) view.findViewById(R.id.directionName);
            runType = (TextView) view.findViewById(R.id.runType);
            departureTimeId = (TextView) view.findViewById(R.id.departureTimeId);
        }

        @Override
        public String toString() {
            return super.toString() + directionName + "/" + departureTimeId;
        }
    }
}
