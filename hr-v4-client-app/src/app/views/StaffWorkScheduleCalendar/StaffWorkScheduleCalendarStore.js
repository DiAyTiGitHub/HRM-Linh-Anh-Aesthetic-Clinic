import { SearchStaffWorkSchedule } from "app/common/Model/SearchObject/SearchStaffWorkSchedule";
import { ScheduledStaffCalendar } from "app/common/Model/Timekeeping/ScheduledStaffCalendar";
import localStorageService from "app/services/localStorageService";
import i18n from "i18n";
import { makeAutoObservable } from "mobx";
import { toast } from "react-toastify";
import LocalConstants from "../../LocalConstants";
import { getWorkCalendarOfStaff, getWorkingScheduleByFilter } from "./StaffWorkScheduleCalendarService";

export default class StaffWorkScheduleCalendarStore {
    intactSearchObject = {
        ... new SearchStaffWorkSchedule(),
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    listWorkSchedules = [];
    pageIndex = 1;
    pageSize = 10;
    scheduledStaffCalendar = new ScheduledStaffCalendar();

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;

        this.listWorkSchedules = [];
        this.scheduledStaffCalendar = new ScheduledStaffCalendar();

    }

    handleSetSearchObject = (searchObject) => {
        const mappings = {
            department: "departmentId",
            staff: "staffId",
            organization: "organizationId",
            position: "positionId",
            positionTitle: "positionTitleId"
        };

        Object.keys(mappings).forEach(key => {
            searchObject[mappings[key]] = searchObject[key]?.id ?? null;
        });

        this.searchObject = { ...searchObject };
    };

    setPageSize = (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        this.getWorkingScheduleByFilter();
    };

    handleChangePage = (event, newPage) => {
        this.setPage(newPage);
    };

    setPage = (page) => {
        this.searchObject.pageIndex = page;
        this.getWorkingScheduleByFilter();
    };


    getWorkingScheduleByFilter = async () => {
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ... this.searchObject, // organizationId: loggedInStaff?.user?.org?.id
            };
            this.handleSetSearchObject(payload);
            const { data } = await getWorkingScheduleByFilter(payload);

            this.listWorkSchedules = data.content;
            this.totalElements = data.totalElements;
            this.totalPages = data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    getWorkCalendarOfStaff = async () => {
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ... this.searchObject, // organizationId: loggedInStaff?.user?.org?.id
            };

            this.scheduledStaffCalendar = null;

            if (payload?.staffId) {
                const { data } = await getWorkCalendarOfStaff(payload);
                this.scheduledStaffCalendar = data;
            }

            if (this.scheduledStaffCalendar == null) {
                this.scheduledStaffCalendar = new ScheduledStaffCalendar();
            }

            // this.listWorkSchedules = data.data.content;
            // this.totalElements = data.data.totalElements;
            // this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };


    // getNeedRenderDates = () => {
    //   const start = new Date(this?.searchObject?.fromDate);
    //   const end = new Date(this?.searchObject?.toDate);

    //   // Ensure we remove time offsets by forcing the date to be in UTC
    //   start.setUTCHours(0, 0, 0, 0);
    //   end.setUTCHours(0, 0, 0, 0);

    //   const dateArray = [];

    //   while (start <= end) {
    //     dateArray.push(start.toISOString().split('T')[0]);
    //     start.setUTCDate(start.getUTCDate() + 1); // Use setUTCDate instead of setDate
    //   }

    //   return dateArray;
    // };

    getNeedRenderDates = () => {
        const fromDate = this?.searchObject?.fromDate;
        const toDate = this?.searchObject?.toDate;

        const start = new Date(fromDate);
        const end = new Date(toDate);
        const dateArray = [];

        if (isNaN(start) || isNaN(end) || start > end) {
            console.error("Invalid date range");
            return [];
        }

        while (start <= end) {
            dateArray.push(new Date(start)); // Store as Date object
            start.setDate(start.getDate() + 1);
        }

        return dateArray;
    };


    // logic
    // TH1: workingDate > currentDate
    // Chưa đến lịch làm việc

    // TH2: workingDate <= currentDate
    // TH2.1: timesheetDetails is empty
    // Chưa chấm công
    // TH2.2: timesheetDetails is not empty
    // Đã chấm công
    // + Checkin (startTime) | Checkout (endTime)

    getShiftWorkStatus = (workingDate, timesheetDetails, workingStatus, leaveType = null) => {
        //Nghỉ phép có lương
        if (leaveType?.isPaid) {
            return 6;
        }
        //Nghỉ phép không lương
        if (leaveType != null && !leaveType?.isPaid) {
            return 5;
        }
        const currentDate = new Date();
        currentDate.setHours(0, 0, 0, 0);

        const targetDate = new Date(workingDate);
        targetDate.setHours(0, 0, 0, 0);

        if (targetDate.getTime() > currentDate.getTime()) {
            // console.log("Returning 1: Chưa đến lịch làm việc");
            return 1;
        } else if (targetDate.getTime() <= currentDate.getTime()) {
            if (!timesheetDetails || timesheetDetails.length === 0 || workingStatus === LocalConstants.StaffWorkScheduleWorkingStatusFull.NOT_ATTENDANCE.value) {
                // console.log("Returning 2: Chưa chấm công");
                return 2;
            }
            // console.log("Returning 3: Đã chấm công");
            if (workingStatus === LocalConstants.StaffWorkScheduleWorkingStatusFull.FULL_ATTENDANCE.value) return 3;
            if (workingStatus === LocalConstants.StaffWorkScheduleWorkingStatusFull.PARTIAL_ATTENDANCE.value) return 4;
        }
    };

    // 1. Chưa đến lịch làm việc
    // 2. Chưa chấm công
    // 3. Đã chấm công
    // 4. Đi làm thiếu giờ
    // 5. Nghỉ phép không lương
    // 6. Nghỉ phép có lương
    mapTicketStatusToText = (status) => {
        const statusMap = {
            1: "Chưa đến lịch làm việc",
            2: "Chưa chấm công",
            3: "Đã chấm công",
            4: "Đi làm thiếu giờ",
            5: "Nghỉ phép không lương",
            6: "Nghỉ phép có lương"
        };
        return statusMap[status] || "Trạng thái không xác định";
    };

    mapStatusToColor = (status) => {
        const colorMap = {
            1: "#2a80c8",
            2: "#D32F2F",
            3: "#338157",
            4: "#f1bf3f",
            5: "#9C27B0",
            6: "#338157",
        };
        return colorMap[status] || "#000000";
    };

    mapStatusToBackgroundColor = (status) => {
        const colorMap = {
            1: "#d4e6f5",
            2: "#FFCDD2",
            3: "#d1e9dd",
            4: "#f5f3d4",
            5: "#E1BEE7",
            6: "#d1e9dd",
        };
        return colorMap[status] || "#ffffff";
    };
}

