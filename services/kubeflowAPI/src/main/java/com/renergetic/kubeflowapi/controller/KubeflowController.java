package com.renergetic.kubeflowapi.controller;

import com.renergetic.common.utilities.HttpAPIs;
import com.renergetic.common.utilities.Json;
import com.renergetic.kubeflowapi.dao.ApiRunPostDAO;
import com.renergetic.kubeflowapi.dao.ExampleRequest;
import com.renergetic.kubeflowapi.dao.ExampleResponse;
import com.renergetic.kubeflowapi.dao.tempcommon.WorkflowDefinitionDAO;
import com.renergetic.kubeflowapi.dao.tempcommon.WorkflowRunDAO;
import com.renergetic.kubeflowapi.model.*;
import com.renergetic.kubeflowapi.service.ExampleService;
import com.renergetic.kubeflowapi.service.KubeflowService;
import com.renergetic.kubeflowapi.service.WorkflowService;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@RestController
@Tag(name = "Kubeflow Controller", description = "Manage the connection to Kubeflow API")
@RequestMapping("/api/kubeflow")
public class KubeflowController {
    @Value("${api.generate.dummy-data}")
    private Boolean generateDummy;

	@Autowired
	private ExampleService exampleService;
	@Autowired
	private HttpAPIs apis;
	@Autowired
	private KubeflowService kubeflowService;
    @Autowired
    private WorkflowService workflowService;

	private String cookie; //= "authservice_session=MTcwODYwNDAzOXxOd3dBTkRVelRVSllWVlJVUTFwR1ZWUk1Wa3RSTmxCUFFrMVNWVXd5V2taUVEwMDFUak5JUzBSRVQwdFVORmRJTjBsSE5WVkhXVUU9fDi91bsE7zDKYFLkmh6D2uH6Coo6WpI_K9526oVRu9TN";


	// ********************************************************************************
	// ********************************************************************************
	// ********************************************************************************
	// KUBEFLOW INTEGRATION
	// ********************************************************************************
	// ********************************************************************************
	// ********************************************************************************

	@Operation(summary = "Get all pipelines from kubeflow")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping("/pipelines/{cookie}")
	public ResponseEntity<?> getAllPipelines(@PathVariable String cookie) {
		this.cookie = cookie;
		return new ResponseEntity<>(kubeflowService.getListPipelines(cookie), HttpStatus.OK);
	}

	@Operation(summary = "Get all runs from kubeflow")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/runs/{cookie}", produces = "application/json")
    public ResponseEntity<?> getAllRuns(@PathVariable String cookie) {
		this.cookie = cookie;
		return new ResponseEntity<>(kubeflowService.getListRuns(cookie), HttpStatus.OK);
    }

