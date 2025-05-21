/*
 * TA va Giang làm
 */

package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.*;
import com.globits.hr.dto.OrgChartDataDto;
import com.globits.hr.dto.OrganizationChartDto;
import com.globits.hr.dto.OrganizationChartRelationDto;
import com.globits.hr.dto.diagram.RequestGetChart;
import com.globits.hr.dto.diagram.ResponseSaveChart;
import com.globits.hr.dto.search.OrgChartSearchDto;
import com.globits.hr.repository.*;
import com.globits.hr.service.OrganizationChartService;
import com.globits.security.domain.User;
import com.globits.security.dto.UserDto;
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
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Transactional
@Service
public class OrganizationChartServiceImpl extends GenericServiceImpl<OrganizationChart, UUID> implements OrganizationChartService {

    @Autowired
    OrganizationChartRepository organizationChartRepository;

    @Autowired
    OrgChartDataRepository orgChartDataRepository;

    @Autowired
    OrganizationChartRelationRepository organizationChartRelationRepository;

    @Autowired
    PositionStaffRepository positionStaffRepository;

    @Autowired
    StaffRepository staffRepository;

    @Autowired
    HrOrganizationRepository hrOrganizationRepository;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    HRDepartmentRepository hrDepartmentRepository;

    @Autowired
    PositionRelationShipRepository positionRelationShipRepository;


    /**
     * Generates edges for the organization chart based on existing nodes
     *
     * @param orgChartData The OrgChartData to generate edges for
     * @return List of OrganizationChartRelation edges
     */
    public Set<OrganizationChartRelation> generateEdges(OrgChartData orgChartData) {
        Set<OrganizationChartRelation> edges = new HashSet<>();

        // Separate nodes by type
        Set<OrganizationChart> staffNodes = new HashSet<>();
        Set<OrganizationChart> departmentNodes = new HashSet<>();
        Set<OrganizationChart> orgNodes = new HashSet<>();
        Set<OrganizationChart> positionNodes = new HashSet<>();

        for (OrganizationChart node : orgChartData.getNodes()) {
            switch (node.getOrgType()) {
                case 0 -> staffNodes.add(node);
                case 1 -> departmentNodes.add(node);
                case 2 -> orgNodes.add(node);
                case 3 -> positionNodes.add(node);
            }
        }

        // StringBuilder to track nodes already connected
        StringBuilder idOfPositionChildrent = new StringBuilder();
        StringBuilder idOfDepartmentChildrent = new StringBuilder();
        StringBuilder idOfOrgChildren = new StringBuilder();

        // Generate edges between organizations
        edges.addAll(generateOrganizationEdges(orgNodes, orgChartData, idOfOrgChildren));

        // Generate edges between positions
        List<PositionRelationShip> listPRS = positionRelationShipRepository.findAll();
        edges.addAll(generatePositionEdges(listPRS, positionNodes, departmentNodes, orgChartData, idOfPositionChildrent, idOfDepartmentChildrent));

        // Generate edges between departments
        edges.addAll(generateDepartmentEdges(departmentNodes, orgNodes, positionNodes, orgChartData, idOfDepartmentChildrent, idOfPositionChildrent));

        return edges;
    }

    @Override
    public OrgChartDataDto generateNodesFromNode(RequestGetChart requestGetChart) {
        if (requestGetChart == null || requestGetChart.getPositionId() == null) {
            return null;
        }

        Position position = positionRepository.findById(requestGetChart.getPositionId()).orElse(null);
        if (position == null) {
            return new OrgChartDataDto();
        }

        OrgChartData orgChartData = new OrgChartData();
        orgChartData.setNodes(new HashSet<>());
        orgChartData.setEdges(new HashSet<>());

        // Lấy nodes và edges
        Set<OrganizationChart> allNodes = generateNodesFromPosition(position, requestGetChart.getNumberOfLevel());
        Set<OrganizationChart> filteredNodes = new HashSet<>();

        // Thu thập ID của tất cả các position "anh em" cùng chịu sự quản lý của một supervisor
        StringBuilder siblingIds = new StringBuilder();
        if (position.getRelationships() != null && !position.getRelationships().isEmpty()) {
            for (PositionRelationShip relationship : position.getRelationships()) {
                if (relationship.getSupervisor() != null && relationship.getSupervisor().getIsSupervisedRelationships() != null &&
                        !relationship.getSupervisor().getIsSupervisedRelationships().isEmpty()) {
                    for (PositionRelationShip sibling : relationship.getSupervisor().getIsSupervisedRelationships()) {
                        if (sibling.getPosition() != null && !sibling.getPosition().getId().equals(position.getId())) {
                            appendId(siblingIds, sibling.getPosition().getId());
                        }
                    }
                }
            }
        }

        // Lọc ra những node không phải là "anh em" của position hiện tại
        for (OrganizationChart node : allNodes) {
            if (!siblingIds.toString().contains(String.valueOf(node.getObjectId()))) {
                filteredNodes.add(node);
            }
        }

        orgChartData.getNodes().addAll(filteredNodes);
        orgChartData.getEdges().addAll(generateEdges(orgChartData));

        return new OrgChartDataDto(orgChartData);
    }

