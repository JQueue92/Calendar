package com.jqueue.calendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.jqueue.calendarview.date.DateCell;
import com.jqueue.calendarview.date.DateSet;
import com.jqueue.formatlog.LogUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarView extends RecyclerView {
    private static final String TAG = "CalendarView";
    CharSequence[] title;
    LinearLayoutManager manager;
    Paint dateDividerPaint, dividerTextPaint, headerBgPaint, headerTextPaint;
    int scrollThreshold;
    DateSet dateSet;
    String year, month;
    float dividerHeight, dividerPaddingLeft;
    float headerHeight, headerTextSize;

    float bottomLineWidth;
    int curDayTextColor, curDayBackgroundColor;
    int commonDayTextColor;
    float curDayTextSize;
    float commonDayTextSize;
    int bottomLineColor;

    public CalendarView(@NonNull Context context) {
        this(context, null);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        manager = new LinearLayoutManager(getContext());
        setLayoutManager(manager);
        setAdapter(adapter);
        scrollToPosition(adapter.getItemCount() / 1000);
        addItemDecoration(itemDecoration);

        year = getResources().getString(R.string.year);
        month = getResources().getString(R.string.month);

        scrollThreshold = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        dateSet = new DateSet();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);

        title = ta.getTextArray(R.styleable.CalendarView_weeks);
        if (title == null || title.length != 7) {
            title = getResources().getStringArray(R.array.calendarView_title);
        }
        headerBgPaint = new Paint();
        headerBgPaint.setColor(ta.getColor(R.styleable.CalendarView_headerBackground, Color.WHITE));
        headerBgPaint.setStyle(Paint.Style.FILL);
        headerBgPaint.setAntiAlias(true);

        headerHeight = ta.getDimension(R.styleable.CalendarView_headerHeight, getResources().getDimension(R.dimen.header_height));
        headerTextSize = ta.getDimension(R.styleable.CalendarView_headerTextSize, getResources().getDimension(R.dimen.header_text_size));

        headerTextPaint = new Paint();
        headerTextPaint.setColor(ta.getColor(R.styleable.CalendarView_headerTextColor, Color.BLACK));
        headerTextPaint.setAntiAlias(true);
        headerTextPaint.setTextSize(headerTextSize);

        dateDividerPaint = new Paint();
        dateDividerPaint.setColor(ta.getColor(R.styleable.CalendarView_dividerBackground, Color.parseColor("#DCDCDC")));
        dateDividerPaint.setAntiAlias(true);
        dateDividerPaint.setStyle(Paint.Style.FILL);

        dividerTextPaint = new Paint();
        dividerTextPaint.setColor(ta.getColor(R.styleable.CalendarView_dividerTextColor, Color.BLACK));
        dividerTextPaint.setAntiAlias(true);
        dividerTextPaint.setTextSize(ta.getDimension(R.styleable.CalendarView_dividerTextSize, getResources().getDimension(R.dimen.date_divider_textSize)));

        dividerHeight = ta.getDimension(R.styleable.CalendarView_dividerHeight, getResources().getDimension(R.dimen.date_devider_height));
        dividerPaddingLeft = ta.getDimension(R.styleable.CalendarView_dividerTextPaddingLeft, getResources().getDimension(R.dimen.date_devider_paddingleft));

        bottomLineWidth = ta.getDimension(R.styleable.CalendarView_lineWidth, getResources().getDimension(R.dimen.bottomLineWidth));
        curDayTextColor = ta.getColor(R.styleable.CalendarView_curDayTextColor, Color.WHITE);
        commonDayTextColor = ta.getColor(R.styleable.CalendarView_dayTextColor, Color.BLACK);
        curDayTextSize = ta.getDimension(R.styleable.CalendarView_curDayTextSize, getResources().getDimension(R.dimen.day_text_size));
        commonDayTextSize = ta.getDimension(R.styleable.CalendarView_dayTextSize, getResources().getDimension(R.dimen.day_text_size));
        bottomLineColor = ta.getColor(R.styleable.CalendarView_lineColor, Color.parseColor("#DCDCDC"));
        curDayBackgroundColor = ta.getColor(R.styleable.CalendarView_curDayBackground, Color.RED);

        ta.recycle();
    }

    RecyclerView.Adapter adapter = new RecyclerView.Adapter<VH>() {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(new MonthView.Builder(getContext())
                    .bottomLineWidth(bottomLineWidth)
                    .bottomLineColor(bottomLineColor)
                    .commonDayTextSize(commonDayTextSize)
                    .commonDayTextColor(commonDayTextColor)
                    .curDayTextColor(curDayTextColor)
                    .curDayTextSize(curDayTextSize)
                    .curDayBackgroundColor(curDayBackgroundColor)
                    .build());
        }

        @Override
        public void onViewRecycled(@NonNull VH holder) {
            super.onViewRecycled(holder);

        }

        @Override
        public void onViewDetachedFromWindow(@NonNull VH holder) {
            super.onViewDetachedFromWindow(holder);
            holder.view.onDetachedFromWindow();
        }

        @Override
        public void onViewAttachedToWindow(@NonNull VH holder) {
            super.onViewAttachedToWindow(holder);

        }

        //position（年、月、日） -> year month
        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.view.update(dateSet.getDateCellByPosition(position, holder.dateCell));
            LogUtils.d(TAG, "bindView:" + position + "\t" + holder.dateCell);
            holder.itemView.setTag(holder.dateCell.getYear() + year + holder.dateCell.getMonth() + month);
        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    };

    RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.onDraw(c, parent, state);
            for (int index = 0; index < parent.getChildCount(); index++) {
                final View child = parent.getChildAt(index);
                if (child.getTag() instanceof String && index == 0) {
                    if (child.getBottom() < parent.getPaddingTop() + dividerHeight && child.getBottom() > parent.getPaddingTop()) {
                        c.drawRect(child.getLeft(), parent.getPaddingTop(), child.getRight(), child.getBottom(), dateDividerPaint);
                        float baseLine = parent.getPaddingTop() + (child.getBottom() - parent.getPaddingTop()) / 2 + (child.getBottom() - parent.getPaddingTop()) / 4 - dividerTextPaint.getFontMetrics().bottom;
                        if (baseLine > parent.getPaddingTop()) {
                            c.drawText((String) child.getTag(), dividerPaddingLeft, baseLine, dividerTextPaint);
                        }
                        child.setVisibility(View.INVISIBLE);
                    }
                    break;
                }
            }
        }

        @Override
        public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
            LogUtils.d(TAG, "DrawOver:" + parent.getTop());
            for (int index = 0; index < parent.getChildCount(); index++) {
                final View child = parent.getChildAt(index);
                if (child.getTag() instanceof String) {
                    if (index == 0) {
                        drawHeader(c, parent);
                        if (child.getBottom() >= parent.getPaddingTop() + dividerHeight) {
                            c.drawRect(child.getLeft(), parent.getPaddingTop(), child.getRight(), parent.getPaddingTop() + dividerHeight, dateDividerPaint);
                            c.drawText((String) child.getTag(), dividerPaddingLeft, parent.getPaddingTop() + dividerHeight - dividerTextPaint.getFontMetrics().bottom, dividerTextPaint);
                        }
                        child.setVisibility(View.VISIBLE);
                    } else {
                        float top = child.getTop() > headerHeight + dividerHeight ? child.getTop() : headerHeight + dividerHeight;
                        c.drawRect(child.getLeft(), top - dividerHeight, child.getRight(), top, dateDividerPaint);
                        c.drawText((String) child.getTag(), dividerPaddingLeft, top - dividerTextPaint.getFontMetrics().bottom, dividerTextPaint);
                    }
                }
            }
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, (int) dividerHeight, 0, 0);
        }
    };

    private void drawHeader(Canvas canvas, RecyclerView parent) {
        canvas.drawRect(parent.getPaddingLeft(), parent.getPaddingTop() - headerHeight,
                parent.getPaddingLeft() + parent.getWidth() - parent.getPaddingRight(),
                parent.getPaddingTop(), headerBgPaint);
        float weekCellwidth = parent.getMeasuredWidth() / 7.0F;
        for (int week = 0; week < 7; week++) {
            canvas.drawText(title[week].toString(), parent.getPaddingLeft() + week * weekCellwidth + weekCellwidth / 2 - headerTextPaint.measureText(title[week].toString()) / 2,
                    parent.getPaddingTop() - headerHeight + headerHeight / 2 + headerHeight / 4 - headerTextPaint.getFontMetrics().bottom, headerTextPaint);
        }
    }

    @Override
    public int getPaddingTop() {
        return super.getPaddingTop() + Math.round(headerHeight);
    }

    class VH extends RecyclerView.ViewHolder {
        DateCell dateCell = new DateCell();
        MonthView view;

        public VH(@NonNull View itemView) {
            super(itemView);
            if (itemView instanceof MonthView) {
                view = (MonthView) itemView;
            }
        }
    }

}
