package com.ptt.control;

import com.jayway.jsonpath.PathNotFoundException;
import com.ptt.boundary.MqttSender;
import com.ptt.entities.*;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.time.Instant;
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
            long endTime = planRun.getStartTime() + planRun.getDuration();
            boolean runOnce = planRun.isRunOnce();
            while (endTime >= Instant.now().getEpochSecond() || runOnce) {
                Queue<QueueElement> stepQueue = new LinkedList<>();
                stepQueue.add(new QueueElement(planRun.getPlan().getStart()));

                while (!stepQueue.isEmpty()) {
                    QueueElement queueElement = stepQueue.poll();
                    Step step = queueElement.getStep();
                    LOG.info(String.format("Entering Queue step: %s", step.toString()));
                    ExecutedStep execStep = StepExecution.executeStep(planRunId, mqttSender, step, queueElement.getParameters());

                    if (endTime < Instant.now().getEpochSecond() && !runOnce) {
                        break;
                    }
                    try {
                        for (NextStep nextStep : step.getNextSteps()) {
                            QueueElement newQueueElement = new QueueElement(nextStep.getNext());
                            for (StepParameterRelation param : nextStep.getParams()) {
                                ParameterValue parameterContent;
                                if (param.getFrom().getOutputType().equals(OutputType.FROM_INPUT_PARAMETER)) {
                                    parameterContent = queueElement.getParameters().get(param.getFrom().getParameterLocation());
                                } else {
                                    parameterContent = new ParameterValue(execStep.getParameter(param.getFrom()), param.getFrom().getOutputType());
                                }
                                newQueueElement.getParameters().put(param.getTo().getName(), parameterContent);
                            }
                            for (int i = 0; i < nextStep.getRepeatAmount(); i++) {
                                stepQueue.add(newQueueElement);
                            }
                        }
                    } catch (IOException e) {
                        LOG.warn(String.format("Could not read output parameter from response body!"), e);
                    } catch (PathNotFoundException pnfe) {
                        LOG.warn(String.format("Response body doesn't include parameter"), pnfe);
                    }
                }
                if (runOnce)
                    break;
            }
            return 0;
        }
    }
}