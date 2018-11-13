package com.zxxkj.util;

import java.text.SimpleDateFormat;
import java.util.Random;

public class ConstantUtil {
    public final static String classpath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
    public final static String SETTING_FILEPATH=classpath+"setting.properties";
	public final static SimpleDateFormat YYYY_MM_DD_SDF = new SimpleDateFormat("yyyy-MM-dd");
	public final static SimpleDateFormat YYYY_MM_DD_HH_MM_SS_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static SimpleDateFormat HH_MM_SS_SDF = new SimpleDateFormat("HH:mm:ss");
	// qiniuyun
	// 设置好账号的ACCESS_KEY和SECRET_KEY
	public final static String QINIUYUN_ACCESS_KEY = "fLtru-pRDaDQT-BSlzTBRlsWsaoZHCm561p3kDm-";
	public final static String QINIUYUN_SECRET_KEY = "UmaCMttbvQpV6UtSdFOyqNEBJYult-xHsvLJweYg";
	// 文件要上传的空间
	public final static String QINIUYUN_BUCKE_RECORDE = "zxxkj-tmk-bot-recorde";
	public final static String QINIUYUN_DOMAIN = "http://qiniu.91tmk.com/";

	// quartz
	// public final static String TIME = "";
	public static final String JOB_GROUP_NAME_BASE = "group_";
	public static final String TRIGGER_GROUP_NAME_BASE = "trigger_";
	public static final String JOBNAME_BASE_START = "callTask_";
	public static final String JOBNAME_BASE_END = "endTask_";

	// freeswitch
	public final static String FREESWITCH_JSONRPC = "2.0";
	public final static String FREESWITCH_METHOD = "ai.dial";
	public final static String FREESWITCH_APPLICATION = "httapi";
	public final static String FREESWITCH_DIAL_STRING = "sofia/gateway/dingxin/";
	public final static String FREESWITCH_DIAL_SIP = "sofia/gateway/";
	public final static String FREESWITCH_FROM = "from";

	public final static String FREESWITCH_POST = ParameterProperties.getValue(SETTING_FILEPATH, "FREESWITCH_POST");
	public final static String FREESWITCH_URL = ParameterProperties.getValue(SETTING_FILEPATH, "FREESWITCH_URL");
	public final static String FREESWITCH_ACTION = ParameterProperties.getValue(SETTING_FILEPATH, "FREESWITCH_ACTION");

	// dingxin test server
	public final static String API_PORT_INFO = ParameterProperties.getValue(SETTING_FILEPATH, "API_PORT_INFO");

	public final static Random random = new Random();
	
	public final static String REDIS_CALLRECORDELIST = "_callRecordeList";
}
