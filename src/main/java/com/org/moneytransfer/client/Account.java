package com.org.moneytransfer.client;

import com.org.moneytransfer.service.enums.AccountType;
import com.org.moneytransfer.service.enums.CurrencyCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Account {
    private Long accountId;
    private String name;
    private AccountType type;
    private BigDecimal balance;
    // Owner of the account
    private Long userId;
    private Long createdAt;
    private Long updatedAt;
}
