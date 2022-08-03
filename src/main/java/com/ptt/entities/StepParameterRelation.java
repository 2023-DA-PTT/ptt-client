package com.ptt.entities;

public class StepParameterRelation {
    private final InputArgument to;
    private final OutputArgument from;

    public StepParameterRelation(InputArgument to, OutputArgument from) {
        this.to = to;
        this.from = from;
    }

    public InputArgument getTo() {
        return to;
    }

    public OutputArgument getFrom() {
        return from;
    }
}
