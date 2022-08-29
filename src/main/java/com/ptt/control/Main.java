package com.ptt.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jayway.jsonpath.PathNotFoundException;
import com.ptt.boundary.MqttSender;
import com.ptt.entities.ExecutedStep;
import com.ptt.entities.NextStep;
import com.ptt.entities.OutputType;
import com.ptt.entities.ParameterValue;
import com.ptt.entities.Step;
import com.ptt.entities.StepParameterRelation;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class Main {

  @SuppressWarnings("rawtypes")
  private static Future<CompositeFuture> doStep(Vertx vertx, QueueElement qe, long planRunId, MqttSender mqttSender) {
    System.out.println(qe.getStep().getName());
    return vertx.executeBlocking((Promise<ExecutedStep> event) -> {
      Step step = qe.getStep();
      ExecutedStep execStep = null;
      try {
        execStep = StepExecution.executeStep(planRunId, mqttSender, step, qe.getParameters());
      } catch (IOException e) {
        e.printStackTrace();
        event.fail(e);
      }
      event.complete(execStep);
    }).compose((blockedEvent) -> {
      List<Future> s = new ArrayList<>();
      try {
        for (NextStep nextStep : qe.getStep().getNextSteps()) {
          QueueElement newQueueElement = new QueueElement(nextStep.getNext());
          for (StepParameterRelation param : nextStep.getParams()) {
            ParameterValue parameterContent;
            if (param.getFrom().getOutputType().equals(OutputType.FROM_INPUT_PARAMETER)) {
              parameterContent = qe.getParameters().get(param.getFrom().getParameterLocation());
            } else {
              parameterContent = new ParameterValue(blockedEvent.getParameter(param.getFrom()),
                  param.getFrom().getOutputType());
            }
            newQueueElement.getParameters().put(param.getTo().getName(), parameterContent);
          }
          for (int i = 0; i < nextStep.getRepeatAmount(); i++) {
            s.add(doStep(vertx, newQueueElement, planRunId, mqttSender));
          }
        }
      } catch (IOException e) {
        //LOG.warn(String.format("Could not read output parameter from response body!"), e);
      } catch (PathNotFoundException pnfe) {
        //LOG.warn(String.format("Response body doesn't include parameter"), pnfe);
      }
      return CompositeFuture.join(s);
    });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    PlanService planService = new PlanService();
    long planRunId = 2; // TODO: read PLAN RUN ID
    MqttSender mqttSender = new MqttSender();
    planService.readPlanRun(vertx, planRunId).andThen((event) -> {
      System.out.println(event.result().getPlan());
      QueueElement queueElement = new QueueElement(event.result().getPlan().getStart());
      doStep(vertx, queueElement, planRunId, mqttSender)
          .andThen((event2) -> event2.result().andThen((compEvent) -> vertx.close()))
          .onFailure((event3) -> {
            System.out.println("TEST FAILURE!");
            vertx.close();
          });
    }).onFailure((event) -> {
      System.out.println("Could not read Test Run!");
      vertx.close();
    });
  }
}
