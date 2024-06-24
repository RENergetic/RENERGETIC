package com.renergetic.kubeflowapi.service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.renergetic.common.dao.PipelineDefinitionDAO;
import com.renergetic.common.dao.PipelineParameterDAO;
import com.renergetic.common.dao.PipelineRunDAO;
import com.renergetic.common.utilities.DateConverter;
import com.renergetic.kubeflowapi.dao.ApiRunPostDAO;
import com.renergetic.kubeflowapi.model.*;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.renergetic.kubeflowapi.service.utils.KubeflowUtils;
import com.renergetic.common.utilities.Json;

@Service
public class KubeflowService {
    //#region properties
    @Value("${kubeflow.user.name}")
    private String kubeflowUsername;

    @Value("${kubeflow.user.password}")
    private String kubeflowPassword;
    @Value("${kubeflow.namespace}")
    private String kubeflowNamespace;
    @Value("${kubeflow.experiment.id}")
    private String kubeflowExperimentId;
    @Value("${kubeflow.url}")
    private String kubeflowUrl;
    @Value("${kubeflow.host}")
    private String kubeflowHost;
//#endregion

    //#region kubeflowmethods
    public String getListPipelines(String cookie) {
        String urlString = kubeflowUrl + "/pipeline/apis/v1beta1/pipelines";
        String httpsMethod = "GET";
        Object body = null;
        HashMap params = new HashMap<>();
        HashMap headers = new HashMap<>();

        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflat, br");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Cookie", cookie);
        headers.put("Host", kubeflowHost);

        //params.put("page_size", "10");
        params.put("sort_by", "created_at desc");

        //params.put("filter", "");

        return KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, headers).getResponseBody();
    }

    public String getListRuns(String cookie) {
        String urlString = kubeflowUrl + "/pipeline/apis/v1beta1/runs";
        String httpsMethod = "GET";
        Object body = null;
        HashMap params = new HashMap<>();
        HashMap headers = new HashMap<>();

        params.put("page_size", "10");
        params.put("resource_reference_key.type", "NAMESPACE");
        params.put("resource_reference_key.id", kubeflowNamespace);
        params.put("sort_by", "created_at desc");
//        params.put("resource_reference_key.id", "kubeflow-renergetic");
        params.put("filter",
                "{\"predicates\":[{\"key\":\"storage_state\",\"op\":\"NOT_EQUALS\",\"string_value\":\"STORAGESTATE_ARCHIVED\"}]}");

        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflat, br");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Cookie", cookie);
        headers.put("Host", "kubeflow.test.pcss.pl");

        return KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, headers).getResponseBody();
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


    //old get cookie
    public String getCookie2(String user, String password) {

        String homeUrl = kubeflowUrl.endsWith("/") ? kubeflowUrl : kubeflowUrl + "/";// "https://kubeflow.test.pcss.pl/";
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
        headers.put("Host", kubeflowHost);

        // Step 1 Obtain state value WORKING
        response = KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, headers);
        stateValue = KubeflowUtils.getState(response.getResponseBody());
//        System.out.println("***************************");
//        System.out.println("***************************");
        System.out.println("STEP 1:");
        System.out.println("Code: " + response.getResponseCode());
//        System.out.println("Body: " + response.getResponseBody());
//        System.out.println("Headers: " + response.getResponseHeaders());
//        System.out.println("");
        System.out.println("State value: " + stateValue);
//        System.out.println("");
//        System.out.println("***************************");
//        System.out.println("***************************");

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

        response = KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, headers);
//        System.out.println("***************************");
//        System.out.println("***************************");
        System.out.println("STEP 2:");
        System.out.println("Code: " + response.getResponseCode());
//        System.out.println("Body: " + response.getResponseBody());
//        System.out.println("Headers: " + response.getResponseHeaders());
//        System.out.println("***************************");
//        System.out.println("***************************");

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

        response = KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, headers);
        stateValue = KubeflowUtils.getState(response.getResponseBody());
//        System.out.println("***************************");
//        System.out.println("***************************");
        System.out.println("STEP 3:");
        System.out.println("Code: " + response.getResponseCode());
//        System.out.println("Body: " + response.getResponseBody());
//        System.out.println("Headers: " + response.getResponseHeaders());
//        System.out.println("");
        System.out.println("State: " + stateValue);
