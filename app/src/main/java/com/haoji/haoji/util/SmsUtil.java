package com.haoji.haoji.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsUtil extends ContentObserver {

	private int _id, oldid;

	private String body, text;

	private Handler handler;

	private Context context;

	private String CODE = "";

	private String SENTENCE = "";

	private static final String MARK = "好记传媒";//过滤使用的内容

	private final int SUCCESS = 0;

	public SmsUtil(Context context, Handler handler, String code,
			String sentence) {
		super(handler);
		this.context = context;
		this.handler = handler;
		this.CODE = code;
		this.SENTENCE = sentence;
	}

	@SuppressLint("UseValueOf")
	public void onChange(boolean selfChange) {
		// TODO Auto-generated method stub
		super.onChange(selfChange);
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(Uri.parse("content://sms/inbox"),
				new String[] { "_id", "address", "body", "date", "type" },
				null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			// 取出短信
			while (cursor.moveToNext()) {
				_id = cursor.getInt(0);
				body = cursor.getString(2);
				int id = new Integer(_id).intValue();
				String centent = String.valueOf(body);
				if (centent.contains(MARK)) {
					if (centent.contains(SENTENCE)) {
						if (centent.contains(CODE)) {
							if (id > oldid) {
								oldid = id;
								text = getNumbers(centent);
								new Thread(new Runnable() {
									public void run() {
										if (text != null) {
											Message msg = new Message();
											msg.what = SUCCESS;
											msg.obj = text;
											handler.sendMessage(msg);
										}
									}
								}).start();
							}
						}
					}
				}
			}
		}
	}

	// 截取短信中的数字部分。
	public String getNumbers(String content) {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			return matcher.group(0);
		}
		return "";
	}

}