package com.ptt.entities.dto;

import com.ptt.entities.OutputType;

public class OutputArgumentDto {
    public long id;
    public long stepId;
    public String name;
    public String parameterLocation;
    public OutputType outputType;

    public OutputArgumentDto(long id,long stepId, String name, String parameterLocation, OutputType outputType) {
        this.id = id;
        this.stepId = stepId;
        this.name = name;
        this.parameterLocation = parameterLocation;
        this.outputType = outputType;
    }
}
