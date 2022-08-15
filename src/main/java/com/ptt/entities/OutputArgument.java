package com.ptt.entities;

public class OutputArgument {
    private final Long id;
    private final Step step;
    private final String name;
    private final String parameterLocation;
    private final OutputType outputType;

    public OutputArgument(Long id, Step step, String name, String parameterLocation, OutputType outputType) {
        this.id = id;
        this.step = step;
        this.name = name;
        this.parameterLocation = parameterLocation;
        this.outputType = outputType;
    }

    public OutputType getOutputType() {
        return outputType;
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
