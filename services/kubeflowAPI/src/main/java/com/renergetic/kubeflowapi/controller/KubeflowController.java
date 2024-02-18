package com.renergetic.kubeflowapi.controller;

import com.renergetic.common.utilities.HttpAPIs;
import com.renergetic.common.utilities.Json;
import com.renergetic.kubeflowapi.dao.ExampleRequest;
import com.renergetic.kubeflowapi.dao.ExampleResponse;
import com.renergetic.kubeflowapi.dao.tempcommon.WorkflowDefinitionDAO;
import com.renergetic.kubeflowapi.dao.tempcommon.WorkflowRunDAO;
import com.renergetic.kubeflowapi.service.ExampleService;
import com.renergetic.kubeflowapi.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Tag(name = "Kubeflow Controller", description = "Manage the connection to Kubeflow API")
@RequestMapping("/api/kubeflow")
public class KubeflowController {
    @Value("${api.generate.dummy-data}")
    private Boolean generateDummy;
    @Autowired
    private ExampleService exampleService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private HttpAPIs apis;

    /**
     * Returns the data from the list of pipelines in the Kubeflow API
     *
     * @return The response entity with a null value.
     */
    @GetMapping("/pipelines")
    public ResponseEntity<?> test() {
        //Initial data
        String urlString = "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/apis/v1beta1/pipelines";
        String httpsMethod = "";
        HashMap params = new HashMap<>();
        Object body = null;
        HashMap headers = new HashMap<>();
        String cookie =
                "authservice_session=MTcwNTA1Njc2OHxOd3dBTkU1U1MwOVdXVk5IUWxkRVZVTkhUMWhXTjBWVlIwRTNVMUExVURSRk1qWkVNbHBZVmxCRE5UTllXVkUxVUVkR1RrNVFSVkU9fH6RL1nwdX7jfD98Q3WqaQwlZ4sJx-xZmSA-50-K5ioH";
        //
        StringBuilder response = new StringBuilder();
        String jsonResponse = "";
        try {
            // Specify the URL you want to connect to
            URL url = new URL(urlString);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Set the request method (GET, POST, etc.)
            connection.setRequestMethod("GET");

            // Set request headers (if needed)
            connection.setRequestProperty("Accept", "*/*");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflat, br");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Cookie", cookie);
            connection.setRequestProperty("Host", "kubeflow.apps.dcw1-test.paas.psnc.pl");
            connection.setRequestProperty("Referer", "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/");

            // Enable input/output streams for reading/writing data
            connection.setDoOutput(true);

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Read the response from the server
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("Response Code: " + responseCode);
                System.out.println("Response Body: " + response.toString());
            }

            System.out.println(Json.toJson(response.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return new ResponseEntity<>(response.toString(), HttpStatus.OK);
        return new ResponseEntity<>(response.toString(), HttpStatus.OK);

    }

    @Operation(summary = "Get all examples saved on the repository")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "", produces = "application/json")
    public ResponseEntity<List<ExampleResponse>> getAllUsers(@RequestParam(required = false) Optional<Integer> offset,
                                                             @RequestParam(required = false) Optional<Integer> limit) {
        return ResponseEntity.ok(exampleService.get(offset.orElse(0), limit.orElse(1000)));
    }

    // Endpoint to save ExampleRequest to the repository
    @Operation(summary = "Save an example")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "", produces = "application/json")
    public ResponseEntity<ExampleResponse> saveExample(@RequestBody ExampleRequest example) {
        return ResponseEntity.ok(exampleService.create(example));
    }

    @Operation(summary = "Update an example")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PutMapping(path = "{id}", produces = "application/json")
    public ResponseEntity<ExampleResponse> updateExample(
            @PathVariable("id") Long id,
            @RequestBody ExampleRequest example) {
        return ResponseEntity.ok(exampleService.update(id, example));
    }

    @Operation(summary = "Delete an example")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @DeleteMapping(path = "", produces = "application/json")
    public ResponseEntity<Void> deleteExample(@RequestParam Long id) {
        exampleService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get All workflow for non-admin users")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/workflow", produces = "application/json")
    public ResponseEntity<List<WorkflowDefinitionDAO>> listAll() {
        List<WorkflowDefinitionDAO> res = workflowService.getAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Get All workflow")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/admin/workflow", produces = "application/json")
    public ResponseEntity<List<WorkflowDefinitionDAO>> listAll(
            @RequestParam(required = false) Optional<Boolean> visible) {
        //TODO: verify admin roles
        List<WorkflowDefinitionDAO> res = workflowService.getAllAdmin(visible);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Get experiment run")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/workflow/{experiment_id}/run", produces = "application/json")
    public ResponseEntity<WorkflowRunDAO> getExperimentRun(
            @PathVariable(name = "experiment_id") String experimentId) {

        WorkflowRunDAO res = workflowService.getRun(experimentId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    @Operation(summary = "Start experiment run")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "/workflow/{experiment_id}/run", produces = "application/json")
    public ResponseEntity<WorkflowRunDAO> startExperimentRun(
            @PathVariable(name = "experiment_id") String experimentId,@RequestBody Map<String,Object> params) {

        WorkflowRunDAO res = workflowService.startRun(experimentId,params);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    @Operation(summary = "Stop experiment run")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @DeleteMapping(path = "/workflow/{experiment_id}/run", produces = "application/json")
    public ResponseEntity<Boolean> startExperimentRun(
            @PathVariable(name = "experiment_id") String experimentId ) {

        Boolean res = workflowService.stopRun(experimentId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Set visibility")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PutMapping(path = "/admin/workflow/{experiment_id}/visibility", produces = "application/json")
    public ResponseEntity<Boolean> setVisibility(@PathVariable(name = "experiment_id") String experimentId) {
        //TODO: verify admin roles
        Boolean res = workflowService.setVisibility(experimentId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Remove visibility")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @DeleteMapping(path = "/admin/workflow/{experiment_id}/visibility", produces = "application/json")
    public ResponseEntity<Boolean> removeVisibility(@PathVariable(name = "experiment_id") String experimentId) {
        //TODO: verify admin roles
        Boolean res = workflowService.removeVisibility(experimentId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
