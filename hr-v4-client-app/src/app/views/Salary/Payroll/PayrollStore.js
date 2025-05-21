import { makeAutoObservable } from "mobx";
import {
    getById,
    getSalaryResultBoard,
    saveSalaryResult,
    deleteSalaryResult,
    searchSalaryResultBoard,
    exportFileImportSalaryValueByFilter,
    importFileSalaryValueByFilter,
    deleteMultiple,
    hasAnyOrphanedPayslips,
    mergeOrphansToSalaryBoard,
    getAllOrphanedPayslips,
    getBasicInfoById,
    recalculationSalaryBoard,
    lockPayroll,
    unlockPayroll
} from "../SalaryResult/SalaryResultService";

import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { SalaryResult } from "app/common/Model/Salary/SalaryResult";

import {
    deleteSalaryResultStaff,
    reCalculateRowByChangingCellValue,
    saveSalaryResultStaff,
    exportPdf,
    exportCalculateSalaryStaffsToExcel,
} from "../SalaryResultDetail/SalaryResultStaffService";

import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import FileSaver from "file-saver";
import { saveAs } from "file-saver";
import { pagingSalaryResultStaff, getTotalSalaryResultStaff } from "../SalaryStaffPayslip/SalaryStaffPayslipService";
import { useState } from "react";


toast.configure({
    autoClose: 5000,
    draggable: false,
    limit: 5,
});

