package com.endpoints.moneyapp.services;

import com.moneyapp.dao.implementation.UserDAOImplementation;
import com.moneyapp.exception.CustomException;
import com.moneyapp.service.UserService;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.endpoints.moneyapp.utils.Utils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;


public class UserServiceTestSuite {

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
        new UserService(new UserDAOImplementation());
        awaitInitialization();
    }

    @After
    public void tearDown() {
        stop();
    }

    @Test
    public void testGetAllUsers() {
        String firstUserName = "Andrzej";
        String firstUserEmail = "test@gmail.com";
        Response response = request("PUT", "/user/create?name=" + firstUserName + "&email=" + firstUserEmail);
        JSONObject json = new JSONObject(response.body);
        String firstUserId = json.getString("id");

        String secondUserName = "Tom";
        String secondUserEmail = "tom@gmail.com";
        response = request("PUT", "/user/create?name=" + secondUserName + "&email=" + secondUserEmail);
        json = new JSONObject(response.body);
        String secondUserId = json.getString("id");

        response = request("GET", "/user/all");
        JSONArray jsonarray = new JSONArray(response.body);

        JSONObject firstJSONObject = jsonarray.getJSONObject(0);
        JSONObject secondJSONObject = jsonarray.getJSONObject(1);

        assertResults(firstUserId, firstUserName, firstUserEmail, firstJSONObject, secondJSONObject);
        assertResults(secondUserId, secondUserName, secondUserEmail, firstJSONObject, secondJSONObject);
    }

    @Test
    public void testGetUser() {
        String name = "Andrzej";
        String email = "test@gmail.com";
        String userId = createUser(name, email);
        Response response = request("GET", "/user/" + userId);
        JSONObject json = new JSONObject(response.body);
        assertJSON(response, json, name, email);
    }

    @Test
    public void testGetNoExistingUser() {
        String noExistingUserId = "2048";
        expectedExceptionThrow(CustomException.class, "Response error");
        request("GET", "/user/" + noExistingUserId);
    }

    @Test
    public void testCreateUser() {
        createUser("Andrzej", "test@gmail.com");
    }

    @Test
    public void testCreateExistingUser() {
        String name = "Andrzej";
        String email = "test@gmail.com";
        createUser(name, email);
        expectedExceptionThrow(CustomException.class, "Response error");
        createUser(name, email);
    }

    @Test
    public void testUpdateUser() {
        String userId = createUser("Andrzej", "test@gmail.com");
        Response response = request("POST", "/user/" + userId + "?name=Tom&email=tom@yahoo.com");
        JSONObject json = new JSONObject(response.body);
        assertJSON(response, json, "Tom", "tom@yahoo.com");
    }

    @Test
    public void testUpdateNoExistingUser() {
        String noExistingUserId = "2048";
        expectedExceptionThrow(CustomException.class, "Response error");
        request("POST", "/user/" + noExistingUserId + "?name=Tom&email=tom@yahoo.com");
    }

    @Test
    public void testDeleteUser() {
        String userId = createUser("Andrzej", "test@gmail.com");
        Response response = request("DELETE", "/user/" + userId);
        assertThat(SUCCESS_RESPONSE, equalTo(response.status));
    }

    @Test
    public void testDeleteNoExistingUser() {
        String noExistingUserId = "2048";
        expectedExceptionThrow(CustomException.class, "Response error");
        request("DELETE", "/user/" + noExistingUserId);
    }

    private <T> void expectedExceptionThrow(Class<T> exceptionType, String exceptionMessage) {
        expectedExceptionThrown.expect((Class<? extends Throwable>) exceptionType);
        expectedExceptionThrown.expectMessage(equalTo(exceptionMessage));
    }

    private String createUser(final String name, final String email) {
        Response response = request("PUT", "/user/create?name=" + name + "&email=" + email);
        JSONObject json = new JSONObject(response.body);
        assertJSON(response, json, name, email);
        return json.getString("id");
    }

    private void assertJSON(Response response, JSONObject json, String name, String email) {
        assertThat(SUCCESS_RESPONSE, equalTo(response.status));
        assertNotNull(json.get("id"));
        assertThat(name, equalTo(json.getString("name")));
        assertThat(email, equalTo(json.getString("email")));
    }

    private void assertResults(String firstUserId, String firstUserName, String firstUserEmail, JSONObject firstJSONObject, JSONObject secondJSONObject) {
        assertAnyOf(firstUserId, new Pair<>(firstJSONObject.getString("id"), secondJSONObject.getString("id")));
        assertAnyOf(firstUserName, new Pair<>(firstJSONObject.getString("name"), secondJSONObject.getString("name")));
        assertAnyOf(firstUserEmail, new Pair<>(firstJSONObject.getString("email"), secondJSONObject.getString("email")));
    }
}
