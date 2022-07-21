package com.ptt;

import com.ptt.entities.dto.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;

@Path("/api/plan/{planId}")
@RegisterRestClient(baseUri = "http://localhost:8080")
public interface Service {
    @GET
    PlanDto getPlanById(@PathParam("planId") long planId);

    @GET
    @Path("step")
    List<StepDto> getStepsByPlanId(@PathParam("planId") long planId);

    @GET
    @Path("step/{stepId}/outputArgument")
    List<OutputArgumentDto> getOutputArgumentsByStepId(@PathParam("planId") long planId, @PathParam("stepId") long id);

    @GET
    @Path("step/{stepId}/inputArgument")
    List<InputArgumentDto> getInputArgumentsByStepId(@PathParam("planId") long planId, @PathParam("stepId") long id);

    @GET
    @Path("step/{stepId}/parameterRelation/from")
    List<StepParameterRelationDto> getStepParameterRelationByStepIdFrom(@PathParam("planId") long planId, @PathParam("stepId") long id);

}
