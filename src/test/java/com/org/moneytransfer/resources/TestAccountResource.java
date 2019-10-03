package com.org.moneytransfer.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.moneytransfer.client.Account;
import com.org.moneytransfer.client.AccountTransaction;
import com.org.moneytransfer.client.Transaction;
import com.org.moneytransfer.client.User;
import com.org.moneytransfer.service.dao.AccountDao;
import com.org.moneytransfer.service.dao.AccountTransactionDao;
import com.org.moneytransfer.service.dao.UserDao;
import com.org.moneytransfer.service.datastore.AccountStore;
import com.org.moneytransfer.service.datastore.AccountTransactionStore;
import com.org.moneytransfer.service.datastore.UserStore;
import com.org.moneytransfer.service.enums.AccountType;
import com.org.moneytransfer.service.enums.TransactionType;
import com.org.moneytransfer.service.managers.AccountManager;
import com.org.moneytransfer.service.managers.impl.AccountManagerImpl;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

public class TestAccountResource extends BaseTestResource {

    private static final UserDao userDao = new UserDao();
    private static final AccountTransactionDao accountTransactionDao = new AccountTransactionDao();
    private static final AccountDao accountDao = new AccountDao(accountTransactionDao);
    private static final AccountManager accountManager = new AccountManagerImpl(accountDao, userDao);

    private ObjectMapper mapper;

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AccountResource(accountManager))
            .build();

    @Before
    public void setup() {
        // Setup Methods
        mapper = new ObjectMapper();
    }

    @Test
    public void getAccount404() {

        WebTarget target = resources.target("/accounts/123");

        Response response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .method("GET");

        assert response.getStatus() == 404;

    }

    @Test
    public void transferMoney() throws IOException {
        UserStore userStore1 = userDao.createUser(buildUser("lyd", "niv", "lyd.niv@gmail.com"));
        UserStore userStore2 = userDao.createUser(buildUser("vin", "win", "vin.win@gmail.com"));

        AccountStore accountStore1 = accountDao.createAccount(buildAccount(userStore1.getId()));
        AccountStore accountStore2 = accountDao.createAccount(buildAccount(userStore2.getId()));


        WebTarget target = resources.target("/accounts/transferMoney");


        Transaction transactionPayload = buildMoneyTransferPayload(
                       userStore1.getId(),
                       accountStore1.getId(),
                       accountStore2.getId(),
                       BigDecimal.valueOf(200)
                );

        Response response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .method("POST", Entity.json(transactionPayload));

        assert response.getStatus() == 400;

        String responseStr = getResponseFromBuffer((InputStream) response.getEntity());

        assert responseStr.contains("The Origin Account has insufficient balance");

        // Deposit Money
        accountManager.deposit(buildMoneyTransferPayload(
                userStore1.getId(),
                null,
                accountStore1.getId(),
                BigDecimal.valueOf(1000)
        ));

        // Redo Transfer of 200
        response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .method("POST", Entity.json(transactionPayload));

        assert response.getStatus() == 200;

        Account account1 = accountManager.getAccountById(accountStore1.getId());
        Account account2 = accountManager.getAccountById(accountStore2.getId());

        assert account1.getBalance().equals(BigDecimal.valueOf(800));
        assert account2.getBalance().equals(BigDecimal.valueOf(200));

        // Transfer of 50
        transactionPayload.setAmount(BigDecimal.valueOf(50));
        response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .method("POST", Entity.json(transactionPayload));

        assert response.getStatus() == 200;

        account1 = accountManager.getAccountById(accountStore1.getId());
        account2 = accountManager.getAccountById(accountStore2.getId());

        assert account1.getBalance().equals(BigDecimal.valueOf(750));
        assert account2.getBalance().equals(BigDecimal.valueOf(250));

        // Transfer of 100 from the opposite accounts
        transactionPayload.setAmount(BigDecimal.valueOf(100));
        transactionPayload.setOriginAccountId(account2.getAccountId());
        transactionPayload.setToAccountId(account1.getAccountId());
        response = target.request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .method("POST", Entity.json(transactionPayload));

        assert response.getStatus() == 200;

        account1 = accountManager.getAccountById(accountStore1.getId());
        account2 = accountManager.getAccountById(accountStore2.getId());

        assert account1.getBalance().equals(BigDecimal.valueOf(850));
        assert account2.getBalance().equals(BigDecimal.valueOf(150));

        // Check Transaction Ledger

        List<AccountTransaction> transactions1 = accountManager.getTransactions(accountStore1.getId());
        List<AccountTransaction> transactions2 = accountManager.getTransactions(accountStore2.getId());


        AccountTransaction firstAccountTransaction1 = transactions1.get(0);
        AccountTransaction firstAccountTransaction2 = transactions2.get(0);

        assert firstAccountTransaction1.getTransactionType().equals(TransactionType.CREDIT);
        assert firstAccountTransaction2.getTransactionType().equals(TransactionType.DEBIT);
        assert firstAccountTransaction1.getAmount().equals(BigDecimal.valueOf(100));
        assert firstAccountTransaction2.getAmount().equals(BigDecimal.valueOf(100));

        AccountTransaction secondAccountTransaction1 = transactions1.get(1);
        AccountTransaction secondAccountTransaction2 = transactions2.get(1);

        assert secondAccountTransaction1.getTransactionType().equals(TransactionType.DEBIT);
        assert secondAccountTransaction2.getTransactionType().equals(TransactionType.CREDIT);
        assert secondAccountTransaction1.getAmount().equals(BigDecimal.valueOf(50));
        assert secondAccountTransaction2.getAmount().equals(BigDecimal.valueOf(50));
    }

    private Transaction buildMoneyTransferPayload(Long initiatorId, Long originAccountId, Long toAccountId, BigDecimal amount) {
        Transaction transaction = new Transaction();

        transaction.setInitiatorId(initiatorId);
        transaction.setOriginAccountId(originAccountId);
        transaction.setToAccountId(toAccountId);
        transaction.setAmount(amount);

        return transaction;
    }

    private Account buildAccount(Long userid) {
        Account account = new Account();

        account.setUserId(userid);
        account.setName("Personal Account");
        account.setType(AccountType.CHECKING);

        return account;
    }

    private User buildUser(String firstName, String lastName, String email) {
        User user = new User();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        return user;
    }
}