	@Operation(summary = "Run dummy pipeline")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@PostMapping(path = "/pipelines/{id_pipeline}/run/{cookie}", produces = "application/json")
	public ResponseEntity<?> runDummy(@PathVariable String id_pipeline, @PathVariable String cookie) { // cambiar PathVariable por requestparam
		String urlString = "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/apis/v1beta1/runs";
		String httpsMethod = "POST";
		HashMap params = new HashMap<>();
		HashMap headers = new HashMap<>();
		Json jsonUtil;
		this.cookie = cookie;
		List<KeyValue> parameters = new ArrayList<>();
		parameters.add(new KeyValue("value_1", "1"));
		parameters.add(new KeyValue("value_2","b"));
		parameters.add(new KeyValue("value_3", "8"));
		//PipelineSpec pipeSpec = new PipelineSpec("5cda125a-2973-467b-b58b-b1c1cab712e6", "DETAULT NAME","WORLKFLOW MANIFEST", "PIPELINE MANIFEST", parameters);
		PipelineSpec pipeSpec = new PipelineSpec(null, null,null, null, parameters);
		List<ApiResourceReference> resourceReference = new ArrayList<>();
		ApiResourceKey key = new ApiResourceKey("a8069584-de7f-4a31-9c1d-6d4a04ce66a8", "EXPERIMENT");
		resourceReference.add(new ApiResourceReference(key, "", "OWNER"));
		key = new ApiResourceKey("cd6edfd0-ed26-4c15-bb18-bdba5d5c99aa", "PIPELINE_VERSION");
		resourceReference.add(new ApiResourceReference(key, "", "CREATOR"));
		ApiRunPostDAO body = new ApiRunPostDAO("DummyTest4Description", "DummyTest4Name", pipeSpec, resourceReference, ""); //TODO: RESOURCE_REFERENCES
		headers.put("Accept", "*/*");
		headers.put("Accept-Encoding", "gzip, deflat, br");
		headers.put("Accept-Language", "en-US,en;q=0.9");
		headers.put("Connection", "keep-alive");
		headers.put("Cookie", cookie);
		headers.put("Host", "kubeflow.apps.dcw1-test.paas.psnc.pl");
		headers.put("Referer", "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/");

		KubeflowUtils utils = new KubeflowUtils();
		System.out.println("+++++++++++++++++++++++++++++++++++++");
		System.out.println("++++++++++++++++++  JSON  ++++++++++++++++++");
		System.out.println("+++++++++++++++++++++++++++++++++++++");
		System.out.println(Json.toJson(body));
		System.out.println("+++++++++++++++++++++++++++++++++++++");
		System.out.println("++++++++++++++++++  BODY  ++++++++++++++++++");
		System.out.println("+++++++++++++++++++++++++++++++++++++");
		System.out.println(body);
		String response = utils.sendRequest(urlString, httpsMethod, params, Json.toJson(body), headers);
		System.out.println("++++++++++++++++++  REQUEST  +++++++++++++++++++");
		System.out.println(response);
		System.out.println("Pipeline ID: " + id_pipeline);
		System.out.println("+++++++++++++++++++++++++++++++++++++");
		System.out.println("++++++++++++++++++  RESPONSE  ++++++++++++++++++");
		System.out.println("+++++++++++++++++++++++++++++++++++++");
		return new ResponseEntity<>(Json.toJson(body), HttpStatus.OK);
	}

