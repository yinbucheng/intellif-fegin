package cn.intellif.fegin.intellif.core;

import java.util.Map;

public class RequestDefination {
    //完整的url
    private String url;
    //请求体
    private String[] body;
    //请求头
    private Map<String,String> headers;

    private int methodType;

    public int getMethodType() {
        return methodType;
    }

    public void setMethodType(int methodType) {
        this.methodType = methodType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String[] getBody() {
        return body;
    }

    public void setBody(String[] body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
