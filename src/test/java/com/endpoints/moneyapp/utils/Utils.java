package com.endpoints.moneyapp.utils;

import com.moneyapp.exception.CustomException;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import spark.utils.IOUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;


public class Utils {

    public static final int SUCCESS_RESPONSE = 200;
    public static final String HTTP_LOCALHOST = "http://localhost";
    public static final String PORT = "4567";
    private final static Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName().getClass());

    public static <T> void assertAnyOf(T actual, Pair<T, T> expected) {
        assertThat(actual, anyOf(equalTo(expected.left), equalTo(expected.right)));
    }

    public static Response request(String method, String path) {
        try {
            URL url = new URL(HTTP_LOCALHOST + ":" + PORT + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            connection.connect();
            String body = IOUtils.toString(connection.getInputStream());
            return new Response(connection.getResponseCode(), body);
        } catch (IOException e) {
            logger.error(new Throwable().getStackTrace()[0].getMethodName() + "() " + e.getMessage());
            throw new CustomException("Response error");
        }
    }

    public static class Response {

        public final String body;
        public final int status;

        public Response(int status, String body) {
            this.status = status;
            this.body = body;
        }
    }

    public static String createAccount(final String name, final String balance, final String currencyCode) throws CustomException {
        Utils.Response response = request("PUT", "/account/create?username=" + name + "&balance=" + balance + "&currencycode=" + currencyCode);
        if (null == response)
            throw new CustomException("Creating account failed");
        JSONObject json = new JSONObject(response.body);
        assertJSON(response, json, name, balance, currencyCode);
        return json.getString("id");
    }

    public static void assertJSON(Utils.Response response, JSONObject json, String name, String balance, String currencyCode) {
        assertThat(SUCCESS_RESPONSE, equalTo(response.status));
        assertNotNull(json.getString("id"));
        assertThat(name, equalTo(json.getString("userName")));
        assertThat(new BigDecimal(balance), equalTo(json.getBigDecimal("balance")));
        assertThat(currencyCode, equalTo(json.getString("currencyCode")));
    }

    public static class Pair<L, R> {

        private final L left;
        private final R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        private L getLeft() {
            return left;
        }

        private R getRight() {
            return right;
        }

        @Override
        public int hashCode() {
            return left.hashCode() ^ right.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pair))
                return false;

            Pair pair = (Pair) o;
            return this.left.equals(pair.getLeft()) && this.right.equals(pair.getRight());
        }
    }
}
