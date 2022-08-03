package com.ptt.control;

import com.ptt.boundary.httpclient.HttpExecutor;
import com.ptt.boundary.httpclient.HttpExecutorBuilder;
import com.ptt.boundary.httpclient.HttpHelper;
import com.ptt.boundary.httpclient.RequestResult;
import com.ptt.entities.*;
import com.ptt.entities.dto.DataPoint;

import javax.inject.Inject;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main {
    public static void main(String... args) {
        Quarkus.run(ClientApp.class, args);
    }

    public static class ClientApp implements QuarkusApplication {
        @Inject
        PlanService planService;

        @Override
        public int run(String... args) throws Exception {
            Plan plan = planService.readPlan(1);

            Queue<QueueElement> stepQueue = new LinkedList<>();
            stepQueue.add(new QueueElement(plan.getStart()));

            while (!stepQueue.isEmpty()) {
                QueueElement queueElement = stepQueue.poll();
                Step step = queueElement.getStep();

                HttpExecutor executor = HttpExecutorBuilder
                        .create()
                        .setUrl(step.getUrl())
                        .setMethod(step.getMethod())
                        .setBody(HttpHelper.parseRequestBody(step.getBody(), queueElement.getParameters()))
                        .build();
                RequestResult result = executor.execute();
                DataPoint dataPoint = new DataPoint(plan.getId(),
                        step.getId(),
                        result.getStartTime(),
                        result.getEndTime() - result.getStartTime());
                // TODO: send duration to backend
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
            Quarkus.waitForExit();
            return 0;
        }
    }
}