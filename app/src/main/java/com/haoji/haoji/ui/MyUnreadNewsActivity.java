package com.haoji.haoji.ui;

import android.content.Intent;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.haoji.haoji.R;
import com.haoji.haoji.adapter.MyUnreadNewsAdapter;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.comment.SocialDetailActivity;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.SPUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.TimeUtils;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyUnreadNewsActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack{
    private PullToRefreshListView pull_refresh_list;
    private ListView actualListView;
    private MyUnreadNewsAdapter adapter;
    private SharedPreferencesUtil util;
    private List<JSONObject> unreadNews=new ArrayList<JSONObject>();
    JSONObject singleEnergy;
    private int page = 1;
    private String energyId;
    @BindView(R.id.pull_refresh_list)
    PullToRefreshListView pullRefreshList;



    @Override
    public void initMainView() {
        setContentView(R.layout.activity_my_unread_news);
        util=new SharedPreferencesUtil(this);
        ButterKnife.bind(this);
        Util.setHeadTitleMore(this, "消息", true);
        ImageView iv_delet= (ImageView) findViewById(R.id.iv_more2);
        iv_delet.setVisibility(View.GONE);
    }

    @Override
    public void initUi() {
        initXListView();
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
                                MyUnreadNewsActivity.this,
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
        adapter = new MyUnreadNewsAdapter(MyUnreadNewsActivity.this, unreadNews);
        LogUtils.i("打印数据：====》"+unreadNews.toString());//TODO 打印转换过来的效果
        actualListView.setAdapter(adapter);
        actualListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {//TODO  如果能量存在，则点击可查看详情

                JSONObject json = null;
                try {
                    json = adapter.getJSONs().get(position);
                    energyId=json.getString("energyid");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(StringUtils.isNotEmpty(energyId,true)){
                    new NetIntent().client_energyDetail(util.getUserId(),energyId,new NetIntentCallBackListener(MyUnreadNewsActivity.this));
                }else {
                    ToastUtils.showShortToastSafe(MyUnreadNewsActivity.this,"抱歉，该数据已经不存在！");
                }


            }

        });
        getData(1);
        pull_refresh_list.setRefreshing(false);
    }
    private void getData(final int page_num) {           //TODO 加载数据逻辑
        new NetIntent().client_getMyUnreadComminfo(util.getUserId(), page_num + "", new NetIntentCallBackListener(MyUnreadNewsActivity.this));
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

    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        JSONObject jsonObject = null;
        try {
            jsonObject= JSON.parseObject(response);
           LogUtils.i(jsonObject.toString());//TODO 打印转换过来的效果
            pull_refresh_list.onRefreshComplete();
            String code = jsonObject.getString("code");
            if (jsonObject.containsKey("infoList")) {
                JSONArray users_temp = jsonObject.getJSONArray("infoList");
                util.setTime(TimeUtils.getNowTime());//TODO  记录上次刷新时间
                if(users_temp!=null){
                    if (page == 1) {
                        unreadNews.clear();//如果是首页，先把集合清理干净
                        for (int i = 0; i < users_temp.size(); i++) {
                            JSONObject json = users_temp.getJSONObject(i);
                            unreadNews.add(json);
                        }
                    } else {
                        for (int i = 0; i < users_temp.size(); i++) {
                            JSONObject json = users_temp.getJSONObject(i);
                            // String sID = json.getString("id");
                            unreadNews.add(json);

                        }

                    }
                }
               // handler.sendEmptyMessage(0);
                adapter.notifyDataSetChanged();

            } else if(jsonObject.containsKey("energy")){
               String string= jsonObject.getString("energy");
                try {
                    org.json.JSONObject jsonObject1= new org.json.JSONObject(response);
                    if (jsonObject.getBoolean("code")) {
                        org.json.JSONArray jsonArray1 = new org.json.JSONArray(jsonObject.getString("energy"));
                        if(jsonArray1!=null && jsonArray1.length()>0){
                            org.json.JSONObject json = jsonArray1.getJSONObject(0);
                            //LogUtils.i("打印数据json：====》"+json.toString());
                            startActivity(new Intent(MyUnreadNewsActivity.this,
                                    SocialDetailActivity.class).putExtra("json",
                                    json.toString()));
                        }else{
                            ToastUtils.showShortToast(MyUnreadNewsActivity.this,"该数据已不存在!");
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                }else{
                    //ToastUtils.showShortToast(MyUnreadNewsActivity.this,"亲，已经见底啦!");
                }


        } catch (com.alibaba.fastjson.JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        new NetIntent().client_processMyUnreadComminfo(util.getUserId(),new NetIntentCallBackListener(MyUnreadNewsActivity.this));
        SPUtils.put(getApplicationContext(),"infoCnt","");
    }
}
