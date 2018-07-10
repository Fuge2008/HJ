package com.haoji.haoji.fragment.tab1;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haoji.haoji.R;
import com.haoji.haoji.adapter.EnergyAdapter;
import com.haoji.haoji.adapter.FriendAdapter_comment;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class DaysFragmentActivity extends BaseActivity implements AdapterView.OnItemClickListener
        , NetIntentCallBackListener.INetIntentCallBack,View.OnClickListener {

    private static String TAG = DaysFragmentActivity.class.getSimpleName();

    //private XListView listView;
    private PullToRefreshListView pull_refresh_list;
    private int page = 1;
    private ListView actualListView;
    private ArrayList<HashMap> maps = new ArrayList<HashMap>();
    private EnergyAdapter adapter;
    private FriendAdapter_comment friendAdapter;
    private int type;
    private int selectpage = 1;
    private EditText edit_comment_content;
    private Button btn_comment_submit;
    private LinearLayout layout_comment;
    private SharedPreferencesUtil util;
    private HashMap map;
    private String date;
    private boolean isFresh = false;

    private boolean isLike = false;
    private boolean isSc = false;
    private TextView tool_zan_text, tool_sc_text;

    private String title = null;
    private String id;

    private View layout_tool_menu;
    private PopupWindow popupWindow;
    private int toolWidth;
    private int toolHeight;
    private int toolXY;



    @Override
    public void initMainView() {
        this.setContentView(R.layout.activity_days);
    }

    @Override
    public void initUi() {

        toolWidth = getResources().getDimensionPixelOffset(R.dimen.tool_layout_width);
        toolHeight=getResources().getDimensionPixelOffset(R.dimen.tool_layout_height);
        toolXY=getResources().getDimensionPixelOffset(R.dimen.tool_XY);
        util = new SharedPreferencesUtil(this);
        type = this.getIntent().getIntExtra("type", 0);

        //listView = (XListView) findViewById(R.id.listview);
//        if (type == 0) {
//            Util.setHeadTitleMore(this, "天天正能量", true);
//            adapter = new EnergyAdapter(this, maps);
//            listView.setAdapter(adapter);
//        } else if (type == 1) {
//            Util.setHeadTitleMore(this, "好友正能量", true);
//            friendAdapter = new FriendAdapter_comment(this, maps, listener);
//            listView.setAdapter(friendAdapter);
//            if (!util.getIsLogin()) {
//                listView.setVisibility(View.GONE);
//            }
//        } else
        if (type == 2) {
            title = this.getIntent().getStringExtra("title");
            if (title != null)
                Util.setHeadTitleMore(this, title, true);
            else
                Util.setHeadTitleMore(this, "天天正能量", true);
            date = this.getIntent().getStringExtra("date");
            initXListView();
            friendAdapter = new FriendAdapter_comment(this, maps, listener);
            actualListView.setAdapter(friendAdapter);
            //listView.setAdapter(friendAdapter);
        }
//        else if (type==3){
//            title = this.getIntent().getStringExtra("title");
//            id=this.getIntent().getStringExtra("id");
//            Util.setHeadTitleMore(this, title, true);
//            friendAdapter = new FriendAdapter_comment(this, maps, listener);
//            listView.setAdapter(friendAdapter);
//            if (!util.getIsLogin()) {
//                listView.setVisibility(View.GONE);
//            }
//        }
        findViewById(R.id.head_more).setVisibility(View.GONE);
        //listView.setOnItemClickListener(this);
//        listView.setXListViewListener(this);
//        listView.setPullLoadEnable(true);
        edit_comment_content = (EditText) findViewById(R.id.edit_comment_content);
        btn_comment_submit = (Button) findViewById(R.id.btn_comment_submit);
        btn_comment_submit.setOnClickListener(this);
        layout_comment = (LinearLayout) findViewById(R.id.layout_comment);
        layout_tool_menu = LayoutInflater.from(this).inflate(R.layout.layout_tool_menu, null);
        layout_tool_menu.findViewById(R.id.btn_zan).setOnClickListener(this);
        layout_tool_menu.findViewById(R.id.btn_pl).setOnClickListener(this);
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
                                DaysFragmentActivity.this,
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
        //adapter = new DayDayListviewAdapter( DaysFragmentActivity.this, articles,listener);
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
        //NetIntent netIntent = new NetIntent();
        // netIntent.client_energyToday(util.getUserId(), page_num + "", new NetIntentCallBackListener(this));
       // new NetIntent().client_getEnergyEverydayList(util.getUserId(),page_num+"", new NetIntentCallBackListener(this));
        new NetIntent().client_getEnergyByMonth(util.getUserId(), date, selectpage + "", new NetIntentCallBackListener(intentCallBack));
        selectpage=page_num;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData(1);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            map = (HashMap) v.getTag();
            System.out.println(map.toString());
            if (map.containsKey("praisestatus")) {
                if (map.get("praisestatus").toString().equals("1")) {
                    tool_zan_text.setText("取消");
                    isLike = true;
                } else {
                    tool_zan_text.setText("赞");
                    isLike = false;
                }
            }
            if (map.containsKey("favoritestatus")) {
                if (map.get("favoritestatus").toString().equals("1")) {
                    tool_sc_text.setText("取消");
                    isSc = true;
                } else {
                    isSc = false;
                    tool_sc_text.setText("收藏");
                }
            }
            switch (v.getId()) {
                /*
                case R.id.btn_share:
                    Intent intent = new Intent(DaysFragmentActivity.this, TXLActivity.class);
                    intent.putExtra("type", 1);
                    intent.putExtra("title", "选择分享用户");
                    intent.putExtra("content", map.get("content").toString());
                    startActivity(intent);
                    break;
                    */
                case R.id.show_tool:
                    popupWindow.showAsDropDown(v, -toolWidth - toolXY, -(toolXY/2)-(toolHeight/2));
                    break;
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (type == 0 | type == 2) {
//            Intent intent = new Intent(this, InfomationFragmentActivity.class);
//            HashMap map = maps.get(position - 1);
//            intent.putExtra("id", map.get("id").toString());
//            startActivity(intent);
//        } else {
//            Intent intent = new Intent(this, InfomationFragmentActivity.class);
//            HashMap map = maps.get(position - 1);
//            intent.putExtra("id", map.get("id").toString());
//            startActivity(intent);
//        }
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (adapter!=null)
                        adapter.notifyDataSetChanged();
                    if (friendAdapter!=null)
                        friendAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    loadData();
                    break;
            }
        }
    };

    @Override
    public void loadData() {
//        NetIntent netIntent = new NetIntent();
//        if (type == 0) {
//            netIntent.client_energyToday("", selectpage + "", new NetIntentCallBackListener(intentCallBack));
//        } else if (type == 1) {
//            netIntent.client_getFriendEnergyList(util.getUserId(), selectpage + "", new NetIntentCallBackListener(intentCallBack));
//        } else if (type == 2) {
//            netIntent.client_getEnergyByMonth(util.getUserId(), date, selectpage + "", new NetIntentCallBackListener(intentCallBack));
//        }else if (type==3){
//            netIntent.client_getMyEnergyList(id,selectpage+"",new NetIntentCallBackListener(intentCallBack));
//        }
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_comment_submit:
                if (!edit_comment_content.getText().toString().equals("")) {
                    NetIntent netIntent1 = new NetIntent();
                    netIntent1.client_addComment(util.getUserId(), map.get("id").toString(), edit_comment_content.getText().toString(), "1", "", new NetIntentCallBackListener(this));
                    HashMap comment = new HashMap();
                    comment.put("nickname", util.getNickName());
                    comment.put("content", edit_comment_content.getText().toString());
                    try {
                        ArrayList<HashMap<String, Object>> coms = new ArrayList<HashMap<String, Object>>();
                        coms.addAll(Util.toHashMap(new JSONArray(map.get("comlist").toString())));
                        coms.add(comment);
                        map.put("comlist", new Gson().toJson(coms).toString());
                        friendAdapter.notifyDataSetChanged();
                        //SystemDialog.DialogToast(getApplicationContext(),"评论成功");
                        ToastUtils.showShortToastSafe(DaysFragmentActivity.this,"评论成功");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //SystemDialog.DialogToast(this, "内容不能为空");
                    ToastUtils.showShortToastSafe(DaysFragmentActivity.this,"内容不能为空");
                }
                layout_comment.setVisibility(View.GONE);
                edit_comment_content.setText("");
                edit_comment_content.clearFocus();
                break;
            case R.id.head_search:
                break;
            case R.id.btn_zan:
                NetIntent netIntent = new NetIntent();
                if (!isLike) {
                    netIntent.client_addPraise(util.getUserId(), map.get("id").toString(), "1", new NetIntentCallBackListener(DaysFragmentActivity.this));
                    map.put("praisestatus", "1");
                    if (map.get("praisenum").toString() != "") {
                        int browsenum = Integer.parseInt(map.get("praisenum").toString());
                        map.put("praisenum",browsenum+1+"");
                    }
                    //SystemDialog.DialogToast(getApplicationContext(), "点赞成功");
                    ToastUtils.showShortToastSafe(DaysFragmentActivity.this,"点赞成功");
                } else {
                    netIntent.client_deletePraise(util.getUserId(), map.get("id").toString(), "1", new NetIntentCallBackListener(DaysFragmentActivity.this));
                    map.put("praisestatus", "0");
                    if (map.get("praisenum").toString() != "") {
                        int browsenum = Integer.parseInt(map.get("praisenum").toString());
                        map.put("praisenum",browsenum-1+"");
                    }
                    //SystemDialog.DialogToast(getApplicationContext(), "取消点赞");
                    ToastUtils.showShortToastSafe(DaysFragmentActivity.this,"取消点赞");
                }
                friendAdapter.notifyDataSetChanged();
                popupWindow.dismiss();
                break;
            case R.id.btn_sc:
                NetIntent netIntent2 = new NetIntent();
                if (!isSc) {
                    netIntent2.client_addContain(util.getUserId(), map.get("id").toString(), "1", new NetIntentCallBackListener(DaysFragmentActivity.this));
                    //SystemDialog.DialogToast(getApplicationContext(), "收藏成功");
                    ToastUtils.showShortToastSafe(DaysFragmentActivity.this,"收藏成功");
                    map.put("favoritestatus", "1");
                } else {
                    netIntent2.client_deleteContain(util.getUserId(), map.get("id").toString(), "1", new NetIntentCallBackListener(DaysFragmentActivity.this));
                    //SystemDialog.DialogToast(getApplicationContext(), "取消收藏");
                    ToastUtils.showShortToastSafe(DaysFragmentActivity.this,"取消收藏");
                    map.put("favoritestatus", "0");
                }
                friendAdapter.notifyDataSetChanged();
                popupWindow.dismiss();
                break;
            case R.id.btn_pl:
                layout_comment.setVisibility(View.VISIBLE);
                popupWindow.dismiss();
                showSoftInputFromWindow(this,edit_comment_content);
                break;
        }
    }

    private NetIntentCallBackListener.INetIntentCallBack intentCallBack = new NetIntentCallBackListener.INetIntentCallBack() {
        @Override
        public void onError(Request request, Exception e) {

        }

        @Override
        public void onResponse(String response) {
            Log.d(TAG, "onResponse: "+response);
//            listView.stopLoadMore();
//            listView.stopRefresh();
            try {
                JSONObject jsonObject = new JSONObject(response);
                pull_refresh_list.onRefreshComplete();
                if (jsonObject.getBoolean("code")) {
                    edit_comment_content.setText("");
                    layout_comment.setVisibility(View.GONE);
                    if (isFresh)
                        maps.clear();
                    maps.addAll(Util.toHashMap(jsonObject.getJSONArray("energyList")));
                    handler.sendEmptyMessage(0);
                } else {
                    //SystemDialog.DialogToast(getApplicationContext(), jsonObject.getString("msg"));
                    ToastUtils.showShortToastSafe(DaysFragmentActivity.this,jsonObject.getString("msg"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        System.out.println(response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            //SystemDialog.DialogToast(getApplicationContext(), jsonObject.getString("msg"));
            if (jsonObject.getBoolean("code")) {
                edit_comment_content.setText("");
                layout_comment.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//
//    @Override
//    public void onRefresh() {
//        selectpage = 1;
//        isFresh = true;
//        loadData();
//    }
//
//    @Override
//    public void onLoadMore() {
//        isFresh = false;
//        selectpage++;
//        loadData();
//    }
}
