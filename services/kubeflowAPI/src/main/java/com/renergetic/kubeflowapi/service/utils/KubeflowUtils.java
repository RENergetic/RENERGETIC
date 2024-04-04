package com.renergetic.kubeflowapi.service.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KubeflowUtils {

    // Create here methods with generic funtionalities that can be used in the project

    public String sendRequest(String urlString, String httpMethod, Map<String, String> params, Object body,
            Map<String, String> headers) {

        StringBuilder response = new StringBuilder();
        String jsonResponse = "";
        String paramsString = "";
        URL url;
        try {
            System.out.println("SEND REQUEST *******************************************");
            // Specify the URL you want to connect to

            String parseParams = mapParams(params);
            parseParams = parseParams.isBlank() ? parseParams : '?' + parseParams;
            url = new URL(urlString + parseParams);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(httpMethod);

            if (headers != null) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    System.out.println("Feedback headers --> " + entry.getKey() + " : " + entry.getValue());
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            connection.setDoOutput(true);

            if (body != null) {
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = body.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            int responseCode = connection.getResponseCode();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("Response Code: " + responseCode);
                System.out.println("Response Body: " + response.toString());
            }

            System.out.println(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }
    
    static public String mapParams(Map<String, String> params) {
        StringBuilder result = new StringBuilder();

        if (params != null)
            for (Entry<String, String> entry : params.entrySet()) {
                try {
                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    result.append("&");
                } catch (UnsupportedEncodingException e) {
                    System.err.println("Parameter with key " + entry.getKey() + " can't be encoded to UTF8");
                }
            }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }
}
