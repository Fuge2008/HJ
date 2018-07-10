package com.haoji.haoji.ui;

import android.graphics.drawable.BitmapDrawable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haoji.haoji.R;
import com.haoji.haoji.adapter.DayDayListviewAdapter;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.TimeUtils;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;




public class DayDayEnergyActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack
        , View.OnClickListener {

    private static String TAG = DayDayEnergyActivity.class.getSimpleName();
    private PullToRefreshListView pull_refresh_list;
    private List<JSONObject> articles=new ArrayList<JSONObject>();
    private int page = 1;
    private ListView actualListView;
    private  JSONObject json;
    private DayDayListviewAdapter adapter;
    private SharedPreferencesUtil util;
    private View layout_tool_menu;
    private PopupWindow popupWindow;
    private int toolWidth;
    private int toolHeight;
    private int toolXY;
    private TextView tool_zan_text, tool_sc_text;
    private boolean isLike = false;
    private boolean isSc = false;


    @Override
    public void initMainView() {
        setContentView(R.layout.activity_daydayenergy);
        util = new SharedPreferencesUtil(this);


    }

    @Override
    public void initUi() {
        toolWidth = getResources().getDimensionPixelOffset(R.dimen.tool_layout_width);
        toolHeight = getResources().getDimensionPixelOffset(R.dimen.tool_layout_height);
        toolXY = getResources().getDimensionPixelOffset(R.dimen.tool_XY);
        Util.setHeadTitleMore(this, "大家正能量", true);
        LinearLayout iv_add= (LinearLayout) findViewById(R.id.head_more);
        iv_add.setVisibility(View.GONE);
        findViewById(R.id.head_more).setOnClickListener(this);
        initXListView();
        layout_tool_menu = LayoutInflater.from(this).inflate(R.layout.layout_tool_menu_not_comment, null);
        layout_tool_menu.findViewById(R.id.btn_zan).setOnClickListener(this);
        layout_tool_menu.findViewById(R.id.btn_sc).setOnClickListener(this);
        tool_zan_text = (TextView) layout_tool_menu.findViewById(R.id.tool_zan_text);
        tool_sc_text = (TextView) layout_tool_menu.findViewById(R.id.tool_sc_text);
        popupWindow = new PopupWindow(layout_tool_menu, toolWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.popWindow_anim_tool);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

    }

    private void initXListView() {
        pull_refresh_list = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pull_refresh_list.setMode(PullToRefreshBase.Mode.BOTH);
        pull_refresh_list
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(
                                DayDayEnergyActivity.this,
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);

                        refreshView.getLoadingLayoutProxy()
                                .setLastUpdatedLabel(label);


                        if (pull_refresh_list.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
                            page = 1;

                        } else if (pull_refresh_list.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_END) {
                            page++;
                        }
                        getData(page);//TODO  刷新获取更多数据
                    }
                });

        actualListView = pull_refresh_list.getRefreshableView();
        adapter = new DayDayListviewAdapter(DayDayEnergyActivity.this, articles,listener);
        actualListView.setAdapter(adapter);
        actualListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }

        });
        getData(1);
        pull_refresh_list.setRefreshing(false);
    }

    @Override
    public void loadData() {

    }

    private void getData(final int page_num) {           //TODO 加载数据逻辑
        //NetIntent netIntent = new NetIntent();
       // netIntent.client_energyToday(util.getUserId(), page_num + "", new NetIntentCallBackListener(this));
        new NetIntent().client_getEnergyEverydayList(util.getUserId(),page_num+"", new NetIntentCallBackListener(this));
        page=page_num;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData(1);
    }



    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_show_pop:
                   json = (JSONObject) v.getTag();
                    Log.d(TAG, "onClick: " + json.toString());
                    if (json.containsKey("praisestatus")) {
                        if (json.get("praisestatus").toString().equals("1")) {
                            String number=json.get("praisenum").toString();
                            int num=Integer.valueOf(number);
                            LogUtils.i("点赞数量：===》"+num);
                            tool_zan_text.setText(" "+num);
                            isLike = true;
                        } else {
                            tool_zan_text.setText("点赞");
                            isLike = false;
                        }
                    }
                    if (json.containsKey("favoritestatus")) {
                        if (json.get("favoritestatus").toString().equals("1")) {
                            String number=json.get("favoritenum").toString();
                            int num=Integer.valueOf(number);
                            LogUtils.i("收藏数量：===》"+num);
                            tool_sc_text.setText(""+num);
                            isSc = true;
                        } else {
                            isSc = false;
                            tool_sc_text.setText("收藏");
                        }
                    }
                    popupWindow.showAsDropDown(v, -toolWidth - toolXY, -(toolXY / 2) - (toolHeight / 2));
                    break;
            }
        }
    };

    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        JSONObject jsonObject = null;
        try {
            jsonObject= JSON.parseObject(response);
            LogUtils.e(jsonObject.toString());//TODO 打印转换过来的效果
            pull_refresh_list.onRefreshComplete();
            String code = jsonObject.getString("code");
            if (jsonObject.containsKey("energyList")) {
                JSONArray users_temp = jsonObject.getJSONArray("energyList");
                util.setTime(TimeUtils.getNowTime());//TODO  记录上次刷新时间
                if(users_temp!=null){
                if (page == 1) {
                    articles.clear();//如果是首页，先把集合清理干净
                    for (int i = 0; i < users_temp.size(); i++) {
                        JSONObject json = users_temp.getJSONObject(i);
                        articles.add(json);
                    }
                } else {
                    for (int i = 0; i < users_temp.size(); i++) {
                        JSONObject json = users_temp.getJSONObject(i);
                       // String sID = json.getString("id");
                            articles.add(json);
                    }
                }
                   // adapter.notifyDataSetChanged();
                }
                //

            } else {

            }
        } catch (com.alibaba.fastjson.JSONException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_zan://点赞
                NetIntent netIntent = new NetIntent();
                if (!isLike) {
                    netIntent.client_addPraise(util.getUserId(), json.get("id").toString(), "1", new NetIntentCallBackListener(DayDayEnergyActivity.this));
                    json.put("praisestatus", "1");
                    if (json.get("praisenum").toString() != "") {
                        int praisenum = Integer.parseInt(json.get("praisenum").toString());
                        json.put("praisenum", praisenum + 1 + "");
                       // json.put("praisestatus", "1");
                    }
                    ToastUtils.showShortToastSafe(DayDayEnergyActivity.this, "点赞成功");
                } else {
                    netIntent.client_deletePraise(util.getUserId(), json.get("id").toString(), "1", new NetIntentCallBackListener(DayDayEnergyActivity.this));
                    json.put("praisestatus", "0");
                    if (json.get("praisenum").toString() != "") {
                        int praisenum = Integer.parseInt(json.get("praisenum").toString());
                        json.put("praisenum", praisenum - 1 + "");
                        //json.put("praisestatus", "1");
                    }
                    ToastUtils.showShortToastSafe(DayDayEnergyActivity.this, "取消点赞");
                }
                adapter.notifyDataSetChanged();
                popupWindow.dismiss();
                break;
            case R.id.btn_sc://收藏
                NetIntent netIntent2 = new NetIntent();
                if (!isSc) {
                    netIntent2.client_addContain(util.getUserId(), json.get("id").toString(), "1", new NetIntentCallBackListener(DayDayEnergyActivity.this));
                    ToastUtils.showShortToastSafe(DayDayEnergyActivity.this, "收藏成功");
                    json.put("favoritestatus", "1");
                    if (json.get("favoritenum").toString() != "") {
                        int favoritenum = Integer.parseInt(json.get("favoritenum").toString());
                        json.put("favoritenum", favoritenum + 1 + "");
                        //json.put("favoritestatus", "0");
                    }
                } else {
                    netIntent2.client_deleteContain(util.getUserId(), json.get("id").toString(), "1", new NetIntentCallBackListener(DayDayEnergyActivity.this));
                    ToastUtils.showShortToastSafe(DayDayEnergyActivity.this, "取消收藏");
                    json.put("favoritestatus", "0");
                    if (json.get("favoritenum").toString() != "") {
                        int favoritenum = Integer.parseInt(json.get("favoritenum").toString());
                        json.put("favoritenum", favoritenum - 1 + "");
                        //json.put("favoritestatus", "1");
                    }

                }
                adapter.notifyDataSetChanged();
                popupWindow.dismiss();
                break;
        }
    }


}
