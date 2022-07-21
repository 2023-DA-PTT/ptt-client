package com.ptt.boundary.httpclient;

import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.io.InputStream;

public class RequestResult {
    private final int statusCode;
    private final InputStream content;

    public RequestResult(int statusCode, InputStream content) {
        this.statusCode = statusCode;
        this.content = content;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public InputStream getContent() {
        return content;
    }

    public String getContent(String jsonLocation) throws IOException {
        String result = JsonPath.read(content, jsonLocation);
        content.reset();
        return result;
    }
}