    @Override
    public OrgChartDataDto generateNodesFromDepartment(RequestGetChart request) {
        if (request == null || request.getDepartmentId() == null) {
            return null;
        }

        HRDepartment department = hrDepartmentRepository.findById(request.getDepartmentId()).orElse(null);
        if (department == null) {
            return new OrgChartDataDto();
        }

        OrgChartData orgChartData = new OrgChartData();
        orgChartData.setNodes(new HashSet<>());
        orgChartData.setEdges(new HashSet<>());

        // Lấy nodes và edges
        orgChartData.getNodes().addAll(generateNodesFromNodeDepartment(department, request.getNumberOfLevel()));
        orgChartData.getEdges().addAll(generateEdges(orgChartData));

        return new OrgChartDataDto(orgChartData);
    }

    private Set<OrganizationChart> generateNodesFromNodeDepartment(HRDepartment department, Integer numberOfLevel) {
        if (department == null) {
            return null;
        }

        Set<OrganizationChart> result = new HashSet<>();
        int maxLevel = numberOfLevel != null ? numberOfLevel : 1;
        StringBuilder idOfNode = new StringBuilder();

        getNodeDepartments(department, result, 0, maxLevel, idOfNode);
        return result;
    }

    private Set<OrganizationChart> generateNodesFromPosition(Position position, Integer numberOfLevel) {
        if (position == null) {
            return null;
        }

        Set<OrganizationChart> result = new HashSet<>();
        int maxLevel = numberOfLevel != null ? numberOfLevel : 1;
        StringBuilder idOfNode = new StringBuilder();

        getNodePositionsSimple(position, result, 0, maxLevel, idOfNode);
        return result;
    }

    private void getNodePositionsSimple(Position position, Set<OrganizationChart> result, int currentLevel, int maxLevel, StringBuilder idOfNode) {
        if (position == null || currentLevel > maxLevel || idOfNode.toString().contains(String.valueOf(position.getId()))) {
            return;
        }

        result.add(createOrgChartNode(position, currentLevel));
        appendId(idOfNode, position.getId());

        // Xử lý cấp dưới và cấp trên
        getUpperPositions(position, result, currentLevel, maxLevel, idOfNode);
        getLowerPositions(position, result, currentLevel, maxLevel, idOfNode);

    }

    private void getNodePositions(Position position, Set<OrganizationChart> result, int currentLevel, int maxLevel, StringBuilder idOfNode) {
        if (position == null || currentLevel > maxLevel || idOfNode.toString().contains(String.valueOf(position.getId()))) {
            return;
        }

        result.add(createOrgChartNode(position, currentLevel));
        appendId(idOfNode, position.getId());

        // Xử lý cấp dưới và cấp trên
        processRelationships(position.getIsSupervisedRelationships(), result, currentLevel, maxLevel, idOfNode, false);
        processRelationships(position.getRelationships(), result, currentLevel, maxLevel, idOfNode, false);

    }

    private void getUpperPositions(Position position, Set<OrganizationChart> result, int currentLevel, int maxLevel, StringBuilder idOfNode) {
//        if (position == null || currentLevel > maxLevel || idOfNode.toString().contains(String.valueOf(position.getId()))) {
//            return;
//        }

        if(position == null || currentLevel > maxLevel) {
            return;
        }

        result.add(createOrgChartNode(position, currentLevel));
        appendId(idOfNode, position.getId());

        if (position.getRelationships() != null && !position.getRelationships().isEmpty()) {
            for (PositionRelationShip p : position.getRelationships()) {
                if (p.getSupervisor() != null) {
                    getUpperPositions(p.getSupervisor(), result, currentLevel + 1, maxLevel, idOfNode);
                }
            }
        }
    }

    private void getLowerPositions(Position position, Set<OrganizationChart> result, int currentLevel, int maxLevel, StringBuilder idOfNode) {
//        if (position == null || currentLevel > maxLevel || idOfNode.toString().contains(String.valueOf(position.getId()))) {
//            return;
//        }

        if(position == null || currentLevel > maxLevel) {
            return;
        }
        result.add(createOrgChartNode(position, currentLevel));
        appendId(idOfNode, position.getId());

        if (position.getIsSupervisedRelationships() != null && !position.getIsSupervisedRelationships().isEmpty()) {
            for (PositionRelationShip p : position.getIsSupervisedRelationships()) {
                if (p.getPosition() != null) {
                    getLowerPositions(p.getPosition(), result, currentLevel + 1, maxLevel, idOfNode);
                }
            }
        }
    }

