package com.haoji.haoji.download.util.content.analyzer.app;

import android.content.Context;
import android.util.Patterns;


import com.haoji.haoji.R;
import com.haoji.haoji.download.core.contract.AbstractSingleton;
import com.haoji.haoji.download.core.exception.HttpException;
import com.haoji.haoji.download.core.exception.URLInvalidException;
import com.haoji.haoji.download.util.contract.VideoParser;
import com.haoji.haoji.download.util.exception.VideoException;
import com.haoji.haoji.download.util.model.app.DouyinUser;
import com.haoji.haoji.download.util.model.app.DouyinVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Douyin extends VideoParser {

    public Douyin(Context context) throws SingletonException {
        super(context);
    }

    public static Douyin getInstance(Context context){
        try {
            return AbstractSingleton.getInstance(Douyin.class, new Class<?>[]{Context.class}, new Object[]{context});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public DouyinVideo get(String str) throws Exception
    {
        String url = this.stripUrl(str);
        if (url == null)
            throw new URLInvalidException(this.getString(R.string.exception_invalid_url));

        String html = new OkHttpClient().newCall(new Request.Builder().url(url).build()).execute().body().string();

        if (html == null || html.isEmpty())
            throw new HttpException(this.getString(R.string.exception_http));

        if (!html.contains("?video_id="))
            throw new VideoException(this.getString(R.string.exception_douyin_url));

        return parseVideo(parseJSON(html));
    }

    private String stripUrl(String shareUrl)
    {
        Pattern pattern = Patterns.WEB_URL;
        Matcher matcher = pattern.matcher(shareUrl);
        if (matcher.find())
            return matcher.group(0);
        return null;
    }

    protected JSONObject parseJSON(String html) throws JSONException {
        Pattern pattern = Pattern.compile("var\\sdata\\s=\\s(.*);\\s*require\\(", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String jsonStr = matcher.group(1);
            JSONArray json = new JSONArray(jsonStr);

            if (json.length() <= 0)
                throw new VideoException(this.getString(R.string.exception_douyin_url));

            return json.length() <= 0 ? null : json.getJSONObject(0);
        } else {
            throw new VideoException(this.getString(R.string.exception_douyin_url));
        }
    }

    protected DouyinVideo parseVideo(JSONObject json) throws JSONException
    {
        JSONObject v = json.getJSONObject("video");

        DouyinVideo video = new DouyinVideo();
        video.setId(v.getJSONObject("play_addr").getString("uri"));
        video.setTitle(json.getString("desc"));
        video.setCoverUrl(v.getJSONObject("cover").getJSONArray("url_list").getString(0));
        video.setDynamicCoverUrl(video.getCoverUrl());
        // No throw
        try {
            video.setWidth(v.getInt("width"));
            video.setHeight(v.getInt("height"));
            video.setDynamicCoverUrl(v.getJSONObject("dynamic_cover").getJSONArray("url_list").getString(0));
            //video.setCreate_time(new Date(json.getLong("create_time") * 1000));
            video.setGroup_id(json.getLong("group_id"));
            video.setAweme_id(json.getLong("aweme_id"));
            video.setMedia_type(json.getInt("media_type"));
        } catch (Exception e) {e.printStackTrace();}

        video.setUser(parseUser(json));

        return video;
    }

    protected DouyinUser parseUser(JSONObject json) throws JSONException
    {
        JSONObject u = json.getJSONObject("author");

        DouyinUser user = new DouyinUser();

        user.setId(u.getString("uid"));
        user.setNickname(u.getString("nickname"));
        user.setAvatarUrl(u.getJSONObject("avatar_larger").getJSONArray("url_list").getString(0));
        user.setAvatarThumbUrl(user.getAvatarUrl());
        try {
            user.setAvatarThumbUrl(u.getJSONObject("avatar_thumb").getJSONArray("url_list").getString(0));
        } catch (Exception e) {}

        return user;
    }


}
