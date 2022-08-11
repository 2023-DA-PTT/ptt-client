package com.ptt.control;

import com.ptt.boundary.RestService;
import com.ptt.entities.*;
import com.ptt.entities.dto.*;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PlanService {
    @Inject
    @RestClient
    RestService service;

    public PlanRun readPlanRun(long planRunId) {
        PlanRunDto planRunDto = service.getPlanRunById(planRunId);
        PlanDto planDto = service.getPlanById(planRunDto.getPlanId());
        Plan plan = new Plan(planDto.id, planDto.name, planDto.description);

        PlanRun planRun = new PlanRun();
        planRun.setId(planRunDto.getId());
        planRun.setPlan(plan);
        planRun.setStartTime(planRunDto.getStartTime());
        planRun.setDuration(planRunDto.getDuration());

        Map<Long, InputArgument> inputMap = new HashMap<>();
        Map<Long, OutputArgument> outputMap = new HashMap<>();

        List<HttpStepDto> httpStepDtoList = service.getHttpStepsByPlanId(plan.getId());
        for (HttpStepDto dto : httpStepDtoList) {
            HttpStep step = new HttpStep(dto.getId(), plan, dto.getName(), dto.getDescription(), dto.getMethod(), dto.getUrl(), dto.getBody(), dto.getResponseContentType(), dto.getContentType());
            plan.getSteps().add(step);
            if (step.getId() == planDto.startId) {
                plan.setStart(step);
            }
        }

        List<ScriptStepDto> scriptStepDtoList = service.getScriptStepsByPlanId(plan.getId());
        for (ScriptStepDto dto : scriptStepDtoList) {
            ScriptStep step = new ScriptStep(dto.getId(), plan, dto.getName(), dto.getDescription(), dto.getScript());
            plan.getSteps().add(step);
            if (step.getId() == planDto.startId) {
                plan.setStart(step);
            }
        }

        for (Step step : plan.getSteps()) {
            List<OutputArgumentDto> outArgsDtoList = service.getOutputArgumentsByStepId(plan.getId(), step.getId());
            for (OutputArgumentDto outArgDto : outArgsDtoList) {
                OutputArgument outArg = new OutputArgument(outArgDto.id, step, outArgDto.name, outArgDto.parameterLocation, outArgDto.outputType);
                outputMap.put(outArg.getId(), outArg);
                step.getOutputArguments().add(outArg);
            }
            List<InputArgumentDto> inArgsDtoList = service.getInputArgumentsByStepId(plan.getId(), step.getId());
            for (InputArgumentDto inArgDto : inArgsDtoList) {
                InputArgument inputArgument = new InputArgument(inArgDto.id, step, inArgDto.name);
                inputMap.put(inputArgument.getId(), inputArgument);
                step.getInputArguments().add(inputArgument);
            }
        }

        for (Step step : plan.getSteps()) {
            List<StepParameterRelationDto> relationDtoList =
                    service.getStepParameterRelationByStepIdFrom(plan.getId(), step.getId());
            Map<Long, NextStep> nextStepMap = new HashMap<>();
            for (StepParameterRelationDto stepParameterRelationDto : relationDtoList) {
                InputArgument inArg = inputMap.get(stepParameterRelationDto.fromId);
                OutputArgument outArg = outputMap.get(stepParameterRelationDto.toId);
                NextStep nextStep = nextStepMap.get(inArg.getStep().getId());
                if (nextStep == null) {
                    nextStep = new NextStep(inArg.getStep());
                    step.getNextSteps().add(nextStep);
                }
                StepParameterRelation rel = new StepParameterRelation(inArg, outArg);
                nextStep.getParams().add(rel);
                nextStepMap.putIfAbsent(inArg.getStep().getId(), nextStep);
            }
        }

        return planRun;
    }
}
