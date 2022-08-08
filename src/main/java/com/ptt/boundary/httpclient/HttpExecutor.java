package com.ptt.boundary.httpclient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.time.Instant;

public class HttpExecutor {
    private final HttpClient httpClient;
    HttpRequestBase request;

    protected HttpExecutor(HttpClient httpClient, HttpRequestBase request) {
        this.httpClient = httpClient;
        this.request = request;
    }

    public RequestResult execute() throws IOException {
        long startMilli = Instant.now().toEpochMilli();
        long startTime = System.nanoTime();
        HttpResponse response = httpClient.execute(request);
        long endTime = System.nanoTime();
        return new RequestResult(response.getStatusLine().getStatusCode(),
                response.getEntity().getContent(), startMilli, endTime-startTime);
    }
}
