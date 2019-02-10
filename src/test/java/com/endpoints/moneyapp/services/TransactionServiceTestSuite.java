package com.endpoints.moneyapp.services;

import com.moneyapp.dao.implementation.AccountDAOImplementation;
import com.moneyapp.dao.implementation.TransactionDAOImplementation;
import com.moneyapp.exception.CustomException;
import com.moneyapp.service.AccountService;
import com.moneyapp.service.TransactionService;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;

import static com.endpoints.moneyapp.utils.Utils.*;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;


public class TransactionServiceTestSuite {

    public static final AccountDAOImplementation ACCOUNT_SERVICE = new AccountDAOImplementation();

    @Rule
    public ExpectedException expectedExceptionThrown = ExpectedException.none();

    @Before
    public void setUp() {
        new AccountService(ACCOUNT_SERVICE);
        new TransactionService(new TransactionDAOImplementation(ACCOUNT_SERVICE));
        awaitInitialization();
    }

    @After
    public void tearDown() {
        stop();
    }

    @Test
    public void testTransferTransaction() throws CustomException {
        String fromAccountId = createAccount("Andrzej", "1000", "USD");
        String toAccountId = createAccount("Tom", "850", "USD");
        String amount = "64";
        String currencyCode = "USD";

        assertNotNull(fromAccountId);
        assertNotNull(toAccountId);

        Response response = request("POST", "/transaction/" + fromAccountId + "/" + toAccountId + "/" + amount + "/" + currencyCode);
        assertThat(response.body, equalTo(Integer.toString(SC_OK)));

        validateAccountBalance(fromAccountId, 936);
        validateAccountBalance(toAccountId, 914);
    }

    @Test
    public void testTransferTransactionNegativeAmount() throws CustomException {
        String fromAccountId = createAccount("Andrzej", "1000", "USD");
        String toAccountId = createAccount("Tom", "850", "USD");
        String amount = "-64";
        String currencyCode = "USD";

        assertNotNull(fromAccountId);
        assertNotNull(toAccountId);

        expectedExceptionThrow(CustomException.class, "Response error");
        request("POST", "/transaction/" + fromAccountId + "/" + toAccountId + "/" + amount + "/" + currencyCode);
    }

    private <T> void expectedExceptionThrow(Class<T> exceptionType, String exceptionMessage) {
        expectedExceptionThrown.expect((Class<? extends Throwable>) exceptionType);
        expectedExceptionThrown.expectMessage(equalTo(exceptionMessage));
    }

    private void validateAccountBalance(String accountId, int balance) {
        Response accountResponse = request("GET", "/account/" + accountId);
        JSONObject json = new JSONObject(accountResponse.body);
        assertThat(new BigDecimal(balance), equalTo(json.getBigDecimal("balance")));
    }
}
