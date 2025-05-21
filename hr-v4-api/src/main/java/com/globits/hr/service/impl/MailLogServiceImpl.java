package com.globits.hr.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.globits.core.dto.FileDescriptionDto;
import com.globits.core.service.FileDescriptionService;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.MailLog;
import com.globits.hr.dto.MailInfoDto;
import com.globits.hr.repository.MailLogRepository;
import com.globits.hr.service.MailLogService;

@Transactional
@Service
public class MailLogServiceImpl extends GenericServiceImpl<MailLog, UUID> implements MailLogService {
	
	@Autowired
	private Environment env;
	@Autowired
	private FileDescriptionService fileService;
	@Autowired
	private MailLogRepository mailLogRepository;
	
	

	@Override
	public MailInfoDto saveOrUpdate(MailInfoDto dto,UUID id) {
		MailLog entity = null;
		if(id != null){
			entity = mailLogRepository.findById(id).orElse(null);
		}
		if(entity == null){
			entity = new MailLog();
		}
		entity.setSendDate(dto.getSendDate());
		entity.setFrom(dto.getFrom());
		entity.setTo(dto.getTo());
		entity.setBcc(dto.getBcc());
		entity.setCc(dto.getCc());
		entity.setBody(dto.getBody());
		entity.setSubject(dto.getSubject());
		entity.setStatus(dto.getStatus());
		entity.setTemplateName(dto.getTemplateName());
		entity.setContentTemplateId(dto.getContentTemplateId());
		String files = "";
		if(dto.getFiles() != null && dto.getFiles().size() > 0){
			for(FileDescriptionDto descriptionDto: dto.getFiles()){
				if("".equals(files)){
					files += descriptionDto.getFilePath();
				}else{
					files += ";" + descriptionDto.getFilePath();
				}
			}
			entity.setFilePaths(files);
		}
		entity = mailLogRepository.save(entity);
		// Lưu lại danh bạ email
//		String userName = "admin";
//		try {
//			userName = userExtService.getCurrentUserNameStr();
//		} catch (Exception e) {
//
//		}
//		List<String> strings = new ArrayList<String>();
//		if (dto.getTo() != null && dto.getTo().length > 0) {
//			for (String string : dto.getTo()) {
//				strings.add(string);
//			}
//		}
//		if (dto.getCc() != null && dto.getCc().length > 0) {
//			for (String string : dto.getCc()) {
//				strings.add(string);
//			}
//		}
//		if (dto.getBcc() != null && dto.getBcc().length > 0) {
//			for (String string : dto.getBcc()) {
//				if (!"abcz@gmail.com".equals(string)) {
//					strings.add(string);
//				}
//			}
//		}

//		if (strings != null && strings.size() > 0) {
//			for (String s : strings) {
//				FriendsEmailDto friendsEmailDto = new FriendsEmailDto();
//				friendsEmailDto.setEmail(s.toLowerCase());
//				friendsEmailDto.setCreatedBy(userName);
//				friendsEmailService.saveOrUpdate(friendsEmailDto, null);
//			}
//		}


		dto.setId(entity.getId());
		return dto;
	}

	
}
