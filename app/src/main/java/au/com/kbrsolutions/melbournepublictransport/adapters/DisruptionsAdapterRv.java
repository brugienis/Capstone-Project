package au.com.kbrsolutions.melbournepublictransport.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.DisruptionsDetails;

/**
 * {@link RecyclerView.Adapter} that can display a 
 * {@link au.com.kbrsolutions.melbournepublictransport.data.DisruptionsDetails}.
 */
public class DisruptionsAdapterRv extends RecyclerView.Adapter<DisruptionsAdapterRv.ViewHolder> {

    private final List<DisruptionsDetails> mValues;

    private static final String TAG = DisruptionsAdapterRv.class.getSimpleName();

    public DisruptionsAdapterRv(List<DisruptionsDetails> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_disruptions_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DisruptionsAdapterRv.ViewHolder holder, int position) {
        DisruptionsDetails disruptionsDetails = mValues.get(position);
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
            title = (TextView) view.findViewById(R.id.disruptionsTitle);
            description = (TextView) view.findViewById(R.id.disruptionsDescription);
        }

        @Override
        public String toString() {
            return super.toString() + title + "/" + description;
        }
    }
}
