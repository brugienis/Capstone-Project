package au.com.kbrsolutions.melbournepublictransport.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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

        setWidgetLayoutResource(R.layout.pref_current_widget_stop);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        View currentWidgetStop = view.findViewById(R.id.current_widget_stop);
        currentWidgetStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                Activity settingsActivity = (SettingsActivity) context;
//                settingsActivity.startActivityForResult(
//                            new Intent(settingsActivity, WidgetStopsActivity.class),
//                            SettingsActivity.WIDGET_STOP_REQUEST);

                Intent intent = new Intent(settingsActivity, WidgetStopsActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    settingsActivity.startActivityForResult(intent,
                            SettingsActivity.WIDGET_STOP_REQUEST,
                            ActivityOptions.makeSceneTransitionAnimation(settingsActivity).toBundle());
                } else {
                    settingsActivity.startActivityForResult(
                            intent,
                            SettingsActivity.WIDGET_STOP_REQUEST);
                }
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

