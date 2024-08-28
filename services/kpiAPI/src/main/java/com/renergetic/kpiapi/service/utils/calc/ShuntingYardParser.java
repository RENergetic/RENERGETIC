package com.renergetic.kpiapi.service.utils.calc;


import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public final class ShuntingYardParser {


    //https://www.geeksforgeeks.org/java-program-to-implement-shunting-yard-algorithm/


    private static boolean letterOrDigit(char c) {
//        if (Character.isLetterOrDigit(c))
//            return true;
        if (Character.isLetter(c)) {
            throw new IllegalArgumentException("invalid token:" + c);
        }
        return Character.isDigit(c) || c=='.';
    }


    // Operator having higher precedence
    // value will be returned
    private static int getPrecedence(char ch) {
        if (ch == '+' || ch == '-')
            return 1;
        else if (ch == '*' || ch == '/')
            return 2;
        else if (ch == '^')
            return 3;
        else
            return -1;
    }

    // Operator has Left --> Right associativity
    static boolean hasLeftAssociativity(char ch) {
        return ch == '+' || ch == '-' || ch == '/' || ch == '*';
    }

    private static int setMeasurementId(int start, String expression, Queue<Token> q) {

        for (int i = start; i < expression.length(); ++i) {
            if (expression.charAt(i) == ']') {
                var mId = expression.substring(start + 1, i);//TODO: load measurement here
                q.add(new MeasurementToken(mId));
                return i;

            }

        }
        throw new IllegalArgumentException("missing  ']'");
    }

    // Method converts  given infixto postfix expression
    // to illustrate shunting yard algorithm
    public static Queue<Token> parse(String expression) {
        // Initialising an empty String
        // (for output) and an empty stack
        Stack<Character> stack = new Stack<>();
        Queue<Token> out = new LinkedList<>();

        // Initially empty string taken
        String buff = "";

        // Iterating over tokens using inbuilt
        // .length() function
        for (int i = 0; i < expression.length(); ++i) {
            // Finding character at 'i'th index
            char c = expression.charAt(i);

            // If the scanned Token is an
            // operand, add it to output
            if ((c) == ']') {
                throw new IllegalArgumentException("invalid bracket ']'" + i);
            }
            if ((c) == '[') {
                i = setMeasurementId(i, expression, out);
            } else if (letterOrDigit(c)) {
                buff += c;
            } else {

                if (!buff.isEmpty())
                    out.add(new ValueToken(new BigDecimal(buff)));
                buff = "";
                // If the scanned Token is an '('
                // push it to the stack
                if (c == '(') {
                    stack.push(c);
                }
                // If the scanned Token is an ')' pop and append
                // it to output from the stack until an '(' is
                // encountered
                else if (c == ')') {
                    while (!stack.isEmpty()
                            && stack.peek() != '(') {

                        var s = stack.pop();
                        out.add(new OperatorToken(s));
                    }

                    stack.pop();
                }

                // If an operator is encountered then taken the
                // further action based on the precedence of the
                // operator

                else {
                    while (
                            !stack.isEmpty()
                                    && getPrecedence(c)
                                    <= getPrecedence(stack.peek())
                                    && hasLeftAssociativity(c)) {
                        // peek() inbuilt stack function to
                        // fetch the top element(token)

                        var s = stack.pop();

                        out.add(new OperatorToken(s));

                    }
                    stack.push(c);


                }
            }

        }
        if (!buff.isEmpty())
            out.add(new ValueToken(new BigDecimal(buff)));
        while (!stack.isEmpty()) {
            if (stack.peek() == '(')
                throw new IllegalArgumentException("");

            var s = stack.pop();
            out.add(new OperatorToken(s));

        }
        // pop all the remaining operators from
        // the stack and append them to output
        return out;
    }

}
