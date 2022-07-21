package com.ptt.entities.dto;

public class InputArgumentDto {
    public long id;
    public long stepId;
    public String name;

    public InputArgumentDto(long id, long stepId, String name) {
        this.stepId = stepId;
        this.id = id;
        this.name = name;
    }
}
