package com.jqueue.calendarview.date;

import com.jqueue.formatlog.LogUtils;

public class DateSet {
    private static final String TAG = "DateSet";
    DateCell baseDateCell;
    int basePosition;

    public DateCell getDateCellByPosition(int position, DateCell dc) {
        if (baseDateCell == null) {
            baseDateCell = new DateCell();
            basePosition = position;
            baseDateCell.toCurrentDate();
        }
        int deltaYear = (position - basePosition) / 12;
        int deltaMonth = (position - basePosition) % 12;
        int month = baseDateCell.month + deltaMonth;
        int year = baseDateCell.year + deltaYear;
        if (month <= 0) {
            --year;
            month += 12;
        } else if (month >= 13) {
            ++year;
            month -= 12;
        }
        dc.setDate(year, month);
        LogUtils.d(TAG, "baseDateCell:" + baseDateCell + "\tdc:" + dc);
        return dc;
    }
}
