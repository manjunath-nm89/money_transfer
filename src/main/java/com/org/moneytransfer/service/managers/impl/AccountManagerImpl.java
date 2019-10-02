package com.org.moneytransfer.service.managers.impl;

import com.org.moneytransfer.client.Account;
import com.org.moneytransfer.service.converters.AccountConversions;
import com.org.moneytransfer.service.dao.AccountDao;
import com.org.moneytransfer.service.dao.UserDao;
import com.org.moneytransfer.service.datastore.AccountStore;
import com.org.moneytransfer.service.enums.AccountType;
import com.org.moneytransfer.service.managers.AccountManager;
import com.org.moneytransfer.service.util.ServiceUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class AccountManagerImpl implements AccountManager {

    private AccountDao accountDao;
    private UserDao userDao;

    public AccountManagerImpl(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @Override
    public Account createAccount(Account account) {
        Long accountOwnerId = account.getUserId();
        AccountType accountType = account.getType();

        if(!userExists(accountOwnerId)) {
            throw new WebApplicationException(
                    ServiceUtils.buildErrorResponse(
                            Response.Status.NOT_FOUND, String.format("User with %d not found", accountOwnerId)
                    )
            );
        }

        if(accountExists(accountOwnerId, accountType)) {
            throw new WebApplicationException(
                    ServiceUtils.buildErrorResponse(
                            Response.Status.FORBIDDEN, String.format("%s Account for User Id %d already exists", accountType, accountOwnerId)
                    )
            );
        }

        AccountStore accountStore = accountDao.createAccount(account);
        return AccountConversions.convertAccount(accountStore);

    }

    private boolean accountExists(Long accountOwnerId, AccountType accountType) {
        return accountDao.findByUserAndType(accountOwnerId, accountType) != null;
    }

    private boolean userExists(Long accountOwnerId) {
        return userDao.findById(accountOwnerId) != null;
    }
}
