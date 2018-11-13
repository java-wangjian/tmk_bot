package com.zxxkj.util;

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2017/1/21.
 */
public class QiniuUtil {

	// 密钥配置
	private static Auth auth = Auth.create(ConstantUtil.QINIUYUN_ACCESS_KEY, ConstantUtil.QINIUYUN_SECRET_KEY);
	// 第二种方式: 自动识别要上传的空间(bucket)的存储区域是华东、华北、华南。
	private static Zone z = Zone.autoZone();
	private static Configuration c = new Configuration(z);
	// 创建上传对象
	private static UploadManager uploadManager = new UploadManager(c);
	//private static String BUCKET_DOMAIN = "http://p8k2ova1x.bkt.clouddn.com/";
	// 简单上传，使用默认策略，只需要设置上传的空间名就可以了
	private static String getUpToken(String bucketname) {
		return auth.uploadToken(bucketname);
	}

	public static String upload(String FilePath, String key, String bucketname){
		try {
			if (StringUtils.isBlank(bucketname)) {
				bucketname = getUpToken(ConstantUtil.QINIUYUN_BUCKE_RECORDE);
			} else {
				bucketname = getUpToken(bucketname);
			}
			// 调用put方法上传
			uploadManager.put(FilePath, key, bucketname);
		} catch (QiniuException e) {
			// 请求失败时打印的异常的信息
			e.printStackTrace();
		}
		return ConstantUtil.QINIUYUN_DOMAIN + key;
	}
	
	/**
	 * 上传文件字节流
	 * @param FilePath
	 * @param key
	 * @param bucketname
	 * @return
	 */
	public static String uploadByte(byte[] fileByte, String key, String bucketname){
		try {
			if (StringUtils.isBlank(bucketname)) {
				bucketname = getUpToken(ConstantUtil.QINIUYUN_BUCKE_RECORDE);
			} else {
				bucketname = getUpToken(bucketname);
			}
			// 调用put方法上传
			uploadManager.put(fileByte, key, bucketname);
		} catch (QiniuException e) {
			Response r = e.response;
			// 请求失败时打印的异常的信息
			System.out.println(r.toString());
			try {
				// 响应的文本信息
				System.out.println(r.bodyString());
			} catch (QiniuException e1) {
				// ignore
			}
		}
		return ConstantUtil.QINIUYUN_DOMAIN + key;
	}
}
