package com.endpoints.moneyapp;

import com.moneyapp.dao.UserDAO;
import com.moneyapp.service.UserService;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.endpoints.moneyapp.utils.Utils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;


public class UserServiceTestSuite {

    private final static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName().getClass());

    @Before
    public void setUp() {
        if (logger.isDebugEnabled())
            logger.debug(new Throwable().getStackTrace()[0].getMethodName()
                    + "() Starting testSuite "
                    + new Throwable().getStackTrace()[0].getClassName()
                    + " on " + HTTP_LOCALHOST + ":" + PORT);
        new UserService(new UserDAO());
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
        String userId = createUser();
        Response response = request("GET", "/user/" + userId);
        JSONObject json = new JSONObject(response.body);
        assertJSON(response, json, "Andrzej", "test@gmail.com");
    }

    @Test
    public void testCreateUser() {
        createUser();
    }

    @Test
    public void testUpdateUser() {
        String userId = createUser();
        Response response = request("POST", "/user/" + userId + "?name=Tom&email=tom@yahoo.com");
        JSONObject json = new JSONObject(response.body);
        assertJSON(response, json, "Tom", "tom@yahoo.com");
    }

    @Test
    public void testDeleteUser() {
        String userId = createUser();
        Response response = request("DELETE", "/user/" + userId);
        assertThat(SUCCESS_RESPONSE, equalTo(response.status));
    }

    private String createUser() {
        Response response = request("PUT", "/user/create?name=Andrzej&email=test@gmail.com");
        JSONObject json = new JSONObject(response.body);
        assertJSON(response, json, "Andrzej", "test@gmail.com");
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
