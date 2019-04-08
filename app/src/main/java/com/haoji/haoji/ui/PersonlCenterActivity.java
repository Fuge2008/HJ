//package com.zhulei.haoji.ui;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.support.v4.content.LocalBroadcastManager;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RadioGroup;
//
//import com.squareup.okhttp.Request;
//import com.zhulei.haoji.R;
//import BaseActivity2;
//import Constants;
//import CustomProgress;
//import DateTimePickDialogUtil;
//import NetIntent;
//import NetIntentCallBackListener;
//import MyDate;
//import SharedPreferencesUtil;
//import SystemDialog;
//import Util;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
///**
// * Created by Administrator on 2017/1/19.
// */
//
//public class PersonlCenterActivity extends BaseActivity2 implements View.OnClickListener{
//    private static String TAG = PersonlCenterActivity.class.getSimpleName();
//    private EditText phone, nickname, realname;
//    private EditText school, zy, birthday;
//    private SharedPreferencesUtil util;
//    private ImageView upload_img;
//    private ArrayList list = new ArrayList();
//    private String newFileName;
//    private CustomProgress dialog;
//    private String universityid;
//    private RadioGroup layout_sex;
//    private String sex;
//    private String majorid;
//    private String collegeid;
//
//    private LocalBroadcastManager localBroadcastManager;
//    private IntentFilter intentFilter;
//
//
//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(Constants.Action_select_school)) {
//                school.setText(intent.getStringExtra("text"));
//                universityid = intent.getStringExtra("universityid");
//            } else if (intent.getAction().equals(Constants.Action_select_zy)) {
//                zy.setText(intent.getStringExtra("text"));
//                majorid = intent.getStringExtra("majorid");
//                collegeid=intent.getStringExtra("collegeid");
//            }
//        }
//    };
//    Handler handler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    dialog = CustomProgress.show(PersonlCenterActivity.this, "处理中..", true, null);
//                    NetIntent netIntent = new NetIntent();
//                    netIntent.client_uploadImg(list.get(0).toString(), new NetIntentCallBackListener(PersonlCenterActivity.this));
//                    break;
//                case 1:
//                    Intent intent = new Intent();
//                    intent.setAction(Constants.Action_update);
//                    LocalBroadcastManager.getInstance(PersonlCenterActivity.this).sendBroadcast(intent);
//                    SystemDialog.DialogToast(getApplicationContext(),"更新成功");
//                    finish();
//                    break;
//            }
//        }
//    };
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//    }
//
//    @Override
//    public void initMainView() {
//        setContentView(R.layout.activity_personlcenter);
//        util = new SharedPreferencesUtil(this);
//        Util.setHeadTitleMore(this, "个人资料", true);
//
//    }
//
//    @Override
//    public void initUi() {
//
//        findViewById(R.id.head_more).setVisibility(View.GONE);
//        findViewById(R.id.submit).setOnClickListener(this);
//        upload_img = (ImageView) findViewById(R.id.upload_img);
//        upload_img.setOnClickListener(this);
//        phone = (EditText) findViewById(R.id.phone);
//        nickname = (EditText) findViewById(R.id.nickname);
//        realname = (EditText) findViewById(R.id.realname);
//        school = (EditText) findViewById(R.id.school);
//        zy = (EditText) findViewById(R.id.zy);
//        birthday = (EditText) findViewById(R.id.birthday);
//        Util.ImageLoaderToPicAuto(this, util.getPicture(), upload_img);
//        birthday.setText(util.getBirthday());
//        phone.setText(util.getMobilePhone());
//        nickname.setText(util.getNickName());
//        school.setText(util.getUniversity());
//        zy.setText(util.getCollege());
//        realname.setText(util.getRealName());
//        findViewById(R.id.btn_select_school).setOnClickListener(this);
//        findViewById(R.id.btn_select_zy).setOnClickListener(this);
//        findViewById(R.id.btn_select_date).setOnClickListener(this);
//        layout_sex = (RadioGroup) findViewById(R.id.layout_sex);
//
//        localBroadcastManager = LocalBroadcastManager.getInstance(this);
//        intentFilter = new IntentFilter();
//        intentFilter.addAction(Constants.Action_select_school);
//        intentFilter.addAction(Constants.Action_select_zy);
//        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
//
//
//    }
//
//    @Override
//    public void loadData() {
//        layout_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.m) {
//                    sex = "1";
//                } else {
//                    sex = "2";
//                }
//                System.out.println("group:" + sex);
//            }
//        });
//        if (util.getSexInt() == 1) {
//            layout_sex.check(R.id.m);
//        } else {
//            layout_sex.check(R.id.w);
//        }
//
//    }
//
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.upload_img:
// //               CommonUtils.launchActivityForResult(this, PhotoSelectorActivity.class, 0);
//                break;
//            case R.id.submit:
//                dialog = CustomProgress.show(PersonlCenterActivity.this, "上传中..", true, null);
//                NetIntent netIntent = new NetIntent();
//                netIntent.client_updateUserById(util.getUserId(), sex, nickname.getText().toString(), realname.getText().toString(), util.getMobilePhone(), birthday.getText().toString(), phone.getText().toString()
//                        , newFileName, universityid,collegeid, majorid, new NetIntentCallBackListener(this));
//                break;
//            case R.id.btn_select_school:
//                Intent intent = new Intent(this, CitySelectActivity.class);
//                intent.putExtra("type", 1001);
//                startActivityForResult(intent, 1001);
//                break;
//            case R.id.btn_select_zy:
//                if (universityid != null) {
//                    Intent intent1 = new Intent(this, SelectActivity.class);
//                    intent1.putExtra("type", 1002);
//                    intent1.putExtra("universityid", universityid);
//                    startActivityForResult(intent1, 1002);
//                } else {
//                    SystemDialog.DialogToast(getApplicationContext(), "请先选择学校");
//                }
//                break;
//            case R.id.btn_select_date:
//                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(this, MyDate.getDateCNNotSS());
//                dateTimePicKDialog.dateTimePicKDialog(birthday);
//                break;
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 0 && resultCode == RESULT_OK) {
//            if (data != null && data.getExtras() != null) {
//
////                List<PhotoModel> photos = (List<PhotoModel>) data.getExtras().getSerializable("photos");
////                if (photos == null || photos.isEmpty())
////                    return;
////                for (PhotoModel photo : photos) {
////                    list.add(photo.getOriginalPath());
////                }
//                handler.sendEmptyMessage(0);
//            }
//        }
//    }
//
//    @Override
//    public void onError(Request request, Exception e) {
//        if (dialog == null) {
//            dialog.dismiss();
//        }
//    }
//
//    @Override
//    public void onResponse(String response) {
//        System.out.println(response);
//        if (dialog != null) {
//            dialog.dismiss();
//        }
//        try {
//            JSONObject jsonObject = new JSONObject(response);
//            if (jsonObject.has("newFileName")) {
//                newFileName = jsonObject.getString("newFileName");
//            } else {
//                handler.sendEmptyMessage(1);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//}
