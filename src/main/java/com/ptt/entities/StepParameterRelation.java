package com.ptt.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

public class StepParameterRelation extends PanacheEntityBase {
    public Long id;
    public InputArgument to;
    public OutputArgument from;
}
