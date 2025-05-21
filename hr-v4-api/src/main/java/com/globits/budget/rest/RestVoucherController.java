package com.globits.budget.rest;

import com.globits.budget.dto.budget.BudgetSearchDto;
import com.globits.budget.dto.VoucherDto;
import com.globits.budget.dto.budget.BudgetSummaryBalanceDto;
import com.globits.budget.dto.budget.BudgetSummaryDto;
import com.globits.budget.service.BudgetService;
import com.globits.budget.service.VoucherService;
import com.globits.budget.utils.ExportExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/voucher")
public class RestVoucherController {
    @Autowired
    private VoucherService service;
    @Autowired
    private BudgetService budgetService;

    @RequestMapping(value = "/paging", method = RequestMethod.POST)
    public ResponseEntity<Page<VoucherDto>> pagingVoucher(@RequestBody BudgetSearchDto dto) {
        Page<VoucherDto> result = service.pagingVoucherDto(dto);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean deleteById(@PathVariable UUID id) {
        return service.deleteById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<VoucherDto> getVoucherById(@PathVariable UUID id) {
        VoucherDto dto = service.getById(id);
        if (dto == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public VoucherDto saveVoucher(@RequestBody VoucherDto dto) {
        return service.saveOrUpdate(dto);
    }
    @RequestMapping(value = "/deleteMultiple", method = RequestMethod.POST)
    public ResponseEntity<?> deleteMultiple(@RequestBody List<UUID> ids) {
        Integer result = service.deleteMultiple(ids);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);

    }
    @RequestMapping(value = "/export-excel", method = RequestMethod.POST)
    public void exportVoucherToExcel(HttpServletResponse response, @RequestBody BudgetSummaryDto dto) throws IOException {
        List<VoucherDto> dataList = service.getAll(dto);
        BudgetSummaryBalanceDto summary = budgetService.getBudgetSummaryBalance(dto);


        if (!dataList.isEmpty() && summary != null) {
            InputStream inputStream = new ClassPathResource("HOA_DON_THU_CHI.xlsx").getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            ByteArrayResource excelFile = ExportExcelUtil.exportVoucher(dataList, summary, workbook);

            if (excelFile != null) {
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.addHeader("Content-Disposition", "attachment; filename=HOA_DON_THU_CHI.xlsx");
                org.apache.commons.io.IOUtils.copy(excelFile.getInputStream(), response.getOutputStream());
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }
}
