package com.ptt.boundary.httpclient;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpExecutorBuilder {
    private String url;
    private String body;
    private String method;
    private final HttpClient httpClient;

    private HttpExecutorBuilder() {
        httpClient = HttpClientBuilder.create().build();
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

    private HttpExecutor buildWithEntityEnclosing(HttpEntityEnclosingRequestBase request) {
        StringEntity params = new StringEntity(body, ContentType.APPLICATION_JSON);
        request.addHeader("content-type", "application/json");
        request.setEntity(params);
        return new HttpExecutor(httpClient, request);
    }
}
