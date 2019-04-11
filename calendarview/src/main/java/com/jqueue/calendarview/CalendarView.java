package com.jqueue.calendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jqueue.calendarview.date.DateCell;
import com.jqueue.calendarview.date.DateSet;
import com.jqueue.formatlog.LogUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarView extends LinearLayout {
    private static  final String TAG = "CalendarView";
    String[] title;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    Paint dateDeviderPaint, dividerTextPaint;
    int scrollThreshold;
    DateSet dateSet;
    String year, month;
    float dividerHeight, deviderPaddingLeft;

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
        LogUtils.d(TAG, "recyclerView.getTop:" + recyclerView.getTop());
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        setOrientation(LinearLayout.VERTICAL);
        title = context.getResources().getStringArray(R.array.calendarView_title);
        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount() / 1000);
        recyclerView.addItemDecoration(itemDecoration);
        addView(createHeadView());
        addView(recyclerView);
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
        }

        @Override
        public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
            LogUtils.d(TAG, "DrawOver:" + parent.getTop());
            for (int index = 0; index < parent.getChildCount(); index++) {
                final View child = parent.getChildAt(index);
                if (child.getTag() instanceof String) {
                    if (index == 0) {
                        if (child.getBottom() > dividerHeight) {
                            c.drawRect(child.getLeft(), parent.getPaddingTop(), child.getRight(), parent.getPaddingTop()+dividerHeight, dateDeviderPaint);
                            c.drawText((String) child.getTag(), deviderPaddingLeft, parent.getPaddingTop()+dividerHeight- dividerTextPaint.getFontMetrics().bottom, dividerTextPaint);
                        } else {
                            c.drawRect(child.getLeft(), parent.getPaddingTop(), child.getRight(), child.getBottom(), dateDeviderPaint);
                            c.drawText((String) child.getTag(), deviderPaddingLeft, parent.getPaddingTop() + child.getBottom() - dividerTextPaint.getFontMetrics().bottom , dividerTextPaint);
                        }
                    } else {
                        c.drawRect(child.getLeft(), child.getTop() - dividerHeight, child.getRight(), child.getTop() , dateDeviderPaint);
                        c.drawText((String) child.getTag(), deviderPaddingLeft, child.getTop() - dividerTextPaint.getFontMetrics().bottom, dividerTextPaint);
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

    private View createHeadView() {
        LinearLayout view = new LinearLayout(getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setOrientation(LinearLayout.HORIZONTAL);
        for (String s : title) {
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(new LayoutParams(0, 60, 1.0F));
            textView.setText(s);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(10F);
            textView.setGravity(Gravity.CENTER);
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            view.addView(textView);
        }
        return view;
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
