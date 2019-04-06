package com.haoji.haoji.fragment;

import android.app.Activity;
import android.graphics.Point;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.haoji.haoji.R;
import com.superplayer.library.SuperPlayer;

import java.lang.reflect.Field;

import butterknife.BindView;

/**
 * 作者：
 * 时间： 2018/7/30 0030-下午 2:55
 * 描述：
 * 来源：
 */


public class VideoFragment extends VideoBaseFragment implements SuperPlayer.OnNetChangeListener{
    @BindView(R.id.view_super_player)
    SuperPlayer player;
    @BindView(R.id.rl_back_right)
    RelativeLayout rlBackRight;
    @BindView(R.id.dl_back_play)
    DrawerLayout dlBackPlay;
    @BindView(R.id.iv_play_thun)
    ImageView ivPlayThun;
    private String url;
    public static final String URL = "URL";

    @Override
    protected int getLayoutId() {
        return R.layout.fm_video;
    }

    @Override
    protected void initView() {

        url = getArguments().getString(URL);
//        //创建player对象
//        mVodPlayer = new TXVodPlayer(context);
////关键player对象与界面view
//        mVodPlayer.setPlayerView(txvVideo);
////        url = "http://v.cctv.com/flash/mp4video6/TMS/2011/01/05/cf752b1c12ce452b3040cab2f90bc265_h264818000nero_aac32-1.mp4";
//        mVodPlayer.setLoop(true);
        initPlayer("标题", url);
        Glide.with(context)
                .load(url)
                .into(ivPlayThun);
//        dlBackPlay.addDrawerListener(new DrawerLayout.DrawerListener() {
//            @Override
//            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
//
//            }
//
//            @Override
//            public void onDrawerOpened(@NonNull View drawerView) {
//                if (mVodPlayer != null) {
//                    mVodPlayer.pause();
//                }
//            }
//
//            @Override
//            public void onDrawerClosed(@NonNull View drawerView) {
//                if (mVodPlayer != null) {
//                    mVodPlayer.resume();
//                }
//            }
//
//            @Override
//            public void onDrawerStateChanged(int newState) {
//
//            }
//        });
//
//
//        //设置菜单内容之外其他区域的背景色
//        dlBackPlay.setScrimColor(Color.TRANSPARENT);
//        //设置 全屏滑动
//        setDrawerRightEdgeSize(getActivity(), dlBackPlay, 1);

    }

    @Override
    protected void loadData() {
       // player.start();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (player == null) {
            return;
        }
        if (isVisibleToUser) {
            player.start();
        } else {
            player.stop();
        }

    }

    @Override
    public void onResume() {

        super.onResume();
        if (player != null) {
            player.start();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null) {
            player.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            // true代表清除最后一帧画面
            player.stop();
        }
        if (player != null) {
            player.onDestroy();
        }
    }


    /**
     * 设置 全屏滑动
     *
     * @param activity
     * @param drawerLayout
     * @param displayWidthPercentage
     */
    private void setDrawerRightEdgeSize(Activity activity, DrawerLayout drawerLayout, float displayWidthPercentage) {
        if (activity == null || drawerLayout == null) return;
        try {
            // 找到 ViewDragHelper 并设置 Accessible 为true
            Field mRightDragger =
                    drawerLayout.getClass().getDeclaredField("mRightDragger");//Right
            mRightDragger.setAccessible(true);
            ViewDragHelper leftDragger = (ViewDragHelper) mRightDragger.get(drawerLayout);

            // 找到 edgeSizeField 并设置 Accessible 为true
            Field edgeSizeField = leftDragger.getClass().getDeclaredField("mEdgeSize");
            edgeSizeField.setAccessible(true);
            int edgeSize = edgeSizeField.getInt(leftDragger);

            // 设置新的边缘大小
            Point displaySize = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
            edgeSizeField.setInt(leftDragger, Math.max(edgeSize, (int) (displaySize.x *
                    displayWidthPercentage)));
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
    }

    /**
     * 初始化播放器
     */
    private void initPlayer(String videoTitle, String videPath) {

        player.setNetChangeListener(true)//设置监听手机网络的变化
                .setOnNetChangeListener(this)//实现网络变化的回调
                .onPrepared(new SuperPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        /**
                         * 监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）
                         */
                       // videoPic.setVisibility(View.GONE);

                    }
                }).onComplete(new Runnable() {
            @Override
            public void run() {
                /**
                 * 监听视频是否已经播放完成了。（可以在这里处理视频播放完成进行的操作）
                 */
                //videoPic.setVisibility(View.VISIBLE);
                player.start();

            }
        }).onInfo(new SuperPlayer.OnInfoListener() {
            @Override
            public void onInfo(int what, int extra) {
                /**
                 * 监听视频的相关信息。
                 */

            }
        }).onError(new SuperPlayer.OnErrorListener() {
            @Override
            public void onError(int what, int extra) {
                /**
                 * 监听视频播放失败的回调
                 */

            }
        }).setTitle(videoTitle)//设置视频的titleName
                .play(videPath);//开始播放视频
        player.setScaleType(SuperPlayer.SCALETYPE_FITXY);
        player.setPlayerWH(0, player.getMeasuredHeight());//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置

    }
    /**
     * 网络链接监听类
     */
    @Override
    public void onWifi() {
        Toast.makeText(context, "当前网络环境是WIFI", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMobile() {
        Toast.makeText(context, "当前网络环境是手机网络", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisConnect() {
        Toast.makeText(context, "网络链接断开", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoAvailable() {
        Toast.makeText(context, "无网络链接", Toast.LENGTH_SHORT).show();
    }
}
