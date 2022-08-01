package com.ptt.entities;

public class InputArgument {
    private final Long id;
    private final Step step;
    private final String name;

    public InputArgument(Long id, Step step, String name) {
        this.id = id;
        this.step = step;
        this.name = name;
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
}
