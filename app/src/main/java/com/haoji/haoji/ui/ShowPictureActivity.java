package com.haoji.haoji.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.HackyViewPager;
import com.haoji.haoji.util.Util;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;


/**
 * 图片预览
 * String path 地址
 * int type 0 服务器 1网络 2本地
 */

public class ShowPictureActivity extends BaseActivity implements View.OnClickListener,ViewPager.OnPageChangeListener{
    private static String TAG = ShowPictureActivity.class.getSimpleName();
    private HackyViewPager show_viewpager;
    private TextView show_save;
    private String path;
    private int type;//0 服务器 1网络 2本地
    private ArrayList<View> views=new ArrayList<View>();
    private ArrayList<String> paths=new ArrayList<String>();
    private TextView image_position;
    private int currentIndex=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_show_picture);
        type=this.getIntent().getIntExtra("type",0);
        path=this.getIntent().getStringExtra("path");
        currentIndex=this.getIntent().getIntExtra("index",0);

    }

    @Override
    public void initUi() {
        show_viewpager= (HackyViewPager) findViewById(R.id.show_viewpager);
        show_save= (TextView) findViewById(R.id.show_save);
        image_position= (TextView) findViewById(R.id.image_position);
        myPagerAdapter myPagerAdapter=new myPagerAdapter();
        show_viewpager.setAdapter(myPagerAdapter);
        show_viewpager.setOnPageChangeListener(this);
        show_save.setOnClickListener(this);
        String[] arr=path.split(",");
        for (String s : arr) {
            paths.add(s);
        }
        image_position.setText("1/"+paths.size());
        myPagerAdapter.notifyDataSetChanged();
        show_viewpager.setCurrentItem(currentIndex);

    }

    @Override
    public void loadData() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.show_save:
                break;
        }
    }

     class myPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return paths.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        // 滑动切换的时候销毁当前的组件
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View view= LayoutInflater.from(ShowPictureActivity.this).inflate(R.layout.show_picture_item_layout,null);
            PhotoView imageView = (PhotoView) view.findViewById(R.id.imageview);
            if (type==0){
                Util.ImageLoaderToShow(ShowPictureActivity.this,paths.get(position),imageView);
            }else{
                Util.ImageLoaderToShowIntent(ShowPictureActivity.this,paths.get(position),imageView);
            }
            // Now just add PhotoView to ViewPager and return it
            container.addView(imageView);
            return view;
        }
    };


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        image_position.setText((position+1)+"/"+paths.size());
        currentIndex=position;
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
