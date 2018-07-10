package com.haoji.haoji.comment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.ui.MyUnreadNewsActivity;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.SPUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.TimeUtils;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;


public class SocialFriendActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack {

    private PullToRefreshListView pull_refresh_list;
    private List<JSONObject> articles = new ArrayList<JSONObject>();
    private SocialFriendAdapter adapter;
    private ListView actualListView;
    private int page = 1;
    private TextView tv_title;
    private String userID,friendID;
    private List<String> sIDs = new ArrayList<String>();
    private SharedPreferencesUtil util;
    private JSONObject json;

    @Override
    protected void onCreate(Bundle arg0) {

        super.onCreate(arg0);

    }

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_moments_me);
    }

    @Override
    public void initUi() {

        util=new SharedPreferencesUtil(this);
        userID = util.getUserId();
        friendID = this.getIntent().getStringExtra("friendID");
        SPUtils.put(SocialFriendActivity.this,"friendid",friendID);
       tv_title = (TextView) this.findViewById(R.id.tv_title);
        setTitlt(tv_title,"好友的");
        new NetIntent().client_getUserById(friendID,new NetIntentCallBackListener(this));
        initView();

    }

    private void setTitlt(TextView v,String s) {
        if (friendID.equals(userID)) {//TODO  如果是自己的userid，则设置为我
            s = "我的相册";
        }
        tv_title.setText(s);//TODO  主标题设置
    }

    @Override
    public void loadData() {

    }

    private void initView() {

        pull_refresh_list = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pull_refresh_list.setMode(PullToRefreshBase.Mode.BOTH);

        pull_refresh_list
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(
                                SocialFriendActivity.this,
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

                        getData(page);
                    }
                });

        actualListView = pull_refresh_list.getRefreshableView();
        adapter = new SocialFriendAdapter(SocialFriendActivity.this, articles);
        actualListView.setAdapter(adapter);
        actualListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {//TODO  如果能量存在，则点击可查看详情
                if (position>2) {
                   // Log.e("position----->>", String.valueOf(position));
                    JSONObject json = adapter.getJSONs().get(position - 3);
                    startActivity(new Intent(SocialFriendActivity.this,
                            SocialDetailActivity.class).putExtra("json",
                            json.toJSONString()));//TODO  把信息封装起来，附带过去
                }
            }

        });
        getData(1);//TODO  加载第一页数据
        pull_refresh_list.setRefreshing(false);
        ImageView iv_back = (ImageView) this.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView iv_info_urread= (ImageView) findViewById(R.id.iv_camera);
        if(StringUtils.equals(friendID,util.getUserId())){
            iv_info_urread.setVisibility(View.VISIBLE);
            iv_info_urread.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SocialFriendActivity.this, MyUnreadNewsActivity.class));
                }
            });
        }else{
            iv_info_urread.setVisibility(View.GONE);
        }



    }

    private void getData(final int page_num) {//TODO  请求网络，加载列表数据
        NetIntent netIntent = new NetIntent();
        netIntent.client_getMyEnergyList(friendID,page_num + "",new NetIntentCallBackListener(this));
        page=page_num;


    }

    @Override
    public void onResume() {
        super.onResume();

        getData(1);
    }

    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        JSONObject jsonObject = null;
        try {
            //jsonObject = new JSONObject(response);
            jsonObject= JSON.parseObject(response);
            LogUtils.e(jsonObject.toString());//TODO 打印转换过来的效果

            pull_refresh_list.onRefreshComplete();
            String code = jsonObject.getString("code");
           // LogUtils.i("打印信息6："+jsonObject.toString());
             if (jsonObject.containsKey("energyList")) {
                JSONArray users_temp = jsonObject.getJSONArray("energyList");
                String time = TimeUtils.getNowTime();
                util.setTime(time);//TODO  记录上次刷新时间

                if (page == 1) {

                    articles.clear();
                    sIDs.clear();
                    for (int i = 0; i < users_temp.size(); i++) {
                        JSONObject json = users_temp.getJSONObject(i);
                        String sID = json.getString("id");
                        sIDs.add(sID);
                        articles.add(json);
                    }
                } else {
                    for (int i = 0; i < users_temp.size(); i++) {
                        JSONObject json = users_temp.getJSONObject(i);
                        String sID = json.getString("id");
                        if (!sIDs.contains(sID)) {
                            sIDs.add(sID);
                            articles.add(json);
                        }
                    }

                }
                handler.sendEmptyMessage(0);
                adapter.notifyDataSetChanged();

            }else if (jsonObject.containsKey("user")) {
                // LogUtils.i("打印个人信息："+jsonObject.toString());
                 try {
                     json = jsonObject.getJSONObject("user");
                     SPUtils.put(SocialFriendActivity.this,"friendMotto",json.getString("motto"));
                     SPUtils.put(SocialFriendActivity.this,"friendNickname",json.getString("nickname"));
                     SPUtils.put(SocialFriendActivity.this,"friendHead",json.getString("pic").toString());
                     adapter.notifyDataSetChanged();
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }else {
                //ToastUtils.showShortToast(SocialFriendActivity.this,"亲，已经见底啦!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String nick=articles.get(0).get("nickname").toString();
                    if(StringUtils.isNotEmpty(nick,true)){
                        setTitlt(tv_title,nick);
                    }
                    break;
                case 1:

                    break;
            }
        }
    };
}
