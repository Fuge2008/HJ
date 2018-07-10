package com.haoji.haoji.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/4/7.
 */

public class MyComparator implements Comparator<HashMap> {

    @Override
    public int compare(HashMap lhs, HashMap rhs) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = dateFormat.parse(lhs.get("updatetime").toString());
            date2 = dateFormat.parse(rhs.get("updatetime").toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //反向排序
        if (date1.getTime()>date2.getTime()){
            return -1;
        }else if (date1.getTime()<date2.getTime()){
            return 1;
        }else {
            return 0;
        }
    }
}
