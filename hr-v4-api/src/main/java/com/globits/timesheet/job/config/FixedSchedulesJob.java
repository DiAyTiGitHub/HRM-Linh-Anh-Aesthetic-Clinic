package com.globits.timesheet.job.config;

import com.globits.hr.HrConstants;
import com.globits.hr.dto.LabourAgreementDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.SystemConfigDto;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.dto.staff.StaffLabourManagementDto;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.SystemConfigRepository;
import com.globits.hr.service.StaffService;
import com.globits.hr.service.StaffWorkScheduleService;
import com.globits.hr.service.SystemConfigService;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.timesheet.repository.SyncLogTimeSheetRepository;
import com.globits.timesheet.service.TimeSheetDetailService;
import jakarta.persistence.EntityManager;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Configuration
@EnableScheduling
public class FixedSchedulesJob {
    private Logger logger = LoggerFactory.getLogger(getClass());

    // chạy đồng thời
    @Autowired
    private Environment env;

    @Autowired
    private TimeSheetDetailService timeSheetDetailService;

    @Autowired
    private SyncLogTimeSheetRepository syncLogTimeSheetRepository;

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffService staffService;

    @Autowired
    private StaffWorkScheduleService staffWorkScheduleService;

    @Autowired
    private EntityManager entityManager;


    @Scheduled(cron = "0 * * * * *") // chạy mỗi phút
    public void generateFixedScheduleForStaff() {
        try {
            // logger.info("Check to generateFixedScheduleForStaff");

            SystemConfigDto dayConfig = systemConfigService.getByKeyCode(HrConstants.SystemConfigCode.GEN_FIXED_SCHEDULES_DAY.getCode());
            SystemConfigDto hourMinuteConfig = systemConfigService.getByKeyCode(HrConstants.SystemConfigCode.GEN_FIXED_SCHEDULES_HOUR_AND_MINUTE.getCode());

            if (dayConfig == null
                    || hourMinuteConfig == null
                    || !StringUtils.hasText(dayConfig.getConfigValue())
                    || !StringUtils.hasText(hourMinuteConfig.getConfigValue())) {
                logger.error("Không tìm thấy đủ cấu hình để tạo lịch làm việc cố định cho nhân viên");
                return;
            }

            int configDay = Integer.parseInt(dayConfig.getConfigValue()); // ngày trong tháng: 1 - 31

            String[] hourMinuteParts = hourMinuteConfig.getConfigValue().split(":");
            if (hourMinuteParts.length != 2) {
                logger.error("Cấu hình giờ-phút không đúng định dạng HH:mm");
                return;
            }

            int configHour = Integer.parseInt(hourMinuteParts[0]);
            int configMinute = Integer.parseInt(hourMinuteParts[1]);

            LocalDateTime now = LocalDateTime.now();
            int currentDayOfMonth = now.getDayOfMonth();
            int currentHour = now.getHour();
            int currentMinute = now.getMinute();

            if (currentDayOfMonth == configDay && currentHour == configHour && currentMinute == configMinute) {
                logger.info("CATCHED => Đúng ngày giờ cấu hình. Bắt đầu thực hiện job tạo lịch làm việc cố định cho các nhân viên có lịch làm việc cố định");
                this.generateFixedSchedulesStaffs();
            } else {
                // logger.info("Chưa đến ngày/giờ cấu hình, bỏ qua job.");
            }
        } catch (Exception exception) {
            logger.error("Lỗi khi thực hiện job generateFixedScheduleForStaff", exception);
        }
    }


    private void generateFixedSchedulesStaffs() {
        SearchStaffDto searchStaffDto = new SearchStaffDto();

        searchStaffDto.setPageSize(50);
        searchStaffDto.setStaffWorkShiftType(HrConstants.StaffWorkShiftType.FIXED.getValue());
        searchStaffDto.setStaffLeaveShiftType(HrConstants.StaffLeaveShiftType.FIXED.getValue());

        int pageIndex = 1;
        searchStaffDto.setPageIndex(pageIndex);


        // Ngày đầu trong tháng tiếp theo
        Date firstDayNextMonth = DateTimeUtil.getFirstDayOfNextMonth();
        // Ngày cuối cùng trong tháng tiếp theo
        Date lastDayNextMonth = DateTimeUtil.getLastDayOfNextMonth();

        boolean hasNextPage = true;
        long startTime = System.nanoTime();

        while (hasNextPage) {
            // searchStaffDto = new SearchStaffDto();
            searchStaffDto.setPageIndex(pageIndex);
            searchStaffDto.setPageSize(50);

            Page<StaffDto> staffPage = staffService.searchByPage(searchStaffDto);

            if (staffPage == null || staffPage.isEmpty()) {
                break;
            }

            for (StaffDto staff : staffPage.getContent()) {
                logger.info("Tạo lịch làm việc cho nhân viên " + staff.getStaffCode() + " " + staff.getDisplayName());

                staffWorkScheduleService.generateFixSchedulesInRangeTimeForStaff(staff.getId(), firstDayNextMonth, lastDayNextMonth);
            }

            entityManager.clear();

            hasNextPage = staffPage.hasNext(); // Kiểm tra xem còn trang tiếp theo không
            pageIndex++; // Tăng pageIndex để lấy trang tiếp theo
        }
        long endTime = System.nanoTime();
        long elapsedTimeMs = (endTime - startTime) / 1_000_000;

        logger.info("Tạo lịch làm việc cố định cho nhân viên - Xử lý mất {} ms ", elapsedTimeMs);
    }

}
