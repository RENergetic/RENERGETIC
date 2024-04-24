package com.renergetic.kubeflowapi.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HttpsResponseInfo {

    private int responseCode;

    private String responseBody;

    private Map<String, List<String>> responseHeaders;

}