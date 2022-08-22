package com.ptt.entities;

public class HttpStepHeader {
    private final Long id;
    private final String name;
    private final String value;
    private final HttpStep step;

    public HttpStepHeader(Long id, String name, String value, HttpStep step) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.step = step;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public HttpStep getStep() {
        return step;
    }
}
