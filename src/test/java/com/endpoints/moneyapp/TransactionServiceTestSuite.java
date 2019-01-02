package com.endpoints.moneyapp;

import com.moneyapp.dao.implementation.AccountDAOImplementation;
import com.moneyapp.dao.implementation.TransactionDAOImplementation;
import com.moneyapp.service.AccountService;
import com.moneyapp.service.TransactionService;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static com.endpoints.moneyapp.utils.Utils.*;
import static com.moneyapp.utils.JSONUtil.SUCCESSFUL_RESPONSE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;


public class TransactionServiceTestSuite {

    public static final AccountDAOImplementation ACCOUNT_SERVICE = new AccountDAOImplementation();
    private final static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName().getClass());

    @Before
    public void setUp() {
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName()
                    + "() Starting testSuite "
                    + new Throwable().getStackTrace()[0].getClassName()
                    + " on " + HTTP_LOCALHOST + ":" + PORT);
        new AccountService(ACCOUNT_SERVICE);
        new TransactionService(new TransactionDAOImplementation(ACCOUNT_SERVICE));
        awaitInitialization();
    }

    @After
    public void tearDown() {
        stop();
    }

    @Test
    public void testTransferTransaction() {
        String fromAccountId = createAccount("Andrzej", "1000", "USD");
        String toAccountId = createAccount("Tom", "850", "USD");
        String amount = "64";
        String currencyCode = "USD";

        assertNotNull(fromAccountId);
        assertNotNull(toAccountId);

        Response response = request("POST", "/transaction/" + fromAccountId + "/" + toAccountId + "/" + amount + "/" + currencyCode);
        assertThat(response.body, equalTo(Integer.toString(SUCCESSFUL_RESPONSE)));

        validateAccountBalance(fromAccountId, 936);
        validateAccountBalance(toAccountId, 914);
    }

    private void validateAccountBalance(String accountId, int balance) {
        Response accountResponse = request("GET", "/account/" + accountId);
        JSONObject json = new JSONObject(accountResponse.body);
        assertThat(new BigDecimal(balance), equalTo(json.getBigDecimal("balance")));
    }
}
