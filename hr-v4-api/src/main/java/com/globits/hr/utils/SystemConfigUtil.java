package com.globits.hr.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SystemConfigUtil {
    public static final int MAX_ENTRIES = 1;

    // Mỗi prefix có một LinkedHashMap riêng
    public static final Map<String, LinkedHashMap<String, String>> prefixMap = new HashMap<>();

    // Đối tượng khóa theo prefix
    private static final Map<String, Object> prefixLocks = new HashMap<>();

    // Tạo hoặc lấy map theo prefix
    private static LinkedHashMap<String, String> getOrCreateMap(String prefix) {
        return prefixMap.computeIfAbsent(prefix, k -> new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > MAX_ENTRIES;
            }
        });
    }

    // Lấy khóa theo prefix (đảm bảo duy nhất cho mỗi prefix)
    private static synchronized Object getLockForPrefix(String prefix) {
        return prefixLocks.computeIfAbsent(prefix, k -> new Object());
    }

    // Đặt giá trị
    public static void put(String prefix, String key, String value) {
        synchronized (getLockForPrefix(prefix)) {
            getOrCreateMap(prefix).put(key, value);
        }
    }

    // Kiểm tra khóa tồn tại
    public static boolean hasKey(String prefix, String key) {
        synchronized (getLockForPrefix(prefix)) {
            LinkedHashMap<String, String> map = prefixMap.get(prefix);
            return map != null && map.containsKey(key);
        }
    }

    // Xoá khóa
    public static void removeKey(String prefix, String key) {
        synchronized (getLockForPrefix(prefix)) {
            LinkedHashMap<String, String> map = prefixMap.get(prefix);
            if (map != null) {
                map.remove(key);
            }
        }
    }

    // Lấy giá trị
    public static String get(String prefix, String key) {
        synchronized (getLockForPrefix(prefix)) {
            LinkedHashMap<String, String> map = prefixMap.get(prefix);
            return map != null ? map.get(key) : null;
        }
    }

    // Ví dụ hàm sinh mã tự động an toàn theo prefix
    public static String generateNextCode(String prefix) {
        synchronized (getLockForPrefix(prefix)) {
            String maxCode = get(prefix, "lastCode");
            int nextNumber = 1;
            if (maxCode != null) {
                String numericPart = maxCode.substring(prefix.length() + 1);
                if (numericPart.matches("\\d+")) {
                    nextNumber = Integer.parseInt(numericPart) + 1;
                }
            }
            String nextCode = prefix + "-" + String.format("%03d", nextNumber);
            put(prefix, "lastCode", nextCode);
            return nextCode;
        }
    }
}
