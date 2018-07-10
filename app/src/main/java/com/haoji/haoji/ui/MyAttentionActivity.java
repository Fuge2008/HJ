package com.haoji.haoji.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haoji.haoji.R;
import com.haoji.haoji.adapter.UserConcernAdapter;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.comment.SocialFriendActivity;
import com.haoji.haoji.model.User;
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


public class MyAttentionActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack,
       AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static String TAG = MyAttentionActivity.class.getSimpleName();

    //private XListView listView;
    private PullToRefreshListView pull_refresh_list;
    private int page = 1;
    private ListView actualListView;
    private UserConcernAdapter adapter;
    private ArrayList<User> maps = new ArrayList<User>();
    private SharedPreferencesUtil util;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_concern_user);
        util = new SharedPreferencesUtil(this);
        //SystemUtil.showSystemBarTint(this, true);
        Util.setHeadTitleMore(this, "我的关注", true);
    }

    @Override
    public void initUi() {
        findViewById(R.id.head_more).setVisibility(View.GONE);
        initXListView();
//        listView = (XListView) findViewById(listView);
//        adapter = new UserConcernAdapter(this,maps);
//        listView.setAdapter(adapter);
//        listView.setXListViewListener(this);
//        listView.setPullLoadEnable(false);
//        listView.setOnItemClickListener(this);
//        listView.setOnItemLongClickListener(this);

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
                                MyAttentionActivity.this,
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
        //adapter = new DayDayListviewAdapter(MyAttentionActivity.this, articles,listener);
        adapter = new UserConcernAdapter(this,maps);
        actualListView.setAdapter(adapter);
        actualListView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }

        });
        actualListView.setOnItemClickListener(this);
       actualListView.setOnItemLongClickListener(this);
        getData(1);
        pull_refresh_list.setRefreshing(false);
    }
    private void getData(final int page_num) {           //TODO 加载数据逻辑
        //NetIntent netIntent = new NetIntent();
        // netIntent.client_energyToday(util.getUserId(), page_num + "", new NetIntentCallBackListener(this));
        new NetIntent().client_getConcernsList(util.getUserId(), new NetIntentCallBackListener(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        getData(1);
    }

    @Override
    public void loadData() {
        //new NetIntent().client_getConcernsList(util.getUserId(), new NetIntentCallBackListener(this));

    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    adapter.notifyDataSetChanged();
                    break;
                case 1:
                    new NetIntent().client_getConcernsList(util.getUserId(), new NetIntentCallBackListener(MyAttentionActivity.this));
                    break;
            }
        }
    };



    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        Log.d(TAG, "onResponse: " + response);
//        listView.stopLoadMore();
//        listView.stopRefresh();
        try {
            pull_refresh_list.onRefreshComplete();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = new JSONArray(jsonObject.getString("friendsList"));
            maps.clear();
            if(jsonArray!=null&& jsonArray.length()>0){
                for (int i = 0; i < jsonArray.length(); i++) {
                    maps.add(new Gson().fromJson(jsonArray.get(i).toString(), User.class));
                }
                handler.sendEmptyMessage(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onRefresh() {
//        new NetIntent().client_getConcernsList(util.getUserId(), new NetIntentCallBackListener(this));
//    }
//
//    @Override
//    public void onLoadMore() {
//
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user = maps.get(position - 1);
        startActivity(new Intent(MyAttentionActivity.this, SocialFriendActivity.class).putExtra("friendID",user.getUserid()).putExtra("nickname",user.getNickname()));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final User user = maps.get(position - 1);
        new AlertDialog.Builder(this).setTitle("提示").setMessage("是否删除关注?").setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new NetIntent().client_deleteConcern(util.getUserId(), user.getUserid(), new NetIntentCallBackListener(iNetIntentCallBack));
            }
        }).setNegativeButton("取消", null).create().show();
        return false;
    }

    private NetIntentCallBackListener.INetIntentCallBack iNetIntentCallBack=new NetIntentCallBackListener.INetIntentCallBack() {
        @Override
        public void onError(Request request, Exception e) {

        }

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonObject=new JSONObject(response);
                ToastUtils.showShortToastSafe(MyAttentionActivity.this,jsonObject.getString("msg"));
                if (jsonObject.getBoolean("code")){
                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
