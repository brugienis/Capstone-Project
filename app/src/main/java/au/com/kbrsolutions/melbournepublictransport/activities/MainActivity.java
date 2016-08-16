package au.com.kbrsolutions.melbournepublictransport.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import au.com.kbrsolutions.melbournepublictransport.R;
import au.com.kbrsolutions.melbournepublictransport.fragments.FavoriteStopsFragment;
import au.com.kbrsolutions.melbournepublictransport.fragments.StationOnMapFragment;

// git push -u origin

public class MainActivity extends AppCompatActivity
        implements FavoriteStopsFragment.OnFragmentInteractionListener {

    private FavoriteStopsFragment mFavoriteStopsFragment;
    private StationOnMapFragment mStationOnMapFragment;
    private final String FAVORITE_STOPS = "favorite_stops";
    private static final String STATION_ON_MAP = "station_on_map";
    static final String TAG_ERROR_DIALOG_FRAGMENT="errorDialog";

    private final String TAG = ((Object) this).getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFavoriteStopsFragment =
                (FavoriteStopsFragment) getSupportFragmentManager().findFragmentByTag(FAVORITE_STOPS);


        if (mFavoriteStopsFragment == null) {
            mFavoriteStopsFragment = new FavoriteStopsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.left_dynamic_fragments_frame, mFavoriteStopsFragment, FAVORITE_STOPS)
                    .commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onFragmentInteractionListener(Uri uri) {
        Log.v(TAG, "onFragmentInteractionListener called");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_stops_nearby) {
//            startActivity(new Intent(this, StopsNearbyActivity.class));
            return true;
        } else if (id == R.id.action_disruptions) {
//            startActivity(new Intent(this, DisruptionsActivity.class));
            return true;
        } else if (id == R.id.action_station_on_map) {
            if (readyToGo()) {
                if (mStationOnMapFragment == null) {
                    mStationOnMapFragment = new StationOnMapFragment();
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.left_dynamic_fragments_frame, mStationOnMapFragment, STATION_ON_MAP)
                        .commit();
            }
            return true;
        } else if (id == R.id.action_settings) {
//            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ErrorDialogFragment extends DialogFragment {
        static final String ARG_ERROR_CODE="errorCode";

        static ErrorDialogFragment newInstance(int errorCode) {
            Bundle args=new Bundle();
            ErrorDialogFragment result=new ErrorDialogFragment();

            args.putInt(ARG_ERROR_CODE, errorCode);
            result.setArguments(args);

            return(result);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle args=getArguments();
            GoogleApiAvailability checker=
                    GoogleApiAvailability.getInstance();

            return(checker.getErrorDialog(getActivity(),
                    args.getInt(ARG_ERROR_CODE), 0));
        }

        @Override
        public void onDismiss(DialogInterface dlg) {
            if (getActivity()!=null) {
                getActivity().finish();
            }
        }
    }

    protected boolean readyToGo() {
        GoogleApiAvailability checker=
                GoogleApiAvailability.getInstance();

        int status=checker.isGooglePlayServicesAvailable(this);

        if (status == ConnectionResult.SUCCESS) {
            if (getVersionFromPackageManager(this)>=2) {
                return(true);
            }
            else {
                Toast.makeText(this, R.string.no_maps, Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else if (checker.isUserResolvableError(status)) {
            FragmentManager fm = getSupportFragmentManager();
            ErrorDialogFragment.newInstance(status)
                    .show(fm, TAG_ERROR_DIALOG_FRAGMENT);
        }
        else {
            Toast.makeText(this, R.string.no_maps, Toast.LENGTH_LONG).show();
            finish();
        }

        return(false);
    }

    private static int getVersionFromPackageManager(Context context) {
        PackageManager packageManager=context.getPackageManager();
        FeatureInfo[] featureInfos=
                packageManager.getSystemAvailableFeatures();
        if (featureInfos != null && featureInfos.length > 0) {
            for (FeatureInfo featureInfo : featureInfos) {
                // Null feature name means this feature is the open
                // gl es version feature.
                if (featureInfo.name == null) {
                    if (featureInfo.reqGlEsVersion != FeatureInfo.GL_ES_VERSION_UNDEFINED) {
                        return getMajorVersion(featureInfo.reqGlEsVersion);
                    }
                    else {
                        return 1; // Lack of property means OpenGL ES
                        // version 1
                    }
                }
            }
        }
        return 1;
    }

    /** @see FeatureInfo#getGlEsVersion() */
    private static int getMajorVersion(int glEsVersion) {
        return((glEsVersion & 0xffff0000) >> 16);
    }

}