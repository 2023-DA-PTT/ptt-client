package com.ptt;

import com.ptt.entities.OutputArgument;
import com.ptt.entities.Plan;
import com.ptt.entities.Step;
import com.ptt.entities.dto.PlanDto;
import com.ptt.service.PlanService;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

@ApplicationScoped
public class InitBean {
    @Inject
    PlanService planService;

    public void startUp(@Observes StartupEvent event) {
        Plan plan = planService.readPlan(1);

        Queue<Step> stepQueue = new PriorityQueue<>();
        stepQueue.add(plan.start);

        while(!stepQueue.isEmpty()) {
            Step step = stepQueue.poll();
            //exec request //TODO
            String jsonResponse = "";
            JsonObject json = JsonValue.EMPTY_JSON_OBJECT;
            Map<String, String> outputMap = new HashMap<>();
            for (OutputArgument outputArgument : step.outputArguments) {
                String value = json.getValue(outputArgument.jsonLocation).toString(); //TODO: change jsonLocation to json pointer
                
            }
        }

    }
}
