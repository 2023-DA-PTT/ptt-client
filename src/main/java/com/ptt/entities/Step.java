package com.ptt.entities;

import java.util.ArrayList;
import java.util.List;

public class Step {
    private final Long id;
    private final Plan plan;
    private final List<NextStep> nextSteps = new ArrayList<>();
    private final String name;
    private final String description;
    private final List<InputArgument> inputArguments = new ArrayList<>();
    private final List<OutputArgument> outputArguments = new ArrayList<>();

    public Step(Long id, Plan plan, String name, String description) {
        this.id = id;
        this.plan = plan;
        this.name = name;
        this.description = description;
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

    public List<InputArgument> getInputArguments() {
        return inputArguments;
    }

    public List<OutputArgument> getOutputArguments() {
        return outputArguments;
    }

    public OutputArgument getOutputArgumentByName(String name) {
        for(OutputArgument arg : outputArguments) {
            if(arg.getName().equals(name)) {
                return arg;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Step [description=" + description + ", id=" + id + ", name=" + name + ", plan=" + plan + "]";
    }
}
