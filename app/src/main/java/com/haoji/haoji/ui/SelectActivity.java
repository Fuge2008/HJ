//package com.haoji.haoji.ui;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.support.v4.content.LocalBroadcastManager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.haoji.haoji.R;
//import com.haoji.haoji.base.BaseActivity2;
//import com.haoji.haoji.common.Constants;
//import com.haoji.haoji.custom.CustomProgress;
//import com.haoji.haoji.network.NetIntent;
//import com.haoji.haoji.network.NetIntentCallBackListener;
//import com.haoji.haoji.util.ToastUtils;
//import com.haoji.haoji.util.Util;
//import com.squareup.okhttp.Request;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//
//public class SelectActivity extends BaseActivity2 implements NetIntentCallBackListener.INetIntentCallBack, AdapterView.OnItemClickListener {
//    private static String TAG = SelectActivity.class.getSimpleName();
//    private ListView listView_select;
//    private ArrayList<HashMap> maps = new ArrayList<HashMap>();
//    private SelectAdapter adapter;
//    private int type;
//    private String universityid = "";
//    private boolean isZy = false;
//    private String city;
//
//    private String collegeid;
//
//    private CustomProgress dialog;
//
//    Handler handler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    adapter.notifyDataSetChanged();
//                    break;
//            }
//        }
//    };
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//    }
//
//    @Override
//    public void initMainView() {
//        setContentView(R.layout.activity_select);
//        type = this.getIntent().getIntExtra("type", 0);
//        universityid = this.getIntent().getStringExtra("universityid");
//        city = this.getIntent().getStringExtra("city");
//        System.out.println("type:" + type);
//    }
//    @Override
//    public void initUi() {
//        dialog = CustomProgress.show(this, "加载中..", true, null);
//        listView_select = (ListView) findViewById(R.id.listView_select);
//        adapter = new SelectAdapter();
//        listView_select.setAdapter(adapter);
//        listView_select.setOnItemClickListener(this);
//        NetIntent netIntent = new NetIntent();
//        switch (type) {
//            case 1001:
//                Util.setHeadTitleMore(this, "选择学校", true);
//                netIntent.client_getUniversityList(city, new NetIntentCallBackListener(this));
//                break;
//            case 1002:
//                Util.setHeadTitleMore(this, "选择专业", true);
//                netIntent.client_getCollegeList(universityid, new NetIntentCallBackListener(this));
//                break;
//        }
//        findViewById(R.id.head_more).setVisibility(View.GONE);
//
//    }
//
//    @Override
//    public void loadData() {
//
//    }
//
//
//
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        HashMap map = maps.get(position);
//        switch (type) {
//            case 1001:
//                Intent intent = new Intent();
//                intent.putExtra("text", map.get("name").toString());
//                intent.putExtra("universityid", map.get("id").toString());
//                intent.setAction(Constants.Action_select_school);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//                finish();
//                break;
//            case 1002:
//                if (!isZy) {
//                    dialog = CustomProgress.show(this, "加载中..", true, null);
//                    NetIntent netIntent = new NetIntent();
//                    collegeid = map.get("id").toString();
//                    netIntent.client_getMajorList(collegeid, new NetIntentCallBackListener(this));
//                } else {
//                    Intent intent1 = new Intent();
//                    intent1.putExtra("text", map.get("majorname").toString());
//                    intent1.putExtra("collegeid", collegeid);
//                    intent1.putExtra("majorid", map.get("id").toString());
//                    intent1.setAction(Constants.Action_select_zy);
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
//                    finish();
//                }
//                break;
//        }
//
//    }
//
//    @Override
//    public void onError(Request request, Exception e) {
//        if (dialog != null)
//            dialog.dismiss();
//    }
//
//    @Override
//    public void onResponse(String response) {
//        if (dialog != null)
//            dialog.dismiss();
//        System.out.println(response);
//        try {
//            JSONObject jsonObject = new JSONObject(response);
//            if (jsonObject.getBoolean("code")) {
//                if (jsonObject.has("universityList"))
//                    maps.addAll(Util.toHashMap(new JSONArray(jsonObject.getString("universityList"))));
//                if (jsonObject.has("collegeList"))
//                    maps.addAll(Util.toHashMap(new JSONArray(jsonObject.getString("collegeList"))));
//                if (jsonObject.has("majorList")) {
//                    JSONArray jsonArray = new JSONArray(jsonObject.getString("majorList"));
//                    if (jsonArray.length() > 0) {
//                        maps.clear();
//                        maps.addAll(Util.toHashMap(jsonArray));
//                        isZy = true;
//                    } else {
//                        ToastUtils.showShortToastSafe(SelectActivity.this, "无数据");
//                    }
//                }
//                handler.sendEmptyMessage(0);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    class SelectAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return maps.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            convertView = LayoutInflater.from(SelectActivity.this).inflate(R.layout.listview_item_text, null);
//            TextView item_text = (TextView) convertView.findViewById(R.id.item_text);
//            HashMap map = maps.get(position);
//            if (map.containsKey("name"))
//                item_text.setText(map.get("name").toString());
//            if (map.containsKey("majorname"))
//                item_text.setText(map.get("majorname").toString());
//            return convertView;
//        }
//    }
//}
