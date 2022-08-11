package com.ptt.control;

import com.jayway.jsonpath.PathNotFoundException;
import com.ptt.boundary.MqttSender;
import com.ptt.entities.*;
import com.ptt.entities.dto.DataPointClientDto;
import com.ptt.httpclient.boundary.HttpExecutor;
import com.ptt.httpclient.control.HttpExecutorBuilder;
import com.ptt.httpclient.control.HttpHelper;
import com.ptt.httpclient.entity.RequestResult;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import org.graalvm.polyglot.*;

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

        private static Map<RequestContentType, String> CONTENT_TYPE_MAPPING = Map.ofEntries(
            Map.entry(RequestContentType.APPLICATION_JSON, "application/json"),
            Map.entry(RequestContentType.MULTIPART_FORM_DATA, "multipart/form-data")
        );

        private interface ExecutedStep {
            String getParameter(OutputArgument argument) throws IOException;
        }

        private ExecutedStep executeStep(Step step, Map<String, ParameterValue> params) throws IOException {
            if (step instanceof HttpStep) {
                return executeHttpStep((HttpStep) step, params);
            } else if (step instanceof ScriptStep) {
                return executeScriptStep((ScriptStep) step, params);
            }
            throw new IllegalStateException("Step is of unknown Step! step: " + step.toString());
        }

        private ExecutedStep executeScriptStep(ScriptStep step, Map<String, ParameterValue> params) throws IOException {
            String scr = "(function(params) {"
                    + step.getScript()
                    + "})";
            Context context = Context.newBuilder("js")
                    .allowHostAccess(HostAccess.ALL)
                    .allowHostClassLookup(className -> true)
                    .build();
            Value func = context.eval("js", scr);
            Value result = func.execute(params);
            return (OutputArgument argument) -> result.getMember(argument.getParameterLocation()).asString();
        }

        private ExecutedStep executeHttpStep(HttpStep step, Map<String, ParameterValue> params) throws IOException {
            HttpExecutorBuilder executorBuilder = HttpExecutorBuilder
                    .create()
                    .setUrl(HttpHelper.parseRequestUrl(step.getUrl(), params))
                    .setMethod(step.getMethod())
                    .setContentType(CONTENT_TYPE_MAPPING.get(RequestContentType.APPLICATION_JSON));
            switch (step.getContentType()) {
                case APPLICATION_JSON -> executorBuilder.setBody(HttpHelper.parseRequestBody(step.getBody(), params));
                case MULTIPART_FORM_DATA -> {
                    for(String key : params.keySet()) {
                        executorBuilder.addMultipartParameter(key, params.get(key)); 
                    }
                    executorBuilder.setBody(HttpHelper.parseRequestBody(step.getBody(), params));
                }
            }
            HttpExecutor executor = executorBuilder.build();
            RequestResult result = executor.execute();
            DataPointClientDto dataPoint = new DataPointClientDto(planRunId,
                    step.getId(),
                    result.getStartTime(),
                    result.getDuration());

            LOG.info(String.format("Sent request to endpoint: %s", result.toString()));
            mqttSender.send(dataPoint);
            LOG.info(String.format("Sent data to backend: %s", dataPoint.toString()));
            return (OutputArgument argument) -> result.getContent(argument.getParameterLocation());
        }

        @Override
        public int run(String... args) throws Exception {
            PlanRun planRun = planService.readPlanRun(planRunId);
            LOG.info(String.format("Read plan run with id %d successfully", planRun.getId()));
            long endTime = planRun.getStartTime() + planRun.getDuration();
            while (endTime >= Instant.now().getEpochSecond()) {
                Queue<QueueElement> stepQueue = new LinkedList<>();
                stepQueue.add(new QueueElement(planRun.getPlan().getStart()));

                while (!stepQueue.isEmpty()) {
                    QueueElement queueElement = stepQueue.poll();
                    Step step = queueElement.getStep();
                    LOG.info(String.format("Entering Queue step: %s", step.toString()));
                    ExecutedStep execStep = executeStep(step, queueElement.getParameters());

                    if (endTime < Instant.now().getEpochSecond()) {
                        break;
                    }
                    try {
                        for (NextStep nextStep : step.getNextSteps()) {
                            QueueElement newQueueElement = new QueueElement(nextStep.getNext());
                            for (StepParameterRelation param : nextStep.getParams()) {
                                String parameterContent = execStep.getParameter(param.getFrom());
                                newQueueElement.getParameters().put(
                                    param.getTo().getName(),
                                    new ParameterValue(parameterContent, param.getFrom().getOutputType()));
                            }
                            stepQueue.add(newQueueElement);
                        }
                    } catch (IOException e) {
                        LOG.warn(String.format("Could not read output parameter from response body!"), e);
                    } catch (PathNotFoundException pnfe) {
                        LOG.warn(String.format("Response body doesn't include parameter"), pnfe);
                    }
                }
            }
            return 0;
        }
    }
}