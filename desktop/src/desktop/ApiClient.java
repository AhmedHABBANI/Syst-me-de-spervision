package desktop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {

    private static final String API = "http://127.0.0.1:8080";

    public static Metric fetchMetrics(String agentId) {
        try {
            URL url = new URL(API + "/metrics/" + agentId);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream())
            );

            String json = in.readLine();
            in.close();

            Metric m = new Metric();
            m.cpu = Double.parseDouble(json.split("\"cpu\":")[1].split(",")[0]);
            m.memory = Double.parseDouble(json.split("\"memory\":")[1].split(",")[0]);
            m.disk = Double.parseDouble(json.split("\"disk\":")[1].split("}")[0]);

            return m;

        } catch (Exception e) {
            return null;
        }
    }
}
