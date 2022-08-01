package com.ptt.entities;

import java.util.ArrayList;
import java.util.List;

public class Step {
    private final Long id;
    private final Plan plan;
    private final List<NextStep> nextSteps = new ArrayList<>();
    private final String name;
    private final String description;
    private final String method;
    private final String url;
    private final String body;
    private final List<InputArgument> inputArguments = new ArrayList<>();
    private final List<OutputArgument> outputArguments = new ArrayList<>();

    public Step(Long id, Plan plan, String name, String description, String method, String url, String body) {
        this.id = id;
        this.plan = plan;
        this.name = name;
        this.description = description;
        this.method = method;
        this.url = url;
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public Plan getPlan() {
        return plan;
    }

    public List<NextStep> getNextSteps() {
        return nextSteps;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    public List<InputArgument> getInputArguments() {
        return inputArguments;
    }

    public List<OutputArgument> getOutputArguments() {
        return outputArguments;
    }
}
