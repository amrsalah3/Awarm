package com.narify.awarm.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.narify.awarm.R;
import com.narify.awarm.databinding.ActivityAlarmDetailsBinding;
import com.narify.awarm.models.Alarm;
import com.narify.awarm.utilities.AlarmUtils;
import com.narify.awarm.utilities.DateTimeUtils;
import com.narify.awarm.utilities.FileUtils;
import com.narify.awarm.utilities.PreferenceUtils;
import com.narify.awarm.utilities.TypeConverterUtils;

import java.time.DayOfWeek;
import java.time.Duration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import static com.narify.awarm.activities.MainScreen.EDIT_ALARM_DETAILS_CODE;
import static com.narify.awarm.activities.MainScreen.NEW_ALARM_DETAILS_CODE;


public class AlarmDetails extends BaseActivity implements TimePicker.OnTimeChangedListener {

    private final static int INTENT_PICK_RINGTONE_CODE = 1;
    private static int mIncomingRequestCode;
    private ActivityAlarmDetailsBinding mBinding;
    private int mAlarmUniqueCode;
    private int mAlarmRepeatId;
    private String mRingtonePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAlarmDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mIncomingRequestCode = getIntent().getIntExtra(
                getString(R.string.key_intent_request_code),
                NEW_ALARM_DETAILS_CODE);
        String clickedAlarm = getIntent().getStringExtra(getString(R.string.key_intent_alarm_extra));

        if (mIncomingRequestCode == EDIT_ALARM_DETAILS_CODE && clickedAlarm != null)
            fillViewsWithSavedAlarm(TypeConverterUtils.jsonToAlarm(clickedAlarm));

