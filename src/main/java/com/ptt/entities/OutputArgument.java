package com.ptt.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

public class OutputArgument {
    private final Long id;
    private final Step step;
    private final String name;
    private final String jsonLocation;

    public OutputArgument(Long id, Step step, String name, String jsonLocation) {
        this.id = id;
        this.step = step;
        this.name = name;
        this.jsonLocation = jsonLocation;
    }

    public Long getId() {
        return id;
    }

    public Step getStep() {
        return step;
    }

    public String getName() {
        return name;
    }

    public String getJsonLocation() {
        return jsonLocation;
    }
}
