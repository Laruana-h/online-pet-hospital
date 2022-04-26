package com.phms.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Created with IDEA
 * @author: Super Zheng
 * @Description: java类作用描述
 * @Date:2018/12/26
 * @Time:12:58
 */
public class HttpClientUtils {

    /**
     * 发送get请求，利用java代码发送请求
     * @param url
     * @return
     * @throws Exception
     */
    public static String doGet(String url) throws Exception{

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig =RequestConfig.custom().setConnectionRequestTimeout(100000)
                .setSocketTimeout(100000)
                .setConnectTimeout(100000).build();
        httpGet.setConfig(requestConfig);
        // 发送了一个http请求
        CloseableHttpResponse response = httpclient.execute(httpGet);
        // 如果响应200成功,解析响应结果
        if(response.getStatusLine().getStatusCode()==200){
            // 获取响应的内容
            HttpEntity responseEntity = response.getEntity();

            return EntityUtils.toString(responseEntity);
        }
        return null;
    }

    /**
     * 发送get请求，利用java代码发送请求
     * @param url
     * @return
     * @throws Exception
     */
    public static String doGetWithHeader(String url,String token) throws Exception{

        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        RequestConfig requestConfig =RequestConfig.custom().setConnectionRequestTimeout(100000)
                .setSocketTimeout(100000)
                .setConnectTimeout(100000).build();
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("Authorization","token "+token);
        // 发送了一个http请求
        CloseableHttpResponse response = httpclient.execute(httpGet);
        // 如果响应200成功,解析响应结果
        if(response.getStatusLine().getStatusCode()==200){
            // 获取响应的内容
            HttpEntity responseEntity = response.getEntity();

            return EntityUtils.toString(responseEntity);
        }
        return null;
    }

    /**
     * 将字符串转换成map
     * @param responseEntity
     * @return
     */
    public static Map<String,String> getMap(String responseEntity) {

        Map<String, String> map = new HashMap<>();
        // 以&来解析字符串
        String[] result = responseEntity.split("\\&");

        for (String str : result) {
            // 以=来解析字符串
            String[] split = str.split("=");
            // 将字符串存入map中
            if (split.length == 1) {
                map.put(split[0], null);
            } else {
                map.put(split[0], split[1]);
            }

        }
        return map;
    }

    /**
     * 通过json获得map
     * @param responseEntity
     * @return
     */
    public static Map<String,String> getMapByJson(String responseEntity) {
        Map<String, String> map = new HashMap<>();
        // 阿里巴巴fastjson  将json转换成map
        JSONObject jsonObject = JSONObject.parseObject(responseEntity);
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            // 将obj转换成string
            String value = String.valueOf(entry.getValue()) ;
            map.put(key, value);
        }
        return map;
    }
}