        mBinding.timePicker.setOnTimeChangedListener(this);
    }


    private void fillViewsWithSavedAlarm(Alarm alarm) {
        // If a new alarm created do nothing
        if (alarm == null) return;

        boolean[] days = alarm.getDays();
        mBinding.cbMon.setChecked(days[DayOfWeek.MONDAY.getValue()]);
        mBinding.cbTue.setChecked(days[DayOfWeek.TUESDAY.getValue()]);
        mBinding.cbWed.setChecked(days[DayOfWeek.WEDNESDAY.getValue()]);
        mBinding.cbThu.setChecked(days[DayOfWeek.THURSDAY.getValue()]);
        mBinding.cbFri.setChecked(days[DayOfWeek.FRIDAY.getValue()]);
        mBinding.cbSat.setChecked(days[DayOfWeek.SATURDAY.getValue()]);
        mBinding.cbSun.setChecked(days[DayOfWeek.SUNDAY.getValue()]);
        mBinding.timePicker.setCurrentHour(alarm.getHour());
        mBinding.timePicker.setCurrentMinute(alarm.getMinute());
        mBinding.etAlarmLabel.setText(alarm.getLabel());

        Duration duration = DateTimeUtils.getNextDuration(getDaysFromCheckBoxes(), alarm.getHour(), alarm.getMinute());
        mBinding.tvRemainingTime.setText(DateTimeUtils.getRemainingTimeText(duration));

        mAlarmUniqueCode = alarm.getUniqueCode();
        mAlarmRepeatId = alarm.getRepeat();
        mRingtonePath = alarm.getRingtonePath();

        mBinding.repeatButton.setText(getResources().getStringArray(R.array.repeat_options)[mAlarmRepeatId]);
        mBinding.tvRingtone.setText(getRingtoneName());
    }


    private String getRingtoneName() {
        String ringtoneName;
        if (mRingtonePath != null) {
            Uri ringtoneUri = FileUtils.getPersistableUriFromPath(mRingtonePath);
            String uriName = FileUtils.getFileName(ringtoneUri);
            ringtoneName = uriName != null ? uriName : getString(R.string.ringtone_default);
        } else ringtoneName = getString(R.string.ringtone_default);

        return ringtoneName;
    }

    /**
     * TimePicker call this method responding to any change in time picked
     */
    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        onAlarmTimingChanged(timePicker);
    }

    /**
     * Day checkboxes call this method responding to onclick
     */
    public void onAlarmTimingChanged(View view) {
        int hour = mBinding.timePicker.getCurrentHour();
        int minute = mBinding.timePicker.getCurrentMinute();
        boolean[] days = getDaysFromCheckBoxes();

        Duration duration = DateTimeUtils.getNextDuration(days, hour, minute);

        mBinding.tvRemainingTime.setText(DateTimeUtils.getRemainingTimeText(duration));
    }


    private boolean[] getDaysFromCheckBoxes() {
        boolean[] weekDay = new boolean[8];
        // weekDay[0] is ignored as java's DayOfWeek constants value starts with 1 throw 7
        weekDay[DayOfWeek.MONDAY.getValue()] = mBinding.cbMon.isChecked();
        weekDay[DayOfWeek.TUESDAY.getValue()] = mBinding.cbTue.isChecked();
        weekDay[DayOfWeek.WEDNESDAY.getValue()] = mBinding.cbWed.isChecked();
        weekDay[DayOfWeek.THURSDAY.getValue()] = mBinding.cbThu.isChecked();
        weekDay[DayOfWeek.FRIDAY.getValue()] = mBinding.cbFri.isChecked();
        weekDay[DayOfWeek.SATURDAY.getValue()] = mBinding.cbSat.isChecked();
        weekDay[DayOfWeek.SUNDAY.getValue()] = mBinding.cbSun.isChecked();

        return weekDay;
    }


    public void selectRepeat(View view) {
        // Use custom theme for the dialog to make the title's color black instead of
        // white on top of a white dialog; cuz of this activity theme's primary text color
        new AlertDialog.Builder(this, R.style.AlarmRepeatDialog)
                .setTitle(R.string.repeat)
                .setItems(R.array.repeat_options, (dialog, which) -> {
                    mBinding.repeatButton.setText(getResources().getStringArray(R.array.repeat_options)[which]);
                    mAlarmRepeatId = which;
                })
                .show();
    }


    public void PickRingtone(View view) {
        Intent intent = new Intent();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("audio/*");
        // Extra to prevent selecting remote (undownloaded) files
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        startActivityForResult(intent, INTENT_PICK_RINGTONE_CODE);
    }


    private Alarm createCurrentAlarmObject(int requestCode) {
        Alarm alarm = new Alarm();

        if (requestCode == NEW_ALARM_DETAILS_CODE)
            alarm.setUniqueCode(AlarmUtils.createUniqueAlarmCode());
        else
            alarm.setUniqueCode(mAlarmUniqueCode);

        alarm.setActivated(true);
        alarm.setDays(getDaysFromCheckBoxes());
        alarm.setHour(mBinding.timePicker.getCurrentHour());
        alarm.setMinute(mBinding.timePicker.getCurrentMinute());
        alarm.setRepeat(mAlarmRepeatId);
        alarm.setRingtonePath(mRingtonePath);
        alarm.setLabel(mBinding.etAlarmLabel.getText().toString());

        return alarm;
    }


    /**
     * Checks if user selected at least one day for the alarm or not
     */
    private boolean isAnyDayChecked() {
        boolean isAnyDayChecked = false;
        for (boolean isChecked : getDaysFromCheckBoxes())
            if (isChecked) {
                isAnyDayChecked = true;
                break;
            }

        return isAnyDayChecked;
    }


    public void saveAlarm(View view) {
        if (!isAnyDayChecked()) {
            Toast.makeText(this, R.string.select_atleast_one_day, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intentBackToMain = new Intent();
        Alarm currentAlarm = createCurrentAlarmObject(mIncomingRequestCode);

        if (mIncomingRequestCode == EDIT_ALARM_DETAILS_CODE) {
            AlarmUtils.cancelAlarm(currentAlarm);
            PreferenceUtils.updateStoredAlarm(currentAlarm);
        } else
            PreferenceUtils.storeNewAlarm(currentAlarm);

        AlarmUtils.scheduleAlarm(currentAlarm);

        setResult(RESULT_OK, intentBackToMain);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            if (requestCode == INTENT_PICK_RINGTONE_CODE) {
                if (intent != null && intent.getData() != null) {
                    Uri uri = intent.getData();
                    if (FileUtils.isAudioUri(uri)) {
                        mRingtonePath = uri.toString();
                        mBinding.tvRingtone.setText(FileUtils.getFileName(uri));
                    } else
                        Toast.makeText(this, R.string.not_valid_file, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(getString(R.string.key_requestcode_savestate_alarmdetails), mIncomingRequestCode);
        outState.putInt(getString(R.string.key_uniquecode_savestate_alarmdetails), mAlarmUniqueCode);
        outState.putInt(getString(R.string.key_repeatid_savestate_alarmdetails), mAlarmRepeatId);
        outState.putString(getString(R.string.key_ringtonepath_savestate_alarmdetails), mRingtonePath);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mIncomingRequestCode = savedInstanceState.getInt(getString(R.string.key_requestcode_savestate_alarmdetails));
        mAlarmUniqueCode = savedInstanceState.getInt(getString(R.string.key_uniquecode_savestate_alarmdetails));
        mAlarmRepeatId = savedInstanceState.getInt(getString(R.string.key_repeatid_savestate_alarmdetails));
        mRingtonePath = savedInstanceState.getString(getString(R.string.key_ringtonepath_savestate_alarmdetails));

        mBinding.repeatButton.setText(getResources().getStringArray(R.array.repeat_options)[mAlarmRepeatId]);
        mBinding.tvRingtone.setText(getRingtoneName());
    }
}



