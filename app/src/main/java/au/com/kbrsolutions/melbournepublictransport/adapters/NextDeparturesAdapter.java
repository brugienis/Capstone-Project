package au.com.kbrsolutions.melbournepublictransport.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.StopsNearbyDetails;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;

/**
 *
 * Adapter used by NextDeparturesFragment.
 *
 */
public class NextDeparturesAdapter<T> extends ArrayAdapter<NextDepartureDetails> {

    private final List<NextDepartureDetails> mValues;
    private final Activity mActivity;

    private static final String TAG = NextDeparturesAdapterRv.class.getSimpleName();

    public NextDeparturesAdapter(Activity activity, List<NextDepartureDetails> items) {
        super(activity.getApplicationContext(), -1, items);
        mActivity = activity;
        mValues = items;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.fragment_next_departure_list, parent, false);

            holder = new ViewHolder();
            holder.transportImage = (ImageView) v.findViewById(R.id.transportImageId);
            holder.directionName = (TextView) v.findViewById(R.id.directionName);
            holder.runType = (TextView) v.findViewById(R.id.runType);
            holder.departureTimeId = (TextView) v.findViewById(R.id.departureTimeId);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        NextDepartureDetails nextDepartureDetails = mValues.get(position);
        String directionName = String.valueOf(nextDepartureDetails.directionName);
        String departureTime = String.valueOf(nextDepartureDetails.utcDepartureTime);
        String runTypeText = nextDepartureDetails.runType;

        holder.directionName.setText(directionName);
        holder.runType.setText(runTypeText);
        holder.departureTimeId.setText(departureTime);

        if (nextDepartureDetails.routeType == StopsNearbyDetails.TRAIN_ROUTE_TYPE) {
            holder.transportImage.setImageResource(R.drawable.ic_stock_train_blue_500);
            holder.transportImage.setContentDescription(mActivity.getResources().
                    getString(R.string.content_desc_train_transport_type));
        } else if (nextDepartureDetails.routeType == StopsNearbyDetails.TRAM_ROUTE_TYPE) {
            holder.transportImage.setImageResource(R.drawable.ic_stock_tram_amber_500);
            holder.transportImage.setContentDescription(mActivity.getResources().
                    getString(R.string.content_desc_tram_transport_type));
        } else if (nextDepartureDetails.routeType  == StopsNearbyDetails.BUS_ROUTE_TYPE) {
            holder.transportImage.setImageResource(R.drawable.ic_stock_directions_bus_green_500);
            holder.transportImage.setContentDescription(mActivity.getResources().
                    getString(R.string.content_desc_bus_transport_type));
        }

        return v;
    }

    public void swap(List<NextDepartureDetails> nextDepartureDetails){
        mValues.clear();
        mValues.addAll(nextDepartureDetails);
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        public ImageView transportImage;
        public TextView directionName;
        public TextView runType;
        public TextView departureTimeId;
    }
}
