package com.ptt.entities;

public class ScriptStep extends Step {
    private final String script;

    public ScriptStep(Long id, Plan plan, String name, String description, String script) {
        super(id, plan, name, description);
        this.script = script;
    }

    public String getScript() {
        return script;
    }
}
