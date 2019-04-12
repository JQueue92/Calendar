package com.jqueue.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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
    String[] title;
    LinearLayoutManager manager;
    Paint dateDeviderPaint, dividerTextPaint, headerBgPaint, headerTextPaint;
    int scrollThreshold;
    DateSet dateSet;
    String year, month;
    float dividerHeight, deviderPaddingLeft;

    float headerHeight, headerTextSize;

    public CalendarView(@NonNull Context context) {
        this(context, null);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        title = context.getResources().getStringArray(R.array.calendarView_title);
        manager = new LinearLayoutManager(getContext());
        setLayoutManager(manager);
        setAdapter(adapter);
        scrollToPosition(adapter.getItemCount() / 1000);
        addItemDecoration(itemDecoration);
    }

    private void initData() {
        dateDeviderPaint = new Paint();
        dateDeviderPaint.setColor(Color.parseColor("#DCDCDC"));
        dateDeviderPaint.setAntiAlias(true);
        dateDeviderPaint.setStyle(Paint.Style.FILL);

        dividerTextPaint = new Paint();
        dividerTextPaint.setColor(Color.BLACK);
        dividerTextPaint.setAntiAlias(true);
        dividerTextPaint.setTextSize(getResources().getDimension(R.dimen.date_divider_textSize));

        headerBgPaint = new Paint();
        headerBgPaint.setColor(Color.WHITE);
        headerBgPaint.setStyle(Paint.Style.FILL);
        headerBgPaint.setAntiAlias(true);

        headerHeight = getResources().getDimension(R.dimen.header_height);
        headerTextSize = getResources().getDimension(R.dimen.header_text_size);
        headerTextPaint = new Paint();
        headerTextPaint.setColor(Color.BLACK);
        headerTextPaint.setAntiAlias(true);
        headerTextPaint.setTextSize(headerTextSize);

        dividerHeight = getResources().getDimension(R.dimen.date_devider_height);
        deviderPaddingLeft = getResources().getDimension(R.dimen.date_devider_paddingleft);
        year = getResources().getString(R.string.year);
        month = getResources().getString(R.string.month);

        scrollThreshold = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        dateSet = new DateSet();

    }

    RecyclerView.Adapter adapter = new RecyclerView.Adapter<VH>() {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(getContext()).inflate(R.layout.month_view, parent, false));
        }

        @Override
        public void onViewRecycled(@NonNull VH holder) {
            super.onViewRecycled(holder);

        }

        @Override
        public void onViewDetachedFromWindow(@NonNull VH holder) {
            super.onViewDetachedFromWindow(holder);

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
                        c.drawRect(child.getLeft(), parent.getPaddingTop(), child.getRight(), child.getBottom(), dateDeviderPaint);
                        float baseLine = parent.getPaddingTop() + (child.getBottom() - parent.getPaddingTop()) / 2 + (child.getBottom() - parent.getPaddingTop()) / 4 - dividerTextPaint.getFontMetrics().bottom;
                        if (baseLine > parent.getPaddingTop()) {
                            c.drawText((String) child.getTag(), deviderPaddingLeft, baseLine, dividerTextPaint);
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
                            c.drawRect(child.getLeft(), parent.getPaddingTop(), child.getRight(), parent.getPaddingTop() + dividerHeight, dateDeviderPaint);
                            c.drawText((String) child.getTag(), deviderPaddingLeft, parent.getPaddingTop() + dividerHeight - dividerTextPaint.getFontMetrics().bottom, dividerTextPaint);
                        }
                        child.setVisibility(View.VISIBLE);
                    } else {
                        float top = child.getTop() > headerHeight + dividerHeight ? child.getTop() : headerHeight + dividerHeight;
                        c.drawRect(child.getLeft(), top - dividerHeight, child.getRight(), top, dateDeviderPaint);
                        c.drawText((String) child.getTag(), deviderPaddingLeft, top - dividerTextPaint.getFontMetrics().bottom, dividerTextPaint);
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
            canvas.drawText(title[week], parent.getPaddingLeft() + week * weekCellwidth + weekCellwidth / 2 - headerTextPaint.measureText(title[week]) / 2,
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
            view = itemView.findViewById(R.id.monthView);
        }
    }

}
