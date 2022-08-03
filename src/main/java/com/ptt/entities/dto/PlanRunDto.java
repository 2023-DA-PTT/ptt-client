package com.ptt.entities.dto;

public class PlanRunDto {
    private long id;
    private long planId;
    private long startTime;
    private long duration;

    public PlanRunDto(long id, long planId, long startTime, long duration) {
        this.id = id;
        this.planId = planId;
        this.startTime = startTime;
        this.duration = duration;
    }

    public PlanRunDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPlanId() {
        return planId;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }    

    
}
