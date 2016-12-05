package au.com.kbrsolutions.melbournepublictransport.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.activities.MainActivity;
import au.com.kbrsolutions.melbournepublictransport.activities.SettingsActivity;
import au.com.kbrsolutions.melbournepublictransport.utilities.SharedPreferencesUtility;

/**
 * Widget Provider - will allow to show selected widget stop's departure details.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DeparturesCollectionWidgetProvider extends AppWidgetProvider {

    private final static String TAG = DeparturesCollectionWidgetProvider.class.getSimpleName();

    /**
     *
     * Initiate Departures Collection widget update.
     *
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        boolean fromBook = false;
//        if (fromBook) {
//            onUpdateFromBook(context, appWidgetManager, appWidgetIds);
//            return;
//        }
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            String stopName = SharedPreferencesUtility.getWidgetStopName(context);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_departures_collection);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
            views.setTextViewText(R.id.widgetSelectedStopName, stopName);

            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }
            Intent clickIntentTemplate = new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     *
     * Initiate Next Departures Collection widget update after receiving broadcast
     * WIDGET_STOP_UPDATED (sent from SettingsActivity).
     *
     */
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (SettingsActivity.WIDGET_STOP_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    /**
     *
     * Remove this method before publishing app on the Google Play
     *
     * Initiate Departures Collection widget update.
     *
     */
//        @Override
    public void onUpdateFromBook(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<appWidgetIds.length; i++) {
            Intent svcIntent=new Intent(context, DeparturesCollectionWidgetRemoteViewsService.class);

            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget=new RemoteViews(context.getPackageName(),
                    R.layout.widget_departures_collection);

            widget.setTextViewText(R.id.widgetSelectedStopName, "Carrum");
            widget.setRemoteAdapter(R.id.widget_list, svcIntent);

            Intent clickIntent=new Intent(context, MainActivity.class);
            PendingIntent clickPI=PendingIntent
                    .getActivity(context, 0,
                            clickIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            widget.setPendingIntentTemplate(R.id.widget_list, clickPI);

            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, DeparturesCollectionWidgetRemoteViewsService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, DeparturesCollectionWidgetRemoteViewsService.class));
    }
}