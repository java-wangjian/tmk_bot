package com.zxxkj.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ParameterProperties {
	private static Properties properties = new Properties(); 
	

	public static String getValue(String filePath, String key){
		InputStream bis = null;
		String value = null;
		try {
			// 读入指定文件路径的文件
			bis = new BufferedInputStream(new FileInputStream(filePath));
			properties.load(bis); // 从输入流中读取属性列表（键和元素对）
			value = properties.getProperty(key); // 获取指定键对应的值
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}
	
	public static Integer getIntegerValue(String filePath, String key){
		InputStream bis = null;
		Integer value = null;
		try {
			// 读入指定文件路径的文件
			bis = new BufferedInputStream(new FileInputStream(filePath));
			properties.load(bis); // 从输入流中读取属性列表（键和元素对）
			value = Integer.valueOf(properties.getProperty(key)); // 获取指定键对应的值
		} catch (Exception e) {
			if(("callCount").equals(key)) {
				
				return 50;
			}
		} finally {
			if(bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}
}
