package com.haoji.haoji.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.ClearEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddNicknameActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.re_avatar)
    RelativeLayout reAvatar;
    @BindView(R.id.ce_input_nickname)
    ClearEditText ceInputNickname;
    @BindView(R.id.ce_input_recommend)
    ClearEditText ceInputRecommend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nickname);
        ButterKnife.bind(this);
    }

    @Override
    public void initMainView() {

    }

    @Override
    public void initUi() {

    }

    @Override
    public void loadData() {

    }

    @OnClick({R.id.iv_back, R.id.btn_send, R.id.re_avatar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                break;
            case R.id.btn_send:
                break;
            case R.id.re_avatar:
                break;
        }
    }
}
