package com.haoji.haoji.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.util.ActivityUtils;

import java.lang.reflect.InvocationTargetException;

//浏览器
public class WebViewActivity extends BaseActivity {


	private WebView mWebView;
	private ProgressBar progressbar;
	private String strurl = "";
	private MyTimer mTimer;
	private int progress = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


	}

	@Override
	public void initMainView() {
		setContentView(R.layout.activity_web);


	}

	@Override
	public void initUi() {
		mWebView = (WebView) findViewById(R.id.mwebview);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.getString("url") != null) {
			strurl = "";
			strurl = bundle.getString("url");
		}
		if (bundle != null && bundle.getString("title") != null) {
			String strTitle=bundle.getString("title").toString();

			//Util.setHeadTitleMore(this,strTitle,true);
		}
		if (!TextUtils.isEmpty(strurl)) {
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.setWebViewClient(new WeiboWebViewClient());
			mWebView.setWebChromeClient(new WebChromeClient());
			mWebView.loadUrl(strurl);
		}

	}

	@Override
	public void loadData() {

	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			mWebView.getClass().getMethod("onResume")
					.invoke(mWebView, (Object[]) null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			mWebView.getClass().getMethod("onPause")
					.invoke(mWebView, (Object[]) null);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	private class WeiboWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (mTimer == null) {
				mTimer = new MyTimer(15000, 50);
			}
			mTimer.start();
			progressbar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mTimer.cancel();
			progress = 0;
			progressbar.setProgress(100);
			progressbar.setVisibility(View.GONE);
		}
	}

	/* 定义一个倒计时的内部类 */
	private class MyTimer extends CountDownTimer {
		public MyTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			progress = 100;
			progressbar.setVisibility(View.GONE);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if (progress == 100) {
				progressbar.setVisibility(View.GONE);
			} else {
				progressbar.setProgress(progress++);
			}
		}
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView.canGoBack())
				mWebView.goBack();
			else
				ActivityUtils.finish(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}



}
