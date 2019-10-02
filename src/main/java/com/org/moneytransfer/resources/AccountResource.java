package com.org.moneytransfer.resources;


import com.codahale.metrics.annotation.Timed;
import com.org.moneytransfer.client.Account;
import com.org.moneytransfer.client.AccountTransaction;
import com.org.moneytransfer.client.Transaction;
import com.org.moneytransfer.client.User;
import com.org.moneytransfer.service.enums.CurrencyCode;
import com.org.moneytransfer.service.enums.TransactionType;
import com.org.moneytransfer.service.managers.AccountManager;
import com.org.moneytransfer.service.util.ServiceUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "AccountResource", description = "Endpoints to create accounts")
public class AccountResource {

    private AccountManager accountManager;

    public AccountResource(AccountManager accountManager) {
        this.accountManager = accountManager;
    }


    @GET
    @Path("{accountId}")
    @Timed
    @ApiOperation("Searches for the account by id and returns the object")
    public Account getAccount(@PathParam("accountId") Long accountId) {

        Account account = accountManager.getAccountById(accountId);

        if(account == null) {
            throw new WebApplicationException(
                    ServiceUtils.buildErrorResponse(
                            Response.Status.NOT_FOUND, String.format("Account with %d not found", accountId)
                    )
            );
        }
        else {
            return account;
        }

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

    @GET
    @Timed
    @ApiOperation("Get the list of transactions on the account sorted by most recent first")
    @Path("/{accountId}/transactions")
    public List<AccountTransaction> getAccountTransactions(@PathParam("accountId") Long accountId) {
        return accountManager.getTransactions(accountId);
    }

    @POST
    @Timed
    @Path("/transferMoney")
    @ApiOperation("Deposit money to Account")
    public List<Account> transferMoney(Transaction transaction) {

        if(transaction.getInitiatorId() == null || transaction.getAmount() == null ||
                transaction.getAmount().equals(BigDecimal.ZERO) ||
                transaction.getToAccountId() == null || transaction.getOriginAccountId() == null) {
            throw new WebApplicationException(
                    ServiceUtils.buildErrorResponse(
                            Response.Status.BAD_REQUEST, "Missing Transaction Data - account details or initiatorId or amount"
                    )
            );
        }

        transaction.setCurrencyCode(CurrencyCode.GBP);

        return accountManager.transferMoney(transaction);

    }

    @POST
    @Timed
    @Path("/{accountId}/deposit")
    @ApiOperation("Deposit money to Account")
    public List<Account> deposit(@PathParam("accountId") Long accountId, Transaction transaction) {

        if(transaction.getInitiatorId() == null || transaction.getAmount() == null ||
                transaction.getAmount().equals(BigDecimal.ZERO)) {
            throw new WebApplicationException(
                    ServiceUtils.buildErrorResponse(
                            Response.Status.BAD_REQUEST, "Missing Transaction Data - initiatorId or amount"
                    )
            );
        }

        transaction.setOriginAccountId(null);
        transaction.setToAccountId(accountId);
        transaction.setCurrencyCode(CurrencyCode.GBP);

        return accountManager.deposit(transaction);

    }

}
