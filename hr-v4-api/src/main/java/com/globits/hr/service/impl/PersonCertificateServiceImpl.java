package com.globits.hr.service.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.globits.core.domain.FileDescription;
import com.globits.core.repository.FileDescriptionRepository;
import com.globits.core.service.FileDescriptionService;
import com.globits.hr.domain.*;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchPersonCertificateDto;
import com.globits.hr.service.UserExtService;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.dto.SalaryResultStaffPaySlipDto;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.globits.core.domain.Person;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.repository.CertificateRepository;
import com.globits.hr.repository.PersonCertificateRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.PersonCertificateService;

@Service
public class PersonCertificateServiceImpl extends GenericServiceImpl<PersonCertificate, UUID> implements PersonCertificateService {
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private PersonCertificateRepository personCertificateRepository;
    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private FileDescriptionRepository fileDescriptionRepository;
    @Autowired
    private UserExtService userExtService;

    @Override
    public PersonCertificateDto saveImportStaffEducationHistory(PersonCertificateDto dto) {
        if (dto != null) {
            PersonCertificate personCertificate = new PersonCertificate();
            Person entity = null;
            if (dto.getPersonCode() != null) {
                List<Staff> listStaff = staffRepository.getByCode(dto.getPersonCode());
                if (listStaff != null && listStaff.size() > 0) {
                    entity = listStaff.get(0);
                    personCertificate.setPerson(entity);

                }
            }
            if (entity == null) {
                return null;
            }
            if (dto.getCertificateType() != null && StringUtils.hasText(dto.getCertificateType())) {
                List<Certificate> listCertificate = certificateRepository.findByType(Integer.parseInt(dto.getCertificateType()));
                if (listCertificate != null && !listCertificate.isEmpty()) {
                    personCertificate.setCertificate(listCertificate.get(0));
                }
            }
            personCertificate.setLevel(dto.getLevel());
            personCertificate.setIssueDate(dto.getIssueDate());
            personCertificate.setName(dto.getName());
            personCertificate = personCertificateRepository.save(personCertificate);
            return new PersonCertificateDto(personCertificate);
        }
        return null;
    }


    @Override
    public SearchPersonCertificateDto getInitialFilter() {
        SearchPersonCertificateDto response = new SearchPersonCertificateDto();

        response.setPageIndex(1);
        response.setPageSize(10);

        Staff staff = userExtService.getCurrentStaffEntity();
        if (staff == null) {
            return response;
        }

        response.setStaff(new StaffDto());
        response.getStaff().setId(staff.getId());
        response.getStaff().setStaffCode(staff.getStaffCode());
        response.getStaff().setDisplayName(staff.getDisplayName());
        response.setStaffId(response.getStaff().getId());

        if (staff.getCurrentPositions() != null && !staff.getCurrentPositions().isEmpty()) {
            Position mainPosition = null;

            for (Position position : staff.getCurrentPositions()) {
                if (position != null && position.getIsMain() != null && position.getIsMain().equals(true)) {
                    mainPosition = position;
                    break;
                }
            }

            if (mainPosition != null) {

                if (mainPosition.getTitle() != null) {
                    PositionTitleDto positionTitle = new PositionTitleDto();

                    positionTitle.setId(mainPosition.getTitle().getId());
                    positionTitle.setCode(mainPosition.getTitle().getCode());
                    positionTitle.setName(mainPosition.getTitle().getName());

                    response.setPositionTitle(positionTitle);
                    response.setPositionTitleId(positionTitle.getId());
                }

                if (mainPosition.getDepartment() != null) {
                    HRDepartmentDto department = new HRDepartmentDto();

                    department.setId(mainPosition.getDepartment().getId());
                    department.setCode(mainPosition.getDepartment().getCode());
                    department.setName(mainPosition.getDepartment().getName());

                    response.setDepartment(department);
                    response.setDepartmentId(department.getId());
                }

                if (mainPosition.getDepartment() != null && mainPosition.getDepartment().getOrganization() != null) {
                    HrOrganizationDto organization = new HrOrganizationDto();

                    organization.setId(mainPosition.getDepartment().getOrganization().getId());
                    organization.setCode(mainPosition.getDepartment().getOrganization().getCode());
                    organization.setName(mainPosition.getDepartment().getOrganization().getName());

                    response.setOrganization(organization);
                    response.setOrganizationId(organization.getId());
                }
            }
        }

        return response;
    }


