//package com.haoji.haoji.ui;
//
//import android.content.Intent;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.text.format.DateUtils;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.handmark.pulltorefresh.library.PullToRefreshBase;
//import com.handmark.pulltorefresh.library.PullToRefreshListView;
//import com.haoji.haoji.R;
//import com.haoji.haoji.adapter.FriendAdapter_comment;
//
//import com.haoji.haoji.base.BaseActivity;
//import com.haoji.haoji.network.NetIntent;
//import com.haoji.haoji.network.NetIntentCallBackListener;
//import com.haoji.haoji.util.SharedPreferencesUtil;
//import com.haoji.haoji.util.ToastUtils;
//import com.haoji.haoji.util.Util;
//import com.squareup.okhttp.Request;
//
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//
//public class MyEnergyActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack
//        , View.OnClickListener
//        , AdapterView.OnItemClickListener {
//    private static String TAG = MyEnergyActivity.class.getSimpleName();
//
//    //private XListView listview_myenery;
//    private PullToRefreshListView pull_refresh_list;
//    private int page = 1;
//    private ListView actualListView;
//    private FriendAdapter_comment adapter;
//    private ArrayList<HashMap> maps = new ArrayList<HashMap>();
//    private SharedPreferencesUtil util;
//   // private int page = 1;
//    private boolean isFresh = false;
//
//    private int delIndex = -1;
//
//    private View layout_head_add;
//
//    private EditText edit_comment_content;
//    private Button btn_comment_submit;
//    private RelativeLayout layout_comment;
//    private View layout_tool_menu_delete;
//    private PopupWindow popupWindow;
//    private int toolWidth;
//    private int toolHeight;
//    private int toolXY;
//    private TextView tool_zan_text, tool_sc_text;
//    private HashMap map;
//    private boolean isLike = false;
//    private boolean isSc = false;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//
//
//    }
//
//    @Override
//    public void initMainView() {
//        setContentView(R.layout.activity_myenergy);
//        util = new SharedPreferencesUtil(this);
//        Util.setHeadTitleMore(this, "我的正能量", true);
//
//    }
//
//    @Override
//    public void initUi() {
//
//        findViewById(R.id.head_more).setVisibility(View.GONE);
//        layout_head_add = LayoutInflater.from(this).inflate(R.layout.layout_item_add, null);
//        layout_head_add.findViewById(R.id.btn_add).setOnClickListener(this);
//        initXListView();
//        //listview_myenery = (XListView) findViewById(listview_myenery);
//
//        //listview_myenery.setAdapter(adapter);
//
////        listview_myenery.setXListViewListener(this);
////        listview_myenery.setPullLoadEnable(true);
//
////        listview_myenery.addHeaderView(layout_head_add);
//
//        toolWidth = getResources().getDimensionPixelOffset(R.dimen.tool_layout_width);
//        toolHeight = getResources().getDimensionPixelOffset(R.dimen.tool_layout_height);
//        toolXY = getResources().getDimensionPixelOffset(R.dimen.tool_XY);
//
//        layout_tool_menu_delete = LayoutInflater.from(this).inflate(R.layout.layout_tool_menu_delete, null);
//        layout_tool_menu_delete.findViewById(R.id.btn_del).setOnClickListener(this);
//        popupWindow = new PopupWindow(layout_tool_menu_delete, toolWidth / 2, LinearLayout.LayoutParams.WRAP_CONTENT);
//        popupWindow.setFocusable(true);
//        popupWindow.setAnimationStyle(R.style.popWindow_anim_tool);
//        popupWindow.setOutsideTouchable(true);
//        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//
//        NetIntent netIntent = new NetIntent();
//        netIntent.client_getMyEnergyList(util.getUserId(), page + "", new NetIntentCallBackListener(this));
//
//    }
//    private void initXListView() {
//        pull_refresh_list = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
//        pull_refresh_list.setMode(PullToRefreshBase.Mode.BOTH);
//        pull_refresh_list
//                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
//                    @Override
//                    public void onRefresh(
//                            PullToRefreshBase<ListView> refreshView) {
//                        String label = DateUtils.formatDateTime(
//                                MyEnergyActivity.this,
//                                System.currentTimeMillis(),
//                                DateUtils.FORMAT_SHOW_TIME
//                                        | DateUtils.FORMAT_SHOW_DATE
//                                        | DateUtils.FORMAT_ABBREV_ALL);
//
//                        refreshView.getLoadingLayoutProxy()
//                                .setLastUpdatedLabel(label);
//
//
//                        if (pull_refresh_list.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
//                            page = 1;
//
//                        } else if (pull_refresh_list.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_END) {
//                            page++;
//                        }
//                        getData(page);//TODO  刷新获取更多数据
//                    }
//                });
//
//        actualListView = pull_refresh_list.getRefreshableView();
//        //adapter = new DayDayListviewAdapter(MyAttentionActivity.this, articles,listener);
//        //adapter = new UserConcernAdapter(this,maps);
//        adapter = new FriendAdapter_comment(this, maps, listener);
//        actualListView.setAdapter(adapter);
//        actualListView.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//
//        });
//        getData(1);
//        pull_refresh_list.setRefreshing(false);
//    }
//    private void getData(final int page_num) {           //TODO 加载数据逻辑
//        //NetIntent netIntent = new NetIntent();
//        // netIntent.client_energyToday(util.getUserId(), page_num + "", new NetIntentCallBackListener(this));
//        //new NetIntent().client_getConcernsList(util.getUserId(), new NetIntentCallBackListener(this));
//        new NetIntent().client_getMyEnergyList(util.getUserId(), page + "", new NetIntentCallBackListener(this));
//        page=page_num;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        getData(1);
//    }
//
//    @Override
//    public void loadData() {
//
//    }
//
//
//
////    @Override
////    protected void onResume() {
////        super.onResume();
////        isFresh = true;
////        page = 1;
////        new NetIntent().client_getMyEnergyList(util.getUserId(), page + "", new NetIntentCallBackListener(this));
////    }
//
//    private View.OnClickListener listener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            map = (HashMap) v.getTag();
//            switch (v.getId()) {
//                case R.id.show_tool:
//                    popupWindow.showAsDropDown(v, -toolWidth / 2 - toolXY, -(toolXY / 2) - (toolHeight / 2));
//                    break;
//            }
//        }
//    };
//
//    private Handler handler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    adapter.notifyDataSetChanged();
//                    break;
//                case 1:
//                    isFresh = true;
//                    page = 1;
//                    new NetIntent().client_getMyEnergyList(util.getUserId(), page + "", new NetIntentCallBackListener(MyEnergyActivity.this));
//                    break;
//            }
//        }
//    };
//
////    private void stop() {
//////        listview_myenery.stopRefresh();
//////        listview_myenery.stopLoadMore();
////    }
//
//    @Override
//    public void onError(Request request, Exception e) {
//
//    }
//
//    @Override
//    public void onResponse(String response) {
//        System.out.println(response);
//        //stop();
//        try {
//            pull_refresh_list.onRefreshComplete();
//            JSONObject jsonObject = new JSONObject(response);
//            if (jsonObject.getBoolean("code")) {
//
//                if (jsonObject.has("energyList")) {
//                    if (isFresh)
//                        maps.clear();
//                    maps.addAll(Util.toHashMap(jsonObject.getJSONArray("energyList")));
//                    adapter.notifyDataSetChanged();
//                }
//                if (jsonObject.getString("msg").toString().equals("删除成功")) {
//                    handler.sendEmptyMessage(1);
//                }
//
//            } else {
//                ToastUtils.showShortToastSafe(MyEnergyActivity.this, jsonObject.getString("msg"));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_add:
//                Intent intent = new Intent(this, AddEnergyActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.btn_del:
//                popupWindow.dismiss();
//                new NetIntent().client_deleteEnergy(util.getUserId(), map.get("id").toString(), new NetIntentCallBackListener(MyEnergyActivity.this));
//                break;
//        }
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(this, InfomationFragmentActivity.class);
//        HashMap map = maps.get(position - 1);
//        intent.putExtra("id", map.get("id").toString());
//        startActivity(intent);
//    }
//
////    @Override
////    public void onRefresh() {
////        isFresh = true;
////        page = 1;
////        new NetIntent().client_getMyEnergyList(util.getUserId(), page + "", new NetIntentCallBackListener(this));
////    }
////
////    @Override
////    public void onLoadMore() {
////        isFresh = false;
////        page++;
////        new NetIntent().client_getMyEnergyList(util.getUserId(), page + "", new NetIntentCallBackListener(this));
////    }
//}
