package com.globits.hr.rest;

import java.text.Normalizer;
import java.text.Normalizer.Form;

import com.globits.core.Constants;
import com.globits.core.domain.FileDescription;
import com.globits.core.dto.FileDescriptionDto;
import com.globits.core.service.FileDescriptionService;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffLabourAgreementDto;
import com.globits.hr.service.StaffLabourAgreementService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.Normalizer;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/labour-agreement-attachment")
public class RestStaffLabourAgreementAttachmentController {
    @Autowired
    FileDescriptionService fileDescriptionService;
    @Autowired
    private Environment env;

    @RequestMapping(
            value = {"/upload"},
            method = {RequestMethod.POST},
            consumes = {"multipart/form-data"}
    )
    public ResponseEntity<FileDescriptionDto> saveUploadFile(@RequestParam("uploadfile") MultipartFile file) {
        String uploadPath = "";
        if (env.getProperty("hrm.file.folder") != null) {
            uploadPath = env.getProperty("hrm.file.folder");
        }

        try {
            byte[] bytes = file.getBytes();
            FileDescription fileDesc = new FileDescription();
            fileDesc.setContentSize(file.getSize());
            fileDesc.setContentType(file.getContentType());
            String filePath = file.getOriginalFilename();
            if (uploadPath != null && uploadPath.length() > 0) {
                filePath = uploadPath + filePath;
            }

            File f = new File(filePath);
            if (!f.exists()) {
                f.createNewFile();
            }

            OutputStream out = new FileOutputStream(f);
            out.write(bytes);
            out.close();
            fileDesc.setFilePath(filePath);
            fileDesc.setName(file.getOriginalFilename());

            fileDesc = fileDescriptionService.save(fileDesc);

            ResponseEntity<FileDescriptionDto> response
                    = new ResponseEntity(new FileDescriptionDto(fileDesc), HttpStatus.OK);
            return response;
        } catch (IOException var7) {
            var7.printStackTrace();
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/download/{fileId}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadFileById(@PathVariable("fileId") UUID fileId, HttpServletRequest request) {
        try {
            FileDescription fileDesc = fileDescriptionService.findById(fileId);

            if (fileDesc == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            String filePath = fileDesc.getFilePath();
            File file = new File(filePath);

            if (!file.exists()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            FileInputStream fileInputStream = new FileInputStream(file);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            String fileName = fileDesc.getName(); // or any title you want to use
            headers.add("Content-Disposition", "inline; filename=\"" + removeDiacritics(fileName) + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(fileDesc.getContentSize())
                    .contentType(MediaType.parseMediaType(fileDesc.getContentType()))
                    .body(new InputStreamResource(fileInputStream));

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static String removeDiacritics(String input) {
        if (input == null) {
            return null;
        }

        // Chuyển chuỗi thành dạng chuẩn hóa NFD (Normalization Form Decomposition)
        String normalized = Normalizer.normalize(input, Form.NFD);

        // Loại bỏ các ký tự dấu bằng cách thay thế bằng một chuỗi không chứa các ký tự Unicode có dấu
        String withoutDiacritics = normalized.replaceAll("\\p{M}", "");

        return withoutDiacritics;
    }

}
