package com.globits.timesheet.job.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.globits.hr.dto.SystemConfigDto;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.StaffService;
import com.globits.hr.service.StaffWorkScheduleService;
import com.globits.hr.service.SystemConfigService;
import org.hibernate.sql.exec.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.SystemConfig;
import com.globits.hr.repository.SystemConfigRepository;
import com.globits.timesheet.domain.SyncLogTimeSheet;
import com.globits.timesheet.dto.TimeSheetDetailDto;
import com.globits.timesheet.dto.api.SearchTimeSheetApiDto;
import com.globits.timesheet.repository.SyncLogTimeSheetRepository;
import com.globits.timesheet.service.TimeSheetDetailService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jakarta.transaction.Transactional;
import org.springframework.util.StringUtils;

@Transactional
@Service
public class SampleJobService {
    public static final long EXECUTION_TIME = 10000L;

    private Logger logger = LoggerFactory.getLogger(getClass());
    private int currentPage = 0;
    private int pageSize = 40;
    private AtomicInteger count = new AtomicInteger();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10); // Giới hạn số lượng thread

    // chạy đồng thời
    @Autowired
    private Environment env;

    @Autowired
    private TimeSheetDetailService timeSheetDetailService;

    @Autowired
    private SyncLogTimeSheetRepository syncLogTimeSheetRepository;

    @Autowired
    private SystemConfigRepository systemConfigRepository;


    public void executeSampleJob() {
        try {
            // sync cham cong
            System.out.print("SYNC CHAM CONG!!");
//			this.syncTimeSheet();
            this.processTimeSheetData();
            Thread.sleep(EXECUTION_TIME);
        } catch (Exception e) {
            logger.error("Error while executing sample job", e);
        } finally {
            count.incrementAndGet();
            logger.info("Sample job has finished...");
        }
    }

    public int getNumberOfInvocations() {
        return count.get();
    }


    public void processTimeSheetData() throws ExecutionException, InterruptedException {
        try {
            System.out.println("Start sync timesheet");
            Date start = new Date();

            boolean isSyncPatientReport = false;
            SystemConfig config = getConfigByKey(HrConstants.HR_SYNC_TIMESHEET, "false", "Đồng bộ time sheet");
            try {
                isSyncPatientReport = Boolean.valueOf(config.getConfigValue());
            } catch (Exception ex) {

            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = new Date();
            Date endDate = new Date();
            Integer hour = 0;
            String fromdate = "";
            String todate = "";
            String urlAPITimeSheet = "";
            Boolean isTimeLock=true;
            config = getConfigByKey(HrConstants.HR_FROMDATE_SYNC_TIMESHEET, dateFormat.format(startDate),
                    "Đồng bộ Time Sheet - Ngày bắt đầu");
            if (config != null) {
                try {
                    fromdate = config.getConfigValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            config = getConfigByKey(HrConstants.HR_TODATE_SYNC_TIMESHEET, dateFormat.format(endDate),
                    "Đồng bộ Time sheet - Ngày kết thúc");
            if (config != null) {
                try {
                    todate = config.getConfigValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            SystemConfig configValue = getConfigByKey(HrConstants.HR_HOUR_SYNC_TIMESHEET, hour.toString(),
                    "Giờ đồng bộ");

            if (configValue != null) {
                try {
                    hour = Integer.valueOf(configValue.getConfigValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            configValue = getConfigByKey(HrConstants.HR_URL_TIMESHEET, HrConstants.HR_URL_TIMESHEET_VALUE,
                    "Đường dẫn đồng bộ");

            if (configValue != null) {
                try {
                    urlAPITimeSheet = configValue.getConfigValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            configValue = getConfigByKey(HrConstants.HR_TIMESHEET_ONE_TIME_LOCK, "true",
                    "Chấm công chỉ tính 1 lần/ngày");

            if (configValue != null) {
                try {
                	isTimeLock =Boolean.valueOf(configValue.getConfigValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Integer hourDay = start.getHours();
            if (hourDay != null && hourDay == 0) {// chuyen sang ngay moi
                this.saveUpdateSystemConfig(config, HrConstants.HR_FROMDATE_SYNC_TIMESHEET,
                        dateFormat.format(startDate), "Đồng bộ Time Sheet - Ngày bắt đầu");
                this.saveUpdateSystemConfig(config, HrConstants.HR_TODATE_SYNC_TIMESHEET, dateFormat.format(endDate),
                        "Đồng bộ Time sheet - Ngày kết thúc");
            }
            if (isSyncPatientReport && hourDay != null && hourDay >= hour) {
                // Tạo AtomicInteger để đếm số bản ghi được tạo
                AtomicInteger recordCount = new AtomicInteger(0);
                LocalDate from = LocalDate.parse(fromdate, formatter);
                LocalDate to = LocalDate.parse(todate, formatter);

                // Tạo danh sách các công việc bất đồng bộ cho từng ngày
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
                    // Đảm bảo date.format(formatter) là một giá trị không thay đổi
                    String formattedDate = date.format(formatter); // Chuyển date thành chuỗi một lần
                    String url = urlAPITimeSheet;
                    boolean isOneTimeLock = isTimeLock;

                    // Kiểm tra xem đã có log đồng bộ cho ngày này chưa
//                    boolean isAlreadySynced = syncLogTimeSheetRepository
//                            .existsByDateSyncAndStatus(java.sql.Date.valueOf(formattedDate), "SUCCESS");

                    boolean isAlreadySynced = false;

                    // Lấy ngày hiện tại
                    LocalDate currentDate = LocalDate.now();
                    // Nếu ngày đã đồng bộ hoặc không phải là ngày hiện tại, bỏ qua đồng bộ
                    if (isAlreadySynced && !formattedDate.equals(currentDate.format(formatter))) {
                        System.out.println("Already synced for date: " + formattedDate);
                        continue; // Bỏ qua, không thực hiện đồng bộ
                    }
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            // Tạo DTO cho API call
                            SearchTimeSheetApiDto dto = new SearchTimeSheetApiDto();
                            dto.setFromdate(formattedDate); // Sử dụng formattedDate thay vì date trực tiếp
                            dto.setTodate(formattedDate);
                            dto.setUrl(url);
                            dto.setIsOneTimeLock(isOneTimeLock);

                            // Call API và xử lý dữ liệu
                            List<TimeSheetDetailDto> data = timeSheetDetailService
                                    .convertTimeSheetDetailByApiTimeSheet(dto);
                            List<String> staffIds = new ArrayList<>(); // Danh sách các staffId
                            Integer number = data != null ? data.size() : 0; // Số bản ghi từ API
                            if (data != null && !data.isEmpty()) {
                                // Lưu hoặc cập nhật dữ liệu
                                data.parallelStream().forEach(timeSheetDetailDto -> {// Chạy saveOrUpdate nhiều dòng
                                    // cùng lúc
                                    // bằng multi-thread
                                    String employeeId = timeSheetDetailDto.getEmployee().getId().toString();
                                    staffIds.add(employeeId); // Thêm staffId vào danh sách
                                    synchronized (employeeId.intern()) { // Dùng mã nhân viên làm khóa để đồng bộ hóa
                                        // theo từng
                                        // nhân viên
                                        timeSheetDetailService.saveOrUpdate(timeSheetDetailDto, null);
                                    }
                                });
                            }
                            // Chuyển danh sách staffIds thành chuỗi JSON
                            ObjectMapper objectMapper = new ObjectMapper();
                            String staffIdsJson = objectMapper.writeValueAsString(staffIds);
                            // Tạo và lưu log sau khi đồng bộ hoàn tất cho ngày này
                            SyncLogTimeSheet log = new SyncLogTimeSheet();
                            log.setDateSync(java.sql.Date.valueOf(formattedDate)); // Ngày đồng bộ
                            log.setNumberApi(number); // Số bản ghi từ API
                            log.setNumberRecord(recordCount.get()); // Số bản ghi đã lưu
                            log.setStatus("SUCCESS"); // Trạng thái thành công
                            log.setStaffResponse(staffIdsJson); // Bạn có thể lưu thêm thông tin phản hồi từ API nếu cần
                            syncLogTimeSheetRepository.save(log); // Lưu log vào database
                        } catch (Exception e) {
                            // Xử lý lỗi nếu có
                            e.printStackTrace();
                            // Tạo và lưu log khi có lỗi
                            SyncLogTimeSheet log = new SyncLogTimeSheet();
                            log.setDateSync(java.sql.Date.valueOf(formattedDate)); // Ngày đồng bộ
                            log.setNumberApi(0); // Không có dữ liệu từ API
                            log.setNumberRecord(0); // Không có bản ghi lưu
                            log.setStatus("ERROR"); // Trạng thái lỗi
                            log.setStaffResponse(e.getMessage()); // Lưu thông báo lỗi nếu có
                            syncLogTimeSheetRepository.save(log); // Lưu log vào database
                        }
                    }, executorService); // Sử dụng thread pool để quản lý số lượng thread
                    futures.add(future);
                }

                // Chờ tất cả các công việc hoàn thành
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                // Sau khi tất cả công việc hoàn thành
                System.out.println("Processing completed.");
                Date endate = new Date();
                System.out.println("Thoi gian sync timesheet " + recordCount.get() + " mất "
                        + (endate.getTime() - start.getTime()) / 1000 + " giây");
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    private SystemConfig getConfigByKey(String configKey, String configValue, String note) {
        List<SystemConfig> configs = systemConfigRepository.getByConfigKey(configKey);
        if (configs != null && configs.size() > 0) {
            return configs.get(0);
        } else {
            SystemConfig config = new SystemConfig();
            return this.saveSystemConfig(config, configKey, configValue, note);
        }
    }

    private SystemConfig saveSystemConfig(SystemConfig config, String configKey, String configValue, String note) {
        config.setConfigValue(configValue);
        config.setConfigKey(configKey);
        config.setNote(note);
        config = systemConfigRepository.save(config);
        return config;
    }

    private SystemConfig saveUpdateSystemConfig(SystemConfig config, String configKey, String configValue,
                                                String note) {
        List<SystemConfig> configs = systemConfigRepository.getByConfigKey(configKey);
        if (configs != null && !configs.isEmpty()) {
            config = configs.get(0);
        }
        config.setConfigValue(configValue);
        config.setConfigKey(configKey);
        config.setNote(note);
        config = systemConfigRepository.save(config);
        return config;
    }
}
