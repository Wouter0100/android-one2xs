package nl.wouter0100.one2xs.utilities;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;

import java.io.IOException;

public class RequestUtilities {

    public static Connection setAuthToken(Connection connection, String authToken) {
        return connection.cookie("store[88-159-157]", authToken);
    }

    public static Response post(Connection connection) throws IOException {
        return connection
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .followRedirects(true)
                .method(Connection.Method.POST)
                .execute();
    }

    public static Response get(Connection connection) throws IOException {
        return connection
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .followRedirects(true)
                .method(Connection.Method.GET)
                .execute();
    }
}
