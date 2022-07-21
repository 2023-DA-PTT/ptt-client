package com.ptt.control;

import com.ptt.entities.Step;

import java.util.HashMap;
import java.util.Map;

public class QueueElement {
    private final Step step;
    private final Map<String, String> parameters = new HashMap<>();
    public QueueElement(Step step) {
        this.step = step;
    }

    public Step getStep() {
        return step;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}