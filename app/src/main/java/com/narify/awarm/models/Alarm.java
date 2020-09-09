package com.narify.awarm.models;

import java.util.Arrays;

public class Alarm {
    private int uniqueCode;
    private boolean isActivated;
    private boolean[] days;
    private boolean[] activeDays;
    private int hour, minute;
    private int repeat;
    private int activeRepeat;
    private String ringtonePath;
    private String label;

    public int getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(int uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public boolean[] getDays() {
        return days;
    }

    public void setDays(boolean[] days) {
        this.days = days;
        setActiveDays(days);
    }

    public boolean[] getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(boolean[] activeDays) {
        this.activeDays = activeDays;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
        setActiveRepeat(repeat);
    }

    public int getActiveRepeat() {
        return activeRepeat;
    }

    public void setActiveRepeat(int activeRepeat) {
        this.activeRepeat = activeRepeat;
    }

    public String getRingtonePath() {
        return ringtonePath;
    }

    public void setRingtonePath(String ringtonePath) {
        this.ringtonePath = ringtonePath;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "uniqueCode=" + uniqueCode +
                ", isActivated=" + isActivated +
                ", days=" + Arrays.toString(days) +
                ", activeDays=" + Arrays.toString(activeDays) +
                ", hour=" + hour +
                ", minute=" + minute +
                ", repeat=" + repeat +
                ", activeRepeat=" + activeRepeat +
                ", ringtonePath='" + ringtonePath + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
