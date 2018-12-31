package com.endpoints.moneyapp;

import com.moneyapp.dao.AccountDAO;
import com.moneyapp.service.AccountService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;

import static com.endpoints.moneyapp.utils.Utils.*;


public class AccountServiceTestSuite {

    @Before
    public void setUp() {
        new AccountService(new AccountDAO());
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
    public void testGetAccount() {
        String accountId = createAccount();
        Response response = request("GET", "/account/" + accountId);
        JSONObject json = new JSONObject(response.body);
        assertJSON(response, json, "1000");
    }

    @Test
    public void testCreateAccount() {
        createAccount();
    }

    @Test
    public void testGetAccountBalance() {
        String accountId = createAccount();
        Response response = request("GET", "/account/" + accountId + "/balance");
        assertThat(SUCCESS_RESPONSE, equalTo(response.status));
        assertThat(response.body, equalTo("1000"));
    }

    @Test
    public void testDeleteAccount() {
        String accountId = createAccount();
        Response response = request("DELETE", "/account/" + accountId);
        assertThat(SUCCESS_RESPONSE, equalTo(response.status));
    }

    @Test
    public void testAccountWithdraw() {
        String accountId = createAccount();
        String amount = "128";
        Response response = request("PUT", "/account/" + accountId + "/withdraw/" + amount);
        JSONObject json = new JSONObject(response.body);
        assertJSON(response, json, "872");
    }

    @Test
    public void testAccountDeposit() {
        String accountId = createAccount();
        String amount = "256";
        Response response = request("PUT", "/account/" + accountId + "/deposit/" + amount);
        JSONObject json = new JSONObject(response.body);
        assertJSON(response, json, "1256");
    }

    private void checkResults(String accountId, String accountUserName, BigDecimal accountBalance, String currencyCode, JSONObject firstJSONObject, JSONObject secondJSONObject) {
        assertAnyOf(accountId, new Pair<>(firstJSONObject.getString("id"), secondJSONObject.getString("id")));
        assertAnyOf(accountUserName, new Pair<>(firstJSONObject.getString("userName"), secondJSONObject.getString("userName")));
        assertAnyOf(accountBalance, new Pair<>(firstJSONObject.getBigDecimal("balance"), secondJSONObject.getBigDecimal("balance")));
        assertAnyOf(currencyCode, new Pair<>(firstJSONObject.getString("currencyCode"), secondJSONObject.getString("currencyCode")));
    }

    private String createAccount() {
        Response response = request("PUT", "/account/create?username=Andrzej&balance=1000&currencycode=USD");
        JSONObject json = new JSONObject(response.body);
        assertJSON(response, json, "1000");
        return json.getString("id");
    }

    private void assertJSON(Response response, JSONObject json, String balance) {
        assertThat(SUCCESS_RESPONSE, equalTo(response.status));
        assertNotNull(json.getString("id"));
        assertThat("Andrzej", equalTo(json.getString("userName")));
        assertThat(new BigDecimal(balance), equalTo(json.getBigDecimal("balance")));
        assertThat("USD", equalTo(json.getString("currencyCode")));
    }
}