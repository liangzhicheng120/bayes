package com.xinrui.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class StringUtil {
	public static String getEncoding(String str) {
		String encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s = encode;
				return s;
			}
		} catch (Exception exception) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s1 = encode;
				return s1;
			}
		} catch (Exception exception1) {
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s2 = encode;
				return s2;
			}
		} catch (Exception exception2) {
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s3 = encode;
				return s3;
			}
		} catch (Exception exception3) {
		}
		return "";
	}

	public static List<Integer> fromStringToInteger(String[] ids) {
		List<Integer> result = new ArrayList<Integer>();
		for (String id : ids) {
			result.add(Integer.valueOf(id));
		}
		return result;
	}

	public static String argsToString(String... args) {
		String result = "";
		for (String s : args) {
			result += " " + s;
		}
		return result;
	}

	public static boolean isNumeric(String str) {
		return (!StringUtils.isNumeric(str) || "".equals(str)) ? false : true;
	}
}
