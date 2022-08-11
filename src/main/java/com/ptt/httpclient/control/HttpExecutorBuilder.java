package com.ptt.httpclient.control;

import java.util.HashMap;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.ptt.entities.ParameterValue;
import com.ptt.httpclient.boundary.HttpExecutor;

public class HttpExecutorBuilder {
    private String url;
    private String body;
    private String method;
    private String contentType;
    private final CloseableHttpClient httpClient;
    private final Map<String, ParameterValue> multipartValues;

    private HttpExecutorBuilder() {
        multipartValues = new HashMap<>();
        httpClient = HttpClients.createDefault();
    }

    public static HttpExecutorBuilder create() {
        return new HttpExecutorBuilder();
    }

    public HttpExecutorBuilder setMethod(String method) {
        this.method = method;
        return this;
    }

    public HttpExecutorBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public HttpExecutorBuilder setBody(String requestBody) {
        this.body = requestBody;
        return this;
    }

    public HttpExecutorBuilder setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpExecutorBuilder addMultipartParameter(String name, ParameterValue value) {
        multipartValues.put(name, value);
        return this;
    }

    public HttpExecutor build() {
        switch (method) {
            case "DELETE":
                return new HttpExecutor(httpClient, new HttpDelete(url));
            case "GET":
                return new HttpExecutor(httpClient, new HttpGet(url));
            case "POST":
                return buildWithEntityEnclosing(new HttpPost(url));
            case "PUT":
                return buildWithEntityEnclosing(new HttpPut(url));
            case "PATCH":
                return buildWithEntityEnclosing(new HttpPatch(url));
            default:
                return null;
        }
    }

    private HttpExecutor buildWithEntityEnclosing(HttpUriRequestBase request) {
        request.addHeader("content-type", contentType);
        switch (contentType) {
            case "multipart/form-data":
                MultipartEntityBuilder meb = MultipartEntityBuilder.create();
                for (String key: multipartValues.keySet()) {
                    ParameterValue pv = multipartValues.get(key);
                    switch (pv.getType()) {
                        case PLAIN_TEXT:
                            meb.addTextBody(key, pv.getValue(), ContentType.APPLICATION_OCTET_STREAM);
                            break;
                            case OCTET_STREAM:
                            meb.addTextBody(key, pv.getValue(), ContentType.APPLICATION_OCTET_STREAM);
                            break;
                        default:
                            break;
                    }
                }
                request.setEntity(meb.build());
                break;
            case "application/json":
                StringEntity params = new StringEntity(body, ContentType.APPLICATION_JSON);
                request.setEntity(params);
                break;
            default:
                return null;
        }
        return new HttpExecutor(httpClient, request);
    }
}
