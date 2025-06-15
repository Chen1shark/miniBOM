package com.idme.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 增强版HTTP工具类
 * 特性：
 * 1. 统一返回响应头+响应体
 * 2. 支持GET/POST(表单+JSON)
 * 3. 完善的异常处理
 * 4. 自动资源清理
 */
public class HttpClientUtil {

    private static final int TIMEOUT_MSEC = 5000;

    /**
     * HTTP响应封装类
     */
    public static class HttpResponse {
        private final int statusCode;
        private final String body;
        private final Map<String, String> headers;

        public HttpResponse(int statusCode, String body, Map<String, String> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = Collections.unmodifiableMap(headers);
        }

        // Getter方法
        public int getStatusCode() { return statusCode; }
        public String getBody() { return body; }
        public Map<String, String> getHeaders() { return headers; }

        public String getHeader(String name) {
            return headers.get(name);
        }
    }

    /**
     * 执行GET请求
     * @param url 请求地址
     * @param params 查询参数
     * @return 封装后的响应对象
     */
    public static HttpResponse doGet(String url, Map<String, String> params) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URI uri = buildUriWithParams(url, params);
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setConfig(buildRequestConfig());

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                return parseResponse(response);
            }
        } catch (Exception e) {
            throw new HttpClientException("GET请求失败: " + url, e);
        }
    }

    /**
     * 执行POST请求（表单格式）
     * @param url 请求地址
     * @param formParams 表单参数
     * @return 封装后的响应对象
     */
    public static HttpResponse doPost(String url, Map<String, String> formParams) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(buildRequestConfig());

            if (formParams != null && !formParams.isEmpty()) {
                List<NameValuePair> formData = formParams.entrySet().stream()
                        .map(e -> new BasicNameValuePair(e.getKey(), e.getValue()))
                        .collect(Collectors.toList());
                httpPost.setEntity(new UrlEncodedFormEntity(formData, StandardCharsets.UTF_8));
            }

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                return parseResponse(response);
            }
        } catch (Exception e) {
            throw new HttpClientException("POST请求失败: " + url, e);
        }
    }

    /**
     * 执行POST请求（JSON格式）
     * @param url 请求地址
     * @param jsonParams JSON参数
     * @return 封装后的响应对象
     */
    public static HttpResponse doPost4Json(String url, Map<String, Object> jsonParams) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(buildRequestConfig());

            // 必须添加的请求头
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");

            if (jsonParams != null && !jsonParams.isEmpty()) {
                String jsonBody = JSON.toJSONString(jsonParams,
                        SerializerFeature.WriteMapNullValue,
                        SerializerFeature.PrettyFormat);

                StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
                httpPost.setEntity(entity);
            }

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                return parseResponse(response);
            }
        } catch (Exception e) {
            throw new HttpClientException("POST-JSON请求失败: " + url, e);
        }
    }

    /**
     * 执行POST请求（JSON格式+自定义请求头）
     * @param url 请求地址
     * @param jsonParams JSON参数
     * @param headers 自定义请求头Map
     * @return 封装后的响应对象
     */
    public static HttpResponse doPost4JsonWithHeaders(
            String url,
            Map<String, Object> jsonParams,
            Map<String, String> headers) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(buildRequestConfig());

            // 设置默认头
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");

            // 添加自定义头
            if (headers != null) {
                headers.forEach(httpPost::setHeader);
            }

            if (jsonParams != null && !jsonParams.isEmpty()) {
                String jsonBody = JSON.toJSONString(jsonParams,
                        SerializerFeature.WriteMapNullValue,
                        SerializerFeature.PrettyFormat);

                StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
                httpPost.setEntity(entity);
            }

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                return parseResponse(response);
            }
        } catch (Exception e) {
            throw new HttpClientException("POST-JSON请求失败: " + url, e);
        }
    }


    /**
     * 执行DELETE请求
     * @param url 请求地址
     * @param params 查询参数（可选）
     * @param headers 自定义请求头（可选）
     * @return 封装后的响应对象
     */
    public static HttpResponse doDelete(String url,
                                        Map<String, String> params,
                                        Map<String, String> headers) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 构建带参数的URI
            URI uri = buildUriWithParams(url, params);
            HttpDelete httpDelete = new HttpDelete(uri);
            httpDelete.setConfig(buildRequestConfig());

            // 添加自定义头
            if (headers != null) {
                headers.forEach(httpDelete::setHeader);
            }

            try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
                return parseResponse(response);
            }
        } catch (Exception e) {
            throw new HttpClientException("DELETE请求失败: " + url, e);
        }
    }

    /**
     * 执行DELETE请求（JSON格式）
     * @param url 请求地址
     * @param jsonBody JSON请求体
     * @param headers 自定义请求头（可选）
     * @return 封装后的响应对象
     */
    public static HttpResponse doDeleteWithJsonBody(String url,
                                                    Object jsonBody,
                                                    Map<String, String> headers) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
            httpDelete.setConfig(buildRequestConfig());

            // 设置默认头
            httpDelete.setHeader("Content-Type", "application/json");
            httpDelete.setHeader("Accept", "application/json");

            // 添加自定义头
            if (headers != null) {
                headers.forEach(httpDelete::setHeader);
            }

            if (jsonBody != null) {
                String jsonStr = JSON.toJSONString(jsonBody,
                        SerializerFeature.WriteMapNullValue,
                        SerializerFeature.PrettyFormat);
                httpDelete.setEntity(new StringEntity(jsonStr, StandardCharsets.UTF_8));
            }

            try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
                return parseResponse(response);
            }
        } catch (Exception e) {
            throw new HttpClientException("DELETE-JSON请求失败: " + url, e);
        }
    }

    // 支持请求体的HttpDelete实现
    private static class HttpDeleteWithBody extends HttpPost {
        public HttpDeleteWithBody(String url) {
            super(url);
        }

        @Override
        public String getMethod() {
            return "DELETE";
        }
    }



    // 构建带参数的URI
    private static URI buildUriWithParams(String url, Map<String, String> params) throws Exception {
        URIBuilder builder = new URIBuilder(url);
        if (params != null) {
            params.forEach(builder::addParameter);
        }
        return builder.build();
    }

    // 解析响应
    private static HttpResponse parseResponse(CloseableHttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        Map<String, String> headers = Arrays.stream(response.getAllHeaders())
                .collect(Collectors.toMap(
                        Header::getName,
                        Header::getValue,
                        (v1, v2) -> v1));  // 重复header取第一个

        String body = "";
        if (response.getEntity() != null) {
            body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        }

        return new HttpResponse(statusCode, body, headers);
    }



    // 构建请求配置
    private static RequestConfig buildRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(TIMEOUT_MSEC)
                .setConnectionRequestTimeout(TIMEOUT_MSEC)
                .setSocketTimeout(TIMEOUT_MSEC)
                .build();
    }

    // 自定义异常
    public static class HttpClientException extends RuntimeException {
        public HttpClientException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
