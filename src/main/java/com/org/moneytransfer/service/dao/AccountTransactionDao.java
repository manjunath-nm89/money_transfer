package com.org.moneytransfer.service.dao;

import com.org.moneytransfer.client.Transaction;
import com.org.moneytransfer.service.datastore.AccountTransactionStore;
import com.org.moneytransfer.service.enums.CurrencyCode;

import java.util.LinkedHashMap;

public class AccountTransactionDao extends BaseDao<AccountTransactionStore> {

    private LinkedHashMap<Long, AccountTransactionStore> dataTable;

    public AccountTransactionDao() {
        dataTable = new LinkedHashMap<>();
    }

    @Override
    LinkedHashMap<Long, AccountTransactionStore>  getDataTable() {
        return dataTable;
    }

    public AccountTransactionStore createLedgerRecord(Transaction transaction) {

        AccountTransactionStore accountTransactionStore = new AccountTransactionStore();

        accountTransactionStore.setInitiatorId(transaction.getInitiatorId());
        accountTransactionStore.setOriginAccountId(transaction.getOriginAccountId());
        accountTransactionStore.setToAccountId(transaction.getToAccountId());
        accountTransactionStore.setAmount(transaction.getAmount());

        // Only GBP supported for now.
        accountTransactionStore.setCurrencyCode(CurrencyCode.GBP);

        return createRecord(accountTransactionStore);
    }
}
