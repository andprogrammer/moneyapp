package com.moneyapp.dao;

import com.moneyapp.exception.CustomException;
import com.moneyapp.model.Account;
import com.moneyapp.model.Transaction;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

import static com.moneyapp.utils.Utils.*;


public class TransactionDAO {

    private AccountDAO accountDAO;
    private final static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName().getClass());

    public TransactionDAO(final AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public int transfer(Transaction transaction) throws CustomException {
        validateTransaction(transaction);

        Account fromAccount = accountDAO.getAccount(transaction.getFromAccountId());
        Account toAccount = accountDAO.getAccount(transaction.getToAccountId());

        checkCurrencyCodes(fromAccount.getCurrencyCode(), toAccount.getCurrencyCode());
        checkCurrencyCodes(transaction.getCurrencyCode(), fromAccount.getCurrencyCode());

        BigDecimal amount = transaction.getAmount();
        validateBalanceLessThanOrEqualZero(amount);

        if (logger.isDebugEnabled()) {
            logger.debug(new Throwable().getStackTrace()[0].getMethodName()
                    + "() fromAccount=" + fromAccount
                    + " toAccount=" + toAccount
                    + " amount=" + amount);
        }

        synchronized (this) {
            BigDecimal fromAccountNewBalance = fromAccount.getBalance().subtract(amount);
            validateBalanceLessThanZero(fromAccountNewBalance);
            fromAccount.setBalance(fromAccountNewBalance);
            toAccount.setBalance(toAccount.getBalance().add(amount));
        }
        return 0;
    }
}
