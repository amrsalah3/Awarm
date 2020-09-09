package com.narify.awarm.utilities;

import com.narify.awarm.R;
import com.narify.awarm.app.AppContext;
import com.narify.awarm.app.AppResources;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class DateTimeUtils {

    public static long getTimeMillisAfterDuration(Duration duration) {
        ZonedDateTime dateTime = ZonedDateTime.now();

        return dateTime.toInstant().toEpochMilli() + duration.toMillis();
    }

    public static String formatTime(int hour, int minute) {
        LocalTime time = LocalTime.of(hour, minute);
        return time.format(DateTimeFormatter.ofPattern("hh:mm"));
    }

    public static String getAmPm(int hour, int minute) {
        LocalTime time = LocalTime.of(hour, minute);
        return time.format(DateTimeFormatter.ofPattern("a"));
    }

    /**
     * Finds the next alarm duration (minimum time left) of all alarm days
     *
     * @param weekDays boolean alarm state of week days. Index must start with 1 (Monday) to 7 (Sunday)
     * @param hour     for the alarm to trigger
     * @param minute   for the alarm to trigger
     * @return next alarm duration left
     */
    public static Duration getNextDuration(boolean[] weekDays, int hour, int minute) {
        if (weekDays.length != 8) throw new IllegalArgumentException(); // Zero index is ignored
        // Initialize LocalDateTime object with today's date and time
        LocalDateTime todayDateTime = LocalDateTime.now();
        // Initialize alarm LocalDateTime object with today and at the alarm clock
        LocalDateTime alarmDateTime = todayDateTime.with(LocalTime.of(hour, minute));
        // Get the order of today in the week
        int todayNum = todayDateTime.getDayOfWeek().getValue();

        Duration minDuration;

        // First, if today's checkbox is checked:
        // If the passed clock is in the future of TODAY (minDuration is not negative)
        // return this minDuration
        if (weekDays[todayNum]) {
            minDuration = Duration.between(todayDateTime, alarmDateTime);
            if (!minDuration.isNegative()) return minDuration;
        }

        // Otherwise alarm clock is in the past of today, then loop throw all days to find
        // the minimum duration coming (nearest alarm)
        minDuration = null;
        for (int i = DayOfWeek.MONDAY.getValue(); i <= DayOfWeek.SUNDAY.getValue(); i++) {
            if (!weekDays[i]) continue; // If day's checkbox is not checked, skip it
            LocalDateTime tempDateTime = alarmDateTime.with(TemporalAdjusters.next(DayOfWeek.of(i)));
            Duration tempDuration = Duration.between(todayDateTime, tempDateTime);
            if (minDuration == null || minDuration.compareTo(tempDuration) > 0)
                minDuration = tempDuration;

        }

        return minDuration;
    }

    public static String getRemainingTimeText(Duration duration) {
        if (duration == null)
            return AppContext.get().getString(R.string.remaining_no_time_picked);

        int days = (int) duration.toDays();
        int hours = (int) duration.minus(Duration.ofDays(days)).toHours();
        int minutes = (int) duration.minus(Duration.ofDays(days)).minus(Duration.ofHours(hours)).toMinutes();
        String remainingDays = AppResources.get().getQuantityString(R.plurals.remaining_days, days, days);
        String remainingHours = AppResources.get().getQuantityString(R.plurals.remaining_hours, hours, hours);
        String remainingMinutes = AppResources.get().getQuantityString(R.plurals.remaining_minutes, minutes, minutes);

        return String.format(AppContext.get().getString(R.string.remaining_day_hour_minute),
                remainingDays, remainingHours, remainingMinutes);
    }
}