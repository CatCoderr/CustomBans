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
public final class TimeUtility {

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
        return getTime(new Date(new Date().getTime() - until.getTime()));
    }

    public static String getTime(Date time) {
        return getTime(time.getTime());
    }

    public static String getTime(long ms) {
        ms = (long) Math.ceil(ms / 1000.0D);
        StringBuilder sb = new StringBuilder(40);
        if (ms / 31449600L > 0L) {
            final long years = ms / 31449600L;
            if (years > 100L) {
                return "Never";
            }
            sb.append(String.valueOf(years) + " лет");
            ms -= years * 31449600L;
        }
        if (ms / 2620800L > 0L) {
            final long months = ms / 2620800L;
            sb.append(String.valueOf(months) + " месяцев");
            ms -= months * 2620800L;
        }
        if (ms / 604800L > 0L) {
            final long weeks = ms / 604800L;
            sb.append(String.valueOf(weeks) + " недель");
            ms -= weeks * 604800L;
        }
        if (ms / 86400L > 0L) {
            final long days = ms / 86400L;
            sb.append(String.valueOf(days) + " дней");
            ms -= days * 86400L;
        }
        if (ms / 3600L > 0L) {
            final long hours = ms / 3600L;
            sb.append(String.valueOf(hours + " часов"));
            ms -= hours * 3600L;
        }
        if (ms / 60L > 0L) {
            final long minutes = ms / 60L;
            sb.append(String.valueOf(minutes) + " минут");
            ms -= minutes * 60L;
        }
        if (ms > 0L) {
            sb.append(String.valueOf(ms) + " секунд");
        }
        if (sb.length() > 1) {
            sb.replace(sb.length() - 1, sb.length(), "");
        } else {
            sb = new StringBuilder("N/A");
        }
        return sb.toString();
    }


    public static long parseTime(CommandContext context) throws CommandException {
        if (context.argsLength() < 3) throw new CommandException("Время не указано.");
        int time = context.getInteger(1);
        TimeUnit unit = getFromModifier(context.getString(2));

        if (unit == null) throw new CommandException("Неверный модификатор времени: " + context.getString(2));

        return unit.toMillis(time);
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
