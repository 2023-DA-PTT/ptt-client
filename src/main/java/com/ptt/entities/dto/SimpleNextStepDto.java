package com.ptt.entities.dto;

public class SimpleNextStepDto {
    private Long id;
    private Long fromStepId;
    private Long toStepId;
    private int repeatAmount;

    public SimpleNextStepDto(Long id,
            long fromStepId,
            long toStepId,
            int repeatAmount) {
        this.id = id;
        this.fromStepId = fromStepId;
        this.toStepId = toStepId;
        this.repeatAmount = repeatAmount;
    }

    public Long getToStepId() {
        return toStepId;
    }

    public void setToStepId(Long toStepId) {
        this.toStepId = toStepId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRepeatAmount() {
        return repeatAmount;
    }

    public void setRepeatAmount(int repeatAmount) {
        this.repeatAmount = repeatAmount;
    }

    public Long getFromStepId() {
        return fromStepId;
    }

    public void setFromStepId(Long fromStepId) {
        this.fromStepId = fromStepId;
    }
}
