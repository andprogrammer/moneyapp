import spark.utils.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.fail;

public class Utils {

    public static final int SUCCESS_RESPONSE = 200;
    public static final String HTTP_LOCALHOST = "http://localhost";
    public static final String PORT = "4567";

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
            e.printStackTrace();
            fail("Sending request failed: " + e.getMessage());
            return null;
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

    public static class Pair<L, R> {

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
            Pair pair = (Pair) o;
            return this.left.equals(pair.getLeft()) &&
                    this.right.equals(pair.getRight());
        }
    }
}
