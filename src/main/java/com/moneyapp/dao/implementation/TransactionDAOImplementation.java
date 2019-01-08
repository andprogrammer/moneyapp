package com.moneyapp.dao.implementation;

import com.moneyapp.dao.TransactionDAO;
import com.moneyapp.exception.CustomException;
import com.moneyapp.model.Account;
import com.moneyapp.model.Transaction;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

import static com.moneyapp.utils.Utils.*;

public class TransactionDAOImplementation implements TransactionDAO {

    protected AccountDAOImplementation accountDAOImplementation;
    private final static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName().getClass());

    public TransactionDAOImplementation(final AccountDAOImplementation accountDAOImplementation) {
        this.accountDAOImplementation = accountDAOImplementation;
    }

    public int transfer(Transaction transaction) throws CustomException {
        validateTransaction(transaction);

        Account fromAccount = accountDAOImplementation.getAccount(transaction.getFromAccountId());
        Account toAccount = accountDAOImplementation.getAccount(transaction.getToAccountId());

        validateCurrencyCodes(fromAccount.getCurrencyCode(), toAccount.getCurrencyCode());
        validateCurrencyCodes(transaction.getCurrencyCode(), fromAccount.getCurrencyCode());

        BigDecimal amount = transaction.getAmount();
        validateAmountLessThanOrEqualZero(amount);

        if (logger.isDebugEnabled()) {
            logger.debug(new Throwable().getStackTrace()[0].getMethodName()
                    + "() fromAccount=" + fromAccount
                    + " toAccount=" + toAccount
                    + " amount=" + amount);
        }

        synchronized (this) {
            BigDecimal fromAccountNewBalance = fromAccount.getBalance().subtract(amount);
            validateAmountLessThanZero(amount);
            fromAccount.setBalance(fromAccountNewBalance);
            toAccount.setBalance(toAccount.getBalance().add(amount));
        }
        return 0;
    }

    private void validateAmountLessThanZero(BigDecimal amount) {
        if (null == amount || amount.compareTo(BigDecimal.ZERO) < 0) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() Incorrect amount=" + amount);
            throw new CustomException("Not enough amount on account");
        }
    }
}
