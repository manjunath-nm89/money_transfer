package com.org.moneytransfer.service.dao;

import com.org.moneytransfer.client.Transaction;
import com.org.moneytransfer.service.datastore.AccountTransactionStore;
import com.org.moneytransfer.service.enums.CurrencyCode;
import com.org.moneytransfer.service.enums.TransactionType;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<AccountTransactionStore> getTransactions(Long accountId, TransactionType transactionType) {

        Collection<AccountTransactionStore> accountTransactionStores = getDataTable().values();

        return accountTransactionStores.stream().filter(accountTransactionStore -> {
            return filterByTransactionType(accountTransactionStore, transactionType, accountId);
        }).collect(Collectors.toList());

    }

    private boolean filterByTransactionType(AccountTransactionStore accountTransactionStore,
                                            TransactionType transactionType,
                                            Long accountId) {

        if(transactionType.equals(TransactionType.CREDIT)) {
            return accountTransactionStore.getToAccountId() != null && accountTransactionStore.getToAccountId().equals(accountId);
        }
        else {
            return accountTransactionStore.getOriginAccountId() != null && accountTransactionStore.getOriginAccountId().equals(accountId);
        }

    }
}
