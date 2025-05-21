package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.HrDocumentItem;
import com.globits.hr.domain.HrDocumentTemplate;
import com.globits.hr.dto.DefaultDocumentTemplateItemDto;
import com.globits.hr.dto.HrDocumentItemDto;
import com.globits.hr.dto.HrDocumentTemplateDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.HrDocumentItemRepository;
import com.globits.hr.repository.HrDocumentTemplateRepository;
import com.globits.hr.service.HrDocumentTemplateService;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class HrDocumentTemplateServiceImpl extends GenericServiceImpl<HrDocumentTemplate, UUID> implements HrDocumentTemplateService {
    @Autowired
    private HrDocumentTemplateRepository hrDocumentTemplateRepository;
    @Autowired
    private HrDocumentItemRepository hrDocumentItemRepository;

    @Override
    public HrDocumentTemplateDto getHrDocumentTemplateById(UUID id) {
        if (id != null) {
            HrDocumentTemplate entity = hrDocumentTemplateRepository.findById(id).orElse(null);
            if (entity != null) {
                return new HrDocumentTemplateDto(entity);
            }
        }
        return null;
    }

    @Override
    public HrDocumentTemplateDto getByCode(String code) {
        if (code == null) return null;

        HrDocumentTemplate commonTemplate = null;

        List<HrDocumentTemplate> availableTemplates = hrDocumentTemplateRepository.findByCode(code);
        if (availableTemplates == null || availableTemplates.isEmpty()) {
        return null;
        }

        commonTemplate = availableTemplates.get(0);

        return new HrDocumentTemplateDto(commonTemplate, true);
    }

    @Override
    public HrDocumentTemplateDto saveOrUpdate(HrDocumentTemplateDto dto) {
        if (dto == null) return null;

        HrDocumentTemplate entity = null;

        if (dto.getId() != null) {
            entity = hrDocumentTemplateRepository.findById(dto.getId()).orElse(null);
        }

        if (entity == null) {
            entity = new HrDocumentTemplate();
        }

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());


        List<HrDocumentItem> documentItems = new ArrayList<>();

        if (dto.getDocumentItems() != null && !dto.getDocumentItems().isEmpty()) {
            for (HrDocumentItemDto item : dto.getDocumentItems()) {
                HrDocumentItem hrDocumentItem = null;

                if (item.getId() != null) {
                    hrDocumentItem = hrDocumentItemRepository.findById(item.getId()).orElse(null);
                }

                if (hrDocumentItem == null) {
                    hrDocumentItem = new HrDocumentItem();
                }

                hrDocumentItem.setCode(item.getCode());
                hrDocumentItem.setName(item.getName());
                hrDocumentItem.setDescription(item.getDescription());
                hrDocumentItem.setDocumentTemplate(entity);
                hrDocumentItem.setDisplayOrder(item.getDisplayOrder());
                hrDocumentItem.setRequired(item.getIsRequired());

                documentItems.add(hrDocumentItem);
            }
        }

        if (entity.getDocumentItems() == null) {
            entity.setDocumentItems(new HashSet<>());
        }
        entity.getDocumentItems().clear();

        entity.getDocumentItems().addAll(documentItems);


        HrDocumentTemplate response = hrDocumentTemplateRepository.save(entity);

        return new HrDocumentTemplateDto(response);
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id != null) {
            HrDocumentTemplate entity = hrDocumentTemplateRepository.findById(id).orElse(null);
            if (entity != null) {
                hrDocumentTemplateRepository.delete(entity);
                return true;
            }
        }
        return false;
    }

    @Override
    public Page<HrDocumentTemplateDto> paging(SearchDto dto) {
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

        String whereClause = "";
        String orderBy = " ORDER BY entity.createDate ";

        String sqlCount = "select count(entity.id) from KPI as entity where (1=1) ";
        String sql = "select new  com.globits.hr.dto.HrDocumentTemplateDto(entity) from HrDocumentTemplate as entity where (1=1) ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.code LIKE :text OR entity.name LIKE :text OR entity.description LIKE :text ) ";
        }
        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, HrDocumentTemplateDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<HrDocumentTemplateDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public Boolean isValidCode(HrDocumentTemplateDto dto) {
        if (dto == null || dto.getCode() == null)
            return false;

        // 1️⃣ Kiểm tra mã của HrDocumentTemplateDto trong database
        // Lấy danh sách các bản ghi có cùng mã code
        List<HrDocumentTemplate> existingTemplates = hrDocumentTemplateRepository.findByCode(dto.getCode());

        if (dto.getId() == null) {
            // Trường hợp tạo mới: Nếu có bất kỳ bản ghi nào có cùng code, thì không hợp lệ
            if (!existingTemplates.isEmpty()) {
                return false;
            }
        } else {
            // Trường hợp cập nhật: Chỉ không hợp lệ nếu có bản ghi khác cùng mã code nhưng ID khác
            for (HrDocumentTemplate entity : existingTemplates) {
                if (!entity.getId().equals(dto.getId())) {
                    return false;
                }
            }
        }

        // 2️⃣ Kiểm tra trùng code trong danh sách DocumentItems
        if (dto.getDocumentItems() != null && !dto.getDocumentItems().isEmpty()) {
            Set<String> uniqueCodes = new HashSet<>();
            for (HrDocumentItemDto item : dto.getDocumentItems()) {
//                Set.add(value):
//                Nếu value chưa tồn tại, nó được thêm vào Set, hàm trả về true.
//                Nếu value đã tồn tại, nó không được thêm, hàm trả về false.
                if (!uniqueCodes.add(item.getCode())) {
                    return false; // Trùng code trong danh sách
                }
            }
        }

        return true; // Không trùng, hợp lệ
    }


    @Override
    public HashMap<UUID, DefaultDocumentTemplateItemDto> getDefaultDocumentTemplateItemMap() {
        HashMap<UUID, DefaultDocumentTemplateItemDto> map = new HashMap<>();

        try {
            List<String> documentItemCodes = HrConstants.DefaultDocumentTemplateItem.getListCode();

            List<Object[]> results = hrDocumentTemplateRepository.findDefaultDocumentTemplateItems(documentItemCodes);

            for (Object[] row : results) {
                try {
                    UUID staffId = row[0] != null ? UUID.fromString(row[0].toString()) : null;
                    if (staffId == null) {
                        continue;
                    }

                    // Sử dụng toString() an toàn hơn, tránh lỗi ép kiểu
                    String hasEmployeeProfile = row[1] != null ? row[1].toString() : null;
                    String hasA34 = row[2] != null ? row[2].toString() : null;
                    String hasCCCD = row[3] != null ? row[3].toString() : null;
                    String hasDUT = row[4] != null ? row[4].toString() : null;
                    String hasSYLL = row[5] != null ? row[5].toString() : null;
                    String hasBC = row[6] != null ? row[6].toString() : null;
                    String hasCCLQ = row[7] != null ? row[7].toString() : null;
                    String hasGKSK = row[8] != null ? row[8].toString() : null;
                    String hasSHK = row[9] != null ? row[9].toString() : null;
                    String hasHSK = row[10] != null ? row[10].toString() : null;
                    String hasPTTCN = row[11] != null ? row[11].toString() : null;
                    String hasCKBMTT = row[12] != null ? row[12].toString() : null;
                    String hasCKBMTTTN = row[13] != null ? row[13].toString() : null;
                    String hasCKTN = row[14] != null ? row[14].toString() : null;
                    String hasHDTV = row[15] != null ? row[15].toString() : null;

                    DefaultDocumentTemplateItemDto dto = new DefaultDocumentTemplateItemDto(
                            staffId,
                            hasEmployeeProfile,
                            hasA34,
                            hasCCCD,
                            hasDUT,
                            hasSYLL,
                            hasBC,
                            hasCCLQ,
                            hasGKSK,
                            hasSHK,
                            hasHSK,
                            hasPTTCN,
                            hasCKBMTT,
                            hasCKBMTTTN,
                            hasCKTN,
                            hasHDTV
                    );
                    map.put(staffId, dto);

                } catch (Exception rowEx) {
                    System.err.println("Error processing row in getDefaultDocumentTemplateItemMap: " + rowEx.getMessage());
                    //rowEx.printStackTrace();
                    continue;
                }
            }

        } catch (Exception ex) {
            System.err.println("Error executing getDefaultDocumentTemplateItemMap: " + ex.getMessage());
            //ex.printStackTrace();
            return null;
        }

        return map;
    }

}
