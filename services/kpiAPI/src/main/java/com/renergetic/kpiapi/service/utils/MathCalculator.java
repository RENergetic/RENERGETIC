package com.renergetic.kpiapi.service.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.renergetic.kpiapi.service.utils.calc.MeasurementToken;
import com.renergetic.kpiapi.service.utils.calc.ShuntingYardParser;
import com.renergetic.kpiapi.exception.InvalidArgumentException;
import com.renergetic.kpiapi.model.Asset;
import com.renergetic.kpiapi.service.utils.calc.Token;
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

    /**
     * Validates a mathematical formula.
     *
     * @param formula string containing the formula to be validated
     * @return true if the formula is valid and all its parenthesis are closed, false otherwise
     */
    public boolean validateFormula(String formula) {
        formula = removeWhiteSpaces(formula);

        // Regex to validate a mathematical formula
        Pattern pattern = Pattern.compile("\\(*((\\d+(\\.\\d+)?)|(\\[\\d+\\]))([+*^\\/-]\\(*((\\d+(\\.\\d+)?)|(\\[\\d+\\]))\\)*)*");

        // Check if the formula close all its parenthesis
        if (formula != null && pattern.matcher(formula).matches())
            return formula.chars().filter(ch -> ch == '(').count() == formula.chars().filter(ch -> ch == ')').count();
        return false;
    }

    /**
     * Validates condition composed of two mathematical formulas.
     *
     * @param formula string containing the condition to be validated
     * @return An Array with the mathematical formulas that compose the condition, null otherwise
     */
    public String[] validateCondition(String formula) {
        String comparator = extractComparator(formula);

        if (comparator != null) {
            String[] formulas = formula.split(comparator);

            return formulas.length == 2 && validateFormula(formulas[0]) || validateFormula(formulas[1]) ?
                    formulas : null;
        }
        return null;
    }

    public BigDecimal calcFormula(String formula, Long from, Long to) {

        var tokens = ShuntingYardParser.parse(formula);
        HashMap<String, BigDecimal> values = new HashMap<>();
        List<Token> list = tokens.stream().filter(it -> it instanceof MeasurementToken).toList();
        String baseUnit = null;
        for (var it : list) {
            MeasurementToken t = (MeasurementToken) it;
            if (!values.containsKey(t.key)) {
                var id = Long.parseLong(t.key);
                var m = getMeasurement(id, baseUnit);
                if (baseUnit == null) {
                    baseUnit = m.getType().getBaseUnit();
                }
                var value = getValueFromMeasurement(m, from, to);
                values.put(t.key, value);
            }
        }
        return Token.eval(tokens, values);
    }

    @Deprecated
    public BigDecimal calculateFormula(String formula, Long from, Long to) {
        formula = removeWhiteSpaces(formula);

        Stack<BigDecimal> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        String compareBaseUnits = null;
        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);
            log.debug("Read character: " + c);
            log.debug("Number list:   " + numbers.toString());
            log.debug("Operator list: " + operators.toString());

            if (Character.isDigit(c)) {
                StringBuilder number = new StringBuilder();
                while (i < formula.length() && (Character.isDigit(formula.charAt(i)) || formula.charAt(i) == '.')) {
                    number.append(formula.charAt(i));
                    i++;
                }
                i--;
                numbers.push(BigDecimal.valueOf(Double.parseDouble(number.toString())));

            } else if (isOperator(c)) {
                while (!operators.empty() && hasPrecedence(operators.peek(), c)) {
                    calculate(numbers, operators);
                }
                operators.push(c);
            } else if (c == '(' || c == '[') {
                operators.push(c);
            } else if (c == ']') {
                String baseUnit = getValueFromMeasurement(numbers, from, to);
                if (compareBaseUnits == null) {
                    compareBaseUnits = baseUnit;
                } else if (!compareBaseUnits.equals(baseUnit)) {
//					different unit
                    throw new IllegalArgumentException("invalid units ");
                }
                operators.pop();
            } else if (c == ')') {
                while (!operators.empty() && operators.peek() != '(') {
                    calculate(numbers, operators);
                }
                operators.pop();
            }
        }

        while (!operators.empty()) {
            calculate(numbers, operators);
        }

        BigDecimal ret = numbers.pop();
        return ret != null ? ret : new BigDecimal(0);
    }

    public boolean compare(String formula, Long from, Long to) {
        String comparator = extractComparator(formula);
        String[] formulas = validateCondition(formula);


        if (formulas == null)
            throw new InvalidArgumentException("%s isn't a comparation", formula);
        else {
            return switch (comparator) {
                case ">=" ->
                        calcFormula(formulas[0], from, to).doubleValue() >= calcFormula(formulas[1], from, to).doubleValue();
                case "<=" ->
                        calcFormula(formulas[0], from, to).doubleValue() <= calcFormula(formulas[1], from, to).doubleValue();
                case ">" ->
                        calcFormula(formulas[0], from, to).doubleValue() > calcFormula(formulas[1], from, to).doubleValue();
                case "<" ->
                        calcFormula(formulas[0], from, to).doubleValue() < calcFormula(formulas[1], from, to).doubleValue();
                case "=" ->
                        calcFormula(formulas[0], from, to).doubleValue() == calcFormula(formulas[1], from, to).doubleValue();
                case "!=" ->
                        calcFormula(formulas[0], from, to).doubleValue() != calcFormula(formulas[1], from, to).doubleValue();
                default -> false;
            };
//            return switch (comparator) {
//                case ">=" ->
//                        calculateFormula(formulas[0], from, to).doubleValue() >= calculateFormula(formulas[1], from, to).doubleValue();
//                case "<=" ->
//                        calculateFormula(formulas[0], from, to).doubleValue() <= calculateFormula(formulas[1], from, to).doubleValue();
//                case ">" ->
//                        calculateFormula(formulas[0], from, to).doubleValue() > calculateFormula(formulas[1], from, to).doubleValue();
//                case "<" ->
//                        calculateFormula(formulas[0], from, to).doubleValue() < calculateFormula(formulas[1], from, to).doubleValue();
//                case "=" ->
//                        calculateFormula(formulas[0], from, to).doubleValue() == calculateFormula(formulas[1], from, to).doubleValue();
//                case "!=" ->
//                        calculateFormula(formulas[0], from, to).doubleValue() != calculateFormula(formulas[1], from, to).doubleValue();
//                default -> false;
//            };
        }
    }

    private String extractComparator(String formula) {
        Pattern pattern = Pattern.compile("(!=|>=|>|<=|<|=)");
        Matcher matcher = pattern.matcher(formula);
        if (matcher.find()) {
            String comparator = matcher.group(1);
            log.debug("Comparator: " + comparator);
            return comparator;
        }
        return null;
    }

    private void calculate(Stack<BigDecimal> numbers, Stack<Character> operators) {
        BigDecimal num2 = numbers.pop();
        BigDecimal num1 = numbers.pop();
        char operator = operators.pop();

        BigDecimal result;
        try {
            result = switch (operator) {
                case '+' -> num1.add(num2);
                case '-' -> num1.subtract(num2);
                case '*' -> num1.multiply(num2);
                case '/' -> num1.divide(num2, MathContext.DECIMAL128);
                case '^' -> num1.pow(num2.intValue());
                default -> throw new IllegalArgumentException("Invalid Operator: " + operator);
            };
        } catch (ArithmeticException e) {
            result = new BigDecimal(0);
            log.warn(String.format("Error executing (%.2f %c %.2f): %s%n", num1, operator, num2, e.getMessage()));
        }

        numbers.push(result);
    }

    private String getValueFromMeasurement(Stack<BigDecimal> numbers, Long from, Long to) {
        Long measurementId = numbers.pop().longValue();
        Measurement measurement = measurementRepository.findById(measurementId).orElse(null);
        if (measurement == null) {
            log.debug(String.format("Measurement not exists: %d ", measurementId));
        } else {
            var measurementValue = getMeasurementData(measurement, from, to);
//convert to SI unit value

            numbers.push(measurementValue.multiply(BigDecimal.valueOf(measurement.getType().getFactor())));
            return measurement.getType().getBaseUnit();
        }

        return null;
    }

    private Measurement getMeasurement(Long measurementId, String baseUnit) {


        Measurement measurement = measurementRepository.findById(measurementId).orElse(null);
        if (measurement == null) {
            log.debug(String.format("Measurement not exists: %d ", measurementId));
        } else {
            if (baseUnit != null && !baseUnit.equals(measurement.getType().getBaseUnit())) {
                throw new IllegalArgumentException("invalid units ");
            }
            return measurement;
        }

        return null;
    }

    private BigDecimal getValueFromMeasurement(Measurement measurement, Long from, Long to) {
        var measurementValue = getMeasurementData(measurement, from, to);
//     var   measurementValue = new BigDecimal("1.0");
//convert to SI unit value

        return measurementValue.multiply(BigDecimal.valueOf(measurement.getType().getFactor()));

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

    private BigDecimal getMeasurementData(Measurement measurement, Long from, Long to) {

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
                        .stream().map(Asset::getName).collect(Collectors.toList()));

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
            if (measurement.getSensorId() != null)
                params.put("sensor_id", measurement.getSensorId());
            if (!assetNames.isEmpty())
                params.put("asset_name", String.join(",", assetNames));
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
                if (!array.isEmpty())
                    for (Object obj : array) {
                        if (obj instanceof JSONObject json && (json.has("measurement"))) {
                            ret = BigDecimal.valueOf(Double.parseDouble(json.getJSONObject("fields").getString(function)));
                        }
                    }
            } else ret = new BigDecimal(0);
            log.debug(String.format("Value for measurement %d: %s", measurement.getId(), ret));
        }


        return ret;
    }

    private String removeWhiteSpaces(String formula) {
        return formula.replace(" ", "");
    }

    public String bigDecimalToDoubleString(BigDecimal number) {
        if (number == null || Double.isNaN(number.doubleValue()) || Double.isInfinite(number.doubleValue()))
            return "0.0";

        String text = number.toPlainString();
        if (text.contains("."))
            return text;
        else return text + ".0";
    }
}