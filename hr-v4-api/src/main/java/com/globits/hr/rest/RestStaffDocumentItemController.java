package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.core.domain.FileDescription;
import com.globits.core.dto.FileDescriptionDto;
import com.globits.core.service.FileDescriptionService;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffDocumentItemDto;
import com.globits.hr.dto.TemplateStaffDto;
import com.globits.hr.dto.search.SearchStaffDocumentItemDto;
import com.globits.hr.service.StaffDocumentItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/staff-document-item")
public class RestStaffDocumentItemController {
    @Autowired
    private StaffDocumentItemService staffDocumentItemService;

    @Autowired
    FileDescriptionService fileDescriptionService;
    @Autowired
    private Environment env;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-or-update")
    public ResponseEntity<StaffDocumentItemDto> saveOrUpdate(@RequestBody StaffDocumentItemDto dto) {
        StaffDocumentItemDto response = staffDocumentItemService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(
            value = {"/upload"},
            method = {RequestMethod.POST},
            consumes = {"multipart/form-data"}
    )
    public ResponseEntity<FileDescriptionDto> saveUploadFile(@RequestParam("uploadfile") MultipartFile file) {
        try {
            // Lấy đường dẫn thư mục lưu file từ cấu hình
            String uploadPath = env.getProperty("hrm.file.folder", System.getProperty("java.io.tmpdir"));

            // Kiểm tra nếu thư mục chưa tồn tại thì tạo mới
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Xử lý tên file để tránh trùng
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = uploadDir.resolve(uniqueFilename);

            // Ghi file vào hệ thống
            try (OutputStream out = new FileOutputStream(filePath.toFile())) {
                out.write(file.getBytes());
            }

            // Lưu thông tin file vào DB
            FileDescription fileDesc = new FileDescription();
            fileDesc.setContentSize(file.getSize());
            fileDesc.setContentType(file.getContentType());
            fileDesc.setFilePath(filePath.toString());
            fileDesc.setName(originalFilename);

            fileDesc = fileDescriptionService.save(fileDesc);

            return ResponseEntity.ok(new FileDescriptionDto(fileDesc));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/search-by-page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<StaffDocumentItemDto>> searchByPage(@RequestBody SearchStaffDocumentItemDto searchDto) {
        Page<StaffDocumentItemDto> page = staffDocumentItemService.searchByPage(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/get-item-by-template-staff", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TemplateStaffDto> getItemByTemplateStaff(@RequestBody SearchStaffDocumentItemDto searchDto) {
        TemplateStaffDto page = staffDocumentItemService.getItemByTemplateStaff(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/save-template-staff", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TemplateStaffDto> saveTemplateStaff(@RequestBody TemplateStaffDto dto) {
        TemplateStaffDto page = staffDocumentItemService.saveTemplateStaff(dto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("id") UUID staffDocumentItemId) {
        Boolean res = staffDocumentItemService.deleteById(staffDocumentItemId);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<StaffDocumentItemDto> getById(@PathVariable("id") UUID id) {
        StaffDocumentItemDto result = staffDocumentItemService.getById(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = staffDocumentItemService.deleteMultiple(ids);

        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }


}
