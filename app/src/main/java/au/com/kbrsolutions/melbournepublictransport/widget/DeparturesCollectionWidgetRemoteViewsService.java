package au.com.kbrsolutions.melbournepublictransport.widget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.NearbyStopsDetails;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.remote.RemoteMptEndpointUtil;

/**
 * Created by business on 15/10/2016.
 */

public class DeparturesCollectionWidgetRemoteViewsService extends RemoteViewsService {
    {
        Log.v(TAG, "instance initializer - start");
    }

    private final static String TAG = DeparturesCollectionWidgetRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            List<NextDepartureDetails> mNextDepartureDetails = new ArrayList<>();

            @Override
            public void onCreate() {
                // Nothing to do
            }

            /**
             *
             * Retrieves the most recent results for default day from DB. Data is sorted in
             * ascending order of 'time' and 'home' columns.
             *
             * This method is called by the app hosting the widget (e.g., the launcher)
             * However, our ContentProvider is not exported so it doesn't have access to the
             * data. Therefore we need to clear (and finally restore) the calling identity so
             * that calls use our process and permission.
             *
             * The data from DB is sorted by time and hone columns in ascending order.
             *
             */
//            private String stopId = "1035";
            private int limit = 5;
            @Override
            public void onDataSetChanged() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String stopId = sp.getString(getString(R.string.pref_key_widget_stop_id), "-1");
                Log.v(TAG, "onDataSetChanged - start - stopId: " + stopId);
//                if (mCursor != null) {
//                    mCursor.close();
//                }
                final long identityToken = Binder.clearCallingIdentity();

//                mNextDepartureDetails.add(new NextDepartureDetails(1, 1, "a", 1, 1, 1, "11:10"));
//                mNextDepartureDetails.add(new NextDepartureDetails(2, 2, "a", 2, 2, 2, "12:10"));
//                mNextDepartureDetails.add(new NextDepartureDetails(3, 3, "a", 3, 3, 3, "13:10"));
//                mNextDepartureDetails.add(new NextDepartureDetails(4, 4, "a", 4, 4, 4, "14:10"));

                mNextDepartureDetails =
                        RemoteMptEndpointUtil.getBroadNextDepartures(
                                NearbyStopsDetails.TRAIN_ROUTE_TYPE,
                                stopId,
                                limit, getResources());
                Log.v(TAG, "onDataSetChanged - after getBroadNextDepartures - mNextDepartureDetails.size: " + mNextDepartureDetails.size());
//                String currDate = MainActivity.getDefaultPageDayInMillis();
//                mCursor = getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
//                        SCORE_COLUMNS,
//                        null,
//                        new String[] {currDate}, DatabaseContract.scores_table.TIME_COL +
//                                ASC +
//                                " ," +
//                                DatabaseContract.scores_table.HOME_COL +
//                                ASC);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                Log.v(TAG, "onDestroy - start");
//                if (mCursor != null) {
//                    mCursor.close();
//                    mCursor = null;
//                }
            }

            @Override
            public int getCount() {
                Log.v(TAG, "onCreate - getCount - count: " + mNextDepartureDetails.size());
                return mNextDepartureDetails.size();
            }


            /**
         *
         * @param   position - index of the ListView row that has to be prepared for display
         * @return  RemoteViews
         */
        @Override
        public RemoteViews getViewAt(int position) {
//            Log.v(TAG, "getViewAt - position: " + position);
            if (position == AdapterView.INVALID_POSITION ||
                    mNextDepartureDetails == null || mNextDepartureDetails.size() - 1 < position) {
                Log.v(TAG, "getViewAt - INVALID_POSITION");
                return null;
            }

            RemoteViews views = new RemoteViews(getPackageName(),
                    R.layout.widget_departures_collection_list);
            views.setTextViewText(R.id.runType, String.valueOf(mNextDepartureDetails.get(position).routeType));
            views.setTextViewText(R.id.directionName, mNextDepartureDetails.get(position).directionName);
            views.setTextViewText(R.id.runType, String.valueOf(mNextDepartureDetails.get(position).runType));
            views.setTextViewText(R.id.departureTimeId, String.valueOf(mNextDepartureDetails.get(position).utcDepartureTime));
//            Log.v(TAG, "getViewAt - populated podition: " + position);

            final Intent fillInIntent = new Intent();

            // FIXME: 15/10/2016 - handle below
//            fillInIntent.putExtra(MainActivity.WIDGET_SELECTED_MATCH_ID, mCursor.getDouble(MATCH_ID_IDX));
//            fillInIntent.putExtra(MainActivity.WIDGET_SELECTED_ROW_IDX, position);
//            views.setOnClickFillInIntent(R.id.widget_scores_collection_list_item, fillInIntent);
            views.setOnClickFillInIntent(R.id.widgetDeparturesCollectionList, fillInIntent);
//            Log.v(TAG, "getViewAt - views: " + views.getLayoutId());
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            Log.v(TAG, "getLoadingView - start");
            return new RemoteViews(getPackageName(), R.layout.widget_departures_collection_list);
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
//            Log.v(TAG, "getItemId - start");
//            if (mCursor.moveToPosition(position))
//                return mCursor.getLong(ID_IDX);
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    };
}

}

