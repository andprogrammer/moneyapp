package com.endpoints.moneyapp.dao;

import com.moneyapp.dao.AbstractFactory;
import com.moneyapp.dao.AccountDAO;
import com.moneyapp.exception.CustomException;
import com.moneyapp.model.Account;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static spark.Spark.stop;

public class AccountDAOTestSuite {

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
    public void testGetAllAccounts() throws CustomException {
        AccountDAO accountDAO = getAccountDAO();
        accountDAO.createAccount("Andrzej", new BigDecimal("1000"), "USD");
        accountDAO.createAccount("Tom", new BigDecimal("850"), "USD");
        List<Account> accounts = accountDAO.getAllAccounts();
        assertThat(2, equalTo(accounts.size()));
    }

    @Test
    public void testGetAccount() throws CustomException {
        createAccount("Andrzej", new BigDecimal("1000"), "USD");
    }

    @Test
    public void testGetNonExistingUser() throws CustomException {
        String accountId = "128";
        expectedExceptionThrow(CustomException.class, "Account with id " + accountId + " not found");
        AccountDAO accountDAO = getAccountDAO();
        accountDAO.getAccount(accountId);
    }

    @Test
    public void testCreateAccount() throws CustomException {
        createAccount("Andrzej", new BigDecimal("1000"), "USD");
    }

    @Test
    public void testDeleteAccount() throws CustomException {
        AccountDAO accountDAO = getAccountDAO();
        Account account = accountDAO.createAccount("Andrzej", new BigDecimal("1000"), "USD");
        assertThat(1, equalTo(accountDAO.getAllAccounts().size()));
        accountDAO.deleteAccount(account.getId());
        assertThat(0, equalTo(accountDAO.getAllAccounts().size()));
    }

    @Test
    public void testDeleteNonExistingAccount() throws CustomException {
        String accountId = "64";
        expectedExceptionThrow(CustomException.class, "Account with id " + accountId + " not found");
        AccountDAO accountDAO = getAccountDAO();
        accountDAO.deleteAccount(accountId);
    }

    @Test
    public void testUpdateAccountBalance() throws CustomException {
        AccountDAO accountDAO = getAccountDAO();
        Account account = accountDAO.createAccount("Andrzej", new BigDecimal("1000"), "USD");
        account.setBalance(new BigDecimal("550"));
        accountDAO.updateAccountBalance(account.getId(), account.getBalance());
        assertThat(account, equalTo(accountDAO.getAccount(account.getId())));
    }

    @Test
    public void testUpdateAccountBalanceNonExistingAccount() throws CustomException {
        String accountId = "2048";
        expectedExceptionThrow(CustomException.class, "Account with id " + accountId + " not found");
        AccountDAO accountDAO = getAccountDAO();
        accountDAO.updateAccountBalance(accountId, new BigDecimal("770"));
    }

    private static Account createAccount(String name, BigDecimal balance, String currencyCode) throws CustomException {
        AccountDAO accountDAO = getAccountDAO();
        Account account = accountDAO.createAccount(name, balance, currencyCode);
        assertThat(account, equalTo(accountDAO.getAccount(account.getId())));
        return account;
    }

    private static AccountDAO getAccountDAO() {
        return AbstractFactory.getFactory(AbstractFactory.FactoryType.DAO).getAccountDAO();
    }

    private void expectedExceptionThrow(Class<CustomException> exceptionType, String exceptionMessage) {
        expectedExceptionThrown.expect(exceptionType);
        expectedExceptionThrown.expectMessage(equalTo(exceptionMessage));
    }
}
