package com.jqueue.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


import com.jqueue.calendarview.date.DateCell;

import androidx.annotation.Nullable;

public class MonthView extends View {

    DateCell dateCell;
    int weeks;
    int sumDays;
    int firstDayOfWeek;

    float dayViewHeight;
    float dayViewWidth;
    float bottomLineWidth;
    Paint curDayBgPaint, dayTextPaint, bottomLinePaint;

    public void update(DateCell dateCell) {
        this.dateCell = dateCell;
    }

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dateCell = new DateCell();
        dateCell.toCurrentDate();
        weeks = dateCell.getSumWeeksOfMonth();
        sumDays = dateCell.getSumDays();
        firstDayOfWeek = dateCell.getFirstDayOfWeek();

        dayViewWidth = getResources().getDimension(R.dimen.dayview_width);
        dayViewHeight = getResources().getDimension(R.dimen.dayview_height);

        curDayBgPaint = new Paint();
        curDayBgPaint.setColor(Color.RED);
        curDayBgPaint.setStyle(Paint.Style.FILL);
        curDayBgPaint.setAntiAlias(true);

        dayTextPaint = new Paint();
        dayTextPaint.setColor(Color.BLACK);
        dayTextPaint.setAntiAlias(true);
        dayTextPaint.setTextSize(getResources().getDimension(R.dimen.day_text_size));

        bottomLinePaint = new Paint();
        bottomLinePaint.setColor(Color.parseColor("#DCDCDC"));
        bottomLinePaint.setAntiAlias(true);
        bottomLinePaint.setStrokeCap(Paint.Cap.ROUND);
        bottomLineWidth = getResources().getDimension(R.dimen.bottomLineWidth);
        bottomLinePaint.setStrokeWidth(bottomLineWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            int minWidth = Math.round(dayViewWidth * 7);
            width = widthSize != 0 ? Math.min(minWidth, widthSize) : Math.round(dayViewWidth * 7);
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            int minHeight = Math.round(dayViewHeight * weeks);
            height = heightSize != 0 ? Math.min(minHeight, heightSize) : minHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int saveCount = canvas.getSaveCount();
        canvas.save();

        canvas.translate(getPaddingLeft(), getPaddingTop());
        for (int week = 0; week < weeks; week++) {
            int count = (week == 0) ? 7 - firstDayOfWeek + 1 : ((week == weeks - 1) ? dateCell.getSumDays() - ((weeks - 2) * 7 + 7 - firstDayOfWeek + 1) : 7);
            for (int index = 0; index < count; index++) {
                String day = String.valueOf(((week == 0) ? index : (week > 1 ? (week - 1) * 7 + 7 - firstDayOfWeek + 1 + index : 7 - firstDayOfWeek + 1 + index)) + 1);
                float l;
                if (week == 0 && firstDayOfWeek > 1) {
                    l = getPaddingLeft() + (firstDayOfWeek - 1 + index) * (getMeasuredWidth() / 7.0F/*day_view_width*/);
                } else {
                    l = getPaddingLeft() + index * (getMeasuredWidth() / 7.0F);
                }
                float t = getPaddingTop() + week * (getMeasuredHeight() / (float) weeks);
                float r = l + getMeasuredWidth() / 7.0F;
                float b = t + getMeasuredHeight() / (float) weeks;
                if (dateCell.isCurMonth() && Integer.parseInt(day) == dateCell.getCurDay()) {
                    canvas.drawCircle(l + (r - l) / 2.0F, t + (b - t) / 2.0F, (r - l) / 4.0F, curDayBgPaint);
                    dayTextPaint.setColor(Color.WHITE);
                } else {
                    dayTextPaint.setColor(Color.BLACK);
                }
                if (week < weeks - 1) {
                    canvas.drawLine(l, b, r, b, bottomLinePaint);
                }
                canvas.drawText(day, l + (r - l) / 2 - dayTextPaint.measureText(day) / 2, t + (b - t) / 2 + (b - t) / 4 - dayTextPaint.getFontMetrics().bottom, dayTextPaint);
            }
        }
        canvas.restoreToCount(saveCount);
    }
}
