package com.ptt.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jayway.jsonpath.PathNotFoundException;
import com.ptt.entities.ExecutedStep;
import com.ptt.entities.NextStep;
import com.ptt.entities.OutputType;
import com.ptt.entities.ParameterValue;
import com.ptt.entities.Step;
import com.ptt.entities.StepParameterRelation;

import io.smallrye.config.SmallRyeConfig;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;

public class PttClient {

  private final Vertx vertx;
  private final MqttClient mqttClient;
  private final StepExecution stepExecution;
  private final PlanService planService;
  private final int mqttPort;
  private final String mqttAddress;
  private final long planRunId;

  public PttClient(SmallRyeConfig config, int mqttPort, String mqttAddress, long planRunId) {
    this.vertx = Vertx.vertx();;
    this.mqttClient = MqttClient.create(vertx);
    this.stepExecution = new StepExecution(mqttClient, planRunId);
    this.planService = new PlanService(vertx, config);
    this.planRunId = planRunId;
    this.mqttPort = mqttPort;
    this.mqttAddress = mqttAddress;
  }

  public void runTestPlan() {
    planService.readPlanRun(planRunId).andThen((planServiceEvent) -> {
      mqttClient.connect(mqttPort, mqttAddress, (mqttClientEvent) -> {
        QueueElement queueElement = new QueueElement(planServiceEvent.result().getPlan().getStart());
        doStep(planRunId, queueElement)
        .andThen((stepEvent) -> stepEvent.result().andThen((compEvent) -> {
          mqttClient.disconnect().andThen((mqttDisconnectEvent) -> {
            vertx.close();
          });
        }))
        .onFailure((failureEvent) -> {
          failureEvent.printStackTrace();
          System.out.println("TEST FAILURE: " + failureEvent.getMessage());
          mqttClient.disconnect().andThen((mqttDisconnectEvent) -> {
            vertx.close();
          });
        });
      });
    }).onFailure((planServiceFailureEvent) -> {
      System.out.println("Could not read Test Run: " + planServiceFailureEvent.getMessage());
      vertx.close();
    });
  }

  @SuppressWarnings("rawtypes")
  private Future<CompositeFuture> doStep(long planRunId, QueueElement qe) {
    System.out.println(qe.getStep().getName());
    return vertx.executeBlocking((Promise<ExecutedStep> event) -> {
      Step step = qe.getStep();
      ExecutedStep execStep = null;
      try {
        execStep = stepExecution.executeStep(step, qe.getParameters());
        event.complete(execStep);
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Step failed: "+ e.getMessage());
        event.complete((param) -> null);
      }
    }).compose((blockedEvent) -> {
      List<Future> s = new ArrayList<>();
      if(blockedEvent == null) {
        return CompositeFuture.join(s);
      }
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
            s.add(doStep(planRunId, newQueueElement));
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
}
