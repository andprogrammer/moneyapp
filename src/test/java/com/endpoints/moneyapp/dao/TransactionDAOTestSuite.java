package com.endpoints.moneyapp.dao;

import com.endpoints.moneyapp.dao.helpers.TransactionDAOImplementationHelper;
import com.moneyapp.dao.implementation.AccountDAOImplementation;
import com.moneyapp.exception.CustomException;
import com.moneyapp.model.Account;
import com.moneyapp.model.Transaction;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static spark.Spark.stop;

public class TransactionDAOTestSuite {

    private final static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName().getClass());

    @Rule
    public ExpectedException expectedExceptionThrown = ExpectedException.none();

    @Before
    public void setUp() {
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName()
                    + "() Starting testSuite "
                    + new Throwable().getStackTrace()[0].getClassName());
    }

    @After
    public void tearDown() {
        stop();
    }

    @Test
    public void testTransactionTransfer() throws CustomException {
        TransactionDAOImplementationHelper transactionDAO = getTransactionDAO();
        Account accountFrom = transactionDAO.createAccount("Andrzej", new BigDecimal("1000"), "USD");
        Account accountTo = transactionDAO.createAccount("Tom", new BigDecimal("1200"), "USD");

        Transaction transaction = new Transaction(accountFrom.getId(), accountTo.getId(), new BigDecimal("256"), "USD");
        assertThat(0, equalTo(transactionDAO.transfer(transaction)));
        validateBalance(new BigDecimal("744"), accountFrom);
        validateBalance(new BigDecimal("1456"), accountTo);
    }

    @Test
    public void testTransactionTransferNoExistingAccount() throws CustomException {
        TransactionDAOImplementationHelper transactionDAO = getTransactionDAO();
        Account accountFrom = transactionDAO.createAccount("Andrzej", new BigDecimal("1000"), "USD");

        String noExistingAccountId = "4096";
        expectedExceptionThrow(CustomException.class, "Account with id " + noExistingAccountId + " not found");
        Transaction transaction = new Transaction(accountFrom.getId(), noExistingAccountId, new BigDecimal("256"), "USD");
        assertThat(0, equalTo(transactionDAO.transfer(transaction)));
    }

    @Test
    public void testTransactionTransferNotEnoughMoney() throws CustomException {
        TransactionDAOImplementationHelper transactionDAO = getTransactionDAO();
        Account accountFrom = transactionDAO.createAccount("Andrzej", new BigDecimal("966"), "USD");
        Account accountTo = transactionDAO.createAccount("Tom", new BigDecimal("1425"), "USD");

        expectedExceptionThrow(CustomException.class, "Parameter 'amount' less than zero");
        BigDecimal amount = accountFrom.getBalance().add(new BigDecimal("55"));
        Transaction transaction = new Transaction(accountFrom.getId(), accountTo.getId(), amount, "USD");
        transactionDAO.transfer(transaction);
    }

    private TransactionDAOImplementationHelper getTransactionDAO() {
        return new TransactionDAOImplementationHelper(new AccountDAOImplementation());
    }

    private <T> void expectedExceptionThrow(Class<T> exceptionType, String exceptionMessage) {
        expectedExceptionThrown.expect((Class<? extends Throwable>) exceptionType);
        expectedExceptionThrown.expectMessage(equalTo(exceptionMessage));
    }

    void validateBalance(BigDecimal expectedBalance, Account account) {
        assertThat(expectedBalance, equalTo(account.getBalance()));
    }
}
