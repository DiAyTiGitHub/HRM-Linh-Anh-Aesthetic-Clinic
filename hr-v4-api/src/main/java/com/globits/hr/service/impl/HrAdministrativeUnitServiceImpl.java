/*
 * TA va Giang làm
 */

package com.globits.hr.service.impl;

import com.globits.core.domain.AdministrativeUnit;
import com.globits.core.dto.AdministrativeUnitDto;
import com.globits.core.repository.AdministrativeUnitRepository;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.HrAdministrativeUnitDto;
import com.globits.hr.dto.search.SearchAdministrativeUnitDto;
import com.globits.hr.service.HrAdministrativeUnitService;
import com.globits.security.domain.User;

import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Function;

@Transactional
@Service
public class HrAdministrativeUnitServiceImpl extends GenericServiceImpl<AdministrativeUnit, UUID> implements HrAdministrativeUnitService {
    @Autowired
    AdministrativeUnitRepository administrativeUnitRepository;

    @Override
    public Page<HrAdministrativeUnitDto> searchByPage(SearchAdministrativeUnitDto dto) {
        if (dto == null) return null;

        int pageIndex = dto.getPageIndex() > 0 ? dto.getPageIndex() - 1 : 0;
        int pageSize = dto.getPageSize();

        String sqlCount = "SELECT COUNT(entity.id) FROM AdministrativeUnit entity";
        String hql = "SELECT new com.globits.hr.dto.HrAdministrativeUnitDto(entity, false) FROM AdministrativeUnit entity";
        String where = " WHERE 1=1";
        String orderBy = " ORDER BY entity.level DESC, entity.code ASC, entity.name DESC";

        // Tìm theo từ khóa
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            where += " AND (entity.code LIKE :text OR entity.name LIKE :text)";
        }

        // Tìm theo cấp độ
        if (dto.getLevel() != null) {
            where += " AND entity.level = :level";
            if (dto.getParentId() != null) {
                where += " AND (entity.parent.id = :parentId) ";
            }
        }

        if (dto.getProvinceId() != null) {
            where += " AND (entity.id = :provinceId OR entity.parent.id = :provinceId OR entity.parent.parent.id = :provinceId) ";
        }
        if (dto.getDistrictId() != null) {
            where += " AND (entity.id = :districtId OR entity.parent.id = :districtId) ";
        }
        if (dto.getCommuneId() != null) {
            where += " AND entity.id = :communeId";
        }

        hql += where + orderBy;
        sqlCount += where;

        Query query = manager.createQuery(hql, HrAdministrativeUnitDto.class);
        Query qCount = manager.createQuery(sqlCount);

