package com.moneyapp.model;

import java.math.BigDecimal;
import java.util.UUID;


public class Account {
    private String id;

    private String userName;

    private BigDecimal balance;

    private String currencyCode;

    public Account() {
    }

    public Account(String userName, BigDecimal balance, String currencyCode) {
        this.id = UUID.randomUUID().toString();
        this.userName = userName;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }

    public Account(String id, String userName, BigDecimal balance, String currencyCode) {
        this.id = id;
        this.userName = userName;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setBalance(BigDecimal balance) {//TODO remove setBalance (no public access)
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            //TODO throw exception
            //throw new CustomException("Not sufficient Fund for account: " + accountId);
        }
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (id != account.id) return false;
        if (!userName.equals(account.userName)) return false;
        if (!balance.equals(account.balance)) return false;
        return currencyCode.equals(account.currencyCode);

    }

    @Override
    public String toString() {
        return "com.moneyapp.model.Account{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", balance=" + balance +
                ", currencyCode='" + currencyCode + '\'' +
                '}';
    }
}
