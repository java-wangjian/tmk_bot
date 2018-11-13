package com.zxxkj.util;



import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class HttpRequestUtil{
    
	/**
     * json 字符串
     * @param url
     * @param param
     * @return
     */
    public static String getSerchPersion(String url,String param){
        /* 1 生成 HttpClinet 对象并设置参数 */
        HttpClient httpClient = new HttpClient();
        // 设置 Http 连接超时为5秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        /* 2 生成 GetMethod 对象并设置参数 */
        GetMethod getMethod = new GetMethod(url);
        // 设置 get 请求超时为 5 秒
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
        // 设置请求重试处理，用的是默认的重试处理：请求三次
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        String response = "";
        /* 3 执行 HTTP GET 请求 */
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            /* 4 判断访问的状态码 */
            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("请求出错: "+ getMethod.getStatusLine());
            }
            /* 5 处理 HTTP 响应内容 */
            // HTTP响应头部信息，这里简单打印
            Header[] headers = getMethod.getResponseHeaders();
            for (Header h : headers)
                System.out.println(h.getName() + "------------ " + h.getValue());
            // 读取 HTTP 响应内容，这里简单打印网页内容
            byte[] responseBody = getMethod.getResponseBody();// 读取为字节数组
            response = new String(responseBody, param);
            System.out.println("----------response:" + response);
            // 读取为 InputStream，在网页内容数据量大时候推荐使用
            // InputStream response = getMethod.getResponseBodyAsStream();
        } catch (HttpException e) {
            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            System.out.println("请检查输入的URL!");
            e.printStackTrace();
        } catch (IOException e) {
            // 发生网络异常
            System.out.println("发生网络异常!");
            e.printStackTrace();
        } finally {
            /* 6 .释放连接 */
            getMethod.releaseConnection();
        }
        return response;
    }
   
    /**
     * post请求
     * @param url
     * @param json
     * @return
     */
    public static int doPost(String url, JSONObject json) {
    	int statusCode = 0;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        
        try {
            StringEntity s = new StringEntity(json.toString());
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");//发送json数据需要设置contentType
            post.setEntity(s);
            CloseableHttpResponse res = httpclient.execute(post);
            if (res != null) {
            	 statusCode = res.getStatusLine().getStatusCode();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return statusCode;
    }
}
