package com.ptt.boundary.httpclient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class HttpExecutor {
    private String url;
    private String body;
    private final HttpClient httpClient;

    private HttpExecutor() {
        httpClient = HttpClientBuilder.create().build();
    }

    public static HttpExecutor create() {
        return new HttpExecutor();
    }

    public RequestResult execute() throws IOException {
        HttpPost request = new HttpPost(url);
        StringEntity params = new StringEntity(body, ContentType.APPLICATION_JSON);
        request.addHeader("content-type", "application/json");
        request.setEntity(params);
        HttpResponse response = httpClient.execute(request);
        return new RequestResult(response.getStatusLine().getStatusCode(),
                response.getEntity().getContent());
    }

    public HttpExecutor setUrl(String url) {
        this.url = url;
        return this;
    }

    public HttpExecutor setBody(String requestBody) {
        this.body = requestBody;
        return this;
    }
}
