package com.ptt.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

public class StepParameterRelation extends PanacheEntityBase {
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
