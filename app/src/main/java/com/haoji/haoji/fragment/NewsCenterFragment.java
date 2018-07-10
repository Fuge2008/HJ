package com.haoji.haoji.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.haoji.haoji.R;
import com.haoji.haoji.base.BaseFragment;
import com.haoji.haoji.network.NetIntent;
import com.haoji.haoji.network.NetIntentCallBackListener;
import com.haoji.haoji.ui.LoginActivity;
import com.haoji.haoji.ui.TXLActivity;
import com.haoji.haoji.util.SharedPreferencesUtil;
import com.haoji.haoji.util.ToastUtils;
import com.squareup.okhttp.Request;

import java.util.Set;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;




public class NewsCenterFragment extends BaseFragment implements NetIntentCallBackListener.INetIntentCallBack{


    private ConversationListFragment fragment;

    private SharedPreferencesUtil util;

    @Override
    public int InitMainView() {
        return R.layout.fragment_tab3;
    }

    @Override
    public void initUI() {

        rootview.findViewById(R.id.btn_txl).setOnClickListener(this);
        util = new SharedPreferencesUtil(getActivity());
        Set<String> set=util.getSend();
        if (util.getIsLogin()&& set.add(util.getUserId())){
            new NetIntent().client_sendDefaultMsg(util.getUserId(),new NetIntentCallBackListener(this));
            util.setSend(set);
            loadlist();
            System.out.println("发送完成");
        }else{
            System.out.println("已经发送过");
        }

    }

    @Override
    public void onResume() {
        if(util.getIsLogin()){
            loadlist();
        }
        super.onResume();
    }

    @Override
    public void loadData() {


    }

    private void loadlist() {
        fragment = (ConversationListFragment) getChildFragmentManager().findFragmentById(R.id.conversationlist);
        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false").build();
        fragment.setUri(uri);
    }

    @Override
    public void onClick(View v) {
        if (util.getIsLogin()) {
            switch (v.getId()) {
                case R.id.btn_txl:
                    startActivity(new Intent(getActivity(), TXLActivity.class));
                    break;
            }
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            ToastUtils.showShortToast(getActivity(),"抱歉，请您先登录！！");
        }
    }

    @Override
    public void onError(Request request, Exception e) {

    }

    @Override
    public void onResponse(String response) {
        System.out.println(response);
    }
}
