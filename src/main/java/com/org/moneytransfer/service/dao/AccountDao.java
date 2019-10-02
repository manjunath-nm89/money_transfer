package com.org.moneytransfer.service.dao;

import com.org.moneytransfer.client.Account;
import com.org.moneytransfer.client.Transaction;
import com.org.moneytransfer.service.datastore.AccountStore;
import com.org.moneytransfer.service.datastore.AccountTransactionStore;
import com.org.moneytransfer.service.enums.AccountType;
import com.org.moneytransfer.service.enums.CurrencyCode;
import com.org.moneytransfer.service.enums.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class AccountDao extends BaseDao<AccountStore> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDao.class);

    private LinkedHashMap<Long, AccountStore> dataTable;
    private AccountTransactionDao accountTransactionDao;

    public AccountDao(AccountTransactionDao accountTransactionDao) {
        dataTable = new LinkedHashMap<>();
        this.accountTransactionDao = accountTransactionDao;
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

    public LinkedList<AccountStore> executeTransaction(Transaction transaction) {
        AccountStore originAccount = getAccount(transaction.getOriginAccountId());
        AccountStore toAccount = getAccount(transaction.getToAccountId());

        LinkedList<AccountStore> accountStores = new LinkedList<>();

        if(accountTransactionDao.createLedgerRecord(transaction) != null) {
            // Success

            // originAccount will be null for direct deposits
            if(originAccount != null) {
                accountStores.add(withdrawAmount(originAccount, transaction.getAmount()));
            }

            accountStores.add(depositAmount(toAccount, transaction.getAmount()));
        }
        else {
            // Fail
            return null;
        }

        return accountStores;

    }

    public List<AccountTransactionStore> getDebitTransactions(Long accountId) {
        return accountTransactionDao.getTransactions(accountId, TransactionType.DEBIT);
    }

    public List<AccountTransactionStore> getCreditTransactions(Long accountId) {
        return accountTransactionDao.getTransactions(accountId, TransactionType.CREDIT);
    }

    private AccountStore withdrawAmount(AccountStore originAccount, BigDecimal withdrawalAmount) {
        BigDecimal currentBalance = originAccount.getBalance();
        BigDecimal newBalance = currentBalance.subtract(withdrawalAmount);

        return updateBalance(originAccount, newBalance);
    }

    private AccountStore depositAmount(AccountStore toAccount, BigDecimal depositAmount) {
        BigDecimal currentBalance = toAccount.getBalance();

        BigDecimal newBalance = currentBalance.add(depositAmount);

        return updateBalance(toAccount, newBalance);
    }

    private AccountStore updateBalance(AccountStore originAccount, BigDecimal newBalance) {
        originAccount.setBalance(newBalance);
        return updateRecord(originAccount);
    }

    private AccountStore getAccount(Long accountId) {
        return accountId != null ? findById(accountId) : null;
    }
}
