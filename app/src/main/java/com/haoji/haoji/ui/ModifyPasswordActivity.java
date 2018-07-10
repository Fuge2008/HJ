package com.haoji.haoji.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.ClearEditText;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.NetUtil;
import com.haoji.haoji.util.SPUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.ToastUtils;
import com.haoji.haoji.util.Util;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;




public class ModifyPasswordActivity extends BaseActivity implements  NetIntentCallBackListener.INetIntentCallBack {
    private static String TAG = ModifyPasswordActivity.class.getSimpleName();

    @BindView(R.id.ce_input_phone)
    ClearEditText ceInputPhone;
    @BindView(R.id.ce_input_old_password)
    ClearEditText ceInputOldPassword;
    @BindView(R.id.ce_input_new_password)
    ClearEditText ceInputNewPassword;
    @BindView(R.id.ll_new_password)
    LinearLayout llNewPassword;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.btn_get_code)
    Button btnGetCode;
    @BindView(R.id.ll_confirm_code)
    LinearLayout llConfirmCode;
    @BindView(R.id.btn_modify_psw)
    Button btnModifyPsw;
    @BindView(R.id.tv_old_psw)
    TextView tvOldPsw;
    @BindView(R.id.tv_new_psw)
    TextView tvNewPsw;
    @BindView(R.id.tv_new_psw2)
    TextView tvNewPsw2;
    @BindView(R.id.ce_input_new_password2)
    ClearEditText ceInputNewPassword2;
    @BindView(R.id.ll_new_password2)
    LinearLayout llNewPassword2;
    @BindView(R.id.ll_old_password)
    LinearLayout llOldPassword;

    private String strUsername,strOldPassword ,strPassword, strPasswordAgain, strCode;
    private boolean isLogin;
    //private SmsContent content;
    private ProgressDialog progressDialog;
    private int i = 120;
    private boolean isSend = false;
    private int requset_type=0;
    private SharedPreferencesUtil util;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (i == 0) {
                        btnGetCode.setText("重新发送");
                        isSend = false;


                    } else {
                        btnGetCode.setText("(" + (i--) + ")已发送");
                        sendCond();
                    }

                    break;

                default:
                    break;
            }
        };
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_modify_password);
        ButterKnife.bind(this);
        util=new SharedPreferencesUtil(this);

    }

    @Override
    public void initUi() {

        findViewById(R.id.head_more).setVisibility(View.GONE);
        isLogin=util.getIsLogin();

        if (isLogin) {
            Util.setHeadTitleMore(this,"修改密码",true);
            llConfirmCode.setVisibility(View.GONE);
            llNewPassword2.setVisibility(View.VISIBLE);
            tvOldPsw.setText("旧密码：   ");
            tvNewPsw.setText("新密码：   ");
            tvNewPsw2.setText("确认密码：");
            ceInputPhone.setText(util.getMobilePhone());
        } else if (!isLogin) {
            Util.setHeadTitleMore(this,"忘记密码",true);
            llConfirmCode.setVisibility(View.VISIBLE);
            llNewPassword2.setVisibility(View.GONE);
            tvOldPsw.setText("设置密码:   ");
            tvNewPsw.setText("确认密码： ");
        }


    }

    @Override
    public void loadData() {
//        content = new SmsContent(new Handler());// 注册短信变化监听
//        this.getContentResolver().registerContentObserver(
//                Uri.parse("content://sms/"), true, content);

    }


    private void getCode() {

        strUsername = ceInputPhone.getText().toString();

        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtils.showShortToastSafe(ModifyPasswordActivity.this,
                    "您的设备尚未连接到网络！");
            return;
        } else {
            if (StringUtils.isPhone(strUsername)) {
                // 显示验证码获取倒计时
                handler.sendEmptyMessage(1);
                isSend = true;
                i = 120;
                requset_type=0;
                // 获取验证码
                NetIntent netIntent=new NetIntent();
                netIntent.client_sendSms(ceInputPhone.getText().toString(),new NetIntentCallBackListener(this));


            } else {
                ToastUtils.showShortToastSafe(ModifyPasswordActivity.this,
                        "请输入11位的手机号码！");
                return;
            }
        }

    }
    private void modifyPassword() {

        if (!NetUtil.isNetworkConnected(this)) {
            ToastUtils.showShortToastSafe(this, "您的设备尚未连接到网络！");
        } else {
            strUsername = ceInputPhone.getText().toString();
            strOldPassword = ceInputOldPassword.getText().toString();
            strPassword = ceInputNewPassword.getText().toString();
            strPasswordAgain = ceInputNewPassword2.getText().toString();
            if (isLogin) {// 已经登录：只需输入用户名+旧密码+新密码->修改成功

                if (StringUtils.isPhone(strUsername)
                        && (StringUtils.equals(strPassword, strPasswordAgain)
                        && (StringUtils.isNotEmpty(strPasswordAgain, true)
                        &&(StringUtils.getLength(strOldPassword,true))>=6
                        &&(StringUtils.getLength(strPassword, true) >= 6
                        && 16 >= StringUtils.getLength(strPassword, true))))) {
                //TODO  已登录改密码
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("正在修改...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                    requset_type=1;
                    NetIntent netIntent = new NetIntent();
                    netIntent.client_updatePwd(ceInputPhone.getText().toString()
                            , ceInputNewPassword.getText().toString()
                            , ceInputOldPassword.getText().toString()
                            , new NetIntentCallBackListener(this));
                    //LogUtils.i("请求信息："+ceInputPhone.getText().toString()+":  "+ceInputNewPassword.getText().toString()+":  "+ceInputOldPassword.getText().toString()+":  ");

                } else if (!StringUtils.isPhone(strUsername)) {
                    ToastUtils.showShortToastSafe(this, "请输入11位的手机号码！");
                } else if (!StringUtils.isNotEmpty(strPasswordAgain, true)
                        || !StringUtils.isNotEmpty(strPassword, true)) {
                    ToastUtils.showShortToastSafe(this, "密码不能为空！");
                } else if (!StringUtils.equals(strPassword, strPasswordAgain)) {
                    ToastUtils.showShortToastSafe(this, "两次输入的密码不相同！");
                } else if (StringUtils.getLength(strPassword, true) < 6 || 16 <StringUtils
                        .getLength(strPassword, true)||StringUtils.getLength(strOldPassword,true)<6) {
                    ToastUtils.showShortToastSafe(this, "请输入6~16位的密码！");
                }

            } else if (!isLogin) {// 没有登：需要先输入用户名->请求验证码->用户名+验证码+新密码+确认新密码->修改成功
                strCode = etCode.getText().toString();
                if (StringUtils.isPhone(strUsername)
                        && (StringUtils.isNumber(strCode)
                        && (StringUtils.equals(strPassword, strOldPassword)
                        && (StringUtils.isNotEmpty(strOldPassword, true)
                        &&(StringUtils.getLength(strPassword, true) >= 6
                        && 16 >= StringUtils.getLength(strPassword, true)))))) {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("正在修改...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                    requset_type=2;
                    //TODO  未登录改密码
                    NetIntent netIntent = new NetIntent();
                    netIntent.client_forgetPwd(ceInputPhone.getText().toString()
                            , etCode.getText().toString()
                            , ceInputNewPassword.getText().toString()
                            , new NetIntentCallBackListener(this));


                } else if (!StringUtils.isPhone(strUsername)) {
                    ToastUtils.showShortToastSafe(this, "请输入11位的手机号码！");
                } else if (!StringUtils.isNotEmpty(strOldPassword, true)
                        || !StringUtils.isNotEmpty(strPassword, true)) {
                    ToastUtils.showShortToastSafe(this, "密码不能为空！");
                } else if (!StringUtils.equals(strPassword, strOldPassword)) {
                    ToastUtils.showShortToastSafe(this, "两次输入的密码不相同！");
                } else if (StringUtils.getLength(strPassword, true) < 6 || 16 <StringUtils
                        .getLength(strPassword, true)) {
                    ToastUtils.showShortToastSafe(this, "请输入6~16位的密码！");
                }
            }
        }

    }

    private void sendCond() {
        handler.sendEmptyMessageDelayed(1, 1000);
    }

    @Override
    public void onError(Request request, Exception e) {
        if( progressDialog!=null){
            progressDialog.dismiss();
        }

    }

    @Override
    public void onResponse(String response) {
        if( progressDialog!=null){
            progressDialog.dismiss();
        }
        try {
            JSONObject jsonObject = new JSONObject(response);
            ToastUtils.showShortToastSafe(ModifyPasswordActivity.this, jsonObject.getString("msg"));
            if(requset_type==1){
                util.setPassWord(ceInputNewPassword.getText().toString());
                startActivity(new Intent(ModifyPasswordActivity.this,MainActivity.class));
            }else if(requset_type==2){

                SPUtils.put(ModifyPasswordActivity.this, "is_save_psw", false);
                SPUtils.put(ModifyPasswordActivity.this, "is_login_self", false);
                startActivity(new Intent(ModifyPasswordActivity.this,LoginActivity.class));
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.btn_get_code, R.id.btn_modify_psw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_get_code:
                if (!isSend) {
                    getCode();
                }
                break;
            case R.id.btn_modify_psw:
                if(isLogin){
                    modifyPassword();
                }else{
                    if(StringUtils.isNumber(etCode.getText().toString())){
                        modifyPassword();
                    }else{
                        ToastUtils.showShortToastSafe(this, "验证码格式不正确！");
                    }
                    break;
                }
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
//                if (sms != null && etCode != null) {
//                    etCode.setText(sms);
//                }
//
//            }
//            if (Build.VERSION.SDK_INT < 14) {
//                cursor.close();
//            }
//        }
//    }
}
