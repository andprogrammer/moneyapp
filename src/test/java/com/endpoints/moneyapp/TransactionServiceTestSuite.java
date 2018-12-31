package com.endpoints.moneyapp;

import com.moneyapp.dao.AccountDAO;
import com.moneyapp.dao.TransactionDAO;
import com.moneyapp.service.AccountService;
import com.moneyapp.service.TransactionService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.endpoints.moneyapp.utils.Utils.createNewAccount;
import static com.endpoints.moneyapp.utils.Utils.Response;
import static com.endpoints.moneyapp.utils.Utils.request;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;


public class TransactionServiceTestSuite {

    public static final AccountDAO ACCOUNT_SERVICE = new AccountDAO();

    @Before
    public void setUp() {
        new AccountService(ACCOUNT_SERVICE);
        new TransactionService(new TransactionDAO(ACCOUNT_SERVICE));
        awaitInitialization();
    }

    @After
    public void tearDown() {
        stop();
    }

    @Test
    public void testTransferTransaction() {
        String firstAccountId = createNewAccount("Andrzej", "1000", "USD");
        String secondAccountId = createNewAccount("Tom", "850", "USD");
        String amount = "64";
        String currencyCode = "USD";

        assertNotNull(firstAccountId);
        assertNotNull(secondAccountId);

        Response response = request("POST", "/transaction/" + firstAccountId + "/" + secondAccountId + "/" + amount + "/" + currencyCode);
        assertThat(response.body, equalTo("\"SUCCESS\""));
    }
}
