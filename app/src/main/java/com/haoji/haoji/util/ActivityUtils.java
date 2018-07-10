package com.haoji.haoji.util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.haoji.haoji.R;


/**
 * Activity相关工具类
 */
public class ActivityUtils {

    private ActivityUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 判断是否存在Activity
     *
     * @param context     上下文
     * @param packageName 包名
     * @param className   activity全路径类名
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isActivityExists(Context context, String packageName, String className) {
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        return !(context.getPackageManager().resolveActivity(intent, 0) == null ||
                intent.resolveActivity(context.getPackageManager()) == null ||
                context.getPackageManager().queryIntentActivities(intent, 0).size() == 0);
    }

    /**
     * 打开Activity
     *
     * @param context     上下文
     * @param packageName 包名
     * @param className   全类名
     */
    public static void launchActivity(Context context, String packageName, String className) {
        launchActivity(context, packageName, className, null);
    }

    /**
     * 打开Activity
     *
     * @param context     上下文
     * @param packageName 包名
     * @param className   全类名
     * @param bundle      bundle
     */
    public static void launchActivity(Context context, String packageName, String className, Bundle bundle) {
        context.startActivity(IntentUtils.getComponentIntent(packageName, className, bundle));
    }
    
    /**
	 * 关闭 Activity
	 * 
	 * @param activity
	 */
	public static void finish(Activity activity) {
		activity.finish();
		activity.overridePendingTransition(R.anim.em_push_right_in,
				R.anim.em_push_right_out);
	}

	/**
	 * 打开Activity
	 * 
	 * @param activity
	 * @param cls
	 * @param name
	 */
//	public static void start_Activity(Activity activity, Class<?> cls,
//			BasicNameValuePair... name) {
//		Intent intent = new Intent();
//		intent.setClass(activity, cls);
//		if (name != null)
//			for (int i = 0; i < name.length; i++) {
//				intent.putExtra(name[i].getName(), name[i].getValue());
//			}
//		activity.startActivity(intent);
//		activity.overridePendingTransition(R.anim.em_push_left_in,
//				R.anim.em_push_left_out);
//
//	}
}
