package com.ptt.entities;

import java.util.ArrayList;
import java.util.List;

public class NextStep {
    private final Step next;
    private final List<StepParameterRelation> params = new ArrayList<>();

    public NextStep(Step next) {
        this.next = next;
    }

    public Step getNext() {
        return next;
    }

    public List<StepParameterRelation> getParams() {
        return params;
    }
}
