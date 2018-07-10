package com.haoji.haoji.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.haoji.haoji.common.Constants;

import java.util.HashSet;
import java.util.Set;


/**
 * Created by Administrator on 2016/12/14.
 */

public class SharedPreferencesUtil {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public void setSend(Set<String> send){
        editor.putStringSet("send",send);
        editor.commit();
    }

    public Set<String> getSend(){
        return sp.getStringSet("send", new HashSet<String>());
    }

    /*首次启动*/
    public void setFirst(boolean first){
        editor.putBoolean("first",first);
        editor.commit();
    }

    public boolean getFirst(){
        return sp.getBoolean("first",false);
    }

    public void setBirthday(String birthday){
        editor.putString("birthday",birthday);
        editor.commit();
    }

    public String getBirthday(){
        return sp.getString("birthday",null);
    }

    public void setZone(String zone){
        editor.putString("zone",zone);
        editor.commit();
    }

    public String getZone(){
        return sp.getString("zone",null);
    }

    public void setStudentid(String studentid){
        editor.putString("studentid",studentid);
        editor.commit();
    }

    public String getStudentid(){
        return sp.getString("studentid",null);
    }

    public void setUniversity(String university){
        editor.putString("university",university);
        editor.commit();
    }

    public String getUniversity(){
        return sp.getString("university",null);
    }

    public void setCollege(String college){
        editor.putString("college",college);
        editor.commit();
    }

    public String getCollege(){
        return sp.getString("college",null);
    }

    public void setMajor(String major){
        editor.putString("major",major);
        editor.commit();
    }

    public String getMajor(){
        return sp.getString("major",null);
    }

    public void setMotto(String motto){
        editor.putString("motto",motto);
        editor.commit();
    }

    public String getMotto(){
        return sp.getString("motto",null);
    }

    public void setToken(String Token){
        editor.putString("Token",Token);
        editor.commit();
    }

    /*用户Token*/
    public String getToken(){
        return sp.getString("Token",null);
    }

    public void setExpiresIn(String expiresIn){
        editor.putString("expiresIn",expiresIn);
        editor.commit();
    }



    public void setProvince(String province){
        editor.putString("province",province);
        editor.commit();
    }

    /*省*/
    public String getProvince(){
        return sp.getString("province",null);
    }

    public void setCity(String city){
        editor.putString("city",city);
        editor.commit();
    }

    /*市*/
    public String getCity(){
        return sp.getString("city",null);
    }

    public void setFistStart(boolean fistStart){
        editor.putBoolean("fistStart",fistStart);
        editor.commit();
    }

    public boolean getFistStart(){
        return sp.getBoolean("fistStart",false);
    }

    public void setIsLogin(boolean isLogin){
        editor.putBoolean("isLogin",isLogin);
        editor.commit();
    }

    public boolean getIsLogin(){
        return  sp.getBoolean("isLogin",false);
    }

    public void setUserId(String userId){
        editor.putString("userId",userId);
        editor.commit();
    }

    public String getUserId(){
        return sp.getString("userId",null);
    }

    public void setMobilePhone(String mobilePhone){
        editor.putString("mobilePhone",mobilePhone);
        editor.commit();
    }

    public String getMobilePhone(){
        return sp.getString("mobilePhone",null);
    }

    public void setUserName(String userName){
        editor.putString("userName",userName);
        editor.commit();
    }

    public String getUserName(){
        return sp.getString("userName",null);
    }

    public void setNickName(String nickName){
        editor.putString("nickName",nickName);
        editor.commit();
    }

    public String getNickName(){
        return sp.getString("nickName",null);
    }

    public void setRealName(String realName){
        editor.putString("realName",realName);
        editor.commit();
    }

    public String getRealName(){
        return sp.getString("realName",null);
    }

    public void setPassWord(String passWord){
        editor.putString("passWord",passWord);
        editor.commit();
    }

    public String getPassWord(){
        return sp.getString("passWord",null);
    }

    public void setPicture(String picture){
        editor.putString("picture",picture);
        editor.commit();
    }

    public String getPicture(){
        return sp.getString("picture",null);
    }

    public void setEmail(String email){
        editor.putString("email",email);
        editor.commit();
    }

    public String getEmail(){
        return sp.getString("email",null);
    }

    public void setAddress(String address){
        editor.putString("address",address);
        editor.commit();
    }

    public String getAddress(){
        return sp.getString("address",null);
    }



    public int getSexInt(){
        return sp.getInt("sex",0);
    }

    public void setSex(int sex){
        editor.putInt("sex",sex);
        editor.commit();
    }

    public String getSex(){
        String sex="";
        switch (sp.getInt("sex",0)){
            case 0:
                sex="未知";
                break;
            case 1:
                sex="男";
                break;
            case 2:
                sex="女";
                break;
        }
        return sex;
    }

    public SharedPreferencesUtil(Context context){
        sp=context.getSharedPreferences(Constants.User,context.MODE_PRIVATE);
        editor=sp.edit();
    }

    public void setDisplayWidth(int DisplayWidth){
        editor.putInt("DisplayWidth",DisplayWidth);
        editor.commit();
    }

    public void setDisplayHeight(int DisplayHeight){
        editor.putInt("DisplayHeight",DisplayHeight);
        editor.commit();
    }

    public int getDisplayHeight(){
        return sp.getInt("DisplayHeight",-1);
    }

    public int getDisplayWidth(){
        return sp.getInt("DisplayWidth",-1);
    }
    //设置刷新时间
    public String getTime(){
        return sp.getString("refreshTime",null);

    }
    public void setTime(String time){
        editor.putString("refreshTime",time);
        editor.commit();
    }
}
