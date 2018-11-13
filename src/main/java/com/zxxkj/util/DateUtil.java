package com.zxxkj.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	/**
	 * 根据某一日期获取时分秒Date格式
	 * @param date
	 * @return
	 */
	public static Date getHHMMSSByDate(Date date) {
		Date HHMMSSDate = null;
		try {
			HHMMSSDate = ConstantUtil.HH_MM_SS_SDF.parse(getHHMMSSStrByDate(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return HHMMSSDate;
	}
	
	/**
	 * 根据时分秒的日期格式转换成字符串的时分秒格式
	 * @param date
	 * @return
	 */
	public static String getHHMMSSStrByDate(Date date) {
		String HHMMSS = null;
		
		HHMMSS = ConstantUtil.HH_MM_SS_SDF.format(date);
		return HHMMSS;
	}
	
	public static Date getNextDate(String dateStr) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(ConstantUtil.YYYY_MM_DD_SDF.parse(dateStr));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date nextDay = new Date(calendar.getTimeInMillis());
		return nextDay;
	}
	
	//日期的字符串转换成日期格式
	public static Date stringFormatToDate_YYYY_MM_DD(String dateStr) {
		Date date = null;
		try {
			date = ConstantUtil.YYYY_MM_DD_SDF.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 时分秒转换成日期格式
	 */
	public static Date stringFormatToDate_HH_MM_SS(String dateStr) {
		Date date = null;
		
		try {
			date = ConstantUtil.HH_MM_SS_SDF.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
}
