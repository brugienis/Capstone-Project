package au.com.kbrsolutions.melbournepublictransport.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.DisruptionsDetails;

/**
 * Adapter used by DisruptionsFragment.
 *
 * @param <T>
 */
public class DisruptionsAdapter<T> extends ArrayAdapter<DisruptionsDetails> {

    private final List<DisruptionsDetails> mValues;

    @SuppressWarnings("unused")
    private static final String TAG = DisruptionsAdapterRv.class.getSimpleName();

    public DisruptionsAdapter(Activity activity, List<DisruptionsDetails> items) {
        super(activity.getApplicationContext(), -1, items);
        mValues = items;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        final ViewHolder holder;
        if (v == null) {
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.fragment_disruptions_list, parent, false);

            holder = new ViewHolder();
            holder.title = (TextView) v.findViewById(R.id.disruptionsTitle);
            holder.description = (TextView) v.findViewById(R.id.disruptionsDescription);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        DisruptionsDetails disruptionsDetails = mValues.get(position);
        holder.title.setText(disruptionsDetails.title);
        holder.title.setContentDescription(disruptionsDetails.title);
        holder.description.setText(disruptionsDetails.description);

        return v;
    }

    public void swap(List<DisruptionsDetails> disruptionsDetails){
        mValues.clear();
        mValues.addAll(disruptionsDetails);
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        public TextView title;
        public TextView description;
    }
}
