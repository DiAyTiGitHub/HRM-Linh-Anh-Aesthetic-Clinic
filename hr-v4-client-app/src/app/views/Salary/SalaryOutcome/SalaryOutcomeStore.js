import { SalaryResult } from "app/common/Model/Salary/SalaryResult";
import { SearchSalaryOutcome } from "app/common/Model/SearchObject/SearchSalaryOutcome";
import localStorageService from "app/services/localStorageService";
import i18n from "i18n";
import { makeAutoObservable } from "mobx";
import { toast } from "react-toastify";
import { saveAs } from "file-saver";
import {
    calculateSalaryByStaffs, calculateSalaryStaffs, exportPdf, exportCalculateSalaryStaffsToExcel
} from "../SalaryResultDetail/SalaryResultStaffService";
import { pagingStaff } from "app/views/HumanResourcesInformation/StaffService";
import LocalConstants from "app/LocalConstants";
import { updateApprovalStatus } from "../SalaryStaffPayslip/SalaryStaffPayslipService";
import FileSaver from "file-saver";
import { exportFileImportSalaryValueByFilter, importFileSalaryValueByFilter } from "../SalaryResult/SalaryResultService";

export default class SalaryOutcomeStore {
    intactSearchObject = {
        ...new SearchSalaryOutcome(),
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;

    listSalaryOutcome = [];
    onViewSalaryResult = new SalaryResult();

    openChooseStaffsPopup = false;
    listAvailableStaffs = [];
    totalStaffElements = 0;
    totalStaffPages = 0;
    listChosenPayslip = [];
    openConfirmDownloadExcelPopup = false;
    openConfirmDownloadPdfPopup = false;

    openConfirmLockPayslipPopup = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.onViewSalaryResult = null;

        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;

        this.listSalaryOutcome = [];

        this.openChooseStaffsPopup = false;

        this.listAvailableStaffs = [];
        this.totalStaffElements = 0;
        this.totalStaffPages = 0;

        this.listChosenPayslip = [];
        this.openConfirmLockPayslipPopup = false;


    }

    toggleSelectAll = () => {
        if (this.listChosenPayslip.length === this.onViewSalaryResult?.salaryResultStaffs?.length) {
            // Nếu đã chọn tất cả, bỏ chọn hết
            this.listChosenPayslip = [];
        } else {
            // Nếu chưa chọn tất cả, chọn tất cả
            this.listChosenPayslip = [...this.onViewSalaryResult?.salaryResultStaffs];
        }
    };

    handleSetSearchObject = (searchObject) => {
        if (searchObject.salaryTemplate == null) {
            searchObject.salaryTemplateId = null;
        } else {
            searchObject.salaryTemplateId = searchObject.salaryTemplate.id;
        }

        if (searchObject.salaryPeriod == null) {
            searchObject.salaryPeriodId = null;
        } else {
            searchObject.salaryPeriodId = searchObject.salaryPeriod.id;
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

        if (searchObject.positionTitle == null) {
            searchObject.positionTitleId = null;
        } else {
            searchObject.positionTitleId = searchObject.positionTitle.id;
        }

        if (searchObject.position == null) {
            searchObject.positionId = null;
        } else {
            searchObject.positionId = searchObject.position.id;
        }

        this.searchObject = { ...searchObject };
    }

    handleCalculateSalaryStaffs = async () => {
        try {
            // const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ...this.searchObject, // organizationId: loggedInStaff?.user?.org?.id
            };

            const { data } = await calculateSalaryStaffs(payload);

            this.onViewSalaryResult = data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    }

    handleCalculateSalaryByStaffs = async () => {
        // console.log("catched")
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ...this.searchObject, // organizationId: loggedInStaff?.user?.org?.id
            };

            // console.log("payload", payload)


            const { data } = await calculateSalaryByStaffs(payload);
            this.onViewSalaryResult = data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    }

    handleOpenChooseStaffsPopup = () => {
        this.openChooseStaffsPopup = true;
    }

