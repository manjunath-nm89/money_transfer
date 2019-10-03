package com.org.moneytransfer.resources;

import com.codahale.metrics.annotation.Timed;
import com.org.moneytransfer.client.User;
import com.org.moneytransfer.service.managers.UserManager;
import com.org.moneytransfer.service.util.ServiceUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "UserResource", description = "Endpoints to create and manage users")

public class UserResource {

    private UserManager userManager;

    public UserResource(UserManager userManager) {
        this.userManager = userManager;
    }

    @GET
    @Path("{userId}")
    @Timed
    @ApiOperation("Searches for the user by id and returns the object")
    public User getUser(@PathParam("userId") Long userId) {

        User user = userManager.getUserById(userId);

        if(user == null) {
            throw new WebApplicationException(
                ServiceUtils.buildErrorResponse(
                    Response.Status.NOT_FOUND, String.format("User with %d not found", userId)
                )
            );
        }
        else {
            return user;
        }

    }

    @POST
    @Timed
    @ApiOperation("Creates the User")
    public User createUser(User user) {
        return userManager.createUser(user);
    }




}