//        System.out.println("");
//        System.out.println("***************************");
//        System.out.println("***************************");

        // Step 4
        urlString = homeUrl + "dex/auth/ldap/login";
        httpsMethod = "POST";
        body = "login=" + user + "&password=" + password;

        headers.clear();
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "deflate, br, zstd");
         headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Host", "kubeflow.test.pcss.pl");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
//        headers.put("Content-Length", "52");

        params.clear();
        params.put("state", stateValue);
        params.put("back", "");

        response = KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, headers);
//        System.out.println("***************************");
//        System.out.println("***************************");
        System.out.println("STEP 4:");
        System.out.println("Code: " + response.getResponseCode());
//        System.out.println("Body: " + response.getResponseBody());
//        System.out.println("Headers: " + response.getResponseHeaders());
//        System.out.println("ResponseHeaders");
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

        response = KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, headers);
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
                    code = KubeflowUtils.extractParamValue(value, "code=", "&");
                    stateValue = KubeflowUtils.extractParamValue(value, "state=", "&");
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

        response = KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, headers);
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
                    cookie = "authservice_session=" + KubeflowUtils.extractParamValue(value, "authservice_session=",
                            ";");
                    stateValue = KubeflowUtils.extractParamValue(value, "Expires=", ";");
                }
            }
            System.out.println();
        }
        System.out.println("Cookie: " + cookie);
        System.out.println("Expiration: " + stateValue);
        return cookie;
    }

