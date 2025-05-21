package com.globits.hr.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.globits.hr.dto.*;
import com.globits.hr.domain.*;
import com.globits.hr.repository.WorkplaceRepository;
import jakarta.activation.DataSource;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.globits.core.service.MailService;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Candidate;
import com.globits.hr.domain.CandidateRecruitmentRound;
import com.globits.hr.domain.PositionStaff;
import com.globits.hr.domain.PositionTitle;
import com.globits.hr.domain.RecruitmentPlan;
import com.globits.hr.domain.RecruitmentRequest;
import com.globits.hr.domain.RecruitmentRequestItem;
import com.globits.hr.domain.RecruitmentRound;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.MailInfoDto;
import com.globits.hr.dto.SendMailCandidateDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.repository.CandidateRepository;
import com.globits.hr.service.MailLogService;
import com.globits.hr.service.SendMailCandidateService;
import com.globits.hr.service.UserExtService;
import com.globits.template.domain.ContentTemplate;
import com.globits.template.repository.ContentTemplateRepository;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SendMailCandidateServiceImpl implements SendMailCandidateService {

    @Autowired
    private ContentTemplateRepository contentTemplateRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserExtService userExtService;
    @Autowired
    private MailLogService mailLogService;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    JavaMailSender mailSender;

    @Override
    public ApiResponse<Boolean> sendMail(SendMailCandidateDto dto)
            throws TemplateException, IOException, MessagingException {
        if (dto.getTemplateId() == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy mẫu", null);
        }

        final ContentTemplate template = contentTemplateRepository.findById(dto.getTemplateId()).orElse(null);
        if (template == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy mẫu", null);
        }

        final List<Candidate> candidates = candidateRepository.findAllById(dto.getCandidateIds());
        if (CollectionUtils.isEmpty(candidates)) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy hồ sơ nào", false);
        }

        final String fromMail = getSenderEmail();
        final String subject = template.getName();
        // Tạo thread pool cố định (10 luồng, bạn có thể điều chỉnh tùy hệ thống)
        ExecutorService executor = Executors.newFixedThreadPool(10);
        for (Candidate candidate : candidates) {
            if (StringUtils.hasText(candidate.getEmail())) {
                Map<String, Object> model = buildTemplateModel(candidate);
                String body = generateByTemplateContent(template.getCode(), decodeHtmlContent(template.getContent()),
                        model);
                MailInfoDto mailDto = new MailInfoDto();
                mailDto.setBody(body);
                mailDto.setSubject(subject);
                mailDto.setTo(new String[]{candidate.getEmail()});
                mailDto.setSendDate(new Date());
                if (template.getId() != null)
                    mailDto.setContentTemplateId(template.getId().toString());
                mailDto.setTemplateName(template.getName());
                executor.submit(() -> {
                    try {
                        mailService.sendEmail(fromMail, candidate.getEmail(), null, null, subject, body, true, null);
                        mailDto.setStatus("PASS");
                        mailLogService.saveOrUpdate(mailDto, null);
                    } catch (MessagingException e) {
                        // Ghi log lỗi cụ thể cho từng email
                        System.err.println("Lỗi gửi mail tới: " + candidate.getEmail());
                        mailDto.setStatus("ERROR");
                        mailLogService.saveOrUpdate(mailDto, null);
                        e.printStackTrace();
                    }
                });
            }
        }

        return new ApiResponse<>(HttpStatus.SC_OK, "Thành công", true);
    }

    @Override
    public ApiResponse<String> getMailPreview(SendMailCandidateDto dto) throws TemplateException, IOException {
        if (dto.getTemplateId() == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy mẫu", null);
        }

        final ContentTemplate template = contentTemplateRepository.findById(dto.getTemplateId()).orElse(null);
        if (template == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy mẫu", null);
        }

        final Candidate candidate = candidateRepository.findById(dto.getCandidateId()).orElse(null);
        if (candidate == null) {
            return new ApiResponse<String>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy hồ sơ nào", null);
        }
        Map<String, Object> model = buildTemplateModel(candidate);
        String body = generateByTemplateContent(template.getCode(), decodeHtmlContent(template.getContent()), model);
        return new ApiResponse<>(HttpStatus.SC_OK, "OK", body);
    }

    private String getSenderEmail() {
        StaffDto staffDto = userExtService.getCurrentStaff();
        return (staffDto != null && StringUtils.hasText(staffDto.getEmail())) ? staffDto.getEmail() : "globits.document@gmail.com";
    }

    void initTemplateModel(Map<String, Object> model) {
        model.put("candidateName", null);
        model.put("workLocation", null);
        model.put("positionTitle", null);
        model.put("hrStaffName", null);
        model.put("hrStaffPhoneNumber", null);
        model.put("directManagerName", null);
        model.put("directManagerPhoneNumber", null);
        model.put("replyDeadline", null);
        model.put("interviewAddress", "");
        model.put("interviewDate", null);
        model.put("interviewFormat", null);
        model.put("interviewers", null);
        model.put("startDate", null);
    }

    private Map<String, Object> buildTemplateModel(Candidate candidate) {
        Map<String, Object> model = new HashMap<>();
        this.initTemplateModel(model);

        // Candidate info
        if (candidate != null) {
            String candidateName = candidate.getDisplayName();
            if (candidateName != null) {
                model.put("candidateName", candidateName);
            }

            RecruitmentPlan plan = candidate.getRecruitmentPlan();
            if (plan != null) {
                RecruitmentRequest request = plan.getRecruitmentRequest();
                if (request != null) {
                    if (request.getWorkPlace() != null) {
                        String workLocation = request.getWorkPlace().getName();
                        if (workLocation != null) {
                            model.put("workLocation", workLocation);
                        }
                    }
                    // Position title
                    String positionTitle = Optional.of(new ArrayList<>(request.getRecruitmentRequestItems()))
                            .filter(list -> !list.isEmpty())
                            .map(list -> list.get(0))
                            .map(RecruitmentRequestItem::getPositionTitle)
                            .map(PositionTitle::getName)
                            .orElse("Chưa xác định");
                    if (!positionTitle.isEmpty()) {
                        model.put("positionTitle", positionTitle);
                    }

                    // HR staff info
                    Staff hrStaff = request.getPersonInCharge();
                    if (hrStaff != null) {
                        String hrStaffName = hrStaff.getDisplayName();
                        String hrStaffPhoneNumber = hrStaff.getPhoneNumber();
                        if (hrStaffName != null) {
                            model.put("hrStaffName", hrStaffName);
                        }
                        if (hrStaffPhoneNumber != null) {
                            model.put("hrStaffPhoneNumber", hrStaffPhoneNumber);
                        }

                        model.put("directManagerName", hrStaffName != null ? hrStaffName : "Chưa xác định");
                        model.put("directManagerPhoneNumber", hrStaffPhoneNumber != null ? hrStaffPhoneNumber : "Chưa xác định");
                    }
                }
            }

            model.put("replyDeadline", new Date()); // Always add the reply deadline as the current date

            // Interview round info
            Set<CandidateRecruitmentRound> rounds = Optional.ofNullable(candidate.getCandidateRecruitmentRounds())
                    .filter(list -> !list.isEmpty())
                    .orElse(new HashSet<>());

            if (!rounds.isEmpty()) {
                CandidateRecruitmentRound latestRound = rounds.stream()
                        .filter(Objects::nonNull)
                        .max(Comparator.comparingInt(crr -> {
                            RecruitmentRound rr = crr.getRecruitmentRound();
                            return rr != null ? rr.getRoundOrder() : Integer.MAX_VALUE;
                        }))
                        .orElse(null);

                if (latestRound != null) {
                    String interviewAddress = null;
                    if (latestRound.getWorkplace() != null && latestRound.getWorkplace().getName() != null) {
                        interviewAddress = latestRound.getWorkplace().getName();
                    }
                    // địa điểm dự thi phân bổ
                    if (interviewAddress != null) {
                        model.put("interviewAddress", interviewAddress);
                    }
                    if (latestRound.getActualTakePlaceDate() != null) {
                        model.put("startDate", latestRound.getActualTakePlaceDate());
                    }
                    RecruitmentRound round = latestRound.getRecruitmentRound();
                    Date interviewDate = latestRound.getActualTakePlaceDate();
                    if (interviewDate == null && round != null) {
                        interviewDate = round.getTakePlaceDate();
                    }
                    if (interviewDate != null) {
                        model.put("interviewDate", interviewDate);
                    }

                    // Interview format
                    String interviewFormat = "";
                    if (latestRound.getRecruitmentType() != null) {
                        HrConstants.RecruitmentType type = HrConstants.RecruitmentType
                                .fromValue(latestRound.getRecruitmentType());
                        if (type != null) {
                            interviewFormat = type.name();
                        }
                    }
                    if (!interviewFormat.isEmpty()) {
                        model.put("interviewFormat", interviewFormat);
                    }

                    List<Map<String, String>> interviewers = getInterviewers(latestRound);
                    if (!interviewers.isEmpty()) {
                        model.put("interviewers", interviewers);
                    }
                }
            }
        }

        return model;
    }

    private List<Map<String, String>> getInterviewers(CandidateRecruitmentRound round) {
        List<Map<String, String>> interviewers = new ArrayList<>();

        if (round != null && round.getRecruitmentRound() != null) {
            Set<Staff> people = round.getRecruitmentRound().getParticipatingPeople();
            if (people != null && !people.isEmpty()) {
                for (Staff staff : people) {
                    if (staff != null) {
                        Map<String, String> interviewer = new HashMap<>();
                        String staffName = staff.getDisplayName();
                        if (staffName != null) {
                            interviewer.put("name", staffName);
                        }

                        String title = Optional.ofNullable(staff.getPositions())
                                .flatMap(positions -> positions.stream()
                                        .filter(PositionStaff::getMainPosition)
                                        .findFirst())
                                .map(ps -> ps.getPosition().getTitle().getName())
                                .orElse("");

                        if (!title.isEmpty()) {
                            interviewer.put("title", title);
                        }
                        interviewers.add(interviewer);
                    }
                }
            }
        }

        return interviewers;
    }


    public String generateByTemplateContent(String templateName, String templateContent, Map<String, Object> model)
            throws IOException, TemplateException {
        Template template = new Template(templateName, new StringReader(templateContent),
                new Configuration(Configuration.VERSION_2_3_30));
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    public String decodeHtmlContent(String encodedContent) {
        return StringEscapeUtils.unescapeHtml4(encodedContent);
    }

    @Override
    public ApiResponse<Boolean> sendMailEdit(SendMailCandidateDto dto)
            throws TemplateException, IOException, MessagingException {

        if (dto.getTemplateId() == null) {
            return new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, "Không tìm thấy mẫu", null);
        }

        final String fromMail = getSenderEmail();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (CandidateDto candidate : dto.getCandidate()) {
            if (StringUtils.hasText(candidate.getEmail())) {
                Candidate entity = candidateRepository.findById(candidate.getId()).orElse(null);
                if (entity == null) continue;
                final String subject = candidate.getTemplate().getName();
                String bodyEdit = candidate.getTemplate().getContent();
                if (!candidate.getIsEdit()) {
                    Map<String, Object> model = buildTemplateModel(entity);
                    bodyEdit = generateByTemplateContent(
                            candidate.getTemplate().getCode(),
                            decodeHtmlContent(candidate.getTemplate().getContent()),
                            model
                    );
                }
                final String body = bodyEdit;
                MailInfoDto mailDto = new MailInfoDto();
                mailDto.setBody(body);
                mailDto.setSubject(subject);
                mailDto.setTo(new String[]{candidate.getEmail()});
                mailDto.setSendDate(new Date());
                if (candidate.getTemplate() != null) {
                    mailDto.setContentTemplateId(candidate.getTemplate().getId().toString());
                    mailDto.setTemplateName(candidate.getTemplate().getName());
                    if (candidate.getTemplate().getCode().equals(HrConstants.MAU_0003)) {
                        entity.setStatus(HrConstants.CandidateStatus.SEND_OFFER.getValue());
                        entity.setSendMailOffer(true);
                    }
                }
                candidateRepository.save(entity);
                // Đọc trước dữ liệu file
                List<SimpleAttachment> attachments = new ArrayList<>();
                if (!CollectionUtils.isEmpty(candidate.getFiles())) {
                    for (MultipartFile file : candidate.getFiles()) {
                        if (file != null && !file.isEmpty()) {
                            attachments.add(new SimpleAttachment(
                                    file.getOriginalFilename(),
                                    file.getContentType(),
                                    file.getBytes()
                            ));
                        }
                    }
                }

                final List<SimpleAttachment> finalAttachments = attachments;

                executor.submit(() -> {
                    try {
                        sendEmailWithBytes(fromMail, candidate.getEmail(), null, null,
                                subject, body, true, finalAttachments);

                        mailDto.setStatus("PASS");
                        mailLogService.saveOrUpdate(mailDto, null);
                    } catch (Exception e) {
                        System.err.println("Lỗi gửi mail tới: " + candidate.getEmail());
                        mailDto.setStatus("ERROR");
                        mailLogService.saveOrUpdate(mailDto, null);
                        e.printStackTrace();
                    }
                });
            }
        }

        return new ApiResponse<>(HttpStatus.SC_OK, "Thành công", true);
    }


    public void sendEmailWithBytes(String fromEmail, String toEmail, String ccEmail, String bccEmail,
                                   String subject, String text, boolean html, List<SimpleAttachment> attachments)
            throws MessagingException {

        MimeMessage message = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject(subject);
        helper.setFrom(fromEmail);

        if (toEmail != null) {
            helper.setTo(InternetAddress.parse(toEmail));
        }
        if (ccEmail != null) {
            helper.setCc(InternetAddress.parse(ccEmail));
        }
        if (bccEmail != null) {
            helper.setBcc(InternetAddress.parse(bccEmail));
        }

        helper.setReplyTo(fromEmail);
        helper.setText(text, html);

        if (attachments != null) {
            for (SimpleAttachment att : attachments) {
                if (att.getFilename() != null && att.getContent() != null) {
                    DataSource dataSource = new ByteArrayDataSource(att.getContent(), att.getContentType());
                    helper.addAttachment(att.getFilename(), dataSource);
                }
            }
        }

        this.mailSender.send(message);
    }

}
