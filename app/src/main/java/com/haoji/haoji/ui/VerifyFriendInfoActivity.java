package com.haoji.haoji.ui;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.ClearEditText;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.ToastUtils;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VerifyFriendInfoActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack{


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.ce_verify_info)
    ClearEditText ceVerifyInfo;
   private String verifyInfo,hjid;
    private SharedPreferencesUtil util;

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_verify_friend_info);
        ButterKnife.bind(this);

    }

    @Override
    public void initUi() {
        util=new SharedPreferencesUtil(this);
        hjid = this.getIntent().getStringExtra("hjid");
        ceVerifyInfo.setText( "我是"+util.getNickName());
    }

    @Override
    public void loadData() {

    }
    @OnClick({R.id.iv_back, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_send:
                verifyInfo=ceVerifyInfo.getText().toString();
                if(StringUtils.isNotEmpty(verifyInfo,true)){
                    NetIntent netIntent = new NetIntent();//TODO  添加好友
                    netIntent.client_addFriend(util.getUserId(), hjid, verifyInfo,new NetIntentCallBackListener(this));
                    ToastUtils.showShortToastSafe(VerifyFriendInfoActivity.this,"发送请求成功");
                }
                //TODO 请求添加好友

                break;
        }
    }

    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        JSONObject jsonObject= null;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.has("msg"))
                ToastUtils.showShortToastSafe(VerifyFriendInfoActivity.this,jsonObject.getString("msg"));
            finish();
        } catch (JSONException e) {


        }

    }
}
