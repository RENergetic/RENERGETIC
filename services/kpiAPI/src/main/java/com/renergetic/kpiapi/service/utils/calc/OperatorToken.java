package com.renergetic.kpiapi.service.utils.calc;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.Stack;

public class OperatorToken implements Token {


    public final Character op;

    public OperatorToken(Character op) {
        this.op = op;
    }

    @Override
    public String toString() {
        return this.op.toString();
    }

    @Override
    public Stack<BigDecimal> consume(Stack<BigDecimal> stack) {
        var v1 = stack.pop();
        var v2 = stack.pop();

        switch (this.op) {
            case '/':
                stack.push(v2.divide(v1, MathContext.DECIMAL128));
//                stack.push(v2 / v1);
                break;
            case '+':

                stack.push(v2.add(v1));
//                stack.push(v2 + v1);
                break;
            case '-':
                stack.push(v2.subtract(v1));
//                stack.push(v2 - v1);
                break;
            case '*':
                stack.push(v2.multiply(v1));
//                stack.push(v2 * v1);
                break;
            case '^':
//                BigDecimalMath.pow(v1, v2) -> TODO:
//                https://stackoverflow.com/questions/16441769/javas-bigdecimal-powerbigdecimal-exponent-is-there-a-java-library-that-does
                stack.push(BigDecimal.valueOf(Math.pow(v1.doubleValue(), v2.doubleValue())));
                break;
        }
        return stack;
    }

    @Override
    public Stack<BigDecimal> consume(Stack<BigDecimal> stack, Map<String, BigDecimal> values) {
        return this.consume(stack);
    }
}
