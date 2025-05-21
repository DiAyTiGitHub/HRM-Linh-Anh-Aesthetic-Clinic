package com.globits.hr.domain;

import java.util.Date;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbl_mail_log")
public class MailLog extends BaseObject {
	@Column(name = "sent_from")
	private String from;
	@Column(name = "send_to")
	private String[] to;
	@Column(name = "send_date")
	private Date sendDate; // Ngày gửi tin nhắn
	@Column(name = "cc")
	private String[] cc;
	@Column(name = "bcc")
	private String[] bcc;
	@Column(name = "subject")
	private String subject;

	@Column(name = "body",columnDefinition = "TEXT")
	private String body;
	@Column(name = "status")
	private String status; // pass, error
	@Lob
	@Column(name = "file_paths")
	private String filePaths;
	//mau tin nhan
	@Column(name = "content_template_id")
	private String contentTemplateId;
	
	@Column(name = "template_name",columnDefinition = "TEXT")
	private String templateName;
	
	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
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

	public String getContentTemplateId() {
		return contentTemplateId;
	}

	public void setContentTemplateId(String contentTemplateId) {
		this.contentTemplateId = contentTemplateId;
	}

	public String getFilePaths() {
		return filePaths;
	}

	public void setFilePaths(String filePaths) {
		this.filePaths = filePaths;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	
}
