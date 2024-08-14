package com.renergetic.kubeflowapi.controller;

import com.renergetic.common.dao.PipelineDefinitionDAO;
import com.renergetic.common.dao.PipelineDefinitionPropertyDAO;
import com.renergetic.common.dao.PipelineParameterDAO;
import com.renergetic.common.dao.PipelineRunDAO;
import com.renergetic.common.utilities.Json;
import com.renergetic.kubeflowapi.service.KubeflowService;
import com.renergetic.kubeflowapi.service.KubeflowPipelineService;
import com.renergetic.kubeflowapi.service.utils.KubeflowUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@Tag(name = "Kubeflow Controller", description = "Manage the connection to Kubeflow API")
@RequestMapping("/api/kubeflow")
public class KubeflowController {
    //#region fields

    @Value("${kubeflow.user.name}")
    private String kubeflowUsername;

    @Value("${kubeflow.user.password}")
    private String kubeflowPassword;

    @Autowired
    private KubeflowService kubeflowService;

    @Autowired
    private KubeflowPipelineService kubeflowPipelineService;

    @Value("${kubeflow.url}")
    String homeUrl = "https://kubeflow.test.pcss.pl/";

    //#endregion
    private String cookie;

    //#region kubeflow integration

    @Operation(summary = "Get all pipelines from kubeflow")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping("/pipelines")
    public ResponseEntity<?> getAllPipelines() {
        cookie = kubeflowService.getCookie(kubeflowUsername, kubeflowPassword);
        return new ResponseEntity<>(kubeflowService.getListPipelines(cookie), HttpStatus.OK);
    }


    @Operation(summary = "Get all runs from kubeflow")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/runs", produces = "application/json")
    public ResponseEntity<?> getAllRuns() {
        cookie = kubeflowService.getCookie(kubeflowUsername, kubeflowPassword);
        return new ResponseEntity<>(kubeflowService.getListRuns(cookie), HttpStatus.OK);
    }


    @Operation(summary = "Last time a pipeline was run")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/pipeline/{id}/lastrun", produces = "application/json")
    public ResponseEntity<?> lastRunPipeline(@PathVariable("id") Long id, @PathVariable String cookie) {
        //return new ResponseEntity<>(kubeflowService.getListIDPipelines(cookie), HttpStatus.OK);
        cookie = kubeflowService.getCookie(kubeflowUsername, kubeflowPassword);
        return new ResponseEntity<>(kubeflowService.getListRuns(cookie), HttpStatus.OK);
    }

