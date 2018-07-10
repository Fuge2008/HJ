package com.haoji.haoji.custom.zxing;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.zxing.core.BGAQRCodeUtil;
import com.haoji.haoji.custom.zxing.zx.QRCodeDecoder;
import com.haoji.haoji.custom.zxing.zx.QRCodeEncoder;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class QRGenerateActivity extends BaseActivity {
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.tv_nick_name)
    TextView tvNickName;
    @BindView(R.id.tv_region)
    TextView tvRegion;
    @BindView(R.id.iv_chinese_logo)
    ImageView ivChineseLogo;


    private String picPath;
    SharedPreferencesUtil util;
    private Bitmap bitmap;

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    initUi();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void initMainView() {
        setContentView(R.layout.activity_qr_generate);
        util = new SharedPreferencesUtil(this);
        ButterKnife.bind(this);
        picPath = util.getPicture().toString();
        tvNickName.setText(util.getNickName().toString());
        tvRegion.setText(util.getProvince() + " " + util.getCity());
        Glide.with(this).load(picPath).diskCacheStrategy(DiskCacheStrategy.ALL).into(ivHead);

        if (StringUtils.isUrl(picPath)) {
            getBitmap();
            handler.sendEmptyMessageDelayed(0, 1500);
        }

    }

    private void getBitmap() {
        Glide.with(QRGenerateActivity.this).load(util.getPicture()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                bitmap = resource;
            }
        });
    }

    @Override
    public void initUi() {
        // TODO AsyncTask 需要解决内存泄露问题
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(
                            QRGenerateActivity.this.getResources(),
                            R.drawable.app_logo);
                }
                return QRCodeEncoder.syncEncodeQRCode(util.getMobilePhone(),
                        BGAQRCodeUtil.dp2px(QRGenerateActivity.this, 150),
                        Color.BLACK, Color.WHITE, bitmap);// 设置二维码颜色，信息，logo等
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    ivChineseLogo.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(QRGenerateActivity.this, "生成二维码失败",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();

    }

    @Override
    public void loadData() {}
    public void decodeChineseWithLogo(View v) {
        ivChineseLogo.setDrawingCacheEnabled(true);
        Bitmap bitmap = ivChineseLogo.getDrawingCache();
        decode(bitmap, "解析二维码失败");
    }

    @SuppressLint("NewApi")
    private void decode(final Bitmap bitmap, final String errorTip) {
        // AsyncTask 需要解决内存泄露问题
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return QRCodeDecoder.syncDecodeQRCode(bitmap);
            }

            @Override
            protected void onPostExecute(String result) {
                if (TextUtils.isEmpty(result)) {
                    Toast.makeText(QRGenerateActivity.this, errorTip,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QRGenerateActivity.this, result,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @OnClick(R.id.iv_head)
    public void onViewClicked() {
    }
}