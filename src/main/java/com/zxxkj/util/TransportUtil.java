package com.zxxkj.util;

import java.util.ArrayList;
import java.util.List;

public class TransportUtil {

	/**
	 * 将时间转换为定时器需要的格式
	 * @param sendTime
	 * @return 定时器需要的格式的时间String
	 */
	public static String transportTimeFormat(String taskTime) {
		
			String taskStartTime = null;
		
			String[] taskTimeArr = taskTime.split(" ");
			String YYMMDD = taskTimeArr[0];
			String HHMMSS = taskTimeArr[1];
			String YY = YYMMDD.split("-")[0];
			String MM = YYMMDD.split("-")[1];
			String DD = YYMMDD.split("-")[2];
			String HH = HHMMSS.split(":")[0];
			String mm = HHMMSS.split(":")[1];
			String ss = HHMMSS.split(":")[2];
			
			if(MM.startsWith("0")) {
				MM = MM.substring(1, MM.length());
			}
			if(DD.startsWith("0")) {
				DD = DD.substring(1, DD.length());
			}
			if(HH.startsWith("0")) {
				HH = HH.substring(1, HH.length());
			}
			if(mm.startsWith("0")) {
				mm = mm.substring(1, mm.length());
			}
			if(ss.startsWith("0")) {
				ss = ss.substring(1, ss.length());
			}
			
			taskStartTime = ss + " " + mm + " " + HH + " " + DD + " " + MM + " " + "?" + " " + YY;
		
		return taskStartTime;
	}

	/**
	 * 将时间转换为定时器需要的格式
	 * @param sendTime
	 * @return 定时器需要的格式的时间String
	 */
	public static String transportTimeFormatToEveryDay(String taskTime) {
		//0 0 12 * * ?   每天12点触发 
			String taskStartTime = null;
		
			String[] taskTimeArr = taskTime.split(" ");
			String HHMMSS = taskTimeArr[1];
			String HH = HHMMSS.split(":")[0];
			
			if(HH.startsWith("0")) {
				HH = HH.substring(1, HH.length());
			}
			
			taskStartTime = "0 0 " + HH + " * * ?";
		
		return taskStartTime;
	}
	
	/**
	 * 将list的字符串形式转换成list
	 * @param groupStr
	 * @return List<String>
	 */
	public static List<String> stringTransportList(String str){
		
		List<String> groupList = new ArrayList<String>();
		
		if(str.contains("[]")) {
			
		}else {
			String groups =  str.replaceAll("\"", "");
			if(groups.contains("\"")) {
				groups.replaceAll("\"", "");
			}
			String[] groupArr = groups.substring(1, groups.length()-1).split(",");
			for (int i = 0; i < groupArr.length; i++) {
				groupList.add(groupArr[i]);
			}
		}
		
		return groupList;
	}
	
	/**
	 * 将idList的字符串形式转换成list
	 * @param groupStr
	 * @return List<String>
	 */
	public static List<Integer> stringTransportListForId(String str){
		
		List<Integer> groupList = new ArrayList<Integer>();
		
		if(str.contains("[]")) {
			
		}else {
			String[] groupArr = str.substring(1, str.length()-1).split(",");
			for (int i = 0; i < groupArr.length; i++) {
				groupList.add(Integer.valueOf(groupArr[i].trim()));
			}
		}
		
		return groupList;
	}
	
	public static String listStrTransportJsonStr(String listStr) {
		
		String[] strArr = listStr.substring(1, listStr.length()-1).split(",");
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < strArr.length; i++) {
			sb.append("\"" + strArr[i] + "\"");
			if(i < strArr.length-1) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
