package com.org.moneytransfer.service.converters;

import com.org.moneytransfer.client.Account;
import com.org.moneytransfer.service.datastore.AccountStore;

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
}
