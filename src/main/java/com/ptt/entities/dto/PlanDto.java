package com.ptt.entities.dto;

public class PlanDto {
    public long id;
    public String name;
    public String description;
    public long startId;

    public PlanDto(long id, long startId, String name, String description) {
        this.startId = startId;
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
