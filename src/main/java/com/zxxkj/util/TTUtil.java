package com.zxxkj.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import sun.misc.BASE64Encoder;

public class TTUtil {

	@SuppressWarnings("unused")
	private static final Logger lg = Logger.getLogger(TTUtil.class);

	/**
	 * 获取一个用于保存参数的Map
	 */
	public static Map<String, Object> getParamMap() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		return paramMap;
	}
	
	public static String changeGrade(Integer grade) {
		String gradeStr = "";
		switch (grade) {
		case 6:
			gradeStr = "F";
			break;
		case 5:
			gradeStr = "E";
			break;
		case 4:
			gradeStr = "D";
			break;
		case 3:
			gradeStr = "C";
			break;
		case 2:
			gradeStr = "B";
			break;
		case 1:
			gradeStr = "A";
			break;
		default:
			gradeStr = "";
		}
		return gradeStr;
	}

	public static List<String> calendarRoll(Integer num) {
		Date d = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String endDate = format.format(d);
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.DATE, -1);
		d = ca.getTime();
		endDate = format.format(d);
		ca.add(Calendar.DATE, -num + 1);
		d = ca.getTime();
		String startDate = format.format(d);
		List<String> list = calendarFormat(startDate, endDate);
		return list;
	}

	public static Boolean isAnyNull(Object... obj) {
		for (Object object : obj) {
			if (object instanceof String) {
				if (StringUtils.isBlank((String) object)) {
					return true;
				}
			}
			if (null == object) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 拼接字符串使用
	 */
	public static String appendString(Object... objects) {
		StringBuilder sb = new StringBuilder();
		for (Object object : objects) {
			sb.append(String.valueOf(object));
		}
		return sb.toString();
	}

	/**
	 * 计算日期区间
	 */
	public static List<String> calendarFormat(String startDate, String endDate) {
		List<String> cals = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date begin = null;
		Date end = null;
		try {
			begin = sdf.parse(startDate);
			end = sdf.parse(endDate);
		} catch (ParseException e) {
		}
		double between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒
		double day = between / (24 * 3600);
		for (int i = 0; i <= day; i++) {
			Calendar cd = Calendar.getInstance();
			try {
				cd.setTime(sdf.parse(startDate));
			} catch (ParseException e) {
			}
			cd.add(Calendar.DATE, i);// 增加一天
			String tempDate = sdf.format(cd.getTime());
			cals.add(tempDate);
		}
		return cals;
	}

	/**
	 * 防止Response中有乱码
	 */
	public static void sendDataByIOStream(HttpServletResponse response, JSONObject resultJSON) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(resultJSON.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

    public static void sendMail(String host, String toMail, String fromMail, String title, String content,
                                String username, String password) {
        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
        senderImpl.setHost(host);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toMail);
        mailMessage.setFrom(fromMail);
        mailMessage.setSubject(title);
        mailMessage.setText(content);
        senderImpl.setUsername(username);
        senderImpl.setPassword(password);
        Properties prop = new Properties();
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        sf.setTrustAllHosts(true);
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.socketFactory", sf);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.timeout", "25000");
        senderImpl.setJavaMailProperties(prop);
        senderImpl.send(mailMessage);
    }

	/**
	 * 返回JSONArray 数据
	 * 
	 * @param response
	 * @param JSONarr
	 */
	public static void sendJSONArrDataByIOStream(HttpServletResponse response, JSONArray JSONarr) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(JSONarr.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static List<String> string2list(String listStr) {
		if (StringUtils.isAnyBlank(listStr)) {
			return null;
		}
		listStr = listStr.replace(" ", "");
		listStr = listStr.replace("[", "");
		listStr = listStr.replace("]", "");
		if (listStr.length() == 0) {
			return null;
		}
		List<String> list = Arrays.asList(listStr.split(","));
		return list;
	}

	/**
	 * 正则校验手机号
	 */
	public static Boolean checkPhoneNum(String phone) {
		String str = "^((13[0-9])|(15[^4,\\D])|(14[57])|(17[0-9])|(18[0,0-9]))\\d{8}$";
		boolean falg = false;
		Pattern pattern = Pattern.compile(str);
		falg = pattern.matcher(phone).matches();
		return falg;
	}

	/**
	 * 发送短信息
	 */
	public static Integer sendSms(String url,String auth,String pwd,String phone,String content) {
		auth = auth +":"+ pwd;
		if (url.contains("api")) {
			int index = url.indexOf("/api");
			url = url.substring(0, index);
		}
		url = url + "/api/send_sms";
		JSONObject tempObj = new JSONObject();
		JSONObject phoneJSON = new JSONObject();
		JSONArray array = new JSONArray();
		phoneJSON.put("number", phone);
		array.add(phoneJSON);
		JSONObject json = new JSONObject();
		json.put("text", content);
		json.put("param", array);
		json.put("url", url);
		json.put("auth", auth);
		json.put("request_status_report", true);
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		// 添加http头信息
		byte[] b = null;
		try {
			b = auth.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		auth = new BASE64Encoder().encode(b);
		httppost.addHeader("Authorization", "Basic " + auth);
		httppost.addHeader("Content-Type", "application/json");
		httppost.addHeader("User-Agent", "imgfornote");
		String rev;
		try {
			StringEntity stringEntity = new StringEntity(json.toString(), "utf-8");
			httppost.setEntity(stringEntity);
			HttpResponse response;
			response = httpclient.execute(httppost);
			// 检验状态码，如果成功接收数据
			int code = response.getStatusLine().getStatusCode();
			rev = EntityUtils.toString(response.getEntity());
			lg.info(rev);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		try {
			tempObj = JSON.parseObject(rev);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		if (null != tempObj && 202 == tempObj.getInteger("error_code")) {
			return 0;
		} 
		return 1;
	}

	/**
	 * 获取网关中端口的信息
	 */
	public static JSONObject getPortInfo(String url, String param,String auth) {
		Map<String, String> resultMap = new HashMap<>();
		List<Map<String, String>> mapList = new ArrayList<>();
		// 创建默认的httpClient实例
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String result = null;
		HttpEntity entity = null;
		try {
			// 用get方法发送http请求
			HttpGet get = new HttpGet(url + "?" + param);
			CloseableHttpResponse httpResponse = null;
			byte[] b = auth.getBytes("utf-8");
			auth = new BASE64Encoder().encode(b); // 使用base64对用户名:密码进行加密
			get.addHeader("Authorization", "Basic " + auth);
			httpResponse = httpClient.execute(get);
			try {
				// response实体
				entity = httpResponse.getEntity();
				if (null != entity) {
					result = EntityUtils.toString(entity);
				}
			} finally {
				httpResponse.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = JSON.parseObject(result);
		} catch (Exception e) {
			return jsonObject;
		}
		return jsonObject;
	}

	/**
	 * 设置跨域,声明返回值
	 */
	public static JSONObject setDomainAndCreateReturn(HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		JSONObject resultJSON = new JSONObject();
		return resultJSON;
	}

	/**
	 * 格式化返回值
	 */
	public static JSONObject formatReturn(JSONObject resultJSON, Integer code, String result) {
		resultJSON.put("code", code);
		resultJSON.put("result", result);
		TTUtil.formatData(0);
		return resultJSON;
	}

	/**
	 * 格式化返回结果
	 */
	public static void formatData(Integer second) {
		try {
			Thread.sleep(second * 1000);
		} catch (InterruptedException e) {
		}
	}
	/**
	 * 接口返回状态码和说明
	 * @author jiedongjun
	 * @param response
	 * @param code
	 * @param explain
	 * @param result
	 */
	public static void sendJSONDataByIOStream(HttpServletResponse response, int code, String explain, JSONObject result) {
		PrintWriter out = null;

		result.put("code", code);
		result.put("explain", explain);
		try {
			out = response.getWriter();
			out.write(result.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
}
