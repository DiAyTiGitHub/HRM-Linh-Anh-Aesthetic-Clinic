package com.globits.hr.service.impl;

import com.globits.core.domain.FileDescription;
import com.globits.core.repository.FileDescriptionRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.HrDocumentItem;
import com.globits.hr.domain.HrDocumentTemplate;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffDocumentItem;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchStaffDocumentItemDto;
import com.globits.hr.repository.HrDocumentItemRepository;
import com.globits.hr.repository.HrDocumentTemplateRepository;
import com.globits.hr.repository.StaffDocumentItemRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.StaffDocumentItemService;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StaffDocumentItemServiceImpl extends GenericServiceImpl<StaffDocumentItem, UUID> implements StaffDocumentItemService {
    @Autowired
    private StaffDocumentItemRepository staffDocumentItemRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private HrDocumentItemRepository hrDocumentItemRepository;

    @Autowired
    private HrDocumentTemplateRepository hrDocumentTemplateRepository;

    @Autowired
    private FileDescriptionRepository fileDescriptionRepository;

    @Override
    public Page<StaffDocumentItemDto> searchByPage(SearchStaffDocumentItemDto dto) {
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
        String orderBy = " ORDER BY entity.createDate desc ";

        String sqlCount = "select count(distinct entity.id) from StaffDocumentItem as entity ";
        String sql = "select distinct new com.globits.hr.dto.StaffDocumentItemDto(entity) from StaffDocumentItem as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.staff.displayName LIKE :text " +
                    "OR entity.documentItem.name LIKE :text ) ";
        }

        if (dto.getHrDocumentItemId() != null) {
            whereClause += " and (entity.documentItem.id = :documentItemId) ";
        }
        if (dto.getStaffId() != null) {
            whereClause += " and (entity.staff.id = :staffId) ";
        }
        if (dto.getHrDocumentTemplateId() != null) {
            whereClause += " and (entity.staff.documentTemplate.id = :hrDocumentTemplateId) ";
        }

        if (dto.getFromDate() != null) {
            whereClause += " and (entity.submissionDate >= :fromDate) ";
        }
        if (dto.getToDate() != null) {
            whereClause += " and (entity.submissionDate <= :toDate) ";
        }
        if (dto.getIsSubmitted() != null && dto.getIsSubmitted()) {
            whereClause += " and (entity.isSubmitted = TRUE AND entity.file.id IS NOT NULL) ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, PositionDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        if (dto.getHrDocumentItemId() != null) {
            query.setParameter("documentItemId", dto.getHrDocumentItemId());
            qCount.setParameter("documentItemId", dto.getHrDocumentItemId());
        }
        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }
        if (dto.getHrDocumentTemplateId() != null) {
            query.setParameter("hrDocumentTemplateId", dto.getHrDocumentTemplateId());
            qCount.setParameter("hrDocumentTemplateId", dto.getHrDocumentTemplateId());
        }

        if (dto.getFromDate() != null) {
            query.setParameter("fromDate", dto.getFromDate());
            qCount.setParameter("fromDate", dto.getFromDate());
        }
        if (dto.getToDate() != null) {
            query.setParameter("toDate", dto.getToDate());
            qCount.setParameter("toDate", dto.getToDate());
        }
        long count = (long) qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<StaffDocumentItemDto> entities = query.getResultList();
        Page<StaffDocumentItemDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public StaffDocumentItemDto getById(UUID id) {
        if (id == null) return null;
        StaffDocumentItem entity = staffDocumentItemRepository.findById(id).orElse(null);

        if (entity == null) return null;
        StaffDocumentItemDto response = new StaffDocumentItemDto(entity, true);

        return response;
    }

    @Override
    public StaffDocumentItemDto saveOrUpdate(StaffDocumentItemDto dto) {
        if (dto == null) {
            return null;
        }

        StaffDocumentItem entity = new StaffDocumentItem();
        if (dto.getId() != null) entity = staffDocumentItemRepository.findById(dto.getId()).orElse(null);
        if (dto.getStaff() != null && dto.getStaff().getId() != null && dto.getDocumentItem() != null && dto.getDocumentItem().getId() != null) {
            List<StaffDocumentItem> staffDocumentItemList = staffDocumentItemRepository.findByStaffIdAndDocumentItemId(dto.getStaff().getId(), dto.getDocumentItem().getId());
            if (staffDocumentItemList != null && !staffDocumentItemList.isEmpty()) {
                entity = staffDocumentItemList.get(0);
            }
        }
        if (entity == null) entity = new StaffDocumentItem();

        if (dto.getStaff() != null) {
            Staff staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
            if (staff == null) return null;
            entity.setStaff(staff);
        } else {
            entity.setStaff(null);
        }

        if (dto.getDocumentItem() != null) {
            HrDocumentItem documentItem = hrDocumentItemRepository.findById(dto.getDocumentItem().getId()).orElse(null);
            if (documentItem == null) return null;
            entity.setDocumentItem(documentItem);
        } else {
            entity.setDocumentItem(null);
        }

        if (dto.getFile() != null) {
            FileDescription fileDescription = fileDescriptionRepository.findById(dto.getFile().getId()).orElse(null);
            if (fileDescription == null) return null;
            entity.setFile(fileDescription);
        } else {
            entity.setFile(null);
        }
        entity.setIsSubmitted(dto.getIsSubmitted());
        entity.setSubmissionDate(dto.getSubmissionDate());

        entity = staffDocumentItemRepository.save(entity);

        return new StaffDocumentItemDto(entity);
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id == null) return false;

        StaffDocumentItem entity = staffDocumentItemRepository.findById(id).orElse(null);
        if (entity == null) return false;

        staffDocumentItemRepository.delete(entity);
        return true;
    }

    @Override
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null) return false;
        boolean isValid = true;
        for (UUID itemId : ids) {
            boolean deleteRes = this.deleteById(itemId);
            if (!deleteRes) isValid = false;
        }
        return isValid;
    }

    @Override
    @Transactional
    public TemplateStaffDto getItemByTemplateStaff(SearchStaffDocumentItemDto searchDto) {
        if (searchDto == null || searchDto.getStaffId() == null) {
            return null;
        }

        Staff staff = staffRepository.findById(searchDto.getStaffId()).orElse(null);
        if (staff == null) return null;

        TemplateStaffDto ans = new TemplateStaffDto();
        ans.setStaff(new StaffDto(staff));

        HrDocumentTemplate template = null;
        if (searchDto.getHrDocumentTemplateId() != null) {
            template = hrDocumentTemplateRepository.findById(searchDto.getHrDocumentTemplateId()).orElse(null);
        } else if (staff.getDocumentTemplate() != null) {
            template = hrDocumentTemplateRepository.findById(staff.getDocumentTemplate().getId()).orElse(null);
        }
        if (template == null) return ans;
        ans.setDocumentTemplate(new HrDocumentTemplateDto(template, false));

        List<StaffDocumentItemDto> res = new ArrayList<>();
        List<StaffDocumentItem> listCurrentStaffDocumentItem = staffDocumentItemRepository.findByStaffId(staff.getId());
        Set<HrDocumentItem> listItems = template.getDocumentItems();

        // Map lưu StaffDocumentItem đã tồn tại theo documentItemId
        // Using mergeFunction to handle duplicate keys - keep the most recently added item
        Map<UUID, StaffDocumentItem> existingItemsMap = listCurrentStaffDocumentItem.stream()
                .filter(item -> item.getDocumentItem() != null)
                .collect(Collectors.toMap(
                        item -> item.getDocumentItem().getId(),
                        item -> item,
                        (existing, replacement) -> replacement  // In case of duplicate keys, keep the latest one
                ));

        // Duyệt qua tất cả các HrDocumentItem trong template
        for (HrDocumentItem documentItem : listItems) {
            StaffDocumentItemDto dto;

            // Nếu đã tồn tại trong map, lấy dữ liệu từ StaffDocumentItem
            if (existingItemsMap.containsKey(documentItem.getId())) {
                dto = new StaffDocumentItemDto(existingItemsMap.get(documentItem.getId()), false);
            } else {
                // Nếu chưa tồn tại, tạo mới
                StaffDocumentItem staffDocumentItem = new StaffDocumentItem();
                staffDocumentItem.setStaff(staff);
                staffDocumentItem.setDocumentItem(documentItem);
                staffDocumentItem = staffDocumentItemRepository.save(staffDocumentItem);
                dto = new StaffDocumentItemDto(staffDocumentItem, false);
            }

            res.add(dto);
        }

        // Sắp xếp theo displayOrder
        Collections.sort(res, new Comparator<StaffDocumentItemDto>() {
            @Override
            public int compare(StaffDocumentItemDto o1, StaffDocumentItemDto o2) {
                // Check if documentItem is null
                if (o1.getDocumentItem() == null && o2.getDocumentItem() == null)
                    return 0;
                if (o1.getDocumentItem() == null)
                    return 1;
                if (o2.getDocumentItem() == null)
                    return -1;

                Integer displayOrder1 = o1.getDocumentItem().getDisplayOrder();
                Integer displayOrder2 = o2.getDocumentItem().getDisplayOrder();

                // Handle null displayOrder
                if (displayOrder1 == null && displayOrder2 == null) {
                    // If both displayOrder are null, compare by submissionDate
                    if (o1.getSubmissionDate() == null && o2.getSubmissionDate() == null)
                        return 0;
                    if (o1.getSubmissionDate() == null)
                        return 1;
                    if (o2.getSubmissionDate() == null)
                        return -1;
                    return o1.getSubmissionDate().compareTo(o2.getSubmissionDate());
                }
                if (displayOrder1 == null)
                    return 1;
                if (displayOrder2 == null)
                    return -1;

                // Compare by displayOrder first
                int orderComparison = displayOrder1.compareTo(displayOrder2);
                if (orderComparison != 0) {
                    return orderComparison;
                }

                // If displayOrder is the same, compare by submissionDate
                if (o1.getSubmissionDate() == null && o2.getSubmissionDate() == null)
                    return 0;
                if (o1.getSubmissionDate() == null)
                    return 1;
                if (o2.getSubmissionDate() == null)
                    return -1;
                return o1.getSubmissionDate().compareTo(o2.getSubmissionDate());
            }
        });

        ans.setStaffDocumentItems(res);
        ans.setStaffDocumentStatus(staff.getStaffDocumentStatus());
        return ans;
    }

    @Override
    @Transactional
    public TemplateStaffDto saveTemplateStaff(TemplateStaffDto dto) {
        if (dto == null) return null;

        if (dto.getStaff() == null || dto.getStaff().getId() == null) return null;

        Staff staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
        if (staff == null) return null;

        // Update staff document status
        staff.setStaffDocumentStatus(dto.getStaffDocumentStatus());

        // Handle document template
        if (dto.getDocumentTemplate() != null && dto.getDocumentTemplate().getId() != null) {
            HrDocumentTemplate documentTemplate = hrDocumentTemplateRepository.findById(dto.getDocumentTemplate().getId()).orElse(null);
            staff.setDocumentTemplate(documentTemplate);
        } else {
            // If template is null, clear the template and all staff document items
            staff.setDocumentTemplate(null);
//            if (staff.getStaffDocumentItems() != null) {
//                staff.getStaffDocumentItems().clear(); // Clear existing items
//            } else {
//                staff.setStaffDocumentItems(new HashSet<>()); // Initialize if null
//            }
        }

        // Process staff document items only if template is not null
        if (dto.getDocumentTemplate() != null && dto.getStaffDocumentItems() != null) {
            for (StaffDocumentItemDto itemDto : dto.getStaffDocumentItems()) {
                StaffDocumentItem item = null;
                if (itemDto.getId() != null) {
                    item = staffDocumentItemRepository.findById(itemDto.getId()).orElse(null);
                }
                if (item == null) {
                    item = new StaffDocumentItem();
                }
                item.setIsSubmitted(itemDto.getIsSubmitted());
                item.setSubmissionDate(itemDto.getSubmissionDate());
                item.setStaff(staff);
                if (itemDto.getFile() != null) {
                    FileDescription file = fileDescriptionRepository.findById(itemDto.getFile().getId()).orElse(null);
                    item.setFile(file);
                } else {
                    item.setFile(null);
                }
                if (itemDto.getDocumentItem() != null) {
                    HrDocumentItem documentItem = hrDocumentItemRepository.findById(itemDto.getDocumentItem().getId()).orElse(null);
                    item.setDocumentItem(documentItem);
                } else {
                    item.setDocumentItem(null);
                }
                staff.getStaffDocumentItems().add(item);
            }
        }

        // Save the staff entity with all changes
        staff = staffRepository.save(staff);
        return dto;
    }

}
