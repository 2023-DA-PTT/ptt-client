package com.ptt.httpclient.entity;

import com.jayway.jsonpath.JsonPath;

import java.io.IOException;

public class RequestResult {
    private final int statusCode;
    private final String content;
    private final long startTime;
    private final long duration;

    public RequestResult(int statusCode, String content, long startTime, long duration) throws IOException {
        this.statusCode = statusCode;
        this.content = content;
        this.startTime = startTime;
        this.duration = duration;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getContent() {
        return content;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public <T> T getContent(String jsonLocation) throws IOException {
        return JsonPath.read(content, jsonLocation);
    }

    @Override
    public String toString() {
        return "RequestResult [duration=" + duration + ", startTime=" + startTime + ", statusCode=" + statusCode + "]";
    }


    
}
