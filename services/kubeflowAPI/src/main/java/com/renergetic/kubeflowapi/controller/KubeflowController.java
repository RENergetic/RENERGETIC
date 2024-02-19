package com.renergetic.kubeflowapi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.common.utilities.HttpAPIs;
import com.renergetic.common.utilities.Json;
import com.renergetic.kubeflowapi.dao.ExampleRequest;
import com.renergetic.kubeflowapi.dao.ExampleResponse;
import com.renergetic.kubeflowapi.model.ApiResourceKey;
import com.renergetic.kubeflowapi.model.ApiResourceReference;
import com.renergetic.kubeflowapi.model.ApiRunPost;
import com.renergetic.kubeflowapi.model.KeyValueParam;
import com.renergetic.kubeflowapi.model.PipelineSpec;
import com.renergetic.kubeflowapi.service.ExampleService;
import com.renergetic.kubeflowapi.service.utils.ExampleUtils;

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

	private String cookie = "authservice_session=MTcwODA4NjkxMHxOd3dBTkZaUlJ6UktOME5CUmxKUlExcEVTVEkxVTBwTVZVUkdVVmxCVXpOSVNqSkhNbEZRUmxSVlUxZExXa1pEVXpOVFNVcEdVRkU9fJ4lCcRW5Jb4LvTpIyQ5ncQtNJJUQehT7X6S7t6kKB9m";

	/**
	 * Returns the data from the list of pipelines in the Kubeflow API
	 *
	 * @return   The response entity with a null value.
	 */
	@Operation(summary = "Get all pipelines from kubeflow")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping("/pipelines")
	public ResponseEntity<?> getAllPipelines() {
		//Initial data
		String urlString = "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/apis/v1beta1/pipelines";
		String httpsMethod ="GET";
		Object body = null;
		HashMap params = new HashMap<>();
		HashMap headers= new HashMap<>();
		//String cookie = "authservice_session=MTcwNzEzODcwMXxOd3dBTkVVeU5sTkVTRlZXVjB0RVNsbFRWMDlWTjBGSlJrNDJOVE0xUTBZMU5GVkVVVWRRU0U1RU5rdEpOVFZNUlRWTFFVb3lUVkU9fM3w1NRjgY4XWImclLUj224bdgiA63MFavHwt2e1C61c";
		//
		ExampleUtils utils = new ExampleUtils();

		headers.put("Accept","*/*");
		headers.put("Accept-Encoding","gzip, deflat, br");
		headers.put("Accept-Language","en-US,en;q=0.9");
		headers.put("Connection","keep-alive");
		headers.put("Cookie",cookie);
		headers.put("Host","kubeflow.apps.dcw1-test.paas.psnc.pl");
		headers.put("Referer", "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/");
		System.out.println(headers);
		return new ResponseEntity<>(utils.sendRequest(urlString, httpsMethod, params, body, headers), HttpStatus.OK);
	}
	
	@Operation(summary = "Get all runs from kubeflow")
    @ApiResponse(responseCode = "200", description = "Request executed correctly")
    @GetMapping(path = "/runs", produces = "application/json")
    public ResponseEntity<?> getAllRuns() {
		String urlString = "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/apis/v1beta1/runs";
		String httpsMethod ="GET";
		Object body = null;
		HashMap params = new HashMap<>();
		HashMap headers= new HashMap<>();
		//String cookie = "authservice_session=MTcwNzEzODcwMXxOd3dBTkVVeU5sTkVTRlZXVjB0RVNsbFRWMDlWTjBGSlJrNDJOVE0xUTBZMU5GVkVVVWRRU0U1RU5rdEpOVFZNUlRWTFFVb3lUVkU9fM3w1NRjgY4XWImclLUj224bdgiA63MFavHwt2e1C61c";
		//
		ExampleUtils utils = new ExampleUtils();
		
		params.put("page_size", "10");
		params.put("resource_reference_key.type", "NAMESPACE");
		params.put("resource_reference_key.id", "kubeflow-renergetic");
		params.put("filter", "{\"predicates\":[{\"key\":\"storage_state\",\"op\":\"NOT_EQUALS\",\"string_value\":\"STORAGESTATE_ARCHIVED\"}]}");
		
		headers.put("Accept","*/*");
		headers.put("Accept-Encoding","gzip, deflat, br");
		headers.put("Accept-Language","en-US,en;q=0.9");
		headers.put("Connection","keep-alive");
		headers.put("Cookie",cookie);
		headers.put("Host","kubeflow.apps.dcw1-test.paas.psnc.pl");
		headers.put("Referer","https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/");
		return new ResponseEntity<>(utils.sendRequest(urlString, httpsMethod, params, body, headers), HttpStatus.OK);
    }

	// Endpoint to save ExampleRequest to the repository
	/*@Operation(summary = "Run dummy pipeline")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "/runpipe/{pipeline}", produces = "application/json")
	public ResponseEntity<?> saveExample(@PathVariable String pipeline) { //cambiar PathVariable por requestparam
		String urlString = "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/apis/v1beta1/pipelines/" + pipeline;
		String httpsMethod = "GET";
		Object body = null;
		HashMap params = new HashMap<>();
		HashMap headers = new HashMap<>();
		Json jsonUtil;
		// String cookie =
		// "authservice_session=MTcwNzEzODcwMXxOd3dBTkVVeU5sTkVTRlZXVjB0RVNsbFRWMDlWTjBGSlJrNDJOVE0xUTBZMU5GVkVVVWRRU0U1RU5rdEpOVFZNUlRWTFFVb3lUVkU9fM3w1NRjgY4XWImclLUj224bdgiA63MFavHwt2e1C61c";
		//
*/
		//headers.put("Accept", "*/*");
/*		headers.put("Accept-Encoding", "gzip, deflat, br");
		headers.put("Accept-Language", "en-US,en;q=0.9");
		headers.put("Connection", "keep-alive");
		headers.put("Cookie", cookie);
		headers.put("Host", "kubeflow.apps.dcw1-test.paas.psnc.pl");
		headers.put("Referer", "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/");

		ExampleUtils utils = new ExampleUtils();
		//System.out.println(utils.sendRequest(urlString, httpsMethod, params, body, headers));
		System.out.println("Pipeline ID: " + pipeline);
		String response = utils.sendRequest(urlString, httpsMethod, params, body, headers);
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
		//System.out.println("Experiment ID: " + experiment);
		System.out.println("Response: ");
		System.out.println(response);
		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
	}*/

	@Operation(summary = "Run dummy pipeline")
	@ApiResponse(responseCode = "200", description = "Request executed correctly")
	@GetMapping(path = "/runpipe/{pipeline}", produces = "application/json")
	public ResponseEntity<?> runDummy(@PathVariable String pipeline) { // cambiar PathVariable por requestparam
		String urlString = "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/apis/v1beta1/runs";
		String httpsMethod = "POST";
		HashMap params = new HashMap<>();
		HashMap headers = new HashMap<>();
		Json jsonUtil;
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

		ExampleUtils utils = new ExampleUtils();
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
		System.out.println("Pipeline ID: " + pipeline);
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
	// pipeline_id: 5cda125a-2973-467b-b58b-b1c1cab712e6
	// experiment_id: a8069584-de7f-4a31-9c1d-6d4a04ce66a8

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
}
