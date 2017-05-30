package me.catcoder.custombans.utility;

import com.sk89q.CommandContext;
import com.sk89q.CommandException;
import me.catcoder.custombans.actor.Actor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date parsing utilities.
 *
 * @author CatCoder
 */
public class TimeUtility {

    private TimeUtility() {
    }

    public static final long MINUTES_IN_SECONDS = 60;
    public static final long HOURS_IN_SECONDS = MINUTES_IN_SECONDS * 60;
    public static final long DAYS_IN_SECONDS = HOURS_IN_SECONDS * 24;
    public static final long WEEKS_IN_SECONDS = DAYS_IN_SECONDS * 7;
    public static final long MONTHS_IN_SECONDS = DAYS_IN_SECONDS * 28;
    public static final long YEARS_IN_SECONDS = DAYS_IN_SECONDS * 356;

    public static String toDMYDateString(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy_HH:mm").format(date);
    }

    public static String toYMDDateString(Date date) {
        return new SimpleDateFormat("yyyy/MM/dd_HH:mm").format(date);
    }

    public static String toMDYDateString(Date date) {
        return new SimpleDateFormat("MM/dd/yyyy_HH:mm").format(date);
    }

    public static Date fromString(String dateFormat) {
        Date end = new Date();
        Matcher m = Pattern.compile("([0-9])+?[A-Za-z]").matcher("1s" + dateFormat);
        while (m.find()) {
            end = combine(end, fromShortString(m.group()));
        }
        return end;
    }

    public static Date fromShortString(String dateFormat) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(0));
        try {
            char timeChar = dateFormat.charAt(dateFormat.length() - 1);
            if (timeChar == 'w') {
                c.add(getCalendarFlag('d'), Integer.valueOf(dateFormat.substring(0, dateFormat.length() - 1)) * 7);
            } else {
                c.add(getCalendarFlag(timeChar), Integer.valueOf(dateFormat.substring(0, dateFormat.length() - 1)));
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return c.getTime();
    }

    public static int getCalendarFlag(char flag) {
        int ret = -1;
        switch (Character.toLowerCase(flag)) {
            case 's':
                ret = Calendar.SECOND;
                break;
            case 'm':
                ret = Calendar.MINUTE;
                break;
            case 'h':
            case 'u':
            default:
                ret = Calendar.HOUR;
                break;
            case 'd':
                ret = Calendar.DAY_OF_MONTH;
                break;
            case 'j':
            case 'y':
                ret = Calendar.YEAR;
                break;
        }
        return ret;
    }


    public static Date combine(Date first, Date toAdd) {
        return new Date(first.getTime() + toAdd.getTime());
    }

    public static String timeUnitNow(Date until) {
        return getTime(new Date(new Date().getTime() - until.getTime()), false);
    }

    public static String getTime(Date time, boolean dateIsPassed) {
        String ret = "";
        if (time.equals(new Date(0))) {
            return "forever";
        }
        if (time.equals(new Date())) {
            return "now";
        }
        long secondsBetween = (long) Math.ceil(((time.getTime()) / 1000));
        if (secondsBetween < 0) {
            secondsBetween *= -1L;
            if (!dateIsPassed) {
                ret += "-";
            }
        } else if (dateIsPassed) {
            ret += "-";
        }
        double minus = 0;
        if (secondsBetween >= YEARS_IN_SECONDS) {
            minus = Math.floor(secondsBetween / YEARS_IN_SECONDS);
            secondsBetween -= (long) minus * YEARS_IN_SECONDS;
            ret += (int) minus + " years, ";
        }
        if (secondsBetween >= MONTHS_IN_SECONDS) {
            minus = Math.floor(secondsBetween / MONTHS_IN_SECONDS);
            secondsBetween -= (long) minus * MONTHS_IN_SECONDS;
            ret += (int) minus + " months, ";
        }
        if (secondsBetween >= WEEKS_IN_SECONDS) {
            minus = Math.floor(secondsBetween / WEEKS_IN_SECONDS);
            secondsBetween -= (long) minus * WEEKS_IN_SECONDS;
            ret += (int) minus + " weeks, ";
        }
        if (secondsBetween >= DAYS_IN_SECONDS) {
            minus = Math.floor(secondsBetween / DAYS_IN_SECONDS);
            secondsBetween -= (long) minus * DAYS_IN_SECONDS;
            ret += (int) minus + " days, ";
        }
        if (secondsBetween >= HOURS_IN_SECONDS) {
            minus = Math.floor(secondsBetween / HOURS_IN_SECONDS);
            secondsBetween -= (long) minus * HOURS_IN_SECONDS;
            ret += (int) minus + " hours, ";
        }
        if (secondsBetween >= MINUTES_IN_SECONDS) {
            minus = Math.floor(secondsBetween / MINUTES_IN_SECONDS);
            secondsBetween -= (long) minus * MINUTES_IN_SECONDS;
            ret += (int) minus + " minutes, ";
        }
        if (ret.endsWith(", ")) {
            ret = ret.substring(0, ret.length() - 2);
        } else if (ret.length() == 0) {
            ret = "Less than a minute";
        }
        return ret;
    }

    public static long parseTime(CommandContext context) throws CommandException {
        if (context.argsLength() < 3) throw new CommandException("Time is not specified.");
        int time = context.getInteger(1);
        TimeUnit unit = getFromModifier(context.getString(2));

        if (unit == null) throw new CommandException("Unknown time modifier: " + context.getString(2));

        return System.currentTimeMillis() + unit.toMillis(time);
    }

    private static TimeUnit getFromModifier(String modifier) {
        switch (modifier.toLowerCase()) {
            case "hour":
                return TimeUnit.HOURS;
            case "day":
                return TimeUnit.DAYS;
            case "min":
                return TimeUnit.MINUTES;
            case "sec":
                return TimeUnit.SECONDS;
        }
        return null;
    }

    public static String timeUntilNow(long epoch) {
        return timeUnitNow(new Date(epoch));
    }
}
