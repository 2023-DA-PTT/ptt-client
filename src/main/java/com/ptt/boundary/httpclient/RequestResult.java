package com.ptt.boundary.httpclient;

import com.jayway.jsonpath.JsonPath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RequestResult {
    private final int statusCode;
    private final InputStream content;
    private final String contentStr;
    private final long startTime;
    private final long endTime;

    public RequestResult(int statusCode, InputStream content, long startTime, long endTime) throws IOException {
        this.statusCode = statusCode;
        this.content = content;
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
          (content, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        contentStr = textBuilder.toString();
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
        String result = JsonPath.read(contentStr, jsonLocation);
        //content.reset();
        return result;
    }

    @Override
    public String toString() {
        return "RequestResult [endTime=" + endTime + ", startTime=" + startTime + ", statusCode=" + statusCode + "]";
    }


    
}
