package com.ptt.httpclient.boundary;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;

import com.ptt.httpclient.entity.RequestResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.rmi.UnknownHostException;
import java.time.Instant;

public class HttpExecutor {
    private final CloseableHttpClient httpClient;
    HttpUriRequestBase request;

    public HttpExecutor(CloseableHttpClient httpClient, HttpUriRequestBase request) {
        this.httpClient = httpClient;
        this.request = request;
    }

    public RequestResult execute() throws IOException {
        long startMilli = Instant.now().toEpochMilli();
        long startTime = System.nanoTime();
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(request);
        } catch (UnknownHostException exception) {
            throw new UnknownHostException("Step Failed: cannot reach endpoint.");
        }
        long endTime = System.nanoTime();

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
          (response.getEntity().getContent(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        return new RequestResult(response.getCode(),
                textBuilder.toString(), startMilli, endTime-startTime);
    }
}
