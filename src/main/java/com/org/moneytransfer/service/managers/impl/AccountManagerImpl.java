package com.org.moneytransfer.service.managers.impl;

import com.org.moneytransfer.client.Account;
import com.org.moneytransfer.client.Transaction;
import com.org.moneytransfer.service.converters.AccountConversions;
import com.org.moneytransfer.service.dao.AccountDao;
import com.org.moneytransfer.service.dao.UserDao;
import com.org.moneytransfer.service.datastore.AccountStore;
import com.org.moneytransfer.service.enums.AccountType;
import com.org.moneytransfer.service.managers.AccountManager;
import com.org.moneytransfer.service.util.ServiceUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<Account> deposit(Transaction transaction) {

        validateTransactionData(transaction);

        LinkedList<AccountStore> accountStores = accountDao.executeTransaction(transaction);
        return checkAndProcessAccounts(accountStores);
    }

    @Override
    public List<Account> transferMoney(Transaction transaction) {
        validateTransactionData(transaction);
        LinkedList<AccountStore> accountStores = accountDao.executeTransaction(transaction);
        return checkAndProcessAccounts(accountStores);
    }

    @Override
    public Account getAccountById(Long accountId) {
        AccountStore accountStore = accountDao.findById(accountId);

        if(accountStore != null) {
            return AccountConversions.convertAccount(accountStore);
        }
        else {
            return null;
        }
    }

    private boolean accountExists(Long accountOwnerId, AccountType accountType) {
        return accountDao.findByUserAndType(accountOwnerId, accountType) != null;
    }

    private boolean userExists(Long accountOwnerId) {
        return userDao.findById(accountOwnerId) != null;
    }

    private void validateTransactionData(Transaction transaction) {
        Long toAccountId = transaction.getToAccountId();
        Long originAccountId = transaction.getOriginAccountId();
        Long initiatorId = transaction.getInitiatorId();

        if(toAccountId != null) {
            validateAccount(toAccountId);
        }

        if(originAccountId != null) {
            validateAccount(originAccountId);
        }

        if(!userExists(initiatorId)) {
            throw new WebApplicationException(
                    ServiceUtils.buildErrorResponse(
                            Response.Status.NOT_FOUND, String.format("User with %d not found", initiatorId)
                    )
            );
        }
    }

    private void validateAccount(Long accountId) {
        Account toAccount = getAccountById(accountId);

        if(toAccount == null) {
            throw new WebApplicationException(
                    ServiceUtils.buildErrorResponse(
                            Response.Status.NOT_FOUND, String.format("Account with %d not found", accountId)
                    )
            );
        }
    }

    private List<Account> checkAndProcessAccounts(LinkedList<AccountStore> accountStores) {
        if(accountStores != null && !accountStores.isEmpty()) {
            return accountStores.stream().map(AccountConversions::convertAccount).collect(Collectors.toList());
        }
        else {
            // LOG this data to another table and have alerts on it.
            throw new WebApplicationException(
                    ServiceUtils.buildErrorResponse(
                            Response.Status.INTERNAL_SERVER_ERROR, "Something went wrong - Transaction Failed!!"
                    )
            );
        }
    }
}
