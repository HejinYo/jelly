package cn.hejinyo.jelly.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/19 22:55
 */
@Slf4j
public class HttpClientUtil implements AutoCloseable {
    private RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(15000).setConnectTimeout(15000).setConnectionRequestTimeout(15000).build();
    private static HttpClientUtil instance = null;

    private HttpClientUtil() {
    }

    /**
     * 获取实例
     */
    public static HttpClientUtil getInstance() {
        if (instance == null) {
            instance = new HttpClientUtil();
        }
        return instance;
    }

    /**
     * 执行请求，返回HttpEntity
     */
    private HttpEntity getEntity(HttpRequestBase request) {
        try {
            request.setConfig(requestConfig);
            // 执行请求
            Optional<CloseableHttpResponse> rep = Optional.ofNullable(HttpClients.createDefault().execute(request));
            return rep.map(HttpResponse::getEntity).orElse(null);
        } catch (IOException e) {
            log.error("请求网络资源发生异常：{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * HttpEntity 转 指定类型
     */
    @SuppressWarnings("unchecked")
    private static <T> T entityToClass(HttpEntity httpEntity, Class<T> clazz) {
        try {
            switch (clazz.getSimpleName()) {
                case "String":
                    return (T) EntityUtils.toString(httpEntity, "UTF-8");
                case "byte[]":
                    return (T) EntityUtils.toByteArray(httpEntity);
                default:
                    String result = EntityUtils.toString(httpEntity, "UTF-8");
                    if (StringUtils.isNotEmpty(result)) {
                        return JsonUtil.fromJson(result, clazz);
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 拼接url参数，返回带参数的URL
     */
    public static String buildUrl(String url, Map<String, Object> params) {
        if (params != null && params.size() > 0) {
            StringBuilder arg = new StringBuilder(url + (url.indexOf("?") > 0 ? "&" : "?"));
            params.forEach((key, value) -> {
                //key去空额
                arg.append(key.trim()).append("=").append(value).append("&");
            });
            //此处为了兼容case内容为空
            return arg.deleteCharAt(arg.length() - 1).toString();
        }
        return url;
    }

    /**
     * 构建cookie字符串
     */
    public static String buildCookie(Map<String, Object> cookies) {
        if (cookies != null && cookies.size() > 0) {
            StringBuilder sb = new StringBuilder();
            cookies.forEach((key, value) -> {
                sb.append(key.trim()).append("=").append(value).append("; ");
            });
            return sb.deleteCharAt(sb.length() - 1).toString();
        }
        return "";
    }

    /**
     * get请求，返回结果InputStream
     */
    private InputStream sendGet(String url) {
        // 创建get请求
        log.info("sendHttpGet  :" + url);
        HttpGet httpGet = new HttpGet(url);
        Optional<HttpEntity> httpEntity = Optional.ofNullable(getEntity(httpGet));
        try {
            if (httpEntity.isPresent()) {
                return httpEntity.get().getContent();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get请求，返回结果为指定类型
     */
    public <T> T sendGet(String url, Class<T> clazz) {
        // 创建get请求
        log.info("sendHttpGet  :" + url);
        HttpGet httpGet = new HttpGet(url);
        Optional<HttpEntity> httpEntity = Optional.ofNullable(getEntity(httpGet));
        return httpEntity.map(httpEntity1 -> entityToClass(httpEntity1, clazz)).orElse(null);
    }

    /**
     * get带参数请求，返回结果为指定类型
     */
    public <T> T sendGet(String url, Map<String, Object> params, Class<T> clazz) {
        // 创建get请求
        String paramsUrl = HttpClientUtil.buildUrl(url, params);
        return sendGet(paramsUrl, clazz);
    }


    /**
     * 创建请求体
     */
    private UrlEncodedFormEntity buildEntity(List<NameValuePair> list) {
        try {
            return new UrlEncodedFormEntity(list, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get请求，返回结果为指定类型
     */
    private <T> T sendPost(String url, Class<T> clazz) {
        log.info("sendHttpPost  url:{}", url);
        HttpPost httpPost = new HttpPost(url);
        Optional<HttpEntity> httpEntity = Optional.ofNullable(getEntity(httpPost));
        return httpEntity.map(httpEntity1 -> entityToClass(httpEntity1, clazz)).orElse(null);
    }


    /**
     * 发送 post请求,带参数
     */
    public <T> T sendHttpPost(String url, Map<String, ? extends Serializable> params, Class<T> clazz) {
        log.info("sendHttpPost  url:{},param:{}", url, JsonUtil.toJson(params));
        HttpPost httpPost = new HttpPost(url);
        // 创建参数队列
        List<NameValuePair> list = new ArrayList<>();
        params.forEach((key, value) -> {
            list.add(new BasicNameValuePair(key, String.valueOf(value)));
        });
        httpPost.setEntity(buildEntity(list));
        Optional<HttpEntity> httpEntity = Optional.ofNullable(getEntity(httpPost));
        return httpEntity.map(httpEntity1 -> entityToClass(httpEntity1, clazz)).orElse(null);
    }


    /**
     * 自动关闭资源
     */
    @Override
    public void close() throws Exception {

    }

    public static void main(String[] args) {
        String result = HttpClientUtil.getInstance().sendGet("http://www.hejinyo.cn", String.class);
        System.out.println(result);
    }

}
