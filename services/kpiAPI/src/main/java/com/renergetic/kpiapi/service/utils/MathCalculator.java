package com.renergetic.kpiapi.service.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.renergetic.kpiapi.model.Details;
import com.renergetic.kpiapi.model.Measurement;
import com.renergetic.kpiapi.model.details.MeasurementDetails;
import com.renergetic.kpiapi.model.details.MeasurementTags;
import com.renergetic.kpiapi.repository.AssetRepository;
import com.renergetic.kpiapi.repository.MeasurementRepository;
import com.renergetic.kpiapi.repository.MeasurementTagsRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MathCalculator {
    @Value("${influx.api.url}")
    String influxURL;
	
	@Autowired
	AssetRepository assetRepository;
	
	@Autowired
	MeasurementRepository measurementRepository;
	
	@Autowired
	MeasurementTagsRepository measurementTagsRepository;
	
	@Autowired
	HttpAPIs httpAPIs;
	
	public BigDecimal calculateEquation(String equation, Long from, Long to) {
		
		Stack<BigDecimal> numbers = new Stack<>();
		Stack<Character> operators = new Stack<>();
		
		for (int i = 0; i < equation.length(); i++) {
			char c = equation.charAt(i);
			log.debug("Read character: " + c);
			log.debug("Number list:   " + numbers.toString());
			log.debug("Operator list: " + operators.toString());
			
			if (Character.isDigit(c)) {
				
				StringBuilder number = new StringBuilder();
				while (i < equation.length() && (Character.isDigit(equation.charAt(i)) || equation.charAt(i) == '.')) {
					number.append(equation.charAt(i));
					i++;
				}
				i--;
				numbers.push(BigDecimal.valueOf(Double.parseDouble(number.toString())));
		
			} else if (isOperator(c)) {
				while (!operators.empty() && hasPrecedence(operators.peek(), c)) {
					calculate(numbers,operators);
				}
				operators.push(c);
			} else if (c == '(' || c == '[') {
				operators.push(c);
			} else if (c == ']') {
				getValueFromMeasurement(numbers, from, to);
				operators.pop();
			} else if (c == ')') {
				while (!operators.empty() && operators.peek() != '(') {
					calculate(numbers,operators);
				}
				operators.pop();
			}
		}
		
		 while (!operators.empty()) {
	            calculate(numbers, operators);
	     }

        return numbers.pop();
	}
	
	private void calculate(Stack<BigDecimal> numbers, Stack<Character> operators) {
		BigDecimal num2 = numbers.pop();
		BigDecimal num1 = numbers.pop();
		char operator = operators.pop();
		BigDecimal result;
		
		switch (operator) {
			case '+':
				result = num1.add(num2);
				break;
			case '-':
				result = num1.subtract(num2);
				break;
			case '*':
				result = num1.multiply(num2);
				break;
			case '/':
				result = num1.divide(num2, MathContext.DECIMAL128);
				break;
			case '^':
				result = num1.pow(num2.intValue());
				break;
			default:
				throw new IllegalArgumentException("Invalid Operator: " + operator);
		}
		
		numbers.push(result);
	}
	
	private void getValueFromMeasurement(Stack<BigDecimal> numbers, Long from, Long to) {
		Long measurementId = numbers.pop().longValue();
		
		numbers.push(getMeasurementData(measurementId, from, to));
		
	}
		
	private static boolean isOperator(Character c) {
		return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
	}
	
	private static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        return (op1 == '^' && op2 != '^') || ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'));
    }
	
	private BigDecimal getMeasurementData(Long id, Long from, Long to) {
		Measurement measurement = measurementRepository.findById(id).orElse(null);
		BigDecimal ret = null;
		
		if (measurement != null) {
			Map<String, String> params = new HashMap<>();
	        List<String> assetNames = new LinkedList<>();
	        String function = measurement.getFunction() != null ? measurement.getFunction() : "sum";
	
	        // GET ASSETS RELATED WITH THE MEASUREMENT (If the assets is a energy island and there isn't category it doesn't filter by asset)
	
	        if (measurement.getAsset() != null && measurement.getAsset().getType() != null && 
	            !measurement.getAsset().getType().getName().equalsIgnoreCase("energy_island"))
	            assetNames.add(measurement.getAsset().getName());
	        if (measurement.getAssetCategory() != null)
	            assetNames.addAll(assetRepository.findByAssetCategoryId(measurement.getAssetCategory().getId())
	                    .stream().map(asset -> asset.getName()).collect(Collectors.toList()));
	
	        // GET MEASUREMENT TAGS
	        List<MeasurementTags> tags = measurementTagsRepository.findByMeasurementId(measurement.getId());
	
	        // PREPARE INFLUX FILTERS
	        if (from != null)
	            params.put("from", from.toString());
	        if (to != null)
	            params.put("to", to.toString());
	        if (measurement.getName() != null)
	            params.put("measurement_type", measurement.getName());
	        if (measurement.getSensorName() != null)
	            params.put("measurements", measurement.getSensorName());
	        if (measurement.getType() != null)
	            params.put("fields", measurement.getType().getName());
	        if (measurement.getDirection() != null)
	            params.put("direction", measurement.getDirection().name());
	        if (measurement.getDomain() != null)
	            params.put("domain", measurement.getDomain().name());
	        if (assetNames != null && !assetNames.isEmpty())
	            params.put("asset_name", assetNames.stream().collect(Collectors.joining(",")));
	        if (tags != null && !tags.isEmpty())
	            params.putAll(tags.stream()
	                    .filter(tag -> !params.containsKey(tag.getValue()))
	                    .collect(Collectors.toMap(Details::getKey, Details::getValue)));
	
	        // PARSE TO NON CUMULATIVE DATA IF THE MEASUREMENT IS CUMULATIVE
	        MeasurementDetails cumulative = null;
	        if (measurement.getDetails() != null)
	            cumulative = measurement.getDetails().stream().filter(
	                details -> details.getKey().equalsIgnoreCase("cumulative")).findFirst().orElse(null);
	
	        if (cumulative != null) {
	            params.put("performDecumulation", cumulative.getValue());
	        }
	
	        // INFLUX API REQUEST
	        HttpResponse<String> response =
	        		httpAPIs.sendRequest(influxURL + "/api/measurement/data/" + function, "GET", params, null,
	                        null);
	
	        if (response.statusCode() < 300) {
	            JSONArray array = new JSONArray(response.body());
	            if (array.length() > 0)
	                for (Object obj : array) {
	                    if (obj instanceof JSONObject) {
	                        JSONObject json = (JSONObject) obj;
	                        if (json.has("measurement"))
	                            ret = BigDecimal.valueOf(Double.parseDouble(json.getJSONObject("fields").getString(function)));
	                    }
	                }
	        } else ret = new BigDecimal(0);
		}
		log.debug(String.format("Value for measurement %d: %s", id, ret));
		
		return ret;
	}
}