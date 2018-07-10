package com.haoji.haoji.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.haoji.haoji.R;
import com.haoji.haoji.adapter.YearGridViewAdapter;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.fragment.tab1.DaysFragmentActivity;
import com.haoji.haoji.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;




public class MoreByYearActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private static String TAG = MoreByYearActivity.class.getSimpleName();

    private GridView gridView;
    private YearGridViewAdapter adapter;
    private ArrayList<HashMap> maps = new ArrayList<HashMap>();
    private String initDate = "2016-01";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public void initMainView() {
        setContentView(R.layout.acitivty_year);

    }

    @Override
    public void initUi() {
        Util.setHeadTitleMore(this, "更多经典正能量", true);
        findViewById(R.id.head_more).setVisibility(View.GONE);
        gridView = (GridView) findViewById(R.id.gridview_year);
        adapter = new YearGridViewAdapter(this, maps);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        gridView.setNumColumns(3);
        gridView.setHorizontalSpacing(10);
        gridView.setVerticalSpacing(10);

    }

    @Override
    public void loadData() {
        for (int i = 1; i <= 24; i++) {
            HashMap map = new HashMap();
            String date = getDateMonthNum(initDate, i);
            map.put("month", date.substring(5, 7));
            map.put("year", date.substring(0, 4));
            maps.add(map);
        }

        adapter.notifyDataSetChanged();

    }


    public String getDateMonthNum(String strdate, int day) {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM");
            Date d = format1.parse(strdate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.MONTH, day);
            Date date = calendar.getTime();
            return format1.format(date);// 2012-10
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap map = maps.get(position);
        Intent intent = new Intent(this, DaysFragmentActivity.class);
        intent.putExtra("date", map.get("year") + "-" + map.get("month"));
        intent.putExtra("title", map.get("year") + "年" + map.get("month") + "月经典正能量");
        intent.putExtra("type", 2);
        startActivity(intent);

    }
}
