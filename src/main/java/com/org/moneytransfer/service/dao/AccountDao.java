package com.org.moneytransfer.service.dao;

import com.org.moneytransfer.client.Account;
import com.org.moneytransfer.service.datastore.AccountStore;
import com.org.moneytransfer.service.enums.AccountType;
import com.org.moneytransfer.service.enums.CurrencyCode;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;

public class AccountDao extends BaseDao<AccountStore> {

    private LinkedHashMap<Long, AccountStore> dataTable;

    public AccountDao() {
        dataTable = new LinkedHashMap<>();
    }

    @Override
    LinkedHashMap<Long, AccountStore> getDataTable() {
        return dataTable;
    }

    public AccountStore createAccount(Account account) {

        AccountStore accountStore = new AccountStore();

        accountStore.setName(account.getName());
        accountStore.setType(account.getType());
        accountStore.setUserId(account.getUserId());

        // Set Defaults for the accounts
        // Supporting only GBP for now.
        accountStore.setCurrencyCode(CurrencyCode.GBP);
        // Balance will be a cache of the current money in account i.e, rollup of the transactions in accountTransactions
        accountStore.setBalance(BigDecimal.ZERO);

        return createRecord(accountStore);
    }

    public AccountStore findByUserAndType(Long accountOwnerId, AccountType accountType) {
        Collection<AccountStore> accountStores = getDataTable().values();

        Optional<AccountStore> accountStore = accountStores.stream().filter(store -> {
            return store.getType().equals(accountType) && store.getUserId().equals(accountOwnerId);
        }).findFirst();

        return accountStore.isPresent() ? accountStore.get() : null;
    }
}
