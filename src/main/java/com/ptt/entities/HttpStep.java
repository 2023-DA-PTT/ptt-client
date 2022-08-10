package com.ptt.entities;

public class HttpStep extends Step {
    private final String method;
    private final String url;
    private final String body;
    private final String responseContentType;

    public HttpStep(Long id, Plan plan, String name, String description, String method, String url, String body,
            String responseContentType) {
        super(id, plan, name, description);
        this.method = method;
        this.url = url;
        this.body = body;
        this.responseContentType = responseContentType;
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

    public String getResponseContentType() {
        return responseContentType;
    }
}
