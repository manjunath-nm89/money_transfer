package com.org.moneytransfer.client;

import com.org.moneytransfer.service.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountTransaction extends Transaction {
    private Long accountTransactionId;
    private TransactionType transactionType;
}
