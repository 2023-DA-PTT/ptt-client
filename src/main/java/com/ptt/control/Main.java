package com.ptt.control;

import io.smallrye.config.SmallRyeConfig;
import org.eclipse.microprofile.config.ConfigProvider;

public class Main {

  public static void main(String[] args) {
    SmallRyeConfig config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);

    long planRunId = config.getValue("test.plan-run.id", Long.class);
    int mqttPort = config.getValue("ptt-client.messaging.outgoing.measurements.port", Integer.class);
    String mqttAddress = config.getValue("ptt-client.messaging.outgoing.measurements.host", String.class);

    new PttClient(config, mqttPort, mqttAddress, planRunId).runTestPlan();  }
}