    @Operation(summary = "List of runs with state of the runs")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/runs/state", produces = "application/json")
    public ResponseEntity<?> runStateList() {
        String response = kubeflowService.getListRuns(cookie);
        cookie = kubeflowService.getCookie(kubeflowUsername, kubeflowPassword);
        try {
            JSONObject jsonResponse = Json.parse(response);
            jsonResponse.remove("total_size");
            jsonResponse.remove("next_page_token");
            JSONArray objectsArray = jsonResponse.getJSONArray("runs");
            for (int i = 0; i < objectsArray.length(); i++) {
                // Get the current object
                JSONObject currentObject = objectsArray.getJSONObject(i);

                // Remove the parameter "param_to_delete" from the current object
                currentObject.remove("created_at");
                currentObject.remove("resource_references");
                currentObject.remove("service_account");
                currentObject.remove("scheduled_at");
                currentObject.remove("finished_at");
                currentObject.remove("pipeline_spec");
            }
            response = jsonResponse.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //#endregion

    //#region Renergetic API

    //#region non-admin
    @Operation(summary = "Get All pipelines for non-admin users")
    // GET ALL PIPELINES/RUNS IT ALREADY EXISTS (ONLY FOR NON ADMINS)
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/pipeline", produces = "application/json")
    public ResponseEntity<List<PipelineDefinitionDAO>> listAll() {
        List<PipelineDefinitionDAO> res = kubeflowPipelineService.getAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @Operation(summary = "Get pipeline current/recent run") //GET RUN
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/pipeline/{pipeline_id}/run", produces = "application/json")
    public ResponseEntity<PipelineRunDAO> getExperimentRun(
            @PathVariable(name = "pipeline_id") String pipelineId) {
        PipelineRunDAO res = kubeflowPipelineService.getRun(pipelineId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Start pipeline") //RUN PIPELINE
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "/pipeline/{pipeline_id}/run", produces = "application/json")
    public ResponseEntity<PipelineRunDAO> startPipeline(
            @PathVariable(name = "pipeline_id") String pipelineId, @RequestBody Map<String, Object> params) {

        PipelineRunDAO res = kubeflowPipelineService.startRun(pipelineId, params);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Stop run") //STOP PIPELINE
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @DeleteMapping(path = "/pipeline/{pipeline_id}/run", produces = "application/json")
    public ResponseEntity<Boolean> stopRun(
            @PathVariable(name = "pipeline_id") String pipelineId) {

        Boolean res = kubeflowPipelineService.stopRun(pipelineId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    @Operation(summary = "Get pipeline runs") //GET RUN
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/pipeline/{pipeline_id}/runs", produces = "application/json")
    public ResponseEntity<List<PipelineRunDAO>>
    listPipelineRuns(@PathVariable(name = "pipeline_id") String pipelineId,
                     @RequestParam(value = "from", required = false) Long from,
                     @RequestParam(value = "to", required = false) Long to,
                     @RequestParam(name = "offset", required = false) Optional<Long> offset,
                     @RequestParam(name = "limit", required = false) Optional<Long> limit) {
        List<PipelineRunDAO> res =
                kubeflowPipelineService.listRuns(pipelineId, from, to, limit.orElse(100L), offset.orElse(0L));
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    //#endregion

    //#region admin
    @Operation(summary = "Get All pipelines") //GET ALL PIPELINES/RUNS IT ALREADY EXISTS (ONLY FOR ADMINS)
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/admin/pipeline", produces = "application/json")
    public ResponseEntity<List<PipelineDefinitionDAO>> listAll(
            @RequestParam(required = false) Optional<Boolean> visible) {
        //TODO: verify admin roles
        List<PipelineDefinitionDAO> res = kubeflowPipelineService.getAllAdmin(visible);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Set pipeline visibility in the UI")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PutMapping(path = "/admin/pipeline/{pipeline_id}/visibility", produces = "application/json")
    public ResponseEntity<Boolean> setVisibility(@PathVariable(name = "pipeline_id") String pipelineId) {
        //TODO: verify admin roles
        Boolean res = kubeflowPipelineService.setVisibility(pipelineId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Remove visibility")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @DeleteMapping(path = "/admin/pipeline/{pipeline_id}/visibility", produces = "application/json")
    public ResponseEntity<Boolean> removeVisibility(@PathVariable(name = "pipeline_id") String pipelineId) {
        //TODO: verify admin roles
        Boolean res = kubeflowPipelineService.removeVisibility(pipelineId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Set pipeline parameters metadata")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PutMapping(path = "/admin/pipeline/{pipeline_id}/parameters", produces = "application/json")
    public ResponseEntity<Map<String, PipelineParameterDAO>> setParameters(
            @PathVariable(name = "pipeline_id") String pipelineId, @RequestBody
    Map<String, PipelineParameterDAO> parameters) {
        //TODO: verify admin roles
        Map<String, PipelineParameterDAO> params = null;
        try {
            params = kubeflowPipelineService.setParameters(pipelineId, parameters);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(params, HttpStatus.OK);
    }
    //#endregion

    //#region properties

    @Operation(summary = "Get  pipeline definitions by property ")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/admin/pipeline/property/{key}/value/{value}", produces = "application/json")
    public ResponseEntity<List<PipelineDefinitionDAO>> getByProperty(@PathVariable(name = "key") String propertyKey,
                                                                     @PathVariable(name = "value") String propertyValue) {
        //TODO: verify admin roles
        var res = kubeflowPipelineService.getByProperty(propertyKey, propertyValue);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Update existing pipeline property ")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PutMapping(path = "/admin/pipeline/{pipeline_id}/property/{key}/value/{value}", produces = "application/json")
    public ResponseEntity<PipelineDefinitionPropertyDAO> setProperty(
            @PathVariable(name = "pipeline_id") String pipelineId,
            @PathVariable(name = "key") String propertyKey,
            @PathVariable(name = "value") String propertyValue,
            @RequestParam(required = false) Optional<Boolean> unique) {
        //TODO: verify admin roles
        var res = kubeflowPipelineService.setProperty(pipelineId, propertyKey, propertyValue, unique);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Set pipeline property")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "/admin/pipeline/{pipeline_id}/property", produces = "application/json")
    public ResponseEntity<PipelineDefinitionPropertyDAO> setProperty(@PathVariable(name = "pipeline_id") String pipelineId,
                                                                     @RequestParam(required = false) Optional<Boolean> unique,
                                                                     @RequestBody PipelineDefinitionPropertyDAO propertyDAO) {
        //TODO: verify admin roles
        var res = kubeflowPipelineService.setProperty(pipelineId, propertyDAO, unique);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "delete pipeline property  ")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @DeleteMapping(path = "/admin/pipeline/{pipeline_id}/property/{key}", produces = "application/json")
    public ResponseEntity<?> deleteProperty(@PathVariable(name = "pipeline_id") String pipelineId,
                                            @PathVariable(name = "key") String propertyKey) {
        //TODO: verify admin roles
        kubeflowPipelineService.deleteProperty(pipelineId, propertyKey);
        return ResponseEntity.ok().build();
    }
//#endregion


}

//    @Operation(summary = "Login user")
//    @ApiResponse(responseCode = "200", description = "Request executed correctly")
//    @GetMapping(path = "/login", produces = "application/json")
//    public ResponseEntity<?> logUser() {
//        cookie = kubeflowService.getCookie(kubeflowUsername, kubeflowPassword);
//        //return new ResponseEntity<>(stateValue + " -> " + body, HttpStatus.OK);
//        return new ResponseEntity<>(kubeflowService.getListPipelines(cookie), HttpStatus.OK);
//    }

//
//
//
//    // Endpoint to save ExampleRequest to the repository
//    @Operation(summary = "Save an example")
//    @ApiResponse(responseCode = "200", description = "Request executed correctly")
//    @PostMapping(path = "", produces = "application/json")
//    public ResponseEntity<ExampleResponse> saveExample(@RequestBody ExampleRequest example) {
//        return ResponseEntity.ok(exampleService.create(example));
//    }
//
//    @Operation(summary = "Update an example")
//    @ApiResponse(responseCode = "200", description = "Request executed correctly")
//    @PutMapping(path = "{id}", produces = "application/json")
//    public ResponseEntity<ExampleResponse> updateExample(
//            @PathVariable("id") Long id,
//            @RequestBody ExampleRequest example) {
//        return ResponseEntity.ok(exampleService.update(id, example));
//    }
//
//    @Operation(summary = "Delete an example")
//    @ApiResponse(responseCode = "200", description = "Request executed correctly")
//    @DeleteMapping(path = "", produces = "application/json")
//    public ResponseEntity<Void> deleteExample(@RequestParam Long id) {
//        exampleService.delete(id);
//        return ResponseEntity.ok().build();
//    }

// Old test messages

//TODO: INSERT AUTOMATIC HEADERS
//TODO: INSERT AUTOMATIC PARAMS
//TODO: IMPORT MOST OF THE FUCTIONS TO A SERVICE CLASS
//@Operation(summary = "Run dummy pipeline")
//@ApiResponse(responseCode = "200", description = "Request executed correctly")
//@PostMapping(path = "/pipelines/{id_pipeline}/run", produces = "application/json")
//public ResponseEntity<?> runDummy(@PathVariable String id_pipeline) { // cambiar PathVariable por requestparam
//    String urlString = homeUrl + "pipeline/apis/v1beta1/runs";
//    String httpsMethod = "POST";
//    HashMap params = new HashMap<>();
//    HashMap headers = new HashMap<>();
//    Json jsonUtil;
//    cookie = kubeflowService.getCookie(kubeflowUsername, kubeflowPassword);
//    List<KeyValue> parameters = new ArrayList<>();
//    parameters.add(new KeyValue("value_1", "1"));
//    parameters.add(new KeyValue("value_2", "b"));
//    parameters.add(new KeyValue("value_3", "8"));
//    //PipelineSpec pipeSpec = new PipelineSpec("5cda125a-2973-467b-b58b-b1c1cab712e6", "DETAULT NAME","WORLKFLOW MANIFEST", "PIPELINE MANIFEST", parameters);
//    PipelineSpec pipeSpec = new PipelineSpec(null, null, null, null, parameters);
//    List<ApiResourceReference> resourceReference = new ArrayList<>();
//    ApiResourceKey key = new ApiResourceKey("a8069584-de7f-4a31-9c1d-6d4a04ce66a8", "EXPERIMENT");
//    resourceReference.add(new ApiResourceReference(null, key, "", "OWNER"));
//    key = new ApiResourceKey("cd6edfd0-ed26-4c15-bb18-bdba5d5c99aa", "PIPELINE_VERSION");
//    resourceReference.add(new ApiResourceReference(null, key, "", "CREATOR"));
//    ApiRunPostDAO body = new ApiRunPostDAO("DummyTest4Description", "DummyTest4Name", pipeSpec, resourceReference,
//            ""); //TODO: RESOURCE_REFERENCES
//    headers.put("Accept", "*/*");
//    headers.put("Accept-Encoding", "gzip, deflat, br");
//    headers.put("Accept-Language", "en-US,en;q=0.9");
//    headers.put("Connection", "keep-alive");
//    headers.put("Cookie", cookie);
//    headers.put("Host", "kubeflow.apps.dcw1-test.paas.psnc.pl");
//    headers.put("Referer", "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/");
//
//    System.out.println("+++++++++++++++++++++++++++++++++++++");
//    System.out.println("++++++++++++++++++  JSON  ++++++++++++++++++");
//    System.out.println("+++++++++++++++++++++++++++++++++++++");
//    System.out.println(Json.toJson(body));
//    System.out.println("+++++++++++++++++++++++++++++++++++++");
//    System.out.println("++++++++++++++++++  BODY  ++++++++++++++++++");
//    System.out.println("+++++++++++++++++++++++++++++++++++++");
//    System.out.println(body);
//    String response = KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, headers).getResponseBody();
//    System.out.println("++++++++++++++++++  REQUEST  +++++++++++++++++++");
//    System.out.println(response);
//    System.out.println("Pipeline ID: " + id_pipeline);
//    System.out.println("+++++++++++++++++++++++++++++++++++++");
//    System.out.println("++++++++++++++++++  RESPONSE  ++++++++++++++++++");
//    System.out.println("+++++++++++++++++++++++++++++++++++++");
//    return new ResponseEntity<>(Json.toJson(body), HttpStatus.OK);
//}
//@Operation(summary = "Get all examples saved on the repository")
//@ApiResponse(responseCode = "200", description = "Request executed correctly")
//@GetMapping(path = "/example/user", produces = "application/json")
//public ResponseEntity<?> getMethodName() {
//    String urlString = homeUrl;
//    String httpsMethod = "GET";
//    Object body = null;
//    HashMap params = new HashMap<>();
//    HashMap headers = new HashMap<>();
//
//    headers.put("Accept", "*/*");
//    headers.put("Accept-Encoding", "gzip, deflat, br");
//    headers.put("Accept-Language", "en-US,en;q=0.9");
//    headers.put("Connection", "keep-alive");
//    headers.put("Host", "kubeflow.apps.dcw1-test.paas.psnc.pl");
//    headers.put("Referer", "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/");
//
//    String response = KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, headers).getResponseBody();
//    String[] lines = response.split("\n");
//    String stateValue = "";
//    for (String line : lines) {
//        if (line.contains("action=\"/dex/auth/local/login?back=&amp;state=")) {
//            int startIndex = line.indexOf("state=") + "state=".length();
//            int endIndex = line.indexOf("\"", startIndex);
//            stateValue = line.substring(startIndex, endIndex);
//            System.out.println("State value: " + stateValue);
//            break;
//        }
//    }
//
//    return new ResponseEntity<>(String.format("%s: %s", kubeflowUsername, kubeflowPassword), HttpStatus.OK);
//}    @Operation(summary = "List of experiments")
//    @ApiResponse(responseCode = "200", description = "Request executed correctly")
//    @GetMapping(path = "/experiments", produces = "application/json")
//    public ResponseEntity<?> experimentList() {
//        String urlString = homeUrl + "pipeline/apis/v1beta1/experiments";
//        String httpsMethod = "GET";
//        Object body = null;
//        HashMap params = new HashMap<>();
//        HashMap headers = new HashMap<>();
//
//        cookie = kubeflowService.getCookie(kubeflowUsername, kubeflowPassword);
//
//        params.put("page_size", "10");
//        params.put("resource_reference_key.type", "NAMESPACE");
//        params.put("resource_reference_key.id", "kubeflow-renergetic");
//        params.put("filter",
//                "{\"predicates\":[{\"key\":\"storage_state\",\"op\":\"NOT_EQUALS\",\"string_value\":\"STORAGESTATE_ARCHIVED\"}]}");
//
//        headers.put("Accept", "*/*");
//        headers.put("Accept-Encoding", "gzip, deflat, br");
//        headers.put("Accept-Language", "en-US,en;q=0.9");
//        headers.put("Connection", "keep-alive");
//        headers.put("Cookie", cookie);
//        headers.put("Host", "kubeflow.apps.dcw1-test.paas.psnc.pl");
//        headers.put("Referer", "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/");
//
//        return new ResponseEntity<>(KubeflowUtils.sendRequest(urlString, httpsMethod, params, body, headers).getResponseBody(),
//                HttpStatus.OK);
//    }
