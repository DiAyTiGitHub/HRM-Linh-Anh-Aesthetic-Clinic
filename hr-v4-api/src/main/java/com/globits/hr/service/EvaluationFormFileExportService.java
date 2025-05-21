package com.globits.hr.service;

import com.globits.hr.dto.search.EvaluationFormSearchDto;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

public interface EvaluationFormFileExportService {
    XWPFDocument exportWord(UUID formId) throws IOException;
    Workbook exportContractApprovalList(EvaluationFormSearchDto searchDto) throws IOException;
}
