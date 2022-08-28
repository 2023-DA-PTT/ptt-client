package com.ptt.control;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.ptt.entities.ParameterValue;
import com.ptt.entities.Step;

public class QueueElement implements Serializable {
    private final Step step;
    private final Map<String, ParameterValue> parameters = new HashMap<>();
    public QueueElement(Step step) {
        this.step = step;
    }

    public Step getStep() {
        return step;
    }

    public Map<String, ParameterValue> getParameters() {
        return parameters;
    }
}
