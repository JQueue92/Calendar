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
        int deltaYear = (position - basePosition) / 12;
        int deltaMonth = (position - basePosition) % 12;
        int month = baseDateCell.month +deltaMonth;
        int year = baseDateCell.year+deltaYear;
        if(month <= 0){
            --year;
            month += 12;
        } else if(month >= 13){
            ++year;
            month -= 12;
        }
        dc.setDate(year,  month);
        Log.d("Wangbin","baseDateCell:"+baseDateCell+"\tdc:"+dc);
        return dc;
    }
}