        // Gán giá trị cho tham số
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            query.setParameter("text", "%" + dto.getKeyword().trim() + "%");
            qCount.setParameter("text", "%" + dto.getKeyword().trim() + "%");
        }
        if (dto.getLevel() != null) {
            query.setParameter("level", dto.getLevel());
            qCount.setParameter("level", dto.getLevel());
            if (dto.getParentId() != null) {
                query.setParameter("parentId", dto.getParentId());
                qCount.setParameter("parentId", dto.getParentId());
            }
        }
        if (dto.getProvinceId() != null) {
            query.setParameter("provinceId", dto.getProvinceId());
            qCount.setParameter("provinceId", dto.getProvinceId());
        }
        if (dto.getDistrictId() != null) {
            query.setParameter("districtId", dto.getDistrictId());
            qCount.setParameter("districtId", dto.getDistrictId());
        }
        if (dto.getCommuneId() != null) {
            query.setParameter("communeId", dto.getCommuneId());
            qCount.setParameter("communeId", dto.getCommuneId());
        }

        // Phân trang
        if (dto.getExportExcel() != null && dto.getExportExcel()) {
            return new PageImpl<>(query.getResultList());
        } else {
            int startPosition = pageIndex * pageSize;
            query.setFirstResult(startPosition);
            query.setMaxResults(pageSize);

            List<HrAdministrativeUnitDto> entities = query.getResultList();
            long count = (long) qCount.getSingleResult();
            Pageable pageable = PageRequest.of(pageIndex, pageSize);

            return new PageImpl<>(entities, pageable, count);
        }
    }


    @Override
    public AdministrativeUnitDto saveAdministrativeUnit(AdministrativeUnitDto dto) {
        AdministrativeUnit administrativeUnit = null;
        if (dto != null && dto.getId() != null) {
            administrativeUnit = (AdministrativeUnit) this.administrativeUnitRepository.getOne(dto.getId());
        }

        if (administrativeUnit == null) {
            administrativeUnit = new AdministrativeUnit();
        }

        administrativeUnit.setCode(dto.getCode());
        administrativeUnit.setName(dto.getName());
        administrativeUnit.setLevel(dto.getLevel());
        if (dto.getParent() != null && dto.getParent().getId() != null) {
            AdministrativeUnit parentAdministrativeUnit = (AdministrativeUnit) this.administrativeUnitRepository.getOne(dto.getParent().getId());
            if (parentAdministrativeUnit.getId() != administrativeUnit.getId()) {
                administrativeUnit.setParent(parentAdministrativeUnit);
            }
        }

        administrativeUnit = (AdministrativeUnit) this.save(administrativeUnit);
        return new AdministrativeUnitDto(administrativeUnit);
    }

    @Override
    @Transactional
    public Integer saveListImportExcel(List<HrAdministrativeUnitDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return 0;
        }

        // Lọc danh sách hợp lệ
        List<HrAdministrativeUnitDto> validDtos = dtos.stream()
                .filter(this::isValidDto)
                .collect(Collectors.toList());
        if (validDtos.isEmpty()) return 0;

        // Lấy danh sách entity hiện có
        Map<String, AdministrativeUnit> existingUnits = administrativeUnitRepository.findAll()
                .stream()
                .collect(Collectors.toMap(AdministrativeUnit::getCode, Function.identity()));

        // Lưu các đơn vị theo thứ tự: Tỉnh (1) → Huyện (2) → Xã (3)
        for (int level = 1; level <= 3; level++) {
            List<AdministrativeUnit> unitsToSave = new ArrayList<>();

            for (HrAdministrativeUnitDto dto : validDtos) {
                if (!dto.getLevel().equals(level)) continue; // Bỏ qua nếu không phải level hiện tại

                AdministrativeUnit entity = existingUnits.getOrDefault(dto.getCode(), new AdministrativeUnit());
                entity.setCode(dto.getCode());
                entity.setName(dto.getName());
                entity.setLevel(dto.getLevel());

                // Gán parent nếu có
                if (dto.getParentCode() != null && !dto.getParentCode().isEmpty()) {
                    AdministrativeUnit parent = existingUnits.get(dto.getParentCode());
                    if (parent != null) entity.setParent(parent);
                }

                unitsToSave.add(entity);
                existingUnits.put(dto.getCode(), entity); // Cập nhật lại danh sách đã lưu
            }

            // Lưu danh sách đơn vị của level hiện tại
            if (!unitsToSave.isEmpty()) {
                administrativeUnitRepository.saveAllAndFlush(unitsToSave);
            }
        }

        return validDtos.size();
    }

    private boolean isValidDto(HrAdministrativeUnitDto dto) {
        return dto != null && 
               dto.getCode() != null && !dto.getCode().trim().isEmpty() && 
               dto.getName() != null && !dto.getName().trim().isEmpty() &&
               dto.getLevel() != null && 
               (dto.getLevel() == HrConstants.AdministrativeLevel.COMMUNE.getValue() 
               || dto.getLevel() == HrConstants.AdministrativeLevel.DISTRICT.getValue() 
               || dto.getLevel() == HrConstants.AdministrativeLevel.PROVINCE.getValue());
    }

