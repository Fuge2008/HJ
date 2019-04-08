//package com.zhulei.haoji.ui;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.squareup.okhttp.Request;
//import com.zhulei.haoji.R;
//import BaseActivity2;
//import CustomProgress;
//import NetIntent;
//import NetIntentCallBackListener;
//import NetIntentCallBackListener.INetIntentCallBack;
//import SharedPreferencesUtil;
//import SystemDialog;
//import Util;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//
//
//public class UserInfoActivity extends BaseActivity2 implements View.OnClickListener,INetIntentCallBack {
//
//    private static String TAG = UserInfoActivity .class.getSimpleName();
//    private EditText phone, nickname, realname, edit_phone;
//    private EditText school, zy, birthday;
//    private TextView sex;
//    private SharedPreferencesUtil util;
//    private CustomProgress dialog;
//    private ImageView upload_img;
//    private JSONObject json;
//    private String userid;
//    private LinearLayout layout_del,layout_add;
//    //0 好友详情 1陌生人
//    private int type=0;
//
//    Handler handler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    updateView();
//                    break;
//            }
//        }
//    };
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
//        setContentView(R.layout.activity_userinfo);
//        Util.setHeadTitleMore(this, "好友详情", true);
//        type=this.getIntent().getIntExtra("type",0);
//        findViewById(R.id.head_more).setVisibility(View.GONE);
//        userid = this.getIntent().getStringExtra("userid");
//
//    }
//
//    @Override
//    public void initUi() {
//        dialog = CustomProgress.show(this, "加载中..", true, null);
//        util = new SharedPreferencesUtil(this);
//        upload_img = (ImageView) findViewById(R.id.upload_img);
//        phone = (EditText) findViewById(R.id.phone);
//        nickname = (EditText) findViewById(R.id.nickname);
//        realname = (EditText) findViewById(R.id.realname);
//        school = (EditText) findViewById(R.id.school);
//        zy = (EditText) findViewById(R.id.zy);
//        sex = (TextView) findViewById(R.id.sex);
//        birthday = (EditText) findViewById(R.id.birthday);
//        edit_phone = (EditText) findViewById(R.id.edit_phone);
//        findViewById(R.id.btn_del).setOnClickListener(this);
//        findViewById(R.id.btn_add_friend).setOnClickListener(this);
//        findViewById(R.id.btn_add_concern).setOnClickListener(this);
//        layout_add= (LinearLayout) findViewById(R.id.layout_add);
//        layout_del= (LinearLayout) findViewById(R.id.layout_del);
//        if (type==0){
//            layout_add.setVisibility(View.GONE);
//        }
//        else if (type==1){
//            layout_del.setVisibility(View.GONE);
//        }
//        new NetIntent().client_getUserById(userid,new NetIntentCallBackListener(this));
//
//
//    }
//
//    @Override
//    public void loadData() {
//
//    }
//
//
//    public void updateView() {
//        try {
//            Util.ImageLoaderToPicAuto(this, json.getString("pic"), upload_img);
//            upload_img.setOnClickListener(this);
//            phone.setText(json.getString("phone"));
//            nickname.setText(json.getString("nickname"));
//            realname.setText(json.getString("realname"));
//            school.setText(json.getString("university"));
//            zy.setText(json.getString("major"));
//            birthday.setText(json.getString("birthday"));
//            sex.setText(Util.getSexForString(json.getString("sex")));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btn_del:
//                new NetIntent().client_deleteFriend(util.getUserId(),userid,new NetIntentCallBackListener(this));
//                break;
//            case R.id.upload_img:
//                try {
//                    Intent intent = new Intent(this, ShowPictureActivity.class);
//                    intent.putExtra("type", 1);
//                    intent.putExtra("path", json.getString("pic"));
//                    startActivity(intent);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//                break;
//            case R.id.btn_add_friend:
//                try {
//                    NetIntent netIntent = new NetIntent();
//                    netIntent.client_addFriend(util.getUserId(), json.getString("userid"), new NetIntentCallBackListener(this));
//                    SystemDialog.DialogToast(getApplicationContext(),"发送请求成功");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case R.id.btn_add_concern:
//                try {
//                    new NetIntent().client_addConcern(util.getUserId(),json.getString("userid").toString(),new NetIntentCallBackListener(this));
//                    SystemDialog.DialogToast(getApplicationContext(),"关注成功");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
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
//        Log.d(TAG, "onResponse: "+response);
//        if (dialog != null)
//            dialog.dismiss();
//        System.out.println(response);
//        try {
//            JSONObject jsonObject=new JSONObject(response);
//            if (jsonObject.has("msg"))
//                SystemDialog.DialogToast(getApplicationContext(),jsonObject.getString("msg"));
//            if (jsonObject.has("user")) {
//                json = jsonObject.getJSONObject("user");
//                handler.sendEmptyMessage(0);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//}
