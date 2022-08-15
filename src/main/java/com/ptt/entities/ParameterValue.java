package com.ptt.entities;

public class ParameterValue {
    private final String value;
    private final OutputType type;
    
    public ParameterValue(String value, OutputType type) {
        this.value = value;
        this.type = type;
    }
    
    public String getValue() {
        return value;
    }

    public OutputType getType() {
        return type;
    }    
}
