package com.ptt.entities.dto;

public class StepParameterRelationDto {
    public Long fromId;
    public Long toId;

    public StepParameterRelationDto(Long fromId, Long toId) {
        this.fromId = fromId;
        this.toId = toId;
    }
}
