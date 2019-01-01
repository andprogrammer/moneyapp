package com.moneyapp.utils;

import com.moneyapp.exception.CustomException;
import com.moneyapp.model.Transaction;

import java.math.BigDecimal;
import java.util.HashSet;


public class Utils {

    public static final HashSet<String> CURRENCY_CODES = new HashSet<>();

    public static void validateId(String id) throws CustomException {
        if (id == null || id.isEmpty())
            throw new CustomException("Argument 'id' cannot be empty");
    }

    public static void validateBalanceLessThanOrEqualZero(BigDecimal balance) throws CustomException {
        if (null == balance || balance.compareTo(BigDecimal.ZERO) <= 0)
            throw new CustomException("Parameter 'balance' less than or equal zero");
    }

    public static void validateBalanceLessThanZero(BigDecimal balance) throws CustomException {
        if (null == balance || balance.compareTo(BigDecimal.ZERO) < 0)
            throw new CustomException("Parameter 'balance' less than zero");
    }

    public static void validateTransaction(Transaction transaction) throws CustomException {
        if (null == transaction || transaction.getFromAccountId().isEmpty() || transaction.getToAccountId().isEmpty()
                || null == transaction.getAmount() || transaction.getCurrencyCode().isEmpty())
            throw new CustomException("Incorrect transaction");
        validateCurrencyCode(transaction.getCurrencyCode());
    }

    public static void validateCurrencyCode(String currencyCode) throws CustomException {
        setCurrencyCodes();
        if (!CURRENCY_CODES.contains(currencyCode))
            throw new CustomException("Incorrect currency code");
    }

    private static void setCurrencyCodes() {
        CURRENCY_CODES.add("USD");
        CURRENCY_CODES.add("PLN");
        CURRENCY_CODES.add("GBP");
    }

    public static void checkCurrencyCodes(String currencyCodeX, String currencyCodeY) throws CustomException {
        if (!currencyCodeX.equals(currencyCodeY))
            throw new CustomException("Different currency codes");
    }
}
