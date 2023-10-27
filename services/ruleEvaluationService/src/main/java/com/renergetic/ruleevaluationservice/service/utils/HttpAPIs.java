package com.renergetic.ruleevaluationservice.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HttpAPIs {

    public HttpResponse<String> sendRequest(String url, String httpMethod, Map<String, String> params,
                                                   Object body, Map<String, String> headers) {
        try {
            // Create client
            HttpClient client = HttpClient.newHttpClient();
            // Prepare URL with params
            String parseParams = mapParams(params);
            parseParams = parseParams.isBlank() ? parseParams : '?' + parseParams;
            URI uri = URI.create(url + parseParams);
            log.info(uri.toString());
            // Prepare Request
            ObjectMapper mapper = new ObjectMapper();
            HttpRequest.Builder builder = HttpRequest.newBuilder(uri);

            if (headers != null && headers.size() > 0)
                for (Entry<String, String> header : headers.entrySet()) {
                    builder = builder.header(header.getKey(), header.getValue());
                }
            if (body == null)
                builder.method(httpMethod, HttpRequest.BodyPublishers.noBody());
            else
                builder.method(httpMethod, HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)));
            // Send request
            return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        } catch (IllegalArgumentException e) {
            log.error("Check that the URL is valid and  the HTTP Method is allowed");
        } catch (JsonProcessingException e) {
            log.error("Can't parse body to a JSON");
        } catch (InterruptedException e) {
            log.error("The connection with server has been interrupted");
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            log.error("Can't get connection with the URL");
        } catch (Exception e) {
        	log.error("Unknow exception");
        	e.printStackTrace();
        }
        return null;
    }

    public String mapParams(Map<String, String> params) {
        StringBuilder result = new StringBuilder();

        if (params != null)
            for (Entry<String, String> entry : params.entrySet()) {
                try {
                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    result.append("&");
                } catch (UnsupportedEncodingException e) {
                    log.error("Parameter with key " + entry.getKey() + " can't be encoded to UTF8");
                }
            }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

}
