package com.haoji.haoji.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.comment.UserDetailsActivity;
import com.haoji.haoji.util.Util;




public class ConversationActivity extends BaseActivity {
    private static String TAG = ConversationActivity.class.getSimpleName();

    private String targetId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_conversation);
        targetId=this.getIntent().getData().getQueryParameter("targetId");

    }

    @Override
    public void initUi() {
        Util.setHeadTitleMore(this,this.getIntent().getData().getQueryParameter("title"),true);
        findViewById(R.id.head_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(ConversationActivity.this,UserInfoActivity.class);
//                intent.putExtra("userid",targetId);
//                startActivity(intent);
               startActivity(new Intent(ConversationActivity.this,UserDetailsActivity.class).putExtra("userid",targetId).putExtra("phone","0"));
            }
        });
    }

    @Override
    public void loadData() {

    }


}
