package com.ptt.entities;

public class HttpStep extends Step {
    private final String method;
    private final String url;
    private final String body;
    private final RequestContentType responseContentType;
    private final RequestContentType contentType; 

    public HttpStep(Long id, Plan plan, String name, String description, String method, String url, String body,
    RequestContentType responseContentType, RequestContentType contentType) {
        super(id, plan, name, description);
        this.method = method;
        this.url = url;
        this.body = body;
        this.responseContentType = responseContentType;
        this.contentType = contentType;
    }

    public RequestContentType getResponseContentType() {
        return responseContentType;
    }

    public RequestContentType getContentType() {
        return contentType;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }
}
