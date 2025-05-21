import { makeAutoObservable } from "mobx";
import {
    pagingStaffAdvancePayment,
    saveStaffAdvancePayment,
    deleteMultiple,
    deleteStaffAdvancePayment,
    updateStaffAdvancePaymentApprovalStatus,
    getStaffAdvancePaymentById
} from "./StaffAdvancePaymentService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import LocalConstants from "app/LocalConstants";
import { StaffAdvancePayment } from "app/common/Model/StaffAdvancePayment";
import { SalaryPeriod } from "../../common/Model/Salary/SalaryPeriod";
import { SearchStaffAdvancedPayment } from "app/common/Model/SearchObject/SearchStaffAdvancedPayment";
import { getCurrentStaff } from "../profile/ProfileService";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class StaffAdvancePaymentStore {
    intactSearchObject = {
        ... new SearchStaffAdvancedPayment()
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    listStaffAdvancePayment = [];
    openCreateEditPopup = false;
    selectedStaffAdvancePayment = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listOnDelete = [];
    openViewPopup = false;

    handleOpenView = async (staffAdvancePaymentId) => {
        try {
            if (staffAdvancePaymentId) {
                const { data } = await getStaffAdvancePaymentById(staffAdvancePaymentId);
                this.selectedStaffAdvancePayment = {
                    ...JSON.parse(JSON.stringify(data))
                };
            } else {
                const staff = this?.searchObject?.staff;
                const salaryPeriod = this?.searchObject?.salaryPeriod;

                this.selectedStaffAdvancePayment = {
                    ... new StaffAdvancePayment(),
                    staff,
                    salaryPeriod,
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
        this.listStaffAdvancePayment = [];
        this.openCreateEditPopup = false;
        this.selectedStaffAdvancePayment = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openViewPopup = false;

    }

    //lọc theo trạng thái trả bảo hiểm = thay đổi tab
    handleChangeViewStatus = (status) => {
        const so = { ... this.searchObject, approvalStatus: status };
        this.searchObject = so;
    }

    handleSetSearchObject = (searchObject) => {
        if (searchObject.department == null) {
            searchObject.departmentId = null;
        } else {
            searchObject.departmentId = searchObject.department.id;
        }
        this.searchObject = { ...searchObject };
    }


    pagingStaffAdvancePayment = async () => {
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ... this.searchObject,
                organizationId: loggedInStaff?.user?.org?.id
            };
            if (!payload?.approvalStatus || payload?.approvalStatus == 0) payload.approvalStatus = null;

            const data = await pagingStaffAdvancePayment(payload);

            this.listStaffAdvancePayment = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingStaffAdvancePayment();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingStaffAdvancePayment();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteStaffAdvancePayments) => {
        this.listOnDelete = deleteStaffAdvancePayments;
    };

    getById = async (staffAdvancePaymentId) => {
        try {
            const { data } = await getStaffAdvancePaymentById(staffAdvancePaymentId);
            this.selectedStaffAdvancePayment = data;
            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openConfirmChangeStatus = false;
        this.onChooseStatus = null;
        this.openViewPopup = false;
    };

    handleDelete = (salaryItem) => {
        this.selectedStaffAdvancePayment = { ...salaryItem };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleOpenCreateEdit = async (staffAdvancePaymentId) => {
        try {
            if (staffAdvancePaymentId) {
                const { data } = await getStaffAdvancePaymentById(staffAdvancePaymentId);
                this.selectedStaffAdvancePayment = {
                    ...JSON.parse(JSON.stringify(data))
                };
            } else {
                const staff = this?.searchObject?.staff;
                const salaryPeriod = this?.searchObject?.salaryPeriod;

                this.selectedStaffAdvancePayment = {
                    ... new StaffAdvancePayment(),
                    staff,
                    salaryPeriod,
                };
            }

            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmDelete = async () => {
        try {
            const { data } = await deleteStaffAdvancePayment(this?.selectedStaffAdvancePayment?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingStaffAdvancePayment();

            this.handleClose();

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        try {
            const deleteData = [];

            for (let i = 0; i < this?.listOnDelete?.length; i++) {
                deleteData.push(this?.listOnDelete[i]?.id);
            }

            // console.log("deleteData", deleteData)
            await deleteMultiple(deleteData);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingStaffAdvancePayment();
            this.listOnDelete = [];

            this.handleClose();


        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveStaffAdvancePayment = async (salaryItem) => {
        try {
            const { data } = await saveStaffAdvancePayment(salaryItem);
            toast.success("Thông tin Đóng bảo hiểm của nhân viên đã được lưu");
            this.handleClose();

            return data;

        } catch (error) {
            console.error(error);
            // if (error.response.status == 409) {
            //   toast.error("Mã Đóng bảo hiểm đã được sử dụng, vui lòng sử dụng mã Đóng bảo hiểm khác", {
            //     autoClose: 5000,
            //     draggable: false,
            //     limit: 5,
            //   });
            // }
            // else if (error.response.status == 304) {
            //   toast.warning("Đóng bảo hiểm mặc định của hệ thống không được phép chỉnh sửa", {
            //     autoClose: 5000,
            //     draggable: false,
            //     limit: 5,
            //   });
            // }
            // else {
            toast.error(i18n.t("toast.error"));
            // }

            throw new Error(i18n.t("toast.error"));
        }
    };

    getApprovalStatusName = (status) => {
        return LocalConstants.StaffAdvancePaymentApprovalStatus.getListData().find(i => i.value == status)?.name;
    }


    // update paid status
    openConfirmChangeStatus = false;
    onChooseStatus = false;

    handleOpenConfirmChangeStatus = (onChooseStatus) => {
        this.openConfirmChangeStatus = true;
        this.onChooseStatus = onChooseStatus;
    }

    handleRemoveActionItem = (onRemoveId) => {
        this.listOnDelete = this?.listOnDelete?.filter(item => item?.id !== onRemoveId);
    };

    handleConfirmChangeStatus = async () => {
        try {
            if (this?.listOnDelete?.length <= 0) {
                toast.error("Không có bản ghi nào được chọn");
                this.handleClose();
                return;
            }

            const payload = {
                chosenRecordIds: this.getSelectedIds(),
                approvalStatus: this.onChooseStatus,
            };

            const { data } = await updateStaffAdvancePaymentApprovalStatus(payload);
            if (!data) throw new Error("");

            toast.success("Cập nhật trạng thái thành công!");

            await this.pagingStaffAdvancePayment();

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    }

    getSelectedIds = () => {
        const ids = [];
        this?.listOnDelete?.forEach(function (candidate) {
            ids.push(candidate?.id);
        });

        return ids;
    }

}
