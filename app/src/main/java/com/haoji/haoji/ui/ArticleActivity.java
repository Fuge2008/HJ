package com.haoji.haoji.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.haoji.haoji.R;
import com.haoji.haoji.adapter.FragAdapter;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.fragment.tab2.BookFragment;
import com.haoji.haoji.util.Util;

import java.util.ArrayList;


/**
 * 日记详情
 */

public class ArticleActivity extends BaseActivity {
    private static String TAG = ArticleActivity.class.getSimpleName();

    private String[] id;
    private int position;
    private ViewPager viewPager;
    private FragAdapter fragAdapter;
    private ArrayList<Fragment> fragments=new ArrayList<Fragment>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_info_video);

    }
    @Override
    public void initUi() {
        id = this.getIntent().getStringExtra("id").split(",");
        position=this.getIntent().getIntExtra("position",0);
        Util.setHeadTitleMore(this, "详情", true);
        findViewById(R.id.head_more).setVisibility(View.GONE);
        viewPager= (ViewPager) findViewById(R.id.viewpager);
        for (String s : id) {
            fragments.add(new BookFragment(s));
        }
        fragAdapter=new FragAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(fragAdapter);
        viewPager.setCurrentItem(position);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
