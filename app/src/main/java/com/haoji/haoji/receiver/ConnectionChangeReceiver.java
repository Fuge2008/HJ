package com.haoji.haoji.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.haoji.haoji.util.ToastUtils;


public class ConnectionChangeReceiver extends BroadcastReceiver {
	
		@Override
		public void onReceive(final Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiNetInfo= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (!mobNetInfo.isConnected()&&!wifiNetInfo.isConnected()) {
				
				ToastUtils.showShortToastSafe(context, "网络不给力,请检查网络连接");
				
			}
		}
	}