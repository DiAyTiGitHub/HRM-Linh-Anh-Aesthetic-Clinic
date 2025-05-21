import { makeAutoObservable } from "mobx";
import {
    pagingStaffAnnualLeaveHistory,
    getById,
    saveStaffAnnualLeaveHistory,
    deleteMultiple,
    deleteById,
} from "./StaffAnnualLeaveHistoryService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { SearchStaffAnnualLeaveHistory } from "app/common/Model/SearchObject/SearchStaffAnnualLeaveHistory";
import { StaffAnnualLeaveHistory } from "app/common/Model/HumanResource/StaffAnnualLeaveHistory";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class StaffAnnualLeaveHistoryStore {
    intactSearchObject = {
        ... new SearchStaffAnnualLeaveHistory(),
        pageIndex: 1,
        pageSize: 10
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    staffAnnualLeaveHistoryList = [];
    openCreateEditPopup = false;
    selectedStaffAnnualLeaveHistory = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listChosen = [];
    openViewPopup = false;

    handleOpenView = async (annualLeaveHistory) => {
        try {
            if (annualLeaveHistory) {
                const { data } = await getById(annualLeaveHistory?.id);
                this.selectedStaffAnnualLeaveHistory = data;
            } else {
                this.selectedStaffAnnualLeaveHistory = {
                    ... new StaffAnnualLeaveHistory(),
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
        this.staffAnnualLeaveHistoryList = [];
        this.openCreateEditPopup = false;
        this.selectedStaffAnnualLeaveHistory = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listChosen = [];
        this.openViewPopup = false;
    }

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

        this.searchObject = { ...searchObject };
    }

    pagingStaffAnnualLeaveHistory = async () => {
        try {
            // this.handleSetSearchObject(this.searchObject);

            const payload = {
                ... this.searchObject,
            };
            const data = await pagingStaffAnnualLeaveHistory(payload);

            this.staffAnnualLeaveHistoryList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingStaffAnnualLeaveHistory();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingStaffAnnualLeaveHistory();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteByIds) => {
        this.listChosen = deleteByIds;
    };

    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;
        this.openConfirmUpdateStatusPopup = false;
        this.openConfirmDeleteListPopup = false;
        this.onUpdateStatus = null;
        this.listChosen = [];
        this.openViewPopup = false;
    };

    handleDelete = (annualLeaveHistory) => {
        this.selectedStaffAnnualLeaveHistory = { ...annualLeaveHistory };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleOpenCreateEdit = async (annualLeaveHistory) => {
        // console.log("annualLeaveHistory", annualLeaveHistory);

        try {
            if (annualLeaveHistory) {
                const { data } = await getById(annualLeaveHistory?.id);
                this.selectedStaffAnnualLeaveHistory = data;
            } else {
                const staff = this?.searchObject?.staff;

                this.selectedStaffAnnualLeaveHistory = {
                    ... new StaffAnnualLeaveHistory(),
                    staff,
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
            const { data } = await deleteById(this?.selectedStaffAnnualLeaveHistory?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingStaffAnnualLeaveHistory();

            this.handleClose();

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        try {
            const deleteData = this.getSelectedIds();

            await deleteMultiple(deleteData);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingStaffAnnualLeaveHistory();
            this.listChosen = [];

            this.handleClose();


        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveStaffAnnualLeaveHistory = async (annualLeaveHistory) => {
        try {
            const { data } = await saveStaffAnnualLeaveHistory(annualLeaveHistory);
            toast.success("Thống kê lịch nghỉ đã được lưu");
            this.handleClose();
            this.pagingStaffAnnualLeaveHistory();

            return data;
        } catch (error) {
            // console.error(error);
            // // toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");
            // if (error.response.status == 409) {
            //     toast.error("Mã yêu cầu đã được sử dụng, vui lòng sử dụng mã yêu cầu khác", {
            //         autoClose: 5000,
            //         draggable: false,
            //         limit: 5,
            //     });
            // } else {
            //     toast.error(i18n.t("toast.error"));
            // }
            // throw new Error(i18n.t("toast.error"));
            // // return null;
            toast.error(i18n.t("toast.error"));
        }
    };

    getSelectedIds = () => {
        const ids = [];
        this?.listChosen?.forEach(function (item) {
            ids.push(item?.id);
        });

        return ids;
    }
}
