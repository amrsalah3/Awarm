package com.narify.awarm.utilities;

import com.narify.awarm.models.Alarm;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TypeConverterUtils {

    private static Gson gson = new Gson();

    public static String alarmListToJson(List<Alarm> alarmList) {
        if (alarmList == null) return alarmListToJson(new ArrayList<>());
        return gson.toJson(alarmList);
    }

    public static List<Alarm> jsonToAlarmList(String json) throws JsonParseException {
        Type listType = new TypeToken<List<Alarm>>() {}.getType();

        return gson.fromJson(json, listType);
    }

    public static String alarmToJson(Alarm alarm) {
        if (alarm == null) return null;
        return gson.toJson(alarm);
    }

    public static Alarm jsonToAlarm(String json) throws JsonParseException {
        Type objType = new TypeToken<Alarm>() {}.getType();

        return gson.fromJson(json, objType);
    }
}
