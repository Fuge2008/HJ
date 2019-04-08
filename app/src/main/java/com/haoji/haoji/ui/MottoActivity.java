//package com.haoji.haoji.ui;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.support.v4.content.LocalBroadcastManager;
//import android.view.View;
//import android.widget.EditText;
//
//import com.haoji.haoji.R;
//import com.haoji.haoji.base.BaseActivity2;
//import com.haoji.haoji.common.Constants;
//import com.haoji.haoji.network.NetIntent;
//import com.haoji.haoji.network.NetIntentCallBackListener;
//import com.haoji.haoji.util.SharedPreferencesUtil;
//import com.haoji.haoji.util.Util;
//import com.squareup.okhttp.Request;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//
//
//
//public class MottoActivity extends BaseActivity2 implements View.OnClickListener, NetIntentCallBackListener.INetIntentCallBack {
//
//    private EditText edit_motto;
//    private SharedPreferencesUtil util;
//    Handler handler=new Handler(Looper.getMainLooper()){
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case 0:
//                    Intent intent = new Intent();
//                    intent.setAction(Constants.Action_update);
//                    LocalBroadcastManager.getInstance(MottoActivity.this).sendBroadcast(intent);
//                    finish();
//                    break;
//            }
//        }
//    };
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public void initMainView() {
//        setContentView(R.layout.activity_motto);
//        util = new SharedPreferencesUtil(this);
//        Util.setHeadTitleMore(this,"修改格言",true);
//
//
//
//    }
//
//    @Override
//    public void initUi() {
//        findViewById(R.id.head_more).setVisibility(View.GONE);
//        edit_motto = (EditText) findViewById(R.id.edit_motto);
//        edit_motto.setText(util.getMotto());
//        findViewById(R.id.btn_submit).setOnClickListener(this);
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
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_submit:
//                if (!edit_motto.getText().toString().equals("")) {
//                    NetIntent netIntent = new NetIntent();
//                    netIntent.client_updateMottoById(util.getUserId(), edit_motto.getText().toString(), new NetIntentCallBackListener(this));
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
//        try {
//            JSONObject jsonObject=new JSONObject(response);
//            if (jsonObject.getBoolean("code")){
//                handler.sendEmptyMessage(0);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//}
