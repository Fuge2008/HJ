//package com.zhulei.haoji.ui;
//
//import android.app.ActionBar;
//import android.content.Intent;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewTreeObserver.OnGlobalLayoutListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//
//import com.haoji.haoji.R;
//import com.haoji.haoji.base.BaseActivity;
//import com.haoji.haoji.ui.LoginActivity;
//import com.haoji.haoji.ui.SplashActivity;
//import com.haoji.haoji.util.CommonUtils;
//import com.haoji.haoji.util.SPUtils;
//
//import java.util.ArrayList;
//
//
///** 引导界面 */
//public class GuideActivity extends BaseActivity implements View.OnClickListener{
//	protected static final String TAG = GuideActivity.class.getSimpleName();
//	private ViewPager mViewPager;
//	private ArrayList<ImageView> mImageList;// 引导页的ImageView集合
//	private LinearLayout ll_point_group;// 点的集合
//	private View viewRedPoint;// 红点
//	private int mPointWidth; // 两点间距
//	private Button btn_start;// 开始体验
//
//
//
//	@Override
//	public void initMainView() {
//		setContentView(R.layout.activity_guide);
//		mViewPager = (ViewPager) findViewById(R.id.view_pager);
//		viewRedPoint = findViewById(R.id.view_red_point);
//		ll_point_group = (LinearLayout) findViewById(R.id.ll_point_group);
//		btn_start = (Button) findViewById(R.id.btn_start);
//		btn_start.setOnClickListener(this);
//		initData();
//	}
//
//	private void initData() {
//		int[] imageResIDs = { R.drawable.app_logo, R.drawable.app_logo,
//				R.drawable.app_logo, R.drawable.app_logo };
//		mImageList = new ArrayList<ImageView>();
//		for (int i = 0; i < imageResIDs.length; i++) {
//			ImageView imageView = new ImageView(this);
//			imageView.setBackgroundResource(imageResIDs[i]);// 设置背景才能填充
//			mImageList.add(imageView);
//
//			View point = new View(this);
//			point.setBackgroundResource(R.drawable.shape_guide_point_default);
//			ActionBar.LayoutParams params = new ActionBar.LayoutParams(CommonUtils.Dp2Px(this, 10),
//					CommonUtils.Dp2Px(this, 10));
//			if (i != 0) {
//				params.leftMargin = CommonUtils.Dp2Px(this, 10);
//			}
//			point.setLayoutParams(params);
//			ll_point_group.addView(point);
//		}
//	}
//
//	@Override
//	public void initUi() {
//
//		mViewPager.setAdapter(new GuideAdapter());
//
//		// measure -> layout -> draw
//		// 获得视图树观察者, 观察当整个布局的layout时的事件
//		viewRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(
//				new OnGlobalLayoutListener() {
//					// 完成布局后会回调改方法, 改方法可能会被回调多次
//					@Override
//					public void onGlobalLayout() {
//						// 此方法只需要执行一次就可以: 把当前的监听事件从视图树中移除掉, 以后就不会在回调此事件了.
//						viewRedPoint.getViewTreeObserver()
//								.removeGlobalOnLayoutListener(this);
//						mPointWidth = ll_point_group.getChildAt(1).getLeft()- ll_point_group.getChildAt(0).getLeft();// 小圆点的间距
//
//					}
//				});
//		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
//
//			@Override
//			public void onPageSelected(int position) {// 页面选中
//				if (position == mImageList.size() - 1) {
//					btn_start.setVisibility(View.VISIBLE);
//				} else {
//					btn_start.setVisibility(View.GONE);
//				}
//
//			}
//
//			@Override
//			public void onPageScrolled(int position, float positionOffset,
//									   int positionOffsetPixels) {// 页面滑动监听
//
//				int leftMargin = (int) (mPointWidth * (positionOffset + position));// position:当前位置
//				// ,positionOffset:偏移比例,leftMargin:点偏移
//				RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) viewRedPoint
//						.getLayoutParams();
//				lp.leftMargin = leftMargin;
//				viewRedPoint.setLayoutParams(lp);
//
//			}
//
//			@Override
//			public void onPageScrollStateChanged(int state) {// 状态改变
//				// TODO Auto-generated method stub
//
//			}
//		});
//
//	}
//
//	@Override
//	public void loadData() {
//
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//			case R.id.btn_start:
//				// 开始体验
//				SPUtils.put(this, SplashActivity.IS_GUIDE_SHOWED, true);// 记录已经展现过了新手引导页
//				startActivity(new Intent(this, LoginActivity.class));
//				finish();
//
//				break;
//		}
//
//	}
//
//	class GuideAdapter extends PagerAdapter {
//
//		@Override
//		public int getCount() {
//			return mImageList.size();
//		}
//
//		@Override
//		public boolean isViewFromObject(View arg0, Object arg1) {// 判断是否由对象生成界面
//
//			return arg0 == arg1;
//		}
//
//		@Override
//		public Object instantiateItem(ViewGroup container, int position) {// 把对象放到容器adapter中
//			container.addView(mImageList.get(position));
//			return mImageList.get(position);
//		}
//
//		@Override
//		public void destroyItem(ViewGroup container, int position, Object object) {// 把对象移出容器
//			container.removeView((ImageView) object);
//		}
//	}
//
//}
