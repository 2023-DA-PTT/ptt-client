package com.ptt.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.List;

public class Step {
    public Long id;
    public Plan plan;
    public List<NextStep> nextSteps;
    public String name;
    public String description;
    public String method;
    public String url;
    public String body;
    public List<InputArgument> inputArguments;
    public List<OutputArgument> outputArguments;

}
