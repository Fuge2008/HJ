package com.haoji.haoji.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.ToastUtils;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;


public class ProfileUpdateActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack{
    private static String TAG = ProfileUpdateActivity.class.getSimpleName();
    public static final int TYPE_NICK = 0;
    public static final int TYPE_ADDRESS = 1;
    public static final int TYPE_SIGN = 2;

    private String defaultStr;
    private TextView titleTV,saveTV;
    private ImageView iv_back;
    private EditText infoET;
    private SharedPreferencesUtil util;
   private  ProgressDialog progressDialog;
    private  int MAX_COUNT = 30;
    private TextView tv_count;
    private int type;
        Handler handler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        Intent intent = new Intent(ProfileUpdateActivity.this,ProfileActivity.class);
                        intent.putExtra("value", infoET.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();

                        break;
                }
            }
        };

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_update_info);
        util=new SharedPreferencesUtil(this);
        type = getIntent().getIntExtra("type", 0);
        defaultStr = getIntent().getStringExtra("default");

    }

    @Override
    public void initUi() {

        titleTV = (TextView) findViewById(R.id.tv_title);
        saveTV = (TextView) findViewById(R.id.tv_save);
        infoET = (EditText) findViewById(R.id.et_info);
        tv_count= (TextView) findViewById(R.id.tv_count);
        infoET.addTextChangedListener(mTextWatcher);
        infoET.setSelection(infoET.length()); // 将光标移动最后一个字符后面
        iv_back = (ImageView) findViewById(R.id.iv_back);
        if(type==0){
            MAX_COUNT = 16;
            tv_count.setText(MAX_COUNT+"");
        }else if(type==1){
            MAX_COUNT = 20;
            tv_count.setText(MAX_COUNT+"");
        }else if(type==2){
            MAX_COUNT = 30;
            tv_count.setText(MAX_COUNT+"");
        }
        if (defaultStr != null) {

            infoET.setText(defaultStr);
        }
        initView(type, titleTV, saveTV, infoET);
    }

    @Override
    public void loadData() {

    }
    /**
     * 监听统计字数
     */
    private TextWatcher mTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
           int editStart = infoET.getSelectionStart();
           int editEnd = infoET.getSelectionEnd();

            // 先去掉监听器，否则会出现栈溢出
            infoET.removeTextChangedListener(mTextWatcher);

            // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
            // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
            if(calculateLength(s.toString())>=MAX_COUNT){
                ToastUtils.showShortToast(ProfileUpdateActivity.this,"字数已达"+MAX_COUNT);
            }

            while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
            }
           infoET.setText(s);
           infoET.setSelection(editStart);

            // 恢复监听器
           infoET.addTextChangedListener(mTextWatcher);

            setLeftCount();
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

    };

    /**
     * 计算分享内容的字数
     *
     * @param c
     * @return
     */
    private long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
//            if (tmp > 0 && tmp < 127) {
//                len += 0.5;
//            } else {
                len++;
        }
//        }
        return Math.round(len);
    }

    /**
     * 刷新剩余输入字数,最大值100个字
     */
    private void setLeftCount() {
        tv_count.setText(String.valueOf((MAX_COUNT - getInputCount())));
    }

    /**
     * 获取用户输入的内容字数
     *
     * @return
     */
    private long getInputCount() {
        return calculateLength(infoET.getText().toString());
    }



    private void initView(int type, TextView titleTV, TextView saveTV, final EditText infoET) {
        String title = "";
        switch (type) {
            case TYPE_NICK:
                title = "修改昵称";
                break;
            case TYPE_ADDRESS:
                title = "修改地址";
                break;
            case TYPE_SIGN:
                title = "修改个人签名";
                break;

        }
        titleTV.setText(title);
        saveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInServer(infoET.getText().toString().trim());
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void updateInServer(String value) {
        if ( TextUtils.isEmpty(value) || ((defaultStr != null) && value.equals(defaultStr))) {
            return;
        }
//        if (value.length() > 30) {
//            Toast.makeText(getApplicationContext(), "不能超过30个字符", Toast.LENGTH_SHORT).show();
//            return;
//        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在更新...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        if (type==2 & StringUtils.isNotEmpty(infoET.getText().toString(),true)) {
            NetIntent netIntent = new NetIntent();
            netIntent.client_updateMottoById(util.getUserId(), infoET.getText().toString(), new NetIntentCallBackListener(this));
        }else if(type==0 & StringUtils.isNotEmpty(infoET.getText().toString(),true)){
            NetIntent netIntent = new NetIntent();
            netIntent.client_updateUserById(util.getUserId(), "", infoET.getText().toString(),  "","", "", "",new NetIntentCallBackListener(this));

        }else if(type==1 & StringUtils.isNotEmpty(infoET.getText().toString(),true)){
            util.setAddress(infoET.getText().toString());
            progressDialog.dismiss();
            finish();
        }

    }
        @Override
        public void onError(Request request, Exception e) {
            if (progressDialog!=null){
                progressDialog.dismiss();
            }

        }

        @Override
        public void onResponse(String response) {
            if (progressDialog!=null){
                progressDialog.dismiss();
            }
            try {
                JSONObject jsonObject=new JSONObject(response);
                if (jsonObject.getBoolean("code")){
                    handler.sendEmptyMessage(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }



}