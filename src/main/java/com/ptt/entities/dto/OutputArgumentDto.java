package com.ptt.entities.dto;

public class OutputArgumentDto {
    public long id;
    public long stepId;
    public String name;
    public String parameterLocation;

    public OutputArgumentDto(long id,long stepId, String name, String parameterLocation) {
        this.id = id;
        this.stepId = stepId;
        this.name = name;
        this.parameterLocation = parameterLocation;
    }
}
