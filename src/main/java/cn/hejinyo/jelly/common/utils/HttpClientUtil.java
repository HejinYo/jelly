package cn.hejinyo.jelly.common.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/19 22:55
 */
@Slf4j
public class HttpClientUtil {
    private RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(15000).setConnectTimeout(15000).setConnectionRequestTimeout(15000).build();
    private static HttpClientUtil instance = null;

    private HttpClientUtil() {
    }

    public static HttpClientUtil getInstance() {
        if (instance == null) {
            instance = new HttpClientUtil();
        }
        return instance;
    }

    /**
     * 发送请求
     */
    private String sendHttp(HttpRequestBase http) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity;
        String responseContent = null;
        try {
            // 创建默认的httpClient实例.
            httpClient = HttpClients.createDefault();
            http.setConfig(requestConfig);
            // 执行请求
            response = httpClient.execute(http);
            entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.close(response, httpClient);
        }
        return responseContent;
    }

    /**
     * 设置参数
     */
    private void setParam(HttpPost httpPost, String param) {
        try {
            //设置参数
            StringEntity stringEntity = new StringEntity(param, "UTF-8");
            stringEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(stringEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 关闭连接
     */
    private void close(CloseableHttpResponse response, CloseableHttpClient httpClient) {
        try {
            // 关闭连接,释放资源
            if (response != null) {
                response.close();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 发送String参数 post请求
     *
     * @param httpUrl url
     * @param params  String参数
     */
    public String sendHttpPost(String httpUrl, String params) {
        log.info("sendHttpPost:" + httpUrl + "," + params);
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        this.setParam(httpPost, params);
        return sendHttp(httpPost);
    }

    /**
     * 发送Map参数 post请求
     */
    public String sendHttpPost(String httpUrl, Map<String, String> params) {
        log.info("sendHttpPost:" + httpUrl + ", " + JSON.toJSONString(params));
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        // 创建参数队列
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (String key : params.keySet()) {
            nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendHttp(httpPost);
    }


    /**
     * httpPost 带head
     */
    public String sendHttpPost(String httpUrl, Map<String, String> params, Map<String, String> heads) {
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        //设置head
        for (String key : heads.keySet()) {
            httpPost.setHeader(key, heads.get(key));
        }
        // 创建参数队列
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (String key : params.keySet()) {
            nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendHttp(httpPost);
    }

    /**
     * 方法名称: sendPost
     */
    public String sendPost(String url, Map<String, String> params, String tokenType, String token) {
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        //设置参数到请求对象中// 创建参数队列
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (String key : params.keySet()) {
            nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //指定报文头【Content-type】、【User-Agent】
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //添加http请求证书
        if (tokenType != null && !"".equals(tokenType)) {
            httpPost.setHeader("Authorization", tokenType + " " + token);
        }
        return sendHttp(httpPost);
    }


    /**
     * 发送 get请求
     */
    public String sendHttpGet(String httpUrl) {
        log.info("sendHttpGet:" + httpUrl);
        // 创建get请求
        HttpGet httpGet = new HttpGet(httpUrl);
        return sendHttp(httpGet);
    }

    /**
     * get 请求直接返回InputStream
     */
    public InputStream sendGet(String httpUrl) {
        log.info("sendHttpGet:" + httpUrl);
        HttpGet httpGet = new HttpGet(httpUrl);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity httpEntity;
        InputStream inputStream = null;
        try {
            // 创建默认的httpClient实例.
            httpClient = HttpClients.createDefault();
            httpGet.setConfig(requestConfig);
            // 执行请求
            response = httpClient.execute(httpGet);
            httpEntity = response.getEntity();
            inputStream = httpEntity.getContent();
            System.out.println("--------" + response.getStatusLine());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.close(response, httpClient);
        }
        return inputStream;
    }

    /**
     * get CloseableHttpResponse
     */
    public CloseableHttpResponse sendPost(String httpUrl) {
        log.info("sendPost:" + httpUrl);
        // 创建httpPost
        HttpPost httpPost = new HttpPost(httpUrl);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();
            response = httpClient.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.close(response, httpClient);
        }
        return response;
    }

}
