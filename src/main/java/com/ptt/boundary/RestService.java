package com.ptt.boundary;

import com.ptt.entities.dto.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@RegisterRestClient
public interface RestService {
    @GET
    @Path("plan/{planId}")
    PlanDto getPlanById(@PathParam("planId") long planId);

    @GET
    @Path("plan/{planId}/step")
    List<StepDto> getStepsByPlanId(@PathParam("planId") long planId);

    @GET
    @Path("plan/{planId}/step/{stepId}/nexts")
    List<StepDto> getNextStepsByStepId(@PathParam("planId") long planId,@PathParam("stepId") long stepId);

    @GET
    @Path("plan/{planId}/step/script")
    List<ScriptStepDto> getScriptStepsByPlanId(@PathParam("planId") long planId);

    @GET
    @Path("plan/{planId}/step/http")
    List<HttpStepDto> getHttpStepsByPlanId(@PathParam("planId") long planId);

    @GET
    @Path("plan/{planId}/step/{stepId}/outputArgument")
    List<OutputArgumentDto> getOutputArgumentsByStepId(@PathParam("planId") long planId, @PathParam("stepId") long id);

    @GET
    @Path("plan/{planId}/step/{stepId}/inputArgument")
    List<InputArgumentDto> getInputArgumentsByStepId(@PathParam("planId") long planId, @PathParam("stepId") long id);

    @GET
    @Path("plan/{planId}/step/{stepId}/parameterRelation/from")
    List<StepParameterRelationDto> getStepParameterRelationByStepIdFrom(@PathParam("planId") long planId, @PathParam("stepId") long id);

    @GET
    @Path("planrun/{planrunid}")
    PlanRunDto getPlanRunById(@PathParam("planrunid") long planRunId);
}
