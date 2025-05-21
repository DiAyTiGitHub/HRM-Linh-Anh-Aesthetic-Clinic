import { makeAutoObservable } from "mobx";
// import {} from "./StaffHasSocialInsuranceService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import _ from "lodash";
import FileSaver from "file-saver";
import {
    pagingStaffLabourAgreement,
    getTotalHasSocialIns,
    exportHasInsuranceStaff,
    exportHICInfoToWord,
    exportExcelStaffSIByType
} from "../StaffLabourAgreement/StaffLabourAgreementService";
import { SearchStaffHasSocialInsurance } from "app/common/Model/SearchObject/SearchStaffHasSocialInsurance";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class StaffHasSocialInsuranceStore {
    intactSearchObject = {
        ...new SearchStaffHasSocialInsurance(),
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    listStaffSocialInsurance = [];
    openCreateEditPopup = false;
    selectedStaffSocialInsurance = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    openConfirmDownloadPopup = false;
    openViewStaffSocialInsurance = false;
    listOnDelete = [];
    openChooseExportType = false;
    exportType = null;

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.listStaffSocialInsurance = [];
        this.openCreateEditPopup = false;
        this.selectedStaffSocialInsurance = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openChooseExportType = false;
        this.exportType = null;
    }

    //lọc theo trạng thái trả bảo hiểm = thay đổi tab
    handleChangeViewPaidStatus = (status) => {
        const so = { ...this.searchObject, paidStatus: status };
        this.searchObject = so;
    }
    handleChangeConfirmDownload = (status, values) => {
        this.exportType = values;
        this.openConfirmDownloadPopup = status;
    }
    handleSetSearchObject = (searchObject) => {
        // if (searchObject.department == null) {
        //     searchObject.departmentId = null;
        // } else {
        //     searchObject.departmentId = searchObject.department.id;
        // }
        this.searchObject = { ...searchObject };
    }

    pagingStaffLabourAgreement = async () => {
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ...this.searchObject,
                //organizationId: loggedInStaff?.user?.org?.id
            };

            const data = await pagingStaffLabourAgreement(payload);
            // this.listStaffSocialInsurance = [...data.data.content];
            const totalRow = await getTotalHasSocialIns(payload);
            this.listStaffSocialInsurance = [...data.data.content, totalRow.data];
            this.totalPages = data.data.totalPages;
            this.totalElements = data.data.totalElements;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleExportExcelByFilter = async () => {
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ...this.searchObject,
                //organizationId: loggedInStaff?.user?.org?.id
            };

            const res = await exportHasInsuranceStaff(payload);

            if (res && res.data) {
                const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });

                FileSaver.saveAs(blob, "Danh_sach_nhan_vien_tham_gia_bao_hiem.xlsx");
            } else {
                toast.error("Không nhận được dữ liệu từ server.");
            }


        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    }

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingStaffLabourAgreement();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingStaffLabourAgreement();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteStaffSocialInsurances) => {
        this.listOnDelete = deleteStaffSocialInsurances;
    };

    handleExportHICInfoToWord = async (id, fileName = "TO_KHAI.docx") => {
        try {
            const response = await exportHICInfoToWord(id);

            // Tạo URL từ blob và tạo một link ẩn để tải file về
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", fileName + ".docx"); // Đặt tên file mặc định hoặc từ tham số
            document.body.appendChild(link);
            link.click(); // Bắt đầu tải xuống

            // Giải phóng URL blob sau khi tải xong
            document.body.removeChild(link);
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error("Error downloading file:", error);
        }
    };

    handleClose = () => {
        this.openChooseExportType = false;
        this.openConfirmDownloadPopup = false;
    };

    handleOpenChooseExportType = () => {
        this.openChooseExportType = true;
    }

    handleExportSOByType = async (exportType) => {
        toast.info("Vui lòng đợi, yêu cầu đang được xử lý");
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ...this.searchObject,
                exportType: exportType
            };
            const res = await exportExcelStaffSIByType(payload);
            this.exportType = null;

            if (res && res.data && res.status === 200) {
                const blob = new Blob([res.data], {
                    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
                });
                FileSaver.saveAs(blob, "Danh_sach_nhan_dong_BHXH_theo_nghiep_vu.xlsx");
                toast.success(i18n.t("general.successExport"));
                await this.pagingStaffLabourAgreement();
            } else if (res.status === 204) {
                toast.warn("Không có dữ liệu để xuất.");
            } else {
                toast.error("Không nhận được dữ liệu từ server.");
            }
        } catch (err) {
            console.error(err);
            toast.error("Có lỗi xảy ra khi thực hiện thao tác xuất Excel, vui lòng thử lại sau");
        }
        this.handleClose();
    };

}
