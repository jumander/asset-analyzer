package financial;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.*;

/**
 * Created by johannes on 17/05/05.
 */
public class Time implements Comparable, Serializable {
    /*private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;*/
    private long epoch;


    public Time(int year, int month, int day, int hour, int minute, int second) {
        /*if(year < 0)
            throw new IllegalArgumentException("Invalid value for year");
        if(month < 1 || month > 12)
            throw new IllegalArgumentException("Invalid value for month");
        if(day < 0)*/
        Calendar c = Calendar.getInstance();
        c.set(year, month-1, day, hour, minute, second);
        epoch = c.getTimeInMillis()/1000;
        /*this.year = c.get(YEAR);
        this.month = c.get(MONTH)+1;
        this.day = c.get(DAY_OF_MONTH);
        this.hour = c.get(HOUR_OF_DAY);
        this.minute = c.get(MINUTE);
        this.second = c.get(SECOND);*/
    }

    public Time(int year, int month, int day, int hour, int minute) {
        this(year, month, day, hour, minute, 0);
    }

    public Time(int year, int month, int day) {
        this(year, month, day, 0, 0);
    }

    /**
     * Constructs a time object with a string of format <year-month-day hour:minute:second>
     */
    public Time(String time) {
        Calendar c = Calendar.getInstance();
        c.set(SECOND, 0);
        c.set(MINUTE, 0);
        c.set(HOUR_OF_DAY, 0);
        c.set(DAY_OF_MONTH, 1);
        c.set(MONTH, 0);
        c.set(YEAR, 0);

        StringBuilder string = new StringBuilder(time);


        c.set(YEAR, extractNumber(string, "-"));
        c.set(MONTH, extractNumber(string, "-") - 1);
        c.set(DAY_OF_MONTH, extractNumber(string, " "));
        if(string.length() == 0) {
            epoch = c.getTimeInMillis()/1000;
            return;
        }
        c.set(HOUR_OF_DAY, extractNumber(string, ":"));
        c.set(MINUTE, extractNumber(string, ":"));
        if(string.length() == 0) {
            epoch = c.getTimeInMillis()/1000;
            return;
        }
        c.set(SECOND, extractNumber(string, ""));

        epoch = c.getTimeInMillis()/1000;
    }

    /**
     * Constructs a time object given the number of seconds since epoch.
     * @param timeSinceEpoch
     */
    public Time(long timeSinceEpoch) {
        epoch = timeSinceEpoch;
    }

    public Time(Calendar cal)
    {
        epoch = cal.getTimeInMillis()/1000;
    }

    private int extractNumber(StringBuilder s, String end)
    {
        int endIndex;

        endIndex = s.indexOf(end);

        if(endIndex <= 0)
            endIndex = s.length();

        int number = Integer.parseInt(s.substring(0, endIndex));


        s.delete(0, endIndex+1);

        return number;
    }

    public int getYear() { return toCalendar().get(YEAR); }

    public int getMonth(){ return toCalendar().get(MONTH) + 1; }

    public int getDay() { return toCalendar().get(DAY_OF_MONTH); }

    public int getHour() { return toCalendar().get(HOUR_OF_DAY); }

    public int getMinute() { return toCalendar().get(MINUTE); }

    public int getSecond() { return toCalendar().get(SECOND); }

    public int toInt()
    {
        return (int)epoch;
    }

    public Calendar toCalendar()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(epoch*1000);
        return cal;
    }

    public Time advance(int period, int value)
    {
        boolean update = false;
        Calendar c = this.toCalendar();

        do
        {
            // SECOND
            if(period == SECOND) {
                break;
            } else {
                if(c.get(SECOND) != 0)
                    update = true;
                c.set(SECOND, 0);
            }

            // MINUTE
            if (period == Calendar.MINUTE) {
                break;
            } else {
                if (c.get(MINUTE) != 0)
                    update = true;
                c.set(MINUTE, 0);
            }

            // HOUR
            if(period == HOUR || period == HOUR_OF_DAY) {
                break;
            } else {
                if(c.get(HOUR_OF_DAY) != 0)
                    update = true;
                c.set(HOUR_OF_DAY, 0);
            }

            // DAY
            if(period == DAY_OF_WEEK || period == DAY_OF_WEEK_IN_MONTH || period == DAY_OF_MONTH || period == DAY_OF_YEAR || period == WEEK_OF_YEAR) {
                break;
            } else {
                if(c.get(DAY_OF_MONTH) != 1)
                    update = true;
                c.set(DAY_OF_MONTH, 1);
            }

            // MONTH
            if(period == MONTH) {
                break;
            } else {
                if(c.get(MONTH) != 0)
                    update = true;
                c.set(MONTH, 0);
            }

        }
        while(false);

        if(update || c.get(period) != value)
        {
            c.add(period, 1);

            int currentValue = c.get(period);
            if(currentValue < value)
                c.add(period, value - currentValue);
            else if(period != Calendar.YEAR)
            {
                while(c.get(period) != value)
                    c.add(period, 1);
            }
            else
            {
                return null; // This event will never happen (A specific year)
            }

            return new Time(c.getTimeInMillis()/1000);
        }
        else
            return new Time(this.toInt());
    }

    /*public Time advance(int period, int value)
    {
        Calendar c = this.toCalendar();

        switch(period)
        {
            case Calendar.YEAR:
            case Calendar.WEEK_OF_YEAR:
                c.set(Calendar.MONTH, 0);
            case Calendar.MONTH:
                c.set(Calendar.DAY_OF_MONTH, 1);
            case Calendar.DAY_OF_YEAR:
            case Calendar.DAY_OF_MONTH:
            case Calendar.DAY_OF_WEEK_IN_MONTH:
            case Calendar.DAY_OF_WEEK:
                c.set(Calendar.HOUR_OF_DAY, 0);
            case Calendar.HOUR_OF_DAY:
            case Calendar.HOUR:
                c.set(Calendar.MINUTE, 0);
            case Calendar.MINUTE:
                c.set(Calendar.SECOND, 0);
        }

        c.add(period, 1);

        int currentValue = c.get(period);
        if(currentValue < value)
            c.add(period, value - currentValue);
        else if(period != Calendar.YEAR)
        {
            while(c.get(period) != value)
                c.add(period, 1);
        }
        else
        {
            return null; // This event will never happen (A specific year)
        }

        return new Time(c.getTimeInMillis()/1000);
    }*/

    public String toString()
    {
        Calendar c = toCalendar();
        return String.format("%04d-%02d-%02d %02d:%02d:%02d", c.get(YEAR), c.get(MONTH), c.get(DAY_OF_MONTH), c.get(HOUR_OF_DAY), c.get(MINUTE), c.get(SECOND));
    }

    @Override
    public int compareTo(Object o) {
        Time that = (Time)o;

        return this.toInt() - that.toInt();
    }

    public boolean isGreaterThan(Object that) {
        return this.compareTo(that) > 0;
    }

    public boolean isLessThan(Object that) {
        return this.compareTo(that) < 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Time time = (Time) o;

        return this.epoch == time.epoch;
    }

    @Override
    public int hashCode() {
        int result = 7;
        result = 31 * result + (int)epoch;
        return result;
    }


    public static void main(String[] args)
    {
        System.out.println(new Time(2017, 12, 12, 6, 32, 16));
        System.out.println(new Time(2017, 12, 12, 6, 32, 0));
        System.out.println(new Time(2017, 12, 12, 6, 0, 0));
        System.out.println(new Time(2017, 12, 12, 0, 0, 0));
        System.out.println(new Time(2017, 12, 12, 23, 59, 59));
    }
}
