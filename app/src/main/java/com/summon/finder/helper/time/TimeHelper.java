package com.summon.finder.helper.time;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeHelper {
    public static Date getDateNowTime() {
        Date date = Calendar.getInstance().getTime();
        return date;
    }

    public static String getStringNowTime() {
        Date date = Calendar.getInstance().getTime();
        return formatDateToString(date);
    }

    @NonNull
    public static String formatDateToString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
        return formatter.format(date);
    }

    public static String findDifference(String start_date, String end_date) {
        try {
            TimeDifference timeDifference = getTimeDifference(start_date, end_date);


            if (timeDifference.year != 0) {
                return timeDifference.year + " năm";
            }

            if (timeDifference.day != 0) {
                return timeDifference.day + " ngày";
            }

            if (timeDifference.hours != 0) {
                return timeDifference.hours + " giờ";
            }

            if (timeDifference.minutes != 0) {
                return timeDifference.minutes + " phút";
            }

            if (timeDifference.seconds >= 0) {
                return timeDifference.seconds + " giây";
            }
        }

        // Catch the Exception
        catch (ParseException e) {
            e.printStackTrace();
        }

        return "???";
    }

    @NonNull
    public static TimeDifference getTimeDifference(String start_date, String end_date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");

        Date d1 = sdf.parse(start_date);
        Date d2 = sdf.parse(end_date);

        long difference_In_Time
                = d2.getTime() - d1.getTime();

        long difference_In_Seconds
                = (difference_In_Time
                / 1000)
                % 60;

        long difference_In_Minutes
                = (difference_In_Time
                / (1000 * 60))
                % 60;

        long difference_In_Hours
                = (difference_In_Time
                / (1000 * 60 * 60))
                % 24;

        long difference_In_Years
                = (difference_In_Time
                / (1000l * 60 * 60 * 24 * 365));

        long difference_In_Days
                = (difference_In_Time
                / (1000 * 60 * 60 * 24))
                % 365;

        return new TimeDifference(difference_In_Years, difference_In_Days, difference_In_Hours, difference_In_Minutes, difference_In_Seconds);
    }

    public static long getMiliSeconds(TimeDifference timeDifference) {
        long SECONDS = 60;
        long HOURS = 60 * SECONDS;
        long DAY = 24 * HOURS;
        long YEAR = 365 * DAY;
        return timeDifference.year * YEAR
                + timeDifference.day * DAY
                + timeDifference.hours * HOURS
                + timeDifference.seconds;
    }

    public static class TimeDifference {
        long year, day, hours, minutes, seconds;

        public TimeDifference(long year, long day, long hours, long minutes, long seconds) {
            this.year = year;
            this.day = day;
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
        }

        public long getYear() {
            return year;
        }

        public long getDay() {
            return day;
        }

        public long getHours() {
            return hours;
        }

        public long getMinutes() {
            return minutes;
        }

        public long getSeconds() {
            return seconds;
        }

        public long getMiliSeconds() {
            return TimeHelper.getMiliSeconds(this);
        }
    }
}
