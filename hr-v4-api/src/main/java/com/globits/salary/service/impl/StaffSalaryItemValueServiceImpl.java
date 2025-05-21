package com.globits.salary.service.impl;

import com.globits.core.domain.FileDescription;
import com.globits.core.dto.FileDescriptionDto;
import com.globits.core.repository.FileDescriptionRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.StaffRepository;
import com.globits.salary.domain.*;
import com.globits.salary.dto.*;
import com.globits.salary.repository.*;
import com.globits.salary.service.StaffSalaryItemValueService;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StaffSalaryItemValueServiceImpl extends GenericServiceImpl<StaffSalaryItemValue, UUID> implements StaffSalaryItemValueService {
    @Autowired
    private StaffSalaryItemValueRepository staffSalaryItemValueRepository;
    @Autowired
    private SalaryItemRepository salaryItemRepository;
    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SalaryTemplateItemRepository salaryTemplateItemRepository;

    @Autowired
    private SalaryTemplateRepository salaryTemplateRepository;

    @Autowired
    private StaffSalaryTemplateRepository staffSalaryTemplateRepository;
    @Autowired
    private FileDescriptionRepository fileDescriptionRepository;

    // Xử lý tạo ra lịch sử giá trị lương
    @Override
    public StaffSalaryItemValueDto preHandleHistoryAndSaveOrUpdate(StaffSalaryItemValueDto dto) {
        if (dto == null) {
            return null;
        }

        boolean isCreateNew = false;

        StaffSalaryItemValue entity = null;
        if (dto.getId() != null) {
            entity = staffSalaryItemValueRepository.findById(dto.getId()).orElse(null);
        }
        if (entity == null) {
            entity = new StaffSalaryItemValue();
            isCreateNew = true;
        }

        // Nếu là tạo mới => Cập nhật thời gian bắt đầu áp dụng
        if (isCreateNew) {
            dto.setFromDate(new Date());
            dto.setIsCurrent(true);

            return this.saveOrUpdateStaffSalaryItemValue(dto);
        }
        // Nếu chưa là tạo mới => Kiểm tra giá trị có bị thay đổi không
        else {
            double oldValue = 0D;
            if (entity.getValue() != null) {
                oldValue = entity.getValue();
            }

            double newValue = 0D;
            if (dto.getValue() != null) {
                newValue = dto.getValue();
            }

            // Nếu giá trị không bị thay đổi => Bỏ qua
            if (oldValue == newValue) {
                return dto;
            }
            // Nếu giá trị bị thay đổi => Tạo lịch sử, đồng thời tạo bản ghi mới lưu giá trị mới{
            else {
                // Kết thúc giá trị cũ
                StaffSalaryItemValueDto mustEndValue = new StaffSalaryItemValueDto(entity, true);
                mustEndValue.setIsCurrent(false);
                mustEndValue.setToDate(new Date());

                this.saveOrUpdateStaffSalaryItemValue(mustEndValue);

                // Tạo giá trị hiện thời mới
                StaffSalaryItemValueDto newStartValue = new StaffSalaryItemValueDto();
                newStartValue.setStaff(dto.getStaff());
                newStartValue.setValue(newValue);
                newStartValue.setSalaryItem(dto.getSalaryItem());
                newStartValue.setFromDate(new Date());
                newStartValue.setTemplateItem(dto.getTemplateItem());
                newStartValue.setIsCurrent(true);
                newStartValue.setCalculationType(dto.getCalculationType());

                return this.saveOrUpdateStaffSalaryItemValue(newStartValue);

            }
        }
    }

    @Override
    @Transactional
    public StaffSalaryItemValueDto save(StaffSalaryItemValueDto dto) {
        if (dto == null) {
            return null;
        }

        StaffSalaryItemValue entity = null;
        if (dto.getId() != null) {
            entity = staffSalaryItemValueRepository.findById(dto.getId()).orElse(null);
        }
        if (entity == null) {
            entity = new StaffSalaryItemValue();
        }

        // Lấy SalaryItem (bắt buộc)
        SalaryItem salaryItem = null;
        if (dto.getSalaryItem() != null && dto.getSalaryItem().getId() != null) {
            salaryItem = salaryItemRepository.findById(dto.getSalaryItem().getId()).orElse(null);
        }
        if (salaryItem == null) {
            throw new IllegalArgumentException("salaryItem - Thành phần lương không hợp lệ.");
        }

        // Lấy Staff (bắt buộc)
        Staff staff = null;
        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        }
        if (staff == null) {
            throw new IllegalArgumentException("Staff không hợp lệ.");
        }

        if (Boolean.TRUE.equals(dto.getIsCurrent())) {
            List<StaffSalaryItemValue> currentItems = staffSalaryItemValueRepository.findByStaffIdAndSalaryItemId(staff.getId(), salaryItem.getId());
            if (currentItems != null && !currentItems.isEmpty()) {
                Date now = new Date();
                for (StaffSalaryItemValue item : currentItems) {
                    if (Boolean.TRUE.equals(item.getIsCurrent()) && (entity.getId() == null || !item.getId().equals(entity.getId()))) {
                        item.setIsCurrent(false);
                        item.setToDate(now);
                    }
                }
                staffSalaryItemValueRepository.saveAll(currentItems); // saveAll nhanh hơn save từng cái
            }
        }

        entity.setIsCurrent(dto.getCurrent());
        entity.setIsCurrent(dto.getIsCurrent());
        entity.setSalaryItem(salaryItem);
        entity.setStaff(staff);
        entity.setCalculationType(dto.getCalculationType());
        entity.setFromDate(dto.getFromDate());
        entity.setToDate(dto.getToDate());
        entity.setFile(null);
        if (dto.getFile() != null && dto.getFile().getId() != null) {
            FileDescription file = fileDescriptionRepository.findById(dto.getFile().getId()).orElse(null);
            entity.setFile(file);
        }

        if (dto.getValue() != null) {
            entity.setValue(dto.getValue());
        } else if (salaryItem.getDefaultValue() != null) {
            entity.setValue(Double.valueOf(salaryItem.getDefaultValue()));
        } else {
            entity.setValue(0.0);
        }

        StaffSalaryItemValue savedEntity = staffSalaryItemValueRepository.save(entity);
        return new StaffSalaryItemValueDto(savedEntity);
    }


    @Override
    public StaffSalaryItemValueDto saveOrUpdateStaffSalaryItemValue(StaffSalaryItemValueDto dto) {
        if (dto == null) {
            return null;
        }

        StaffSalaryItemValue entity = null;
        if (dto.getId() != null) {
            entity = staffSalaryItemValueRepository.findById(dto.getId()).orElse(null);
        }
        if (entity == null) {
            entity = new StaffSalaryItemValue();
        }

        // Lấy template item (bắt buộc phải có)
//        SalaryTemplateItem salaryTemplateItem = null;
//        if (dto.getTemplateItem() != null && dto.getTemplateItem().getId() != null) {
//            salaryTemplateItem = salaryTemplateItemRepository.findById(dto.getTemplateItem().getId()).orElse(null);
//        }
//        if (salaryTemplateItem == null) {
//            throw new IllegalArgumentException("TemplateItem không hợp lệ.");
//        }
        // Lấy template item (bắt buộc phải có)
        SalaryItem salaryItem = null;
        if (dto.getSalaryItem() != null && dto.getSalaryItem().getId() != null) {
            salaryItem = salaryItemRepository.findById(dto.getSalaryItem().getId()).orElse(null);
        }
        if (salaryItem == null) {
            throw new IllegalArgumentException("salaryItem - Thành phần lương không hợp lệ.");
        }

        // Lấy staff (bắt buộc phải có)
        Staff staff = null;
        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        }
        if (staff == null) {
            throw new IllegalArgumentException("Staff không hợp lệ.");
        }

        // Set các thuộc tính còn lại
        //entity.setTemplateItem(salaryTemplateItem);
        entity.setSalaryItem(salaryItem);
        entity.setStaff(staff);
        entity.setCalculationType(dto.getCalculationType());
        entity.setIsCurrent(dto.getIsCurrent());
        entity.setFromDate(dto.getFromDate());
        entity.setToDate(dto.getToDate());

        // Set giá trị value
        if (dto.getValue() != null) {
            entity.setValue(dto.getValue());
        } else if (salaryItem != null && salaryItem.getDefaultValue() != null) {
            entity.setValue(Double.valueOf(salaryItem.getDefaultValue()));
        } else {
            entity.setValue(0.0); // fallback nếu không có giá trị nào
        }

        // Lưu và trả về kết quả
        StaffSalaryItemValue savedEntity = staffSalaryItemValueRepository.save(entity);
        return new StaffSalaryItemValueDto(savedEntity);
    }

    @Override
    public StaffSalaryItemValueDto getById(UUID id) {
        StaffSalaryItemValue entity = staffSalaryItemValueRepository.findById(id).orElse(null);
        if (entity != null) {
            return new StaffSalaryItemValueDto(entity, true);
        }
        return null;
    }

    @Override
    public Boolean deleteStaffSalaryItemValue(UUID id) {
        StaffSalaryItemValue entity = staffSalaryItemValueRepository.findById(id).orElse(null);
        if (entity != null) {
            staffSalaryItemValueRepository.delete(entity);
            return true;
        }
        return false;
    }

    @Override
    public Page<StaffSalaryItemValueDto> pagingStaffSalaryItemValue(SearchDto dto) {
        if (dto == null) {
            return null;
        }

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.createDate ";

        String sqlCount = "select count( entity.id) from StaffSalaryItemValue as entity ";
        String sql = "select new com.globits.salary.dto.StaffSalaryItemValueDto(entity) from StaffSalaryItemValue as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.staff.displayName = :text OR entity.salaryItem.name = :text ) ";
        }


        if (dto.getStaffId() != null) {
            whereClause += " AND ( entity.staff.id = :staffId) ";
        }

        if (dto.getSalaryItemId() != null) {
            whereClause += " AND ( entity.salaryItem.id = :salaryItemId) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, StaffSalaryItemValueDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", "%" + dto.getKeyword() + "%");
            qCount.setParameter("text", "%" + dto.getKeyword() + "%");
        }

        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }

        if (dto.getSalaryItemId() != null) {
            query.setParameter("salaryItemId", dto.getSalaryItemId());
            qCount.setParameter("salaryItemId", dto.getSalaryItemId());
        }

        List<StaffSalaryItemValueDto> entities = new ArrayList<>();

        long count = (long) qCount.getSingleResult();
        Page<StaffSalaryItemValueDto> result;
        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        result = new PageImpl<>(entities, pageable, count);

        return result;

    }

    @Override
    public StaffSalaryItemValueListDto getBySalaryTemplateItem(RequestSalaryValueDto dto) {
        if (dto.getStaff() == null || dto.getSalaryTemplate() == null) {
            return null;
        }

        List<StaffSalaryItemValueDto> itemList = new ArrayList<StaffSalaryItemValueDto>();

        Staff staff = null;
        if (dto.getStaff().getId() != null) {
            staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        }

        if (staff == null) return null;

        StaffDto simpleStaffDto = new StaffDto();
        simpleStaffDto.setId(staff.getId());
        simpleStaffDto.setStaffCode(staff.getStaffCode());
        simpleStaffDto.setDisplayName(staff.getDisplayName());

        SalaryTemplate salaryTemplate = null;
        if (dto.getSalaryTemplate().getId() != null) {
            salaryTemplate = salaryTemplateRepository.findById(dto.getSalaryTemplate().getId()).orElse(null);
        }

        if (salaryTemplate == null) {
            return null;
        }

        StaffSalaryItemValueListDto resultDto = new StaffSalaryItemValueListDto();

        resultDto.setSalaryTemplate(new SalaryTemplateDto(salaryTemplate));
        resultDto.setStaff(new StaffDto(staff));

        if (!CollectionUtils.isEmpty(salaryTemplate.getTemplateItems())) {
            for (SalaryTemplateItem salaryTemplateItem : salaryTemplate.getTemplateItems()) {
                if (salaryTemplateItem == null || salaryTemplateItem.getSalaryItem() == null || salaryTemplateItem.getCalculationType() == null
                        || !salaryTemplateItem.getCalculationType().equals(HrConstants.SalaryItemCalculationType.FIX.getValue()))
                    continue;

                StaffSalaryItemValueDto responseItem = null;

                //List<StaffSalaryItemValue> itemValueList = staffSalaryItemValueRepository.findByStaffIdAndSalaryTemplateItemId(staff.getId(), salaryTemplateItem.getId());
                List<StaffSalaryItemValue> itemValueList = staffSalaryItemValueRepository.findCurrentByStaffIdAndSalaryItemId(staff.getId(), salaryTemplateItem.getSalaryItem().getId());

                if (itemValueList == null || CollectionUtils.isEmpty(itemValueList)) {
                    responseItem = new StaffSalaryItemValueDto();

                    //responseItem.setTemplateItem(new SalaryTemplateItemDto(salaryTemplateItem));
                    responseItem.setSalaryItem(new SalaryItemDto(salaryTemplateItem.getSalaryItem()));
                    responseItem.setStaff(simpleStaffDto);
                    responseItem.setCalculationType(salaryTemplateItem.getCalculationType());
                    responseItem.setFromDate(new Date());
                    responseItem.setIsCurrent(true);

                    responseItem.setValue(0D);

                    if (salaryTemplateItem.getSalaryItem() != null && salaryTemplateItem.getSalaryItem().getDefaultValue() != null) {
                        String defaultValue = salaryTemplateItem.getSalaryItem().getDefaultValue();
                        try {
                            double parsedValue = Double.parseDouble(defaultValue);
                            responseItem.setValue(parsedValue);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid defaultValue format: " + defaultValue);
                        }
                    }
                } else {
                    responseItem = new StaffSalaryItemValueDto(itemValueList.get(0), true);
                    if (responseItem.getFromDate() == null) {
                        responseItem.setFromDate(new Date());
                    }
//                    responseItem = new StaffSalaryItemValueDto(itemValueList.get(0));
                }

                itemList.add(responseItem);
            }
        }
        resultDto.setStaffSalaryItemValue(itemList);
        return resultDto;
    }

    //    @Override
    public StaffSalaryItemValueListDto getBySalaryTemplateItemV2(RequestSalaryValueDto dto) {
        if (dto.getStaff() == null || dto.getSalaryTemplate() == null) {
            return null;
        }

        List<StaffSalaryItemValueDto> itemList = new ArrayList<StaffSalaryItemValueDto>();

        Staff staff = null;
        if (dto.getStaff().getId() != null) {
            staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        }

        if (staff == null) return null;

        StaffDto simpleStaffDto = new StaffDto();
        simpleStaffDto.setId(staff.getId());
        simpleStaffDto.setStaffCode(staff.getStaffCode());
        simpleStaffDto.setDisplayName(staff.getDisplayName());

        SalaryTemplate salaryTemplate = null;
        if (dto.getSalaryTemplate().getId() != null) {
            salaryTemplate = salaryTemplateRepository.findById(dto.getSalaryTemplate().getId()).orElse(null);
        }

        if (salaryTemplate == null) {
            return null;
        }

        StaffSalaryItemValueListDto resultDto = new StaffSalaryItemValueListDto();

        resultDto.setSalaryTemplate(new SalaryTemplateDto(salaryTemplate));
        resultDto.setStaff(new StaffDto(staff));

        if (!CollectionUtils.isEmpty(salaryTemplate.getTemplateItems())) {
            for (SalaryTemplateItem salaryTemplateItem : salaryTemplate.getTemplateItems()) {
                if (salaryTemplateItem == null || salaryTemplateItem.getCalculationType() == null
                        || !salaryTemplateItem.getCalculationType().equals(HrConstants.SalaryItemCalculationType.FIX.getValue()))
                    continue;

                StaffSalaryItemValueDto responseItem = null;
                List<StaffSalaryItemValue> itemValueList = staffSalaryItemValueRepository.findByStaffIdAndSalaryItemId(staff.getId(), salaryTemplateItem.getSalaryItem().getId());

                if (itemValueList == null || CollectionUtils.isEmpty(itemValueList)) {
                    responseItem = new StaffSalaryItemValueDto();

                    responseItem.setTemplateItem(new SalaryTemplateItemDto(salaryTemplateItem));
                    responseItem.setStaff(simpleStaffDto);
                    responseItem.setCalculationType(salaryTemplateItem.getCalculationType());

                    responseItem.setValue(0D);

                    if (salaryTemplateItem.getSalaryItem() != null && salaryTemplateItem.getSalaryItem().getDefaultValue() != null) {
                        String defaultValue = salaryTemplateItem.getSalaryItem().getDefaultValue();
                        try {
                            double parsedValue = Double.parseDouble(defaultValue);
                            responseItem.setValue(parsedValue);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid defaultValue format: " + defaultValue);
                        }
                    }
                } else {
                    responseItem = new StaffSalaryItemValueDto(itemValueList.get(0), true);
                }

                itemList.add(responseItem);
            }
        }
        resultDto.setStaffSalaryItemValue(itemList);
        return resultDto;
    }

    @Override
    public Integer saveStaffSalaryItemValueList(StaffSalaryItemValueListDto dto) {
        if (dto == null || CollectionUtils.isEmpty(dto.getStaffSalaryItemValue())) {
            return 0;
        }

        int count = 0;
        for (StaffSalaryItemValueDto itemDto : dto.getStaffSalaryItemValue()) {
            if (itemDto != null) {

//                this.saveOrUpdateStaffSalaryItemValue(itemDto);
                // Tạo lịch sử thay đổi
                this.preHandleHistoryAndSaveOrUpdate(itemDto);

                count++;
            }
        }

        return count;
    }

    @Override
    public StaffSalaryItemValueDto getTaxBHXHByStaffId(UUID staffId) {
        if (staffId == null) {
            return null;
        }
        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null) return null;
        StaffSalaryItemValueDto dto = new StaffSalaryItemValueDto();

        List<StaffSalaryTemplate> template = staffSalaryTemplateRepository.findTaxByStaffId(staffId);
        if (template.isEmpty()) {
            return dto;
        }
        List<StaffSalaryItemValue> itemValueList = staffSalaryItemValueRepository.getTaxByStaffId(staffId);


        if (!itemValueList.isEmpty()) {
            StaffSalaryItemValue res = itemValueList.get(0);
            dto = new StaffSalaryItemValueDto(res);
        }

        return dto;
    }

    @Override
    public List<MapStaffSalaryItemValueDto> getByStaffId(UUID staffId) {
        if (staffId == null) {
            return null; // Trả về null nếu staffId là null
        }

        // Tìm nhân viên theo staffId, kiểm tra null nếu không tìm thấy
        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (staff == null) {
            return null; // Trả về null nếu không tìm thấy nhân viên
        }

        // Lấy danh sách StaffSalaryItemValue của nhân viên đó, kiểm tra null
        List<StaffSalaryItemValue> itemValueList = staffSalaryItemValueRepository.findByStaffId(staff.getId());
        if (itemValueList == null || itemValueList.isEmpty()) {
            return new ArrayList<>(); // Trả về danh sách rỗng nếu không có dữ liệu
        }

        // Bước 2: Group theo salaryItem, tránh null key
        Map<SalaryItem, List<StaffSalaryItemValue>> grouped = itemValueList.stream()
                .filter(item -> item.getSalaryItem() != null) // Tránh null key
                .collect(Collectors.groupingBy(StaffSalaryItemValue::getSalaryItem));

        // Bước 3: Map thành danh sách cây
        List<MapStaffSalaryItemValueDto> result = new ArrayList<>();  // Khởi tạo danh sách kết quả

        // Duyệt qua tất cả các entry trong Map
        for (Map.Entry<SalaryItem, List<StaffSalaryItemValue>> entry : grouped.entrySet()) {
            MapStaffSalaryItemValueDto dto = new MapStaffSalaryItemValueDto();  // Tạo mới đối tượng DTO

            // Kiểm tra nếu SalaryItem không null
            if (entry.getKey() != null) {
                SalaryItemDto salaryItemDto = new SalaryItemDto(entry.getKey());  // Chuyển đổi SalaryItem thành SalaryItemDto
                dto.setSalaryItem(salaryItemDto); // Gán vào dto
            }

            // Kiểm tra và chuyển đổi danh sách StaffSalaryItemValue thành danh sách StaffSalaryItemValueDto và gán vào dto
            List<StaffSalaryItemValueDto> staffSalaryItemValueDtos = new ArrayList<>();
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                for (StaffSalaryItemValue itemValue : entry.getValue()) {
                    if (itemValue != null) { // Kiểm tra null cho từng itemValue
                        StaffSalaryItemValueDto itemValueDto = new StaffSalaryItemValueDto(itemValue);
                        staffSalaryItemValueDtos.add(itemValueDto);  // Thêm vào danh sách DTO
                    }
                }
            }
            dto.setStaffSalaryItemValues(staffSalaryItemValueDtos);

            // Thêm MapStaffSalaryItemValueDto vào kết quả
            result.add(dto);
        }

        return result;
    }


    @Override
    public List<StaffSalaryItemValueDto> getSalaryValueHistories(UUID id) {
        if (id == null) return null;

        StaffSalaryItemValue currentValue = staffSalaryItemValueRepository.findById(id).orElse(null);

        if (currentValue == null || currentValue.getStaff() == null || currentValue.getSalaryItem() == null) {
            return null;
        }

        UUID salaryItemId = currentValue.getSalaryItem().getId();
        UUID staffId = currentValue.getStaff().getId();

        List<StaffSalaryItemValue> historyValues = staffSalaryItemValueRepository.findByStaffIdAndSalaryItemId(staffId, salaryItemId);

        List<StaffSalaryItemValueDto> response = new ArrayList<>();

        if (historyValues == null || historyValues.isEmpty()) {
            return response;
        }

        for (StaffSalaryItemValue historyItem : historyValues) {
            StaffSalaryItemValueDto responseItem = new StaffSalaryItemValueDto(historyItem, true);

            response.add(responseItem);
        }

        return response;
    }


    @Override
    public StaffSalaryItemValue findCurrentByStaffIdAndSalaryItemId(UUID staffId, UUID salaryItemId) {
        if (staffId == null || salaryItemId == null) return null;

        List<StaffSalaryItemValue> availableResults = staffSalaryItemValueRepository.findCurrentByStaffIdAndSalaryItemId(staffId, salaryItemId);
        if (availableResults == null || availableResults.isEmpty()) return null;

        return availableResults.get(0);
    }
}
