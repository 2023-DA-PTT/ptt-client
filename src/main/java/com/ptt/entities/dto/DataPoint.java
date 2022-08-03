package com.ptt.entities.dto;

public class DataPoint {
    private final long planId;
    private final long stepId;
    private final long startTime;
    private final long duration;

    public DataPoint(long planId, long stepId, long startTime, long duration) {
        this.planId = planId;
        this.stepId = stepId;
        this.startTime = startTime;
        this.duration = duration;
    }

    public long getPlanId() {
        return planId;
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
}
