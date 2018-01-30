package cn.hejinyo.jelly.common.utils;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/19 22:55
 */
public class HttpClientUtil {
    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(15000)
            .setConnectTimeout(15000)
            .setConnectionRequestTimeout(15000)
            .build();

    protected Logger logger = LoggerFactory.getLogger(getClass());
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
     * 发送 post请求
     */
    public String sendHttpPost(String httpUrl, String params) {
        logger.info("sendHttpPost:" + httpUrl + "," + params);

        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost

        this.setParam(httpPost, params);
        return sendHttpPost(httpPost);
    }

    /**
     * 发送 post请求
     */
    public String sendHttpPost(String httpUrl, Map<String, String> maps) {
        logger.info("sendHttpPost:" + httpUrl + ", " + JSON.toJSONString(maps));
        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
        // 创建参数队列
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for (String key : maps.keySet()) {
            nameValuePairs.add(new BasicNameValuePair(key, maps.get(key)));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendHttpPost(httpPost);
    }

    /**
     * 发送Post请求
     */
    private String sendHttpPost(HttpPost httpPost) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;
        try {
            // 创建默认的httpClient实例.
            httpClient = HttpClients.createDefault();
            //httpPost.setConfig(requestConfig);
            // 执行请求
            response = httpClient.execute(httpPost);
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
     * 发送 get请求
     */
    public String sendHttpGet(String httpUrl) {
        logger.info("sendHttpGet:" + httpUrl);
        HttpGet httpGet = new HttpGet(httpUrl);// 创建get请求
        return sendHttpGet(httpGet);
    }

    /**
     * 发送Get请求
     */
    private String sendHttpGet(HttpGet httpGet) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;
        try {
            // 创建默认的httpClient实例.
            httpClient = HttpClients.createDefault();
            httpGet.setConfig(requestConfig);
            // 执行请求
            response = httpClient.execute(httpGet);
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
     * httpPost 带head
     */
    public String sendHttpPost2(String httpUrl, String parmas, Map<String, String> head) {

        HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
        if (head != null) {
            httpPost.setHeader(head.get("key"), head.get("value"));
        }
        this.setParam(httpPost, parmas);
        return sendHttpPost(httpPost);
    }

    /**
     * 设置参数带提取出来
     */
    private HttpPost setParam(HttpPost httpPost, String param) {
        try {
            //设置参数
            StringEntity stringEntity = new StringEntity(param, "UTF-8");
            stringEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(stringEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpPost;
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
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * get 请求直接返回InputStream
     */
    public InputStream sendGet(String url) {
        HttpGet httpGet = new HttpGet(url);

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
            // this.close(response,httpClient);
        }
        return inputStream;
    }

    /**
     * get CloseableHttpResponse
     */
    public CloseableHttpResponse sendPost(String url) {

        HttpPost httpPost = new HttpPost(url);// 创建httpPost
        //this.setParam(httpPost,params);

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
