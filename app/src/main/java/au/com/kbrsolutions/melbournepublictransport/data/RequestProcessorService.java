package au.com.kbrsolutions.melbournepublictransport.data;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import au.com.kbrsolutions.melbournepublictransport.events.MainActivityEvents;


public class RequestProcessorService extends IntentService {

    private EventBus eventBus;

    private static final String TAG = RequestProcessorService.class.getSimpleName();

    public RequestProcessorService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (eventBus == null) {
            eventBus = EventBus.getDefault();
            eventBus.register(this);
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.v(TAG, "ni is null: " + ni);

            sendMessageToSpotifyStreamerActivity(new MainActivityEvents.Builder(MainActivityEvents.MainEvents.NETWORK_STATUS)
                    .setMsg("NO NETWORK CONNECTION")
                    .build());
//            sendStickyBroadcast(
//                    new Intent(BROADCAST_ACTION_STATE_CHANGE).
//                            putExtra(EXTRA_NETWORK_PROBLEM,
//                                    R.string.broadcast_no_network_connection));
            return;
        } else {
            Log.v(TAG, "ni not null: " + ni);
            sendMessageToSpotifyStreamerActivity(new MainActivityEvents.Builder(MainActivityEvents.MainEvents.NETWORK_STATUS)
                    .setMsg("NETWORK CONNECTION OK")
                    .build());
        }
    }

    private void sendMessageToSpotifyStreamerActivity(MainActivityEvents event) {
        EventBus.getDefault().post(event);
//        if (isRegisterForPlayNowEvents) {
//            eventBus.post(event);
//        }
    }
    // This method will be called when a MainActivityEvents is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(MainActivityEvents event) {
        MainActivityEvents.MainEvents requestEvent = event.event;
        switch (requestEvent) {

            case NETWORK_STATUS:
//                showSnackBar(event.msg, true);
                break;

            default:
                throw new RuntimeException("LOC_CAT_TAG - onEvent - no code to handle requestEvent: " + requestEvent);
//        Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();
        }
    }
}