//#endregion

    //#region renergetic kubeflowmethods
     public HashMap<String, PipelineDefinitionDAO> getPipelines() throws ParseException {
        String urlString = kubeflowUrl + "/pipeline/apis/v1beta1/pipelines";
        String httpsMethod = "GET";
        Object body = null;
        HashMap<String, String> params = new HashMap<>();
        HashMap headers = this.initHeaders();
        //params.put("page_size", "10");
        params.put("sort_by", "created_at desc");
        //params.put("filter", "");

        String pipelinesJSON =
                KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, headers).getResponseBody();
        var pipelineObj = Json.parse(pipelinesJSON);
        JSONArray pipelinesArr = pipelineObj.getJSONArray("pipelines");
        HashMap<String, PipelineDefinitionDAO> pipelineMap = new HashMap<>();
        for (int i = 0; i < pipelinesArr.length(); i++) {
            // Get the current object
            JSONObject obj = pipelinesArr.getJSONObject(i);
            var dao = this.parsePipelineObj(obj);

            pipelineMap.put(dao.getPipelineId(), dao);
        }
        return pipelineMap;
    }

    public PipelineDefinitionDAO getPipeline(String id) throws ParseException {
        String urlString = kubeflowUrl + "/pipeline/apis/v1beta1/pipelines/" + id;
        String httpsMethod = "GET";
        Object body = null;
        HashMap<String, String> params = new HashMap<>();
        HashMap headers = this.initHeaders();

        String pipelinesJSON =
                KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, headers).getResponseBody();
        var pipelineObj = Json.parse(pipelinesJSON);
        var dao = this.parsePipelineObj(pipelineObj);
        return dao;
    }

    public void stopRun(String id) {
        String urlString = kubeflowUrl + "/pipeline/apis/v1beta1/runs/" + id + "/terminate";
        ;
        String httpsMethod = "POST";
        HashMap headers = this.initHeaders();
        KubeflowUtils.sendRequest(urlString, httpsMethod, null, null, headers).getResponseBody();

    }

    public PipelineRunDAO getRun(String id) {
        String urlString = kubeflowUrl + "/pipeline/apis/v1beta1/runs/" + id;
        String httpsMethod = "GET";
        HashMap headers = this.initHeaders();
        String pipelinesJSON =
                KubeflowUtils.sendRequest(urlString, httpsMethod, null, null, headers).getResponseBody();

        PipelineRunDAO runObj = null;
        try {
            runObj = this.parseRunObj(Json.parse(pipelinesJSON));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return runObj;
    }

    public PipelineRunDAO runPipeline(String id, Map<String, Object> inputParams) {
        String urlString = kubeflowUrl + "/pipeline/apis/v1beta1/runs";
        String httpsMethod = "POST";
//        HashMap params = new HashMap<>();
//        params.put("resource_reference_key.type", "NAMESPACE");
//        params.put("resource_reference_key.id", kubeflowNamespace);

        HashMap headers = initHeaders();
        List<KeyValue> parameters =
                inputParams.entrySet().stream().filter(it -> it.getValue() != null).map(
                        it -> new KeyValue(it.getKey(), it.getValue().toString())).collect(
                        Collectors.toList());
        PipelineSpec pipeSpec = new PipelineSpec(id, null, null, null, parameters);
        PipelineDefinitionDAO pipeline;
        try {
            pipeline = this.getPipeline(id);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        List<ApiResourceReference> resourceReference = new ArrayList<>();
        ApiResourceKey experiment = new ApiResourceKey(kubeflowExperimentId, "EXPERIMENT");
        resourceReference.add(new ApiResourceReference(null, experiment, "", "OWNER"));
        var pipelineVersion = new ApiResourceKey(pipeline.getVersion(), "PIPELINE_VERSION");
        resourceReference.add(new ApiResourceReference(null, pipelineVersion, "", "CREATOR"));
        ApiRunPostDAO body =
                new ApiRunPostDAO(pipeline.getDescription(), pipeline.getName(), pipeSpec, resourceReference,
                        ""); //TODO: RESOURCE_REFERENCES
//        headers.put("Referer", "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/");

        var resp = KubeflowUtils.sendRequest(urlString, httpsMethod, null, body, headers);
        try {
            var obj = Json.parse(resp.getResponseBody());
            var dao = this.parseRunObj(obj);
            return dao;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    private PipelineDefinitionDAO parsePipelineObj(JSONObject obj) {
        var dao = new PipelineDefinitionDAO();
        dao.setPipelineId(obj.getString("id"));
        dao.setName(obj.getString("name"));
        try {
            dao.setDescription(obj.getString("description"));
        } catch (org.json.JSONException ex) {
            dao.setDescription(null);
        }
        HashMap<String, PipelineParameterDAO> paramsMap = new HashMap<>();
        JSONArray paramArr;
        try {
            paramArr = obj.getJSONArray("parameters");
        } catch (org.json.JSONException ex) {
            paramArr = null;
        }

        var defaultVersion = obj.getJSONObject("default_version").getString("id");
        dao.setVersion(defaultVersion);
        if (paramArr != null) {
            for (int j = 0; j < paramArr.length(); j++) {
                var param = paramArr.getJSONObject(j);
                var paramDAO = new PipelineParameterDAO();
                paramDAO.setKey(param.getString("name"));
                paramDAO.setType("string");
                paramDAO.setDefaultValue(param.optString("value", null));
                paramsMap.put(paramDAO.getKey(), paramDAO);
            }
            dao.setParameters(paramsMap);
        }
        return dao;
    }

    private PipelineRunDAO parseRunObj(JSONObject obj) {
        JSONObject runObj = obj.getJSONObject("run");
        PipelineRunDAO dao = new PipelineRunDAO();
        dao.setRunId(runObj.getString("id"));
        try {
            dao.setState(runObj.getString("status"));
        } catch (Exception ex) {

        }
        try {
            var init = DateConverter.toEpoch(Date.from(Instant.parse(runObj.getString("created_at"))));
            var start = DateConverter.toEpoch(Date.from(Instant.parse(runObj.getString("scheduled_at"))));
            var end = DateConverter.toEpoch(Date.from(Instant.parse(runObj.getString("finished_at"))));
            dao.setStartTime(start);
            if (end > 0)
                dao.setEndTime(end);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//todo: parse run manifest

        return dao;
    }

    //#endregion

    //#region initialization of the kubeflow metadata
    public String getCookie(String user, String password) {

        String homeUrl = kubeflowUrl.endsWith("/") ? kubeflowUrl : kubeflowUrl + "/";// "https://kubeflow.test.pcss.pl/";
        String urlString = homeUrl;
        String httpsMethod = "GET";
        Object body = null;
        HashMap params = new HashMap<>();
        HashMap headers = new HashMap<>();
        String stateValue = "";
        String code = "";
        HttpsResponseInfo response;
        String cookie = "";
//        headers.put("Accept", "*/*");
        // Step 1 Obtain state value WORKING
        response = KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, Collections.emptyMap());
        stateValue = KubeflowUtils.getState(response.getResponseBody());
        System.out.println("STEP 1:");
        System.out.println("Code: " + response.getResponseCode());
        System.out.println("State value: " + stateValue);

        // Step 2 Get info from home
        urlString = homeUrl + "dex/auth";
        params.clear();
        params.put("client_id", "kubeflow-oidc-authservice");
        params.put("redirect_uri", "/login/oidc");
        params.put("response_type", "code");
        params.put("scope", "profile+email+groups+openid");
        params.put("state", stateValue);

//        headers.clear();
//        headers.put("Accept", "*/*");
        response = KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, Collections.emptyMap());
        System.out.println("STEP 2:");
        System.out.println("Code: " + response.getResponseCode());

        // Step 3 Get real state
        urlString = homeUrl + "dex/auth/ldap";
//        headers.clear();
        headers.put("Accept", "*/*");
        params.clear();
        params.put("client_id", "kubeflow-oidc-authservice");
        params.put("redirect_uri", "/login/oidc");
        params.put("response_type", "code");
        params.put("scope", "profile email groups openid");
        params.put("state", stateValue);

        response = KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, Collections.emptyMap());
        stateValue = KubeflowUtils.getState(response.getResponseBody());
        System.out.println("STEP 3:");
        System.out.println("Code: " + response.getResponseCode());
        System.out.println("State: " + stateValue);

        // Step 4
        urlString = homeUrl + "dex/auth/ldap/login";
        httpsMethod = "POST";
        body = "login=" + user + "&password=" + password;

        headers.clear();
        headers.put("Accept", "*/*");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        params.clear();
        params.put("state", stateValue);
        params.put("back", "");

        response = KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, headers);
        System.out.println("STEP 4:");
        System.out.println("Code: " + response.getResponseCode());
        String reqValue = "";
        for (Map.Entry<String, List<String>> entry : response.getResponseHeaders().entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
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
        }
        System.out.println("Reqvalue -" + reqValue + "-");
        urlString = homeUrl + "dex/approval";
        httpsMethod = "GET";
        body = null;
        params.clear();
        params.put("req", reqValue);
//        headers.clear();
//        headers.put("Accept", "*/*");

        response = KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, Collections.emptyMap());
        System.out.println("STEP 5:");
        System.out.println("Code: " + response.getResponseCode());
        System.out.println("ResponseHeaders");
        for (Map.Entry<String, List<String>> entry : response.getResponseHeaders().entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
//            System.out.println("Key: " + key);
//            System.out.println("Values:");
            if (key != null && key.equals("location")) {
                for (String value : values) {
                    System.out.println("  - " + value);
                    code = KubeflowUtils.extractParamValue(value, "code=", "&");
                    stateValue = KubeflowUtils.extractParamValue(value, "state=", "&");
                }
            }
        }
        System.out.println("Location: " + code + " - " + stateValue);

        // Step 6
        urlString = homeUrl + "login/oidc";

        params.clear();
        params.put("code", code);
        params.put("state", stateValue);
//        headers.clear();
//        headers.put("Accept", "*/*");

        response = KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, Collections.emptyMap());
        System.out.println("STEP 6:");
        System.out.println("Code: " + response.getResponseCode());
        for (Map.Entry<String, List<String>> entry : response.getResponseHeaders().entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            System.out.println("Key: " + key);
            System.out.println("Values:");
            if (key != null && key.equals("set-cookie")) {
                for (String value : values) {
                    System.out.println("  - " + value);
                    cookie = "authservice_session=" + KubeflowUtils.extractParamValue(value, "authservice_session=",
                            ";");
                    stateValue = KubeflowUtils.extractParamValue(value, "Expires=", ";");
                }
            }
            System.out.println();
        }
        System.out.println("Cookie: " + cookie);
        System.out.println("Expiration: " + stateValue);
        return cookie;
    }

    private HashMap<String, String> initHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "*/*");
//        headers.put("Accept-Encoding", "gzip, deflat, br");
//        headers.put("Accept-Language", "en-US,en;q=0.9");
//        headers.put("Connection", "keep-alive");
        String cookie = this.getCookie(kubeflowUsername, kubeflowPassword);
        headers.put("Cookie", cookie);
//        headers.put("Host", kubeflowHost);
        return headers;

    }


//#endregion
}
