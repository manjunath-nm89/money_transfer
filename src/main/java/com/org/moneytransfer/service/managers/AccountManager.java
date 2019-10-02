package com.org.moneytransfer.service.managers;

import com.org.moneytransfer.client.Account;
import com.org.moneytransfer.client.AccountTransaction;
import com.org.moneytransfer.client.Transaction;

import java.util.List;

public interface AccountManager {
    Account createAccount(Account account);

    Account getAccountById(Long accountId);

    List<Account> deposit(Transaction transaction);

    List<Account> transferMoney(Transaction transaction);

    List<AccountTransaction> getTransactions(Long accountId);
}
