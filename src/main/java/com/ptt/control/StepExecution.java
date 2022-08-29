package com.ptt.control;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ptt.boundary.MqttSender;
import com.ptt.entities.ExecutedStep;
import com.ptt.entities.HttpStep;
import com.ptt.entities.HttpStepHeader;
import com.ptt.entities.OutputArgument;
import com.ptt.entities.ParameterValue;
import com.ptt.entities.RequestContentType;
import com.ptt.entities.ScriptStep;
import com.ptt.entities.Step;
import com.ptt.entities.dto.DataPointClientDto;
import com.ptt.httpclient.boundary.HttpExecutor;
import com.ptt.httpclient.control.HttpExecutorBuilder;
import com.ptt.httpclient.control.HttpHelper;
import com.ptt.httpclient.entity.RequestResult;

import org.graalvm.polyglot.*;

public class StepExecution {
  
    private static Map<RequestContentType, String> CONTENT_TYPE_MAPPING = Map.ofEntries(
            Map.entry(RequestContentType.APPLICATION_JSON, "application/json"),
            Map.entry(RequestContentType.MULTIPART_FORM_DATA, "multipart/form-data"));

    public static ExecutedStep executeStep(long planRunId, MqttSender mqttSender, Step step, Map<String, ParameterValue> params) throws IOException {
        if (step instanceof HttpStep) {
            return executeHttpStep(planRunId, mqttSender, (HttpStep) step, params);
        } else if (step instanceof ScriptStep) {
            return executeScriptStep((ScriptStep) step, params);
        }
        throw new IllegalStateException("Step is of unknown Step! step: " + step.toString());
    }

    private static ExecutedStep executeScriptStep(ScriptStep step, Map<String, ParameterValue> params) throws IOException {
        String scr = "(function(params) {"
                + step.getScript()
                + "})";
        Context context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(className -> true)
                .build();
        Value func = context.eval("js", scr);
        Map<String, String> convert = new HashMap<>();
        for (String key : params.keySet()) {
            convert.put(key, params.get(key).getValue());
        }
        Value result = func.execute(convert);
        return (OutputArgument argument) -> result.getMember(argument.getParameterLocation()).asString();
    }

    private static ExecutedStep executeHttpStep(long planRunId, MqttSender mqttSender, HttpStep step, Map<String, ParameterValue> params) throws IOException {
        HttpExecutorBuilder executorBuilder = HttpExecutorBuilder
                .create()
                .setUrl(HttpHelper.parseRequestUrl(step.getUrl(), params))
                .setMethod(step.getMethod());
        for (HttpStepHeader header : step.getHeaders()) {
            executorBuilder.setHeader(header.getName(), HttpHelper.parseRequestBody(header.getValue(), params));
        }
        executorBuilder.setContentType(CONTENT_TYPE_MAPPING.get(step.getContentType()));
        switch (step.getContentType()) {
            case APPLICATION_JSON -> executorBuilder.setBody(HttpHelper.parseRequestBody(step.getBody(), params));
            case MULTIPART_FORM_DATA -> {
                for (String key : params.keySet()) {
                    executorBuilder.addMultipartParameter(key, params.get(key));
                    //LOG.info("Multipart: " + key + " -> " + params.get(key).getValue() + " " + params.get(key).getType().toString());
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

        //LOG.info(String.format("Sent request to endpoint: %s", result.toString()));
        mqttSender.send(dataPoint);
        //LOG.info(String.format("Sent data to backend: %s", dataPoint.toString()));
        return (OutputArgument argument) -> result.getContent(argument.getParameterLocation());
    }
}
