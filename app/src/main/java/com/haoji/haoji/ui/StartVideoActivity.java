//package com.zhulei.haoji.ui;
//
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.MediaController;
//import android.widget.TextView;
//import android.widget.VideoView;
//
//import com.zhulei.haoji.R;
//
///**
// * Created by Administrator on 2017/2/28.
// */
//
//public class StartVideoActivity extends AppCompatActivity {
//
//    public final static int PageCode=111;
//    private VideoView mVideoView01;
//    private String videoPath = "";//视频存放的路径
//    private String TAG = "HIPPO_VIDEOVIEW";
//    private TextView video_loading;
//    private int old_duration;
//    private int position;
//
//    @Override
//    public void onBackPressed() {
//        back();
//    }
//
//    private Handler handler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    finish();
//                    break;
//            }
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Window window = getWindow();
//        //隐藏标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //隐藏状态栏
//        //定义全屏参数
//        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        //设置当前窗体为全屏显示
//        window.setFlags(flag, flag);
//        setContentView(R.layout.activity_start_video);
//        initUI();
//    }
//
//    public void back(){
//        Intent intent=new Intent();
//        intent.putExtra("position",mVideoView01.getCurrentPosition());
//        setResult(PageCode,intent);
//        finish();
//    }
//
//    Runnable runnable = new Runnable() {
//        public void run() {
//            int duration = mVideoView01.getCurrentPosition();
//            if (old_duration == duration && mVideoView01.isPlaying()) {
//                video_loading.setVisibility(View.VISIBLE);
//            } else {
//                video_loading.setVisibility(View.GONE);
//            }
//            old_duration = duration;
//            handler.postDelayed(runnable, 500);
//        }
//    };
//
//    private void initUI() {
//        videoPath = this.getIntent().getStringExtra("videoPath");
//        position=this.getIntent().getIntExtra("position",0);
//        video_loading = (TextView) findViewById(R.id.video_loading);
//        //获取VideoView对象
//        mVideoView01 = (VideoView) findViewById(R.id.myVideoView1);
//        //设置视频播放器，准备函数
//        mVideoView01.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                setTitle(videoPath.toString());
//            }
//        });
//        //设置视频播放完毕动作，可以在内部设置是否重复播放
//        mVideoView01.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer arg0) {
//                handler.sendEmptyMessage(0);
//            }
//        });
//        playVideo(videoPath);
//        handler.postDelayed(runnable,0);
//    }
//
//    //媒体播放动作函数
//    private void playVideo(String strPath) {
//        if (strPath != "") {
//            /*调用VideoURI方法，指定解析路径 */
//            mVideoView01.setVideoURI(Uri.parse(strPath));
//            /* 设置控制Bar，显示在Context中*/
//            mVideoView01.setMediaController(new MediaController(this));
//            mVideoView01.requestFocus();
//
//            /*调用VideoView.start()自动播放*/
//            mVideoView01.start();
//            if (mVideoView01.isPlaying()) {
//                /* start()后，仍需要preparing() */
//                //mTextView01.setText("Now Playing:"+strPath);
//                setTitle(videoPath.toString());
//                Log.i(TAG, strPath);
//            }
//            mVideoView01.seekTo(position);
//        }
//    }
//
//
//}
