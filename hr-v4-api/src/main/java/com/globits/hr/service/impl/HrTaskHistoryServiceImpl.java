package com.globits.hr.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.domain.HrTaskHistory;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.HrTaskHistoryDto;
import com.globits.hr.dto.search.SearchTaskHistoryDto;
import com.globits.hr.repository.HrTaskHistoryRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.HrTaskHistoryService;
import com.globits.hr.service.UserExtService;
import com.globits.task.domain.HrTask;
import com.globits.task.dto.HrTaskDto;
import com.globits.task.dto.KanbanDto;
import com.globits.task.repository.HrTaskRepository;
import com.globits.timesheet.dto.LabelDto;
import com.globits.timesheet.repository.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jakarta.persistence.Query;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class HrTaskHistoryServiceImpl extends GenericServiceImpl<HrTaskHistory, UUID> implements HrTaskHistoryService {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> convertStringToMap(String jsonString) throws IOException {
        return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
    }

    public static String convertMapToString(Map<String, Object> map) throws JsonProcessingException {
        return objectMapper.writeValueAsString(map);
    }

    //use for handling task history,
    // the value of a key can be string (for old version of task history),
    // or can be another object that contains 2 pairs of keyValue (oldValue and newValue)

    @Autowired
    private HrTaskHistoryRepository taskHistoryRepository;

    @Autowired
    private UserExtService userService;

    @Autowired
    private HrTaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public List<HrTaskHistoryDto> getAllHistoryOfTask(UUID taskId) {
        if (taskId == null) return null;

        HrTask task = taskRepository.findById(taskId).orElse(null);
        if (task == null) return null;

        String sql = "select entity from HrTaskHistory as entity where (1=1) ";

        String whereClause = " and entity.task.id = :taskId";
        String orderBy = " ORDER BY entity.createDate desc";

        sql += whereClause + orderBy;

        Query q = manager.createQuery(sql, HrTaskHistory.class);
        q.setParameter("taskId", task.getId());

        List<HrTaskHistory> entities = q.getResultList();
        List<HrTaskHistoryDto> response = new ArrayList<>();

        try {
            for (HrTaskHistory entity : entities) {
                HrTaskHistoryDto responseItem = new HrTaskHistoryDto(entity);

                if (entity.getEvent() != null) {
                    //convert string back to object and attach to responseItem
                    Map<String, Object> resultMap = this.convertStringToMap(entity.getEvent());
                    responseItem.setEvent(resultMap);
                }

                responseItem.setTask(null);
                response.add(responseItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    public HrTaskHistoryDto createHistoryComment(HrTaskDto taskComment) {
        if (taskComment == null || taskComment.getId() == null) return null;
        HrTask task = taskRepository.findById(taskComment.getId()).orElse(null);
        if (task == null) return null;

        HrTaskHistory entity = new HrTaskHistory();
        Staff modifier = userService.getCurrentStaffEntity();

        entity.setModifier(modifier);
        entity.setCreateDate(LocalDateTime.now());
        entity.setCreatedBy(userService.getCurrentUser().getUsername());
        entity.setTask(task);
        if (modifier != null) {
            entity.setCreatedBy(modifier.getUser().getUsername());
        }

        try {
            String jsonString = this.handleTaskCommentObjectMapper(taskComment);
            entity.setEvent(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HrTaskHistory response = null;
        if (entity.getEvent() != null && entity.getEvent().length() > 2)
            response = taskHistoryRepository.save(entity);

        if (response == null) return null;

        HrTaskHistoryDto responseDto = null;
        try {
            responseDto = new HrTaskHistoryDto(entity);

            if (entity.getEvent() != null) {
                //convert string back to object and attach to responseItem
                Map<String, Object> resultMap = this.convertStringToMap(entity.getEvent());
                responseDto.setEvent(resultMap);
            }

            responseDto.setTask(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseDto;
    }

    private String handleTaskCommentObjectMapper(HrTaskDto taskComment)
            throws Exception {
        Map<String, Object> map = new HashMap<>();

        //handle compare value of comment
        if (taskComment.getComment() != null && taskComment.getComment().length() > 0) {
            map.put("comment", taskComment.getComment());
        }

        return this.convertMapToString(map);
    }

    @Override
    public HrTaskHistoryDto generateHistoryLog(HrTaskDto oldValues, HrTaskDto currentValues, int countSubtaskUpdated, int countSubTaskUnchecked) {
        HrTask task = taskRepository.findById(currentValues.getId()).orElse(null);
        if (task == null) return null;

        HrTaskHistory entity = new HrTaskHistory();
        Staff modifier = userService.getCurrentStaffEntity();

        entity.setModifier(modifier);
        entity.setCreateDate(LocalDateTime.now());
        entity.setCreatedBy(userService.getCurrentUser().getUsername());
        entity.setTask(task);

        if (modifier != null) {
            entity.setCreatedBy(modifier.getUser().getUsername());
        }

        //save first time to notify that there's a record of task history to manipulate its id

        try {
            //this is task is new
            if (oldValues == null || oldValues.getId() == null) {

                Map<String, Object> map = new HashMap<>();

                map.put("isNew", true);
                //handle compare value of comment
                if (currentValues.getComment() != null && currentValues.getComment().length() > 0) {
                    map.put("comment", currentValues.getComment());
                }

                String jsonString = this.convertMapToString(map);
                entity.setEvent(jsonString);

            }
            //this task is updated, then create object string to store in event field of task history
            else {
                String jsonString = this.handleTaskHistoryObjectMapper(oldValues, currentValues, countSubtaskUpdated, countSubTaskUnchecked);

                entity.setEvent(jsonString);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        HrTaskHistory response = null;
        if (entity.getEvent() != null && entity.getEvent().length() > 2)
            response = taskHistoryRepository.save(entity);

        if (response == null) return null;
        return new HrTaskHistoryDto(response);
    }

    private String handleTaskHistoryObjectMapper(HrTaskDto oldValues, HrTaskDto currentValues, int countSubtaskUpdated, int countSubTaskUnchecked)
            throws Exception {
        Map<String, Object> map = new HashMap<>();

        //log that an todo/subtaskItem is updated from UNCHECKED TO CHECKED
        if (countSubtaskUpdated > 0) map.put("countSubtaskUpdated", countSubtaskUpdated);

        //log that an todo/subtaskItem is updated from CHECKED TO UNCHECKED
        if (countSubTaskUnchecked > 0) map.put("countSubTaskUnchecked", countSubTaskUnchecked);

        //handle compare value of comment
        if (currentValues.getComment() != null && currentValues.getComment().length() > 0) {
            map.put("comment", currentValues.getComment());
        }

        //handle compare value of labels
        String oldLabels = "";
        String currentLabels = "";
        if (oldValues.getLabels() != null) {
            Collections.sort(oldValues.getLabels(), new Comparator<LabelDto>() {
                @Override
                public int compare(LabelDto o1, LabelDto o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            for (LabelDto label : oldValues.getLabels()) {
                if (oldLabels.length() != 0) oldLabels += ", ";
                oldLabels += label.getName();
            }
        }
        if (currentValues.getLabels() != null) {
            Collections.sort(currentValues.getLabels(), new Comparator<LabelDto>() {
                @Override
                public int compare(LabelDto o1, LabelDto o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            for (LabelDto label : currentValues.getLabels()) {
                if (currentLabels.length() != 0) currentLabels += ", ";
                currentLabels += label.getName();
            }
        }
        if (!oldLabels.equals(currentLabels)) {
            map.put("labels", getOnSaveValue(oldLabels, currentLabels));
        }

        //handle compare value of priority
        String oldPriority = "";
        String currentPriority = "";
        if (oldValues.getPriority() != null) oldPriority = oldValues.getPriority().toString();
        if (currentValues.getPriority() != null) currentPriority = currentValues.getPriority().toString();
        if (!oldPriority.equals(currentPriority)) {
            map.put("priority", getOnSaveValue(oldPriority, currentPriority));
        }

        //handle compare value of orderNumber
        String oldOrderNumber = "";
        String currentOrderNumber = "";
        if (oldValues.getOrderNumber() != null) oldOrderNumber = oldValues.getOrderNumber().toString();
        if (currentValues.getOrderNumber() != null) currentOrderNumber = currentValues.getOrderNumber().toString();
        if (!oldOrderNumber.equals(currentOrderNumber)) {
            map.put("orderNumber", getOnSaveValue(oldOrderNumber, currentOrderNumber));
        }

        //handle compare value of status
        String oldStatus = "";
        String currentStatus = "";
        if (oldValues.getStatus() != null) oldStatus = oldValues.getStatus().getName();
        if (currentValues.getStatus() != null) currentStatus = currentValues.getStatus().getName();
        if (!oldStatus.equals(currentStatus)) {
            map.put("status", getOnSaveValue(oldStatus, currentStatus));
        }

        //handle compare value of endTime
        String oldEndTime = "";
        String currentEndTime = "";
        if (oldValues.getEndTime() != null) oldEndTime = oldValues.getEndTime().toString();
        if (currentValues.getEndTime() != null) currentEndTime = currentValues.getEndTime().toString();
        //handle for case create new end time
        if ((oldEndTime == null && currentEndTime != null)
                || (oldValues.getEndTime() == null && currentValues.getEndTime() != null)) {
            map.put("endTime", getOnSaveValue("", currentEndTime));
        }
        //handle for case delete end time
        else if ((oldEndTime != null && currentEndTime == null)
                || (oldValues.getEndTime() != null && currentValues.getEndTime() == null)) {
            map.put("endTime", getOnSaveValue(oldEndTime, ""));
        }
        //both oldValues and currentValues are not null
        else if (!oldEndTime.equals(currentEndTime)
                && oldValues.getEndTime().getTime() != currentValues.getEndTime().getTime()) {
            map.put("endTime", getOnSaveValue(oldEndTime, currentEndTime));
        }

        //handle compare value of startTime
        String oldStartTime = "";
        String currentStartTime = "";
        if (oldValues.getStartTime() != null) oldStartTime = oldValues.getStartTime().toString();
        if (currentValues.getStartTime() != null) currentStartTime = currentValues.getStartTime().toString();
        //handle for case create new start time
        if ((oldStartTime == null && currentStartTime != null)
                || (oldValues.getStartTime() == null && currentValues.getStartTime() != null)) {
            map.put("startTime", getOnSaveValue("", currentStartTime));
        }
        //handle for case delete start time
        else if ((oldStartTime != null && currentStartTime == null)
                || (oldValues.getStartTime() != null && currentValues.getStartTime() == null)) {
            map.put("startTime", getOnSaveValue(oldStartTime, ""));
        }
        //both oldValues and currentValues are not null
        else if (!oldStartTime.equals(currentStartTime)
                && oldValues.getStartTime().getTime() != currentValues.getStartTime().getTime()) {
            map.put("startTime", getOnSaveValue(oldStartTime, currentStartTime));
        }

        //handle compare value of estimateHour
        String oldEstimateHour = "";
        String currentEstimateHour = "";
        if (oldValues.getEstimateHour() != null) oldEstimateHour = oldValues.getEstimateHour().toString();
        if (currentValues.getEstimateHour() != null) currentEstimateHour = currentValues.getEstimateHour().toString();
        if (oldEstimateHour != null && currentEstimateHour != null
                && oldValues.getEstimateHour() != null && currentValues.getEstimateHour() != null
                && !oldEstimateHour.equals(currentEstimateHour)) {
            map.put("estimateHour", getOnSaveValue(oldEstimateHour, currentEstimateHour));
        }

        //handle compare value of description
        String oldDescription = "";
        String currentDescription = "";
        if (oldValues.getDescription() != null) oldDescription = oldValues.getDescription();
        if (currentValues.getDescription() != null) currentDescription = currentValues.getDescription();
        if (!oldDescription.equals(currentDescription)) {
//            map.put("description", getOnSaveValue("Mô tả công việc", oldDescription, currentDescription));
//            map.put("description", currentDescription);
            map.put("description", "Mô tả công việc đã được sửa đổi");
        }

        //handle compare value of code
        String oldCode = "";
        String currentCode = "";
        if (oldValues.getCode() != null) oldCode = oldValues.getCode();
        if (currentValues.getCode() != null) currentCode = currentValues.getCode();
        if (!oldCode.equals(currentCode)) {
            map.put("code", getOnSaveValue(oldCode, currentCode));
        }

        //handle compare value of name
        String oldName = "";
        String currentName = "";
        if (oldValues.getName() != null) oldName = oldValues.getName();
        if (currentValues.getName() != null) currentName = currentValues.getName();
        if (!oldName.equals(currentName)) {
            map.put("name", getOnSaveValue(oldName, currentName));
        }

        //handle compare value of Assignee
        String oldContributorNames = "";
        String currentContributorNames = "";
        if (oldValues.getAssignee() != null) oldContributorNames = oldValues.getAssignee().getDisplayName();
        if (currentValues.getAssignee() != null) currentContributorNames = currentValues.getAssignee().getDisplayName();
        if (!oldContributorNames.equals(currentContributorNames)) {
            map.put("assignee", getOnSaveValue(oldContributorNames, currentContributorNames));
        }

        //handle compare value of Project
        String oldProject = "";
        String currentProject = "";
        if (oldValues.getProject() != null) oldProject = oldValues.getProject().getName();
        if (currentValues.getProject() != null) currentProject = currentValues.getProject().getName();
        if (!oldProject.equals(currentProject)) {
            map.put("project", getOnSaveValue(oldProject, currentProject));
        }

        //handle compare value of Activity
        String oldActivity = "";
        String currentActivity = "";
        if (oldValues.getActivity() != null) oldActivity = oldValues.getActivity().getName();
        if (currentValues.getActivity() != null) currentActivity = currentValues.getActivity().getName();
        if (!oldActivity.equals(currentActivity)) {
            map.put("activity", getOnSaveValue(oldActivity, currentActivity));
        }

        return this.convertMapToString(map);
    }

    private Object getOnSaveValue(String oldValue, String newValue) {
//        String res = "";
//        if (oldValue == "") res = title + " đã được tạo mới (" + newValue + ")";
//        else if (newValue == "") res = title + " cũ (" + oldValue + ") đã bị xóa";
//        else res = title + " thay đổi từ \"" + oldValue + "\" thành \"" + newValue + "\"";

        Map<String, Object> map = new HashMap<>();

        map.put("oldValue", oldValue);
        map.put("newValue", newValue);

        return map;
    }

    @Override
    public List<HrTaskHistoryDto> pagingHistoryOfTask(UUID taskId, SearchTaskHistoryDto searchObject) {
        if (searchObject == null) return null;

        if (taskId == null) taskId = searchObject.getTaskId();

        HrTask task = taskRepository.findById(taskId).orElse(null);
        if (task == null) return null;

        int pageIndex = searchObject.getPageIndex();
        int pageSize = searchObject.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        String sql = "select entity from HrTaskHistory as entity where (1=1) ";

        String whereClause = " and entity.task.id = :taskId";
        String orderBy = " ORDER BY entity.createDate desc";

        if (searchObject.getKeyword() != null && StringUtils.hasText(searchObject.getKeyword())) {
            whereClause += " AND ( entity.name LIKE :text OR entity.code LIKE :text or entity.description like :text or entity.modifier.displayName like :text) ";
        }
        if (searchObject.getFromDate() != null) {
            whereClause += " AND ( entity.createDate >= :fromDate ) ";
        }
        if (searchObject.getToDate() != null) {
            whereClause += " AND ( entity.createDate <= :toDate ) ";
        }
        sql += whereClause + orderBy;

        Query q = manager.createQuery(sql, HrTaskHistory.class);

        q.setParameter("taskId", task.getId());

        if (searchObject.getKeyword() != null && StringUtils.hasText(searchObject.getKeyword())) {
            q.setParameter("text", '%' + searchObject.getKeyword() + '%');
        }
        if (searchObject.getFromDate() != null) {
            q.setParameter("fromDate", searchObject.getFromDate());
        }
        if (searchObject.getToDate() != null) {
            q.setParameter("toDate", searchObject.getToDate());
        }

        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);

        List<HrTaskHistory> entities = q.getResultList();
        List<HrTaskHistoryDto> response = new ArrayList<>();

        try {
            for (HrTaskHistory entity : entities) {
                HrTaskHistoryDto responseItem = new HrTaskHistoryDto(entity);

                if (entity.getEvent() != null) {
                    //convert string back to object and attach to responseItem
                    Map<String, Object> resultMap = this.convertStringToMap(entity.getEvent());
                    responseItem.setEvent(resultMap);
                }

                responseItem.setTask(null);
                response.add(responseItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private static final String firstHistorySign = "\"isNew\":true}";

    @Override
    public HrTaskHistoryDto getFirstCreatedHistoryOfTask(UUID taskId) {
        //"isNew":true}
        //which history of task has event contains that key is the first history of task

        List<HrTaskHistory> firstHistories = taskHistoryRepository.getFirstCreatedHistoryOfTask(taskId, firstHistorySign);

        if (firstHistories == null || firstHistories.size() == 0) return null;

        return new HrTaskHistoryDto(firstHistories.get(0));
    }



    @Override
    public List<HrTaskHistory> findHistoryOfStaffInRangeTime(UUID staffId, Date fromDate, Date toDate) {
        if (staffId == null || fromDate == null || toDate == null)
            return null;

        String hql = "SELECT distinct history from HrTaskHistory history  " +
                "where history.modifier.id = :staffId and " +
                "(DATE(history.createDate) >= DATE(:fromDate) and DATE(history.createDate) <= DATE(:toDate)) ";

//        +"and history.event like '%" + STATUS_SIGN + "%' and " +
//                "(history.event like '%" + RESOLVED_STATUS + "%' or history.event like '%" + COMPLETED_STATUS + "%')";

        Query query = manager.createQuery(hql, KanbanDto.class);
        query.setParameter("staffId", staffId);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        List<HrTaskHistory> result = query.getResultList();
        return result;
    }
}
