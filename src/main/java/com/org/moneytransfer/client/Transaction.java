package com.org.moneytransfer.client;

import com.org.moneytransfer.service.enums.CurrencyCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Transaction {
    private Long initiatorId;
    private Long originAccountId;
    private Long toAccountId;
    private CurrencyCode currencyCode;
    private BigDecimal amount;
    private Long createdAt;
    private Long updatedAt;
}
