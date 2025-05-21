package com.globits.hr.dto.comparator;

import com.globits.task.dto.HrSubTaskDto;

import java.util.Comparator;

public class HrSubTaskComparator implements Comparator<HrSubTaskDto> {
    @Override
    public int compare(HrSubTaskDto subTask1, HrSubTaskDto subTask2) {
        // Handle null cases
        if (subTask1 == null && subTask2 == null) {
            return 0;
        } else if (subTask1 == null) {
            return -1;
        } else if (subTask2 == null) {
            return 1;
        }

        // Compare by name field
        String name1 = subTask1.getName();
        String name2 = subTask2.getName();

        // Handle null cases for name field
        if (name1 == null && name2 == null) {
            return 0;
        } else if (name1 == null) {
            return -1;
        } else if (name2 == null) {
            return 1;
        }

        return name1.compareTo(name2);
    }
}