    @Override
    public Page<PersonCertificateDto> searchByPage(SearchPersonCertificateDto searchDto) {
        if (searchDto == null) {
            return null;
        }

        int pageIndex = Math.max(0, searchDto.getPageIndex() - 1);
        int pageSize = searchDto.getPageSize();

        if (searchDto.getStaff() != null) {
            searchDto.setStaffId(searchDto.getStaff().getId());
        }

        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.modifyDate desc ";

        String sqlCount = "select count(distinct entity.id) from PersonCertificate as entity inner join Staff staff on staff.id = entity.person.id ";
        String sql = "select distinct new com.globits.hr.dto.PersonCertificateDto(entity, staff) from PersonCertificate as entity inner join Staff staff on staff.id = entity.person.id ";

        String joinPositionStaff = "";
        boolean hasJoinMainPosition = false;

        if (searchDto.getPositionTitleId() != null || searchDto.getDepartmentId() != null || searchDto.getOrganizationId() != null) {
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = staff.id ";
            hasJoinMainPosition = true;
        }

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            whereClause += " AND (staff.staffCode LIKE :text OR staff.displayName LIKE :text OR entity.certificate.name LIKE :text OR entity.certificate.code LIKE :text) ";
        }
        if (searchDto.getStaffId() != null) {
            whereClause += " AND (staff.id = :staffId) ";
        }
        if (searchDto.getCertificateId() != null) {
            whereClause += " AND (entity.certificate.id = :certificateId) ";
        }

        if (hasJoinMainPosition) {
            if (searchDto.getOrganizationId() != null && StringUtils.hasText(searchDto.getOrganizationId().toString())) {
                whereClause += " AND (pos.department.organization.id = :organizationId) ";
            }
            if (searchDto.getDepartmentId() != null && StringUtils.hasText(searchDto.getDepartmentId().toString())) {
                whereClause += " AND (pos.department.id = :departmentId) ";
            }
            if (searchDto.getPositionTitleId() != null && StringUtils.hasText(searchDto.getPositionTitleId().toString())) {
                whereClause += " AND (pos.title.id = :positionTitleId) ";
            }
        }

        sql += joinPositionStaff + whereClause + orderBy;
        sqlCount += joinPositionStaff + whereClause;

        // ✅ Sử dụng TypedQuery để hỗ trợ constructor-based DTO
        TypedQuery<PersonCertificateDto> query = manager.createQuery(sql, PersonCertificateDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            String keyword = '%' + searchDto.getKeyword().trim() + '%';
            query.setParameter("text", keyword);
            qCount.setParameter("text", keyword);
        }

        if (searchDto.getStaffId() != null) {
            query.setParameter("staffId", searchDto.getStaffId());
            qCount.setParameter("staffId", searchDto.getStaffId());
        }

        if (searchDto.getCertificateId() != null) {
            query.setParameter("certificateId", searchDto.getCertificateId());
            qCount.setParameter("certificateId", searchDto.getCertificateId());
        }

        if (hasJoinMainPosition) {
            if (searchDto.getOrganizationId() != null && StringUtils.hasText(searchDto.getOrganizationId().toString())) {
                query.setParameter("organizationId", searchDto.getOrganizationId());
                qCount.setParameter("organizationId", searchDto.getOrganizationId());
            }
            if (searchDto.getDepartmentId() != null && StringUtils.hasText(searchDto.getDepartmentId().toString())) {
                query.setParameter("departmentId", searchDto.getDepartmentId());
                qCount.setParameter("departmentId", searchDto.getDepartmentId());
            }
            if (searchDto.getPositionTitleId() != null && StringUtils.hasText(searchDto.getPositionTitleId().toString())) {
                query.setParameter("positionTitleId", searchDto.getPositionTitleId());
                qCount.setParameter("positionTitleId", searchDto.getPositionTitleId());
            }
        }

