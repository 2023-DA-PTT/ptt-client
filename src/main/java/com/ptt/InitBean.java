package com.ptt;

import com.ptt.entities.NextStep;
import com.ptt.entities.OutputArgument;
import com.ptt.entities.Plan;
import com.ptt.entities.Step;
import com.ptt.entities.dto.PlanDto;
import com.ptt.service.PlanService;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

@ApplicationScoped
public class InitBean {
    @Inject
    PlanService planService;

    public void startUp(@Observes StartupEvent event) {
        Plan plan = planService.readPlan(1);

        Queue<Step> stepQueue = new LinkedList<>();
        stepQueue.add(plan.getStart());

        while(!stepQueue.isEmpty()) {
            Step step = stepQueue.poll();
            System.out.println(step.getName());
            //exec request //TODO:
            for (NextStep nextStep : step.getNextSteps()) {
                stepQueue.add(nextStep.getNext());
            }
        }
    }
}
