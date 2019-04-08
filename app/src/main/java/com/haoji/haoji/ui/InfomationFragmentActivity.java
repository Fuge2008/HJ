//package com.haoji.haoji.ui;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.haoji.haoji.R;
//import com.haoji.haoji.adapter.CommentAdapter;
//import com.haoji.haoji.adapter.ShowImgGridViewAdapter;
//import com.haoji.haoji.base.BaseActivity2;
//import com.haoji.haoji.network.NetIntent;
//import com.haoji.haoji.network.NetIntentCallBackListener;
//import com.haoji.haoji.util.SharedPreferencesUtil;
//import com.haoji.haoji.util.ToastUtils;
//import com.haoji.haoji.util.Util;
//import com.squareup.okhttp.Request;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//
//
//
//public class InfomationFragmentActivity extends BaseActivity2 implements NetIntentCallBackListener.INetIntentCallBack, AdapterView.OnItemClickListener,View.OnClickListener {
//    private static String TAG = InfomationFragmentActivity.class.getSimpleName();
//    private ImageView info_img;
//    private TextView info_title, info_date, info_area, info_content, info_top;
//    private String id;
//    private JSONObject json;
//    private EditText edit_comment_content;
//    private Button btn_comment_submit;
//    private RelativeLayout layout_comment;
//    private SharedPreferencesUtil util;
//    private RelativeLayout btn_top;
//    private ListView listivew_comment;
//    private CommentAdapter adapter;
//    private ArrayList<HashMap<String, Object>> maps = new ArrayList<HashMap<String, Object>>();
//    private GridView info_gridview;
//    private ShowImgGridViewAdapter imgGridViewAdapter;
//    private List imgs = new ArrayList();
//    private LinearLayout layout_imgs;
//    private boolean isLike = false;
//    private boolean isSc = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public void initMainView() {
//        setContentView(R.layout.activity_infomation);
//    Util.setHeadTitleMore(this, "详细", true);
//    findViewById(R.id.head_more).setVisibility(View.GONE);
//    util = new SharedPreferencesUtil(this);
//    id = this.getIntent().getStringExtra("id");
//    }
//    @Override
//    public void initUi() {
//
//        info_img = (ImageView) findViewById(R.id.info_img);
//        info_title = (TextView) findViewById(R.id.info_title);
//        info_date = (TextView) findViewById(R.id.info_date);
//        info_area = (TextView) findViewById(R.id.info_area);
//        info_top = (TextView) findViewById(R.id.info_top);
//        info_gridview = (GridView) findViewById(R.id.info_gridview);
//        btn_top = (RelativeLayout) findViewById(R.id.btn_top);
//        info_content = (TextView) findViewById(R.id.info_content);
//        edit_comment_content = (EditText) findViewById(R.id.edit_comment_content);
//        btn_comment_submit = (Button) findViewById(R.id.btn_comment_submit);
//        layout_comment = (RelativeLayout) findViewById(R.id.layout_comment);
//        listivew_comment = (ListView) findViewById(R.id.listview_comment);
//        btn_top.setOnClickListener(this);
//        findViewById(R.id.btn_share).setOnClickListener(this);
//        btn_comment_submit.setOnClickListener(this);
//        findViewById(R.id.btn_comment).setOnClickListener(this);
//        findViewById(R.id.btn_sc).setOnClickListener(this);
//        adapter = new CommentAdapter(this, maps, null);
//        listivew_comment.setAdapter(adapter);
//        imgGridViewAdapter = new ShowImgGridViewAdapter(this, imgs);
//        info_gridview.setAdapter(imgGridViewAdapter);
//        info_gridview.setOnItemClickListener(this);
//        info_gridview.setNumColumns(3);
//        info_gridview.setHorizontalSpacing(5);
//        info_gridview.setVerticalSpacing(5);
//        layout_imgs = (LinearLayout) findViewById(R.id.layout_imgs);
//    }
//
//    Handler handler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    updateView();
//                    break;
//                case 1:
//                    adapter.notifyDataSetChanged();
//                    Util.setListViewHeightBasedOnChildren(listivew_comment);
//                    break;
//                case 2:
//                    imgGridViewAdapter.notifyDataSetChanged();
//                    Util.setListViewHeightBasedOnChildren(info_gridview, 3);
//                    break;
//                case 3:
//                    loadData();
//                    break;
//            }
//        }
//    };
//
//    private void updateView() {
//        try {
//            Util.ImageLoaderToPicAuto(this, json.getString("pic"), info_img);
//            info_title.setText(json.getString("title"));
//            info_content.setText(json.getString("content"));
//            info_date.setText(json.getString("updatetime"));
//            info_area.setText(json.getString("university"));
//            info_top.setText(json.getString("praisenum"));
//            if (json.has("praisestatus")) {
//                if (json.getInt("praisestatus") == 1) {
//                    isLike = true;
//                } else {
//                    isLike = false;
//                }
//            }
//            if (json.has("favoritestatus")) {
//                if (json.getInt("favoritestatus") == 1) {
//                    isSc = true;
//                } else {
//                    isSc = false;
//                }
//            }
//            imgs.clear();
//            imgs.addAll(Arrays.asList(json.getString("apppics").split(";")));
//            for (int i = 0; i < imgs.size(); i++) {
//                if (imgs.get(i).toString().length() > 0) {
//                    layout_imgs.setVisibility(View.VISIBLE);
//                    handler.sendEmptyMessage(2);
//                } else {
//                    layout_imgs.setVisibility(View.GONE);
//                }
//                break;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void loadData() {
//        NetIntent netIntent = new NetIntent();
//        if (util.getUserId() != null) {
//            netIntent.client_energyDetail(util.getUserId(), id, new NetIntentCallBackListener(this));
//        } else {
//            netIntent.client_energyDetail(id, new NetIntentCallBackListener(this));
//        }
//        NetIntent netIntent1 = new NetIntent();
//        if (util.getUserId() != null)
//            netIntent1.client_getCommentList(util.getUserId(), id, "1", new NetIntentCallBackListener(this));
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (util.getIsLogin()) {
//            switch (v.getId()) {
//                case R.id.btn_top:
//                    NetIntent netIntent = new NetIntent();
//                    if (!isLike) {
//                        netIntent.client_addPraise(util.getUserId(), id, "1", new NetIntentCallBackListener(InfomationFragmentActivity.this));
//                    } else {
//                        netIntent.client_deletePraise(util.getUserId(), id, "1", new NetIntentCallBackListener(this));
//                    }
//                    handler.sendEmptyMessageDelayed(3, 1000);
//                    break;
//                case R.id.btn_comment_submit:
//                    if (!edit_comment_content.getText().toString().equals("")) {
//                        NetIntent netIntent1 = new NetIntent();
//                        netIntent1.client_addComment(util.getUserId(), id, edit_comment_content.getText().toString(), "1", "", new NetIntentCallBackListener(InfomationFragmentActivity.this));
//                        handler.sendEmptyMessageDelayed(3, 1000);
//                        ToastUtils.showShortToastSafe(InfomationFragmentActivity.this,"评论成功");
//                    } else {
//                        ToastUtils.showShortToastSafe(InfomationFragmentActivity.this, "内容不能为空");
//                    }
//                    break;
//                case R.id.btn_sc://收藏
//                    NetIntent netIntent2 = new NetIntent();
//                    if (!isSc) {
//                        netIntent2.client_addContain(util.getUserId(), id, "1", new NetIntentCallBackListener(this));
//                        ToastUtils.showShortToastSafe(InfomationFragmentActivity.this, "收藏成功");
//                    } else {
//                        netIntent2.client_deleteContain(util.getUserId(), id, "1", new NetIntentCallBackListener(this));
//                        ToastUtils.showShortToastSafe(InfomationFragmentActivity.this, "取消收藏");
//                    }
//                    handler.sendEmptyMessageDelayed(3, 1000);
//                    break;
//                case R.id.btn_comment:
//                    layout_comment.setVisibility(View.VISIBLE);
//                    break;
//                case R.id.btn_share://分享
//                    Intent intent = new Intent(this, TXLActivity.class);
//                    intent.putExtra("type", 1);
//                    intent.putExtra("title", "选择分享用户");
//                    intent.putExtra("content", info_content.getText().toString());
//                    startActivity(intent);
//                    break;
//            }
//        } else {
//            startActivity(new Intent(this,LoginActivity.class));
//        }
//    }
//
//    @Override
//    public void onError(Request request, Exception e) {
//
//    }
//
//    @Override
//    public void onResponse(String response) {
//        System.out.println(response);
//        try {
//            JSONObject jsonObject = new JSONObject(response);
//            if (jsonObject.getBoolean("code")) {
//                edit_comment_content.setText("");
//                layout_comment.setVisibility(View.GONE);
//                if (jsonObject.has("energy")) {
//                    JSONArray jsonArray = new JSONArray(jsonObject.getString("energy"));
//                    json = jsonArray.getJSONObject(0);
//                    handler.sendEmptyMessage(0);
//                }
//                if (jsonObject.has("commentList")) {
//                    JSONArray jsonArray = new JSONArray(jsonObject.getString("commentList"));
//                    maps.clear();
//                    maps.addAll(Util.toHashMap(jsonArray));
//                    handler.sendEmptyMessage(1);
//                }
//            } else {
//                ToastUtils.showShortToastSafe(InfomationFragmentActivity.this,jsonObject.getString("msg"));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        String path = "";
//        for (int i = 0; i < imgs.size(); i++) {
//            path += imgs.get(i) + ",";
//        }
//        Intent intent = new Intent(this, ShowPictureActivity.class);
//        intent.putExtra("type", 1);
//        intent.putExtra("path", path);
//        intent.putExtra("index", position);
//        startActivity(intent);
//    }
//}