        long count = (long) qCount.getSingleResult();
        int startPosition = pageIndex * pageSize;

        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<PersonCertificateDto> entities = query.getResultList();

        return new PageImpl<>(entities, pageable, count);
    }


    @Override
    public PersonCertificateDto saveOrUpdate(PersonCertificateDto dto) {
        if (dto == null) return null;

        PersonCertificate entity = null;

        if (dto.getId() != null) {
            entity = personCertificateRepository.findById(dto.getId()).orElse(null);
        }
        if (entity == null) {
            entity = new PersonCertificate();
        }

        entity.setLevel(dto.getLevel());
        entity.setIssueDate(dto.getIssueDate());
        entity.setName(dto.getName());


        Person person = null;
        if (dto.getPerson() != null && dto.getPerson().getId() != null) {
            person = staffRepository.findById(dto.getPerson().getId()).orElse(null);
        }
        entity.setPerson(person);

        if (person == null && dto.getStaff() != null && dto.getStaff().getId() != null) {
            Staff staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
            if (staff == null) return null;
            entity.setPerson(staff);
        }

        if (entity.getPerson() == null) return null;

        Certificate certificate = null;
        if (dto.getCertificate() != null && dto.getCertificate().getId() != null) {
            certificate = certificateRepository.findById(dto.getCertificate().getId()).orElse(null);
        }

        if (certificate == null) return null;

        entity.setCertificate(certificate);

        FileDescription certificateFile = null;

        if ((dto.getCertificateFile() == null || dto.getCertificateFile().getId() == null)
                && entity.getCertificateFile() != null) {
            String filePath = entity.getCertificateFile().getFilePath();
            if (filePath != null) {
                try {
                    File file = new File(filePath);
                    if (file.exists() && file.isFile()) {
                        file.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (dto.getCertificateFile() != null && dto.getCertificateFile().getId() != null) {
            certificateFile = fileDescriptionRepository.findById(dto.getCertificateFile().getId()).orElse(null);
        }

        if (entity.getCertificateFile() != null
                && entity.getCertificateFile().getId() != null
                && certificateFile != null
                && certificateFile.getId() != null
                && certificateFile.getId() != dto.getCertificateFile().getId()
        ) {
            String oldFilePath = entity.getCertificateFile().getFilePath();
            if (oldFilePath != null) {
                try {
                    File file = new File(oldFilePath);
                    if (file.exists() && file.isFile()) {
                        file.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            entity.setCertificateFile(certificateFile);
        }


        PersonCertificate response = personCertificateRepository.save(entity);
        return new PersonCertificateDto(response);
    }

    @Override
    public PersonCertificateDto getById(UUID id) {
        if (id == null) {
            return null;
        }

        PersonCertificate entity = personCertificateRepository.findById(id).orElse(null);

        if (entity == null) {
            return null;
        }

        Staff staff = staffRepository.findById(entity.getPerson().getId()).orElse(null);
        if (staff == null) return null;

        return new PersonCertificateDto(entity, staff);
    }

    @Override
    public Boolean deleteById(UUID id) {
        if (id != null) {
            PersonCertificate entity = personCertificateRepository.findById(id).orElse(null);
            if (entity != null) {
                personCertificateRepository.delete(entity);
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public Integer deleteMultiple(List<UUID> ids) {
        int result = 0;
        for (UUID id : ids) {
            deleteById(id);
            result++;
        }
        return result;
    }

    @Override
    public List<PersonCertificateDto> getPersonCertificateAllByPersonId(UUID personId) {
        if (personId == null) return null;
        return personCertificateRepository.findPersonCertificatesByPersonId(personId);
    }

}
