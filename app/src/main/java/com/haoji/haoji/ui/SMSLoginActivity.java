package com.haoji.haoji.ui;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.ClearEditText;
import com.haoji.haoji.custom.CustomProgress;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.NetUtil;
import com.haoji.haoji.util.SPUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SMSLoginActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack{

    @BindView(R.id.ce_input_username)
    ClearEditText ceInputUsername;
    @BindView(R.id.et_input_code)
    EditText etInputCode;
    @BindView(R.id.btn_get_code)
    Button btnGetCode;
    @BindView(R.id.btn_login)
    Button btnLogin;
    private String strUsername, strPassword;
    private CustomProgress dialog;
    private JSONObject json;
    private SharedPreferencesUtil util;
    private int i = 120;
    private boolean isSend = false;
    //private SmsContent content;
    //private SmsUtil content;
    int requst_type=0;
    private final  int SUCCESS=2;
//    private static String CODE = "您的验证码是：";
//
//    private static String SENTENCE = "请不要把验证码泄露给其他人。";
//
//    private SmsUtil content;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (i == 0) {
                        btnGetCode.setText("重新发送");
                        isSend = false;

                    } else {
                        btnGetCode.setText("(" + (i--) + ")已发送");
                        sendCode();
                    }

                    break;
                case SUCCESS:
                    etInputCode.setText(String.valueOf(msg.obj));
                    break;

                default:
                    break;
            }
        };
    };
    private void sendCode() {
        handler.sendEmptyMessageDelayed(1, 1000);
    }

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_smslogin);
        ButterKnife.bind(this);
        util = new SharedPreferencesUtil(this);
        Util.setHeadTitleMore(this, "短信登录", true);
        findViewById(R.id.head_more).setVisibility(View.GONE);

    }

    @Override
    public void initUi() {
//        content = new SmsContent(new Handler());// 注册短信变化监听
//        this.getContentResolver().registerContentObserver(
//                Uri.parse("content://sms/"), true, content);
//        // 调用短信获取工具类
//        content = new SmsUtil(this, handler, CODE, SENTENCE);
//        // 注册内容观察者
//        this.getContentResolver().registerContentObserver(
//                Uri.parse("content://sms/"), true, content);

    }

    @Override
    public void loadData() {

    }

    @OnClick({R.id.btn_get_code, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_code:
                if (!isSend) {
                    getCode();
                }
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }
    private void login() {
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtils.showShortToastSafe(SMSLoginActivity.this, "您的设备尚未连接到网络！");
            return;
        } else {
            strUsername = ceInputUsername.getText().toString();
            strPassword = etInputCode.getText().toString();
            if (StringUtils.isPhone(strUsername)
                    && (StringUtils.getLength(strPassword, true) == 6)&&StringUtils.isNumber(strPassword)) {
                dialog = CustomProgress.show(this, "登录中..", true, null);
            new NetIntent().client_phoneLogin(strUsername,strPassword, new NetIntentCallBackListener(this));

            } else if (!StringUtils.isPhone(strUsername)) {
                ToastUtils.showShortToastSafe(SMSLoginActivity.this,
                        "请输入11位的手机号码！");
                return;

            } else if ((StringUtils.getLength(strPassword, true) == 6)||StringUtils.isNumber(strPassword)) {
                ToastUtils.showShortToastSafe(SMSLoginActivity.this, "验证码短信格式不正确！");
                return;
            }

        }

    }
    private void getCode() {
        strUsername = ceInputUsername.getText().toString();
        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtils.showShortToastSafe(SMSLoginActivity.this, "您的设备尚未连接到网络！");
            return;
        } else {
            if (StringUtils.isPhone(strUsername)) {
                // 设置显示倒计时
                handler.sendEmptyMessage(1);
                isSend = true;
                i = 120;
                requst_type=0;
                // 获取验证码
                NetIntent netIntent1 = new NetIntent();
                netIntent1.client_sendSms( strUsername, new NetIntentCallBackListener(new NetIntentCallBackListener.INetIntentCallBack() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            if (jsonObject.getBoolean("code")){
                                ToastUtils.showShortToastSafe(SMSLoginActivity.this,"发送成功");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }));

            } else if (!StringUtils.isPhone(strUsername)) {
                ToastUtils.showShortToastSafe(this,
                        "请输入11位的手机号码！");
                return;
            }
        }
    }

    @Override
    public void onError(Request request, Exception e) {
        if (dialog != null)
            dialog.dismiss();
    }
        
    

    @Override
    public void onResponse(String response) {
        if (dialog != null)
            dialog.dismiss();
        try {
            JSONObject jsonObject = new JSONObject(response);
            LogUtils.i("短信登录路返回数据"+response.toString());
            if (jsonObject.getBoolean("code")) {
                JSONArray jsonArray = new JSONArray(jsonObject.getString("list"));
                json = jsonArray.getJSONObject(0);
                saveUser();

            }else {
                ToastUtils.showShortToast(SMSLoginActivity.this,jsonObject.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void saveUser() {
        try {
            util.setUserId(json.getString("userid"));
            util.setUserName(json.getString("username"));
            util.setPassWord("");
            util.setMobilePhone(json.getString("phone"));
            util.setIsLogin(true);
            if(StringUtils.isNotEmpty(json.getString("motto"),true)){
              util.setNickName(json.getString("motto"));
            }else {
                util.setNickName(getResources().getString(R.string.init_nickname));
            }
            if(StringUtils.isNotEmpty(json.getString("motto"),true)){
                util.setPicture(json.getString("pic"));
            }else {
                util.setPicture(getResources().getString(R.string.init_picture_path));
            }
            if(StringUtils.isNotEmpty(json.getString("motto"),true)){
                util.setMotto(json.getString("motto"));
            }else {
                util.setMotto(getResources().getString(R.string.init_motto));
            }
            SPUtils.put(SMSLoginActivity.this, "username1", json.getString("userid").substring(14, 25));

            SPUtils.put(SMSLoginActivity.this, "password1","");
            LogUtils.i("info", "返回数据----->" + json.getString("userid"));
           startActivity(new Intent(SMSLoginActivity.this,MainActivity.class));
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
//    /**
//     * 监听短信数据库
//     */
//    class SmsContent extends ContentObserver {
//        private Cursor cursor = null;
//
//        public SmsContent(Handler handler) {
//            super(handler);
//        }
//
//        @Override
//        public void onChange(boolean selfChange) {
//            super.onChange(selfChange);
//            String listenerPhone = "10698000171101135848"; // 监听的号码
//            cursor = managedQuery(Uri.parse("content://sms/inbox"),
//                    new String[]{"_id", "address", "read", "body"},
//                    " address=? and read=?",
//                    new String[]{listenerPhone, "0"}, "_id desc");
//            if (cursor != null && cursor.getCount() > 0) {
//                ContentValues values = new ContentValues();
//                values.put("read", "1");
//                cursor.moveToNext();
//                int smsbodyColumn = cursor.getColumnIndex("body");
//                String smsBody = cursor.getString(smsbodyColumn);
//
//                Pattern continuousNumberPattern = Pattern.compile("(?<![0-9])([0-9]{"
//                        + 6 + "})(?![0-9])");
//                Matcher m = continuousNumberPattern.matcher(smsBody);
//                String dynamicPassword = "";
//                while (m.find()) {
//                    System.out.print(m.group());
//                    dynamicPassword = m.group();
//                }
//                String sms = dynamicPassword;
//                if (sms != null ) {
//                    etInputCode.setText(sms);
//                }
//
//            }
//            if (Build.VERSION.SDK_INT < 14) {
//                cursor.close();
//            }
//        }
//    }
}
