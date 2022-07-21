package com.ptt.control;

import com.ptt.boundary.httpclient.HttpExecutor;
import com.ptt.boundary.httpclient.HttpHelper;
import com.ptt.boundary.httpclient.RequestResult;
import com.ptt.entities.*;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

@ApplicationScoped
public class InitBean {
    @Inject
    PlanService planService;


    public void startUp(@Observes StartupEvent event) throws IOException {
        Plan plan = planService.readPlan(1);

        Queue<QueueElement> stepQueue = new LinkedList<>();
        stepQueue.add(new QueueElement(plan.getStart()));

        while (!stepQueue.isEmpty()) {
            QueueElement queueElement = stepQueue.poll();
            Step step = queueElement.getStep();

            RequestResult result = HttpExecutor
                    .create()
                    .setUrl(step.getUrl())
                    .setBody(HttpHelper.parseRequestBody(step.getBody(), queueElement.getParameters()))
                    .execute();
            try {
                for (NextStep nextStep : step.getNextSteps()) {
                    QueueElement newQueueElement = new QueueElement(nextStep.getNext());
                    for (StepParameterRelation param : nextStep.getParams()) {
                        newQueueElement.getParameters().put(param.getTo().getName(),
                                result.getContent(param.getFrom().getJsonLocation()));
                    }
                    stepQueue.add(newQueueElement);
                }
            } catch (IOException e) {
                System.out.println("Could not read output parameter from response body!");
            }
        }
    }
}
