package com.narify.awarm.utilities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.narify.awarm.models.Alarm;
import com.narify.awarm.R;
import com.narify.awarm.app.App;
import com.narify.awarm.app.AppContext;
import com.narify.awarm.app.AppResources;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * The class is used for shared preference storage file operations for persisting data
 */
public class PreferenceUtils {

    private final static SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(App.getInstance());
    private final static String alarmListPrefKey =
            AppContext.get().getString(R.string.key_pref_alarm_list);

    @NonNull
    public static List<Alarm> getStoredAlarmList() {
        List<Alarm> alarmList = null;
        try {
            String jsonList = prefs.getString(alarmListPrefKey, null);
            alarmList = TypeConverterUtils.jsonToAlarmList(jsonList);
        } finally {
            if (alarmList == null) alarmList = new ArrayList<>();
        }

        return alarmList;
    }

    public static void storeNewAlarm(Alarm alarm) {
        if (alarm == null) return;
        ArrayList<Alarm> alarmList = new ArrayList<>(getStoredAlarmList());
        alarmList.add(0, alarm);

        prefs.edit()
                .putString(alarmListPrefKey, TypeConverterUtils.alarmListToJson(alarmList))
                .apply();
    }


    public static void updateStoredAlarm(Alarm alarm) {
        List<Alarm> alarmList = PreferenceUtils.getStoredAlarmList();

        for (int i = 0; i < alarmList.size(); i++) {
            if (alarmList.get(i).getUniqueCode() == alarm.getUniqueCode())
                alarmList.set(i, alarm);
        }

        prefs.edit()
                .putString(alarmListPrefKey, TypeConverterUtils.alarmListToJson(alarmList))
                .apply();
    }

    public static void removeStoredAlarm(Alarm alarm) {
        List<Alarm> alarmList = PreferenceUtils.getStoredAlarmList();

        for (int i = 0; i < alarmList.size(); i++) {
            if (alarmList.get(i).getUniqueCode() == alarm.getUniqueCode()) {
                alarmList.remove(i);
                break;
            }
        }

        prefs.edit()
                .putString(alarmListPrefKey, TypeConverterUtils.alarmListToJson(alarmList))
                .apply();
    }


    public static boolean isSpinner() {
        String spinner = AppResources.get().getStringArray(R.array.pref_time_picker_mode_choices)[0];

        return spinner.equals(
                prefs.getString(AppContext.get().getString(R.string.key_pref_time_picker_mode), spinner)
        );
    }

    public static void clearAllData() {
        prefs.edit().clear().commit();
    }

}
