package com.globits.hr.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import com.globits.hr.dto.StaffSearchDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.timesheet.service.TimeSheetService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/public/hr/file")
public class RestHRPublicController {
    private static final Logger logger = LoggerFactory.getLogger(RestHRPublicController.class);
    @Autowired
    private Environment env;
    @Autowired
    private TimeSheetService timeSheetService;

    @RequestMapping(path = "/getImage/{filename}/{type}", method = RequestMethod.GET)
    public void getImage(HttpServletResponse response, @PathVariable String filename, @PathVariable String type) {
        try {
            String path = "";
            if (env.getProperty("hrm.file.folder") != null) {
                path = env.getProperty("hrm.file.folder");
            }
            File file = new File(path + filename + "." + type);
            String contentType = "application/octet-stream";
            response.setContentType(contentType);
            OutputStream out = response.getOutputStream();
            FileInputStream in = new FileInputStream(file);
            IOUtils.copy(in, out);
            out.close();
            in.close();
        } catch (Exception e){
            logger.error("Error get image: {}", e.getMessage(), e.getLocalizedMessage());
        }
    }
    @GetMapping("/get-ip")
    public ResponseEntity<String> getClientIp(HttpServletRequest request) {
        return new ResponseEntity<>(timeSheetService.getClientIpV2(request), HttpStatus.OK);
    }
    @RequestMapping(value = "/test-post", method = RequestMethod.POST)
    public ResponseEntity<String> testPost( @RequestBody SearchDto dto) {
        return new ResponseEntity<>(dto.getKeyword(), HttpStatus.OK);
    }
    @GetMapping("/get-client-ip")
    public ResponseEntity<String> getClientIp(HttpServletRequest request, @RequestHeader(value = "X-Forwarded-For", required = false) String xForwardedFor) {
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            String str = "Client IP (Proxy): " + xForwardedFor;
            return new ResponseEntity<>(str, HttpStatus.OK);
        }
        String str = "Client IP: " + request.getRemoteAddr();
        return new ResponseEntity<>(str, HttpStatus.OK);
    }
    @RequestMapping(path = "/get-image-signature", method = RequestMethod.POST)
    public ResponseEntity<String> getImageBySignature(@RequestBody String base64Image) {
        try {
            String path = "";
            if (env.getProperty("hrm.file.folder") != null) {
                path = env.getProperty("hrm.file.folder");
            }
            path +="decoded_image.png";
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
            Files.write(Paths.get(path), decodedBytes);
            return new ResponseEntity<>(path, HttpStatus.OK);
        } catch (Exception e){
            logger.error("Error get image: {}", e.getMessage(), e.getLocalizedMessage());
            return null;
        }
    }
}
