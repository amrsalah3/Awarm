package com.narify.awarm.activities;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import com.narify.awarm.R;
import com.narify.awarm.utilities.PreferenceUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

public class BaseActivity extends AppCompatActivity {

    private GradientDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Global variable because there is a bug that drawable color can't be set to list items
        drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.corners);

        String theme = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.key_pref_app_theme), getString(R.string.Purple));

        boolean isAlarmDetailsWithSpinner = this instanceof AlarmDetails && PreferenceUtils.isSpinner();

        if (theme != null)
            setActivityTheme(theme, isAlarmDetailsWithSpinner);
    }

    private void setActivityTheme(String theme, boolean isAlarmDetailsWithSpinner) {
        switch (theme) {
            default:
                changeCornerDrawableColor(R.color.colorPrimaryPurple);
                if (isAlarmDetailsWithSpinner) setTheme(R.style.PurpleThemeSpinnerTimePicker);
                else setTheme(R.style.AppTheme);
                break;
            case "Blue":
                changeCornerDrawableColor(R.color.colorPrimaryBlue);
                if (isAlarmDetailsWithSpinner) setTheme(R.style.BlueThemeSpinnerTimePicker);
                else setTheme(R.style.AppBlueTheme);
                break;
            case "Orange":
                changeCornerDrawableColor(R.color.colorPrimaryOrange);
                if (isAlarmDetailsWithSpinner) setTheme(R.style.OrangeThemeSpinnerTimePicker);
                else setTheme(R.style.AppOrangeTheme);
                break;
            case "Black":
                changeCornerDrawableColor(R.color.colorPrimaryBlack);
                if (isAlarmDetailsWithSpinner) setTheme(R.style.BlackThemeSpinnerTimePicker);
                else setTheme(R.style.AppBlackTheme);
                break;
        }
    }

    private void changeCornerDrawableColor(int colorResId) {
        if (drawable != null)
            drawable.setColor(ContextCompat.getColor(this, colorResId));
    }


}
