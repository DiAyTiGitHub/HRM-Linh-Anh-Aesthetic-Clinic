package com.globits.hr.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.globits.core.dto.BaseObjectDto;

import com.globits.core.dto.FileDescriptionDto;

public class MailInfoDto extends BaseObjectDto {
    private String from = "GlobitsHR";
    private Date sendDate = new Date();
    private String[] to = {};
    private String[] cc = {};
    private String[] bcc = {};
    private String subject;
    private String body;
    private List<FileDescriptionDto> files = new ArrayList<>();
    private String status; // pass, error
    private String templateName;
    private String contentTemplateId;

    public MailInfoDto() {
    }

    public MailInfoDto(String[] to, String subject) {
        super();
        this.to = to;
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public String[] getCc() {
        return cc;
    }

    public void setCc(String[] cc) {
        this.cc = cc;
    }

    public String[] getBcc() {
        return bcc;
    }

    public void setBcc(String[] bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<FileDescriptionDto> getFiles() {
        return files;
    }

    public void setFiles(List<FileDescriptionDto> files) {
        this.files = files;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

	public String getContentTemplateId() {
		return contentTemplateId;
	}

	public void setContentTemplateId(String contentTemplateId) {
		this.contentTemplateId = contentTemplateId;
	}

}
