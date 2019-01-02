package com.endpoints.moneyapp.dao.helpers;

import com.moneyapp.dao.implementation.AccountDAOImplementation;
import com.moneyapp.dao.implementation.TransactionDAOImplementation;
import com.moneyapp.exception.CustomException;
import com.moneyapp.model.Account;

import java.math.BigDecimal;

public class TransactionDAOImplementationHelper extends TransactionDAOImplementation {

    public TransactionDAOImplementationHelper(AccountDAOImplementation accountDAOImplementation) {
        super(accountDAOImplementation);
    }

    public Account createAccount(String name, BigDecimal balance, String email) throws CustomException {
        return accountDAOImplementation.createAccount(name, balance, email);
    }
}
