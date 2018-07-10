package com.haoji.haoji.fragment.tab2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.haoji.haoji.R;
import com.haoji.haoji.adapter.ArticleAdapter;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.ui.ArticleActivity;
import com.haoji.haoji.ui.VideoSearchActivity;
import com.haoji.haoji.util.MyComparator;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;



public class VideoActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack
        , AdapterView.OnItemClickListener, View.OnClickListener {

   private static String TAG = VideoActivity.class.getSimpleName();

    private ListView listView_video;
    private ArticleAdapter adapter;
    private ArrayList<HashMap> maps = new ArrayList<HashMap>();
    private int type;


    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Sort();
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
        setContentView(R.layout.activity_video);
        type = this.getIntent().getIntExtra("type", 0);
        //SystemUtil.showSystemBarTint(this, true);

    }

    @Override
    public void initUi() {
        listView_video = (ListView) findViewById(R.id.listview_video);
        adapter = new ArticleAdapter(this, maps, type);
        listView_video.setAdapter(adapter);
        listView_video.setOnItemClickListener(this);
        findViewById(R.id.head_search).setOnClickListener(this);
        NetIntent netIntent = new NetIntent();
        if (type == 0) {
            Util.setHeadTitleMore(this, "视频", true);
            netIntent.client_getVideoList("", new NetIntentCallBackListener(this));
        } else if (type == 1) {
            Util.setHeadTitleMore(this, "经典短文", true);
            netIntent.client_getDiaryList("", new NetIntentCallBackListener(this));
        }

    }

    @Override
    public void loadData() {

    }

    private void Sort() {
        Collections.sort(maps, new MyComparator());
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        if (resultCode == 1) {
            String tag = data.getStringExtra("tag");
            NetIntent netIntent = new NetIntent();
            if (type == 0) {
                Util.setHeadTitleMore(this, "视频", true);
                netIntent.client_getVideoList(tag, new NetIntentCallBackListener(this));
            } else if (type == 1) {
                Util.setHeadTitleMore(this, "经典短文", true);
                netIntent.client_getDiaryList(tag, new NetIntentCallBackListener(this));
            }
        }
    }



    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        System.out.println(response);
        Log.d(TAG, "onResponse: " + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean("code")) {
                maps.clear();
                if (type == 0) {
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("videoList"));
                    maps.addAll(Util.toHashMap(jsonArray));
                } else if (type == 1) {
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("diaryList"));
                    maps.addAll(Util.toHashMap(jsonArray));
                }
                handler.sendEmptyMessage(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getIds() {
        String str = "";
        if (maps.size() == 0)
            return null;
        for (HashMap map : maps) {
            str += map.get("id").toString() + ",";
        }
        return str;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap map = maps.get(position);
        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("position", position);
        intent.putExtra("size", maps.size());
        intent.putExtra("id", getIds());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_search:
                Intent intent = new Intent(this, VideoSearchActivity.class);
                if (type == 0)
                    intent.putExtra("title", "视频搜索");
                else
                    intent.putExtra("title", "日记搜索");
                startActivityForResult(intent, 1);
                break;
        }
    }

}
