package com.ptt.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

public class InputArgument {
    public Long id;
    public Step step;
    public String name;
    public OutputArgument from;
}
