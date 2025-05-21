package com.globits.hr.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import com.globits.core.dto.FileDescriptionDto;
import com.globits.hr.dto.MailInfoDto;

import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;

import org.thymeleaf.context.Context;
import org.springframework.util.StreamUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;

public class MailUtil {

	private static final SpringTemplateEngine templateEngine = new SpringTemplateEngine();

	public static void sendEmail(MailInfoDto dto, Context context) throws MessagingException {
		System.out.println("Start send email");
		String username = "lamnt21398@gmail.com";
		String password = "ghys ezth immp wobw";

		Session session = createSession(username, password);
		MimeMessage message = new MimeMessage(session);

		MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
		prepareEmail(helper, dto);

		String htmlContent = templateEngine.process(loadHtmlTemplate(dto.getTemplateName()), context);
		helper.setText(htmlContent, true);

		System.out.println("Bắt đầu gửi ");
		Transport.send(message);
		System.out.println("Gửi thành công");
	}

	private static String loadHtmlTemplate(String templateName) {
		try {
			ClassPathResource resource = new ClassPathResource("templates/" + templateName + ".html");
			return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			// Handle exception appropriately
			e.printStackTrace();
			return ""; // or throw exception
		}
	}

	private static Session createSession(String username, String password) {
		Properties prop = new Properties();

		prop.put("mail.smtp.host", "smtp.gmail.com"); // SMTP Host
		prop.put("mail.smtp.port", "587"); // TLS Port
		prop.put("mail.smtp.auth", "true"); // enable authentication
		prop.put("mail.smtp.starttls.enable", "true"); // enable STARTTLS
		prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
		prop.put("mail.smtp.ssl.trust", "*");
		System.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

		return Session.getInstance(prop, new jakarta.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
	}

	private static void prepareEmail(MimeMessageHelper helper, MailInfoDto dto) throws MessagingException {
		helper.setFrom(dto.getFrom());
		helper.setTo(dto.getTo());
		helper.setSubject(dto.getSubject());
		helper.setReplyTo(dto.getFrom());
		if (dto.getCc() != null && dto.getCc().length > 0) {
			helper.setCc(dto.getCc());
		}
		if (dto.getBcc() != null && dto.getBcc().length > 0) {
			helper.setBcc(dto.getBcc());
		}
		if (dto.getFiles() != null && dto.getFiles().size() > 0) {
			for (FileDescriptionDto fileDescription : dto.getFiles()) {
				File file = new File(fileDescription.getFilePath());
				helper.addAttachment(fileDescription.getName().intern(), file);
			}
		}
	}
}
