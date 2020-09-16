package com.narify.awarm.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import com.narify.awarm.models.Alarm;
import com.narify.awarm.R;
import com.narify.awarm.activities.AlarmTrigger;
import com.narify.awarm.app.AppContext;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import androidx.annotation.NonNull;

import static android.content.Context.ALARM_SERVICE;

/**
 * The class is for alarm manager operations
 */
public class AlarmUtils {

    private static AlarmManager alarmManager =
            (AlarmManager) AppContext.get().getSystemService(ALARM_SERVICE);

    public static void scheduleAlarm(@NonNull Alarm alarm) {
        PendingIntent pi = createAlarmPendingIntent(alarm);

        // Get timing of the alarm since epoch in millis
        Duration nextDuration = DateTimeUtils.getNextDuration(
                alarm.getActiveDays(),
                alarm.getHour(),
                alarm.getMinute());
        long timing = DateTimeUtils.getTimeMillisAfterDuration(nextDuration);

        // Schedule the alarm in the device
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timing, pi);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timing, pi);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, timing, pi);

    }

    public static void cancelAlarm(Alarm alarm) {
        PendingIntent pi = createAlarmPendingIntent(alarm);
        alarmManager.cancel(pi);
        pi.cancel();
    }

    private static PendingIntent createAlarmPendingIntent(Alarm alarm) {
        Intent alarmTriggerIntent = new Intent(AppContext.get(), AlarmTrigger.class);
        alarmTriggerIntent.putExtra(
                AppContext.get().getString(R.string.key_pi_alarm_extra),
                TypeConverterUtils.alarmToJson(alarm));

        return PendingIntent.getActivity(AppContext.get(), alarm.getUniqueCode(),
                alarmTriggerIntent, 0);
    }

    public static int createUniqueAlarmCode() {
        int code = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        List<Alarm> alarmList = PreferenceUtils.getStoredAlarmList();

        return isCodeExists(code, alarmList) ? createUniqueAlarmCode() : code;
    }

    private static boolean isCodeExists(int alarmCode, List<Alarm> alarmList) {
        for (Alarm temp : alarmList)
            if (temp.getUniqueCode() == alarmCode)
                return true;

        return false;
    }


}
