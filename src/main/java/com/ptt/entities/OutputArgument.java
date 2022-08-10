package com.ptt.entities;

public class OutputArgument {
    private final Long id;
    private final Step step;
    private final String name;
    private final String parameterLocation;

    public OutputArgument(Long id, Step step, String name, String parameterLocation) {
        this.id = id;
        this.step = step;
        this.name = name;
        this.parameterLocation = parameterLocation;
    }

    public Long getId() {
        return id;
    }

    public Step getStep() {
        return step;
    }

    public String getName() {
        return name;
    }

    public String getParameterLocation() {
        return parameterLocation;
    }
}
