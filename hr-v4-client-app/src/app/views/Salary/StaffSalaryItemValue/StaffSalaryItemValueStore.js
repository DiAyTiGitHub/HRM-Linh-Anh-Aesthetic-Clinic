import { makeAutoObservable } from "mobx";
import {
    deleteStaffSalaryItemValue,
    getById,
    getListByStaffAndTemplateItem,
    getListByStaffId,
    getSalaryValueHistories,
    pagingStaffSalaryItemValue,
    saveStaffSalaryItemValue,
    updateStaffSalaryItemValue,
} from "./StaffSalaryItemValueService";

import i18n from "i18n";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class StaffSalaryItemValueStore {
    pageIndex = 1;
    pageSize = 10;
    keyword = null;
    salaryTemplateId = null;
    totalElements = 0;
    totalPages = 0;
    listStaffSalaryItemValue = [];
    openCreateEditPopup = false;
    selectedStaffSalaryItemValue = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listOnDelete = [];
    listChooseStaffSalaryItem = [];
    selectedStaff = null;
    selectedSalaryTemplate = null;
    openValueHitoriesPopup = false;
    listValueHistories = [];

    searchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null,
    };

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.pageIndex = 1;
        this.pageSize = 10;
        this.keyword = null;
        this.salaryTemplateId = null;
        this.totalElements = 0;
        this.totalPages = 0;
        this.listStaffSalaryItemValue = [];
        this.openCreateEditPopup = false;
        this.selectedStaffSalaryItemValue = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.listChooseStaffSalaryItem = [];
        this.listValueHistories = [];

        this.searchObject = {
            pageIndex: 1,
            pageSize: 10,
            keyword: null,
        };
    };

    updatePageData = (keyword) => {
        this.page = 1;
        this.keyword = keyword || null;
        this.pagingStaffSalaryItemValue();
    };

    saveStaffSalaryItemValue = async (staffSalaryItemValue) => {
        this.loadingInitial = true;
        try {
            const { data } = await saveStaffSalaryItemValue(staffSalaryItemValue);
            if (data) {
                toast.success(i18n.t("Lưu thành công"));
                this.openCreateEditPopup = false;
                this.selectedStaffSalaryItemValue = null;
            } else {
                toast.error(i18n.t("toast.error"));
            }
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        } finally {
            this.loadingInitial = false;
        }
    };

    pagingStaffSalaryItemValue = async (searchProp) => {
        this.loadingInitial = true;
        try {
            const searchObject = {
                keyword: this.keyword,
                pageIndex: this.pageIndex,
                pageSize: this.pageSize,
                ...searchProp
            };
            const data = await pagingStaffSalaryItemValue(searchObject);
            this.listStaffSalaryItemValue = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            this.loadingInitial = false;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
            this.loadingInitial = false;
        }
    };

    setStaff = (value) => {
        this.selectedStaff = value;
    };

    setSelectedSalaryTemplate = (value) => {
        this.selectedSalaryTemplate = value;
        this.setListChooseStaffSalaryItem(this.selectedSalaryTemplate);
    };

    setListChooseStaffSalaryItem = (value) => {
        this.listChooseStaffSalaryItem = value.templateItems || [];
    };

    setPageIndex = async (page) => {
        this.pageIndex = page;
        await this.pagingStaffSalaryItemValue();
    };

    setPageSize = async (event) => {
        this.pageSize = event.target.value;
        this.pageIndex = 1;

        await this.pagingStaffSalaryItemValue();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    getById = async (salaryTypeId) => {
        try {
            const { data } = await getById(salaryTypeId);
            this.selectedStaffSalaryItemValue = data;
            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    getListStaffSalaryItemValue = async (staffId) => {
        try {
            const { data } = await getListByStaffId(staffId);
            this.listStaffSalaryItemValue = data || [];
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;
        this.openConfirmDeleteListPopup = false;
        this.openValueHitoriesPopup = false;
        this.listValueHistories = [];
    };

    handleDelete = (value) => {
        this.selectedStaffSalaryItemValue = value;
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleOpenCreateEdit = async (selectedStaffSalaryItemValueId) => {
        try {
            if (selectedStaffSalaryItemValueId) {
                const { data } = await getById(selectedStaffSalaryItemValueId);
                this.selectedStaffSalaryItemValue = data;
            } else {
                this.selectedStaffSalaryItemValue = null;
            }

            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleOpenCreate = (staffSalaryItemValue) => {
        this.selectedStaffSalaryItemValue = { ...staffSalaryItemValue };
        this.openCreateEditPopup = true;
    };

    handleConfirmDelete = async () => {
        try {
            const { data } = await deleteStaffSalaryItemValue(this?.selectedStaffSalaryItemValue?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingStaffSalaryItemValue();

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteNonPaging = async () => {
        try {
            const { data } = await deleteStaffSalaryItemValue(this?.selectedStaffSalaryItemValue?.id);
            toast.success(i18n.t("toast.delete_success"));

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };


    getListSalaryTemplateItem = async (payload) => {
        try {
            const { data } = await getListByStaffAndTemplateItem(payload);

            if (!data) throw new Error("Something error");

            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("Có lỗi xảy ra"));
        }
    };

    handleOpenValueHitoriesPopup = async (salaryValueId) => {
        try {
            const { data } = await getSalaryValueHistories(salaryValueId);

            this.listValueHistories = data || [];

            this.openValueHitoriesPopup = true;
        } catch (error) {
            toast.error("Có lỗi xảy ra khi lấy dữ liệu lịch sử thành phần lương");
            console.error(error);
        }
    };

    getValueHistoryStaffName = () => {
        let res = null;

        if (this.listValueHistories?.length > 0) {
            res = this.listValueHistories[0]?.staff?.displayName;
        }

        return res;
    };

    getValueHistorySalaryItemName = () => {
        let res = null;

        if (this.listValueHistories?.length > 0) {
            res = this.listValueHistories[0]?.salaryItem?.name;
        }

        return res;
    };
}
