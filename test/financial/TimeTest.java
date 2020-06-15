package financial;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Created by johannes on 17/05/05.
 */
public class TimeTest {

    @Test
    public void shouldAcceptSecondFormat() {
        Time t = new Time("2017-07-12 23:59:58");
        assertEquals(2017, t.getYear());
        assertEquals(7, t.getMonth());
        assertEquals(12, t.getDay());
        assertEquals(23, t.getHour());
        assertEquals(59, t.getMinute());
        assertEquals(58, t.getSecond());
    }

    @Test
    public void shouldAcceptMinuteFormat() {
        Time t = new Time("2017-07-12 23:59");
        assertEquals(2017, t.getYear());
        assertEquals(7, t.getMonth());
        assertEquals(12, t.getDay());
        assertEquals(23, t.getHour());
        assertEquals(59, t.getMinute());
        assertEquals(0, t.getSecond());
    }

    @Test
    public void shouldAcceptDayFormat() {
        Time t = new Time("2017-07-12");
        assertEquals(2017, t.getYear());
        assertEquals(7, t.getMonth());
        assertEquals(12, t.getDay());
        assertEquals(0, t.getHour());
        assertEquals(0, t.getMinute());
        assertEquals(0, t.getSecond());
    }

    @Test (expected=IllegalArgumentException.class)
    public void shouldThrowExceptionOnIncorrectFormat() {
        new Time("2017-07");
    }

    @Test
    public void shouldAdvanceToCorrectDate()
    {
        Time t = new Time(2017, 4, 12, 14, 0, 2);
        Time r = t.advance(Calendar.HOUR_OF_DAY, 12);
        assertTrue(r.equals(new Time(2017, 4, 13, 12, 0, 0)));
    }

    @Test
    public void shouldNotAdvanceWhenAlreadyOnCorrectDate()
    {
        Time t = new Time(2017, 4, 12, 14, 45, 0);
        Time r = t.advance(Calendar.MINUTE, 45);
        assertTrue(r.equals(t));
    }

    @Test
    public void shouldAdvanceToCorrectDate2()
    {
        Time t = new Time(2017, 4, 12, 14, 0, 2);
        Time r = t.advance(Calendar.MONTH, Calendar.MARCH);
        assertTrue(r.equals(new Time(2018, 3, 1, 0, 0, 0)));
    }

    @Test
    public void shouldAdvanceToCorrectDate3()
    {
        Time t = new Time(2017, 4, 12, 14, 0, 2);
        Time r = t.advance(Calendar.MINUTE, 12);
        assertTrue(r.equals(new Time(2017, 4, 12, 14, 12, 0)));
    }

    @Test
    public void shouldAdvanceToCorrectDate4()
    {
        Time t = new Time(2017, 4, 12, 14, 0, 2);
        Time r = t.advance(Calendar.YEAR, 2020);
        assertTrue(r.equals(new Time(2020, 1, 1, 0, 0, 0)));
    }



}