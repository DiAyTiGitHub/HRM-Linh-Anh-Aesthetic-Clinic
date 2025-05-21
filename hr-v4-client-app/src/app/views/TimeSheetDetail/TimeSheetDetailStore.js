import { makeAutoObservable } from "mobx";
import {
    pagingTimeSheetDetail,
    getTimeSheetDetailById,
    deleteTimeSheetDetail,
    importTimeSheetDetail,
    downloadTimesheetDetailTemplate,
    saveOrUpdateTimeSheetDetail,
    deleteMultiple,
    importDataWithSystemTemplate,
    exportDataWithSystemTemplate,
    exportExcelLATimekeepingData,
} from "./TimeSheetDetailService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import { saveAs } from "file-saver";
import { getCurrentTimekeepingData, getTimeSheetByDate, saveTimeSheet } from "../TimeKeeping/TimeKeepService";
import { getStaff } from "../HumanResourcesInformation/StaffService";
import { getCurrentStaff } from "../profile/ProfileService";
import { TimesheetDetail } from "app/common/Model/Timekeeping/TimesheetDetail";
import { TimesheetStaff } from "app/common/Model/Timekeeping/TimesheetStaff";
import { SearchTimesheet } from "app/common/Model/SearchObject/SearchTimesheet";
import { SalaryItem } from "../../common/Model/Salary/SalaryItem";
import moment from "moment";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class TimeSheetDetailStore {
    intactSearchObject = {
        ...new SearchTimesheet(),
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    initialTimeSheetDetail = {
        ...new TimesheetDetail(),
    };

    timeSheetDetailList = [];
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
    loadingInitial = false;

    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    openCreateEditPopup = false;
    openTimeSheetDetailCUForm = false;
    listChosen = [];
    selectedTimeSheetDetail = null;
    selectedTimeSheetDetailList = [];
    openFormTimeSheetDetailCheck = false;
    selectedStaff = null;

    currentTimekeeping = null;

    openViewPopup = false;

    handleOpenView = async (timeSheetDetailId) => {
        try {
            if (timeSheetDetailId) {
                const { data } = await getTimeSheetDetailById(timeSheetDetailId);
                this.selectedTimeSheetDetail = data;
            } else {
                this.selectedTimeSheetDetail = {
                    ...this.initialTimeSheetDetail,
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
        this.timeSheetDetailList = [];
        this.openCreateEditPopup = false;
        this.openTimeSheetDetailCUForm = false;
        this.selectedTimeSheetDetail = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.listChosen = [];
        this.currentTimekeeping = null;
        this.openExportLATimekeepingDataPopup = false;
    };

    getStaff = async (id) => {
        try {
            let data = await getStaff(id);
            this.handleSelectStaff(data.data);
            this.searchObject = {
                ...this.searchObject,
                staff: data.data,
            };
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.get_fail"));
            this.handleSelectStaff(null);
        }
    };

    getCurrentStaff = async () => {
        try {
            let data = await getCurrentStaff();
            this.handleSelectStaff(data?.data);
            this.searchObject = {
                ...this.searchObject,
                staff: data.data,
            };
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.get_fail"));
            this.handleSelectStaff(null);
        }
    };

    handleSelectStaff(timesheetStaff) {
        this.selectedStaff = timesheetStaff;
    }

    handleOpenFormTimeSheetDetailCheck = async () => {
        try {
            const { data } = await getCurrentTimekeepingData();
            if (data?.errorMessage) {
                toast.error(data?.errorMessage, {
                    autoClose: 5555,
                });
                return;
            }
            this.currentTimekeeping = data;
            if (!this.currentTimekeeping) {
                this.currentTimekeeping = new TimesheetStaff();
            }
            this.openFormTimeSheetDetailCheck = true;
        } catch (error) {
            console.log(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleSaveTimeSheet = async (data) => {
        try {
            const response = await saveTimeSheet({
                ...data,
                staffId: data?.staff?.id,
            });
            toast.success(response?.data, {
                autoClose: 5555,
            });
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau");
        }
    };

    pagingTimeSheetDetail = async () => {
        try {
            this.handleSetSearchObject(this.searchObject);
            const payload = {
                ...this.searchObject,
            };
            // Kiểm tra các object và thêm trường ID tương ứng nếu có
            if (payload.timesheet && payload.timesheet.id) {
                payload.timesheetId = payload.timesheet.id;
            }
            if (payload.staff && payload.staff.id) {
                payload.staffId = payload.staff.id;
            }
            if (payload.shiftWork && payload.shiftWork.id) {
                payload.shiftWorkId = payload.shiftWork.id;
            }

            const data = await pagingTimeSheetDetail(payload);
            this.timeSheetDetailList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.pagingTimeSheetDetail();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.pagingTimeSheetDetail();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleOpenCreateEdit = async (timeSheetDetailId) => {
        try {
            if (timeSheetDetailId) {
                const { data } = await getTimeSheetDetailById(timeSheetDetailId);
                this.selectedTimeSheetDetail = data;
            } else {
                this.selectedTimeSheetDetail = {
                    ...this.initialTimeSheetDetail,
                };
            }
            //this.openCreateEditPopup = true;
            this.openTimeSheetDetailCUForm = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleOpenCreateEditInStaffWorkSchedule = (data) => {
        this.selectedTimeSheetDetail = data;
        this.openTimeSheetDetailCUForm = true;
    };

    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;
        this.openConfirmDeleteListPopup = false;
        this.openFormTimeSheetDetailCheck = false;
        this.openTimeSheetDetailCUForm = false;
        this.listChosen = [];
        this.openViewPopup = false;
        this.openExportLATimekeepingDataPopup = false;
    };

    handleDelete = (timeSheetDetail) => {
        this.selectedTimeSheetDetail = { ...timeSheetDetail };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleConfirmDelete = async () => {
        try {
            const { data } = await deleteTimeSheetDetail(this.selectedTimeSheetDetail.id);
            toast.success(i18n.t("toast.delete_success"));
            await this.pagingTimeSheetDetail();
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        try {
            const deleteData = [];

            for (let i = 0; i < this?.listChosen?.length; i++) {
                deleteData.push(this?.listChosen[i]?.id);
            }

            await deleteMultiple(deleteData);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingTimeSheetDetail();
            this.listChosen = [];

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleSelectListDelete = (timeSheetDetails) => {
        this.listChosen = timeSheetDetails;
    };

    handleRemoveActionItem = (onRemoveId) => {
        this.listChosen = this?.listChosen?.filter((item) => item?.id !== onRemoveId);
    };

    saveTimeSheetDetail = async (timeSheetDetail) => {
        try {
            const { data } = await saveOrUpdateTimeSheetDetail(timeSheetDetail);
            toast.success("Thông tin chấm công đã được lưu");
            this.pagingTimeSheetDetail();
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
            throw new Error(i18n.t("toast.error"));
        }
    };

    getTimeSheetDetail = async (id) => {
        if (id != null) {
            try {
                const { data } = await getTimeSheetDetailById(id);
                this.selectedTimeSheetDetail = data;
                this.openCreateEditPopup = true;
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectTimeSheet(null);
        }
    };

    handleSetSearchObject = (searchObject) => {
        if (searchObject.staff == null) {
            searchObject.staffId = null;
        } else {
            searchObject.staffId = searchObject.staff.id;
        }

        if (searchObject.department == null) {
            searchObject.departmentId = null;
        } else {
            searchObject.departmentId = searchObject.department.id;
        }

        if (searchObject.organization == null) {
            searchObject.organizationId = null;
        } else {
            searchObject.organizationId = searchObject.organization.id;
        }

        if (searchObject.positionTitle == null) {
            searchObject.positionTitleId = null;
        } else {
            searchObject.positionTitleId = searchObject.positionTitle.id;
        }

        if (searchObject.timesheet == null) {
            searchObject.timesheetId = null;
        } else {
            searchObject.timesheetId = searchObject.timesheet.id;
        }
        this.searchObject = { ...searchObject };
    };

    uploadFileExcel = async (event) => {
        const file = event.target.files[0];
        importTimeSheetDetail(file)
            .then(() => {
                toast.success("Nhập excel thành công");
                this.searchObject = {
                    ...this.searchObject,
                    pageIndex: 1,
                };
                this.pagingTimeSheetDetail();
            })
            .catch(() => {
                toast.error("Nhập excel thất bại");
            })
            .finally(() => {
                this.handleClose();
            });
        event.target.value = null;
    };

    uploadFileDataWithSystemTemplate = async (event) => {
        const file = event.target.files[0];
        importDataWithSystemTemplate(file)
            .then(() => {
                toast.success("Nhập excel thành công");
                this.searchObject = {
                    ...this.searchObject,
                    pageIndex: 1,
                };
                this.pagingTimeSheetDetail();
            })
            .catch(() => {
                toast.error("Nhập excel thất bại");
            })
            .finally(() => {
                this.handleClose();
            });
        event.target.value = null;
    };

    handleDownloadTimesheetDetailTemplate = async () => {
        try {
            const res = await downloadTimesheetDetailTemplate();
            const blob = new Blob([res.data], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            });
            saveAs(blob, "Mẫu nhập dữ liệu chấm công.xlsx");
        } catch (error) {
            console.error("Error downloading timesheet detail template:", error);
        }
    };

    handleExportDataWithSystemTemplate = async () => {
        try {
            const payload = {
                ...this.searchObject,
            };

            const res = await exportDataWithSystemTemplate(payload);
            let blob = new Blob([res.data], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            });

            saveAs(blob, "Dữ liệu chấm công theo mẫu hệ thống.xlsx");
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        } finally {
        }
    };



    openExportLATimekeepingDataPopup = false;

    handleOpenExportLATimekeepingDataPopup = () => {
        this.openExportLATimekeepingDataPopup = true;
    };

    handleExportLATimekeepingData = async (values) => {
        if (!values?.fromDate || !values?.toDate) {
            toast.info("Chưa chọn thời gian bắt đầu và kết thúc xuất dữ liệu", {
                autoClose: 5555,
                draggable: false,
                limit: 5,
            });
            return;
        }

        toast.info("Dữ liệu đang được tạo, vui lòng đợi", {
            autoClose: 5555,
            draggable: false,
            limit: 5,
        });

        try {
            const payload = {
                ...values,
                // salaryPeriodId: values?.salaryPeriod?.id,
                fromdate: moment(values?.fromDate).format("YYYY-MM-DD"),
                todate: moment(values?.toDate).format("YYYY-MM-DD"),
            };

            console.log("payload", payload);

            const res = await exportExcelLATimekeepingData(payload);

            let blob = new Blob([res.data], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            });

            saveAs(blob, `DU_LIEU_CHAM_CONG_LA_TU_${payload?.fromdate}-${payload?.todate}.xlsx`);

            toast.dismiss();
            toast.success(i18n.t("general.successExport"));
        } catch (error) {
            console.error("Error downloading timesheet detail template:", error);
            toast.dismiss();
            toast.error("Có lỗi xảy ra khi xuất dữ liệu");
        }
    }
}