    private void processRelationships(Set<PositionRelationShip> relationships, Set<OrganizationChart> result, int currentLevel, int maxLevel, StringBuilder idOfNode, boolean showDepartment) {
        if (relationships == null || relationships.isEmpty()) {
            return;
        }

        relationships.forEach(relation -> {
            if (relation.getPosition() != null && !idOfNode.toString().contains(String.valueOf(relation.getPosition().getId()))) {
                getNodePositions(relation.getPosition(), result, currentLevel + 1, maxLevel, idOfNode);
            }
            if (relation.getSupervisor() != null && !idOfNode.toString().contains(String.valueOf(relation.getSupervisor().getId()))) {
                getNodePositions(relation.getSupervisor(), result, currentLevel + 1, maxLevel, idOfNode);
            }
            if (showDepartment) {
                if (relation.getDepartment() != null && !idOfNode.toString().contains(String.valueOf(relation.getDepartment().getId()))) {
                    getNodeDepartments(relation.getDepartment(), result, currentLevel + 1, maxLevel, idOfNode);
                }
            }
        });
    }


    private void getNodeDepartments(HRDepartment department, Set<OrganizationChart> result, int currentLevel, int maxLevel, StringBuilder idOfNode) {
        if (department == null || currentLevel > maxLevel || idOfNode.toString().contains(String.valueOf(department.getId()))) {
            return;
        }

        result.add(createOrgChartNode(department, currentLevel));
        appendId(idOfNode, department.getId());

        department.getPositions().forEach(position -> getNodePositions(position, result, currentLevel + 1, maxLevel, idOfNode));

        if (department.getParent() != null) {
            getNodeDepartments((HRDepartment) department.getParent(), result, currentLevel + 1, maxLevel, idOfNode);
        } else if (department.getOrganization() != null) {
            getNodeOrgs(department.getOrganization(), result, currentLevel + 1, maxLevel, idOfNode);
        }

        Optional.ofNullable(department.getSubDepartments()).ifPresent(subDepts -> subDepts.forEach(sub -> getNodeDepartments((HRDepartment) sub, result, currentLevel + 1, maxLevel, idOfNode)));
    }

    private void getNodeOrgs(HrOrganization org, Set<OrganizationChart> result, int currentLevel, int maxLevel, StringBuilder idOfNode) {
        if (org == null || currentLevel > maxLevel || idOfNode.toString().contains(String.valueOf(org.getId()))) {
            return;
        }

        result.add(createOrgChartNode(org, currentLevel));
        appendId(idOfNode, org.getId());

        if (org.getParent() != null) {
            getNodeOrgs((HrOrganization) org.getParent(), result, currentLevel + 1, maxLevel, idOfNode);
        }

        Optional.ofNullable(org.getDepartments()).ifPresent(depts -> depts.stream().filter(dept -> dept.getParent() == null).forEach(dept -> getNodeDepartments(dept, result, currentLevel + 1, maxLevel, idOfNode)));
    }

    // Helper methods
    private OrganizationChart createOrgChartNode(Position position, int level) {
        OrganizationChart node = new OrganizationChart();
        node.setObjectId(position.getId());
        node.setTitle(position.getName());
        node.setName(position.getStaff() != null ? position.getStaff().getDisplayName() : null);
        node.setOrgType(3);
        node.setDescription(String.valueOf(level));
        if (level == 0) {
            node.setHighlight(true);
        }
        return node;
    }

    private OrganizationChart createOrgChartNode(HRDepartment dept, int level) {
        OrganizationChart node = new OrganizationChart();
        node.setObjectId(dept.getId());
        node.setName(dept.getName());
        node.setTitle(dept.getName());
        node.setOrgType(1);
        node.setDescription(String.valueOf(level + 1));
        return node;
    }

    private OrganizationChart createOrgChartNode(HrOrganization org, int level) {
        OrganizationChart node = new OrganizationChart();
        node.setObjectId(org.getId());
        node.setName(org.getName());
        node.setTitle(org.getName());
        node.setOrgType(2);
        node.setDescription(String.valueOf(level + 1));
        return node;
    }

    private void appendId(StringBuilder idOfNode, UUID id) {
        if (!idOfNode.isEmpty()) {
            idOfNode.append(",");
        }
        idOfNode.append(id);
    }

