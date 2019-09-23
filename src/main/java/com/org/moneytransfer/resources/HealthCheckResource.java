package com.org.moneytransfer.resources;

import com.org.moneytransfer.client.HealthCheck;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/apihealthcheck")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "HealthcheckResource")
public class HealthCheckResource {

    @GET
    @ApiOperation("Check if the Service is running. Called by healthcheck.")
    public HealthCheck get() {
        HealthCheck healthcheck = new HealthCheck();

        healthcheck.setHealthy(true);

        return healthcheck;
    }

}

