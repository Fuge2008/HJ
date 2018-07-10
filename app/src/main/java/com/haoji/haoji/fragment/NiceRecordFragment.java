package com.haoji.haoji.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseFragment;
import com.haoji.haoji.comment.SocialMainActivity;
import com.haoji.haoji.custom.DragPointView;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.ui.DayDayEnergyActivity;
import com.haoji.haoji.ui.LoginActivity;
import com.haoji.haoji.util.SPUtils;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.StringUtils;
import com.haoji.haoji.util.ToastUtils;
import com.squareup.okhttp.Request;

public class NiceRecordFragment extends BaseFragment implements DragPointView.OnDragListencer,NetIntentCallBackListener.INetIntentCallBack{
    private DragPointView mUnreadNumView;
    private SharedPreferencesUtil util;
    private Context context;



    @Override
    public int InitMainView() {
        return R.layout.fragment_tab1;
    }

    @Override
    public void initUI() {
        context=getActivity();
        util=new SharedPreferencesUtil(getActivity());
        mUnreadNumView = (DragPointView) rootview.findViewById(R.id.seal_num);
        //mUnreadNumView.setText("10");
        mUnreadNumView.setDragListencer(this);
        rootview.findViewById(R.id.lin_click_one).setOnClickListener(this);
        rootview.findViewById(R.id.lin_click_two).setOnClickListener(this);
        String infoCnt = (String) SPUtils.get(getActivity(),"infoCnt","");
        if(StringUtils.isNotEmpty(infoCnt,true)){
            int infoNum=Integer.valueOf(infoCnt);
            onCountChanged(infoNum);
        }else{
            onCountChanged(0);
        }

    }

    @Override
    public void loadData() {

    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.lin_click_one:

                if(util.getIsLogin()) {
                    intent=new Intent(getActivity(), DayDayEnergyActivity.class);
                    intent.putExtra("type",0);
                }else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    ToastUtils.showShortToastSafe(getActivity(),"尚未登录！");
                }
                break;
            case R.id.lin_click_two:
                if(util.getIsLogin()) {
                    startActivity(new Intent(getActivity(), SocialMainActivity.class));
                }else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    ToastUtils.showShortToastSafe(getActivity(),"尚未登录！");
                }

                break;
        }
        if (intent!=null){
            startActivity(intent);
        }
    }


    public void onCountChanged(int count) {
        if (count == 0) {
            mUnreadNumView.setVisibility(View.GONE);
        } else if (count > 0 && count < 100) {
            mUnreadNumView.setVisibility(View.VISIBLE);
            mUnreadNumView.setText(String.valueOf(count));
        } else {
            mUnreadNumView.setVisibility(View.VISIBLE);
            mUnreadNumView.setText("99+");
        }
    }





    @Override
    public void onDragOut() {
        mUnreadNumView.setVisibility(View.GONE);
        ToastUtils.showShortToast(getActivity(), "清理成功");
        SPUtils.put(getActivity(),"infoCnt","");
        //new NetIntent().client_processMyUnreadComminfo(util.getUserId(),new NetIntentCallBackListener());
    }


    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {

    }

    @Override
    public void onStart() {
        super.onStart();
//        String infoCnt = (String) SPUtils.get(getActivity(),"infoCnt","");
//        if(StringUtils.isNotEmpty(infoCnt,true)){
//            int infoNum=Integer.valueOf(infoCnt);
//            onCountChanged(infoNum);
//        }

    }


}
