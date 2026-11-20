package com.mirea.kt.ribo.passwordmanager;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AuthClient {

    private static final String TAG = "AuthClient";
    private static final String LOGIN_URL = "https://android-for-students.ru/coursework/login.php";
    private static final int TIMEOUT_MS = 10000;

    public AuthResult login(String login, String password, String group) throws IOException, JSONException {
        String body = "lgn=" + URLEncoder.encode(login, "UTF-8")
                + "&pwd=" + URLEncoder.encode(password, "UTF-8")
                + "&g=" + URLEncoder.encode(group, "UTF-8");

        HttpURLConnection connection = (HttpURLConnection) new URL(LOGIN_URL).openConnection();
        try {
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(body.getBytes("UTF-8"));
            outputStream.close();

            int httpCode = connection.getResponseCode();
            Log.d(TAG, "HTTP code: " + httpCode);
            if (httpCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP " + httpCode);
            }

            String response = readResponse(connection);
            Log.d(TAG, "Server response: " + response);
            return parseResponse(response);
        } finally {
            connection.disconnect();
        }
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "UTF-8"));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }

    private AuthResult parseResponse(String response) throws JSONException {
        JSONObject json = new JSONObject(response);
        int resultCode = json.optInt("result_code", -1);
        String title = json.optString("title", "");
        String task = json.optString("task", "");
        int variant = json.optInt("variant", 0);
        return new AuthResult(resultCode, title, task, variant);
    }
}
