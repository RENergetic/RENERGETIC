package com.renergetic.kubeflowapi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.common.utilities.HttpAPIs;
import com.renergetic.common.utilities.Json;
import com.renergetic.kubeflowapi.model.ApiResourceKey;
import com.renergetic.kubeflowapi.model.ApiResourceReference;
import com.renergetic.kubeflowapi.model.ApiRunPost;
import com.renergetic.kubeflowapi.model.KeyValueParam;
import com.renergetic.kubeflowapi.model.PipelineSpec;
import com.renergetic.kubeflowapi.service.ExampleService;
import com.renergetic.kubeflowapi.service.KubeflowService;
import com.renergetic.kubeflowapi.service.utils.KubeflowUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Kubeflow Controller", description = "Manage the connection to Kubeflow API")
@RequestMapping("/api/kubeflow")
public class KubeflowController {
	
	@Autowired
	private ExampleService exampleService;
	@Autowired
	private HttpAPIs apis;
	@Autowired
	private KubeflowService kubeflowService;

	private String cookie; //= "authservice_session=MTcwODYwNDAzOXxOd3dBTkRVelRVSllWVlJVUTFwR1ZWUk1Wa3RSTmxCUFFrMVNWVXd5V2taUVEwMDFUak5JUzBSRVQwdFVORmRJTjBsSE5WVkhXVUU9fDi91bsE7zDKYFLkmh6D2uH6Coo6WpI_K9526oVRu9TN";

	/**
	 * Returns the data from the list of pipelines in the Kubeflow API
	 *
	 * @return   The response entity with a null value.
	 */
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
		// String cookie =
		// "authservice_session=MTcwNzEzODcwMXxOd3dBTkVVeU5sTkVTRlZXVjB0RVNsbFRWMDlWTjBGSlJrNDJOVE0xUTBZMU5GVkVVVWRRU0U1RU5rdEpOVFZNUlRWTFFVb3lUVkU9fM3w1NRjgY4XWImclLUj224bdgiA63MFavHwt2e1C61c";
		//
		//Constructing the request
		List<KeyValueParam> parameters = new ArrayList<>();
		parameters.add(new KeyValueParam("value_1","1"));
		parameters.add(new KeyValueParam("value_2","b"));
		parameters.add(new KeyValueParam("value_3", "8"));
		//PipelineSpec pipeSpec = new PipelineSpec("5cda125a-2973-467b-b58b-b1c1cab712e6", "DETAULT NAME","WORLKFLOW MANIFEST", "PIPELINE MANIFEST", parameters);
		PipelineSpec pipeSpec = new PipelineSpec(null, null,null, null, parameters);
		List<ApiResourceReference> resourceReference = new ArrayList<>();
		ApiResourceKey key = new ApiResourceKey("a8069584-de7f-4a31-9c1d-6d4a04ce66a8", "EXPERIMENT");
		resourceReference.add(new ApiResourceReference(key, "", "OWNER"));
		key = new ApiResourceKey("cd6edfd0-ed26-4c15-bb18-bdba5d5c99aa", "PIPELINE_VERSION");
		resourceReference.add(new ApiResourceReference(key, "", "CREATOR"));
		ApiRunPost body = new ApiRunPost("DummyTest4Description", "DummyTest4Name", pipeSpec, resourceReference, ""); //TODO: RESOURCE_REFERENCES
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
		/*String response = utils.sendRequest(urlString, httpsMethod, params, body, headers);
		JSONObject jsonObject = null;
		try {
			jsonObject = Json.parse(response);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("JSON: ");
		System.out.println(jsonObject);
		System.out.println("*********************************** ");
		System.out.println(jsonObject.getString("name"));
		System.out.println("*********************************** ");
		// System.out.println("Experiment ID: " + experiment);
		System.out.println("Response: ");
		System.out.println(response);
		*/
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
}

//TODO: INSERT AUTOMATIC HEADERS
//TODO: INSERT AUTOMATIC PARAMS
//TODO: IMPORT MOST OF THE FUCTIONS TO A SERVICE CLASS