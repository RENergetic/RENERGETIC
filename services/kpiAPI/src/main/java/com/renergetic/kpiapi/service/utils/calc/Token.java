package com.renergetic.kpiapi.service.utils.calc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public interface Token {

    public Stack<BigDecimal> consume(Stack<BigDecimal> stack);

    public Stack<BigDecimal> consume(Stack<BigDecimal> stack, Map<String, BigDecimal> values);

    public static BigDecimal eval(Queue<Token> q) {
        if (q.isEmpty()) {
            return null;
        }
        Stack<BigDecimal> buff = new Stack<>();
        while (q.peek() != null) {
            buff = q.poll().consume(buff);

        }
        var res = buff.pop();
        if (!buff.empty()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return res;

    }
    public static BigDecimal eval(Queue<Token> q,Map<String ,BigDecimal> values) {
        if (q.isEmpty()) {
            return null;
        }
        Stack<BigDecimal> buff = new Stack<>();
        while (q.peek() != null) {
            buff = q.poll().consume(buff,values);

        }
        var res = buff.pop();
        if (!buff.empty()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return res;

    }
}
