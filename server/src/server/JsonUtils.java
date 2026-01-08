package server;

public class JsonUtils {

    public static String quote(String s) {
        return "\"" + s + "\"";
    }

    public static String field(String key, String value) {
        return quote(key) + ":" + value;
    }

    public static String obj(String... fields) {
        return "{" + String.join(",", fields) + "}";
    }
}
