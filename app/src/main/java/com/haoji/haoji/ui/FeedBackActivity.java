package com.haoji.haoji.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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


public class FeedBackActivity extends BaseActivity implements CheckBox.OnCheckedChangeListener, NetIntentCallBackListener.INetIntentCallBack{

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.cb1)
    CheckBox cb1;
    @BindView(R.id.cb2)
    CheckBox cb2;
    @BindView(R.id.cb3)
    CheckBox cb3;
    @BindView(R.id.cb4)
    CheckBox cb4;
    @BindView(R.id.cb5)
    CheckBox cb5;
    @BindView(R.id.cb6)
    CheckBox cb6;
    @BindView(R.id.cb7)
    CheckBox cb7;
    @BindView(R.id.cb8)
    CheckBox cb8;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.et_info)
    EditText etInfo;
    private String   strContent,  strPic1="",  strPic2="",  strPic3="", strPic4="";
    private SharedPreferencesUtil util;
    private CustomProgress dialog;
    private List<String> path;
    private  int MAX_COUNT = 100;
    private String type_opinion="";
    private JSONObject jsonObject;


    //选取相片
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private int selectMode = FunctionConfig.MODE_MULTIPLE;
    private int maxSelectNum = 4;// 图片最大可选数量
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


    @Override
    public void initMainView() {
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
    }

    @Override
    public void initUi() {
        cb1.setOnCheckedChangeListener(this);
        cb2.setOnCheckedChangeListener(this);
        cb3.setOnCheckedChangeListener(this);
        cb4.setOnCheckedChangeListener(this);
        cb5.setOnCheckedChangeListener(this);
        cb6.setOnCheckedChangeListener(this);
        cb7.setOnCheckedChangeListener(this);
        cb8.setOnCheckedChangeListener(this);

        util = new SharedPreferencesUtil(this);
        path=new ArrayList<>();
        //选取相片
        mContext = this;
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        initData();
        FullyGridLayoutManager manager = new FullyGridLayoutManager(FeedBackActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(FeedBackActivity.this, onAddPicClickListener);
        adapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                // 这里可预览图片
                PictureConfig.getPictureConfig().externalPicturePreview(mContext, position, selectMedia);
            }
        });
        countWord();

    }

    private void countWord() {
        etInfo.addTextChangedListener(mTextWatcher);
        etInfo.setSelection(etInfo.length()); // 将光标移动最后一个字符后面
        iv_back = (ImageView) findViewById(R.id.iv_back);
      
            tvCount.setText(MAX_COUNT+"");
    
    }
    /**
     * 监听统计字数
     */
    private TextWatcher mTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            int editStart =  etInfo.getSelectionStart();
            int editEnd =  etInfo.getSelectionEnd();

            // 先去掉监听器，否则会出现栈溢出
             etInfo.removeTextChangedListener(mTextWatcher);

            // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
            // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
            if(calculateLength(s.toString())>=MAX_COUNT){
                ToastUtils.showShortToast(FeedBackActivity.this,"字数已达"+MAX_COUNT);
            }

            while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
            }
             etInfo.setText(s);
             etInfo.setSelection(editStart);

            // 恢复监听器
             etInfo.addTextChangedListener(mTextWatcher);
            setLeftCount();
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

    };

    /**
     * 计算分享内容的字数
     *
     * @param c
     * @return
     */
    private long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
//            if (tmp > 0 && tmp < 127) {
//                len += 0.5;
//            } else {
                len++;
