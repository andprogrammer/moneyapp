package com.moneyapp.utils;

import com.moneyapp.exception.CustomException;
import com.moneyapp.model.Transaction;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashSet;

public class Utils {

    private static final HashSet<String> CURRENCY_CODES = new HashSet<>();
    private final static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName().getClass());

    public static void validateId(String id) throws CustomException {
        if (id == null || id.isEmpty()) {
            String errorMessage = "Incorrect id=" + id;
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() " + errorMessage);
            throw new CustomException(errorMessage);
        }
    }

    public static void validateAmountLessThanOrEqualZero(BigDecimal amount) throws CustomException {
        if (null == amount || amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() Incorrect amount=" + amount);
            throw new CustomException("Parameter 'amount' less than or equal zero");
        }
    }

    public static void validateAmountLessThanZero(BigDecimal amount) throws CustomException {
        if (null == amount || amount.compareTo(BigDecimal.ZERO) < 0) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() Incorrect amount=" + amount);
            throw new CustomException("Parameter 'amount' less than zero");
        }
    }

    public static void validateTransaction(Transaction transaction) throws CustomException {
        if (null == transaction || transaction.getFromAccountId().isEmpty() || transaction.getToAccountId().isEmpty()
                || null == transaction.getAmount() || transaction.getCurrencyCode().isEmpty()) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() Incorrect transaction=" + transaction);
            throw new CustomException("Incorrect transaction");
        }
        validateCurrencyCode(transaction.getCurrencyCode());
    }

    private static void validateCurrencyCode(String currencyCode) throws CustomException {
        setCurrencyCodes();
        if (!CURRENCY_CODES.contains(currencyCode)) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() Incorrect currency code=" + currencyCode);
            throw new CustomException("Incorrect currency code");
        }
    }

    private static void setCurrencyCodes() {
        CURRENCY_CODES.add("USD");
        CURRENCY_CODES.add("PLN");
        CURRENCY_CODES.add("GBP");
    }

    public static void checkCurrencyCodes(String currencyCodeX, String currencyCodeY) throws CustomException {
        if (!currencyCodeX.equals(currencyCodeY)) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() Different currency codes "
                    + currencyCodeX + " " + currencyCodeY);
            throw new CustomException("Different currency codes");
        }
    }
}
