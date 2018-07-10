package com.haoji.haoji.fragment.tab2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.haoji.haoji.R;
import com.haoji.haoji.adapter.ListGridAdapter;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.CustomProgress;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.MyComparator;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;



public class VideoListActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack, AdapterView.OnItemClickListener {
    private static String TAG = VideoListActivity.class.getSimpleName();
    private ListView listView_video_list;
    private ListGridAdapter adapter;
    private ArrayList<ArrayList<HashMap>> datas = new ArrayList<ArrayList<HashMap>>();
    private ArrayList<HashMap> maps = new ArrayList<HashMap>();
    private CustomProgress dialog;
    private int type = 1;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    update();
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_video_list);
        type = this.getIntent().getIntExtra("type", 1);
        if (type == 1) {
            Util.setHeadTitleMore(this, "感动型视频", true);
        } else if (type == 2) {
            Util.setHeadTitleMore(this, "知识型视频", true);
        }
        findViewById(R.id.head_more).setVisibility(View.GONE);

    }

    @Override
    public void initUi() {
        dialog = CustomProgress.show(this, "加载中..", true, null);
        listView_video_list = (ListView) findViewById(R.id.listView_video_list);
        adapter = new ListGridAdapter(this, datas);
        listView_video_list.setAdapter(adapter);
        listView_video_list.setOnItemClickListener(this);
        if (type == 1) {
            new NetIntent().client_getVideoList("", new NetIntentCallBackListener(this));
        } else if (type == 2) {
            new NetIntent().client_getVideoList2("", new NetIntentCallBackListener(this));
        }

    }

    @Override
    public void loadData() {

    }


    private void update() {
        Collections.sort(maps, new MyComparator());//时间反排序
        ArrayList<HashMap> data = null;
        for (int i = 0; i < maps.size(); i++) {
            if (i == 0) {
                data = new ArrayList<HashMap>();
                data.add(maps.get(i));
            } else if (isTure(maps.get(i - 1), maps.get(i))) {
                System.out.println("=====================");
                data.add(maps.get(i));
            } else {
                System.out.println("---------------------");
                datas.add(data);
                data = new ArrayList<HashMap>();
                data.add(maps.get(i));
            }
            if (i == maps.size() - 1) {
                datas.add(data);
            }
        }
        System.out.println(new Gson().toJson(datas).toString() + " datas:" + datas.size());
    }

    public boolean isTure(HashMap map1, HashMap map2) {
        System.out.println(map1.get("weeks").toString());
        System.out.println(map2.get("weeks").toString());
        if (map1.get("weeks").toString().equals(map2.get("weeks").toString())) {
            return true;
        }
        return false;
    }


    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        if (dialog != null)
            dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("code")) {
                maps.clear();
                JSONArray jsonArray = new JSONArray(jsonObject.getString("videoList"));
                maps.addAll(Util.toHashMap(jsonArray));
                handler.sendEmptyMessage(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