//            }
        }
        return Math.round(len);
    }

    /**
     * 刷新剩余输入字数,最大值100个字
     */
    private void setLeftCount() {
        tvCount.setText(String.valueOf((MAX_COUNT - getInputCount())));
    }

    /**
     * 获取用户输入的内容字数
     *
     * @return
     */
    private long getInputCount() {
        return calculateLength( etInfo.getText().toString());
    }

    @Override
    public void loadData() {

    }

    @OnClick({R.id.iv_back, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_send:
                LogUtils.i("发送请求中。。。。");
                handler.sendEmptyMessage(2);
                break;
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
        //copyMode = FunctionConfig.COPY_MODEL_16_9;// TODO 裁剪 16：9
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
                        config.setThemeStyle(ContextCompat.getColor(FeedBackActivity.this, R.color.blue));
                        // 可以自定义底部 预览 完成 文字的颜色和背景色
                        if (!isCheckNumMode) {
                            // QQ 风格模式下 这里自己搭配颜色，使用蓝色可能会不好看
                            config.setPreviewColor(ContextCompat.getColor(FeedBackActivity.this, R.color.white));
                            config.setCompleteColor(ContextCompat.getColor(FeedBackActivity.this, R.color.white));
                            config.setPreviewBottomBgColor(ContextCompat.getColor(FeedBackActivity.this, R.color.blue));
                            config.setBottomBgColor(ContextCompat.getColor(FeedBackActivity.this, R.color.blue));
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
    String type1 = "",type2 = "",type3 = "",type4 = "",type5 = "",type6 = "",type7 = "",type8 = "";
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()){
            case R.id.cb1:
                if(isChecked){
                    type1=",1";
                }else {
                    type1="";
                }
                break;
            case R.id.cb2:
                if(isChecked){
                    type2=",2";
                }else {
                    type2="";
                }
                break;
            case R.id.cb3:
                if(isChecked){
                    type3=",3";
                }else {
                    type3="";
                }
                break;
            case R.id.cb4:
                if(isChecked){
                    type4=",4";
                }else {
                    type4="";
                }
                break;
            case R.id.cb5:
                if(isChecked){
                    type5=",5";
                }else {
                    type5="";
                }
                break;
            case R.id.cb6:
                if(isChecked){
                    type6=",6";
                }else {
                    type6="";
                }
                break;
            case R.id.cb7:
                if(isChecked){
                    type7=",7";
                }else {
                    type7="";
                }
                break;
            case R.id.cb8:
                if(isChecked){
                    type8=",8";
                }else {
                    type8="";
                }
                break;
        }
        String str=type1+type2+type3+type4+type5+type6+type7+type8;
        if(StringUtils.isNotEmpty(str,true)){
            type_opinion=str.substring(1,str.length());
        }
        
    }

    @Override
    public void onError(Request request, Exception e) {
        if (dialog != null)
            dialog.dismiss();

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
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    dialog = CustomProgress.show(FeedBackActivity.this, "处理中..", true, null);
                    picNum=selectMedia.size();
                    if(selectMedia!=null & picNum>0){
                        for(int i=0;i<picNum;i++){
                            picOrder=i;
                            NetIntent netIntent = new NetIntent();
                            File file=new File(selectMedia.get(i).getPath().toString());
                            netIntent.client_uploadImg(file, new NetIntentCallBackListener(FeedBackActivity.this));//TODO  上传图片
                        }

                    }
                    break;
                case 1:
                    ToastUtils.showShortToastSafe(FeedBackActivity.this,"谢谢您的宝贵意见，我们将尽快为您解决您所遇到的问题！");
                    finish();
                    break;
                case 2:
                    strContent=etInfo.getText().toString();
                    if (StringUtils.isNotEmpty(strContent,true)|| StringUtils.isNotEmpty(type_opinion,true)) {
                        dialog = CustomProgress.show(FeedBackActivity.this, "上传中..", true, null);
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

                        //ToastUtils.showShortToast(FeedBackActivity.this,"谢谢您的宝贵意见，我们将尽快为您解决您所遇到的问题！");
                        Log.i("提交数据：","userid:"+util.getUserId()+"type_opinion:"+type_opinion+"strContent:"+strContent+"strPic1:"+strPic1+"strPic2:"+strPic2);
                        new NetIntent().client_addFeedback(util.getUserId(),type_opinion, strContent,strPic1,  strPic2,  strPic3,  strPic4,  new NetIntentCallBackListener(FeedBackActivity.this));
                    }else{
                        ToastUtils.showShortToastSafe(FeedBackActivity.this,"反馈标签或描述不能都为空！");
                    }

                    break;

            }
        }
    };



}
