package com.ptt.control;

import javax.enterprise.context.ApplicationScoped;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@ApplicationScoped
public class ScriptExecutor {

    public ScriptExecutor() {
    }

    public void execute(String script, Object... param) throws ScriptException, NoSuchMethodException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.eval(script);
        Invocable inv = (Invocable) engine;
        inv.invokeFunction(script, param);
    }
}
