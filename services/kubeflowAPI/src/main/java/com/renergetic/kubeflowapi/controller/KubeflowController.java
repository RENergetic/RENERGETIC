package com.renergetic.kubeflowapi.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.renergetic.common.utilities.HttpAPIs;
import com.renergetic.common.utilities.Json;
import com.renergetic.kubeflowapi.dao.ExampleRequest;
import com.renergetic.kubeflowapi.dao.ExampleResponse;
import com.renergetic.kubeflowapi.service.ExampleService;

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

	/**
	 * Returns the data from the list of pipelines in the Kubeflow API
	 *
	 * @return   The response entity with a null value.
	 */
	@GetMapping("/pipelines")
	public ResponseEntity<?> test() {
		//Initial data
		String urlString = "https://kubeflow.apps.dcw1-test.paas.psnc.pl/pipeline/apis/v1beta1/pipelines";
		String httpsMethod ="";
		HashMap params = new HashMap<>();
		Object body = null;
		HashMap headers= new HashMap<>();
		String cookie = "authservice_session=MTcwNTA1Njc2OHxOd3dBTkU1U1MwOVdXVk5IUWxkRVZVTkhUMWhXTjBWVlIwRTNVMUExVURSRk1qWkVNbHBZVmxCRE5UTllXVkUxVUVkR1RrNVFSVkU9fH6RL1nwdX7jfD98Q3WqaQwlZ4sJx-xZmSA-50-K5ioH";
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
}
