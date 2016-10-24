package au.com.kbrsolutions.melbournepublictransport.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import au.com.kbrsolutions.melbournepublictransport.R;

/**
 * Created by business on 24/10/2016.
 */

public class WidgetStopEditTextPreference extends EditTextPreference {

    static final private int DEFAULT_MINIMUM_WIDGET_STOP_LENGTH = 2;
    private int mMinLength;

    public WidgetStopEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.WidgetStopEditTextPreference,
                0, 0);
        try {
            mMinLength = a.getInteger(R.styleable.WidgetStopEditTextPreference_minLgth, DEFAULT_MINIMUM_WIDGET_STOP_LENGTH);
        } finally {
            a.recycle();
        }

        // Check to see if Google Play services is available. The Place Picker API is available
        // through Google Play services, so if this is false, we'll just carry on as though this
        // feature does not exist. If it is true, however, we can add a widget to our preference.
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
        if (resultCode == ConnectionResult.SUCCESS) {
            // Add the get current location widget to our location preference
            setWidgetLayoutResource(R.layout.pref_current_widget_stop);
        }
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        View currentWidgetStop = view.findViewById(R.id.current_widget_stop);
        currentWidgetStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
//                Activity settingsActivity = (SettingsActivity) context;
//                    settingsActivity.startActivityForResult(
//                            new Intent(settingsActivity, WidgetStopsActivity.class),
//                            SettingsActivity.WIDGET_STOP_REQUEST);
                Activity settingsActivity = (SettingsActivity) context;
                settingsActivity.startActivityForResult(
                            new Intent(settingsActivity, WidgetStopsActivity.class),
                            SettingsActivity.WIDGET_STOP_REQUEST);
            }
        });

        return view;
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        EditText et = getEditText();
        et.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Dialog d = getDialog();
                if (d instanceof AlertDialog) {
                    AlertDialog dialog = (AlertDialog) d;
                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    // Check if the EditText is empty
                    if (s.length() < mMinLength) {
                        // Disable OK button
                        positiveButton.setEnabled(false);
                    } else {
                        // Re-enable the button.
                        positiveButton.setEnabled(true);
                    }
                }
            }
        });
    }
}

