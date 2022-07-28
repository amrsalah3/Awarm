package com.narify.awarm.activities;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.narify.awarm.R;
import com.narify.awarm.databinding.ActivityAlarmTriggerBinding;
import com.narify.awarm.gestures.OnSwipeTouchListener;
import com.narify.awarm.models.Alarm;
import com.narify.awarm.utilities.AlarmUtils;
import com.narify.awarm.utilities.DateTimeUtils;
import com.narify.awarm.utilities.FileUtils;
import com.narify.awarm.utilities.PreferenceUtils;
import com.narify.awarm.utilities.TypeConverterUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;


public class AlarmTrigger extends BaseActivity {

    ActivityAlarmTriggerBinding mBinding;
    Alarm mAlarm;
    MediaPlayer mMediaPlayer;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAlarmTriggerBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        turnAndKeepScreenOn();

        if (getIntent() != null) {
            mAlarm = TypeConverterUtils.jsonToAlarm(getIntent().getStringExtra(getString(R.string.key_pi_alarm_extra)));
            scheduleNextAlarm();
            setClockAndLabelText();
            playRingtone();
        }

        mBinding.rootAlarmTrigger.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeTop() {
                dismissAlarm();
            }
        });

    }

    private void turnAndKeepScreenOn() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    private void scheduleNextAlarm() {
        LocalDate date = LocalDate.now();

        // Get active days of the current alarm and set current day to false (as already triggered)
        boolean[] activeDays = mAlarm.getActiveDays();
        activeDays[date.getDayOfWeek().getValue()] = false;

        // Change the received current alarm object after this modification
        mAlarm.setActiveDays(activeDays);

        // If there is still any active day left with current alarm, schedule the next one
        // and return from this function.
        for (boolean isActive : mAlarm.getActiveDays())
            if (isActive) {
                AlarmUtils.scheduleAlarm(mAlarm);
                return;
            }

        // ------------------------------------------------------------------------------------
        // If no current active days left, reset current alarm active days
        // to the original selected by the user, then check if there is any repeat coming
        mAlarm.setActiveDays(mAlarm.getDays());

        // If active repeat is not a Weekly repeat (weekly repeat id = 4), subtract 1 week from it
        // as 1 week of alarm has already triggered
        if (mAlarm.getActiveRepeat() != 4)
            mAlarm.setActiveRepeat(mAlarm.getActiveRepeat() - 1);

        // If there is still remaining repeat weeks (1 week left id = 0), schedule it
        // If not, reset repeats, deactivate current alarm object, and store the alarm object
        // so that later the user sees the alarm is deactivated in Main activity.
        if (mAlarm.getActiveRepeat() >= 0) {
            AlarmUtils.scheduleAlarm(mAlarm);
        } else {
            mAlarm.setActiveRepeat(mAlarm.getRepeat());
            mAlarm.setActivated(false);
            PreferenceUtils.updateStoredAlarm(mAlarm);
        }

    }

    private void setClockAndLabelText() {
        String label = mAlarm.getLabel() != null ? mAlarm.getLabel() : "";
        LocalTime now = LocalTime.now();
        String formattedTime = DateTimeUtils.formatTime(now.getHour(), now.getMinute());

        mBinding.rvAlarmClock.setText(formattedTime);
        mBinding.tvAlarmLabel.setText(label);
    }

    private void playRingtone() {
        Uri ringtoneUri;
        try {
            ringtoneUri = FileUtils.getPersistableUriFromPath(mAlarm.getRingtonePath());
            setUpMediaPlayer(ringtoneUri);
        } catch (Exception e) {
            ringtoneUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.overthehorizon);
            try {
                setUpMediaPlayer(ringtoneUri);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setUpMediaPlayer(Uri ringtoneUri) throws IOException {
        mMediaPlayer = new MediaPlayer();

        mMediaPlayer.setDataSource(this, ringtoneUri);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        else
            mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM).build());

        mMediaPlayer.setLooping(true);
        mMediaPlayer.prepare();
        mMediaPlayer.start();
    }

    public void dismissAlarm() {
        try {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) finishAndRemoveTask();
            else finish();
        }
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

}
