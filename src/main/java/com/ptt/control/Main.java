package com.ptt.control;

import java.util.ArrayList;
import java.util.List;

import com.ptt.entities.NextStep;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class Main {

  @SuppressWarnings("rawtypes")
  private static Future<CompositeFuture> doStep(Vertx vertx, QueueElement qe) {
    System.out.println(qe.getStep().getName());
    return vertx.executeBlocking((event) -> {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      event.complete();
    }).compose((arg0) -> {
      List<Future> s = new ArrayList<>();
      for (NextStep nextStep : qe.getStep().getNextSteps()) {
        QueueElement nextElement = new QueueElement(nextStep.getNext());
        s.add(doStep(vertx, nextElement));
      }
      //wait for every next step to finish and then succeed
      return CompositeFuture.join(s);
    });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    PlanService planService = new PlanService();

    planService.read(vertx).andThen((event) -> {
        System.out.println(event.result().getPlan());
        QueueElement queueElement = new QueueElement(event.result().getPlan().getStart());
        doStep(vertx, queueElement).andThen((event2) ->
          event2.result().andThen((compEvent) -> vertx.close()));}
    ).onFailure((event) -> {
      System.out.println("Could not read Test Run!");
      vertx.close();
    });
  }
}
