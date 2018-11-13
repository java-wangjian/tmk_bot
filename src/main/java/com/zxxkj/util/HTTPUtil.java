package com.zxxkj.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import sun.misc.BASE64Encoder;

import static com.zxxkj.util.ConstantUtil.API_PORT_INFO;
import static com.zxxkj.util.Utils.isEmpty;

public class HTTPUtil {

    /**
     * 原生的HTTP请求
     *
     * @param uri
     * @param param   请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @param charset
     * @return
     */
    private static String sendPost(String uri, String param, String charset) {
        String result = null;
        PrintWriter out = null;
        InputStream in = null;
        try {
            URL url = new URL(uri);
            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            urlcon.setDoInput(true);
            urlcon.setDoOutput(true);
            urlcon.setUseCaches(false);
            urlcon.setRequestMethod("POST");
            urlcon.connect();// 获取连接
            out = new PrintWriter(urlcon.getOutputStream());
            out.print(param);
            out.flush();
            in = urlcon.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(in, charset));
            StringBuffer bs = new StringBuffer();
            String line = null;
            while ((line = buffer.readLine()) != null) {
                bs.append(line);
            }
            result = bs.toString();
        } catch (Exception e) {
            System.out.println("[请求异常][地址：" + uri + "][参数：" + param + "][错误信息：" + e.getMessage() + "]");
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if (null != out) {
                    out.close();
                }
            } catch (Exception e2) {
                System.out.println("[关闭流异常][错误信息：" + e2.getMessage() + "]");
            }
        }
        return result;
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static List<Map<String, String>> sendGet(String url, String param, String gatewayAccount, String password) {
        Map<String, String> resultMap = new HashMap<String, String>();
        List<Map<String, String>> mapList = new ArrayList<>();
        //创建默认的httpClient实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = null;
        HttpEntity entity = null;
        try {
            //用get方法发送http请求
            HttpGet get = new HttpGet(url + "?" + param);
            System.out.println("执行get请求:...." + get.getURI());
            CloseableHttpResponse httpResponse = null;
            String str = gatewayAccount + ":" + password;   //这是能通过认证的用户名和密码
            byte[] b = str.getBytes("utf-8");
            str = new BASE64Encoder().encode(b);  //使用base64对用户名:密码进行加密
            get.addHeader("Authorization", "Basic " + str);
            //发送get请求
            httpResponse = httpClient.execute(get);
            try {
                //response实体
                entity = httpResponse.getEntity();
                if (null != entity) {
                    result = EntityUtils.toString(entity);
                }
            } finally {
                httpResponse.close();
            }
        } catch (Exception e) {
            
        	return mapList;
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JSONObject jsonObject = JSON.parseObject(result);
        Integer error_code = jsonObject.getInteger("error_code");
        if (error_code.equals(200)) {
            JSONArray jsonArray = jsonObject.getJSONArray("info");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                //System.out.println("呼叫状态"+jsonObject1.getString("callstate"));
                if (!isEmpty(jsonObject1.getString("callstate")) && jsonObject1.getString("callstate").equals("Idle") && jsonObject1.getString("reg").equals("REGISTER_OK")) {
                    resultMap.put("port", jsonObject1.getString("port"));
                    resultMap.put("type", jsonObject1.getString("LTE"));
                    resultMap.put("imei", jsonObject1.getString("imei"));
                    resultMap.put("imsi", jsonObject1.getString("imsi"));
                    resultMap.put("iccid", jsonObject1.getString("iccid"));
                    resultMap.put("callstate", jsonObject1.getString("callstate"));
                    resultMap.put("signal", jsonObject1.getString("signal"));
                    mapList.add(resultMap);
                }
            }
        }
        return mapList;
    }
    
    public static String honglianSMS(String url) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		BufferedReader in = null;
		try {
			HttpGet get = new HttpGet(url);
			CloseableHttpResponse httpResponse = null;
			httpResponse = httpClient.execute(get);
			try {
				HttpEntity entity = httpResponse.getEntity();
				if (null != entity) {
					String tempStr = "";
					in = new BufferedReader(new InputStreamReader(entity.getContent()));
					StringBuffer content = new StringBuffer();
					while ((tempStr = in.readLine()) != null) {
						content.append(tempStr);
					}
					return content.toString();
				}
			} finally {
				httpResponse.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

    public static String doLogin() {
        String cookie=null;
        // 登陆 Url
        String loginUrl = "https://server02.dmcld.com:3000/doLogin?username=diting&password=diting";
        // 需登陆后访问的 Url
        String dataUrl = "http://hi.mop.com/?";
        HttpClient httpClient = new HttpClient();

        // 模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式
        GetMethod postMethod = new GetMethod(loginUrl);

        // 设置登陆时要求的信息，用户名和密码
        try {
            // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
            httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            int statusCode=httpClient.executeMethod(postMethod);

            // 获得登陆后的 Cookie
            Cookie[] cookies = httpClient.getState().getCookies();
            StringBuffer tmpcookies = new StringBuffer();
            for (Cookie c : cookies) {
                tmpcookies.append(c.toString() + ";");
                cookie=tmpcookies.toString()+"devckie=db38-0118-0406-0188";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return cookie;
    }

    public static boolean updateCallCount(long phone) {

        String result = sendPost("http://192.168.1.116/tmk-bot/customer/updateCallCountByPhone", "phone=" + phone, "UTF-8");
        int flag = JSON.parseObject(result).getIntValue("result");
        if (flag == 0) {
            return true;
        }
        return false;
    }

    private CloseableHttpClient getHttpClient() {
        return HttpClients.createDefault();
    }

    private void closeHttpClient(CloseableHttpClient client) throws IOException {
        if (client != null) {
            client.close();
        }
    }

}
