package com.haoji.haoji.network;

import com.squareup.okhttp.Request;



public class NetIntentCallBackListener extends OkHttpClientManager.ResultCallback<String>{

    private INetIntentCallBack iNetIntentCallBack;

    public NetIntentCallBackListener(INetIntentCallBack iNetIntentCallBack){
        this.iNetIntentCallBack=iNetIntentCallBack;
    }

    @Override
    public void onError(Request request, Exception e) {
        if (this.iNetIntentCallBack!=null){
            this.iNetIntentCallBack.onError(request,e);
        }
    }

    @Override
    public void onResponse(String response) {
        if (this.iNetIntentCallBack!=null){
            this.iNetIntentCallBack.onResponse(response);
        }
    }

    public void setiNetIntentCallBack(INetIntentCallBack iNetIntentCallBack) {
        this.iNetIntentCallBack = iNetIntentCallBack;
    }

    public interface INetIntentCallBack{

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(String response);
    }

}
