/*
 * TA va Giang làm
 */

package com.globits.hr.service.impl;

import com.globits.core.domain.Organization;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.dto.AcademicTitleDto;
import com.globits.hr.dto.OrganizationChartDto;
import com.globits.hr.dto.OrganizationChartRelationDto;
import com.globits.hr.dto.search.OrgChartSearchDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class OrganizationChartRelationServiceImpl extends GenericServiceImpl<OrganizationChartRelation, UUID> implements OrganizationChartRelationService {

    @Autowired
    private OrganizationChartRelationRepository organizationChartRelationRepository;
    @Autowired
    private OrganizationChartRepository organizationChartRepository;
    @Autowired
    private PositionStaffService positionStaffService;
    @Autowired
    private PositionRelationShipRepository positionRelationShipRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private PositionRelationshipService positionRelationshipService;
    @Autowired
    private HRDepartmentRepository hrDepartmentRepository;

    @Autowired
    private HrOrganizationRepository hrOrganizationRepository;
    @Override
    @Transactional
    public OrganizationChartRelationDto saveOrUpdate(OrganizationChartRelationDto dto) {
        if (dto != null) {
            OrganizationChartRelation organizationChartRelation = null;
            if (dto.getId() != null) {
                organizationChartRelation = organizationChartRelationRepository.findById(dto.getId()).orElse(null);
            }
            if (organizationChartRelation == null) {
                organizationChartRelation = new OrganizationChartRelation();
            }
            organizationChartRelation.setRelationDescription(dto.getRelationDescription());
            organizationChartRelation.setRelationIcon(dto.getRelationIcon());
            organizationChartRelation.setRelationType(dto.getRelationType());
            OrganizationChart source = null;
            if (dto.getSourceOrgId() != null) {
                source = organizationChartRepository.findById(dto.getSourceOrgId()).orElse(null);
                organizationChartRelation.setSourceOrg(source);
            }
            if (source == null && dto.getSourceOrg() != null && dto.getSourceOrg().getId() != null) {
                source = organizationChartRepository.findById(dto.getSourceOrg().getId()).orElse(null);
                organizationChartRelation.setSourceOrg(source);
            }

            OrganizationChart target = null;
            if (dto.getTargetOrgId() != null) {
                target = organizationChartRepository.findById(dto.getTargetOrgId()).orElse(null);
                organizationChartRelation.setTargetOrg(target);
            }
            if (target == null && dto.getTargetOrg() != null && dto.getTargetOrg().getId() != null) {
                target = organizationChartRepository.findById(dto.getTargetOrg().getId()).orElse(null);
                organizationChartRelation.setTargetOrg(target);
            }

            if (source == null || target == null) {
                return null;
            }

            organizationChartRelation.setOrgChartData(source.getOrgChartData());

            // Mapping quan hệ với pofsitionstaff
//            PositionStaff postionStaff = positionStaffService.mappingWithChartRelation(dto);
            positionRelationshipService.mappingWithChartRelation(dto);

            organizationChartRelation = organizationChartRelationRepository.save(organizationChartRelation);
            OrganizationChartRelationDto ret = new OrganizationChartRelationDto(organizationChartRelation);
            return ret;
        }
        return null;
    }

    @Override
    public OrganizationChartRelationDto deleteById(UUID id) {
        if (id != null) {
            OrganizationChartRelation entity = repository.findById(id).orElse(null);
            if (entity == null) {
                return null;
            }

            OrganizationChart sourceNode = entity.getSourceOrg();
            OrganizationChart targetNode = entity.getTargetOrg();
            /*
             * Staff : 0
             * Department: 1
             * Organization: 2
             * Position: 3
             */

            // org - org
            if (sourceNode.getOrgType() == 2 && targetNode.getOrgType() == 2) {
               HrOrganization organization = hrOrganizationRepository.findById(targetNode.getObjectId()).orElse(null);
               if(organization != null) {
                   organization.setParent(null);
                   hrOrganizationRepository.save(organization);
               }
            }
            // org nối department ( 2 và 1 )
            else if (sourceNode.getOrgType() == 2 && targetNode.getOrgType() == 1) {
                HRDepartment department = hrDepartmentRepository.findById(targetNode.getObjectId()).orElse(null);

                if (department != null) {
                    department.setOrganization(null);
                    hrDepartmentRepository.save(department);
                }
            }
            // department - department ( 1 - 1 )
            else if (sourceNode.getOrgType() == 1 && targetNode.getOrgType() == 1) {

                HRDepartment department = hrDepartmentRepository.findById(targetNode.getObjectId()).orElse(null);

                if (department != null) {
                    department.setParent(null);
                    hrDepartmentRepository.save(department);
                }
            }
            // department - position ( 1 - 3)
            else if (sourceNode.getOrgType() == 1 && targetNode.getOrgType() == 3) {
                Position position = positionRepository.findById(targetNode.getObjectId()).orElse(null);

                if (position != null) {
                    position.setDepartment(null);
                    positionRepository.save(position);
                }
            }
            // position - departmnet ( 3 -1 )
            else if (sourceNode.getOrgType() == 3 && targetNode.getOrgType() == 1) {
                List<PositionRelationShip> positionRelationShip = positionRelationShipRepository.findByPositionIdAndDepartmentId(sourceNode.getObjectId(), targetNode.getObjectId());

                if (!positionRelationShip.isEmpty()) {
                    positionRelationShipRepository.deleteById(positionRelationShip.get(0).getId());
                }
            }
            // position - position
            else if (sourceNode.getOrgType() == 3 && targetNode.getOrgType() == 3) {
                List<PositionRelationShip> positionRelationShip = positionRelationShipRepository.findBySupervisorIdAndPositionId(sourceNode.getObjectId(), targetNode.getObjectId());

                if (!positionRelationShip.isEmpty()) {
                    positionRelationShipRepository.deleteById(positionRelationShip.get(0).getId());
                }
            }

            OrganizationChartRelation organizationChartRelation = this.delete(id);

            if (organizationChartRelation != null) {
                return new OrganizationChartRelationDto(organizationChartRelation);
            }
        }
        return null;
    }

    @Override
    public OrganizationChartRelationDto savePositionRelationShip(OrganizationChartRelationDto dto) {

        if (dto == null || dto.getSourceOrg() == null || dto.getSourceOrg().getObjectId() == null || dto.getTargetOrg() == null || dto.getTargetOrg().getObjectId() == null || dto.getRelationType() == null) {
            return null;
        }
        if (HrConstants.RelationshipType.UNDER_DIRECT_MANAGEMENT.getValue() == (dto.getRelationType() + 3) || HrConstants.RelationshipType.UNDER_INDIRECT_MANAGEMENT.getValue() == (dto.getRelationType() + 3)) {
            Position source = null;
            Position target = null;
            if (dto.getSourceOrg().getObjectId() != null) {
                source = positionRepository.findById(dto.getSourceOrg().getObjectId()).orElse(null);
            }
            if (dto.getTargetOrg().getObjectId() != null) {
                target = positionRepository.findById(dto.getTargetOrg().getObjectId()).orElse(null);
            }
            if (source == null || target == null) return null;

            PositionRelationShip positionRelationShip = positionRelationShipRepository.getPositionRelationShipBySupervisorAndPosition(source.getId(), target.getId()).orElse(null);
            if (positionRelationShip == null) {
                positionRelationShip = new PositionRelationShip();
            }
            positionRelationShip.setSupervisor(source);
            positionRelationShip.setPosition(target);
            positionRelationShip.setRelationshipType(dto.getRelationType() + 3);
            PositionRelationShip response = positionRelationShipRepository.save(positionRelationShip);

            if (response != null) {
                return dto;
            }
            return null;
        } else {
            return null;
        }
    }

    @Override
    public Boolean deletePositionRelationShip(OrganizationChartRelationDto dto) {
        if (dto == null || dto.getSourceOrg() == null || dto.getSourceOrg().getObjectId() == null || dto.getTargetOrg() == null || dto.getTargetOrg().getObjectId() == null || dto.getRelationType() == null) {
            return false;
        }
        Position source = null;
        Position target = null;
        if (dto.getSourceOrg().getObjectId() != null) {
            source = positionRepository.findById(dto.getSourceOrg().getObjectId()).orElse(null);
        }
        if (dto.getTargetOrg().getObjectId() != null) {
            target = positionRepository.findById(dto.getTargetOrg().getObjectId()).orElse(null);
        }
        if (source == null || target == null) return false;

        PositionRelationShip positionRelationShip = positionRelationShipRepository.getPositionRelationShipBySupervisorAndPosition(source.getId(), target.getId()).orElse(null);
        if (positionRelationShip != null) {
            positionRelationShipRepository.delete(positionRelationShip);
            return true;
        }
        return false;
    }

    public List<OrganizationChartDto> deleteChildRelation(UUID sourceId) {
        return null;
    }
}