    /**
     * Generate edges between organization nodes based on organizational hierarchy
     *
     * @param orgNodes        Set of organization nodes to connect
     * @param orgChartData    The OrgChartData to add edges to
     * @param idOfOrgChildren StringBuilder to track organization nodes already connected
     * @return Set of OrganizationChartRelation edges between organization nodes
     */
    private Set<OrganizationChartRelation> generateOrganizationEdges(Set<OrganizationChart> orgNodes, OrgChartData orgChartData, StringBuilder idOfOrgChildren) {

        Set<OrganizationChartRelation> orgEdges = new HashSet<>();

        for (OrganizationChart orgNode : orgNodes) {
            if (orgNode.getObjectId() == null) {
                continue;
            }

            HrOrganization organization = hrOrganizationRepository.findById(orgNode.getObjectId()).orElse(null);
            if (organization == null) {
                continue;
            }

            // Connect organization to parent organization
            if (organization.getParent() != null && !idOfOrgChildren.toString().contains("" + orgNode.getId())) {
                for (OrganizationChart parentOrg : orgNodes) {
                    if (parentOrg.getObjectId().equals(organization.getParent().getId())) {
                        OrganizationChartRelation relation = new OrganizationChartRelation();
                        relation.setRelationType(0); // Parent-child relationship
                        relation.setOrgChartData(orgChartData);
                        relation.setSourceOrg(parentOrg);
                        relation.setTargetOrg(orgNode);

                        // Track connected organization
                        if (idOfOrgChildren.length() > 0) {
                            idOfOrgChildren.append(",");
                        }
                        idOfOrgChildren.append(orgNode.getId());

                        orgEdges.add(relation);
                        break;
                    }
                }
            }
        }

        return orgEdges;
    }

    /**
     * Generate edges between positions based on position relationships
     */
    private Set<OrganizationChartRelation> generatePositionEdges(List<PositionRelationShip> listPRS, Set<OrganizationChart> positionNodes, Set<OrganizationChart> departmentNodes, OrgChartData orgChartData, StringBuilder idOfPositionChildrent, StringBuilder idOfDepartmentChildrent) {

        Set<OrganizationChartRelation> positionEdges = new HashSet<>();

        for (PositionRelationShip prs : listPRS) {
            OrganizationChart positionNode = null;
            OrganizationChart supervisorNode = null;
            OrganizationChart departmentNode = null;

            // Find matching position and supervisor nodes
            for (OrganizationChart node : positionNodes) {
                if (prs.getPosition() != null && node.getObjectId().equals(prs.getPosition().getId())) {
                    positionNode = node;
                }
                if (prs.getSupervisor() != null && node.getObjectId().equals(prs.getSupervisor().getId())) {
                    supervisorNode = node;
                }
            }

            // Find matching department node
            for (OrganizationChart node : departmentNodes) {
                if (prs.getDepartment() != null && node.getObjectId().equals(prs.getDepartment().getId())) {
                    departmentNode = node;
                }
            }

            // Generate edges based on relationship type
            switch (prs.getRelationshipType()) {
                // Position to Department
                case 1, 2:
                    if (positionNode != null && departmentNode != null) {
                        OrganizationChartRelation relation = new OrganizationChartRelation();
                        relation.setRelationType((prs.getRelationshipType() + 1) % 2);
                        relation.setOrgChartData(orgChartData);
                        relation.setSourceOrg(positionNode);
                        relation.setTargetOrg(departmentNode);

                        // Track department connections
                        if (!idOfDepartmentChildrent.isEmpty()) {
                            idOfDepartmentChildrent.append(",");
                        }
                        idOfDepartmentChildrent.append(departmentNode.getId());

                        positionEdges.add(relation);
                    }
                    break;

                // Position to Position
                case 3, 4:
                    if (supervisorNode != null && positionNode != null) {
                        OrganizationChartRelation relation = new OrganizationChartRelation();
                        relation.setRelationType((prs.getRelationshipType() + 1) % 2);
                        relation.setOrgChartData(orgChartData);
                        relation.setSourceOrg(supervisorNode);
                        relation.setTargetOrg(positionNode);

                        // Track position connections
//                        if (idOfPositionChildrent.length() > 0) {
//                            idOfPositionChildrent.append(",");
//                        }
//                        idOfPositionChildrent.append(positionNode.getId());

                        positionEdges.add(relation);
                    }
                    break;
            }
        }

        return positionEdges;
    }

