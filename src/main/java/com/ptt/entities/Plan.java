package com.ptt.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public final class Plan {
    private final Long id;
    private Step start = null;
    private final List<Step> steps = new ArrayList<>();
    private final String name;
    private final String description;

    public Plan(Long id, Step start, String name, String description) {
        this.id = id;
        this.start = start;
        this.name = name;
        this.description = description;
    }

    public Plan(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void setStart(Step start) {
        this.start = this.start == null ? start : this.start;
    }

    public Long getId() {
        return id;
    }

    public Step getStart() {
        return start;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
