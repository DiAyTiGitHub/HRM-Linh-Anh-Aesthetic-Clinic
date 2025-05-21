package com.globits.hr.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class DateTimeUtil {

    public static Date setTime(final Date date, final int hourOfDay, final int minute, final int second, final int ms) {
        final GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.set(Calendar.HOUR_OF_DAY, hourOfDay);
        gc.set(Calendar.MINUTE, minute);
        gc.set(Calendar.SECOND, second);
        gc.set(Calendar.MILLISECOND, ms);
        return gc.getTime();
    }

    public static Date getDateAfterSixMonths(Date inputDate) {
        // Chuyển Date -> LocalDate
        LocalDate localDate = inputDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Cộng 6 tháng
        LocalDate afterSixMonths = localDate.plusMonths(6);

        // Chuyển LocalDate -> Date
        return Date.from(afterSixMonths.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }


    public static Date setTimeToDate(Date time, Date date) {
        final GregorianCalendar gc = new GregorianCalendar();
        int hourOfDay = time.getHours();
        int minute = time.getMinutes();
        int second = time.getSeconds();
        gc.setTime(date);
        gc.set(Calendar.HOUR_OF_DAY, hourOfDay);
        gc.set(Calendar.MINUTE, minute);
        gc.set(Calendar.SECOND, second);
        return gc.getTime();
    }

    public static int getYear(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    public static int getMonth(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        return month;
    }

    public static int getDay(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    public static int getHours(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public static int getMinutes(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int minute = calendar.get(Calendar.MINUTE);
        return minute;
    }

    public static String getHourMinutes(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

    public static double hoursDifference(Date date1, Date date2) {
        final int MILLI_TO_HOUR = 1000 * 60 * 60;
        return (double) (date2.getTime() - date1.getTime()) / MILLI_TO_HOUR;
    }

    public static int lastDateOfMonth(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        return cal.getActualMaximum(Calendar.DATE);
    }

    public static int numberWeekOfMonth(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        if (cal.getActualMaximum(Calendar.DATE) <= 28) {
            return 4;
        }
        return 5;
    }

    public static List<Date> getDateToDateOfWeek(int week, int month, int year) {
        List<Date> dateList = new ArrayList<>();
        int start = 0;
        int end = 0;
        if (week == 1) {
            start = 1;
            end = 7;
        }
        if (week == 2) {
            start = 8;
            end = 14;
        }
        if (week == 3) {
            start = 15;
            end = 21;
        }
        if (week == 4) {
            start = 22;
            end = 28;
        }
        if (week == 5) {
            start = 29;
            end = lastDateOfMonth(month, year);
        }
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, start, 0, 0, 0);
        Date startDate = cal.getTime();
        dateList.add(startDate);
        cal.set(year, month - 1, end, 23, 59, 59);
        Date endDate = cal.getTime();
        dateList.add(endDate);
        return dateList;
    }

    public static Date numberToDate(int day, int month, int year) throws ParseException {
        String dateString = String.format("%02d", day) + "/" + String.format("%02d", month) + "/" + year;
        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);

        return date;
    }

    public static Date getLastDayOfMonth(int month, int year) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date = "01/" + String.format("%02d", month) + "/" + year;
        LocalDate localDate = LocalDate.parse(date, formatter);
        LocalDate lastDay = localDate.with(TemporalAdjusters.lastDayOfMonth());

        Date lastDayOfMonth = Date.from(lastDay.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastDayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    public static List<LocalDate> getListMonthByMonthYear(int fromMonth, int fromyear, int toMonth, int toYear) {
        List<int[]> ret = new ArrayList<int[]>();
        List<LocalDate> retCalendar = new ArrayList<LocalDate>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate beginCalendar = LocalDate.parse("01/" + String.format("%02d", fromMonth) + "/" + fromyear, formatter);
        LocalDate finishCalendar = LocalDate.parse(getLastDayOfMonth(toMonth, toYear).getDate() + "/" + String.format("%02d", toMonth) + "/" + toYear, formatter);

        while (beginCalendar.isBefore(finishCalendar)) {
            retCalendar.add(beginCalendar);
            beginCalendar = beginCalendar.plusMonths(1L);
        }
        return retCalendar;
    }

    public static Date getEndOfDay(Date date) {
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            return calendar.getTime();
        }
        return null;
    }

    public static Date getStartOfDay(Date date) {
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 00);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 00);
            calendar.set(Calendar.MILLISECOND, 000);
            return calendar.getTime();
        }
        return null;
    }

    public static Date setHourAndMinute(Date date, int hour, int minute) {
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            return calendar.getTime();
        }
        return null;
    }

    public static LocalDateTime getStartOfDayLocal(LocalDateTime date) {
        if (date != null) {
            date = date.withHour(0).withMinute(0).withSecond(0).withNano(0);
            return date;
        }
        return null;
    }

    public static LocalDateTime getEndOfDayLocal(LocalDateTime date) {
        if (date != null) {
            date = date.withHour(23).withMinute(59).withSecond(59).withNano(99999);
            return date;
        }
        return null;
    }

    public static Date getPrevDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.MILLISECOND, 000);
        return calendar.getTime();
    }

    public static Date getFirstDayOfNextMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);         // về ngày đầu tháng hiện tại
        cal.add(Calendar.MONTH, 1);                // sang tháng tiếp theo
        cal.set(Calendar.HOUR_OF_DAY, 0);          // reset giờ/phút/giây
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getLastDayOfNextMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);         // về ngày đầu tháng hiện tại
        cal.add(Calendar.MONTH, 2);                // sang tháng kế tiếp của tháng kế tiếp
        cal.set(Calendar.DAY_OF_MONTH, 1);         // ngày đầu tháng sau kế tiếp
        cal.add(Calendar.DATE, -1);                // lùi lại 1 ngày để được cuối tháng kế tiếp
        cal.set(Calendar.HOUR_OF_DAY, 23);         // set giờ cuối ngày
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }


    // month from 0-> 11
    public static List<Date> getDatesInMonthJava7(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        int date = 1;
        calendar.set(year, month, date);
        Date startDate = calendar.getTime();

        List<Date> datesInRange = new ArrayList<>();
        calendar.setTime(startDate);

        Calendar endCalendar = Calendar.getInstance();
        int maxDay = 0;
        maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        endCalendar.set(year, month, maxDay);
        endCalendar.add(Calendar.DATE, 1);
        Date endDate = endCalendar.getTime();
        endCalendar.setTime(endDate);

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }

    // month from 0-> 11
    public static List<Date> getDatesByYearMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        int date = 1;
        calendar.set(year, month, date);
        Date startDate = calendar.getTime();

        List<Date> datesInRange = new ArrayList<>();
        calendar.setTime(startDate);

        Calendar endCalendar = Calendar.getInstance();
        int maxDay = 0;
        maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yyyy");
        if (simpleDateFormat.format(new Date()).equals(simpleDateFormat.format(calendar.getTime()))) {
            endCalendar = Calendar.getInstance();
        } else {
            endCalendar.set(year, month, maxDay);
        }
        endCalendar.add(Calendar.DATE, 1);
        Date endDate = endCalendar.getTime();
        endCalendar.setTime(endDate);

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }

    public static boolean checkQuarter(Date date) throws ParseException {
        LocalDateTime currentDate = LocalDateTime.now();
        Date now = new Date();
        boolean isUpdate = false;
        Date startOfJanuary = numberToDate(1, 1, currentDate.getYear());// ngày 1 tháng 1
        startOfJanuary = getStartOfDay(startOfJanuary);

        Date endOfMarch = getLastDayOfMonth(3, currentDate.getYear());//ngày 31 tháng 3

        Date startOfApril = numberToDate(1, 4, currentDate.getYear());//ngày 1 tháng 4
        startOfApril = getStartOfDay(startOfApril);

        Date endOfJune = getLastDayOfMonth(6, currentDate.getYear());//ngày 30 tháng 6

        Date startOfJuly = numberToDate(1, 7, currentDate.getYear());//ngày 1 tháng 7
        startOfJuly = getStartOfDay(startOfJuly);

        Date endOfSeptember = getLastDayOfMonth(9, currentDate.getYear());// ngày 30 tháng 9

        Date startOfOctober = numberToDate(1, 10, currentDate.getYear());// ngày 1 tháng 10
        startOfOctober = getStartOfDay(startOfOctober);

        Date endOfDecember = getLastDayOfMonth(12, currentDate.getYear());// ngày 30 tháng 12

        if (now.after(startOfJanuary) && now.before(endOfMarch) && date.after(startOfJanuary) && date.before(endOfMarch)) {
            isUpdate = true;
        }
        if (now.after(startOfApril) && now.before(endOfJune) && date.after(startOfApril) && date.before(endOfJune)) {
            isUpdate = true;
        }
        if (now.after(startOfJuly) && now.before(endOfSeptember) && date.after(startOfJuly) && date.before(endOfSeptember)) {
            isUpdate = true;
        }
        if (now.after(startOfOctober) && now.before(endOfDecember) && date.after(startOfOctober) && date.before(endOfDecember)) {
            isUpdate = true;
        }
        return isUpdate;
    }

    public static boolean checkEditableByMonth(int numberMonth, java.time.LocalDateTime date) {
        if (date == null) {
            return false;
        }
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return !date.isBefore(now.minusMonths(numberMonth));
    }

    public static List<Date> getDaysBetweenDates(Date startDate, Date endDate) {
        List<Date> dates = new ArrayList<Date>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        while (calendar.getTime().before(endDate)) {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }


    public static int countWeekdayInRange(Integer weekDay, Date fromDate, Date toDate) {
        if (weekDay == null || fromDate == null || toDate == null) {
            throw new IllegalArgumentException("Input parameters must not be null.");
        }

        if (fromDate.after(toDate)) {
            throw new IllegalArgumentException("fromDate must not be after toDate.");
        }

        int calendarDayOfWeek = mapWeekdayToCalendarDay(weekDay);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);

        int count = 0;

        while (!calendar.getTime().after(toDate)) {
            if (calendar.get(Calendar.DAY_OF_WEEK) == calendarDayOfWeek) {
                count++;
            }
            calendar.add(Calendar.DATE, 1);
        }

        return count;
    }

    private static int mapWeekdayToCalendarDay(int weekDay) {
        // WeekDays: MON=2,...,SAT=7,SUN=8 → Calendar: MON=2,...,SAT=7,SUN=1
        if (weekDay < 2 || weekDay > 8) {
            throw new IllegalArgumentException("Invalid weekDay value: " + weekDay);
        }
        return (weekDay == 8) ? Calendar.SUNDAY : weekDay;
    }


    public static List<Date> getListOfDaysBetweenTwoDates(Date startDate, Date endDate) {
        List<Date> result = new ArrayList<Date>();
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        end.add(Calendar.DAY_OF_YEAR, 1); //Add 1 day to endDate to make sure endDate is included into the final list
        while (start.before(end)) {
            result.add(start.getTime());
            start.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

    public static String customFormatDate(Date inputDate, String format) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.format(inputDate);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static LocalDate convertDateToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static boolean isContainsDate(Set<Date> holidayDates, Date onLoopDate) {
        if (onLoopDate == null || holidayDates == null) return false;

        LocalDate loopDate = convertDateToLocalDate(onLoopDate);

        return holidayDates.stream()
                .map(DateTimeUtil::convertDateToLocalDate)
                .anyMatch(date -> date.equals(loopDate));
    }

}