    /**
     * Generate edges between departments and related entities
     */
    private Set<OrganizationChartRelation> generateDepartmentEdges(Set<OrganizationChart> departmentNodes, Set<OrganizationChart> orgNodes, Set<OrganizationChart> positionNodes, OrgChartData orgChartData, StringBuilder idOfDepartmentChildrent, StringBuilder idOfPositionChildrent) {

        Set<OrganizationChartRelation> departmentEdges = new HashSet<>();

        for (OrganizationChart departmentNode : departmentNodes) {
            if (departmentNode.getObjectId() == null) {
                continue;
            }

            HRDepartment department = hrDepartmentRepository.findById(departmentNode.getObjectId()).orElse(null);
            if (department == null) {
                continue;
            }

            // Connect department to parent department
            if (department.getParent() != null && !idOfDepartmentChildrent.toString().contains("" + departmentNode.getId())) {
                for (OrganizationChart parentDepartment : departmentNodes) {
                    if (parentDepartment.getObjectId().equals(department.getParent().getId())) {
                        OrganizationChartRelation relation = new OrganizationChartRelation();
                        relation.setRelationType(0);
                        relation.setOrgChartData(orgChartData);
                        relation.setSourceOrg(parentDepartment);
                        relation.setTargetOrg(departmentNode);

                        departmentEdges.add(relation);
                        break;
                    }
                }
            }
            // Connect organization to department
            else if (department.getOrganization() != null && !idOfDepartmentChildrent.toString().contains("" + departmentNode.getId())) {
                for (OrganizationChart orgNode : orgNodes) {
                    if (department.getOrganization().getId() != null && orgNode.getObjectId().equals(department.getOrganization().getId())) {
                        OrganizationChartRelation relation = new OrganizationChartRelation();
                        relation.setRelationType(0);
                        relation.setOrgChartData(orgChartData);
                        relation.setSourceOrg(orgNode);
                        relation.setTargetOrg(departmentNode);

                        // Track department connections
                        if (!idOfDepartmentChildrent.isEmpty()) {
                            idOfDepartmentChildrent.append(",");
                        }
                        idOfDepartmentChildrent.append(departmentNode.getId());

                        departmentEdges.add(relation);
                        break;
                    }
                }
            }

            // Connect department to position
            for (OrganizationChart positionNode : positionNodes) {
                if (positionNode == null) {
                    continue;
                }

                Position position = positionRepository.findById(positionNode.getObjectId()).orElse(null);
                if (position == null || position.getDepartment() == null) {
                    continue;
                }

                // Check if position is not already connected to another position
                if (position.getDepartment().getId().equals(department.getId()) && !idOfPositionChildrent.toString().contains("" + positionNode.getId())) {
                    OrganizationChartRelation relation = new OrganizationChartRelation();
                    relation.setRelationType(0);
                    relation.setOrgChartData(orgChartData);
                    relation.setSourceOrg(departmentNode);
                    relation.setTargetOrg(positionNode);

                    departmentEdges.add(relation);
                }
            }
        }

        return departmentEdges;
    }

    /**
     * Thêm mới node vào trong chart và thực hiện nối các node lại với nhau dựa trên
     * position, positionRelation, department, oranization có sẵn trong hệ thống
     *
     * @param dto nối được thêm mới vào trong chart
     * @return thông tin node được thêm mới và danh sách tất cả các edge của chart
     * (bao gồm cả những edge được sinh ra từ node mới)
     * <p>
     * các bước thực hiện nối
     * org => department: dựa vào org có trong department
     * department <=> department: dựa vào parent của department
     * position <=> position: dựa vào positionRelationShip
     * position => department: dựa vào relationshipType == 1, 2
     * department => position: dựa vào department có trong position
     */
    @Override
    public ResponseSaveChart saveOrUpdate(OrganizationChartDto dto) {
        if (dto != null) {
            ResponseSaveChart res = new ResponseSaveChart();
            OrgChartData orgChartData = null;
            if (dto.getOrgChartDataId() != null) {
                orgChartData = orgChartDataRepository.findById(dto.getOrgChartDataId()).orElse(null);
            }

            if (orgChartData == null) {
                return null;
            }

            // Existing node creation and validation logic
            OrganizationChart newNode = null;
            if (dto.getId() != null) {
                newNode = organizationChartRepository.findById(dto.getId()).orElse(null);
            }
            if (newNode == null) {
                newNode = new OrganizationChart();
            }

            // Code duplication check
            if (dto.getCode() != null && dto.getOrgChartDataId() != null) {
                List<OrganizationChart> listCheck = organizationChartRepository.findByObjectIdAndOrgChartDataId(dto.getObjectId(), dto.getOrgChartDataId());
                if (!CollectionUtils.isEmpty(listCheck)) {
                    for (OrganizationChart check : listCheck) {
                        if (check != null && check.getId() != null && !check.getId().equals(newNode.getId())) {
                            OrganizationChartDto ret = new OrganizationChartDto();
                            ret.setCode("ERROR");
                            ret.setDescription("Đã tồn tại đối tượng tên: " + dto.getName() + " trên sơ đồ này");
                            res.setNode(ret);
                            return res;
                        }
                    }
                }
            }

            // Set node properties
            newNode.setX(dto.getX());
            newNode.setY(dto.getY());
            newNode.setCode(dto.getCode());
            newNode.setName(dto.getName());
            newNode.setTitle(dto.getTitle());
            newNode.setDescription(dto.getDescription());
            newNode.setObjectId(dto.getObjectId());
            newNode.setOrgIcon(dto.getOrgIcon());
            newNode.setOrgShape(dto.getOrgShape());
            newNode.setOrgType(dto.getOrgType());
            newNode.setOrgChartData(orgChartData);

            // Thêm node mới vào collection hiện tại
            if (orgChartData.getNodes() == null) {
                orgChartData.setNodes(new HashSet<>());
            }
            orgChartData.getNodes().add(newNode);

            // Generate edges
            Set<OrganizationChartRelation> generatedEdges = generateEdges(orgChartData);

            // Cập nhật edges mà không thay đổi tham chiếu
            if (orgChartData.getEdges() == null) {
                orgChartData.setEdges(new HashSet<>());
            } else {
                orgChartData.getEdges().clear(); // Xóa phần tử cũ
            }
            orgChartData.getEdges().addAll(generatedEdges); // Thêm phần tử mới

            // Lưu orgChartData
            orgChartData = orgChartDataRepository.save(orgChartData);

            // Convert edges to DTOs
            List<OrganizationChartRelationDto> edgesDto = new ArrayList<>();
            for (OrganizationChartRelation relation : orgChartData.getEdges()) {
                edgesDto.add(new OrganizationChartRelationDto(relation));
            }

            OrganizationChartDto ret = new OrganizationChartDto(newNode);

            res.setNode(ret);
            res.setEdges(edgesDto);
            return res;
        }
        return null;
    }

