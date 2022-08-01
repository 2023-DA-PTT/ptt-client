package com.ptt.boundary.httpclient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

public class HttpExecutor {
    private final HttpClient httpClient;
    HttpRequestBase request;

    protected HttpExecutor(HttpClient httpClient, HttpRequestBase request) {
        this.httpClient = httpClient;
        this.request = request;
    }

    public RequestResult execute() throws IOException {
        HttpResponse response = httpClient.execute(request);
        return new RequestResult(response.getStatusLine().getStatusCode(),
                response.getEntity().getContent());
    }
}
