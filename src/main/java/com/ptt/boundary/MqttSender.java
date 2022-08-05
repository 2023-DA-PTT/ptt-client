package com.ptt.boundary;

import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ptt.entities.dto.DataPointClientDto;

@ApplicationScoped
public class MqttSender {

    @Inject
    @Channel("measurements")
    Emitter<DataPointClientDto> emitter;

    public void send(DataPointClientDto dataPoint) throws JsonProcessingException {
        //ObjectMapper objectMapper = new ObjectMapper();
        //return objectMapper.writeValueAsString(dataPoint);
        CompletionStage<Void> cs =  emitter.send(dataPoint);
        cs.toCompletableFuture().join();
    }
}
