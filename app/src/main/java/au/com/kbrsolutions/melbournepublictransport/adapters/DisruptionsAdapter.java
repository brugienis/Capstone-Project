package au.com.kbrsolutions.melbournepublictransport.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.DisruptionsDetails;

import static au.com.kbrsolutions.melbournepublictransport.R.id.directionName;
import static au.com.kbrsolutions.melbournepublictransport.R.id.runType;

/**
 * {@link RecyclerView.Adapter} that can display a 
 * {@link au.com.kbrsolutions.melbournepublictransport.data.DisruptionsDetails}.
 */
public class DisruptionsAdapter extends RecyclerView.Adapter<DisruptionsAdapter.ViewHolder> {

    private final List<DisruptionsDetails> mValues;

    private static final String TAG = DisruptionsAdapter.class.getSimpleName();

    public DisruptionsAdapter(List<DisruptionsDetails> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_disruptions_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DisruptionsAdapter.ViewHolder holder, int position) {
        DisruptionsDetails disruptionsDetails = mValues.get(position);
        String title = String.valueOf(disruptionsDetails.title);
        String description = String.valueOf(disruptionsDetails.description);
        holder.title.setText(disruptionsDetails.title);
        holder.description.setText(disruptionsDetails.description);
    }

    public void swap(List<DisruptionsDetails> disruptionsDetailsList){
        mValues.clear();
        mValues.addAll(disruptionsDetailsList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final TextView description;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(directionName);
            description = (TextView) view.findViewById(runType);
        }

        @Override
        public String toString() {
            return super.toString() + title + "/" + description;
        }
    }
}
