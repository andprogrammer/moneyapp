package com.endpoints.moneyapp;

import com.moneyapp.dao.AccountDAO;
import com.moneyapp.dao.TransactionDAO;
import com.moneyapp.service.AccountService;
import com.moneyapp.service.TransactionService;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static com.endpoints.moneyapp.utils.Utils.createAccount;
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
        String fromAccountId = createAccount("Andrzej", "1000", "USD");
        String toAccountId = createAccount("Tom", "850", "USD");
        String amount = "64";
        String currencyCode = "USD";

        assertNotNull(fromAccountId);
        assertNotNull(toAccountId);

        Response response = request("POST", "/transaction/" + fromAccountId + "/" + toAccountId + "/" + amount + "/" + currencyCode);
        assertThat(response.body, equalTo("\"SUCCESS\""));

        validateAccountBalance(fromAccountId, 936);
        validateAccountBalance(toAccountId, 914);
    }

    private void validateAccountBalance(String accountId, int balance) {
        Response accountResponse = request("GET", "/account/" + accountId);
        JSONObject json = new JSONObject(accountResponse.body);
        assertThat(new BigDecimal(balance), equalTo(json.getBigDecimal("balance")));
    }
}
