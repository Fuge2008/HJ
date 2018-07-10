package com.haoji.haoji.ui;

import android.Manifest;
import android.content.Intent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.util.SPUtils;

import kr.co.namee.permissiongen.PermissionGen;


/** 闪屏界面 */
public class SplashActivity extends BaseActivity {
	protected static final String TAG = SplashActivity.class.getSimpleName();
	public static final String IS_GUIDE_SHOWED = "is_guide_showed";// 标记新手引导是否已经展示过

	@Override
	public void initMainView() {
		setContentView(R.layout.activity_splash);
	}

	@Override
	public void initUi() {
		RelativeLayout rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
		// 旋转动画
		RotateAnimation rotate = new RotateAnimation(0, 360,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(1000);
		rotate.setFillAfter(true);// 结束后保持最终状态
		// 缩放动画
		ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1,
				ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
				ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
		scale.setDuration(1000);
		scale.setFillAfter(true);
		// 渐变动画
		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		alpha.setDuration(2000);
		alpha.setFillAfter(true);

		AnimationSet set = new AnimationSet(false);
		set.addAnimation(rotate);
		set.addAnimation(scale);
		set.addAnimation(alpha);
		set.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {// 动画结束时候回调
				// 判断新手引导页是否展示过
				boolean showed = (boolean) SPUtils.get(SplashActivity.this,
						IS_GUIDE_SHOWED, false);
				//if (showed) {
					startActivity(new Intent(SplashActivity.this,
							MainActivity.class));
//
//				} else {
//					startActivity(new Intent(SplashActivity.this,
//							GuideActivity.class));
//				}
				finish();

			}
		});
		rlRoot.startAnimation(set);

	}

	@Override
	public void loadData() {
		setPermissionGen();
	}
	private void setPermissionGen() {
		PermissionGen.with(this)
				.addRequestCode(100)
				.permissions(
						//电话通讯录
						Manifest.permission.GET_ACCOUNTS,
						Manifest.permission.READ_PHONE_STATE,
						//位置
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.ACCESS_FINE_LOCATION,
						//相机、麦克风
						Manifest.permission.RECORD_AUDIO,
						Manifest.permission.WAKE_LOCK,
						Manifest.permission.CAMERA,
						//存储空间
						Manifest.permission.WRITE_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_SETTINGS
				)
				.request();
	}



}
