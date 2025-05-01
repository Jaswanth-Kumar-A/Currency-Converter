import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrencyService {
    private static final String API_KEY = "9139b84f42fe19123faca2e1";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    public static String[] getAllCurrencies() throws Exception {
        try {
            String endpoint = BASE_URL + API_KEY + "/latest/USD";
            JSONObject response = makeApiCall(endpoint);
            
            if (!response.getString("result").equals("success")) {
                throw new Exception("API request failed");
            }
            
            JSONObject rates = response.getJSONObject("conversion_rates");
            List<String> currencies = new ArrayList<>(rates.keySet());
            Collections.sort(currencies);
            return currencies.toArray(new String[0]);
        } catch (Exception e) {
            System.err.println("Error in getAllCurrencies: " + e.getMessage());
            throw e;
        }
    }

    public static double convertCurrency(String fromCurrency, String toCurrency, double amount) throws Exception {
        try {
            // Using direct conversion endpoint
            String endpoint = BASE_URL + API_KEY + "/pair/" + fromCurrency + "/" + toCurrency + "/" + amount;
            JSONObject response = makeApiCall(endpoint);
            
            if (!response.getString("result").equals("success")) {
                throw new Exception("API request failed");
            }
            
            return response.getDouble("conversion_result");
        } catch (Exception e) {
            System.err.println("Error in convertCurrency: " + e.getMessage());
            throw e;
        }
    }

    private static JSONObject makeApiCall(String endpoint) throws Exception {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if (conn.getResponseCode() != 200) {
                throw new Exception("HTTP error code: " + conn.getResponseCode());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return new JSONObject(response.toString());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}