package com.haoji.haoji.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.util.TxtUtils;
import com.haoji.haoji.util.Util;

import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HaojiLagelActivity extends BaseActivity {
    private static String TAG = HaojiLagelActivity.class.getSimpleName();
    @BindView(R.id.tv_haoji_lage)
    TextView tvHaojiLage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_haoji_lagel);
        ButterKnife.bind(this);
        Util.setHeadTitleMore(this, "好记软件服务条款", true);


    }

    @Override
    public void initUi() {
        findViewById(R.id.head_more).setVisibility(View.GONE);
        InputStream inputStream = getResources().openRawResource(R.raw.haoji_lagel);
        String string = TxtUtils.getString(inputStream);
        tvHaojiLage.setText(string);

    }

    @Override
    public void loadData() {

    }


}
