package com.ptt.control;

import org.eclipse.microprofile.config.ConfigProvider;

import io.smallrye.config.SmallRyeConfig;

public class Main {

  public static void main(String[] args) {
    SmallRyeConfig config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);

    long planRunId = config.getValue("ptt-client.plan-run.id", Long.class);
    int mqttPort = config.getValue("ptt-client.messaging.outgoing.measurements.port", Integer.class);
    String mqttAddress = config.getValue("ptt-client.messaging.outgoing.measurements.host", String.class);

    new PttClient(config, mqttPort, mqttAddress, planRunId).runTestPlan();
  }
}
