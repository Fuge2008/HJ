package com.haoji.haoji.util;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;
import android.widget.TextView;

public class StringUtils {
	public static final String TAG = "StringUtil";
	public static final String YUAN = "元";
	private static String currentString = "";
	
	public static String URL="url";
	public static String TITLE="title";

	/** 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false */
	public static boolean isEmpty(String value) {
		if (value != null && !"".equalsIgnoreCase(value.trim()) && !"null".equalsIgnoreCase(value.trim())) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * 判断两字符串是否相等
	 *
	 * @param a
	 *            待校验字符串a
	 * @param b
	 *            待校验字符串b
	 * @return @return {@code true}: 相等<br>
	 * 		{@code false}: 不相等
	 */
	public static boolean equals(CharSequence a, CharSequence b) {
		if (a == b)
			return true;
		int length;
		if (a != null && b != null && (length = a.length()) == b.length()) {
			if (a instanceof String && b instanceof String) {
				return a.equals(b);
			} else {
				for (int i = 0; i < length; i++) {
					if (a.charAt(i) != b.charAt(i))
						return false;
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 判断两字符串是否不相等
	 *
	 * @param a
	 *            待校验字符串a
	 * @param b
	 *            待校验字符串b
	 * @return @return {@code true}: 不相等<br>
	 * 		{@code false}: 相等
	 */
	public static boolean isNotEquals(CharSequence a, CharSequence b) {
		if (a == b)
			return false;
		int length;
		if (a != null && b != null && (length = a.length()) == b.length()) {
			if (a instanceof String && b instanceof String) {
				return a.equals(b);
			} else {
				for (int i = 0; i < length; i++) {
					if (a.charAt(i) == b.charAt(i))
						return false;
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取刚传入处理后的string
	 * 
	 * @must 上个影响currentString的方法 和 这个方法都应该在同一线程中，否则返回值可能不对
	 * @return
	 */
	public static String getCurrentString() {
		return currentString == null ? "" : currentString;
	}

	// 获取string,为null时返回"" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	/**
	 * 获取string,为null则返回""
	 * 
	 * @param tv
	 * @return
	 */
	public static String getString(TextView tv) {
		if (tv == null || tv.getText() == null) {
			return "";
		}
		return getString(tv.getText().toString());
	}

	/**
	 * 获取string,为null则返回""
	 * 
	 * @param object
	 * @return
	 */
	public static String getString(Object object) {
		return object == null ? "" : getString(String.valueOf(object));
	}

	/**
	 * 获取string,为null则返回""
	 * 
	 * @param cs
	 * @return
	 */
	public static String getString(CharSequence cs) {
		return cs == null ? "" : getString(cs.toString());
	}

	/**
	 * 获取string,为null则返回""
	 * 
	 * @param s
	 * @return
	 */
	public static String getString(String s) {
		return s == null ? "" : s;
	}

	// 获取string,为null时返回"" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	// 获取去掉前后空格后的string<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	/**
	 * 获取去掉前后空格后的string,为null则返回""
	 * 
	 * @param tv
	 * @return
	 */
	public static String getTrimedString(TextView tv) {
		return getTrimedString(getString(tv));
	}

	/**
	 * 获取去掉前后空格后的string,为null则返回""
	 * 
	 * @param object
	 * @return
	 */
	public static String getTrimedString(Object object) {
		return getTrimedString(getString(object));
	}

	/**
	 * 获取去掉前后空格后的string,为null则返回""
	 * 
	 * @param cs
	 * @return
	 */
	public static String getTrimedString(CharSequence cs) {
		return getTrimedString(getString(cs));
	}

	/**
	 * 获取去掉前后空格后的string,为null则返回""
	 * 
	 * @param s
	 * @return
	 */
	public static String getTrimedString(String s) {
		return getString(s).trim();
	}

	// 获取去掉前后空格后的string>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	// 获取去掉所有空格后的string <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	/**
	 * 获取去掉所有空格后的string,为null则返回""
	 * 
	 * @param tv
	 * @return
	 */
	public static String getNoBlankString(TextView tv) {
		return getNoBlankString(getString(tv));
	}

	/**
	 * 获取去掉所有空格后的string,为null则返回""
	 * 
	 * @param object
	 * @return
	 */
	public static String getNoBlankString(Object object) {
		return getNoBlankString(getString(object));
	}

	/**
	 * 获取去掉所有空格后的string,为null则返回""
	 * 
	 * @param cs
	 * @return
	 */
	public static String getNoBlankString(CharSequence cs) {
		return getNoBlankString(getString(cs));
	}

	/**
	 * 获取去掉所有空格后的string,为null则返回""
	 * 
	 * @param s
	 * @return
	 */
	public static String getNoBlankString(String s) {
		return getString(s).replaceAll(" ", "");
	}

	// 获取去掉所有空格后的string >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	// 获取string的长度<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	/**
	 * 获取string的长度,为null则返回0
	 * 
	 * @param tv
	 * @param trim
	 * @return
	 */
	public static int getLength(TextView tv, boolean trim) {
		return getLength(getString(tv), trim);
	}

	/**
	 * 获取string的长度,为null则返回0
	 * 
	 * @param object
	 * @param trim
	 * @return
	 */
	public static int getLength(Object object, boolean trim) {
		return getLength(getString(object), trim);
	}

	/**
	 * 获取string的长度,为null则返回0
	 * 
	 * @param cs
	 * @param trim
	 * @return
	 */
	public static int getLength(CharSequence cs, boolean trim) {
		return getLength(getString(cs), trim);
	}

	/**
	 * 获取string的长度,为null则返回0
	 * 
	 * @param s
	 * @param trim
	 * @return
	 */
	public static int getLength(String s, boolean trim) {
		s = trim ? getTrimedString(s) : s;
		return getString(s).length();
	}

	// 获取string的长度>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	// 判断字符是否非空 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	/**
	 * 判断字符是否非空
	 * 
	 * @param tv
	 * @param trim
	 * @return
	 */
	public static boolean isNotEmpty(TextView tv, boolean trim) {
		return isNotEmpty(getString(tv), trim);
	}

	/**
	 * 判断字符是否非空
	 * 
	 * @param object
	 * @param trim
	 * @return
	 */
	public static boolean isNotEmpty(Object object, boolean trim) {
		return isNotEmpty(getString(object), trim);
	}

	/**
	 * 判断字符是否非空
	 * 
	 * @param cs
	 * @param trim
	 * @return
	 */
	public static boolean isNotEmpty(CharSequence cs, boolean trim) {
		return isNotEmpty(getString(cs), trim);
	}

	/**
	 * 判断字符是否非空
	 * 
	 * @param s
	 * @param trim
	 * @return
	 */
	public static boolean isNotEmpty(String s, boolean trim) {
		// Log.i(TAG, "getTrimedString s = " + s);
		if (s == null) {
			return false;
		}
		if (trim) {
			s = s.trim();
		}
		if (s.length() <= 0) {
			return false;
		}

		currentString = s;

		return true;
	}

	// 判断字符是否非空 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	// 判断字符类型 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	// 判断手机格式是否正确
	public static boolean isPhone(String phone) {
		if (isNotEmpty(phone, true) == false) {
			return false;
		}

		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-2,5-9])|(17[0-9]))\\d{8}$");

		currentString = phone;

		return p.matcher(phone).matches();
	}

	// 判断email格式是否正确
	public static boolean isEmail(String email) {
		if (isNotEmpty(email, true) == false) {
			return false;
		}

		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);

		currentString = email;

		return p.matcher(email).matches();
	}

	// 判断是否全是数字
	public static boolean isNumber(String number) {
		if (isNotEmpty(number, true) == false) {
			return false;
		}

		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(number);
		if (!isNum.matches()) {
			return false;
		}

		currentString = number;

		return true;
	}

	/**
	 * 判断字符类型是否是号码或字母
	 * 
	 * @param inputed
	 * @return
	 */
	public static boolean isNumberOrAlpha(String inputed) {
		if (inputed == null) {
			Log.e(TAG, "isNumberOrAlpha  inputed == null >> return false;");
			return false;
		}
		Pattern pNumber = Pattern.compile("[0-9]*");
		Matcher mNumber;
		Pattern pAlpha = Pattern.compile("[a-zA-Z]");
		Matcher mAlpha;
		for (int i = 0; i < inputed.length(); i++) {
			mNumber = pNumber.matcher(inputed.substring(i, i + 1));
			mAlpha = pAlpha.matcher(inputed.substring(i, i + 1));
			if (!mNumber.matches() && !mAlpha.matches()) {
				return false;
			}
		}

		currentString = inputed;
		return true;
	}

	/**
	 * 判断字符类型是否是身份证号
	 * 
	 * @param idCard
	 * @return
	 */
	public static boolean isIDCard(String idCard) {
		if (isNumberOrAlpha(idCard) == false) {
			return false;
		}
		idCard = getString(idCard);
		if (idCard.length() == 15) {
			Log.w(TAG, "isIDCard idCard.length() == 15 old IDCard");
			currentString = idCard;
			return true;
		}
		if (idCard.length() == 18) {
			currentString = idCard;
			return true;
		}

		return false;
	}

	public static final String HTTP = "http";
	public static final String URL_PREFIX = "http://";
	public static final String URL_PREFIXs = "https://";
	public static final String URL_STAFFIX = URL_PREFIX;
	public static final String URL_STAFFIXs = URL_PREFIXs;

	/**
	 * 判断字符类型是否是网址
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isUrl(String url) {
		if (isNotEmpty(url, true) == false) {
			return false;
		} else if (!url.startsWith(URL_PREFIX) && !url.startsWith(URL_PREFIXs)) {
			return false;
		}

		currentString = url;
		return true;
	}

	public static final String FILE_PATH_PREFIX = "file://";

	/**
	 * 判断文件路径是否存在
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isFilePathExist(String path) {
		return StringUtils.isFilePath(path) && new File(path).exists();
	}

	/**
	 * 判断字符类型是否是路径
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isFilePath(String path) {
		if (isNotEmpty(path, true) == false) {
			return false;
		}

		if (!path.contains(".") || path.endsWith(".")) {
			return false;
		}

		currentString = path;

		return true;
	}
	// 判断字符类型 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	// 提取特殊字符<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	/**
	 * 去掉string内所有非数字类型字符
	 * 
	 * @param tv
	 * @return
	 */
	public static String getNumber(TextView tv) {
		return getNumber(getString(tv));
	}

	/**
	 * 去掉string内所有非数字类型字符
	 * 
	 * @param object
	 * @return
	 */
	public static String getNumber(Object object) {
		return getNumber(getString(object));
	}

	/**
	 * 去掉string内所有非数字类型字符
	 * 
	 * @param cs
	 * @return
	 */
	public static String getNumber(CharSequence cs) {
		return getNumber(getString(cs));
	}

	/**
	 * 去掉string内所有非数字类型字符
	 * 
	 * @param s
	 * @return
	 */
	public static String getNumber(String s) {
		if (isNotEmpty(s, true) == false) {
			return "";
		}

		String numberString = "";
		String single;
		for (int i = 0; i < s.length(); i++) {
			single = s.substring(i, i + 1);
			if (isNumber(single)) {
				numberString += single;
			}
		}

		return numberString;
	}

	// 提取特殊字符>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	// 校正（自动补全等）字符串<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

	/**
	 * 获取网址，自动补全
	 * 
	 * @param tv
	 * @return
	 */
	public static String getCorrectUrl(TextView tv) {
		return getCorrectUrl(getString(tv));
	}

	/**
	 * 获取网址，自动补全
	 * 
	 * @param url
	 * @return
	 */
	public static String getCorrectUrl(String url) {
		Log.i(TAG, "getCorrectUrl : \n" + url);
		if (isNotEmpty(url, true) == false) {
			return "";
		}

		if (!url.endsWith("/") && !url.endsWith(".html")) {
			url = url + "/";
		}

		if (isUrl(url) == false) {
			return URL_PREFIX + url;
		}
		return url;
	}

	/**
	 * 获取去掉所有 空格 、"-" 、"+86" 后的phone
	 * 
	 * @param tv
	 * @return
	 */
	public static String getCorrectPhone(TextView tv) {
		return getCorrectPhone(getString(tv));
	}

	/**
	 * 获取去掉所有 空格 、"-" 、"+86" 后的phone
	 * 
	 * @param phone
	 * @return
	 */
	public static String getCorrectPhone(String phone) {
		if (isNotEmpty(phone, true) == false) {
			return "";
		}

		phone = getNoBlankString(phone);
		phone = phone.replaceAll("-", "");
		if (phone.startsWith("+86")) {
			phone = phone.substring(3);
		}else if(phone.startsWith("12953")){
			phone = phone.substring(5);
		}
		return phone;
	}

	/**
	 * 获取邮箱，自动补全
	 * 
	 * @param tv
	 * @return
	 */
	public static String getCorrectEmail(TextView tv) {
		return getCorrectEmail(getString(tv));
	}

	/**
	 * 获取邮箱，自动补全
	 * 
	 * @param email
	 * @return
	 */
	public static String getCorrectEmail(String email) {
		if (isNotEmpty(email, true) == false) {
			return "";
		}

		email = getNoBlankString(email);
		if (isEmail(email) == false && !email.endsWith(".com")) {
			email += ".com";
		}

		return email;
	}

	public static final int PRICE_FORMAT_DEFAULT = 0;
	public static final int PRICE_FORMAT_PREFIX = 1;
	public static final int PRICE_FORMAT_SUFFIX = 2;
	public static final int PRICE_FORMAT_PREFIX_WITH_BLANK = 3;
	public static final int PRICE_FORMAT_SUFFIX_WITH_BLANK = 4;
	public static final String[] PRICE_FORMATS = { "", "¥", "元", "¥ ", " 元" };

	/**
	 * 获取价格，保留两位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String getPrice(String price) {
		return getPrice(price, PRICE_FORMAT_DEFAULT);
	}

	/**
	 * 获取价格，保留两位小数
	 * 
	 * @param price
	 * @param formatType
	 *            添加单位（元）
	 * @return
	 */
	public static String getPrice(String price, int formatType) {
		if (isNotEmpty(price, true) == false) {
			return getPrice(0, formatType);
		}

		// 单独写到getCorrectPrice? <<<<<<<<<<<<<<<<<<<<<<
		String correctPrice = "";
		String s;
		for (int i = 0; i < price.length(); i++) {
			s = price.substring(i, i + 1);
			if (".".equals(s) || isNumber(s)) {
				correctPrice += s;
			}
		}
		// 单独写到getCorrectPrice? >>>>>>>>>>>>>>>>>>>>>>

		Log.i(TAG, "getPrice  <<<<<<<<<<<<<<<<<< correctPrice =  " + correctPrice);
		if (correctPrice.contains(".")) {
			// if (correctPrice.startsWith(".")) {
			// correctPrice = 0 + correctPrice;
			// }
			if (correctPrice.endsWith(".")) {
				correctPrice = correctPrice.replaceAll(".", "");
			}
		}

		Log.i(TAG, "getPrice correctPrice =  " + correctPrice + " >>>>>>>>>>>>>>>>");
		return isNotEmpty(correctPrice, true) ? getPrice(new BigDecimal(0 + correctPrice), formatType)
				: getPrice(0, formatType);
	}

	/**
	 * 获取价格，保留两位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String getPrice(BigDecimal price) {
		return getPrice(price, PRICE_FORMAT_DEFAULT);
	}

	/**
	 * 获取价格，保留两位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String getPrice(double price) {
		return getPrice(price, PRICE_FORMAT_DEFAULT);
	}

	/**
	 * 获取价格，保留两位小数
	 * 
	 * @param price
	 * @param formatType
	 *            添加单位（元）
	 * @return
	 */
	public static String getPrice(BigDecimal price, int formatType) {
		return getPrice(price == null ? 0 : price.doubleValue(), formatType);
	}

	/**
	 * 获取价格，保留两位小数
	 * 
	 * @param price
	 * @param formatType
	 *            添加单位（元）
	 * @return
	 */
	public static String getPrice(double price, int formatType) {
		String s = new DecimalFormat("#########0.00").format(price);
		switch (formatType) {
		case PRICE_FORMAT_PREFIX:
			return PRICE_FORMATS[PRICE_FORMAT_PREFIX] + s;
		case PRICE_FORMAT_SUFFIX:
			return s + PRICE_FORMATS[PRICE_FORMAT_SUFFIX];
		case PRICE_FORMAT_PREFIX_WITH_BLANK:
			return PRICE_FORMATS[PRICE_FORMAT_PREFIX_WITH_BLANK] + s;
		case PRICE_FORMAT_SUFFIX_WITH_BLANK:
			return s + PRICE_FORMATS[PRICE_FORMAT_SUFFIX_WITH_BLANK];
		default:
			return s;
		}
	}
	/**
	 * 切割字符串，存到数组之中
	 * @param value  切割对象
	 * @param array	存取切割对象
	 * @param flag	切割标志
	 * @return
	 */
	public static String[] doSplit(String value,String flag) {
		String[]array = null;
		if(isNotEmpty(value, true)){
			array = value.split(flag);
		}
		return array;
	}
	
	

	// 校正（自动补全等）字符串>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
	
	/**
	 * 判断字符串是否为null或全为空格
	 *
	 * @param s 待校验字符串
	 * @return {@code true}: null或全空格<br> {@code false}: 不为null且不全空格
	 */
	public static boolean isSpace(String s) {
		return (s == null || s.trim().length() == 0);
	}
}
