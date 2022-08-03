package com.ptt.boundary.httpclient;

import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.io.InputStream;

public class RequestResult {
    private final int statusCode;
    private final InputStream content;
    private final long startTime;
    private final long endTime;

    public RequestResult(int statusCode, InputStream content, long startTime, long endTime) {
        this.statusCode = statusCode;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public InputStream getContent() {
        return content;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getContent(String jsonLocation) throws IOException {
        String result = JsonPath.read(content, jsonLocation);
        content.reset();
        return result;
    }
}
