package com.ptt.entities;

public class PlanRun {
    private long id;
    private Plan plan;
    private long startTime;
    private long duration;
    private String name;
    private boolean runOnce;

    public PlanRun(long id, Plan plan, long startTime, long duration, boolean runOnce) {
        this.id = id;
        this.plan = plan;
        this.startTime = startTime;
        this.duration = duration;
    }

    public boolean isRunOnce() {
        return runOnce;
    }

    public void setRunOnce(boolean runOnce) {
        this.runOnce = runOnce;
    }

    public PlanRun() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
