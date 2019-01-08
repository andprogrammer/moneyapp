package com.moneyapp.model;

import com.moneyapp.exception.CustomException;

import java.math.BigDecimal;
import java.util.UUID;

import static com.moneyapp.utils.Utils.validateAmountLessThanZero;

public class Account {

    private String id;
    private String userName;
    private BigDecimal balance;
    private String currencyCode;

    public Account(String userName, BigDecimal balance, String currencyCode) {
        this.id = UUID.randomUUID().toString();
        this.userName = userName;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) throws CustomException {
        validateAmountLessThanZero(balance);
        this.balance = balance;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Account account = (Account) o;
//        if (!id.equals(account.id))
//            return false;
        if (!userName.equals(account.userName))
            return false;
        if (!balance.equals(account.balance))
            return false;
        return currencyCode.equals(account.currencyCode);
    }

    @Override
    public String toString() {
        return "UserName=" + userName + " Balance=" + balance + " CurrencyCode=" + currencyCode;
    }
}
