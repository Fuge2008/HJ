package com.haoji.haoji.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.haoji.haoji.R;
import com.haoji.haoji.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;



public class Util {

    /*获取app文件根目录*/
    public static String getYouquRootFile() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/haoji/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 将json格式的字符串解析成Map对象
     *
     * @throws JSONException
     */
    public static ArrayList<HashMap<String, Object>> toHashMap(JSONArray array) {
        ArrayList<HashMap<String, Object>> maps = new ArrayList<HashMap<String, Object>>();
        try {
            for (int i = 0; i < array.length(); i++) {
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                JSONObject object = array.getJSONObject(i);
                Iterator it = object.keys();
                while (it.hasNext()) {
                    String key = String.valueOf(it.next());
                    Object value = object.get(key);
                    if ("null".equals(value)) {
                        hashMap.put(key, null);
                    } else {
                        hashMap.put(key, value);
                    }
                }
                maps.add(hashMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maps;
    }

    public static HashMap<String, Object> toHashMap(JSONObject jsonObject) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        try {
            Iterator it = jsonObject.keys();
            while (it.hasNext()) {
                String key = String.valueOf(it.next());
                Object value = jsonObject.get(key);
                if ("null".equals(value)) {
                    hashMap.put(key, null);
                } else {
                    hashMap.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    public static ArrayList<HashMap<String, Object>> toHashMap(String array) {
        ArrayList<HashMap<String, Object>> maps = new ArrayList<HashMap<String, Object>>();
        try {
            JSONArray jsonArray = new JSONArray(array);
            for (int i = 0; i < jsonArray.length(); i++) {
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                JSONObject object = jsonArray.getJSONObject(i);
                Iterator it = object.keys();
                while (it.hasNext()) {
                    String key = String.valueOf(it.next());
                    Object value = object.get(key);
                    if ("null".equals(value)) {
                        hashMap.put(key, null);
                    } else {
                        hashMap.put(key, value);
                    }
                }
                maps.add(hashMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maps;
    }

    public static void setListViewHeightBasedOnChildren(GridView gridView, int Colums) {
        // 获取listview的adapter
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = Colums;// listView.getNumColumns();
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        // ((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        gridView.setLayoutParams(params);
    }

    public static void setListViewHeightBasedOnChildren(GridView gridView, int Colums,int width) {
        // 获取listview的adapter
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = Colums;// listView.getNumColumns();
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight()+width;
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        //((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        gridView.setLayoutParams(params);
    }


    /**
     * 头像加载
     *
     * @param context
     * @param path
     * @param image
     */
    public static void ImageLoaderToPic(Context context, String path, ImageView image) {
        try {
            Glide.with(context).load(Constants.HttpRoot + path)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.my_headphoto).error(R.drawable.my_headphoto).into(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ImageLoaderToPicAuto(Context context, String path, ImageView image) {
        try {
            if (isRootPath(path)) {
                Glide.with(context).load(Constants.HttpRoot + path)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable.my_headphoto).error(R.drawable.my_headphoto).into(image);
            } else {
                Glide.with(context).load(path)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable.my_headphoto).error(R.drawable.my_headphoto).into(image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片浏览加载
     *
     * @param context
     * @param path
     * @param imageView
     */
    public static void ImageLoaderToShow(Context context, String path, ImageView imageView) {
        try {
            Glide.with(context).load(Constants.HttpRoot + path)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.pictures_no)
                    .error(R.drawable.default_pic_fail).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ImageLoaderToShowIntent(Context context, String path, ImageView imageView) {
        try {
            Glide.with(context).load(path)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.pictures_no)
                    .error(R.drawable.default_pic_fail).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ImageLoader_thumbnail(Context context, String path, ImageView imageView) {
        try {
            Glide.with(context).load(path)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 自适应宽度加载图片。保持图片的长宽比例不变，通过修改imageView的高度来完全显示图片。
     */
    public static void loadIntoUseFitWidth(Context context, final String imageUrl, final ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (imageView == null) {
                            return false;
                        }
                        if (imageView.getScaleType() != ImageView.ScaleType.FIT_XY) {
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        int vw = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
                        float scale = (float) vw / (float) resource.getIntrinsicWidth();
                        int vh = Math.round(resource.getIntrinsicHeight() * scale);
                        params.height = vh + imageView.getPaddingTop() + imageView.getPaddingBottom();
                        imageView.setLayoutParams(params);
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.color.colorBackground)
                .error(R.drawable.default_pic_fail)
                .into(imageView);
    }

    public static void loadIntoUseFitWidth(Context context, final String imageUrl, final ImageView imageView, final HashMap<String, ViewGroup.LayoutParams> map) {
        Glide.with(context)
                .load(Constants.HttpRoot + imageUrl)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (imageView == null) {
                            return false;
                        }
                        if (imageView.getScaleType() != ImageView.ScaleType.FIT_XY) {
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        }
                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        int vw = imageView.getWidth() - imageView.getPaddingLeft() - imageView.getPaddingRight();
                        float scale = (float) vw / (float) resource.getIntrinsicWidth();
                        int vh = Math.round(resource.getIntrinsicHeight() * scale);
                        params.height = vh + imageView.getPaddingTop() + imageView.getPaddingBottom();
                        imageView.setLayoutParams(params);
                        map.put(imageUrl, params);
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.color.colorBackground)
                .error(R.drawable.default_pic_fail)
                .into(imageView);
    }


    public static boolean isPicture(String path) {
        if (path == null) {
            return false;
        }
        if (path.endsWith(".png")) {
            return true;
        }
        if (path.endsWith(".jpg")) {
            return true;
        }
        if (path.endsWith(".gif")) {
            return true;
        }
        return false;

    }

    public static boolean isGif(String path) {
        if (path.endsWith(".gif")) {
            return true;
        }
        return false;
    }

    public static boolean isRootPath(String path) {
        if (path == null) {
            return false;
        }
        if (!isPicture(path)) {
            return false;
        }
        if (path.indexOf("http") != -1) {
            return false;
        }
        return true;
    }

    public static boolean isNull(Object o) {
        if (o == null)
            return false;
        if (o.toString().equals("null"))
            return false;
        if (o.toString().equals("NULL"))
            return false;
        if (o.toString().equals(""))
            return false;
        if (o.toString().equals(" "))
            return false;
        return true;
    }

    public static void SystemBar(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) context).getWindow();// 获取window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 增加bar高度
     */
    public static void setBarHeight(Context context) {
        try {
            View view = ((Activity) context).findViewById(R.id.top_bar);
            int height = SystemUtil.getHeight(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
            view.setLayoutParams(params);
        } catch (Exception e) {

            ToastUtils.showShortToastSafe(context, "设置bar出错" + e.toString());
        }
    }

    public static void setHeadTitleMore(final Context context, String title, boolean leftOnClick) {
        try {
            TextView mHeadTitle = (TextView) ((Activity) context).findViewById(R.id.head_title);
            mHeadTitle.setText(title);
            if (leftOnClick) {
                ((Activity) context).findViewById(R.id.head_back).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Activity) context).finish();
                    }
                });
            }
        } catch (Exception e) {
            ToastUtils.showShortToastSafe(context, "初始化标题出错" + e.toString());
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    public static int getListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return 0;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        return totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
    }

    public static boolean getVideoThumbnailutils(String path, String savePath, String name) {
        try {
            Bitmap bitmap = null;
            bitmap = ThumbnailUtils.createVideoThumbnail(
                    path, MediaStore.Images.Thumbnails.MICRO_KIND);//创建一个视频缩略图
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(new File(savePath, name));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static Bitmap getVideoThumbnail(String filePath, String savePath, String name) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
            File file = new File(savePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(new File(savePath, name));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    //获取随机颜色
    public static int randomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    public static String getThumbnailName() {
        return "thumbnail_" + System.currentTimeMillis() + ".png";
    }

    /**
     * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在
     * {
     * @link #init(Context)} 之后调用。</p>
     *
     * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
     * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
     *
     * @param token    从服务端获取的用户身份令牌（Token）。
     * @return RongIM  客户端核心类的实例。
     */
    public static void connect(String token) {

        RongIM.connect(token, new RongIMClient.ConnectCallback() {

            /**
             * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
             *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
             */
            @Override
            public void onTokenIncorrect() {
                System.out.println("onTokenIncorrect");
            }

            /**
             * 连接融云成功
             * @param userid 当前 token 对应的用户 id
             */
            @Override
            public void onSuccess(String userid) {
                System.out.println("--onSuccess" + userid);
                Log.d("LoginActivity", "--onSuccess" + userid);
            }

            /**
             * 连接融云失败
             * @param errorCode 错误码，可到官网 查看错误码对应的注释
             */
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                System.out.println(errorCode);
            }
        });
    }

    public static int getSex(String sex){
        if (sex==null)
            return 0;
        if ("男".equals(sex))
            return 1;
        if ("女".equals(sex))
            return 2;
        return 1;
    }

    public static String getSexForString(String sex){
        if (sex==null)
            return "未知";
        if ("1".equals(sex))
            return "男";
        if ("2".equals(sex))
            return "女";
        return "未知";
    }

    /**
     * 设置添加屏幕的背景透明度 * @param bgAlpha
     */
    public static void setBackgroundAlpha(float bgAlpha,Context context) {
        WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        ((Activity)context).getWindow().setAttributes(lp);
        ((Activity)context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public static void getUserIsNull(SharedPreferencesUtil util,Context context){
        if (!util.getIsLogin())
            ToastUtils.showShortToastSafe(context,"用户未登录，请登录");
            return;
    }

}
