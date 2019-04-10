package com.jqueue.calendarview.date;

import android.text.format.DateFormat;

import java.util.Calendar;

public class DateCell {
    int year;
    int month;
    int sumDays;

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public DateCell() {

    }

    public DateCell toCurrentDate(){
        year = Integer.parseInt(DateFormat.format("yyyy", System.currentTimeMillis()).toString());
        month = Integer.parseInt(DateFormat.format("MM", System.currentTimeMillis()).toString());
        sumDays = getDaysOfMonth(year, month);
        return this;
    }

    public void setDate(int year, int month) {
        this.year = year;
        this.month = month;
        sumDays = getDaysOfMonth(year, month);
    }

    public int getSumDays() {
        return sumDays;
    }


    public int getSumWeeksOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, sumDays);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    public int getFirstDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    private int getDaysOfMonth(int year, int moth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, moth - 1);
        c.set(Calendar.DATE, 1);
        c.roll(Calendar.DATE, -1);
        return c.get(Calendar.DATE);
    }

    @Override
    public String toString() {
        return "DateCell{" +
                "year=" + year +
                ", month=" + month +
                ", sumDays=" + sumDays +
                '}';
    }
}
