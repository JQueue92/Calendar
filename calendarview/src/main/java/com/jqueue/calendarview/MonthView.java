package com.jqueue.calendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jqueue.calendarview.date.DateCell;

import java.util.HashMap;

import androidx.annotation.Nullable;

public class MonthView extends View {

    DateCell dateCell;
    int weeks;
    int sumDays;
    int firstDayOfWeek;

    float dayViewHeight;
    float dayViewWidth;
    float bottomLineWidth;

    float clickPositionX, clickPositionY;

    Paint curDayBgPaint, dayTextPaint, bottomLinePaint;

    int curDayTextColor, commonDayTextColor, bottomLineColor, curDayBackgroundColor;
    float curDayTextSize, commonDayTextSize;

    HashMap<String, float[]> map = new HashMap<>(31);
    OnClickDayListener listener;

    public void update(DateCell dateCell) {
        this.dateCell = dateCell;
        weeks = dateCell.getSumWeeksOfMonth();
        sumDays = dateCell.getSumDays();
        firstDayOfWeek = dateCell.getFirstDayOfWeek();
        map.clear();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        map.clear();
    }

    public MonthView(Builder builder, Context context) {
        this(context);
        bottomLineColor = builder.bottomLineColor;
        bottomLineWidth = builder.bottomLineWidth;
        curDayTextColor = builder.curDayTextColor;
        curDayTextSize = builder.curDayTextSize;
        commonDayTextColor = builder.commonDayTextColor;
        commonDayTextSize = builder.commonDayTextSize;
        curDayBackgroundColor = builder.curDayBackgroundColor;

        curDayBgPaint.setColor(curDayBackgroundColor);
        dayTextPaint.setColor(commonDayTextColor);
        dayTextPaint.setTextSize(commonDayTextSize);
        bottomLinePaint.setColor(bottomLineColor);
        bottomLinePaint.setStrokeWidth(bottomLineWidth);
    }

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dateCell = new DateCell();
        dateCell.toCurrentDate();
        weeks = dateCell.getSumWeeksOfMonth();
        sumDays = dateCell.getSumDays();
        firstDayOfWeek = dateCell.getFirstDayOfWeek();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);

        dayViewWidth = getResources().getDimension(R.dimen.dayview_width);
        dayViewHeight = getResources().getDimension(R.dimen.dayview_height);

        curDayTextColor = ta.getColor(R.styleable.CalendarView_curDayTextColor, Color.WHITE);
        commonDayTextColor = ta.getColor(R.styleable.CalendarView_dayTextColor, Color.BLACK);
        curDayTextSize = ta.getDimension(R.styleable.CalendarView_curDayTextSize, getResources().getDimension(R.dimen.day_text_size));
        commonDayTextSize = ta.getDimension(R.styleable.CalendarView_dayTextSize, getResources().getDimension(R.dimen.day_text_size));

        curDayBgPaint = new Paint();
        curDayBackgroundColor = ta.getColor(R.styleable.CalendarView_curDayBackground, Color.RED);
        curDayBgPaint.setColor(curDayBackgroundColor);
        curDayBgPaint.setStyle(Paint.Style.FILL);
        curDayBgPaint.setAntiAlias(true);

        dayTextPaint = new Paint();
        dayTextPaint.setColor(commonDayTextColor);
        dayTextPaint.setAntiAlias(true);
        dayTextPaint.setTextSize(commonDayTextSize);

        bottomLinePaint = new Paint();
        bottomLineColor = ta.getColor(R.styleable.CalendarView_lineColor, Color.parseColor("#DCDCDC"));
        bottomLinePaint.setColor(bottomLineColor);
        bottomLinePaint.setAntiAlias(true);
        bottomLinePaint.setStrokeCap(Paint.Cap.ROUND);
        bottomLineWidth = ta.getDimension(R.styleable.CalendarView_lineWidth, getResources().getDimension(R.dimen.bottomLineWidth));
        bottomLinePaint.setStrokeWidth(bottomLineWidth);

        ta.recycle();
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
                    dayTextPaint.setColor(curDayTextColor);
                    dayTextPaint.setTextSize(curDayTextSize);
                } else {
                    dayTextPaint.setColor(commonDayTextColor);
                    dayTextPaint.setTextSize(commonDayTextSize);
                }
                if (week < weeks - 1) {
                    canvas.drawLine(l, b, r, b, bottomLinePaint);
                }
                canvas.drawText(day, l + (r - l) / 2 - dayTextPaint.measureText(day) / 2, t + (b - t) / 2.0F + (dayTextPaint.getFontMetrics().descent - dayTextPaint.getFontMetrics().ascent) / 2.0F - dayTextPaint.getFontMetrics().descent, dayTextPaint);
                map.put(day, new float[]{l, r, t, b});
            }
        }
        canvas.restoreToCount(saveCount);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                clickPositionX = event.getX();
                clickPositionY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (clickPositionX == event.getX() && clickPositionY == event.getY()) {
                    int day = getDayByPosition(clickPositionX, clickPositionY);
                    if (day != 0) {
                        if (listener != null) {
                            listener.clickDay(dateCell.getYear(), dateCell.getMonth(), day);
                        } else {
                            Toast.makeText(getContext(), dateCell.getYear() + "年" + dateCell.getMonth() + "月" + day + "日", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private int getDayByPosition(float x, float y) {
        if (map.size() == 0) return 0;
        float[] rectInfo;//[left,right,top,bottom]
        for (int day = 1; day < 8; day++) {//先确定点击的点在周几
            rectInfo = map.get(String.valueOf(day));
            if (x >= rectInfo[0] && x <= rectInfo[1]) {
                while (rectInfo != null && (y < rectInfo[2] || y > rectInfo[3])) {
                    rectInfo = map.get(String.valueOf(day += 7));
                }
                return day > sumDays ? 0 : day;
            }
        }
        return 0;
    }

    public void setOnClickDayListener(OnClickDayListener listener) {
        this.listener = listener;
    }

    interface OnClickDayListener {
        void clickDay(int year, int month, int day);
    }

    public static class Builder {
        float bottomLineWidth;
        int curDayTextColor = Color.RED;
        int commonDayTextColor = Color.BLACK;
        float curDayTextSize;
        float commonDayTextSize;
        int bottomLineColor = Color.parseColor("#DCDCDC");
        int curDayBackgroundColor = Color.RED;
        Context context;

        public Builder(Context context) {
            this.context = context;
            bottomLineWidth = context.getResources().getDimension(R.dimen.bottomLineWidth);
            curDayTextSize = context.getResources().getDimension(R.dimen.day_text_size);
            commonDayTextSize = curDayTextSize;
        }

        public Builder bottomLineColor(int color) {
            bottomLineColor = color;
            return this;
        }

        public Builder bottomLineWidth(float width) {
            bottomLineWidth = width;
            return this;
        }

        public Builder curDayTextColor(int corlor) {
            curDayTextColor = corlor;
            return this;
        }

        public Builder commonDayTextColor(int corlor) {
            commonDayTextColor = corlor;
            return this;
        }

        public Builder curDayTextSize(float size) {
            curDayTextSize = size;
            return this;
        }

        public Builder commonDayTextSize(float size) {
            commonDayTextSize = size;
            return this;
        }

        public Builder curDayBackgroundColor(int color) {
            curDayBackgroundColor = color;
            return this;
        }

        public MonthView build() {
            return new MonthView(this, context);
        }

    }

}
