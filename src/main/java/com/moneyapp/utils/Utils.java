package com.moneyapp.utils;

import com.moneyapp.exception.CustomException;

import java.math.BigDecimal;


public class Utils {

    public static void checkIdConstraint(String id) throws CustomException {
        if (id == null || id.isEmpty())
            throw new CustomException("Argument 'id' cannot be empty");
    }

    public static void checkBalanceLessThanOrEqualZero(BigDecimal balance) throws CustomException {
        if (null == balance || balance.compareTo(BigDecimal.ZERO) <= 0)
            throw new CustomException("Parameter 'balance' less than or equal zero");
    }

    public static void checkBalanceLessThanZero(BigDecimal balance) throws CustomException {
        if (null == balance || balance.compareTo(BigDecimal.ZERO) < 0)
            throw new CustomException("Parameter 'balance' less than zero");
    }
}
