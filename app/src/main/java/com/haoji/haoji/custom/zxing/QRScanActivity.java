package com.haoji.haoji.custom.zxing;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.comment.UserDetailsActivity;
import com.haoji.haoji.custom.zxing.core.QRCodeView;
import com.haoji.haoji.custom.zxing.zx.QRCodeDecoder;
import com.haoji.haoji.custom.zxing.zx.ZXingView;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.ui.WebViewActivity;
import com.haoji.haoji.util.CommonUtils;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.ToastUtils;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

public class QRScanActivity extends BaseActivity implements QRCodeView.Delegate,NetIntentCallBackListener.INetIntentCallBack,View.OnClickListener{
	private static final String TAG = QRScanActivity.class.getSimpleName();
	private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
	private QRCodeView mQRCodeView;
	private ImageView iv_photo;
	private CheckBox cb_flashlight;
	private String bitmappath,phone;
	private JSONObject json;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	@Override
	public void initMainView() {
		setContentView(R.layout.activity_qr_scan);

	}

	@Override
	public void initUi() {
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		cb_flashlight = (CheckBox) findViewById(R.id.cb_flashlight);
		iv_photo.setOnClickListener(this);
		cb_flashlight.setOnClickListener(this);
		mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
		mQRCodeView.setDelegate(this);

	}

	@Override
	public void loadData() {

	}


	@Override
	protected void onStart() {
		super.onStart();
		mQRCodeView.startCamera();
		//mQRCodeView.showScanRect();
		handler.sendEmptyMessageDelayed(2,1500);
	}

	@Override
	protected void onStop() {
		mQRCodeView.stopCamera();
		super.onStop();
	}


	@Override
	protected void onDestroy() {
		mQRCodeView.onDestroy();
		super.onDestroy();
	}



	private void vibrate() {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(200);
	}

	@Override
	public void onScanQRCodeSuccess(String result) {
		Log.i(TAG, "result:" + result);
		Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
		vibrate();
		mQRCodeView.startSpot();
		if(StringUtils.isUrl(result)){
			startActivity(new Intent(QRScanActivity.this, WebViewActivity.class).putExtra("title","").putExtra("url",result));
		}else if(StringUtils.isPhone(result)){
			Message msg=new Message();
			msg.obj=result;
			msg.what=1;
			handler.sendMessage(msg);
		}
	}

	@Override
	public void onScanQRCodeOpenCameraError() {
		Log.i(TAG, "打开相机出错");
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		mQRCodeView.showScanRect();

		if (resultCode == Activity.RESULT_OK
				&& requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {

			ContentResolver resolver = getContentResolver();
			// 照片的原始资源地址
			Uri originalUri = data.getData();
			try {
				// 使用ContentProvider通过URI获取原始图片
				Bitmap photo = MediaStore.Images.Media.getBitmap(resolver,
						originalUri);
				if (photo != null) {
					// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
					Bitmap smallBitmap = CommonUtils.zoomBitmap(photo,
							photo.getWidth() / 2, photo.getHeight() / 2);
					// 释放原始图片占用的内存，防止out of memory异常发生
					photo.recycle();
					bitmappath = CommonUtils.saveFile(smallBitmap,
							CommonUtils.setImageName());

				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				return QRCodeDecoder.syncDecodeQRCode(bitmappath);
			}

			@Override
			protected void onPostExecute(final String result) {
				if (TextUtils.isEmpty(result)) {
					Toast.makeText(QRScanActivity.this, "未发现二维码",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(QRScanActivity.this, result,
							Toast.LENGTH_SHORT).show();
					if(StringUtils.isUrl(result)){
						startActivity(new Intent(QRScanActivity.this, WebViewActivity.class).putExtra("title","").putExtra("url",result));
					}else if(StringUtils.isPhone(result)){
						Message msg=new Message();
						msg.obj=result;
						msg.what=1;
					handler.sendMessage(msg);

					}
				}
			}
		}.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.cb_flashlight:
				if (cb_flashlight.isChecked()) {
					mQRCodeView.closeFlashlight();
				} else if (!cb_flashlight.isChecked()) {
					mQRCodeView.openFlashlight();

				}
				break;

			case R.id.iv_photo:
				Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
				openAlbumIntent.setType("image/*");
				startActivityForResult(openAlbumIntent,
						REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
				break;
		}


	}

	@Override
	public void onError(Request request, Exception e) {

	}

	@Override
	public void onResponse(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.has("user")) {
				json = jsonObject.getJSONObject("user");
				handler.sendEmptyMessage(0);
				LogUtils.i("json---->"+json);
			}else{
				ToastUtils.showShortToast(QRScanActivity.this,"抱歉，该用户不存在！");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					updateView();
					break;
				case 1:
					final String phone1= (String) msg.obj;
					new Thread(new Runnable() {
						@Override
						public void run() {
							NetIntent netIntent = new NetIntent();
							netIntent.client_getUserByPhone(phone1, new NetIntentCallBackListener(QRScanActivity.this));
						}
					}).start();
					break;
				case 2:
					if(mQRCodeView!=null){
						mQRCodeView.startSpot();
					}

					break;

			}
		}
	};
	public void updateView() {
		try {
		phone=json.getString("phone").toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		startActivity(new Intent(QRScanActivity.this, UserDetailsActivity.class).putExtra("phone",phone));

	}
}