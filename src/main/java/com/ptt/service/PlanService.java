package com.ptt.service;

import com.ptt.Service;
import com.ptt.entities.*;
import com.ptt.entities.dto.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PlanService {
    @Inject
    Service service;

    public Plan readPlan(long planId) {
        PlanDto planDto = service.getPlanById(planId);
        Plan plan = getPlan(planDto);
        Map<Long, InputArgument> inputMap = new HashMap<>();
        Map<Long, OutputArgument> outputMap = new HashMap<>();

        List<StepDto> stepDtos = service.getStepsByPlanId(planId);
        for (StepDto dto : stepDtos) {
            Step step = getStep(dto);
            step.plan = plan;
            plan.steps.add(step);
        }
        for (Step step : plan.steps) {
            List<OutputArgumentDto> outArgsDtoList = service.getOutputArgumentsByStepId(plan.id, step.id);
            for (OutputArgumentDto outArgDto : outArgsDtoList) {
                OutputArgument outArg = getOutputArgument(outArgDto);
                outArg.step = step;
                outputMap.put(outArg.id, outArg);
                step.outputArguments.add(outArg);
            }
            List<InputArgumentDto> inArgsDtoList = service.getInputArgumentsByStepId(plan.id, step.id);
            for (InputArgumentDto inArgDto : inArgsDtoList) {
                InputArgument inputArgument = getInputArgument(inArgDto);
                inputArgument.step = step;
                inputMap.put(inputArgument.id, inputArgument);
                step.inputArguments.add(inputArgument);
            }
        }

        for (Step step : plan.steps) {
            List<StepParameterRelationDto> relationDtoList =
                    service.getStepParameterRelationByStepIdFrom(plan.id, step.id);
            Map<Long, NextStep> nextStepMap = new HashMap<>();
            for (StepParameterRelationDto stepParameterRelationDto : relationDtoList) {
                InputArgument inArg = inputMap.get(stepParameterRelationDto.fromId);
                OutputArgument outArg = outputMap.get(stepParameterRelationDto.toId);
                NextStep nextStep = nextStepMap.get(inArg.step.id);
                if (nextStep == null) {
                    nextStep = new NextStep();
                }
                nextStep.next = inArg.step;
                StepParameterRelation rel = new StepParameterRelation();
                rel.from = outArg;
                rel.to = inArg;
                nextStep.params.add(rel);
                nextStepMap.putIfAbsent(inArg.step.id, nextStep);
            }
        }

        return plan;
    }

    private InputArgument getInputArgument(InputArgumentDto inArgDto) {
        InputArgument inputArgument = new InputArgument();
        inputArgument.id = inArgDto.id;
        inputArgument.name = inArgDto.name;
        return inputArgument;
    }

    private OutputArgument getOutputArgument(OutputArgumentDto outArgDto) {
        OutputArgument outArg = new OutputArgument();
        outArg.id = outArgDto.id;
        outArg.name = outArgDto.name;
        outArg.jsonLocation = outArgDto.jsonLocation;
        return outArg;
    }

    private Step getStep(StepDto dto) {
        Step step = new Step();
        step.id = dto.id;
        step.name = dto.name;
        step.description = dto.description;
        step.method = dto.method;
        step.url = dto.url;
        step.body = dto.body;
        step.outputArguments = new ArrayList<>();
        step.inputArguments = new ArrayList<>();
        step.nextSteps = new ArrayList<>();
        return step;
    }

    private Plan getPlan(PlanDto planDto) {
        Plan plan = new Plan();
        plan.id = planDto.id;
        plan.name = planDto.name;
        plan.description = planDto.description;
        plan.steps = new ArrayList<>();
        return plan;
    }
}
