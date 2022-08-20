package com.ptt.entities.dto;

import com.ptt.entities.HttpStep;
import com.ptt.entities.HttpStepHeader;

public class HttpStepHeaderDto {
    public Long id;
    public String name;
    public String value;

    public HttpStepHeaderDto(Long id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public static HttpStepHeaderDto from(HttpStepHeader header) {
        return new HttpStepHeaderDto(header.getId(), header.getName(), header.getValue());
    }

    public static HttpStepHeader to(HttpStepHeaderDto dto, HttpStep step) {
        return new HttpStepHeader(dto.id, dto.name, dto.value, step);
    }
}
