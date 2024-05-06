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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.renergetic.common.utilities.Json;
import com.renergetic.kubeflowapi.model.HttpsResponseInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KubeflowUtils {

    // Create here methods with generic funtionalities that can be used in the project

    public static HttpsResponseInfo sendRequest(String urlString, String httpMethod, Map<String, String> params,
                                                Object body,
                                                Map<String, String> headers) {

//        String response = new String();
//        String jsonResponse = "";
//        String paramsString = "";
        HttpURLConnection connection;
        URL url;
        try {
//            System.out.println("SEND REQUEST *******************************************");
            // Specify the URL you want to connect to
            if (params != null) {
                String parseParams = mapParams(params);
                parseParams = parseParams.isBlank() ? parseParams : '?' + parseParams;
                url = new URL(urlString + parseParams);
            } else
                url = new URL(urlString  );

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
                String mBody=null;
                if(body instanceof String){
                    mBody=(String)body;
                }else
                    mBody=Json.toJson(body);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = mBody.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            HttpsResponseInfo info = new HttpsResponseInfo(connection.getResponseCode(), getBody(connection),
                    connection.getHeaderFields());

//            System.out.println(info);

            connection.disconnect();
            return info;
        } catch (IllegalArgumentException e) {
            System.err.println("Check that the URL is valid and  the HTTP Method is allowed");
        } catch (JsonProcessingException e) {
            System.err.println("Can't parse body to a JSON");
        } catch (IOException e) {
            System.err.println("Can't get connection with the URL");
        } catch (Exception e) {
            System.err.println("Unknow exception");
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

    private static String getBody(HttpURLConnection connection) {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            System.out.println("");
            /*System.out.println("Response body 1: " + response);
            System.out.println("Response body 2: " + response.toString());*/

        } catch (IllegalArgumentException e) {
            System.err.println("Check that the URL is valid and  the HTTP Method is allowed");
        } catch (JsonProcessingException e) {
            System.err.println("Can't parse body to a JSON");
        } catch (IOException e) {
            System.err.println("Can't get connection with the URL");
        } catch (Exception e) {
            System.err.println("Unknow exception");
            e.printStackTrace();
        }
        return response.toString();
    }

    public static String getState(String response) { //MAYBE IT CAN BE DELETED
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
