package com.renergetic.kpiapi.service.utils.calc;


import java.math.BigDecimal;
import java.util.Map;
import java.util.Stack;

public class MeasurementToken implements Token {

    public final String key;

    public MeasurementToken(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "[" + this.key + "]";
    }

    @Override
    public Stack<BigDecimal> consume(Stack<BigDecimal> stack, Map<String, BigDecimal> values) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("No value for token: " + this.toString());
        }
        stack.push(values.get(this.key));
        return stack;
    }

    @Override
    public Stack<BigDecimal> consume(Stack<BigDecimal> stack) {
        throw new IllegalArgumentException("Formula contains measurement token: " + this.toString());
    }
}
