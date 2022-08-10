package com.ptt.entities.dto;

import com.ptt.entities.ScriptStep;

public class ScriptStepDto {
    private Long id;
    private String name;
    private String description;
    private String script;

    public ScriptStepDto(Long id, String name, String description, String script) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.script = script;
    }

    public static ScriptStepDto from(ScriptStep httpStep) {
        return new ScriptStepDto(httpStep.getId(), httpStep.getName(), httpStep.getDescription(), httpStep.getScript());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
