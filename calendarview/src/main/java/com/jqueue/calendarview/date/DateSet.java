package com.jqueue.calendarview.date;

import android.util.Log;

public class DateSet {
    DateCell baseDateCell;
    int basePosition;

    public DateCell getDateCellByPosition(int position,DateCell dc) {
        if (baseDateCell == null) {
            baseDateCell = new DateCell();
            basePosition = position;
            baseDateCell.toCurrentDate();
        }
        int year = (position - basePosition) / 12;
        int month = (position - basePosition) % 12;
        dc.setDate(baseDateCell.year + year, baseDateCell.month + month);
        Log.d("Wangbin","baseDateCell:"+baseDateCell+"\tdc:"+dc);
        return dc;
    }
}
