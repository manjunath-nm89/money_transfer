package com.org.moneytransfer.resources;


import com.codahale.metrics.annotation.Timed;
import com.org.moneytransfer.client.Account;
import com.org.moneytransfer.service.managers.AccountManager;
import com.org.moneytransfer.service.util.ServiceUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "AccountResource", description = "Endpoints to create accounts")
public class AccountResource {

    private AccountManager accountManager;

    public AccountResource(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @POST
    @Timed
    @ApiOperation("Create account for a user")
    public Account createAccount(Account account) {
        if(account.getUserId() != null && account.getType() != null) {
            return accountManager.createAccount(account);
        }
        else {
            throw new WebApplicationException(
                    ServiceUtils.buildErrorResponse(
                            Response.Status.BAD_REQUEST, "Account is missing the account owner or the account type"
                    )
            );
        }
    }

}
