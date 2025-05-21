package com.globits.hr.service;

import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.SendMailCandidateDto;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface SendMailCandidateService {

    ApiResponse<Boolean> sendMail(SendMailCandidateDto dto) throws TemplateException, IOException, MessagingException;
    ApiResponse<Boolean> sendMailEdit(SendMailCandidateDto dto) throws TemplateException, IOException, MessagingException;

    ApiResponse<String> getMailPreview(SendMailCandidateDto dto) throws MessagingException, TemplateException, IOException;
}
