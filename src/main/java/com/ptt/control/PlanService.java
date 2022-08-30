package com.ptt.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ptt.entities.HttpStep;
import com.ptt.entities.HttpStepHeader;
import com.ptt.entities.InputArgument;
import com.ptt.entities.NextStep;
import com.ptt.entities.OutputArgument;
import com.ptt.entities.OutputType;
import com.ptt.entities.Plan;
import com.ptt.entities.PlanRun;
import com.ptt.entities.RequestContentType;
import com.ptt.entities.ScriptStep;
import com.ptt.entities.Step;
import com.ptt.entities.StepParameterRelation;

import io.smallrye.config.SmallRyeConfig;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

public class PlanService {

  private static final String GET_PLAN_BY_ID_URL_PATH = "/api/plan/export/";
  private static final String GET_PLANRUN_BY_ID_URL_PATH = "/api/planrun/";
  private final String backendUrl;
  private final Integer backendPort;
  private final Boolean backendSsl;

  public PlanService(SmallRyeConfig config) {
    backendUrl = config.getValue("ptt-client.backend.url", String.class);
    backendPort = config.getValue("ptt-client.backend.port", Integer.class);
    backendSsl = config.getValue("ptt-client.backend.ssl", Boolean.class);
  }

  public Future<Plan> readPlan(WebClient client, long planId) {
    return client.get(backendPort, backendUrl, GET_PLAN_BY_ID_URL_PATH + planId)
        .ssl(backendSsl).send()
        .compose((arg0) -> Future.future((event) -> {
          JsonObject planExportJson = arg0.bodyAsJsonObject();
          JsonObject planJson = planExportJson.getJsonObject("plan");
          long startStepId = planJson.getLong("startId");
          Plan plan = new Plan(
              planJson.getLong("id"),
              planJson.getString("name"),
              planJson.getString("description"));

          Map<Long, Step> stepMap = new HashMap<>();
          Map<Long, InputArgument> inputMap = new HashMap<>();
          Map<Long, OutputArgument> outputMap = new HashMap<>();
          Map<Long, NextStep> nextStepMap = new HashMap<>();

          JsonArray httpStepsJson = planExportJson.getJsonArray("httpSteps");
          for (int i = 0; i < httpStepsJson.size(); i++) {
            JsonObject stepJsonObj = httpStepsJson.getJsonObject(i);
            JsonArray headersJson = stepJsonObj.getJsonArray("headers");
            List<HttpStepHeader> headers = new ArrayList<>();
            HttpStep step = new HttpStep(
                stepJsonObj.getLong("id"),
                plan,
                stepJsonObj.getString("name"),
                stepJsonObj.getString("description"),
                stepJsonObj.getString("method"),
                stepJsonObj.getString("url"),
                stepJsonObj.getString("body"),
                RequestContentType.valueOf(stepJsonObj.getString("contentType")),
                RequestContentType.valueOf(stepJsonObj.getString("responseContentType")),
                headers);
            for (int j = 0; j < headersJson.size(); j++) {
              JsonObject headerObj = headersJson.getJsonObject(j);
              headers.add(new HttpStepHeader(
                  headerObj.getLong("id"),
                  headerObj.getString("name"),
                  headerObj.getString("value"),
                  step));
            }
            plan.getSteps().add(step);
            stepMap.put(step.getId(), step);
            if (step.getId() == startStepId) {
              plan.setStart(step);
            }
          }

          JsonArray scriptStepsJson = planExportJson.getJsonArray("scriptSteps");
          for (int i = 0; i < scriptStepsJson.size(); i++) {
            JsonObject stepJsonObj = scriptStepsJson.getJsonObject(i);
            ScriptStep step = new ScriptStep(
                stepJsonObj.getLong("id"),
                plan,
                stepJsonObj.getString("name"),
                stepJsonObj.getString("description"),
                stepJsonObj.getString("script"));
            plan.getSteps().add(step);
            stepMap.put(step.getId(), step);
            if (step.getId() == startStepId) {
              plan.setStart(step);
            }
          }

          JsonArray inputsJson = planExportJson.getJsonArray("inputs");
          for (int i = 0; i < inputsJson.size(); i++) {
            JsonObject input = inputsJson.getJsonObject(i);
            Step step = stepMap.get(input.getLong("stepId"));
            InputArgument inArg = new InputArgument(
                input.getLong("id"),
                step,
                input.getString("name"));
            step.getInputArguments().add(inArg);
            inputMap.put(inArg.getId(), inArg);
          }

          JsonArray outputsJson = planExportJson.getJsonArray("outputs");
          for (int i = 0; i < outputsJson.size(); i++) {
            JsonObject output = outputsJson.getJsonObject(i);
            Step step = stepMap.get(output.getLong("stepId"));
            OutputArgument outArg = new OutputArgument(
                output.getLong("id"),
                step,
                output.getString("name"),
                output.getString("parameterLocation"),
                OutputType.valueOf(output.getString("outputType")));
            step.getOutputArguments().add(outArg);
            outputMap.put(outArg.getId(), outArg);
          }

          JsonArray nextsJson = planExportJson.getJsonArray("nextSteps");
          for (int i = 0; i < nextsJson.size(); i++) {
            JsonObject next = nextsJson.getJsonObject(i);
            Step step = stepMap.get(next.getLong("fromStepId"));
            NextStep nextStep = new NextStep(
                stepMap.get(next.getLong("toStepId")),
                next.getInteger("repeatAmount"));
            step.getNextSteps().add(nextStep);
            nextStepMap.put(nextStep.getNext().getId(), nextStep);
          }

          JsonArray relations = planExportJson.getJsonArray("relations");
          for (int i = 0; i < relations.size(); i++) {
            JsonObject relationJson = relations.getJsonObject(i);
            StepParameterRelation relation = new StepParameterRelation(
                inputMap.get(relationJson.getLong("toId")),
                outputMap.get(relationJson.getLong("fromId")));

            NextStep nextStep = nextStepMap.get(relation.getTo().getStep().getId());
            nextStep.getParams().add(relation);
          }
          event.complete(plan);
        }));
  }

  public Future<PlanRun> readPlanRun(Vertx vertx, long planRunId) {
    WebClient client = WebClient.create(vertx);
    return client
        .get(backendPort, backendUrl, GET_PLANRUN_BY_ID_URL_PATH + planRunId)
        .ssl(backendSsl).send()
        .compose((res) -> Future.future((event) -> {
          readPlan(client, planRunId).andThen((planEvent) -> {
            JsonObject planRunJson = res.bodyAsJsonObject();
            PlanRun planRun = new PlanRun(
                planRunJson.getLong("id"),
                planEvent.result(),
                planRunJson.getLong("startTime"),
                planRunJson.getLong("duration"),
                planRunJson.getBoolean("runOnce"));
            event.complete(planRun);
          });
        }));
  }
}
