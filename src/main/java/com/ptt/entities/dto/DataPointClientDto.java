package com.ptt.entities.dto;

public class DataPointClientDto {
    private long planRunId;
    private long stepId;
    private long startTime;
    private long duration;

    public DataPointClientDto(long planRunId, long stepId, long startTime, long duration) {
        this.planRunId = planRunId;
        this.stepId = stepId;
        this.startTime = startTime;
        this.duration = duration;
    }

    public DataPointClientDto() {
    }

    public long getPlanRunId() {
        return planRunId;
    }

    public void setPlanRunId(long planRunId) {
        this.planRunId = planRunId;
    }

    public long getStepId() {
        return stepId;
    }
    public long getStartTime() {
        return startTime;
    }
    public long getDuration() {
        return duration;
    }

    public void setStepId(long stepId) {
        this.stepId = stepId;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
