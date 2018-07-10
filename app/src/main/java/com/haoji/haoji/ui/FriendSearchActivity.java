package com.haoji.haoji.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.adapter.NewFriendAdapter;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.comment.UserDetailsActivity;
import com.haoji.haoji.custom.ClearEditText;
import com.haoji.haoji.custom.CustomProgress;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class FriendSearchActivity extends BaseActivity implements View.OnClickListener, NetIntentCallBackListener.INetIntentCallBack {
    private static String TAG = FriendSearchActivity.class.getSimpleName();
    private ClearEditText ce_input_phone;
    private TextView tv_sumbit;
    private SharedPreferencesUtil util;
   // private ImageView upload_img;
    private JSONObject json;
    private ListView listView_new_friend;
    private NewFriendAdapter adapter;
    private ArrayList<HashMap> maps = new ArrayList<HashMap>();

    private CustomProgress dialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_friend_search);
        util = new SharedPreferencesUtil(this);
        Util.setHeadTitleMore(this, "搜索好友", true);
        findViewById(R.id.head_more).setVisibility(View.GONE);

    }

    @Override
    public void initUi() {
        dialog = CustomProgress.show(this, "加载中..", true, null);
        //upload_img = (ImageView) findViewById(R.id.upload_img);
       // upload_img.setOnClickListener(this);

        tv_sumbit = (TextView) findViewById(R.id.tv_submit);
        tv_sumbit.setOnClickListener(this);

        ce_input_phone= (ClearEditText) findViewById(R.id.ce_input_phone);




        listView_new_friend = (ListView) findViewById(R.id.listView_new_friend);
        adapter = new NewFriendAdapter(this, maps, listener);
        listView_new_friend.setAdapter(adapter);
        if (util.getIsLogin()) {
            NetIntent netIntent = new NetIntent();
            netIntent.client_getSysInfo(util.getUserId(), new NetIntentCallBackListener(this));
        } else {
            ToastUtils.showShortToastSafe(FriendSearchActivity.this, "您未登录,请登录后再试");
            finish();
        }
        ce_input_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(StringUtils.isNotEmpty(ce_input_phone.getText().toString(),true)){
                    tv_sumbit.setVisibility(View.VISIBLE);
                }else{
                    tv_sumbit.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @Override
    public void loadData() {

    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    startActivity(new Intent(FriendSearchActivity.this,UserDetailsActivity.class).putExtra("phone",ce_input_phone.getText().toString()));
                    break;
                case 1:
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    new NetIntent().client_getSysInfo(util.getUserId(), new NetIntentCallBackListener(FriendSearchActivity.this));
                    break;
            }
        }
    };





    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            HashMap map = (HashMap) v.getTag();
            NetIntent netIntent = new NetIntent();
            switch (v.getId()) {
                case R.id.friend_ty:
                    netIntent.client_agreeAddFriend(util.getUserId(),
                            map.get("id").toString(),
                            map.get("other").toString(),
                            ty);
                    handler.sendEmptyMessage(2);
                    break;
                case R.id.friend_jj:
                    netIntent.client_disagreeAddFriend(util.getUserId(),
                            map.get("id").toString(),
                            ty);
                    handler.sendEmptyMessage(2);
                    break;

            }
        }
    };

    private NetIntentCallBackListener ty = new NetIntentCallBackListener(new NetIntentCallBackListener.INetIntentCallBack() {
        @Override
        public void onError(Request request, Exception e) {

        }

        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                ToastUtils.showShortToastSafe(FriendSearchActivity.this,jsonObject.getString("msg"));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    });

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_submit:
                if (StringUtils.isPhone(ce_input_phone.getText().toString())) {
                    dialog = CustomProgress.show(this, "加载中..", true, null);
                    NetIntent netIntent = new NetIntent();
                    netIntent.client_getUserByPhone(ce_input_phone.getText().toString(), new NetIntentCallBackListener(this));
                }else{
                    ToastUtils.showShortToast(FriendSearchActivity.this,"请输入11位的手机号");
                }
                break;
//            case R.id.btn_add_friend:
//                try {
//                    NetIntent netIntent = new NetIntent();
//                    netIntent.client_addFriend(util.getUserId(), json.getString("userid"), new NetIntentCallBackListener(this));
//                    SystemDialog.DialogToast(getApplicationContext(),"发送请求成功");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case R.id.btn_send_chat:
//                try {
//                    if (RongIM.getInstance() != null) {
//                        RongIM.getInstance().startPrivateChat(this, json.getString("userid"), json.getString("nickname"));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
        }
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
            if (jsonObject.has("user")) {
                json = jsonObject.getJSONObject("user");
                handler.sendEmptyMessage(0);
            }
            if (jsonObject.has("sysInfoList")) {
                maps.clear();
                maps.addAll(Util.toHashMap(new JSONArray(jsonObject.getString("sysInfoList"))));
                handler.sendEmptyMessage(1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
