package com.globits.salary.service.impl;

import com.globits.salary.service.ExpressionEvaluatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional
@Service
public class ExpressionEvaluatorServiceImpl implements ExpressionEvaluatorService {
    // used for handling expression of formula in salary item
    // Danh sách các hàm và từ khóa cần xử lý
    private static final String[] KEYWORDS = {
            "IF", "AND", "OR", "ROUND", "DATE", "MONTH", "TODAY", "INT", "MAX", "MIN"
    };

    public static double formatDouble(double value, int places) {
        if (places < 0) throw new IllegalArgumentException("Decimal places must be non-negative");

        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }


    private String replaceVariables(String expression, Map<String, Object> variableValues) {
        Pattern pattern = Pattern.compile("\\b[A-Z_][A-Z0-9_]*\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(expression);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String variable = matcher.group();
            if (variableValues.containsKey(variable)) {
                matcher.appendReplacement(sb, variableValues.get(variable).toString());
            } else {
                // Keep the variable unchanged if it appears in a condition
                matcher.appendReplacement(sb, variable);
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private boolean isKeyword(String word) {
        for (String keyword : KEYWORDS) {
            if (keyword.equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }

    private List<String> infixToPostfix(String expression) {
        List<String> postfix = new ArrayList<>();
        Stack<String> operators = new Stack<>();
        String[] tokens = tokenize(expression);

        for (String token : tokens) {
            if (isNumber(token)) {
                postfix.add(token);
            } else if (isFunction(token)) {
                operators.push(token);
            } else if (isOperator(token)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(token)) {
                    postfix.add(operators.pop());
                }
                operators.push(token);
            } else if ("(".equals(token)) {
                operators.push(token);
            } else if (")".equals(token)) {
                while (!operators.isEmpty() && !"(".equals(operators.peek())) {
                    postfix.add(operators.pop());
                }
                if (!operators.isEmpty() && "(".equals(operators.peek())) {
                    operators.pop();
                }
                if (!operators.isEmpty() && isFunction(operators.peek())) {
                    postfix.add(operators.pop());
                }
            }
        }

        while (!operators.isEmpty()) {
            postfix.add(operators.pop());
        }

        return postfix;
    }

    private Double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();
        Stack<Object> paramsStack = new Stack<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token)) {
                double b = stack.pop();
                double a = stack.pop();
                stack.push(applyOperator(a, b, token));
            } else if (isFunction(token)) {
                List<Double> params = new ArrayList<>();
                while (!stack.isEmpty()) {
                    params.add(stack.pop());
                }
                Collections.reverse(params);
                stack.push(applyFunction(token, params));
            }
        }

        return stack.pop();
    }

    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isOperator(String token) {
        return "+-*/".contains(token);
    }

    private boolean isFunction(String token) {
        return Arrays.asList(KEYWORDS).contains(token.toUpperCase());
    }

    private int precedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return 0;
        }
    }

    private double applyOperator(double a, double b, String operator) {
        switch (operator) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return a / b;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private double applyFunction(String function, List<Double> params) {
        switch (function.toUpperCase()) {
            case "MAX":
                if (params.size() < 2) {
                    throw new IllegalArgumentException("MAX function requires at least two parameters");
                }
                return Collections.max(params);
            case "IF":
                return params.get(0) != 0 ? params.get(1) : params.get(2);
            case "AND":
                return params.stream().allMatch(p -> p != 0) ? 1.0 : 0.0;
            case "OR":
                return params.stream().anyMatch(p -> p != 0) ? 1.0 : 0.0;
            case "ROUND":
                int precision = (int) Math.round(params.get(1));
                return Math.round(params.get(0) * Math.pow(10, precision)) / Math.pow(10, precision);
            case "DATE":
                return new GregorianCalendar((int) params.get(0).doubleValue(),
                        (int) params.get(1).doubleValue() - 1,
                        (int) params.get(2).doubleValue()).getTimeInMillis();
            case "MONTH":
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(params.get(0).longValue());
                return (double) (cal.get(Calendar.MONTH) + 1);
            case "TODAY":
                return (double) System.currentTimeMillis();
            case "INT":
                return Math.floor(params.get(0));
            case "MIN":
                return Collections.min(params);
            default:
                throw new IllegalArgumentException("Unsupported function: " + function);
        }
    }

    private String[] tokenize(String expression) {
        return expression.split("(?<=[-+*/(),])|(?=[-+*/(),])");
    }

    @Override
    public List<String> extractVariables(String expression) {
        Pattern pattern = Pattern.compile("\\b[A-Z_][A-Z0-9_]*\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(expression);
        Set<String> variables = new HashSet<>();

        while (matcher.find()) {
            String variable = matcher.group();
            if (!isKeyword(variable)) {
                variables.add(variable);
            }
        }

        return new ArrayList<>(variables);
    }

    @Override
    public Double evaluateExpression(String expression, Map<String, Object> variableValues) {
        try {
            // Replace variables in the expression with their corresponding values
            String replacedExpression = replaceVariables(expression, variableValues);

            // Evaluate IF statements before converting to postfix notation
            String resultOfReplacedExpression = evaluateIFStatements(replacedExpression);

            // Convert infix expression to postfix
            List<String> postfix = infixToPostfix(resultOfReplacedExpression);

            // Evaluate the postfix expression
            Double result = evaluatePostfix(postfix);
            if (result == null) throw new Exception("Error unhandled in " + expression);

            return formatDouble(result, 2);
        } catch (Exception e) {
            System.err.println("Error evaluating expression: " + e.getMessage());
            return 0.00;
        }
    }

    private String evaluateIFStatements(String expression) {
        Pattern ifPattern = Pattern.compile("IF\\(([^,]+),([^,]+),([^\\)]+)\\)");
        Matcher matcher;

        while ((matcher = ifPattern.matcher(expression)).find()) {
            String condition = matcher.group(1).trim();
            String trueExpr = matcher.group(2).trim();
            String falseExpr = matcher.group(3).trim();

            boolean conditionResult = evaluateCondition(condition);

            // Decide which expression to use based on the condition result
            String result = conditionResult ? trueExpr : falseExpr;

            // Replace the entire IF expression with the result, ensuring no trailing characters are left
            expression = matcher.replaceFirst(Matcher.quoteReplacement(result));
        }

        return expression;
    }

    private boolean evaluateCondition(String condition) {
        Pattern conditionPattern = Pattern.compile("(\\d+(\\.\\d+)?)\\s*([<>=!]+)\\s*(\\d+(\\.\\d+)?)");
        Matcher matcher = conditionPattern.matcher(condition);

        if (matcher.matches()) {
            double left = Double.parseDouble(matcher.group(1));
            String operator = matcher.group(3);
            double right = Double.parseDouble(matcher.group(4));

            return switch (operator) {
                case "<" -> left < right;
                case "<=" -> left <= right;
                case ">" -> left > right;
                case ">=" -> left >= right;
                case "==" -> left == right;
                case "!=" -> left != right;
                default -> throw new IllegalArgumentException("Invalid operator: " + operator);
            };
        }

        throw new IllegalArgumentException("Invalid condition: " + condition);
    }



}
