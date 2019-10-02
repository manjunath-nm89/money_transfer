package com.org.moneytransfer.service.managers.impl;

import com.org.moneytransfer.client.Account;
import com.org.moneytransfer.client.AccountTransaction;
import com.org.moneytransfer.client.Transaction;
import com.org.moneytransfer.service.converters.AccountConversions;
import com.org.moneytransfer.service.dao.AccountDao;
import com.org.moneytransfer.service.dao.UserDao;
import com.org.moneytransfer.service.datastore.AccountStore;
import com.org.moneytransfer.service.datastore.AccountTransactionStore;
import com.org.moneytransfer.service.enums.AccountType;
import com.org.moneytransfer.service.enums.TransactionType;
import com.org.moneytransfer.service.managers.AccountManager;
import com.org.moneytransfer.service.util.ServiceUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                            Response.Status.NOT_FOUND, String.format("User with Id %d not found", accountOwnerId)
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
    public List<AccountTransaction> getTransactions(Long accountId) {

        List<AccountTransactionStore> debitTransactionStores = accountDao.getDebitTransactions(accountId);
        List<AccountTransactionStore> creditTransactionStores = accountDao.getCreditTransactions(accountId);

        List<AccountTransaction> debitTransactions = convertTransactions(debitTransactionStores, TransactionType.DEBIT);
        List<AccountTransaction> creditTransactions = convertTransactions(creditTransactionStores, TransactionType.CREDIT);

        return sortAndMergeTransactions(debitTransactions, creditTransactions);
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

        if(originAccountId != null) {
            Account originAccount = getAccountById(originAccountId);
            validateAccount(originAccount, originAccountId);
            checkBalance(originAccount, transaction.getAmount());
        }

        if(toAccountId != null) {
            validateAccount(getAccountById(toAccountId), toAccountId);
        }

        if(!userExists(initiatorId)) {
            throw new WebApplicationException(
                    ServiceUtils.buildErrorResponse(
                            Response.Status.NOT_FOUND, String.format("User with Id %d not found", initiatorId)
                    )
            );
        }
    }

    private void validateAccount(Account account, Long accountId) {
        if(account == null) {
            throw new WebApplicationException(
                    ServiceUtils.buildErrorResponse(
                            Response.Status.NOT_FOUND, String.format("Account with Id %d not found", accountId)
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

    private void checkBalance(Account originAccount, BigDecimal amount) {
        BigDecimal diffAmount = originAccount.getBalance().subtract(amount);
        boolean hasInSufficientBalance = (diffAmount.compareTo(BigDecimal.ZERO) < 0);

        if(hasInSufficientBalance) {
            throw new WebApplicationException(
                    ServiceUtils.buildErrorResponse(
                            Response.Status.BAD_REQUEST, "The Origin Account has insufficient balance"
                    )
            );
        }
    }

    private List<AccountTransaction> convertTransactions(List<AccountTransactionStore> transactionStores,
                                                         TransactionType transactionType) {

        return transactionStores.stream().map(accountTransactionStore -> {
            return AccountConversions.convertTransaction(accountTransactionStore, transactionType);
        }).collect(Collectors.toList());
    }

    private List<AccountTransaction> sortAndMergeTransactions(List<AccountTransaction> debitTransactions,
                                                              List<AccountTransaction> creditTransactions) {

        return Stream.concat(debitTransactions.stream(), creditTransactions.stream()).sorted(
            (aT1, aT2) -> aT2.getUpdatedAt().compareTo(aT1.getUpdatedAt())
        ).collect(Collectors.toList());

    }
}


