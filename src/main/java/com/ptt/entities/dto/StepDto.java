package com.ptt.entities.dto;

public class StepDto {
    public long id;
    public String name;
    public String description;

    public StepDto(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