    @Override
    public OrgChartDataDto saveListOrganizationChart(List<OrganizationChartDto> dtos) {
        if (dtos == null || dtos.isEmpty()) return null;
        OrgChartData orgChartData = null;
        if (dtos.get(0).getOrgChartDataId() != null) {
            orgChartData = orgChartDataRepository.findById(dtos.get(0).getOrgChartDataId()).orElse(null);
        }

        if (orgChartData == null) {
            return null;
        }
        for (OrganizationChartDto dto : dtos) {
            if (dto.getOrgType() == null) continue;
            Object entity = null;
            switch (dto.getOrgType()) {
                case 0:
                    entity = staffRepository.findById(dto.getObjectId()).orElse(null);
                    break;
                case 1:
                    entity = hrDepartmentRepository.findById(dto.getObjectId()).orElse(null);
                    break;
                case 2:
                    entity = hrOrganizationRepository.findById(dto.getObjectId()).orElse(null);
                    break;
                case 3:
                    entity = positionRepository.findById(dto.getObjectId()).orElse(null);
                    break;
                default:
                    break;
            }
            if (entity == null) continue;


            // Existing node creation and validation logic remains the same
            OrganizationChart newNode = null;
            if (dto.getId() != null) {
                newNode = organizationChartRepository.findById(dto.getId()).orElse(null);
            }
            if (newNode == null) {
                newNode = new OrganizationChart();
            }

            // Code duplication check remains the same
            if (dto.getCode() != null && dto.getOrgChartDataId() != null) {
                List<OrganizationChart> listCheck = organizationChartRepository.findByObjectIdAndOrgChartDataId(dto.getObjectId(), dto.getOrgChartDataId());
                if (!CollectionUtils.isEmpty(listCheck)) {
                    for (OrganizationChart check : listCheck) {
                        if (check != null && check.getId() != null && !check.getId().equals(newNode.getId())) {
                            continue;
                        }
                    }
                }
            }

            // Set node properties
            newNode.setX(dto.getX());
            newNode.setY(dto.getY());
            newNode.setCode(dto.getCode());
            newNode.setName(dto.getName());
            newNode.setTitle(dto.getTitle());
            newNode.setDescription(dto.getDescription());
            newNode.setObjectId(dto.getObjectId());
            newNode.setOrgIcon(dto.getOrgIcon());
            newNode.setOrgShape(dto.getOrgShape());
            newNode.setOrgType(dto.getOrgType());
            newNode.setOrgChartData(orgChartData);

            orgChartData.getNodes().add(newNode);
        }
        // Generate edges using the new method
        Set<OrganizationChartRelation> generatedEdges = generateEdges(orgChartData);

        if (!orgChartData.getEdges().isEmpty()) {
            orgChartData.getEdges().removeAll(orgChartData.getEdges()); // Xóa phần tử cũ, nhưng giữ nguyên reference
        }
        orgChartData.getEdges().addAll(generatedEdges); // Thêm phần tử mới

        // Save
        orgChartData = orgChartDataRepository.save(orgChartData);
        return new OrgChartDataDto(orgChartData);
    }

