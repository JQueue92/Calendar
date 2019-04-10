package com.jqueue.calendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.jqueue.calendarview.date.DateCell;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MonthView extends FrameLayout {

    DateCell dateCell;
    ArrayList<View> children = new ArrayList<>(31);
    private final ArrayList<View> mMatchParentChildren = new ArrayList<>(1);
    int weeks;
    int firstDayOfWeek;

    OnClickListener dayClickListener;

    public void update(DateCell dateCell) {
        this.dateCell = dateCell;
        weeks = dateCell.getSumWeeksOfMonth();
        firstDayOfWeek = dateCell.getFirstDayOfWeek();
        requestLayout();
    }

    public MonthView(@NonNull Context context) {
        this(context, null);
    }

    public MonthView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
        initChildren();
    }



    private void initChildren() {
        for (int index = 0; index < 31; index++) {
            View v = createDayView();
            children.add(v);
            addView(v);
        }
    }

    private void initData() {
        dateCell = new DateCell();
        dateCell.toCurrentDate();
        weeks = dateCell.getSumWeeksOfMonth();
        firstDayOfWeek = dateCell.getFirstDayOfWeek();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final boolean measureMatchParentChildren =
                MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY ||
                        MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;

        View v = getChildAt(0);
        int parentWidthMeasureSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int parentWidthMeasureSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeightMeasureSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int parentHeightMeasureSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int maxWidth = 0;
        int maxHeigth = 0;
        for (int index = 0; index < getChildCount(); index++) {
            final View child = getChildAt(index);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            measureChildWithMargins(child,
                    MeasureSpec.makeMeasureSpec(parentWidthMeasureSpecSize / 7, parentWidthMeasureSpecMode),
                    0, MeasureSpec.makeMeasureSpec(parentHeightMeasureSpecSize / weeks, parentHeightMeasureSpecMode),
                    0);
            maxWidth = Math.max(maxWidth, (child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin) * 7);
            maxHeigth = Math.max(maxHeigth, (child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin) * weeks);
            if (measureMatchParentChildren) {
                if (lp.width == LayoutParams.MATCH_PARENT ||
                        lp.height == LayoutParams.MATCH_PARENT) {
                    mMatchParentChildren.add(child);
                }
            }
        }
        setMeasuredDimension(maxWidth, maxHeigth);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.d("Wangbin", "MonthView OnLayout:" + dateCell + "\tl:" + left + "\tt:" + top + "\tr:" + right + "\tb:" + bottom);
        layoutChildren(left, top, right, bottom);
    }

    private View createDayView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.day_view, null);
    }

    private void layoutChildren(int left, int top, int right, int bottom) {
        for (int week = 0; week < weeks; week++) {
            int count = (week == 0) ? 7 - firstDayOfWeek + 1 : ((week == weeks - 1) ? dateCell.getSumDays() - ((weeks - 2) * 7 + 7 - firstDayOfWeek + 1) : 7);
            for (int index = 0; index < count; index++) {
                int childIndex = (week == 0) ? index : (week > 1 ? (week - 1) * 7 + 7 - firstDayOfWeek + 1 + index : 7 - firstDayOfWeek + 1 + index);
                Log.d("Wangbin", "week:" + (week + 1) + "\tcount:" + count + "\tchildIndex:" + childIndex);
                View child = getChildAt(childIndex);
                bindChildView(child, childIndex + 1);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int l = 0;
                if (week == 0 && firstDayOfWeek > 1) {
                    l = left + getPaddingLeft() + (firstDayOfWeek - 1 + index) * (child.getMeasuredWidth() + lp.leftMargin);
                } else {
                    l = left + getPaddingLeft() + index * (child.getMeasuredWidth() + lp.leftMargin);
                }
                int t = top + getPaddingTop() + week * (child.getMeasuredHeight() + lp.topMargin);
                int r = l + child.getMeasuredWidth();
                int b = t + child.getMeasuredHeight();
                Log.d("Wangbin", "hori-step:" + (child.getMeasuredWidth() + lp.leftMargin));
                Log.d("Wangbin", "childIndex:" + childIndex + "\tleft:" + l + "\ttop:" + t + "\tright" + r + "\tbottom:" + b);
                child.layout(l, t, r, b);
            }
        }
    }

    private void bindChildView(View child, int day) {
        ((TextView) child.findViewById(R.id.mainTitle)).setText(String.valueOf(day));
        if (dayClickListener != null) {
            child.setOnClickListener(dayClickListener);
        } else {
            child.setOnClickListener(defaultClickListener);
        }
    }

    public void setOnDayClickListener(OnClickListener clickListener) {
        dayClickListener = clickListener;
    }

    private OnClickListener defaultClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(),
                    String.valueOf(dateCell.getYear())+getContext().getString(R.string.year)+dateCell.getMonth()+getContext().getString(R.string.month)+((TextView) v.findViewById(R.id.mainTitle)).getText(),
                    Toast.LENGTH_SHORT).show();
        }
    };
}
