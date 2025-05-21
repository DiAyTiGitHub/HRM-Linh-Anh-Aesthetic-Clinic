package com.globits.hr.dto.comparator;

import com.globits.task.dto.HrSubTaskItemDto;
import java.util.Comparator;

public class HrSubTaskItemComparator implements Comparator<HrSubTaskItemDto> {
    @Override
    public int compare(HrSubTaskItemDto item1, HrSubTaskItemDto item2) {
        // First, compare by value field
        boolean value1 = item1.isValue();
        boolean value2 = item2.isValue();

        // If values are different, prioritize false over true
        if (value1 != value2) {
            return value1 ? 1 : -1; // false comes before true
        }

        // If values are the same, then compare by name field
        String name1 = item1.getName();
        String name2 = item2.getName();

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