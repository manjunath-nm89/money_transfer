package com.org.moneytransfer.service.converters;

import com.org.moneytransfer.client.Account;
import com.org.moneytransfer.client.AccountTransaction;
import com.org.moneytransfer.service.datastore.AccountStore;
import com.org.moneytransfer.service.datastore.AccountTransactionStore;
import com.org.moneytransfer.service.enums.TransactionType;

public class AccountConversions {
    public static Account convertAccount(AccountStore accountStore) {

        Account account = new Account();

        account.setAccountId(accountStore.getId());
        account.setName(accountStore.getName());
        account.setType(accountStore.getType());
        account.setUserId(accountStore.getUserId());
        account.setCurrencyCode(accountStore.getCurrencyCode());
        account.setBalance(accountStore.getBalance());
        account.setCreatedAt(accountStore.getCreatedAt());
        account.setUpdatedAt(accountStore.getUpdatedAt());

        return account;
    }

    public static AccountTransaction convertTransaction(AccountTransactionStore accountTransactionStore,
                                                        TransactionType transactionType) {

        AccountTransaction accountTransaction = new AccountTransaction();

        accountTransaction.setAccountTransactionId(accountTransactionStore.getId());
        accountTransaction.setAmount(accountTransactionStore.getAmount());
        accountTransaction.setCurrencyCode(accountTransactionStore.getCurrencyCode());
        accountTransaction.setInitiatorId(accountTransactionStore.getInitiatorId());
        accountTransaction.setCreatedAt(accountTransactionStore.getCreatedAt());
        accountTransaction.setUpdatedAt(accountTransactionStore.getUpdatedAt());

        accountTransaction.setOriginAccountId(accountTransactionStore.getOriginAccountId());
        accountTransaction.setToAccountId(accountTransactionStore.getToAccountId());

        accountTransaction.setTransactionType(transactionType);

        return accountTransaction;

    }
}
