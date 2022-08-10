package com.ptt.httpclient.boundary;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import com.ptt.httpclient.entity.RequestResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class HttpExecutor {
    private final HttpClient httpClient;
    HttpRequestBase request;

    public HttpExecutor(HttpClient httpClient, HttpRequestBase request) {
        this.httpClient = httpClient;
        this.request = request;
    }

    public RequestResult execute() throws IOException {
        long startMilli = Instant.now().toEpochMilli();
        long startTime = System.nanoTime();
        HttpResponse response = httpClient.execute(request);
        long endTime = System.nanoTime();

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
          (response.getEntity().getContent(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        return new RequestResult(response.getStatusLine().getStatusCode(),
                textBuilder.toString(), startMilli, endTime-startTime);
    }
}
