import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;

public class UserControllerTestSuite {

    public static final int SUCCESSFUL_RESPONSE = 200;

    @Before
    public void setUp() {
        new UserController(new UserService());
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
        TestResponse response = request("PUT", "/user/create?name=" + firstUserName + "&email=" + firstUserEmail);
        Map<String, String> json = response.json();
        String firstUserId = json.get("id");

        String secondUserName = "Tom";
        String secondUserEmail = "tom@gmail.com";
        response = request("PUT", "/user/create?name=" + secondUserName + "&email=" + secondUserEmail);
        json = response.json();
        String secondUserId = json.get("id");

        response = request("GET", "/user/all");
        JSONArray jsonarray = new JSONArray(response.body);

        JSONObject firstJSonObject = jsonarray.getJSONObject(0);
        JSONObject secondJSonObject = jsonarray.getJSONObject(1);

        assertAnyOf(firstUserId, new Pair<>(firstJSonObject.getString("id"), secondJSonObject.getString("id")));
        assertAnyOf(firstUserName, new Pair<>(firstJSonObject.getString("name"), secondJSonObject.getString("name")));
        assertAnyOf(firstUserEmail, new Pair<>(firstJSonObject.getString("email"), secondJSonObject.getString("email")));

        assertAnyOf(secondUserId, new Pair<>(firstJSonObject.getString("id"), secondJSonObject.getString("id")));
        assertAnyOf(secondUserName, new Pair<>(firstJSonObject.getString("name"), secondJSonObject.getString("name")));
        assertAnyOf(secondUserEmail, new Pair<>(firstJSonObject.getString("email"), secondJSonObject.getString("email")));
    }

    @Test
    public void testGetUser() {
        String userId = createUser();

        TestResponse response = request("GET", "/user/" + userId);
        Map<String, String> json = response.json();
        Assert.assertEquals(SUCCESSFUL_RESPONSE, response.status);
        Assert.assertEquals("Andrzej", json.get("name"));
        Assert.assertEquals("test@gmail.com", json.get("email"));
        assertNotNull(json.get("id"));
    }

    @Test
    public void testCreateUser() {
        TestResponse response = request("PUT", "/user/create?name=Andrzej&email=test@gmail.com");
        request("PUT", "/user/create?name=Tom&email=tom@gmail.com");
        Map<String, String> json = response.json();
        Assert.assertEquals(SUCCESSFUL_RESPONSE, response.status);
        Assert.assertEquals("Andrzej", json.get("name"));
        Assert.assertEquals("test@gmail.com", json.get("email"));
        assertNotNull(json.get("id"));
    }

    @Test
    public void testCreateNewUser() {
        TestResponse response = request("PUT", "/user/create?name=Andrzej&email=test@gmail.com");
        Map<String, String> json = response.json();
        Assert.assertEquals(SUCCESSFUL_RESPONSE, response.status);
        Assert.assertEquals("Andrzej", json.get("name"));
        Assert.assertEquals("test@gmail.com", json.get("email"));
        assertNotNull(json.get("id"));
    }

    @Test
    public void testUpdateUser() {
        String userId = createUser();

        TestResponse response = request("POST", "/user/" + userId + "?name=Tom&email=tom@yahoo.com");
        Map<String, String> json = response.json();
        Assert.assertEquals(SUCCESSFUL_RESPONSE, response.status);
        Assert.assertEquals("Tom", json.get("name"));
        Assert.assertEquals("tom@yahoo.com", json.get("email"));
        assertNotNull(json.get("id"));
    }

    @Test
    public void testDeleteUser() {
        String userId = createUser();
        TestResponse response = request("DELETE", "/user/" + userId);
        Assert.assertEquals(SUCCESSFUL_RESPONSE, response.status);
    }

    private void assertAnyOf(String actual, Pair<String, String> expected) {
        assertThat(actual, anyOf(equalTo(expected.left), equalTo(expected.right)));
    }

    private String createUser() {
        TestResponse response = request("PUT", "/user/create?name=Andrzej&email=test@gmail.com");
        Map<String, String> json = response.json();
        Assert.assertEquals(SUCCESSFUL_RESPONSE, response.status);
        Assert.assertEquals("Andrzej", json.get("name"));
        Assert.assertEquals("test@gmail.com", json.get("email"));
        assertNotNull(json.get("id"));
        return json.get("id");
    }

    private TestResponse request(String method, String path) {
        try {
            URL url = new URL("http://localhost:4567" + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            connection.connect();
            String body = IOUtils.toString(connection.getInputStream());
            return new TestResponse(connection.getResponseCode(), body);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Sending request failed: " + e.getMessage());
            return null;
        }
    }

    private static class TestResponse {

        public final String body;
        public final int status;

        public TestResponse(int status, String body) {
            this.status = status;
            this.body = body;
        }

        public Map<String, String> json() {
            return new Gson().fromJson(body, HashMap.class);
        }
    }

    private class Pair<L, R> {

        private final L left;
        private final R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public L getLeft() {
            return left;
        }

        public R getRight() {
            return right;
        }

        @Override
        public int hashCode() {
            return left.hashCode() ^ right.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pair)) return false;
            Pair pairo = (Pair) o;
            return this.left.equals(pairo.getLeft()) &&
                    this.right.equals(pairo.getRight());
        }
    }
}