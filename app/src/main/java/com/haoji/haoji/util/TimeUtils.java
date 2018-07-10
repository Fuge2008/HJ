package com.haoji.haoji.util;

import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeUtils {
    public static String getNowTime() {//TODO 获取当前时间
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
  public static String getNowTimeOfMillis() {//TODO 获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return date.toString();
    }

    public static String getTime(String rel_time, String now_time) {
        String backStr = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(rel_time);
            d2 = format.parse(now_time);

            // 毫秒ms
            long diff = d2.getTime() - d1.getTime();
            if (diff < 0) {
                backStr = "刚刚";
            } else {

                long diffMinutes = diff / (60 * 1000) % 60;
                long diffHours = diff / (60 * 60 * 1000) % 24;
                long diffDays = diff / (24 * 60 * 60 * 1000);

                if (diffDays != 0) {
                    if (diffDays < 30) {
                        if (1 < diffDays && diffDays < 2) {
                            backStr = "昨天";
                        } else if (1 < diffDays && diffDays < 2) {
                            backStr = "前天";

                        } else {

                            backStr = String.valueOf(diffDays) + "天前";
                        }
                    } else {
                        backStr = "很久以前";
                    }

                } else if (diffHours != 0) {
                    backStr = String.valueOf(diffHours) + "小时前";

                } else if (diffMinutes != 0) {
                    backStr = String.valueOf(diffMinutes) + "分钟前";

                } else {

                    backStr = "刚刚";

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return backStr;

    }
}