    @Override
    public List<OrganizationChartDto> searchByDto(OrgChartSearchDto searchDto) {
        String SQL = " SELECT new com.globits.hr.dto.OrganizationChartDto(oc) FROM OrganizationChart oc WHERE (oc.voided is null or oc.voided = false) ";
        String WHERECLAUSE = "  ";
        if (searchDto != null) {
            if (StringUtils.hasText(searchDto.getKeyword())) {
                WHERECLAUSE += " AND oc.code like :keyword ";
            }
            if (searchDto.getObjectId() != null) {
                WHERECLAUSE += " AND oc.objectId = :objectId ";
            }
        }
        Query query = manager.createQuery(SQL + WHERECLAUSE, OrganizationChartDto.class);
        if (searchDto != null) {
            if (StringUtils.hasText(searchDto.getKeyword())) {
                query.setParameter("keyword", searchDto.getKeyword());
            }
            if (searchDto.getObjectId() != null) {
                query.setParameter("objectId", searchDto.getObjectId());
            }
        }
        return query.getResultList();
    }

    @Override
    public OrganizationChartDto deleteById(UUID id) {
        if (id != null) {
            String hql = "delete from OrganizationChartRelation r where r.sourceOrg.id = :orgId or r.targetOrg.id=:orgId";
            Query q = this.manager.createQuery(hql);
            q.setParameter("orgId", id);
            int numberItem = q.executeUpdate();

            OrganizationChart organizationChart = this.delete(id);
            if (organizationChart != null) {
                return new OrganizationChartDto(organizationChart);
            }
        }
        return null;
    }

