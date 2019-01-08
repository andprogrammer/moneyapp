package com.moneyapp.model;

import java.math.BigDecimal;

public class Transaction {

    private String fromAccountId;
    private String toAccountId;
    private BigDecimal amount;
    private String currencyCode;

    public Transaction(String fromAccountId, String toAccountId, BigDecimal amount, String currencyCode) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.currencyCode = currencyCode;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public String getToAccountId() {
        return toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Transaction account = (Transaction) o;
        if (!fromAccountId.equals(account.fromAccountId))
            return false;
        if (!toAccountId.equals(account.toAccountId))
            return false;
        if (!amount.equals(account.amount))
            return false;
        return currencyCode.equals(account.currencyCode);
    }

    @Override
    public String toString() {
        return "FromAccountId=" + fromAccountId
                + " ToAccountId=" + toAccountId
                + " Amount=" + amount
                + " CurrencyCode=" + currencyCode;
    }
}
