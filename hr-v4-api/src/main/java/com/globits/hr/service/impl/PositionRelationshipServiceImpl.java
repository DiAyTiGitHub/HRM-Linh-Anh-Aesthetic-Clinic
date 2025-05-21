package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.*;
import com.globits.hr.dto.OrganizationChartRelationDto;
import com.globits.hr.dto.PositionDto;
import com.globits.hr.dto.PositionRelationshipDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.PositionRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PositionRelationshipServiceImpl extends GenericServiceImpl<PositionRelationShip, UUID> implements PositionRelationshipService {
    @Autowired
    private PositionRelationShipRepository positionRelationshipRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private HRDepartmentRepository departmentRepository;

    @Autowired
    OrganizationChartRepository organizationChartRepository;

    @Autowired
    HrOrganizationRepository hrOrganizationRepository;

    @Autowired
    StaffRepository staffRepository;

    public void handleSetRelationshipsInPotion(PositionDto dto, Position entity) {
        Set<PositionRelationShip> relationShips = new HashSet<>();

        if (dto.getRelationships() != null && !dto.getRelationships().isEmpty()) {
            for (PositionRelationshipDto relationshipDto : dto.getRelationships()) {
                PositionRelationShip relationShip = null;

                if (relationshipDto.getId() != null) {
                    relationShip = positionRelationshipRepository.findById(relationshipDto.getId()).orElse(null);
                }

                if (relationShip == null) {
                    relationShip = new PositionRelationShip();
                    relationShip.setPosition(entity);
                }

                /*
                 * 1. Position quan ly truc tiep phong ban
                 * 2. Position quan ly gian tiep phong ban
                 */
                if (relationshipDto.getRelationshipType() != null && (relationshipDto.getRelationshipType() == 1 || relationshipDto.getRelationshipType() == 2)) {
                    if (relationshipDto.getDepartment() != null) {
                        HRDepartment department = departmentRepository.findById(relationshipDto.getDepartment().getId()).orElse(null);
                        relationShip.setDepartment(department);
                        relationShip.setSupervisor(null);
                    }
                }
                /*
                 * 3. supervisor quan ly truc tiep position
                 * 4. supervisor quan ly gian tiep position
                 */
                else if (relationshipDto.getRelationshipType() != null && (relationshipDto.getRelationshipType() == 3 || relationshipDto.getRelationshipType() == 4)) {
                    if (relationshipDto.getSupervisor() != null) {
                        Position supervisor = positionRepository.findById(relationshipDto.getSupervisor().getId()).orElse(null);
                        relationShip.setSupervisor(supervisor);
                        relationShip.setDepartment(null);
                    }
                }

                relationShip.setRelationshipType(relationshipDto.getRelationshipType());

                relationShips.add(relationShip);
            }
        }

        if (entity.getRelationships() == null) {
            entity.setRelationships(new HashSet<>());
        }
        entity.getRelationships().clear();
        entity.getRelationships().addAll(relationShips);
    }

    @Override
    public void mappingWithChartRelation(OrganizationChartRelationDto dto) {
        if (dto.getSourceOrgId() == null || dto.getTargetOrgId() == null) {
            return; // Không có source hoặc target → return ngay
        }

        OrganizationChart source = organizationChartRepository.findById(dto.getSourceOrgId()).orElse(null);
        OrganizationChart target = organizationChartRepository.findById(dto.getTargetOrgId()).orElse(null);

        if (source == null || target == null) {
            return; // Không tìm thấy source hoặc target → return ngay
        }

        Object entityOfSource = getEntityByObjectIdAndOrgType(source.getObjectId(), source.getOrgType());
        Object entityOfTarget = getEntityByObjectIdAndOrgType(target.getObjectId(), target.getOrgType());

        if (entityOfSource == null || entityOfTarget == null) {
            return;
        }


        // org - org
        if (entityOfSource instanceof HrOrganization && entityOfTarget instanceof HrOrganization) {
            HrOrganization organizationSource = (HrOrganization) entityOfSource;
            HrOrganization organizationTarget = (HrOrganization) entityOfTarget;
            if (organizationSource != null && organizationTarget != null) {
                organizationTarget.setParent(organizationSource);
                hrOrganizationRepository.save(organizationTarget);
            }
        }  // org - department
        else if (entityOfSource instanceof HrOrganization && entityOfTarget instanceof HRDepartment) {
            HRDepartment department = (HRDepartment) entityOfTarget;
            HrOrganization organization = (HrOrganization) entityOfSource;
            if (department != null && organization != null) {
                department.setOrganization(organization);
                departmentRepository.save(department);
            }
        }
        // department - department
        else if (entityOfSource instanceof HRDepartment && entityOfTarget instanceof HRDepartment) {
            HRDepartment departmentTarget = (HRDepartment) entityOfTarget;
            HRDepartment departmentSource = (HRDepartment) entityOfSource;

            if (departmentTarget != null && departmentSource != null) {
                departmentTarget.setParent(departmentSource);
                departmentTarget.setOrganization(departmentSource.getOrganization());
                departmentRepository.save(departmentTarget);
            }
        }
        // Position - department
        else if (entityOfSource instanceof Position && entityOfTarget instanceof HRDepartment) {
            HRDepartment departmentTarget = (HRDepartment) entityOfTarget;
            Position positionSource = (Position) entityOfSource;

            if (departmentTarget != null && positionSource != null) {
                List<PositionRelationShip> exitPosition = positionRelationshipRepository.findByPositionIdAndDepartmentId(positionSource.getId(), departmentTarget.getId());

                PositionRelationShip positionRelationShip = exitPosition.isEmpty() ? new PositionRelationShip() : exitPosition.get(0);
                positionRelationShip.setPosition(positionSource);
                /*
                 * 1. Position quan ly truc tiep phong ban
                 * 2. Position quan ly gian tiep phong ban
                 * 3. supervisor quan ly truc tiep position
                 * 4. supervisor quan ly gian tiep position
                 */
                positionRelationShip.setRelationshipType(dto.getRelationType() == 0 ? 1 : 2);
                positionRelationShip.setDepartment(departmentTarget);

                positionRelationshipRepository.save(positionRelationShip);
            }
        }
        // Position - Position
        else if (entityOfSource instanceof Position && entityOfTarget instanceof Position) {
            Position positionSource = (Position) entityOfSource;
            Position positionTarget = (Position) entityOfTarget;
            if (positionTarget != null && positionSource != null) {
                List<PositionRelationShip> exitPosition = positionRelationshipRepository.findBySupervisorIdAndPositionId(positionSource.getId(), positionTarget.getId());
                /*
                 * 1. Position quan ly truc tiep phong ban
                 * 2. Position quan ly gian tiep phong ban
                 * 3. supervisor quan ly truc tiep position
                 * 4. supervisor quan ly gian tiep position
                 */
                PositionRelationShip positionRelationShip = exitPosition.isEmpty() ? new PositionRelationShip() : exitPosition.get(0);
                positionRelationShip.setSupervisor(positionSource);
                positionRelationShip.setRelationshipType(dto.getRelationType() == 0 ? 3 : 4);
                positionRelationShip.setPosition(positionTarget);

                positionRelationshipRepository.save(positionRelationShip);
            }
        }
        // HRDepartment - Position
        else if (entityOfSource instanceof HRDepartment && entityOfTarget instanceof Position) {
            HRDepartment departmentTarget = (HRDepartment) entityOfSource;
            Position positionSource = (Position) entityOfTarget;

            if (departmentTarget != null && positionSource != null) {
                positionSource.setDepartment(departmentTarget);
                positionRepository.save(positionSource);
            }
        }
    }

    public Object getEntityByObjectIdAndOrgType(UUID objectId, Integer orgType) {
        switch (orgType) {
            case 0:
                return staffRepository.findById(objectId).orElse(null);
            case 1:
                return departmentRepository.findById(objectId).orElse(null);
            case 2:
                return hrOrganizationRepository.findById(objectId).orElse(null);
            case 3:
                return positionRepository.findById(objectId).orElse(null);
            default:
                return null;
        }
    }

}
