package com.renergetic.kubeflowapi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.renergetic.kubeflowapi.model.HttpsResponseInfo;
import com.renergetic.kubeflowapi.service.utils.KubeflowUtils;
import com.renergetic.common.utilities.Json;

@Service
public class KubeflowService {
    
    public String getListPipelines(String cookie) {
        String urlString = "https://kubeflow.test.pcss.pl/pipeline/apis/v1beta1/pipelines";
        String httpsMethod = "GET";
        Object body = null;
        HashMap params = new HashMap<>();
        HashMap headers = new HashMap<>();
        KubeflowUtils utils = new KubeflowUtils();

        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflat, br");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Cookie", cookie);
        headers.put("Host", "kubeflow.test.pcss.pl");

        //params.put("page_size", "10");
        params.put("sort_by", "created_at desc");
        //params.put("filter", "");

        return utils.sendRequest(urlString, httpsMethod, params, body, headers).getResponseBody();
    }

    public String getListRuns(String cookie) {
        String urlString = "https://kubeflow.test.pcss.pl/pipeline/apis/v1beta1/runs";
        String httpsMethod = "GET";
        Object body = null;
        HashMap params = new HashMap<>();
        HashMap headers = new HashMap<>();
        // String cookie =
        // "authservice_session=MTcwNzEzODcwMXxOd3dBTkVVeU5sTkVTRlZXVjB0RVNsbFRWMDlWTjBGSlJrNDJOVE0xUTBZMU5GVkVVVWRRU0U1RU5rdEpOVFZNUlRWTFFVb3lUVkU9fM3w1NRjgY4XWImclLUj224bdgiA63MFavHwt2e1C61c";
        //
        KubeflowUtils utils = new KubeflowUtils();

        params.put("page_size", "10");
        params.put("resource_reference_key.type", "NAMESPACE");
        params.put("sort_by", "created_at desc");
        params.put("resource_reference_key.id", "kubeflow-renergetic");
        params.put("filter","{\"predicates\":[{\"key\":\"storage_state\",\"op\":\"NOT_EQUALS\",\"string_value\":\"STORAGESTATE_ARCHIVED\"}]}");

        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflat, br");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Cookie", cookie);
        headers.put("Host", "kubeflow.test.pcss.pl");

        return utils.sendRequest(urlString, httpsMethod, params, body, headers).getResponseBody();
    }
    
    public String getRunsFromPipeline(String cookie) {
        return getListPipelines(cookie);
    }

    
    public List<String> getListIDRuns(String cookie) {
        String listPipelines = getListRuns(cookie);
        List<String> idPipelines = new ArrayList<>(); //no sirve porque necesito idrun, idpipeline, fecha
        try {
            JSONObject jsonObject = Json.parse(listPipelines);
            JSONArray objectsArray = jsonObject.getJSONArray("pipelines");
            for (int i = 0; i < objectsArray.length(); i++) {
                // Get the current object
                JSONObject currentObject = objectsArray.getJSONObject(i);
                idPipelines.add(currentObject.get("id").toString());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return idPipelines;
    }
    
    public String getCookie(String user, String password) {
        
        KubeflowUtils utils = new KubeflowUtils();
        String homeUrl = "https://kubeflow.test.pcss.pl/";
        String urlString = homeUrl;
        String httpsMethod = "GET";
        Object body = null;
        HashMap params = new HashMap<>();
        HashMap headers = new HashMap<>();
        String stateValue = "";
        String code = "";
        HttpsResponseInfo response;
        String cookie = "";

        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate, br, zstd");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "kubeflow.test.pcss.pl");

        // Step 1 Obtain state value WORKING
        response = utils.sendRequest(urlString, httpsMethod, params, body, headers);
        stateValue = utils.getState(response.getResponseBody());
        System.out.println("***************************");
        System.out.println("***************************");
        System.out.println("STEP 1:");
        System.out.println("Code: " + response.getResponseCode());
        System.out.println("Body: " + response.getResponseBody());
        System.out.println("Headers: " + response.getResponseHeaders());
        System.out.println("");
        System.out.println("State value: " + stateValue);
        System.out.println("");
        System.out.println("***************************");
        System.out.println("***************************");

        // Step 2 Get info from home
        urlString = homeUrl + "dex/auth";
        params.clear();
        params.put("client_id", "kubeflow-oidc-authservice");
        params.put("redirect_uri", "/login/oidc");
        params.put("response_type", "code");
        params.put("scope", "profile+email+groups+openid");
        params.put("state", stateValue);

        headers.clear();
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "deflate, br, zstd");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "kubeflow.test.pcss.pl");

        response = utils.sendRequest(urlString, httpsMethod, params, body, headers);
        System.out.println("***************************");
        System.out.println("***************************");
        System.out.println("STEP 2:");
        System.out.println("Code: " + response.getResponseCode());
        System.out.println("Body: " + response.getResponseBody());
        System.out.println("Headers: " + response.getResponseHeaders());
        System.out.println("***************************");
        System.out.println("***************************");

        // Step 3 Get real state
        urlString = homeUrl + "dex/auth/ldap";
        headers.clear();
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "kubeflow.test.pcss.pl");

        params.clear();
        params.put("client_id", "kubeflow-oidc-authservice");
        params.put("redirect_uri", "/login/oidc");
        params.put("response_type", "code");
        params.put("scope", "profile email groups openid");
        params.put("state", stateValue);

        response = utils.sendRequest(urlString, httpsMethod, params, body, headers);
        stateValue = utils.getState(response.getResponseBody());
        System.out.println("***************************");
        System.out.println("***************************");
        System.out.println("STEP 3:");
        System.out.println("Code: " + response.getResponseCode());
        System.out.println("Body: " + response.getResponseBody());
        System.out.println("Headers: " + response.getResponseHeaders());
        System.out.println("");
        System.out.println("State: " + stateValue);
        System.out.println("");
        System.out.println("***************************");
        System.out.println("***************************");

        // Step 4
        urlString = homeUrl + "dex/auth/ldap/login";
        httpsMethod = "POST";
        body = "login=" + user + "&password=" + password;

        headers.clear();
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "deflate, br, zstd");
        // headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "kubeflow.test.pcss.pl");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Content-Length", "52");

        params.clear();
        params.put("state", stateValue);
        params.put("back", "");

        response = utils.sendRequest(urlString, httpsMethod, params, body, headers);
        System.out.println("***************************");
        System.out.println("***************************");
        System.out.println("STEP 4:");
        System.out.println("Code: " + response.getResponseCode());
        System.out.println("Body: " + response.getResponseBody());
        System.out.println("Headers: " + response.getResponseHeaders());
        System.out.println("ResponseHeaders");
        String reqValue = "";
        for (Map.Entry<String, List<String>> entry : response.getResponseHeaders().entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            System.out.println("Key -" + key + "-");
            System.out.println("Values:");
            System.out.println("Test");
            if (key != null && key.equals("location")) {
                for (String value : values) {
                    System.out.println("  - " + value);
                    int reqIndex = value.indexOf("req=");
                    if (reqIndex != -1) {
                        // Get the substring after "req="
                        reqValue = value.substring(reqIndex + 4);
                        System.out.println("Value after req=: " + reqValue);
                    } else {
                        System.out.println("String does not contain 'req='.");
                    }
                }
            }
            System.out.println();
        }
        System.out.println("Reqvalue -" + reqValue + "-");
        System.out.println();
        System.out.println("***************************");
        System.out.println("***************************");

        // Step 5
        urlString = homeUrl + "dex/approval";
        httpsMethod = "GET";
        body = null;

        params.clear();
        params.put("req", reqValue);

        headers.clear();
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "deflate");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "kubeflow.test.pcss.pl");

        response = utils.sendRequest(urlString, httpsMethod, params, body, headers);
        System.out.println("***************************");
        System.out.println("***************************");
        System.out.println("STEP 5:");
        System.out.println("Code: " + response.getResponseCode());
        System.out.println("Body: " + response.getResponseBody());
        System.out.println("Headers: " + response.getResponseHeaders());
        System.out.println("ResponseHeaders");
        for (Map.Entry<String, List<String>> entry : response.getResponseHeaders().entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            System.out.println("Key: " + key);
            System.out.println("Values:");
            if (key != null && key.equals("location")) {
                for (String value : values) {
                    System.out.println("  - " + value);
                    code = utils.extractParamValue(value, "code=", "&");
                    stateValue = utils.extractParamValue(value, "state=", "&");
                }
            }
            System.out.println();
        }
        System.out.println("Location: " + code + " - " + stateValue);

        // Step 6
        urlString = homeUrl + "login/oidc";

        params.clear();
        params.put("code", code);
        params.put("state", stateValue);

        headers.clear();
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "deflate, br, zstd");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "kubeflow.test.pcss.pl");

        response = utils.sendRequest(urlString, httpsMethod, params, body, headers);
        System.out.println("***************************");
        System.out.println("***************************");
        System.out.println("STEP 6:");
        System.out.println("Code: " + response.getResponseCode());
        System.out.println("Body: " + response.getResponseBody());
        System.out.println("Headers: " + response.getResponseHeaders());
        for (Map.Entry<String, List<String>> entry : response.getResponseHeaders().entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            System.out.println("Key: " + key);
            System.out.println("Values:");
            if (key != null && key.equals("set-cookie")) {
                for (String value : values) {
                    System.out.println("  - " + value);
                    cookie = "authservice_session=" + utils.extractParamValue(value, "authservice_session=", ";");
                    stateValue = utils.extractParamValue(value, "Expires=", ";");
                }
            }
            System.out.println();
        }
        System.out.println("Cookie: " + cookie);
        System.out.println("Expiration: " + stateValue);
        return cookie;
    }
}
