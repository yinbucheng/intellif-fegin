package cn.intellif.fegin.intellif.utils;


import com.alibaba.fastjson.JSONObject;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ApacheHttpUtils {

    private static Logger logger = LoggerFactory.getLogger(ApacheHttpUtils.class);

    /**
     * 通过get请求获取sessionId
     * @param url
     * @return
     */
    public static String requireCookieByGet(String url){
        try {
            HttpClientContext context = new HttpClientContext();
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = client.execute(httpGet,context);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            List<Cookie> cookies = context.getCookieStore().getCookies();
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("JSESSIONID"))
                    return cookie.getName() + "=" + cookie.getValue();
            }
        }catch (Exception e){
            logger.error(Constant.PRE_LOG+"错误信息："+e);
        }
        return null;
    }

    /**
     * 通过post请求获取sessionId
     * @param url
     * @param params
     * @return
     */
    public static String requireCookieByPost(String url,Map<String,Object> params){
        HttpClientContext context = new HttpClientContext();
        CloseableHttpClient client = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(url);
        try {
            UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(
                    getParam(params), "UTF-8");
            httpPost.setEntity(postEntity);
            // 执行post请求
            HttpResponse httpResponse = client.execute(httpPost, context);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            List<Cookie> cookies = context.getCookieStore().getCookies();
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("JSESSIONID"))
                    return cookie.getName() + "=" + cookie.getValue();
            }
        }catch(Exception e){
           logger.error(Constant.PRE_LOG+"错误信息:"+e);
        }
        return null;
    }


        private static List<NameValuePair> getParam(Map<String,Object> parameterMap) {
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            Iterator it = parameterMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry parmEntry = (Map.Entry) it.next();
                param.add(new BasicNameValuePair((String) parmEntry.getKey(),
                        (String) parmEntry.getValue()));
            }
            return param;
        }


    /**
     * get请求
     * @param path 网络地址
     * @param headers 头部信息
     * @param sessionId 唯一标示
     * @return
     */
    public static String get(String path,Map<String,String> headers,String sessionId) {
        try {
            URL url = new URL(path.trim());
            // 打开连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if(sessionId!=null){
                urlConnection.setRequestProperty("Cookie",sessionId);
            }
            if(headers!=null){
                for(Map.Entry<String,String> entry:headers.entrySet()){
                    String key = entry.getKey();
                    String value = entry.getValue();
                    urlConnection.addRequestProperty(key,value);
                }
            }
            if (200 == urlConnection.getResponseCode()) {
                // 得到输入流
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = is.read(buffer))) {
                    baos.write(buffer, 0, len);
                    baos.flush();
                }
                return baos.toString("utf-8");
            }
            return null;
        } catch (Exception e) {
           logger.error(Constant.PRE_LOG+"错误信息:"+e);
            return "";
        }
    }

    /**
     * 发送json格式数据
     * @param url 网络地址
     * @param params 参数
     * @param sessionId 标示
     * @return
     * @throws Exception
     */
    public static String postJson(String url,Map<String,Object> params,String sessionId) {
        try {
            HttpPost httpPost = new HttpPost(url);
            HttpClientContext context = new HttpClientContext();
            CloseableHttpClient client = HttpClients.createDefault();
            String respContent = null;
            StringEntity entity = new StringEntity(JSONObject.toJSONString(params), "utf-8");//解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            if (sessionId != null) {
                httpPost.addHeader(new BasicHeader("Cookie", sessionId));
            }
            HttpResponse resp = client.execute(httpPost);
            respContent = EntityUtils.toString(resp.getEntity());
            return respContent;
        }catch (Exception e){
            logger.error(Constant.PRE_LOG+"错误信息:"+e);
        }
        return null;
    }


    /**
     * post请求上传form格式
     * @param url 网络地址
     * @param params 参数
     * @param sessionId 唯一标示
     * @return
     */
    public static String postForm(String url,Map<String,Object> params,String sessionId){
        try {
            HttpClientContext context = new HttpClientContext();
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(url);
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(getParam(params), Consts.UTF_8);   //返回的实体
            httppost.setEntity(entity);
            if(sessionId!=null) {
                httppost.addHeader(new BasicHeader("Cookie", sessionId));
            }
            //检测以下响应状态是否正确
            CloseableHttpResponse resp = httpclient.execute(httppost,context);
            System.out.println(resp);
            return EntityUtils.toString(resp.getEntity());
        }catch (Exception e){
           logger.error(Constant.PRE_LOG+"错误信息:"+e);
        }
        return null;
    }

    public static String postXml(String url,String content,String sessionId){
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            //将流转换并放入到InputStreamEntity中
            InputStreamEntity inputStreamEntity = new InputStreamEntity(new ByteArrayInputStream(content.getBytes("utf-8")));
            HttpUriRequest httpPost = null;
            RequestBuilder builder = RequestBuilder.put()
                    .setUri(url)
                    .setEntity(inputStreamEntity)
                    .setHeader("Content-Type", "application/xml");
            if(sessionId!=null){
                builder .setHeader("Cookie", sessionId);
            }
                 httpPost = builder .build();
            CloseableHttpResponse response = httpclient.execute(httpPost);
            System.out.println(response.getStatusLine().getStatusCode());
            HttpEntity responseEntity=response.getEntity();
            return EntityUtils.toString(responseEntity);
        }catch (Exception e){
           logger.error(Constant.PRE_LOG+"错误信息:"+e);
        }
        return null;
    }

}