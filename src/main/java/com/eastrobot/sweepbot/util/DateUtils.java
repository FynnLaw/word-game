package com.eastrobot.sweepbot.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class DateUtils {
	public static Date stringToDate(String date, String formatString) {
		try {
			DateFormat df = new SimpleDateFormat(formatString);
			return df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String dateToString(Date date, String fmt) {
		DateFormat df = new SimpleDateFormat(fmt);
		return df.format(date);
	}

	public static Date parseDate(String date, String fmt) {
		Date ret = null;
		if (StringUtils.isNotBlank(date) && StringUtils.isNotBlank(fmt)) {
			DateFormat df = new SimpleDateFormat(fmt);
			try {
				ret = df.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public static String parseDate(String pdate) {
		int n = 12;
		if(n == pdate.length())
			return pdate;
		return pdate + String.format("%1$0" + (n - pdate.length()) + "d", 0);
	}

	public static void main(String[] args) {
		String s = "201511171431";
		s = parseDate(s);
		System.out.println(s);
	}
	
}