    handleExportPopup = (state) => {
        this.openConfirmDownloadExcelPopup = state;
    }

    handlePdfPopup = (state) => {
        this.openConfirmDownloadPdfPopup = state;

    }
    handleClose = () => {
        this.openChooseStaffsPopup = false;
        this.openConfirmLockPayslipPopup = false;
        this.openConfirmDownloadPdfPopup = false;
        this.openConfirmDownloadExcelPopup = false;
    }


    pagingAvailableCalculateStaffs = async () => {
        try {
            let payload = {
                ...this?.searchObject,
            };

            const data = await pagingStaff(payload);

            this.listAvailableStaffs = data.data.content;
            this.totalStaffElements = data.data.totalElements;
            this.totalStaffPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };


    handleConfirmChangeStatusToLock = async () => {
        try {
            if (this?.listChosenPayslip?.length <= 0) {
                toast.error("Không có bản ghi nào được chọn");
                this.handleClose();
                return;
            }

            const payload = {
                chosenPayslipIds: this.getSelectedIds(),
                salaryResultStaffIds: this.getSelectedIds(),
                approvalStatus: LocalConstants.SalaryStaffPayslipApprovalStatus.LOCKED.value,
            };

            const { data } = await updateApprovalStatus(payload);
            if (!data) throw new Error("");

            toast.success("Đã khóa phiếu lương!");

            await this.handleCalculateSalaryByStaffs();

            this.listChosenPayslip = [];

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };


    getSelectedIds = () => {
        const ids = [];
        this?.listChosenPayslip?.forEach(function (candidate) {
            ids.push(candidate?.id);
        });

        return ids;
    };

    handleChosenItem = (item) => {
        const index = this.listChosenPayslip.findIndex(payslip => payslip?.id === item?.id);

        if (index !== -1) {
            // Item exists, remove it
            this.listChosenPayslip.splice(index, 1);
        } else {
            // Item does not exist, add it
            this.listChosenPayslip.push(item);
        }
    };

    handleOpenConfirmLockPayslipPopup = () => {
        this.openConfirmLockPayslipPopup = true;
    }

    handExportExcelCalculateSalaryStaffs = async () => {
        toast.info("Vui lòng đợi, yêu cầu đang được xử lý");
        try {
            const payload = {
                ...this.searchObject,
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

    handExportPdfCalculateSalaryStaffs = async () => {
        toast.info("Vui lòng đợi, yêu cầu đang được xử lý");
        try {
            const payload = {
                ...this.searchObject,
            };
            const res = await exportPdf(payload);
            this.exportType = null;

            if (res && res.data && res.status === 200) {
                const blob = new Blob([res.data], {
                    type: 'application/pdf'
                });
                FileSaver.saveAs(blob, "Bang_Luong.pdf");
                toast.success(i18n.t("general.successExport"));
            } else {
                toast.error("Có lỗi xảy ra khi thực hiện thao tác xuất PDF, vui lòng thử lại sau");
            }
        } catch (err) {
            console.error(err);
            toast.error("Có lỗi xảy ra khi thực hiện thao tác xuất PDF, vui lòng thử lại sau");
        }
        this.handleClose();
    };


    // Tải mẫu nhập giá trị lương
    handleExportFileImportSalaryValueByFilter = async () => {
        try {
            const payload = {
                ...this.searchObject,
            };

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


    // Nhập mẫu giá trị lương
    handleImportFileSalaryValueByFilter = async (event) => {
        console.log("evnet", event);
        
        const fileInput = event.target;
        const file = fileInput.files[0];

        const payload = {
            ...this.searchObject,
        };

        importFileSalaryValueByFilter(file, payload)
            .then(() => {
                this.handleCalculateSalaryStaffs();
                toast.success("Nhập excel thành công");
            })
            .catch(() => {
                toast.error("Nhập excel thất bại");
            })
            .finally(() => {
                // this.handleClose();
                fileInput.value = null;
            });
    };
}
