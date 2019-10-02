package com.org.moneytransfer.service.datastore;

import com.org.moneytransfer.service.enums.AccountType;
import com.org.moneytransfer.service.enums.CurrencyCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountStore extends BaseDataStore {
    private String name;
    private AccountType type;
    private Long userId;
    // Keeping things simple by having a CurrencyCode enum instead of java.util.Currency
    private CurrencyCode currencyCode;
    // Using BigDecimal instead of a Double here.
    // https://stackoverflow.com/questions/3413448/double-vs-bigdecimal
    // http://download.oracle.com/javase/1.5.0/docs/api/java/math/BigDecimal.html
    private BigDecimal balance;
}
