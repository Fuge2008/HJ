package com.haoji.haoji.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;

import com.haoji.haoji.model.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fzwoo on 2017/5/4.
 */

public class GetPhoneContect {
    //-------------------------------------------------------------  获取sim
    /**
     * 获取库Phone表字段
     **/
    private static final String[] PHONES_PROJECTION = new String[]{
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID};
    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;
    /**
     * 头像ID
     **/
    private static final int PHONES_PHOTO_ID_INDEX = 2;
    /**
     * 联系人的ID
     **/
    private static final int PHONES_CONTACT_ID_INDEX = 3;
    static ArrayList<Person> list = new ArrayList<Person>();//创造存放数据的列表
    @SuppressLint("NewApi")
    public List<Person> GetPhoneContect(Context context) {
            //进行查询
            //----------------------------------  查询SIM卡
            ContentResolver resolver = context.getContentResolver();
            // 获取Sims卡联系人
            Uri uri1 = Uri.parse("content://icc/adn");
            Cursor phoneCursor = resolver.query(uri1, PHONES_PROJECTION, null, null,
                    null);
            if (phoneCursor != null) {
                while (phoneCursor.moveToNext()) {
                    Person p = new Person();//创建一个对象
                    // 得到手机号码
                    String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                    // 当手机号码为空的或者为空字段 跳过当前循环
                    if (TextUtils.isEmpty(phoneNumber))
                        continue;
                    // 得到联系人名称
                    String contactName = phoneCursor
                            .getString(PHONES_DISPLAY_NAME_INDEX);

                    //Sim卡中没有联系人头像

                    p.setName(contactName);//将名字放入
                    p.setPhone(phoneNumber);//将电话号码放入里面
                    list.add(p);//将信息全部放进去
                }

                phoneCursor.close();
            }
            android.util.Log.e("SIM卡中的联系人的个数：", "" + list.size());
            //获取  本地 联系人
            Uri uri = ContactsContract.Data.CONTENT_URI;//2.0以上系统使用ContactsContract.Data访问联系人
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, "display_name");//显示联系人时按显示名字排序
            cursor.moveToFirst();
            List<Person> list = new ArrayList<Person>();
            int Index_CONTACT_ID = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);//获得CONTACT_ID在ContactsContract.Data中的列数
            int Index_DATA1 = cursor.getColumnIndex(ContactsContract.Data.DATA1);//获得DATA1在ContactsContract.Data中的列数
            int Index_MIMETYPE = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);//获得MIMETYPE在ContactsContract.Data中的列数
            while (cursor.getCount() > cursor.getPosition()) {
                Person person = null;
                String id = cursor.getString(Index_CONTACT_ID);//获得CONTACT_ID列的内容
                String info = cursor.getString(Index_DATA1);//获得DATA1列的内容
                String mimeType = cursor.getString(Index_MIMETYPE);//获得MIMETYPE列的内容
                //遍历查询当前行对应的联系人信息是否已添加到list中
                for (int n = 0; n < list.size(); n++) {
                    if (list.get(n).getID() != null) {
                        if (list.get(n).getID().equals(id)) {
                            person = list.get(n);
                            break;
                        }
                    }
                }
                if (person == null) {
                    person = new Person();
                    person.setID(id);
                    list.add(person);
                }
                if (mimeType.equals("vnd.android.cursor.item/email_v2"))//该行数据为邮箱
                {
                    person.setEmail(info);
                } else if (mimeType.equals("vnd.android.cursor.item/postal-address_v2"))//该行数据为地址
                {
                    person.setAddress(info);
                } else if (mimeType.equals("vnd.android.cursor.item/phone_v2"))//该行数据为电话号码
                {
                    person.setPhone(info);
                } else if (mimeType.equals("vnd.android.cursor.item/name"))//该行数据为名字
                {
                   LogUtils.e("名字", info);//将联系人的名字打印出来
                    person.setName(info);
                }
                cursor.moveToNext();
            }
            //获得联系人
            android.util.Log.i("联系人的数量", list.size() + "");


        return  list;
    }
}