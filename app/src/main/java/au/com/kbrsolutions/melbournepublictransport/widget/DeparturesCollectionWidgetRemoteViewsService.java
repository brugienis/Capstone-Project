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
            @Override
            public void onDataSetChanged() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String stopId = sp.getString(getString(R.string.widget_stop_id), "-1");
                Log.v(TAG, "onDataSetChanged - start - stopId: " + stopId);
                int limit = 5;

                final long identityToken = Binder.clearCallingIdentity();

                mNextDepartureDetails =
                        RemoteMptEndpointUtil.getBroadNextDepartures(
                                NearbyStopsDetails.TRAIN_ROUTE_TYPE,
                                stopId,
                                limit, getResources());
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
            }

            @Override
            public int getCount() {
                return mNextDepartureDetails.size();
            }


            /**
             *
             * @param   position - index of the ListView row that has to be prepared for display
             * @return  RemoteViews
             */
            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        mNextDepartureDetails == null || mNextDepartureDetails.size() - 1 < position) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_departures_collection_list);
                views.setTextViewText(R.id.runType,
                        String.valueOf(mNextDepartureDetails.get(position).routeType));
                views.setTextViewText(R.id.directionName,
                        mNextDepartureDetails.get(position).directionName);
                views.setTextViewText(R.id.runType,
                        String.valueOf(mNextDepartureDetails.get(position).runType));
                views.setTextViewText(R.id.departureTimeId,
                        String.valueOf(mNextDepartureDetails.get(position).utcDepartureTime));

                final Intent fillInIntent = new Intent();
                views.setOnClickFillInIntent(R.id.widgetDeparturesCollectionList, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_departures_collection_list);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

}

