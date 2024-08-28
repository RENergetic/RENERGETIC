package com.renergetic.kpiapi.service.utils.calc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Stack;

public class ValueToken implements Token {

    public final BigDecimal value;

    public ValueToken(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public Stack<BigDecimal> consume(Stack<BigDecimal> stack) {
        stack.push(this.value);
        return stack;
    }

    @Override
    public Stack<BigDecimal> consume(Stack<BigDecimal> stack, Map<String, BigDecimal> values) {
        return this.consume(stack);
    }
}
