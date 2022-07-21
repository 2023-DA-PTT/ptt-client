package com.ptt.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.List;

public class Plan {
    public Long id;
    public Step start;
    public List<Step> steps;
    public String name;
    public String description;
}
