package com.globits.hr.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import com.globits.hr.domain.PositionTitle;
import com.globits.hr.repository.PositionTitleRepository;
import com.globits.hr.utils.SystemConfigUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.SystemConfig;
import com.globits.hr.dto.SystemConfigDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.SystemConfigRepository;
import com.globits.hr.service.SystemConfigService;

import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Transactional
@Service
public class SystemConfigServiceImpl extends GenericServiceImpl<SystemConfig, UUID> implements SystemConfigService {
    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Autowired
    private PositionTitleRepository positionTitleRepository;

    @Override
    public Page<SystemConfigDto> pagingSystemConfig(SearchDto dto) {
        if (dto == null)
            return null;

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0)
            pageIndex--;
        else
            pageIndex = 0;

        String whereClause = "";

        String orderBy = " ORDER BY entity.createDate DESC ";

        String sqlCount = "select count(entity.id) from SystemConfig as entity where (1=1)";
        String sql = "select new com.globits.hr.dto.SystemConfigDto(entity) from SystemConfig as entity where (1=1)";
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.configKey LIKE :text OR entity.configValue LIKE :text OR entity.note LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = manager.createQuery(sql, SystemConfigDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            q.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);
        List<SystemConfigDto> entities = q.getResultList();
        long count = (long) qCount.getSingleResult();

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return new PageImpl<>(entities, pageable, count);
    }

    @Override
    public SystemConfigDto saveOrUpdate(SystemConfigDto dto) {
        if (dto == null) {
            return null;
        }

        if (dto.getConfigKey() != null) {
            dto.setConfigKey(dto.getConfigKey().trim());
        }
        if (dto.getConfigValue() != null) {
            dto.setConfigValue(dto.getConfigValue());
        }

        SystemConfig entity = null;

        if (dto.getId() != null) {
            entity = systemConfigRepository.findById(dto.getId()).orElse(null);
        }
        if (entity == null && dto.getConfigKey() != null && StringUtils.hasText(dto.getConfigKey())) {
            List<SystemConfig> availableConfigs = systemConfigRepository.getByConfigKey(dto.getConfigKey());
            if (availableConfigs != null && !availableConfigs.isEmpty()) {
                entity = availableConfigs.get(0);
            }
        }
        if (entity == null) {
            entity = new SystemConfig();
        }
        entity.setNumberOfZero(dto.getNumberOfZero() != null ? dto.getNumberOfZero() : 0);
        entity.setConfigKey(dto.getConfigKey());
        entity.setConfigValue(dto.getConfigValue());
        entity.setNote(dto.getNote());
        entity = systemConfigRepository.save(entity);

        return new SystemConfigDto(entity);
    }

    @Override
    public SystemConfigDto getById(UUID id) {
        if (id == null) {
            return null;
        }

        SystemConfig entity = systemConfigRepository.findById(id).orElse(null);

        if (entity == null) {
            return null;
        }

        return new SystemConfigDto(entity);
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id != null) {
            SystemConfig entity = systemConfigRepository.findById(id).orElse(null);
            if (entity != null) {
                systemConfigRepository.delete(entity);
                // call lai ham init de set cac gia tri moi vao cau hinh
                return true;
            }
        }
        return null;
    }

    @Override
    public Integer deleteMultiple(List<UUID> ids) {
        int result = 0;
        for (UUID id : ids) {
            deleteById(id);
            result++;
        }
        return result;
    }

    @Override
    public Boolean checkKeyCode(SystemConfigDto dto) {
        if (dto != null && dto.getConfigKey() != null && StringUtils.hasText(dto.getConfigKey())) {
            List<SystemConfig> listSystemConfig = systemConfigRepository.getByConfigKey(dto.getConfigKey());
            if (listSystemConfig != null && !listSystemConfig.isEmpty()) {
                SystemConfig entity = listSystemConfig.get(0);
                if (entity.getId() != null && dto.getId() != null && dto.getId().equals(entity.getId())) {
                    return false;
                }
                return true;
            }
            return false;
        }
        return null;
    }

    @Override
    public SystemConfigDto getByKeyCode(String configKey) {
        if (configKey == null || !StringUtils.hasText(configKey)) {
            return null;
        }

        List<SystemConfig> listSystemConfig = systemConfigRepository.getByConfigKey(configKey);
        if (listSystemConfig == null || listSystemConfig.isEmpty()) {
            return null;
        }

        SystemConfig entity = listSystemConfig.get(0);
        return new SystemConfigDto(entity);
    }

    @Override
    public SystemConfig getConfigByKey(String configKey, String configValue, String note) {
        List<SystemConfig> configs = systemConfigRepository.getByConfigKey(configKey);
        if (configs != null && !configs.isEmpty()) {
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

    @Override
    public SystemConfig getConfigByConfigValue(String configKey) {
        SystemConfig config = systemConfigRepository.getSystemConfigByConfigKey(configKey);
        if (config != null) {
            if (config.getNumberOfZero() == null) {
                config.setNumberOfZero(3);
                systemConfigRepository.save(config);
            }
        }
        return config;
    }

    @Override
    public String generateNextCode(String prefix, int zeroPadding, String maxCode) {
        int nextNumber = 1;

        // Bước 1: Lấy số từ maxCode nếu có
        if (maxCode != null) {
            String numericPart = maxCode.substring(prefix.length() + 1);
            if (NumberUtils.isParsable(numericPart)) {
                nextNumber = Integer.parseInt(numericPart) + 1;
            }
        }


        // Bước 2: Lấy mã lớn nhất hiện có trong prefixMap nếu có
        LinkedHashMap<String, String> prefixEntries = SystemConfigUtil.prefixMap.get(prefix);
        if (prefixEntries != null && !prefixEntries.isEmpty()) {
            for (String existingCode : prefixEntries.keySet()) {
                try {
                    String numericPart = existingCode.substring(prefix.length() + 1);
                    int existingNumber = Integer.parseInt(numericPart);
                    if (existingNumber >= nextNumber) {
                        nextNumber = existingNumber + 1;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (zeroPadding == 0) {
            zeroPadding = 3;
        }

        // Đảm bảo độ dài phần số
        int totalLength = String.valueOf(nextNumber).length() + zeroPadding;
        String code = prefix + "_" + String.format("%0" + totalLength + "d", nextNumber);

        // Đảm bảo không trùng
        while (SystemConfigUtil.hasKey(prefix, code)) {
            nextNumber++;
            code = prefix + "_" + String.format("%0" + totalLength + "d", nextNumber);
        }

        // Lưu vào prefixMap
        SystemConfigUtil.prefixMap
                .computeIfAbsent(prefix, k -> new LinkedHashMap<>())
                .put(code, code);

        return code;
    }

    @Override
    public String generateNextCodeKL(String prefix, String maxCode) {
        if (maxCode == null || !maxCode.startsWith(prefix) || !maxCode.contains("/")) {
            throw new IllegalArgumentException("Mã không hợp lệ");
        }

        // Cắt phần sau prefix
        String numberPart = maxCode.substring(prefix.length(), maxCode.indexOf("/"));
        String yearPart = maxCode.substring(maxCode.indexOf("/") + 1);

        int currentMonth = Integer.parseInt(numberPart);
        int currentYear = Integer.parseInt(yearPart);

        int nextMonth = currentMonth + 1;
        int nextYear = currentYear;

        if (nextMonth > 12) {
            nextMonth = 1;
            nextYear += 1;
        }

        // Format tháng luôn có 2 chữ số
        String code = prefix + String.format("%02d", nextMonth) + "/" + nextYear;

        // Kiểm tra duplicate
        if (SystemConfigUtil.hasKey(prefix, code)) {
            return generateNextCodeKL(prefix, code);
        }

        // Lưu vào prefixMap
        SystemConfigUtil.prefixMap
                .computeIfAbsent(prefix, k -> new LinkedHashMap<>())
                .put(code, code);

        return code;
    }
}
