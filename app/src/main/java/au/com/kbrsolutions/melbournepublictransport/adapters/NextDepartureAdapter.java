package au.com.kbrsolutions.melbournepublictransport.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;

// FIXME: 2/10/2016 - how to show scrollbar?
/**
 * {@link RecyclerView.Adapter} that can display a {@link NextDepartureDetails}.
 */
public class NextDepartureAdapter extends RecyclerView.Adapter<NextDepartureAdapter.ViewHolder> {

    private final List<NextDepartureDetails> mValues;
    // FIXME: 20/09/2016 - move strings to values
    private static final String ALL_STOPS = "All stops";
    private static final String EXPRESS = "Express";

    private static final String TAG = NextDepartureAdapter.class.getSimpleName();

    public NextDepartureAdapter(List<NextDepartureDetails> items) {
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
        // FIXME: 20/09/2016 - move strings to values
        String runTypeText = nextDepartureDetails.numSkipped == 0 ? ALL_STOPS : EXPRESS;
        holder.directionName.setText(directionName);
        holder.runType.setText(runTypeText);
        holder.departureTimeId.setText(departureTime);
    }

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
        public final TextView directionName;
        public final TextView runType;
        public final TextView departureTimeId;

        public ViewHolder(View view) {
            super(view);
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
