package com.globits.timesheet.dto;

import java.util.*;

import com.globits.timesheet.domain.TimeSheetDetail;

public class TimesheetDetailDescriptor {
    //map timesheet detail - its contents (saved in description field) (1 - n)
    private Map<TimeSheetDetail, Set<String>> timesheetDetailContent;

    public TimesheetDetailDescriptor() {
        timesheetDetailContent = new HashMap<>();
    }

    public boolean containsTimesheetDetail(TimeSheetDetail timesheetDetail) {
        if (this.timesheetDetailContent.containsKey(timesheetDetail)) return true;
        return false;
    }

    public void addDetailContent(TimeSheetDetail timesheetDetail, String content) {
        if (!timesheetDetailContent.containsKey(timesheetDetail))
            timesheetDetailContent.put(timesheetDetail, new HashSet<>());
        timesheetDetailContent.get(timesheetDetail).add(content);
    }

    public Set<TimeSheetDetail> getNeedCreateUpdateTimesheetDetails() {
        return timesheetDetailContent.keySet();
    }

    public List<String> getContentsOfTimesheetDetail(TimeSheetDetail timesheetDetail) {
        if (!containsTimesheetDetail(timesheetDetail)) return null;
        List<String> contents = new ArrayList<>(timesheetDetailContent.get(timesheetDetail));
        Collections.sort(contents);
        return contents;
    }

    public String getBuiltContent(TimeSheetDetail timesheetDetail) {
        String res = "";
        res += "<ol>";
        List<String> contents = this.getContentsOfTimesheetDetail(timesheetDetail);
        if (contents != null && contents.size() > 0)
            for (String content : contents) {
                res += "<li>" + content + "</li>";
            }
        res += "</ol>";
        return res;
    }

    public Map<TimeSheetDetail, Set<String>> getTimesheetDetailContent() {
        return timesheetDetailContent;
    }

    public void setTimesheetDetailContent(Map<TimeSheetDetail, Set<String>> timesheetDetailContent) {
        this.timesheetDetailContent = timesheetDetailContent;
    }
}
