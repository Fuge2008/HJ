package com.haoji.haoji.comment;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;



public class BigImageActivity extends BaseActivity {

    private ImageCycleView mAdView;

    @Override
    public void initMainView() {setContentView(R.layout.activity_bigimage);}

    @Override
    public void initUi() {
        String[] images=getIntent().getStringArrayExtra("images");
        int page = getIntent().getIntExtra("page", 0);
        mAdView = (ImageCycleView) this.findViewById(R.id.ad_view);
        mAdView.setImageResources(images, page, mAdCycleViewListener);

    }
    @Override
    public void loadData() {}

    private ImageCycleView.ImageCycleViewListener mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {
        @Override
        public void onImageClick(int position, View imageView) {
        }

        @Override
        public void displayImage(String imageURL, ImageView imageView) {
            Glide.with(BigImageActivity.this).load(imageURL).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        }
    };

}