    @Override
    @Transactional
    public Boolean deleteOrgChartData(UUID id) {
        if (id == null) {
            return false;
        }
        try {
            OrgChartData orgChartData = this.orgChartDataRepository.findById(id).orElse(null);
            if (orgChartData == null) {
                return false;
            }

            if (orgChartData.getNodes() != null && !orgChartData.getNodes().isEmpty()) {
                for (OrganizationChart orgChart : orgChartData.getNodes()) {
                    if (orgChart.getId() != null) {
                        organizationChartRelationRepository.deleteByOrgChartId(orgChart.getId());
                        organizationChartRepository.deleteById(orgChart.getId());
                    }
                }
                orgChartData.getNodes().clear();
                this.orgChartDataRepository.save(orgChartData);
            }
            orgChartDataRepository.deleteById(id);

            return true;
        } catch (Exception e) {
            //System.err.println("Error delete OrgChartData: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public Boolean deleteMultipleOrgChartData(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        try {
            for (UUID orgChartDataId : ids) {
                if (!deleteOrgChartData(orgChartDataId)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            //System.err.println("Error delete multiple OrgChartData: " + e.getMessage());
            return false;
        }
    }

    @Override
    public OrgChartDataDto saveOrgChartData(OrgChartDataDto dto) {
        OrgChartData entity = null;
        if (dto.getId() != null) {
            entity = this.orgChartDataRepository.findById(dto.getId()).orElse(null);
        }
        if (entity == null) {
            entity = new OrgChartData();
        }
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setBase64Edges(dto.getBase64Edges());
        entity.setBase64Nodes(dto.getBase64Nodes());

        /*
         * TODO : Viet them doan Save tung Object Item neu muon (cung chua can thiet lam)
         */

        entity = orgChartDataRepository.saveAndFlush(entity);
        return new OrgChartDataDto(entity);
    }

    @Override
    public OrgChartDataDto getOrgChartData(UUID id) {
        OrgChartData entity = this.orgChartDataRepository.findById(id).orElse(null);
        if (entity != null) {
            return new OrgChartDataDto(entity);
        }
        return null;
    }

    @Override
    public Page<OrgChartDataDto> pagingOrgChartData(OrgChartSearchDto dto) {
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

        String sqlCount = "select count(distinct entity.id) from OrgChartData as entity ";
        String sql = "select distinct new com.globits.hr.dto.OrgChartDataDto(entity) from OrgChartData as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text ) ";
        }

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query query = manager.createQuery(sql, OrgChartDataDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            query.setParameter("text", '%' + dto.getKeyword() + '%');
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }

        List<OrgChartDataDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        Page<OrgChartDataDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    public UserDto getLineManager(@PathVariable("username") String username) {
        String hql = "from OrganizationChart c inner join Staff s on c.objectId=s.id where s.user.username=:username";
        Query q = manager.createQuery(hql);
        q.setParameter("username", username);
        List<OrganizationChart> orgCharts = (List<OrganizationChart>) q.getResultList();
        if (!CollectionUtils.isEmpty(orgCharts)) {
            hql = "select rc.sourceOrg from OrganizationChartRelation rc where rc.targetOrg.id=:id and rc.relationType=0 and rc.sourceOrg.orgType=0";
            q = manager.createQuery(hql);
            q.setParameter("id", orgCharts.get(0).getId());
            orgCharts = (List<OrganizationChart>) q.getResultList();
            if (!CollectionUtils.isEmpty(orgCharts)) {
                Staff staff = staffRepository.findById(orgCharts.get(0).getObjectId()).orElse(null);
                if (staff != null && staff.getUser() != null) {
                    User user = staff.getUser();
                    String s = user.getUsername();
                    return new UserDto(user);
                }
            }
        }
        return null;
    }

    @Override
    public OrgChartDataDto syncPosition() {
//        OrgChartData entity = this.orgChartDataRepository.findById(id).orElse(null);
        OrgChartData entity = new OrgChartData();
        if (entity == null) {
            return null;
        }

        // danh sach tao node
        if (entity.getNodes() != null) {
            entity.getNodes().clear();
        } else {
            entity.setNodes(new HashSet<>());
        }
        List<Position> listPosition = positionRepository.findAll();

        for (Position position : listPosition) {
            OrganizationChart node = new OrganizationChart();
            node.setOrgChartData(entity);
            node.setOrgType(3);
            node.setObjectId(position.getId());

//            if(position.getTitle()!=null && position.getTitle().getName()!=null) {
//                node.setTitle(position.getTitle().getName());
//            }else {
//            }
            node.setTitle(position.getName());

            if (position.getStaff() != null) {
                node.setName(position.getStaff().getDisplayName());
            } else {
                node.setName("Vacant");
            }
            node.setCode(position.getCode());
            entity.getNodes().add(node);
        }

        // danh sach quan he
        List<PositionRelationShip> listPRS = positionRelationShipRepository.findAll();
        if (entity.getEdges() != null) {
            entity.getEdges().clear();
        } else {
            entity.setEdges(new HashSet<>());
        }
        for (PositionRelationShip prs : listPRS) {
            OrganizationChart positionNode = null;
            OrganizationChart supervisorNode = null;

            for (OrganizationChart node : entity.getNodes()) {
                if (prs.getPosition() != null && node.getObjectId().equals(prs.getPosition().getId())) {
                    positionNode = node;
                }
                if (prs.getSupervisor() != null && node.getObjectId().equals(prs.getSupervisor().getId())) {
                    supervisorNode = node;
                }
            }

            if (positionNode != null && supervisorNode != null) {
                OrganizationChartRelation relation = new OrganizationChartRelation();
                relation.setRelationType((prs.getRelationshipType() + 1) % 2);
                relation.setOrgChartData(entity);
                relation.setSourceOrg(supervisorNode);
                relation.setTargetOrg(positionNode);

                entity.getEdges().add(relation);
            }

        }

//        entity = orgChartDataRepository.save(entity);

        if (entity != null) {
            return new OrgChartDataDto(entity);
        } else {
            return null;
        }
    }

    @Override
    public OrgChartDataDto refreshOrgChartData(UUID id) {
        if (id == null) return null;

        OrgChartData entity = orgChartDataRepository.findById(id).orElse(null);
        if (entity == null) return null;

        // re-new node (name -title)
        for (OrganizationChart node : entity.getNodes()) {
            switch (node.getOrgType()) {
                case 0:
                    Staff staff = staffRepository.findById(node.getObjectId()).orElse(null);
                    if (staff != null) {
                        node.setName(staff.getDisplayName());
                    }
                    break;
                case 1:
                    HRDepartment department = hrDepartmentRepository.findById(node.getObjectId()).orElse(null);
                    if (department != null) {
                        node.setName(department.getName());
                    }
                    break;
                case 2:
                    HrOrganization org = hrOrganizationRepository.findById(node.getObjectId()).orElse(null);
                    if (org != null) {
                        node.setName(org.getName());
                    }
                    break;
                case 3:
                    Position position = positionRepository.findById(node.getObjectId()).orElse(null);
                    if (position != null) {
                        node.setTitle(position.getName());
                        if (position.getStaff() != null) {
                            node.setName(position.getStaff().getDisplayName());
                        } else {
                            node.setName("Vacant");
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        // Generate edges using the new method
        Set<OrganizationChartRelation> generatedEdges = generateEdges(entity);


        if (entity.getEdges() == null) {
            entity.setEdges(new HashSet<>());
        }
        entity.getEdges().clear();

        entity.getEdges().addAll(generatedEdges); // Thêm phần tử mới

        return new OrgChartDataDto(entity);
    }
}
