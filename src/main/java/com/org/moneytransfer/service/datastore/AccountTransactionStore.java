package com.org.moneytransfer.service.datastore;

import com.org.moneytransfer.service.enums.CurrencyCode;
import com.org.moneytransfer.service.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountTransactionStore extends BaseDataStore {
    private Long initiatorId;
    private Long originAccountId;
    private Long toAccountId;
    private CurrencyCode currencyCode;
    private BigDecimal amount;
}
