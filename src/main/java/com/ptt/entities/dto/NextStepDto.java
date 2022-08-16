package com.ptt.entities.dto;

public class NextStepDto {
    private Long id;
    private StepDto toStep;
    private int repeatAmount;
    
    public NextStepDto(Long id, StepDto toStep, int repeatAmount) {
        this.id = id;
        this.toStep = toStep;
        this.repeatAmount = repeatAmount;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public StepDto getToStep() {
        return toStep;
    }
    public void setToStep(StepDto toStep) {
        this.toStep = toStep;
    }
    public int getRepeatAmount() {
        return repeatAmount;
    }
    public void setRepeatAmount(int repeatAmount) {
        this.repeatAmount = repeatAmount;
    }
}
