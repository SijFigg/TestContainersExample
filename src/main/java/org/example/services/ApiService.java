package org.example.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiService {

    private final String baseUrl;
    private final DatabaseService dbService;
    private Logger log = LoggerFactory.getLogger(ApiService.class);

    public ApiService(String baseUrl, DatabaseService dbService) {
        this.baseUrl = baseUrl;
        this.dbService = dbService;
    }

    public void fetchAndInsert(int id)
    {
        try {
            URL url = new URL(baseUrl + "/api/data/" + id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            String name = json.getString("name");

            dbService.insertRecord(id, name);
        }
        catch (Exception e)
        {
            log.error("Fetching data failed due to the following error: {}",e.getMessage());
        }
    }

    public String getUserName(int id) {
        String urlString = baseUrl + "/api/getName/" + id;
        try {
            log.info("🌐 Calling external API to get user: {}", urlString);

            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            if (status != 200) {
                log.warn("⚠️ API returned status {} for ID={}", status, id);
                return null;
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                JSONObject json = new JSONObject(response.toString());
                String name = json.optString("name", null);

                if (name != null) {
                    log.info("✅ Retrieved user from API: id={}, name={}", id, name);
                } else {
                    log.warn("⚠️ API response missing 'name' for ID={}", id);
                }
                return name;
            }

        } catch (Exception e) {
            log.error("Failed to call API for ID={}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to retrieve record for ID=" + id, e);
        }
    }

}