//    @Override
//    @Transactional
//    public Integer saveListImportExcel(List<HrAdministrativeUnitDto> dtos) {
//        if (dtos == null || dtos.isEmpty()) return 0;
//        int totalSaved = 0;
//        for (HrAdministrativeUnitDto item : dtos) {
//            int result = saveAdministrativeUnitRecursive(item, null);
//            totalSaved += result;
//        }
//        return totalSaved;
//    }
//
//    private int saveAdministrativeUnitRecursive(HrAdministrativeUnitDto item, AdministrativeUnit parent) {
//        if (item == null) return 0;
//        AdministrativeUnit administrativeUnit = null;
//        if (item.getCode() != null) {
//            List<AdministrativeUnit> hrAdministrativeUnitList = administrativeUnitRepository.findListByCode(item.getCode());
//            if (hrAdministrativeUnitList != null && !hrAdministrativeUnitList.isEmpty()) {
//            	administrativeUnit = hrAdministrativeUnitList.get(0);
//            }
//        }
//        if (administrativeUnit == null) {
//        	 administrativeUnit = new AdministrativeUnit();
//        }
//        administrativeUnit.setCode(item.getCode());
//        administrativeUnit.setName(item.getName());
//        administrativeUnit.setLevel(item.getLevel());
//        administrativeUnit.setParent(parent);
//        administrativeUnit = administrativeUnitRepository.save(administrativeUnit);
//        int savedCount = 1; // Đã lưu 1 administrativeUnit
//        // Đệ quy lưu các con (nếu có)
//        if (item.getChildren() != null && !item.getChildren().isEmpty()) {
//            for (HrAdministrativeUnitDto childDto : item.getChildren()) {
//                savedCount += saveAdministrativeUnitRecursive(childDto, administrativeUnit);
//            }
//        }
//        return savedCount;
//    }


    @Override
    public void remove(UUID id) {
        AdministrativeUnit entity = null;
        Optional<AdministrativeUnit> optional = administrativeUnitRepository.findById(id);
        if (optional.isPresent()) {
            entity = optional.get();
        }
        if (entity != null) {
            administrativeUnitRepository.delete(entity);
        }
    }

    @Override
    public Boolean checkCode(UUID id, String code) {
        if (StringUtils.hasText(code)) {
            AdministrativeUnit entity = administrativeUnitRepository.findByCode(code);
            if (entity != null) {
                return id == null || !entity.getId().equals(id);
            }
            return false;
        }
        return null;
    }

    @Override
    public AdministrativeUnitDto updateAdministrativeUnit(AdministrativeUnitDto administrativeUnit, UUID administrativeUnitId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User modifiedUser = null;
        LocalDateTime currentDate = LocalDateTime.now();
        String currentUserName = "Unknown User";
        if (authentication != null) {
            try {
                modifiedUser = (User) authentication.getPrincipal();
                currentUserName = modifiedUser.getUsername();
            } catch (Exception e) {
            }
        }
        AdministrativeUnit updateAdministrativeUnit = null;
        if (administrativeUnit.getId() != null) {
            updateAdministrativeUnit = (AdministrativeUnit) this.findById(administrativeUnit.getId());
        } else {
            updateAdministrativeUnit = (AdministrativeUnit) this.findById(administrativeUnitId);
        }

        if (updateAdministrativeUnit.getCreateDate() == null || updateAdministrativeUnit.getCreatedBy() == null) {
            updateAdministrativeUnit.setCreateDate(currentDate);
            updateAdministrativeUnit.setCreatedBy(currentUserName);
        }

        updateAdministrativeUnit.setModifyDate(currentDate);
        updateAdministrativeUnit.setModifiedBy(currentUserName);
        updateAdministrativeUnit.setCode(administrativeUnit.getCode());
        updateAdministrativeUnit.setName(administrativeUnit.getName());
        updateAdministrativeUnit.setLevel(administrativeUnit.getLevel());
        if (administrativeUnit.getParent() != null && administrativeUnit.getParent().getId() != null) {
            AdministrativeUnit parentAdministrativeUnit = (AdministrativeUnit) this.administrativeUnitRepository.getOne(administrativeUnit.getParent().getId());
            if (parentAdministrativeUnit.getId() != updateAdministrativeUnit.getId()) {
                updateAdministrativeUnit.setParent(parentAdministrativeUnit);
            }
        } else if (updateAdministrativeUnit.getParent() != null) {
            updateAdministrativeUnit.setParent((AdministrativeUnit) null);
        }

        updateAdministrativeUnit = (AdministrativeUnit) this.save(updateAdministrativeUnit);
        if (updateAdministrativeUnit.getParent() != null) {
            updateAdministrativeUnit.setParent(new AdministrativeUnit(updateAdministrativeUnit.getParent(), false));
        }

        return new AdministrativeUnitDto(updateAdministrativeUnit);
    }

    @Override
    public int deleteAdministrativeUnits(List<AdministrativeUnitDto> dtos) {
        if (dtos == null) {
            return 0;
        } else {
            int ret = 0;

            for (Iterator var3 = dtos.iterator(); var3.hasNext(); ++ret) {
                AdministrativeUnitDto dto = (AdministrativeUnitDto) var3.next();
                if (dto.getId() != null) {
                    this.administrativeUnitRepository.deleteById(dto.getId());
                }
            }

            return ret;
        }
    }

    public AdministrativeUnitDto deleteAdministrativeUnit(UUID id) {
        new ArrayList();
        AdministrativeUnitDto ret = new AdministrativeUnitDto();
        AdministrativeUnit au = (AdministrativeUnit) this.administrativeUnitRepository.getOne(id);
        if (au != null && au.getId() != null) {
            ret.setId(au.getId());
            ret.setCode(au.getCode());
            ret.setName(au.getName());
            List<AdministrativeUnit> list = this.administrativeUnitRepository.getListdministrativeUnitbyParent(au.getId());
            if (list == null || list.size() <= 0) {
                this.administrativeUnitRepository.deleteById(au.getId());
                ret.setCode("-1");
            }
        }

        return ret;
    }

    @Override
    public List<AdministrativeUnitDto> getAllChildByParentId(UUID parentId) {
        String sql = "select new com.globits.core.dto.AdministrativeUnitDto(entity) FROM AdministrativeUnit entity where entity.parent.id =:parentId ";
        Query q = manager.createQuery(sql, AdministrativeUnitDto.class);
        q.setParameter("parentId", parentId);
        List<AdministrativeUnitDto> dtos = q.getResultList();
        return dtos;
    }

    public List<AdministrativeUnit> getAdministrativeUnitByCodeAndLevel(String code, Integer level) {
        if (!StringUtils.hasText(code) && level == null) {
            return null;
        }
        StringBuilder sql = new StringBuilder("SELECT entity FROM AdministrativeUnit entity WHERE 1=1");
        if (StringUtils.hasText(code)) {
            sql.append(" AND entity.code = :code");
        }
        if (level != null) {
            sql.append(" AND entity.level = :level");
        }
        Query query = manager.createQuery(sql.toString(), AdministrativeUnit.class);
        if (StringUtils.hasText(code)) {
            query.setParameter("code", code);
        }
        if (level != null) {
            query.setParameter("level", level);
        }
        return query.getResultList();
    }

    @Override
    public List<AdministrativeUnitDto> getAllByLevel(Integer level) {
        String sql = "select new com.globits.core.dto.AdministrativeUnitDto(entity,true) FROM AdministrativeUnit entity where entity.level =:level ";
        Query q = manager.createQuery(sql, AdministrativeUnitDto.class);
        q.setParameter("level", level);
        List<AdministrativeUnitDto> dtos = q.getResultList();
        return dtos;
    }

    private void addChldren(AdministrativeUnit parent, List<AdministrativeUnit> children, Boolean isCommunes) {
        if (parent != null && parent.getSubAdministrativeUnits() != null) {
            if (!isCommunes && parent.getLevel() != null && parent.getLevel() != 5 && parent.getLevel() != 4) {
                for (AdministrativeUnit child : parent.getSubAdministrativeUnits()) {
                    children.add(child);
                    addChldren(child, children, isCommunes);
                }
            }
        }
    }

    @Override
    public List<UUID> getAllAdministrativeIdByParentId(UUID parentId, Boolean isCommunes) {
        AdministrativeUnit parent = repository.findById(parentId).orElse(null);
        if (parent != null && parent.getId() != null) {
            List<UUID> ret = new ArrayList<UUID>();
            List<AdministrativeUnit> chidren = new ArrayList<AdministrativeUnit>();

            ret.add(parent.getId());
            this.addChldren(parent, chidren, isCommunes);
            if (chidren != null && chidren.size() > 0) {
                for (AdministrativeUnit admin : chidren) {
                    ret.add(admin.getId());
                }
            }
            return ret;
        }
        return null;
    }


}

