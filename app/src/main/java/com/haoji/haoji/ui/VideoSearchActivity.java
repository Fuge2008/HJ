package com.haoji.haoji.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.util.Util;


/**
 * 视频搜索页面-功能暂无
 */

public class VideoSearchActivity extends BaseActivity implements View.OnClickListener {
    private static String TAG = VideoSearchActivity.class.getSimpleName();

    private String title;
    private EditText edit_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_video_search);
        title = this.getIntent().getStringExtra("title");
        //SystemUtil.showSystemBarTint(this, true);
        Util.setHeadTitleMore(this, title, true);

    }

    @Override
    public void initUi() {
        edit_content = (EditText) findViewById(R.id.edit_content);
        findViewById(R.id.btn_submit).setOnClickListener(this);
        findViewById(R.id.head_more).setVisibility(View.GONE);

    }

    @Override
    public void loadData() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                Intent intent = new Intent();
                intent.putExtra("tag", edit_content.getText().toString());
                setResult(1, intent);
                finish();
                break;
        }
    }

}
