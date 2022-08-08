package com.ptt.control;

import com.jayway.jsonpath.PathNotFoundException;
import com.ptt.boundary.MqttSender;
import com.ptt.boundary.httpclient.HttpExecutor;
import com.ptt.boundary.httpclient.HttpExecutorBuilder;
import com.ptt.boundary.httpclient.HttpHelper;
import com.ptt.boundary.httpclient.RequestResult;
import com.ptt.entities.*;
import com.ptt.entities.dto.DataPointClientDto;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

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

        private static final Logger LOG = Logger.getLogger(ClientApp.class);

        @Inject
        PlanService planService;

        @Inject
        MqttSender mqttSender;

        @ConfigProperty(name = "test.plan-run.id")
        long planRunId;

        @Override
        public int run(String... args) throws Exception {
            PlanRun planRun = planService.readPlanRun(planRunId);
            LOG.info(String.format("Read plan run with id %d successfully", planRun.getId()));
            Queue<QueueElement> stepQueue = new LinkedList<>();
            stepQueue.add(new QueueElement(planRun.getPlan().getStart()));

            while (!stepQueue.isEmpty()) {
                QueueElement queueElement = stepQueue.poll();
                Step step = queueElement.getStep();
                LOG.info(String.format("Entering Queue step: %s", step.toString()));

                HttpExecutor executor = HttpExecutorBuilder
                        .create()
                        .setUrl(step.getUrl())
                        .setMethod(step.getMethod())
                        .setBody(HttpHelper.parseRequestBody(step.getBody(), queueElement.getParameters()))
                        .build();
                RequestResult result = executor.execute();
                DataPointClientDto dataPoint = new DataPointClientDto(planRun.getId(),
                        step.getId(),
                        result.getStartTime(),
                        result.getDuration());

                LOG.info(String.format("Sent request to endpoint: %s", result.toString()));
                mqttSender.send(dataPoint);
                LOG.info(String.format("Sent data to backend: %s", dataPoint.toString()));
                try {
                    for (NextStep nextStep : step.getNextSteps()) {
                        QueueElement newQueueElement = new QueueElement(nextStep.getNext());
                        for (StepParameterRelation param : nextStep.getParams()) {
                            LOG.info(String.format("Reading json output of response. jsonLocation: %s",
                                    param.getFrom().getJsonLocation()));
                            newQueueElement.getParameters().put(param.getTo().getName(),
                                    result.getContent(param.getFrom().getJsonLocation()));
                        }
                        stepQueue.add(newQueueElement);
                    }
                } catch (IOException e) {
                    LOG.warn(String.format("Could not read output parameter from response body!"), e);
                } catch (PathNotFoundException pnfe) {
                    LOG.warn(String.format("Response body doesn't include parameter"), pnfe);
                }
            }
            return 0;
        }
    }
}