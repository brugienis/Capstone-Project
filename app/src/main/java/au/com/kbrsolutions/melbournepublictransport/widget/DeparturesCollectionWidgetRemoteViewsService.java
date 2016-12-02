package au.com.kbrsolutions.melbournepublictransport.widget;

import android.content.Intent;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.data.NextDepartureDetails;
import au.com.kbrsolutions.melbournepublictransport.data.StopsNearbyDetails;
import au.com.kbrsolutions.melbournepublictransport.remote.RemoteMptEndpointUtil;
import au.com.kbrsolutions.melbournepublictransport.utilities.SharedPreferencesUtility;

/**
 *
 * Departures Collection WidgetRemote Views Service.
 *
 * Builds the widget details view.
 *
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
             * Retrieves the most recent departures details from PTV web site. Data is sorted in
             * ascending order of departure time.
             *
             * This method is called by the app hosting the widget (e.g., the launcher)
             *
             */

            @Override
            public void onDataSetChanged() {
//                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String stopId = SharedPreferencesUtility.getWidgetStopId(getApplication());

                final long identityToken = Binder.clearCallingIdentity();

                int nextDeparturesLimitParam = 5;
                mNextDepartureDetails =
                        RemoteMptEndpointUtil.getBroadNextDepartures(
                                StopsNearbyDetails.TRAIN_ROUTE_TYPE,
                                stopId,
                                nextDeparturesLimitParam,
                                getApplicationContext());

                Binder.restoreCallingIdentity(identityToken);
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
//            Log.v(TAG, "getViewAt - position: " + position);
                if (position == AdapterView.INVALID_POSITION ||
                        mNextDepartureDetails == null || mNextDepartureDetails.size() - 1 < position) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_departures_collection_list);
                views.setTextViewText(R.id.runType, String.valueOf(mNextDepartureDetails.get(position).routeType));
                views.setTextViewText(R.id.directionName, mNextDepartureDetails.get(position).directionName);
                views.setTextViewText(R.id.runType, String.valueOf(mNextDepartureDetails.get(position).runType));
                views.setTextViewText(R.id.departureTimeId, String.valueOf(mNextDepartureDetails.get(position).utcDepartureTime));

                final Intent fillInIntent = new Intent();

                views.setOnClickFillInIntent(R.id.widgetDeparturesCollectionList, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_departures_collection_list);
            }

            /**
             * Returns just one view type
             *
             * @return
             */
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

            @Override
            public void onDestroy() {

            }
        };
    }

}