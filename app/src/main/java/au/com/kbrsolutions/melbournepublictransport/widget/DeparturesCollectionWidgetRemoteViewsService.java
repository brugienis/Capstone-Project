package au.com.kbrsolutions.melbournepublictransport.widget;

import android.content.Intent;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;

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
//            private Cursor mCursor = null;
            List<NextDepartureDetails> nextDepartureDetailses = new ArrayList<>();
            {
                Log.v(TAG, "RemoteViewsFactory onGetViewFactory - instance initializer - start");
            }

            @Override
            public void onCreate() {
                Log.v(TAG, "onCreate - start");
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
            @Override
            public void onDataSetChanged() {
                Log.v(TAG, "onDataSetChanged - start");
//                if (mCursor != null) {
//                    mCursor.close();
//                }
                final long identityToken = Binder.clearCallingIdentity();

                nextDepartureDetailses.add(new NextDepartureDetails(1, 1, "a", 1, 1, 1, "11:10"));
                nextDepartureDetailses.add(new NextDepartureDetails(2, 2, "a", 2, 2, 2, "12:10"));
                nextDepartureDetailses.add(new NextDepartureDetails(3, 3, "a", 3, 3, 3, "13:10"));
                nextDepartureDetailses.add(new NextDepartureDetails(4, 4, "a", 4, 4, 4, "14:10"));
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
                Log.v(TAG, "onCreate - getCount - count: " + nextDepartureDetailses.size());
                return nextDepartureDetailses.size();
            }


            /**
         *
         * @param   position - index of the ListView row that has to be prepared for display
         * @return  RemoteViews
         */
        @Override
        public RemoteViews getViewAt(int position) {
            Log.v(TAG, "getViewAt - position: " + position);
            if (position == AdapterView.INVALID_POSITION ||
                    nextDepartureDetailses == null || nextDepartureDetailses.size()  -1 < position) {
                return null;
            }

            String directionName = nextDepartureDetailses.get(position).directionName;
            int runType = nextDepartureDetailses.get(position).routeType;
            String departureTimeId = nextDepartureDetailses.get(position).utcDepartureTime;

            RemoteViews views = new RemoteViews(getPackageName(),
                    R.layout.widget_departures_collection_list);

            views.setTextViewText(R.id.runType, String.valueOf(nextDepartureDetailses.get(position).routeType));

            final Intent fillInIntent = new Intent();

            // FIXME: 15/10/2016 - handle below
//            fillInIntent.putExtra(MainActivity.WIDGET_SELECTED_MATCH_ID, mCursor.getDouble(MATCH_ID_IDX));
//            fillInIntent.putExtra(MainActivity.WIDGET_SELECTED_ROW_IDX, position);
//            views.setOnClickFillInIntent(R.id.widget_scores_collection_list_item, fillInIntent);
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
            Log.v(TAG, "getItemId - start");
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

