package com.ptt.boundary;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Outgoing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptt.entities.dto.DataPoint;

@ApplicationScoped
public class MqttSender {
    
    @Outgoing("measurements2")
    public String send(DataPoint dataPoint) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(dataPoint);
    }
}
