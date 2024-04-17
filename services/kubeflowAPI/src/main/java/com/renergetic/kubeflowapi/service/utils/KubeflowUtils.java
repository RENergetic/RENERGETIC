package com.renergetic.kubeflowapi.service.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import com.renergetic.kubeflowapi.model.HttpsResponseInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KubeflowUtils {

    // Create here methods with generic funtionalities that can be used in the project
    
    public HttpsResponseInfo sendRequest(String urlString, String httpMethod, Map<String, String> params, Object body,
            Map<String, String> headers) {

        String response = new String();
        String jsonResponse = "";
        String paramsString = "";
        HttpURLConnection connection;
        URL url;
        try {
            System.out.println("SEND REQUEST *******************************************");
            // Specify the URL you want to connect to

            String parseParams = mapParams(params);
            parseParams = parseParams.isBlank() ? parseParams : '?' + parseParams;
            url = new URL(urlString + parseParams);

            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(true);
            HttpURLConnection.setFollowRedirects(true); /////// TODO: REVISAR QUE ES NECESARIO DE AQUÍ
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

            HttpsResponseInfo info = new HttpsResponseInfo(connection.getResponseCode(), getBody(connection), connection.getHeaderFields());
            
            return info;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public String getBody(HttpURLConnection connection) {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            System.out.println("");
            System.out.println("Response Body: " + response.toString());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response.toString();
    }
}
