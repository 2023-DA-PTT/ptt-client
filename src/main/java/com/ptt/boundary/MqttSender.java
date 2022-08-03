package com.ptt.boundary;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ptt.entities.dto.DataPoint;

@ApplicationScoped
public class MqttSender {
    
    @Inject
    @Channel("measurements")
    Emitter<DataPoint> emitter;

    public void send(DataPoint dataPoint) throws JsonProcessingException {
        //ObjectMapper objectMapper = new ObjectMapper();
        //return objectMapper.writeValueAsString(dataPoint);
        emitter.send(dataPoint);
    }
}
