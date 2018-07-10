package com.haoji.haoji.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haoji.haoji.R;
import com.haoji.haoji.adapter.GridImageAdapter;
import com.haoji.haoji.base.BaseActivity;
import com.haoji.haoji.custom.CustomProgress;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.util.FullyGridLayoutManager;
import com.haoji.haoji.util.LogUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.ToastUtils;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.model.PictureConfig;
import com.squareup.okhttp.Request;
import com.yalantis.ucrop.entity.LocalMedia;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddEnergyActivity extends BaseActivity implements NetIntentCallBackListener.INetIntentCallBack {
    private static String TAG = AddEnergyActivity.class.getSimpleName();
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.iv_locaton)
    CheckBox ivLocaton;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.ll_location)
    RelativeLayout llLocation;
    private SharedPreferencesUtil util;
    private CustomProgress dialog;
    //private ArrayList list = new ArrayList();
    private int index = 1;
    private JSONObject jsonObject;
    private String   strContent, strLocation, strPic1="",  strPic2="",  strPic3="",  strPic4="",  strPic5="",  strPic6="",  strPic7="",  strPic8="",  strPic9="";
    private List<String> path;

    //选取相片
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private int selectMode = FunctionConfig.MODE_MULTIPLE;
    private int maxSelectNum = 9;// 图片最大可选数量
    private boolean isShow = true;
    private int selectType = LocalMediaLoader.TYPE_IMAGE;
    private int copyMode = FunctionConfig.COPY_MODEL_DEFAULT;
    private boolean enablePreview = true;
    private boolean isPreviewVideo = true;
    private boolean enableCrop = true;
    private boolean theme = false;
    private boolean selectImageType = false;
    private int cropW = 0;
    private int cropH = 0;
    private int compressW = 0;
    private int compressH = 0;
    private boolean isCompress = false;
    private boolean isCheckNumMode = false;
    private int compressFlag = 1;// 1 系统自带压缩 2 luban压缩
    private List<LocalMedia> selectMedia = new ArrayList<>();
    private Context mContext;
    public static final int REQUSET = 1;
    private  int picNum,picOrder=0;



    /**
     * 接收返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // requestCode标示请求的标示 resultCode表示有数据
        switch (requestCode) {
            case REQUSET:
                if (requestCode == AddEnergyActivity.REQUSET && resultCode == RESULT_OK) {
                    String strLocation = data.getStringExtra(MyLocationActivity.DATA_STR);
                    Log.i("info", "返回位置" + strLocation);
                    tvCancel.setVisibility(View.VISIBLE);
                    tvLocation.setText(strLocation.toString());
                    tvLocation.setTextColor(getResources().getColor(R.color.tableMenuFontColor));
                    ivLocaton.setChecked(true);
                }
                break;
        }

    }


    @Override
    public void initMainView() {
        this.setContentView(R.layout.activity_addenergy);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//禁止键盘弹出
        ButterKnife.bind(this);
    }

    @Override
    public void initUi() {

        util = new SharedPreferencesUtil(this);
        path=new ArrayList<>();
        //选取相片
        mContext = this;
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        initData();
        FullyGridLayoutManager manager = new FullyGridLayoutManager(AddEnergyActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(AddEnergyActivity.this, onAddPicClickListener);
        adapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                // 这里可预览图片
                PictureConfig.getPictureConfig().externalPicturePreview(mContext, position, selectMedia);
            }
        });

    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    dialog = CustomProgress.show(AddEnergyActivity.this, "处理中..", true, null);


                    picNum=selectMedia.size();
                    if(selectMedia!=null & picNum>0){
                        for(int i=0;i<picNum;i++){
                            picOrder=i;
                            NetIntent netIntent = new NetIntent();
                            File file=new File(selectMedia.get(i).getPath().toString());
                            netIntent.client_uploadImg(file, new NetIntentCallBackListener(AddEnergyActivity.this));//TODO  上传图片
                        }

                    }
                    break;
                case 1:
                    ToastUtils.showShortToastSafe(AddEnergyActivity.this,"发布成功");
                    finish();
                    break;
                case 2:
                    //TODO  接口已经修改  参数
                    strContent=etContent.getText().toString();
                    LogUtils.i("&&&&&&&&&&&&&&&&&&&&&&&发表正能量的内容："+strContent);
                    strLocation=tvLocation.getText().toString();
                    if(StringUtils.equals(strLocation,"所在位置")){
                        strLocation="";
                    }
                    if (StringUtils.isNotEmpty(strContent,true)) {
                        dialog = CustomProgress.show(AddEnergyActivity.this, "上传中..", true, null);
                        if( path.size()>=1 ){
                            if(path.get(0)!=null){
                                strPic1=path.get(0).toString();
                            }

                        }
                        if( path.size()>=2 ){
                            if(path.get(1)!=null){
                                strPic2=path.get(1).toString();
                            }

                        }
                        if( path.size()>=3  ){
                            if(path.get(2)!=null){
                                strPic3=path.get(2).toString();
                            }

                        }
                        if( path.size()>=4 ){
                            if(path.get(3)!=null){
                                strPic4=path.get(3).toString();
                            }

                        }
                        if(path.size()>=5 ){
                            if(path.get(4)!=null){
                                strPic5=path.get(4).toString();
                            }

                        }
                        if(path.size()>=6 ){
                            if( path.get(5)!=null){
                                strPic6=path.get(5).toString();
                            }

                        }
                        if( path.size()>=7  ){
                            if(path.get(6)!=null){
                                strPic7=path.get(6).toString();
                            }

                        }
                        if( path.size()>=8  ){
                            if(path.get(7)!=null){
                                strPic8=path.get(7).toString();
                            }

                        }
                        if( path.size()>=9 ){
                            if(path.get(8)!=null){
                                strPic9=path.get(8).toString();
                            }

                        }
                        NetIntent netIntent = new NetIntent();
                        Log.i("提交数据：","userid:"+util.getUserId()+"strContent"+strContent+"strLocation"+strLocation+"strPic1"+strPic1+"strPic2"+strPic2);
                        netIntent.client_addEnergy(util.getUserId(),
                                strContent,strLocation, strPic1,  strPic2,  strPic3,  strPic4,  strPic5,  strPic6,  strPic7,  strPic8,  strPic9,
                                new NetIntentCallBackListener(AddEnergyActivity.this));
                    }
                    break;
            }
        }
    };

    @Override
    public void loadData() {
        ImageView iv_back= (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    public void onError(Request request, Exception e) {

        if( dialog!=null){
            dialog.dismiss();
        }

    }

    @Override
    public void onResponse(String response) {
        if (dialog != null)
            dialog.dismiss();
        System.out.println("观察：====》"+response);
        try {
            jsonObject = new JSONObject(response);

            if (jsonObject.has("newFileName")) {
                Log.i("info=======>图片路径",jsonObject.toString());

                path.add(jsonObject.getString("newFileName"));
                for(int i=0;i<path.size();i++){
                    Log.i("图片集合：==============》",path.get(i).toString());
                }
                Log.i("图片汇总集合：==============》",path.toString());


            }else{
                if (jsonObject.getBoolean("code")){
                    handler.sendEmptyMessage(1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void initData() {
        //isCheckNumMode = false;// TODO 默认模式
        isCheckNumMode = true;//TODO  qq样式
        //selectMode = FunctionConfig.MODE_SINGLE;//TODO  单张图片
        selectMode = FunctionConfig.MODE_MULTIPLE;//TODO  多张图片
        selectType = LocalMediaLoader.TYPE_IMAGE;// TODO  图片
        //selectType = LocalMediaLoader.TYPE_VIDEO;//TODO 视频
        isShow = true;//TODO 显示拍摄
        //isShow = false;//TODO 隐藏拍摄
        copyMode = FunctionConfig.COPY_MODEL_DEFAULT;//TODO 原图
        //copyMode = FunctionConfig.CROP_MODEL_1_1;//TODO 裁剪 1：1
        //copyMode = FunctionConfig.CROP_MODEL_3_2;//TODO 裁剪 3：2
        //copyMode = FunctionConfig.CROP_MODEL_3_4;//TODO 裁剪 3：4
        //copyMode = FunctionConfig.CROP_MODEL_16_9;// TODO 裁剪 16：9
        enablePreview = true;//TODO 图片允许预览
        //enablePreview = false;//TODO 图片禁止预览
        //isPreviewVideo = true;//TODO 视频允许预览
        //isPreviewVideo = false;//TODO 视频禁止预览
        //enableCrop = true;//TODO  允许剪切
        enableCrop = false;//TODO  不允许剪切
        theme = false;//TODO 默认主题
        //theme = true;//TODO 蓝色主题
        selectImageType = false;//TODO 默认样式
        //selectImageType = true;//TODO 自定义样式
        //isCompress = false;//TODO 不压缩
        isCompress = true;  //TODO 压缩
        compressFlag = 1; //  TODO 系统自带压缩
        //compressFlag = 2; // TODO 其他 压缩
    }

    /**
     * 删除图片回调接口
     */

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    // 进入相册
                    /**
                     * type --> 1图片 or 2视频
                     * copyMode -->裁剪比例，默认、1:1、3:4、3:2、16:9
                     * maxSelectNum --> 可选择图片的数量
                     * selectMode         --> 单选 or 多选
                     * isShow       --> 是否显示拍照选项 这里自动根据type 启动拍照或录视频
                     * isPreview    --> 是否打开预览选项
                     * isCrop       --> 是否打开剪切选项
                     * isPreviewVideo -->是否预览视频(播放) mode or 多选有效
                     * ThemeStyle -->主题颜色
                     * CheckedBoxDrawable -->图片勾选样式
                     * cropW-->裁剪宽度 值不能小于100  如果值大于图片原始宽高 将返回原图大小
                     * cropH-->裁剪高度 值不能小于100
                     * isCompress -->是否压缩图片
                     * setEnablePixelCompress 是否启用像素压缩
                     * setEnableQualityCompress 是否启用质量压缩
                     * setRecordVideoSecond 录视频的秒数，默认不限制
                     * setRecordVideoDefinition 视频清晰度  Constants.HIGH 清晰  Constants.ORDINARY 低质量
                     * setImageSpanCount -->每行显示个数
                     * setCheckNumMode 是否显示QQ选择风格(带数字效果)
                     * setPreviewColor 预览文字颜色
                     * setCompleteColor 完成文字颜色
                     * setPreviewBottomBgColor 预览界面底部背景色
                     * setBottomBgColor 选择图片页面底部背景色
                     * setCompressQuality 设置裁剪质量，默认无损裁剪
                     * setSelectMedia 已选择的图片
                     * setCompressFlag 1为系统自带压缩  2为第三方luban压缩
                     * 注意-->type为2时 设置isPreview or isCrop 无效
                     * 注意：Options可以为空，默认标准模式
                     */
                   // int selector = R.drawable.select_cb;
                    FunctionConfig config = new FunctionConfig();
                    config.setType(selectType);
                    config.setCopyMode(copyMode);
                    config.setCompress(isCompress);
                    config.setEnablePixelCompress(true);
                    config.setEnableQualityCompress(true);
                    config.setMaxSelectNum(maxSelectNum);
                    config.setSelectMode(selectMode);
                    config.setShowCamera(isShow);
                    config.setEnablePreview(enablePreview);
                    config.setEnableCrop(enableCrop);
                    config.setPreviewVideo(isPreviewVideo);
                    config.setRecordVideoDefinition(FunctionConfig.HIGH);// 视频清晰度
                    config.setRecordVideoSecond(60);// 视频秒数
                    config.setCropW(cropW);
                    config.setCropH(cropH);
                    config.setCheckNumMode(isCheckNumMode);
                    config.setCompressQuality(100);
                    config.setImageSpanCount(4);
                    config.setSelectMedia(selectMedia);
                    config.setCompressFlag(compressFlag);
                    config.setCompressW(compressW);
                    config.setCompressH(compressH);
                    if (theme) {
                        config.setThemeStyle(ContextCompat.getColor(AddEnergyActivity.this, R.color.blue));
                        // 可以自定义底部 预览 完成 文字的颜色和背景色
                        if (!isCheckNumMode) {
                            // QQ 风格模式下 这里自己搭配颜色，使用蓝色可能会不好看
                            config.setPreviewColor(ContextCompat.getColor(AddEnergyActivity.this, R.color.white));
                            config.setCompleteColor(ContextCompat.getColor(AddEnergyActivity.this, R.color.white));
                            config.setPreviewBottomBgColor(ContextCompat.getColor(AddEnergyActivity.this, R.color.blue));
                            config.setBottomBgColor(ContextCompat.getColor(AddEnergyActivity.this, R.color.blue));
                        }
                    }

                    // 先初始化参数配置，在启动相册
                    PictureConfig.init(config);
                    PictureConfig.getPictureConfig().openPhoto(mContext, resultCallback);
                    // 只拍照
                   //PictureConfig.getPictureConfig().startOpenCamera(mContext, resultCallback);
                    break;
                case 1:
                    // 删除图片
                    selectMedia.remove(position);
                    adapter.notifyItemRemoved(position);
                    break;
            }
        }
    };


    /**
     * 图片回调方法
     */
    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            selectMedia = resultList;
            Log.i("callBack_result", selectMedia.size() + "");
            if (selectMedia != null) {
                adapter.setList(selectMedia);
                adapter.notifyDataSetChanged();
                handler.sendEmptyMessage(0);
                List<LocalMedia> list=new ArrayList<>();
                list=resultList;
                for(int i=0;i<list.size();i++){
                    LogUtils.i("图片路径：==========>"+list.get(i).getPath().toString());
                }
            }
        }
    };


    /**
     * 判断 一个字段的值否为空
     */

    public boolean isNull(String s) {
        if (null == s || s.equals("") || s.equalsIgnoreCase("null")) {
            return true;
        }
        return false;
    }


    @OnClick({R.id.btn_send, R.id.recycler, R.id.iv_locaton, R.id.tv_cancel, R.id.ll_location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                handler.sendEmptyMessage(2);
                break;
            case R.id.recycler:
                break;
            case R.id.iv_locaton:
                break;
            case R.id.tv_cancel:
                tvLocation.setText("所在位置");
                tvCancel.setVisibility(View.GONE);
                tvLocation.setTextColor(getResources().getColor(R.color.color_location));
                ivLocaton.setChecked(false);
                break;
            case R.id.ll_location:

                Intent intent3 = new Intent(AddEnergyActivity.this, MyLocationActivity.class);
                startActivityForResult(intent3, REQUSET);
                break;
        }
    }



}
