package com.renergetic.kubeflowapi.service.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.renergetic.common.utilities.Json;
import com.renergetic.kubeflowapi.dao.HttpsResponseInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KubeflowUtils {

    // Create here methods with generic funtionalities that can be used in the project

    public static HttpsResponseInfo sendRequest(String urlString, String httpMethod, Map<String, String> params,
                                                Object body,
                                                Map<String, String> headers) {
        HttpURLConnection connection;
        URL url;
        try {
            if (params != null) {
                String parseParams = mapParams(params);
                parseParams = parseParams.isBlank() ? parseParams : '?' + parseParams;
                url = new URL(urlString + parseParams);
            } else
                url = new URL(urlString);
            System.out.println(url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            HttpURLConnection.setFollowRedirects(false);
            connection.setRequestMethod(httpMethod);
            if (headers != null) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    System.out.println("Feedback headers --> " + entry.getKey() + " : " + entry.getValue());
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            connection.setDoOutput(true);
            if (body != null) {
                String mBody = body instanceof String ? (String) body : Json.toJson(body);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = mBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            HttpsResponseInfo info = new HttpsResponseInfo(connection.getResponseCode(), getBody(connection),
                    connection.getHeaderFields());
            connection.disconnect();
            return info;
        } catch (IllegalArgumentException e) {
            log.error("Check that the URL is valid and  the HTTP Method is allowed");
            return new HttpsResponseInfo(503, "Check that the URL is valid and  the HTTP Method is allowed", null);
        } catch (JsonProcessingException e) {
            log.error("Can't parse body to a JSON");
            return new HttpsResponseInfo(503, "Can't parse body to a JSON", null);

        } catch (IOException e) {
            log.error("Can't get connection with the URL");
            return new HttpsResponseInfo(503, "Can't get connection with the URL", null);
        } catch (Exception e) {

            log.error("Unknown exception", e);
            return new HttpsResponseInfo(503, "Unknown exception", null);

        }

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

    private static String getBody(HttpURLConnection connection) {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
//            System.out.println("");
            /*System.out.println("Response body 1: " + response);
            System.out.println("Response body 2: " + response.toString());*/

        } catch (IllegalArgumentException e) {
            log.error("Check that the URL is valid and  the HTTP Method is allowed");
        } catch (JsonProcessingException e) {
            log.error("Can't parse body to a JSON");
        } catch (IOException e) {
            log.error("Can't get connection with the URL");
        } catch (Exception e) {
            log.error("Unknown exception", e);
        }
        return response.toString();
    }

    public static String getState(String response) {
        /**
         * state prevents an attack where the attacker produces a fake authentication response,
         * e.g. as part of the Basic Client Profile by sending a code to the Client's redirect URI.
         * For example: after phishing the user an attacker could inject a stolen code that would be
         * associated with the current user in this way. The state correlates request and response
         * so an unsolicited crafted response is not possible without knowing the state parameter
         * that was used in the request.
         */
        Pattern pattern = Pattern.compile("state=([^&\"]+)");
        Matcher matcher = pattern.matcher(response);
        String stateValue = "";
        // Find the state value if the pattern matches
        if (matcher.find()) {
            stateValue = matcher.group(1);
            System.out.println("State Value: " + stateValue);
        } else {
            System.out.println("State value not found.");
        }
        return stateValue;
    }

    public static String extractParamValue(String inputString, String paramName, String end) {
        String paramValue = "";
        int startIndex = inputString.indexOf(paramName);
        if (startIndex != -1) {
            startIndex += paramName.length();
            int endIndex = inputString.indexOf(end, startIndex);
            if (endIndex != -1) {
                paramValue = inputString.substring(startIndex, endIndex);
            } else {
                paramValue = inputString.substring(startIndex);
            }
        }
        return paramValue;
    }
}
