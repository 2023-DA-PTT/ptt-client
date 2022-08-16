package com.ptt.entities;

import java.util.ArrayList;
import java.util.List;

public class NextStep {
    private final Step next;
    private final int repeatAmount;
    private final List<StepParameterRelation> params = new ArrayList<>();

    public NextStep(Step next, int repeatAmount) {
        this.next = next;
        this.repeatAmount = repeatAmount;
    }

    public Step getNext() {
        return next;
    }

    public List<StepParameterRelation> getParams() {
        return params;
    }

    public int getRepeatAmount() {
        return repeatAmount;
    }
}