export default class PayrollStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null,
        staff: null,
        staffId: null,
        salaryPeriod: null,
        salaryPeriodId: null,
        salaryResult: null,
        salaryResultId: null,
        organization: null,
        organizationId: null,
        department: null,
        departmentId: null,
        positionTitle: null,
        positionTitleId: null,
        salaryResultStaffIds: [],
    };

    totalPages = 0;
    totalElements = 0;
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    onViewSalaryResult = new SalaryResult();

    openConfirmDeletePopup = false;
    openChooseStaffsPopup = false;
    openConfirmLockPayslipPopup = false;
    openConfirmDownloadPdfPopup = false;
    openConfirmDownloadExcelPopup = false;
    openConfirmDeleteListPopup = false
    orphanedPayslipsCount = 0;
    orphanedPayslipsList = [];
    openViewOrphanedPayslipsPopup = false;
    openConfirmRecalculationPopup = false;
    listSalaryResultStaffs = [];
    totalSalaryResultStaff = null;
    basicInfo = null;

    tabCU = 0;

    openSchedulePopup = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.totalElements = 0;
        this.totalPages = 0;
        this.onViewSalaryResult = new SalaryResult();
        this.openConfirmDeletePopup = false;
        this.tabCU = 0;
        this.orphanedPayslipsCount = 0;
        this.orphanedPayslipsList = [];
        this.openConfirmLockPayslipPopup = false;
        this.openConfirmDownloadPdfPopup = false;
        this.openConfirmDownloadExcelPopup = false;
        this.openConfirmDeleteListPopup = false;
        this.openViewOrphanedPayslipsPopup = false;
        this.openConfirmRecalculationPopup = false;
        this.listSalaryResultStaffs = [];
        this.totalSalaryResultStaff = null;
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.openSchedulePopup = false;
        this.openLockPayrollPopup = false;
        this.openUnlockPayrollPopup = false;

    }

    handleSetOpenSchedulePopup = (state) => {
        this.openSchedulePopup = state;
    }

    handleSetSearchObj = (so) => {
        if (so) {
            if (so.staff == null) {
                so.staffId = null;
            } else {
                so.staffId = so.staff.id;
            }

            if (so.department == null) {
                so.departmentId = null;
            } else {
                so.departmentId = so.department.id;
            }

            if (so.organization == null) {
                so.organizationId = null;
            } else {
                so.organizationId = so.organization.id;
            }

            if (so.positionTitle == null) {
                so.positionTitleId = null;
            } else {
                so.positionTitleId = so.positionTitle.id;
            }

            const newSO = {
                ... this.searchObject,
                ...so,
            };

            this.searchObject = { ...newSO };
        }
    }


    handleChangePage = (event, newPage) => {
        this.setPage(newPage);
    };

    setTabCU = (tabCU) => {
        this.tabCU = tabCU;
    }

    setPage = async (page) => {
        this.searchObject.pageIndex = page;
        await this.pagingSalaryResultStaff();
        await this.getListSumSalaryResultStaff();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingSalaryResultStaff();
        await this.getListSumSalaryResultStaff();
    };


    handleClose = async () => {
        this.openConfirmDeletePopup = false;
        this.openChooseStaffsPopup = false;
        this.openConfirmLockPayslipPopup = false;
        this.openConfirmDownloadPdfPopup = false;
        this.openConfirmDownloadExcelPopup = false;
        this.openConfirmDeleteListPopup = false;
        this.openConfirmRecalculationPopup = false;
        this.listOnDelete = [];
        this.openLockPayrollPopup = false;
        this.openUnlockPayrollPopup = false;

    };


    handleExportPopup = (state) => {
        this.openConfirmDownloadExcelPopup = state;
    }


    handleDelete = (salaryTemplate) => {
        this.onViewSalaryResult = { ...salaryTemplate };
        this.openConfirmDeletePopup = true;
    };


    handleConfirmDelete = async () => {
        try {
            const { data } = await deleteSalaryResult(this?.onViewSalaryResult?.id);
            toast.success(i18n.t("toast.delete_success"));

            this.handleClose();

            await this.pagingSalaryTemplates();
            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
            throw new Error(error);
        }
    };


    handlePdfPopup = (state) => {
        this.openConfirmDownloadPdfPopup = state;

    }

    handleImportFileSalaryValueByFilter = async (event) => {
        const fileInput = event.target;
        const file = fileInput.files[0];

        if (!file) {
            toast.error("Không có file được chọn");
            return false;
        }

        const payload = {
            ... this.searchObject,
        };

        try {
            await importFileSalaryValueByFilter(file, payload);
            toast.success("Nhập excel thành công");
            return true;
        } catch (error) {
            toast.error("Nhập excel thất bại");
            return false;
        } finally {
            fileInput.value = null;
        }
    };


    // Tải mẫu nhập giá trị lương
    handleExportFileImportSalaryValueByFilter = async () => {
        try {
            const payload = {
                ... this.searchObject,
                salaryPeriodId: this?.onViewSalaryResult?.salaryPeriod?.id,
                salaryTemplateId: this?.onViewSalaryResult?.salaryTemplate?.id,
                isExportExcel: true
            };

            console.log("payload", payload);

            const res = await exportFileImportSalaryValueByFilter(payload);

            let blob = new Blob([res.data], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            });

            saveAs(blob, "Mẫu nhập giá trị lương.xlsx");
            toast.success(i18n.t("general.successExport"));
        } catch (error) {
            console.error("Error downloading timesheet detail template:", error);
        }
    }

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handExportExcelCalculateSalaryStaffs = async () => {
        toast.info("Vui lòng đợi, yêu cầu đang được xử lý");

        try {
            const payload = {
                // staffs: this.onViewSalaryResult?.staffs,
                salaryPeriodId: this.onViewSalaryResult?.salaryPeriod?.id,
                salaryTemplateId: this.onViewSalaryResult?.salaryTemplate?.id,
                ... this.searchObject,
            };

            const res = await exportCalculateSalaryStaffsToExcel(payload);
            this.exportType = null;

            if (res && res.data && res.status === 200) {
                const blob = new Blob([res.data], {
                    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
                });
                FileSaver.saveAs(blob, "Bang_Luong.xlsx");
                toast.success(i18n.t("general.successExport"));
            } else {
                toast.error("Có lỗi xảy ra khi thực hiện thao tác xuất Excel, vui lòng thử lại sau");
            }
        } catch (err) {
            console.error(err);
            toast.error("Có lỗi xảy ra khi thực hiện thao tác xuất Excel, vui lòng thử lại sau");
        }
        this.handleClose();
    };

    handleConfirmDeleteList = async () => {
        try {
            const salaryResultId = this.onViewSalaryResult?.id;
            if (!salaryResultId) {
                toast.error("Không xác định được bảng lương cần xóa");
            }

            await deleteMultiple([salaryResultId]);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };


    hasAnyOrphanedPayslips = async (salaryResultId) => {
        try {
            const { data } = await hasAnyOrphanedPayslips(salaryResultId);

            this.orphanedPayslipsCount = data;

            return data;

        } catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra khi lấy dữ liệu phiếu lương chưa được tổng hợp");
        }
    }

    getAllOrphanedPayslips = async (salaryResultId) => {
        try {
            const { data } = await getAllOrphanedPayslips(salaryResultId);

            if (data && data.length > 0) {
                this.orphanedPayslipsList = [
                    ...data
                ];
            } else {
                this.orphanedPayslipsList = [];
            }

            return data;

        } catch (error) {
            console.error(error);
            toast.error("Có lỗi xảy ra khi lấy dữ liệu phiếu lương chưa được tổng hợp");
        }
    }

    handleViewOrphanedPayslipsPopup = async () => {
        try {
            const salaryResultId = this.onViewSalaryResult?.id;
            await this.getAllOrphanedPayslips(salaryResultId);

            this.openViewOrphanedPayslipsPopup = true;

        } catch (error) {
            console.error(error);
        }
    }

    handleCloseViewOrphanedPayslip = async () => {
        try {
            const salaryResultId = this.onViewSalaryResult?.id;

            this.orphanedPayslipsList = [];
            await this.hasAnyOrphanedPayslips(salaryResultId);

            this.openViewOrphanedPayslipsPopup = false;
        } catch (error) {
            console.error(error);
        }
    }

    handleMergeOrphansToSalaryBoard = async (selectedIds) => {
        try {
            const payload = {
                salaryResultId: this.onViewSalaryResult?.id,
                chosenPayslipIds: selectedIds || []
            };
            const { data } = await mergeOrphansToSalaryBoard(payload);

            if (data) {
                toast.success("Đã tổng hợp các phiếu lương được chọn vào bảng lương");
            } else {
                throw new Error();
            }

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    }


    getBasicInfoById = async (salaryResultId) => {
        try {
            const { data } = await getBasicInfoById(salaryResultId);

            this.onViewSalaryResult = data;
            this.basicInfo = data;
            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };


    pagingSalaryResultStaff = async () => {
        try {
            // const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ... this.searchObject,
                // organizationId: loggedInStaff?.user?.org?.id,
            };

            const data = await pagingSalaryResultStaff(payload);

            this.listSalaryResultStaffs = data.data.content || [];
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            //console.log("listSalaryResultStaffs", this.listSalaryResultStaffs)
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    getListSumSalaryResultStaff = async () => {
        try {

            const salaryResultStaffIds = Array.isArray(this.listSalaryResultStaffs)
                ? this.listSalaryResultStaffs.map(item => item?.id).filter(Boolean)
                : [];
            // console.log("salaryResultStaffIds", salaryResultStaffIds)
            // Nếu không có ID nào thì không cần gọi API


            if (salaryResultStaffIds.length === 0) {
                this.totalSalaryResultStaff = null;
                return;
            }

            const salaryTemplateId = this.listSalaryResultStaffs[0]?.salaryTemplate?.id;
            if (!salaryTemplateId) {
                return;
            }

            const payload = {
                ... this.searchObject,
                salaryResultStaffIds,
                salaryTemplateId: salaryTemplateId
            };

            const response = await getTotalSalaryResultStaff(payload);
            this.totalSalaryResultStaff = response.data || null;

        } catch (error) {
            console.error("Error in getListSumSalaryResultStaff:", error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleRecalculation = () => {
        this.openConfirmRecalculationPopup = true;
    };

    handRecalculationConfirmSalaryBoard = async (salaryResultId) => {
        toast.info("Vui lòng đợi, yêu cầu đang được xử lý", {
            autoClose: 55555,
            draggable: false,
            limit: 5,
        });

        try {
            const { data } = await recalculationSalaryBoard(salaryResultId);

            await this.pagingSalaryResultStaff();
            await this.getListSumSalaryResultStaff();

            toast.dismiss();

            toast.success("Bảng lương đã được tính toán lại");

            this.handleClose();
        } catch (err) {
            console.error(err);
            toast.error("Có lỗi xảy ra lỗi, vui lòng thử lại sau");
        }
    };


    openLockPayrollPopup = false;
    openUnlockPayrollPopup = false;

    handleOpenLockPayrollPopup = () => {
        this.openLockPayrollPopup = true;
    }

    handleOpenUnlockPayrollPopup = () => {
        this.openUnlockPayrollPopup = true;
    }

    confirmLockPayroll = async () => {
        try {
            const { data } = await lockPayroll(this?.onViewSalaryResult?.id);

            if (!data) {
                throw new Error("Không thể khóa bảng lương");
            }

            toast.success("Đã khóa bảng lương");

            this.openLockPayrollPopup = false;

            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));

            return null;
        }
    };

    confirmUnLockPayroll = async () => {
        try {
            const { data } = await unlockPayroll(this?.onViewSalaryResult?.id);

            if (!data) {
                throw new Error("Không thể hủy khóa bảng lương");
            }

            toast.success("Đã hủy khóa bảng lương");

            this.openUnlockPayrollPopup = false;

            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));

            return null;
        }
    };
}
