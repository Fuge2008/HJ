//package com.haoji.haoji.custom;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.text.Layout;
//import android.text.StaticLayout;
//import android.text.TextPaint;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.TextView;
//
//import com.haoji.haoji.R;
//import com.haoji.haoji.model.TabClass;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.GregorianCalendar;
//
//
//
//
//public class TableClass extends View {
//
//    private Context context;
//    private float table_width;//列宽
//    private float table_height;
//    private float x;//当前按下的位置
//    private float y;
//    private int columns = 7;//列宽个数
//    private int moveX;//当前游标位置
//    private int moveY;
//    private Bitmap Me;
//    private int row;//当前行数
//    private int col;
//
//    private OnClickTableListener onClickTableListener;
//
//    private ArrayList<TabClass> tabClasses = new ArrayList<TabClass>();
//
//    public TableClass(Context context) {
//        super(context);
//        this.context = context;
//        init();
//    }
//
//    public TableClass(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        this.context = context;
//        init();
//    }
//
//    public TableClass(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        this.context = context;
//        init();
//    }
//
//    private void init() {
//        table_height = getResources().getDimension(R.dimen.table_height);
//        table_width = getResources().getDimension(R.dimen.table_width);
//        Me = BitmapFactory.decodeResource(getResources(), R.mipmap.btn_gxj_addclass);
//
//    }
//
//    // 获得当前日期与本周日相差的天数
//    private int getMondayPlus() {
//        Calendar cd = Calendar.getInstance();
//        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
//        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
//        if (dayOfWeek == 1) {
//            return 0;
//        } else {
//            return 1 - dayOfWeek;
//        }
//    }
//
//    /*获取本周的第一天日期*/
//    private Date Week(){
//        int mondayPlus = getMondayPlus();
//        GregorianCalendar currentDate = new GregorianCalendar();
//        currentDate.add(GregorianCalendar.DATE, mondayPlus);
//        Date monday = currentDate.getTime();
//        return monday;
//    }
//
//    /*设置月份*/
//    private <T extends Activity> void setMonth(T context, String Month) {
//        TextView tableMonth = (TextView) context.findViewById(R.id.table_month);
//        tableMonth.setText(Month);
//    }
//
//    /*初始化日期时间*/
//    public <T extends Activity> void initDate(T context) {
//        Date date = new Date(System.currentTimeMillis());
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        String d = new SimpleDateFormat("MM-dd").format(cal.getTime());
//        setMonth(context, new SimpleDateFormat("MM").format(cal.getTime()) + "月");
//        for (int i=0;i<7;i++){
//            Date weekone=Week();
//            Calendar cals = Calendar.getInstance();
//            cals.setTime(weekone);
//            cals.set(Calendar.DATE, cals.get(Calendar.DATE)+i);
//            Date date1=new Date(cals.getTimeInMillis());
//            String d2 = new SimpleDateFormat("MM-dd").format(date1.getTime());
//            setDate(getWeek(date1), d2, context);
//        }
//    }
//
//    /*设置文本日期*/
//    private <T extends Activity> void setDate(int p, String date, T context) {
//        switch (p) {
//            case 0:
//                TextView table_seven = (TextView) context.findViewById(R.id.table_seven);
//                table_seven.setText(date);
//                break;
//            case 1:
//                TextView table_one = (TextView) context.findViewById(R.id.table_one);
//                table_one.setText(date);
//                break;
//            case 2:
//                TextView table_two = (TextView) context.findViewById(R.id.table_two);
//                table_two.setText(date);
//                break;
//            case 3:
//                TextView table_three = (TextView) context.findViewById(R.id.table_three);
//                table_three.setText(date);
//                break;
//            case 4:
//                TextView table_four = (TextView) context.findViewById(R.id.table_four);
//                table_four.setText(date);
//                break;
//            case 5:
//                TextView table_five = (TextView) context.findViewById(R.id.table_five);
//                table_five.setText(date);
//                break;
//            case 6:
//                TextView table_six = (TextView) context.findViewById(R.id.table_six);
//                table_six.setText(date);
//                break;
//        }
//    }
//
//    /*要把日期获取周几*/
//    private int getWeek(Date date) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
//        if (week_index < 0) {
//            week_index = 0;
//        }
//        return week_index;
//    }
//
//    @Override
//    public void draw(Canvas canvas) {
//        addCursor(canvas);
//        for (TabClass aClass : tabClasses) {
//            NewClass(canvas, aClass);
//        }
//    }
//
//    /*添加课程*/
//    private void NewClass(Canvas canvas, TabClass tabClass) {
//        /*保存画布*/
//        canvas.save();
//        Paint paint = new Paint();
//        paint.setColor(Color.parseColor("#e6f5ff"));
//        paint.setAlpha(120);
//        Rect rect = new Rect((int) (tabClass.getCol() * table_width) + tabClass.getCol(), (int) (tabClass.getRow() * table_height) + tabClass.getRow(), (int) table_width + (int) (tabClass.getCol() * table_width) + tabClass.getCol(), (int) (table_height) * tabClass.getClassLength() + (int) (tabClass.getRow() * table_height) + tabClass.getRow());
//        canvas.drawRect(rect, paint);
//        TextPaint textPaint = new TextPaint();
//        textPaint.setColor(Color.parseColor("#333333"));
//        textPaint.setTextAlign(Paint.Align.CENTER);
//        textPaint.setTextSize(40);
//        textPaint.setAlpha(120);
//        StaticLayout layout = new StaticLayout(tabClass.getClassName(), textPaint, (int) table_width - 20, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
//        canvas.translate((int) (tabClass.getCol() * table_width) + tabClass.getCol() + table_width / 2, (int) (tabClass.getRow() * table_height) + tabClass.getRow());
//        layout.draw(canvas);
//        /*重置画布*/
//        canvas.restore();
//    }
//
//    /*对外添加数据方法*/
//    public void addClass(ArrayList<TabClass> cls) {
//        tabClasses.addAll(cls);
//        invalidate();
//    }
//
//    /*添加游标*/
//    private void addCursor(Canvas canvas) {
//        Paint paint = new Paint();
//        paint.setColor(Color.parseColor("#e6f5ff"));
//        paint.setAlpha(120);
//        Rect rect = new Rect(moveX, moveY, (int) table_width + moveX, (int) table_height + moveY);
//        canvas.drawRect(rect, paint);
//        canvas.drawBitmap(big(Me, table_height / 2, table_height / 2), moveX + (table_width / 2 - table_width / 4), moveY + (table_height / 2 - table_height / 4), paint);
//    }
//
//    /*放大图片*/
//    public Bitmap big(Bitmap b, float x, float y) {
//        int w = b.getWidth();
//        int h = b.getHeight();
//        float sx = (float) x / w;//要强制转换
//        float sy = (float) y / h;
//        Matrix matrix = new Matrix();
//        matrix.postScale(sx, sy); // 长和宽放大缩小的比例
//        Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w,
//                h, matrix, true);
//        return resizeBmp;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                x = event.getX();
//                y = event.getY();
//                break;
//            case MotionEvent.ACTION_UP:
//                OnTouch();
//                break;
//        }
//        return true;
//        //return super.onTouchEvent(event);
//    }
//
//    private void OnTouch() {
//        /*后面加上的是偏移量*/
//        int c = getCols(x);
//        int r = getRows(y);
//        if (this.onClickTableListener != null) {
//            this.onClickTableListener.ClickItem(r, c);
//        }
//        if (c == col && row == r) {
//            if (this.onClickTableListener != null) {
//                this.onClickTableListener.ClickDoubleItem(r, c);
//            }
//            return;
//        }
//        col = c;
//        row = r;
//        moveX = (int) (c * table_width) + c;
//        moveY = (int) (r * table_height) + r;
//        invalidate();
//    }
//
//    /*计算游标行数*/
//    private int getRows(float y) {
//        int col = (int) (y / table_height);
//        return col;
//    }
//
//    /*计算游标列数*/
//    private int getCols(float x) {
//        int row = (int) (x / table_width);
//        return row;
//    }
//
//    public OnClickTableListener getOnClickTableListener() {
//        return onClickTableListener;
//    }
//
//    public void setOnClickTableListener(OnClickTableListener onClickTableListener) {
//        if (onClickTableListener != null) {
//            this.onClickTableListener = onClickTableListener;
//        }
//    }
//
//    public interface OnClickTableListener {
//
//        public abstract void ClickItem(int row, int col);
//
//        public abstract void ClickDoubleItem(int row, int col);
//    }
//}
