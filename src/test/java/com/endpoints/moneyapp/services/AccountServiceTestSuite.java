package com.endpoints.moneyapp.services;

import com.moneyapp.dao.implementation.AccountDAOImplementation;
import com.moneyapp.exception.CustomException;
import com.moneyapp.service.AccountService;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;

import static com.endpoints.moneyapp.utils.Utils.*;
import static com.moneyapp.utils.JSONUtil.SUCCESSFUL_RESPONSE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;


public class AccountServiceTestSuite {

    private final static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName().getClass());

    @Rule
    public ExpectedException expectedExceptionThrown = ExpectedException.none();

    @Before
    public void setUp() {
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName()
                    + "() Starting testSuite "
                    + new Throwable().getStackTrace()[0].getClassName()
                    + " on " + HTTP_LOCALHOST + ":" + PORT);
        new AccountService(new AccountDAOImplementation());
        awaitInitialization();
    }

    @After
    public void tearDown() {
        stop();
    }

    @Test
    public void testGetAllAccounts() {
        String firstAccountUserName = "Andrzej";
        BigDecimal firstAccountBalance = new BigDecimal("1000");
        String firstCurrencyCode = "USD";

        Response response = request("PUT", "/account/create?username=" + firstAccountUserName + "&balance=" + firstAccountBalance + "&currencycode=" + firstCurrencyCode);
        JSONObject json = new JSONObject(response.body);
        assertThat(SUCCESS_RESPONSE, equalTo(response.status));
        String firstAccountId = json.getString("id");

        String secondAccountUserName = "Tom";
        BigDecimal secondAccountBalance = new BigDecimal("850");
        String secondCurrencyCode = "USD";

        response = request("PUT", "/account/create?username=" + secondAccountUserName + "&balance=" + secondAccountBalance + "&currencycode=" + secondCurrencyCode);
        json = new JSONObject(response.body);
        assertThat(SUCCESS_RESPONSE, equalTo(response.status));
        String secondAccountId = json.getString("id");

        response = request("GET", "/account/all");
        JSONArray jsonarray = new JSONArray(response.body);
        assertThat(SUCCESS_RESPONSE, equalTo(response.status));

        JSONObject firstJSONObject = jsonarray.getJSONObject(0);
        JSONObject secondJSONObject = jsonarray.getJSONObject(1);

        checkResults(firstAccountId, firstAccountUserName, firstAccountBalance, firstCurrencyCode, firstJSONObject, secondJSONObject);
        checkResults(secondAccountId, secondAccountUserName, secondAccountBalance, secondCurrencyCode, firstJSONObject, secondJSONObject);
    }

    @Test
    public void testGetAccount() throws CustomException {
        String name = "Andrzej";
        String balance = "1000";
        String currencyCode = "USD";
        String accountId = createAccount(name, balance, currencyCode);
        Response response = request("GET", "/account/" + accountId);
        JSONObject json = new JSONObject(response.body);
        assertJSON(response, json, name, balance, currencyCode);
    }

    @Test
    public void testGetNoExistingAccount() throws CustomException {
        String noExistingAccountId = "1024";
        expectedExceptionThrow(CustomException.class, "Response error");
        request("GET", "/account/" + noExistingAccountId);
    }

    @Test
    public void testCreateAccount() throws CustomException {
        createAccount("Andrzej", "1000", "USD");
    }

    @Test
    public void testCreateExistingAccount() throws CustomException {
        createAccount("Andrzej", "1000", "USD");
        expectedExceptionThrow(CustomException.class, "Response error");
        createAccount("Andrzej", "1000", "USD");
    }

    @Test
    public void testGetAccountBalance() throws CustomException {
        String name = "Andrzej";
        String balance = "1000";
        String currencyCode = "USD";
        String accountId = createAccount(name, balance, currencyCode);
        Response response = request("GET", "/account/" + accountId + "/balance");
        assertThat(SUCCESS_RESPONSE, equalTo(response.status));
        assertThat(response.body, equalTo("1000"));
    }

    @Test
    public void testGetNoExistingAccountBalance() throws CustomException {
        String noExistingAccountId = "1024";
        expectedExceptionThrow(CustomException.class, "Response error");
        request("GET", "/account/" + noExistingAccountId + "/balance");
    }

    @Test
    public void testDeleteAccount() throws CustomException {
        String accountId = createAccount("Andrzej", "1000", "USD");
        Response response = request("DELETE", "/account/" + accountId);
        assertThat(response.body, equalTo(Integer.toString(SUCCESSFUL_RESPONSE)));
        assertThat(SUCCESS_RESPONSE, equalTo(response.status));
    }

    @Test
    public void testDeleteNoExistingAccount() throws CustomException {
        String noExistingAccountId = "1024";
        expectedExceptionThrow(CustomException.class, "Response error");
        request("DELETE", "/account/" + noExistingAccountId);
    }

    @Test
    public void testAccountWithdraw() throws CustomException {
        String name = "Andrzej";
        String currencyCode = "USD";
        String accountId = createAccount(name, "1000", currencyCode);
        String amount = "128";
        Response response = request("PUT", "/account/" + accountId + "/withdraw/" + amount);
        JSONObject json = new JSONObject(response.body);
        assertJSON(response, json, name, "872", currencyCode);
    }

    @Test
    public void testNoExistingAccountWithdraw() throws CustomException {
        String noExistingAccountId = "1024";
        expectedExceptionThrow(CustomException.class, "Response error");
        String amount = "128";
        request("PUT", "/account/" + noExistingAccountId + "/withdraw/" + amount);
    }

    @Test
    public void testAccountDeposit() throws CustomException {
        String name = "Andrzej";
        String currencyCode = "USD";
        String accountId = createAccount(name, "1000", currencyCode);
        String amount = "256";
        Response response = request("PUT", "/account/" + accountId + "/deposit/" + amount);
        JSONObject json = new JSONObject(response.body);
        assertJSON(response, json, name, "1256", currencyCode);
    }

    @Test
    public void testNoExistingAccountDeposit() throws CustomException {
        String noExistingAccountId = "1024";
        expectedExceptionThrow(CustomException.class, "Response error");
        String amount = "256";
        request("PUT", "/account/" + noExistingAccountId + "/deposit/" + amount);
    }

    private <T> void expectedExceptionThrow(Class<T> exceptionType, String exceptionMessage) {
        expectedExceptionThrown.expect((Class<? extends Throwable>) exceptionType);
        expectedExceptionThrown.expectMessage(equalTo(exceptionMessage));
    }

    private void checkResults(String accountId, String accountUserName, BigDecimal accountBalance, String currencyCode, JSONObject firstJSONObject, JSONObject secondJSONObject) {
        assertAnyOf(accountId, new Pair<>(firstJSONObject.getString("id"), secondJSONObject.getString("id")));
        assertAnyOf(accountUserName, new Pair<>(firstJSONObject.getString("userName"), secondJSONObject.getString("userName")));
        assertAnyOf(accountBalance, new Pair<>(firstJSONObject.getBigDecimal("balance"), secondJSONObject.getBigDecimal("balance")));
        assertAnyOf(currencyCode, new Pair<>(firstJSONObject.getString("currencyCode"), secondJSONObject.getString("currencyCode")));
    }
}
