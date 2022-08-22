package com.ptt.httpclient.control;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.quarkus.logging.Log;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.ptt.entities.ParameterValue;
import com.ptt.httpclient.boundary.HttpExecutor;

public class HttpExecutorBuilder {
    private String url;
    private String body;
    private String method;
    private String contentType;
    private Map<String, String> headers;
    private final CloseableHttpClient httpClient;
    private final Map<String, ParameterValue> multipartValues;

    private final String multipartBoundary = UUID.randomUUID().toString();

    private HttpExecutorBuilder() {
        multipartValues = new HashMap<>();
        httpClient = HttpClients.createDefault();
        headers = new HashMap<>();
    }

    public static HttpExecutorBuilder create() {
        return new HttpExecutorBuilder();
    }

    public HttpExecutorBuilder setMethod(String method) {
        this.method = method;
        return this;
    }

    public HttpExecutorBuilder setHeader(String header, String value) {
        this.headers.put(header, value);
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

    public HttpExecutorBuilder setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpExecutorBuilder addMultipartParameter(String name, ParameterValue value) {
        multipartValues.put(name, value);
        return this;
    }

    public HttpExecutor build() {
        HttpUriRequestBase request = new HttpUriRequestBase(method, URI.create(url));

        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue());
            Log.info("Sending header " + entry.getKey() + " with value " + entry.getValue());
        }

        switch (method) {
            case "DELETE":
            case "GET":
                return new HttpExecutor(httpClient, request);
            case "POST":
            case "PUT":
            case "PATCH":
                return buildWithEntityEnclosing(request);
            default:
                return null;
        }
    }

    private HttpExecutor buildWithEntityEnclosing(HttpUriRequestBase request) {
        String contentTypeHeaderSuffix = "";
        switch (contentType) {
            case "multipart/form-data":
                MultipartEntityBuilder meb = MultipartEntityBuilder.create();
                for (String key: multipartValues.keySet()) {
                    ParameterValue pv = multipartValues.get(key);
                    switch (pv.getType()) {
                        case PLAIN_TEXT:
                            meb.addTextBody(key, pv.getValue(), ContentType.TEXT_PLAIN);
                            break;
                            case OCTET_STREAM:
                            meb.addTextBody(key, pv.getValue(), ContentType.APPLICATION_OCTET_STREAM);
                            break;
                        default:
                            break;
                    }
                }
                contentTypeHeaderSuffix = "; boundary=\"" + multipartBoundary + "\"";
                meb.setBoundary(multipartBoundary);
                request.setEntity(meb.build());
                break;
            case "application/json":
                StringEntity params = new StringEntity(body, ContentType.APPLICATION_JSON);
                request.setEntity(params);
                break;
            default:
                return null;
        }

        request.addHeader("content-type", contentType + contentTypeHeaderSuffix);

        return new HttpExecutor(httpClient, request);
    }
}