	@Operation(summary = "Last time a pipeline was run")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "/pipeline/{id}/lastrun/{cookie}", produces = "application/json")
	public ResponseEntity<?> lastRunPipeline(@PathVariable("id") Long id, @PathVariable String cookie) {
		//return new ResponseEntity<>(kubeflowService.getListIDPipelines(cookie), HttpStatus.OK);
		this.cookie = cookie;
		return new ResponseEntity<>(kubeflowService.getListRuns(cookie), HttpStatus.OK);
	}

	@Operation(summary = "List of runs with state of the runs")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "/runs/state/{cookie}", produces = "application/json")
	public ResponseEntity<?> runStateList(@PathVariable String cookie) {
		String response = kubeflowService.getListRuns(cookie);
		this.cookie = cookie;
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

	@Operation(summary = "List of experiments")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "/experiments/{cookie}", produces = "application/json")
	public ResponseEntity<?> experimentList(@PathVariable String cookie) {
		String urlString = "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/apis/v1beta1/experiments";
		String httpsMethod = "GET";
		Object body = null;
		HashMap params = new HashMap<>();
		HashMap headers = new HashMap<>();
		KubeflowUtils utils = new KubeflowUtils();
		this.cookie = cookie;

		params.put("page_size", "10");
		params.put("resource_reference_key.type", "NAMESPACE");
		params.put("resource_reference_key.id", "kubeflow-renergetic");
		params.put("filter","{\"predicates\":[{\"key\":\"storage_state\",\"op\":\"NOT_EQUALS\",\"string_value\":\"STORAGESTATE_ARCHIVED\"}]}");

		headers.put("Accept", "*/*");
		headers.put("Accept-Encoding", "gzip, deflat, br");
		headers.put("Accept-Language", "en-US,en;q=0.9");
		headers.put("Connection", "keep-alive");
		headers.put("Cookie", cookie);
		headers.put("Host", "kubeflow.apps.dcw1-test.paas.psnc.pl");
		headers.put("Referer", "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/");
		return new ResponseEntity<>(utils.sendRequest(urlString, httpsMethod, params, body, headers), HttpStatus.OK);
	}
	
	// ********************************************************************************
	// ********************************************************************************
	// ********************************************************************************
	// EXAMPLE TESTS
	// ********************************************************************************
	// ********************************************************************************
	// ********************************************************************************

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
	
	// ********************************************************************************
	// ********************************************************************************
	// ********************************************************************************
	// TOMEK'S TESTS
	// ********************************************************************************
	// ********************************************************************************
	// ********************************************************************************

    @Operation(summary = "Get All workflow for non-admin users") // GET ALL PIPELINES/RUNS IT ALREADY EXISTS (ONLY FOR NON ADMINS)
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/workflow", produces = "application/json")
    public ResponseEntity<List<WorkflowDefinitionDAO>> listAll() {
        List<WorkflowDefinitionDAO> res = workflowService.getAll();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Get All workflow") //GET ALL PIPELINES/RUNS IT ALREADY EXISTS (ONLY FOR ADMINS)
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/admin/workflow", produces = "application/json")
    public ResponseEntity<List<WorkflowDefinitionDAO>> listAll(
            @RequestParam(required = false) Optional<Boolean> visible) {
        //TODO: verify admin roles
        List<WorkflowDefinitionDAO> res = workflowService.getAllAdmin(visible);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Get experiment run") //GET RUN
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/workflow/{experiment_id}/run", produces = "application/json")
    public ResponseEntity<WorkflowRunDAO> getExperimentRun(
            @PathVariable(name = "experiment_id") String experimentId) {

        WorkflowRunDAO res = workflowService.getRun(experimentId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    @Operation(summary = "Start experiment run") //RUN PIPELINE
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @PostMapping(path = "/workflow/{experiment_id}/run", produces = "application/json")
    public ResponseEntity<WorkflowRunDAO> startExperimentRun(
            @PathVariable(name = "experiment_id") String experimentId,@RequestBody Map<String,Object> params) {

        WorkflowRunDAO res = workflowService.startRun(experimentId,params);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    @Operation(summary = "Stop experiment run") //STOP PIPELINE
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

//TODO: INSERT AUTOMATIC HEADERS
//TODO: INSERT AUTOMATIC PARAMS
//TODO: IMPORT MOST OF THE FUCTIONS TO A SERVICE CLASS

/*
@Operation(summary = "Login")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping("/pipelines/login/{cookie}")
	public ResponseEntity<?> getAllPipelinesWithLogin(@PathVariable String cookie) {

		String stateValue = "STATE_VALUE"; // You need to generate a unique state value
		String step0Url = String.format("http://%s:%s", SERVICE, PORT);
		String step0Response = restTemplate.getForObject(step0Url, String.class);
		HttpServletRequest request;
		HttpServletResponse response;
		// Extract the URL from the response and fetch the state value

		// Step 1
		String step1Url = String.format(
				"http://%s:%s/dex/auth?client_id=kubeflow-oidc-authservice&redirect_uri=%2Flogin%2Foidc&response_type=code&scope=profile+email+groups+openid&state=%s",
				SERVICE, PORT, stateValue);
		String step1Response = restTemplate.getForObject(step1Url, String.class);
		// Extract the URL from the response and fetch the REQ value
		System.out.println(step1Response);
		
		// Step 2
		String reqValue = "REQ_VALUE"; // You need to get this from step 1 response
		String step2Url = String.format("http://%s:%s/dex/auth/local?req=%s", SERVICE, PORT, reqValue);
		String step2Response = restTemplate.postForObject(step2Url, null, String.class);

		// Step 3
		String step3Url = String.format("http://%s:%s/dex/approval?req=%s", SERVICE, PORT, reqValue);
		String step3Response = restTemplate.getForObject(step3Url, String.class);
		// Extract the URL from the response and fetch the CODE value

		// Step 4
		String codeValue = "CODE_VALUE"; // You need to get this from step 3 response
		String step4Url = String.format("http://%s:%s/login/oidc?code=%s&state=%s", SERVICE, PORT, codeValue,
				stateValue);
		String step4Response = restTemplate.getForObject(step4Url, String.class);
		// Extract the SESSION cookie from the response and set it in the
		// HttpServletResponse object

		// Step 5
		String pipelineUrl = String.format("http://%s:%s/pipeline/apis/v1beta1/pipelines", SERVICE, PORT);
		String sessionCookie = request.getHeader("Cookie");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", sessionCookie);
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> pipelineResponse = restTemplate.exchange(pipelineUrl, HttpMethod.GET, entity,
				String.class);
		this.cookie = cookie;
		
		return new ResponseEntity<>(kubeflowService.getListPipelines(cookie), HttpStatus.OK);
	}
	*/