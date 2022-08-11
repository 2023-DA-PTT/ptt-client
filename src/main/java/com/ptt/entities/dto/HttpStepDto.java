package com.ptt.entities.dto;

import com.ptt.entities.HttpStep;
import com.ptt.entities.RequestContentType;

public class HttpStepDto {
    private Long id;
    private String name;
    private String description;
    private String method;
    private String url;
    private String body;
    private RequestContentType responseContentType;
    private RequestContentType contentType;

    public HttpStepDto(Long id, String name, String description, String method, String url, String body, RequestContentType responseContentType, RequestContentType contentType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.method = method;
        this.url = url;
        this.body = body;
        this.responseContentType = responseContentType;
        this.contentType = contentType;
    }

    public static HttpStepDto from(HttpStep httpStep) {
        return new HttpStepDto(httpStep.getId(), httpStep.getName(), httpStep.getDescription(), httpStep.getMethod(), httpStep.getUrl(), httpStep.getBody(), httpStep.getResponseContentType(), httpStep.getContentType());
    }

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getMethod() {
        return method;
    }


    public void setMethod(String method) {
        this.method = method;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getBody() {
        return body;
    }


    public void setBody(String body) {
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public RequestContentType getResponseContentType() {
        return responseContentType;
    }


    public void setResponseContentType(RequestContentType responseContentType) {
        this.responseContentType = responseContentType;
    }


    public RequestContentType getContentType() {
        return contentType;
    }


    public void setContentType(RequestContentType contentType) {
        this.contentType = contentType;
    }
}
