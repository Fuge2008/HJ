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
import java.util.HashMap;
import java.util.List;


public class MyCollectionActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack
      , View.OnClickListener {
    private static String TAG = MyCollectionActivity.class.getSimpleName();
    private PullToRefreshListView pull_refresh_list;
    private List<com.alibaba.fastjson.JSONObject> articles=new ArrayList<com.alibaba.fastjson.JSONObject>();
    private ListView actualListView;
    private com.alibaba.fastjson.JSONObject json;
    private DayDayListviewAdapter adapter;
    private SharedPreferencesUtil util;
    private int page = 1;
    //private boolean isFresh = false;
    private View layout_tool_menu_and_delete;
    private PopupWindow popupWindow;
    private int toolWidth;
    private int toolHeight;
    private int toolXY;
    private HashMap map;
    int position;

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_mycollection);
        util = new SharedPreferencesUtil(this);
        Util.setHeadTitleMore(this, "我的收藏", true);

    }

    @Override
    public void initUi() {
        findViewById(R.id.head_more).setVisibility(View.GONE);
       
        initXListView();

        toolWidth = getResources().getDimensionPixelOffset(R.dimen.tool_layout_width_delete);
        toolHeight = getResources().getDimensionPixelOffset(R.dimen.tool_layout_height);
        toolXY = getResources().getDimensionPixelOffset(R.dimen.tool_XY);

        layout_tool_menu_and_delete = LayoutInflater.from(this).inflate(R.layout.layout_tool_menu_and_delete, null);

        layout_tool_menu_and_delete.findViewById(R.id.btn_zf).setOnClickListener(this);
        layout_tool_menu_and_delete.findViewById(R.id.btn_del).setOnClickListener(this);

        popupWindow = new PopupWindow(layout_tool_menu_and_delete, toolWidth , LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.popWindow_anim_tool);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        NetIntent netIntent = new NetIntent();
        netIntent.client_getFavoriteEnergyList(util.getUserId(), page + "", new NetIntentCallBackListener(this));

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
                                MyCollectionActivity.this,
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
        adapter = new DayDayListviewAdapter(MyCollectionActivity.this, articles,listener);

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
    private void getData(final int page_num) {           //TODO 加载数据逻辑
        new NetIntent().client_getFavoriteEnergyList(util.getUserId(), page_num + "", new NetIntentCallBackListener(MyCollectionActivity.this));
        page=page_num;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData(1);
    }

    @Override
    public void loadData() {

    }

//    private Handler handler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                   // isFresh = true;
//                    page = 1;
//                    new NetIntent().client_getFavoriteEnergyList(util.getUserId(), page + "", new NetIntentCallBackListener(MyCollectionActivity.this));
//                    adapter.notifyDataSetChanged();
//                    break;
//            }
//        }
//    };

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            json = (JSONObject) v.getTag();
            Log.d(TAG, "onClick: " + json.toString());
            
            switch (v.getId()) {
                case R.id.iv_show_pop:
                    json = (JSONObject) v.getTag();
                    position=json.getInteger("position");
                    Log.d(TAG, "onClick: " + json.toString());
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
        System.out.println(response);
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
                }
                adapter.notifyDataSetChanged();

            } else {
                //ToastUtils.showShortToast(DayDayEnergyActivity.this,"服务器繁忙,请稍后!");
            }
        } catch (com.alibaba.fastjson.JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_zf:
                popupWindow.dismiss();
                ToastUtils.showShortToastSafe(MyCollectionActivity.this,"转发功能尚未开放");
                break;
            case R.id.btn_del:
                popupWindow.dismiss();
                new NetIntent().client_deleteContain(util.getUserId(),  json.get("id").toString(),json.get("type").toString(), new NetIntentCallBackListener(MyCollectionActivity.this));
                ToastUtils.showShortToastSafe(MyCollectionActivity.this,"删除成功");
                //handler.sendEmptyMessage(0);
                articles.remove(position-1);
                adapter.notifyDataSetChanged();
                break;
        }
    }
}
