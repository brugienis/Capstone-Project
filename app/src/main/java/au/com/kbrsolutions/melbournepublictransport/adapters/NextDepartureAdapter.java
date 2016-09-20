package au.com.kbrsolutions.melbournepublictransport.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.fragments.StopDetailAdapter;

/**
 * {@link RecyclerView.Adapter} that can display a {@link NextDepartureDetails}.
 */
public class NextDepartureAdapter extends RecyclerView.Adapter<NextDepartureAdapter.ViewHolder> {

    private final List<NextDepartureDetails> mValues;
//    private final OnItemFragmentInteractionListener mListener;

    private static final String TAG = StopDetailAdapter.class.getSimpleName();

//    public NextDepartureAdapter(List<NextDepartureDetails> items, OnItemFragmentInteractionListener listener) {
    public NextDepartureAdapter(List<NextDepartureDetails> items) {
        mValues = items;
//        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_next_departure_list, parent, false);
//        TextView content = (TextView) view.findViewById(R.id.content);
//        content.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.v(TAG, "onCreateViewHolder - content clicked - mListener: " + mListener);
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
////                    mListener.onItemFragmentInteractionListener(holder.mItem);
//                }
//            }
//        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
//        holder.mContentView.setText(mValues.get(position).content);
//        Log.v(TAG, "onBindViewHolder - value: " + mValues.get(position));
        NextDepartureDetails nextDepartureDetails = mValues.get(position);
//        String directionId = String.valueOf(nextDepartureDetails.directionId);
        String directionName = String.valueOf(nextDepartureDetails.directionName);
        String departureTime = String.valueOf(nextDepartureDetails.utcDepartureTime);
        String runTypeText = nextDepartureDetails.numSkipped == 0 ? "All stops" : "Express";
        holder.directionName.setText(directionName);
        holder.runType.setText(runTypeText);
        holder.departureTimeId.setText(departureTime);

//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onItemFragmentInteractionListener(holder.mItem);
//                }
//            }
//        });
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
//        public final View mView;
//        public final TextView mIdView;
//        public final TextView mContentView;
//        public NextDepartureDetails mItem;
        public final TextView directionName;
        public final TextView runType;
        public final TextView departureTimeId;

        public ViewHolder(View view) {
            super(view);
//            mView = view;
//            mIdView = (TextView) view.findViewById(R.id.id);
//            mContentView = (TextView) view.findViewById(R.id.content);
            directionName = (TextView) view.findViewById(R.id.directionName);
            runType = (TextView) view.findViewById(R.id.runType);
            departureTimeId = (TextView) view.findViewById(R.id.departureTimeId);
        }

        @Override
        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
            return super.toString() + directionName + "/" + departureTimeId;
        }
    }
}
