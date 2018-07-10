package com.haoji.haoji.network;

import com.google.gson.Gson;
import com.haoji.haoji.common.Constants;
import com.haoji.haoji.util.MyDate;

import java.io.File;
import java.io.IOException;


public class NetIntent {


    public void log(String url) {
        System.out.println("http->log:" + url);
    }

    //登录1
    public void client_login(String phone, String password, NetIntentCallBackListener callBack) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("phone", phone),
                new OkHttpClientManager.Param("password", password)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.login, callBack, params);
    }
    //短信验证登录
    public void client_phoneLogin(String phone, String number, NetIntentCallBackListener callBack) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("phone", phone),
                new OkHttpClientManager.Param("number", number)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.phoneLogin, callBack, params);
    }

    //注册2
    public void client_register(String phone, String password,
                                String number, NetIntentCallBackListener callBackListener) {

        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("phone", phone)
                , new OkHttpClientManager.Param("password", password)
                , new OkHttpClientManager.Param("number", number)};
        for (int i = 0; i < params.length; i++) {
            System.out.println(params[i].key + ":" + params[i].value);
        }
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.register, callBackListener, params);
    }

    //退出3
    public void client_logout(String userid, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param param = new OkHttpClientManager.Param("userid", userid);
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.logout, callBackListener, param);
    }

    //今天正能量列表4
    public void client_energyToday(String userid,  String selectpage, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {
                new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("selectpage", selectpage)};
        OkHttpClientManager.getInstance().getAsyn(Constants.HttpRoot + Constants.energyToday, callBackListener, params);

    }


    //按日期查询正能量列表5
    public void client_energyByDate(String date, String city, String university, String selectpage, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("date", date)
                , new OkHttpClientManager.Param("city", city)
                , new OkHttpClientManager.Param("university", university)
                , new OkHttpClientManager.Param("selectpage", selectpage)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.energyByDate, callBackListener, params);
    }


    //正能量详情6
    public void client_energyDetail(String id, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param param = new OkHttpClientManager.Param("id", id);
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.energyDetail, callBackListener, param);
    }

    //正能量详情6
    public void client_energyDetail(String userid, String id, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("id", id)
                , new OkHttpClientManager.Param("userid", userid)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.energyDetail, callBackListener, params);
    }

    //添加点赞 type 1能量 2视频 3日记 4评论
    public void client_addPraise(String userid, String energyid, String type, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("energyid", energyid)
                , new OkHttpClientManager.Param("type", type)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.addPraise, callBackListener, params);
    }

    //取消点赞8
    public void client_deletePraise(String userid, String energyid,String type, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("energyid", energyid)
                , new OkHttpClientManager.Param("type", type)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.deletePraise, callBackListener, params);

    }

    //添加评论9
    public void client_addComment(String userid, String energyid, String content, String type, String place, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("energyid", energyid)
                , new OkHttpClientManager.Param("content", content)
                , new OkHttpClientManager.Param("type", type)
                , new OkHttpClientManager.Param("place", place)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.addComment, callBackListener, params);
    }

    //删除评论10
    public void client_deleteComment(String userid, String type, String commentid, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("commentid", commentid)
                , new OkHttpClientManager.Param("type", type)
        };
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.deleteComment, callBackListener, params);
    }

    //转发11
    public void client_addForward(String id, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param param = new OkHttpClientManager.Param("id", id);
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.addForward, callBackListener, param);
    }

    //添加收藏12
    public void client_addContain(String userid, String energyid, String type, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("energyid", energyid)
                , new OkHttpClientManager.Param("type", type)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.addContain, callBackListener, params);
    }

    //删除收藏13
    public void client_deleteContain(String userid, String energyid, String type,NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("energyid", energyid)
                , new OkHttpClientManager.Param("type", type)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.deleteContain, callBackListener, params);
    }

    //更多正能量列表14
    public void client_getEnergyByMonth(String userid, String date, String selectpage, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("date", date)
                , new OkHttpClientManager.Param("selectpage", selectpage)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getEnergyByMonth, callBackListener, params);
    }

    //收藏正能量列表15
    public void client_getFavoriteEnergyList(String userid, String selectpage, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("selectpage", selectpage)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getFavoriteEnergyList, callBackListener, params);
    }

    //好友正能量列表16
    public void client_getFriendEnergyList(String userid, String selectpage, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("selectpage", selectpage)};
        OkHttpClientManager.getInstance().getAsyn(Constants.HttpRoot + Constants.getFriendEnergyList, callBackListener, params);
        this.log(Constants.HttpRoot + Constants.getFriendEnergyList);
    }


    //增加正能量17
    public void client_addEnergy(String userid, String content,
                                 String location, String pic1Path, String pic2Path, String pic3Path,String pic4Path, String pic5Path, String pic6Path,String pic7Path, String pic8Path, String pic9Path,
                                  NetIntentCallBackListener callBackListener) {
        try {
            OkHttpClientManager.Param[] params = {
                    new OkHttpClientManager.Param("userid", userid)
                    , new OkHttpClientManager.Param("content", content)
                    , new OkHttpClientManager.Param("place", location)
                    , new OkHttpClientManager.Param("pic1", pic1Path)
                    , new OkHttpClientManager.Param("pic2", pic2Path)
                    , new OkHttpClientManager.Param("pic3", pic3Path)
                     , new OkHttpClientManager.Param("pic4", pic4Path)
                    , new OkHttpClientManager.Param("pic5", pic5Path)
                    , new OkHttpClientManager.Param("pic6", pic6Path)
                     , new OkHttpClientManager.Param("pic7", pic7Path)
                    , new OkHttpClientManager.Param("pic8", pic8Path)
                    , new OkHttpClientManager.Param("pic9", pic9Path)};
            OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.addEnergy, callBackListener, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //上传图片18
    public void client_uploadImg(File file, NetIntentCallBackListener callBackListener) {
        try {
//            File file = new File(filenamePath);
//            Log.i("info==>文件",file.toString());
            OkHttpClientManager.Param param = new OkHttpClientManager.Param("filename", file.getName());
            String filekey = "file";
            OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.uploadImg, callBackListener, file, filekey, param);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //我的正能量列表19
    public void client_getMyEnergyList(String userid, String selectpage, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("selectpage", selectpage)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getMyEnergyList, callBackListener, params);
    }
  //天天正能量
    public void client_getEnergyEverydayList(String userid,String selectpage, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {
             new OkHttpClientManager.Param("userid", userid),
             new OkHttpClientManager.Param("selectpage", selectpage)
        };
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getEnergyEverydayList, callBackListener, params);
    }

    //删除我的正能量20
    public void client_deleteEnergy(String userid, String energyid, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("energyid", energyid)
                , new OkHttpClientManager.Param("type", "1")};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.deleteEnergy, callBackListener, params);
    }

    //视频列表
    public void client_getVideoList(String tag, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("tag", tag)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getVideoList, callBackListener, params);
    }

    //视频列表
    public void client_getVideoList2(String tag, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("tag", tag)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getVideoList2, callBackListener, params);
    }

    //视频详情
    public void client_getVideoById(String id, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param param = new OkHttpClientManager.Param("id", id);
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getVideoById, callBackListener, param);
    }

    //视频详情
    public void client_getVideoById(String userid,String id, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid),new OkHttpClientManager.Param("id", id)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getVideoById, callBackListener, params);
    }

    //日记列表
    public void client_getDiaryList(String tag, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("tag", tag)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getDiaryList, callBackListener, params);
    }

    //日记详情
    public void client_getDiaryById(String id, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param param = new OkHttpClientManager.Param("id", id);
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getDiaryById, callBackListener, param);
    }

    //通讯录
    public void client_getFriendsList(String userid, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param param = new OkHttpClientManager.Param("userid", userid);
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getFriendsList, callBackListener, param);
    }

    //好友详情
    public void client_getFriendsById(String userid, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param param = new OkHttpClientManager.Param("userid", userid);
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getFriendsById, callBackListener, param);
    }

    //上传聊天记录
    public void client_uploadRecord(String userid, String friendid, String content, String updatetime, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("friendid", friendid)
                , new OkHttpClientManager.Param("content", content)
                , new OkHttpClientManager.Param("updatetime", updatetime)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.uploadRecord, callBackListener, params);
    }

    //添加好友
    public void client_addFriend(String userid, String friendid,String remark, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("friendid", friendid)  , new OkHttpClientManager.Param("remark", remark)
        };
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.addFriend, callBackListener, params);
    }

    /*删除好友*/
    public void client_deleteFriend(String userid, String friendid, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("friendid", friendid)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.deleteFriend, callBackListener, params);
    }

    public void client_getUserByPhone(String phone, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param param = new OkHttpClientManager.Param("phone", phone);
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getUserByPhone, callBackListener, param);
    }

    public void client_checkVersion(String version, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param param = new OkHttpClientManager.Param("version", version);
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.checkVersion, callBackListener, param);
    }

    public void client_serviceCall(NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param param = new OkHttpClientManager.Param("test", "");
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.serviceCall, callBackListener, param);
    }

    public void client_ourInfo(NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param param = new OkHttpClientManager.Param("test", "");
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.ourInfo, callBackListener, param);
    }
    /*编辑个人信息*/
    public void client_updateUserById(String userid, String sex, String nickname, String city, String province
            , String phone, String pic, NetIntentCallBackListener callBackListener) {
        if (pic == null)
            pic = "";
        if (phone == null)
            phone = "";
        if (nickname == null)
            nickname = "";
        if (province == null)
            province = "";
        if (sex == sex)
            sex = "1";

        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("sex", sex)
                , new OkHttpClientManager.Param("nickname", nickname)
                , new OkHttpClientManager.Param("province", province)
                , new OkHttpClientManager.Param("city", city)
                , new OkHttpClientManager.Param("phone", phone)
                , new OkHttpClientManager.Param("pic", pic)};
        System.out.println(new Gson().toJson(params).toString());
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.updateUserById, callBackListener, params);
    }

    /**
     * 获取评论
     * @param userid
     * @param energyid
     * @param type 1能量 2视频 3日记
     * @param callBackListener
     */
    public void client_getCommentList(String userid, String energyid,String type, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("energyid", energyid)
                , new OkHttpClientManager.Param("type", type)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getCommentList, callBackListener, params);
    }

    /*获取学校*/
    public void client_getUniversityList(String city,NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("city", city)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getUniversityList, callBackListener, params);
    }

    /*获取学院*/
    public void client_getCollegeList(String universityid, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("universityid", universityid)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getCollegeList, callBackListener, params);
    }

    /*获取专业*/
    public void client_getMajorList(String collegeid, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("collegeid", collegeid)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getMajorList, callBackListener, params);
    }

    /*更新格言*/
    public void client_updateMottoById(String userid, String motto, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("motto", motto)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.updateMottoById, callBackListener, params);
    }

    /*获取用户信息*/
    public String client_getUserById(String userid) throws IOException {
        return OkHttpClientManager.getInstance().getAsString(Constants.HttpRoot + Constants.getUserById + "?userid=" + userid);
    }

    public void client_getUserById(String userid,NetIntentCallBackListener callBackListener){
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getUserById, callBackListener, params);
    }

    /*获取系统信息*/
    public void client_getSysInfo(String userid, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getSysInfo, callBackListener, params);
    }

    /*拒绝好友*/
    public void client_disagreeAddFriend(String userid, String id, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("id", id)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.disagreeAddFriend, callBackListener, params);

    }

    /*同意好友*/
    public void client_agreeAddFriend(String userid, String id, String other, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("id", id)
                , new OkHttpClientManager.Param("other", other)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.agreeAddFriend, callBackListener, params);

    }
    /*判断是否是好友*/
    public void client_isFriend(String userid, String friendid,NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)
                , new OkHttpClientManager.Param("friendid", friendid)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.isFriend, callBackListener, params);

    }

    /*发送验证码*/
    public void client_sendSms(String phone, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("phone", phone)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.sendSms, callBackListener, params);
    }
    /*忘记密码*/
    public void client_forgetPwd(String phone, String number, String password, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("phone", phone)
                , new OkHttpClientManager.Param("number", number)
                , new OkHttpClientManager.Param("password", password)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.forgetPwd, callBackListener, params);
    }
    /*修改密码*/
    public void client_updatePwd(String phone, String newpwd, String oldpwd, NetIntentCallBackListener callBackListener) {
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("phone", phone)
                , new OkHttpClientManager.Param("newpwd", newpwd)
                , new OkHttpClientManager.Param("oldpwd", oldpwd)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.updatePwd, callBackListener, params);
    }

    /**
     * 获取省
     * @param callBackListener
     */
    public void client_getProvinceList(NetIntentCallBackListener callBackListener){
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("date", MyDate.getDateEN())};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getProvinceList, callBackListener, params);
    }

    /**
     * 获取市
     * @param province
     * @param callBackListener
     */
    public void client_getCityList(String province,NetIntentCallBackListener callBackListener){
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("province", province)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getCityList, callBackListener, params);
    }

    /**
     * 调用系统信息
     * @param userid
     * @param callBackListener
     */
    public void client_sendDefaultMsg(String userid, NetIntentCallBackListener callBackListener){
        OkHttpClientManager.Param param = new OkHttpClientManager.Param("userid", userid);
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.processDefaultMsg2, callBackListener, param);
    }

    /**
     * 添加关注
     * @param userid
     * @param friendid
     * @param callBackListener
     */
    public void client_addConcern(String userid,String friendid,NetIntentCallBackListener callBackListener){
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid),
                new OkHttpClientManager.Param("friendid", friendid)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.addConcern, callBackListener, params);
    }

    /**
     * 删除关注
     * @param userid
     * @param friendid
     * @param callBackListener
     */
    public void client_deleteConcern(String userid,String friendid,NetIntentCallBackListener callBackListener){
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid),
                new OkHttpClientManager.Param("friendid", friendid)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.deleteConcern, callBackListener, params);
    }

    /**
     * 获取关注
     * @param userid
     * @param callBackListener
     */
    public void client_getConcernsList(String userid,NetIntentCallBackListener callBackListener){
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getConcernsList, callBackListener, params);
    }
    /**
     * 65、	获取我的未读评论或点赞信息
     * @param userid
     * @param callBackListener
     */
    public void client_getMyUnreadComminfo(String userid,String selectpage,NetIntentCallBackListener callBackListener){
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid),new OkHttpClientManager.Param("selectpage", selectpage)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.getMyUnreadComminfo, callBackListener, params);
    }
    /**
     * 66、	置我的未读评论或点赞信息为已读
     * @param userid
     * @param callBackListener
     */
    public void client_processMyUnreadComminfo(String userid,NetIntentCallBackListener callBackListener){
        OkHttpClientManager.Param[] params = {new OkHttpClientManager.Param("userid", userid)};
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.processMyUnreadComminfo, callBackListener, params);
    }
    /**
     * 67、	意见反馈
     * @param userid
     * @param callBackListener
     */
    public void client_addFeedback(String userid,String opiniontype,String info,String pic1,String pic2,String pic3,String pic4,NetIntentCallBackListener callBackListener){
        OkHttpClientManager.Param[] params = {
                new OkHttpClientManager.Param("userid", userid),
                new OkHttpClientManager.Param("opiniontype", opiniontype),
                new OkHttpClientManager.Param("info", info),
                new OkHttpClientManager.Param("pic1", pic1),
                new OkHttpClientManager.Param("pic2", pic2),
                new OkHttpClientManager.Param("pic3", pic3),
                new OkHttpClientManager.Param("pic4", pic4)
        };
        OkHttpClientManager.getInstance().postAsyn(Constants.HttpRoot + Constants.addFeedback, callBackListener, params);
    }
}
