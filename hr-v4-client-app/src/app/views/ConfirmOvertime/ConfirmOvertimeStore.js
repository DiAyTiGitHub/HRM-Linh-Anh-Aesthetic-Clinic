import { makeAutoObservable } from "mobx";

import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import { StaffWorkSchedule } from "app/common/Model/Timekeeping/StaffWorkSchedule";
import {
    getStaffWorkSchedule,
    pagingWorkScheduleResult,
    updateScheduleOTHours,
} from "../StaffWorkScheduleV2/StaffWorkScheduleService";
import { SearchStaffWorkSchedule } from "app/common/Model/SearchObject/SearchStaffWorkSchedule";
import LocalConstants from "app/LocalConstants";
import { getByKey } from "../SystemConfig/SystemConfigService";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class ConfirmOvertimeStore {
    intactSearchObject = {
        ... new SearchStaffWorkSchedule(),
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    totalElements = 0;
    totalPages = 0;
    listStaffWorkSchedules = [];
    openCreateEditPopup = false;
    selectedStaffWorkSchedule = null;

    listOnDelete = [];
    openViewPopup = false;
    minOTMinutes = 0;

    getMinOTMinutes = async () => {
        try {
            const { data } = await getByKey(LocalConstants.SystemConfigCode.MIN_OT_MINUTES_TO_SHOW_CONFIRM.code);

            if (data) {
                this.minOTMinutes = data?.configValue;
            }

        } catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra khi lấy cấu hình hệ thống");
        }
    }

    handleOpenView = async (staffWorkScheduleId) => {
        try {
            if (staffWorkScheduleId) {
                const { data } = await getStaffWorkSchedule(staffWorkScheduleId);
                this.selectedStaffWorkSchedule = {
                    ...JSON.parse(JSON.stringify(data))
                };
            } else {
                this.selectedStaffWorkSchedule = {
                    ... new StaffWorkSchedule()
                };
            }
            this.openViewPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.listStaffWorkSchedules = [];
        this.openCreateEditPopup = false;
        this.selectedStaffWorkSchedule = null;
        this.listOnDelete = [];
        this.openViewPopup = false;
    }

    //lọc theo trạng thái làm việc = thay đổi tab
    handleChangeWorkingStatus = (status) => {
        const so = { ... this.searchObject, workingStatus: status };
        this.searchObject = so;
    }

    handleSetSearchObject = (searchObject) => {
        if (searchObject.shiftWork == null) {
            searchObject.shiftWorkId = null;
        } else {
            searchObject.shiftWorkId = searchObject.shiftWork.id;
        }

        if (searchObject.department == null) {
            searchObject.departmentId = null;
        } else {
            searchObject.departmentId = searchObject.department.id;
        }

        if (searchObject.staff == null) {
            searchObject.staffId = null;
        } else {
            searchObject.staffId = searchObject.staff.id;
        }

        if (searchObject.organization == null) {
            searchObject.organizationId = null;
        } else {
            searchObject.organizationId = searchObject.organization.id;
        }

        if (searchObject.position == null) {
            searchObject.positionId = null;
        } else {
            searchObject.positionId = searchObject.position.id;
        }

        this.searchObject = { ...searchObject };
    }

    mapTabToWorkingStatus = (tab) => {
        // tab 0 => Tất cả
        if (tab == 0) return null;
        // tab 1 => Đi làm đủ
        if (tab == 1) return LocalConstants.StaffWorkScheduleWorkingStatus.FULL_ATTENDANCE.value;
        // tab 2 => Đi thiếu giờ
        if (tab == 2) return LocalConstants.StaffWorkScheduleWorkingStatus.PARTIAL_ATTENDANCE.value;
        // tab 3 => Nghỉ có phép
        if (tab == 3) return LocalConstants.StaffWorkScheduleWorkingStatus.LEAVE_WITH_PERMISSION.value;
        // tab 4 => Nghỉ không phép
        if (tab == 4) return LocalConstants.StaffWorkScheduleWorkingStatus.LEAVE_WITHOUT_PERMISSION.value;
        // if (tab == 5) return LocalConstants.StaffWorkScheduleWorkingStatus.LATE_FOR_WORK.value;

        return null;
    }

    pagingWorkScheduleResult = async () => {
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ... this.searchObject,
                workingStatus: this.mapTabToWorkingStatus(this?.searchObject?.workingStatus),
                // organizationId: loggedInStaff?.user?.org?.id
            };

            const data = await pagingWorkScheduleResult(payload);

            this.listStaffWorkSchedules = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingWorkScheduleResult();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingWorkScheduleResult();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteStaffWorkSchedules) => {
        this.listOnDelete = deleteStaffWorkSchedules;
    };

    getStaffWorkSchedule = async (staffWorkScheduleId) => {
        try {
            const { data } = await getStaffWorkSchedule(staffWorkScheduleId);
            this.selectedStaffWorkSchedule = data;
            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleClose = () => {
        this.openCreateEditPopup = false;
        this.listOnDelete = [];
        this.openViewPopup = false;

    };

    handleOpenCreateEdit = async (staffWorkScheduleId) => {
        try {
            if (staffWorkScheduleId) {
                const { data } = await getStaffWorkSchedule(staffWorkScheduleId);
                this.selectedStaffWorkSchedule = {
                    ...JSON.parse(JSON.stringify(data))
                };
            } else {
                this.selectedStaffWorkSchedule = {
                    ... new StaffWorkSchedule()
                };
            }

            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    getCheckInAndCheckOutTimeOfShiftWork = (shiftWork) => {
        if (!shiftWork || !shiftWork.timePeriods || shiftWork.timePeriods.length === 0) {
            return { checkInTime: null, checkOutTime: null };
        }

        // Sort timePeriods by startTime to find the earliest and latest
        const sortedPeriods = shiftWork.timePeriods.sort((a, b) =>
            new Date(a.startTime) - new Date(b.startTime)
        );

        return {
            checkInTime: sortedPeriods[0].startTime, // Earliest start time
            checkOutTime: sortedPeriods[sortedPeriods.length - 1].endTime // Latest end time
        };
    }


    updateScheduleOTHours = async (staffWorkSchedule) => {
        try {
            const { data } = await updateScheduleOTHours(staffWorkSchedule);

            toast.success("Đã xác nhận số giờ làm thêm cho ca làm việc");

            this.pagingWorkScheduleResult();

            this.handleClose();

            return data;

        } catch (error) {
            console.error(error);
            // if (error.response.status == 409) {
            //     toast.error("Mã thành phần đã được sử dụng, vui lòng sử dụng mã thành phần khác", {
            //         autoClose: 5000,
            //         draggable: false,
            //         limit: 5,
            //     });
            // }
            // else if (error.response.status == 304) {
            //     toast.warning("Thành phần mặc định của hệ thống không được phép chỉnh sửa", {
            //         autoClose: 5000,
            //         draggable: false,
            //         limit: 5,
            //     });
            // }
            // else {
            toast.error(i18n.t("toast.error"));
            // }

            throw new Error(i18n.t("toast.error"));
        }
    };


}
