package com.renergetic.kubeflowapi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.renergetic.common.utilities.Json;
import com.renergetic.kubeflowapi.service.utils.KubeflowUtils;

@Service
public class KubeflowService {
    
    public String getListPipelines(String cookie) {
        String urlString = "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/apis/v1beta1/pipelines";
        String httpsMethod = "GET";
        Object body = null;
        HashMap params = new HashMap<>();
        HashMap headers = new HashMap<>();
        //String cookie = "authservice_session=MTcwNzEzODcwMXxOd3dBTkVVeU5sTkVTRlZXVjB0RVNsbFRWMDlWTjBGSlJrNDJOVE0xUTBZMU5GVkVVVWRRU0U1RU5rdEpOVFZNUlRWTFFVb3lUVkU9fM3w1NRjgY4XWImclLUj224bdgiA63MFavHwt2e1C61c";
        //
        KubeflowUtils utils = new KubeflowUtils();

        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflat, br");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Cookie", cookie);
        headers.put("Host", "kubeflow.apps.dcw1-test.paas.psnc.pl");
        headers.put("Referer", "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/");
        return utils.sendRequest(urlString, httpsMethod, params, body, headers);
    }

    public String getListRuns(String cookie) {
        String urlString = "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/apis/v1beta1/runs";
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
        headers.put("Host", "kubeflow.apps.dcw1-test.paas.psnc.pl");
        headers.put("Referer", "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/");

        return utils.sendRequest(urlString, httpsMethod, params, body, headers);
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
}
