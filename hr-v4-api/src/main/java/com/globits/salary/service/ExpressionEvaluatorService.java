package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.search.SearchSalaryItemDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ExpressionEvaluatorService {
    Double evaluateExpression(String expression, Map<String, Object> variableValues);

    List<String> extractVariables(String expression);


}
