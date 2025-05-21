import { makeAutoObservable } from "mobx";
import {
    pagingHrDepartmentIp,
    getById,
    saveDepartmentIp,
    deleteMultiple,
    deleteHrDepartmentIp,
} from "./HrDepartmentIpService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import { SalaryItem } from "../../common/Model/Salary/SalaryItem";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class HrDepartmentIpStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null,
        department: null,
        departmentId: null,
        organization: null,
        organizationId: null,
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    listDepartmentIp = [];
    openCreateEditPopup = false;
    selectedDepartmentIp = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listOnDelete = [];
    openViewPopup = false;

    handleOpenView = async (departmentIp) => {
        try {
            if (departmentIp) {
                const { data } = await getById(departmentIp);
                this.selectedDepartmentIp = data;
            } else {
                this.selectedDepartmentIp = {
                    ...this.initialDepartmentIp,
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
        this.listDepartmentIp = [];
        this.openCreateEditPopup = false;
        this.selectedDepartmentIp = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openViewPopup = false;
    };

    handleSetSearchObject = (searchObject) => {
        this.searchObject = { ...searchObject };
    };

    pagingHrDepartmentIp = async () => {
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ...this.searchObject,
                // organizationId:loggedInStaff?.user?.org?.id ,
                // departmentId:this.searchObject?.department?.id ,
                department: null,
                organization: null,
            };
            const data = await pagingHrDepartmentIp(payload);

            this.listDepartmentIp = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingHrDepartmentIp();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingHrDepartmentIp();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteHrDepartmentIps) => {
        this.listOnDelete = deleteHrDepartmentIps;
    };

    getById = async (departmentIp) => {
        try {
            const { data } = await getById(departmentIp);
            this.selectedDepartmentIp = data;
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
        this.openViewPopup = false;
    };

    handleDelete = (departmentIp) => {
        this.selectedDepartmentIp = { id: departmentIp };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    initialDepartmentIp = {
        id: null,
        ipAddress: null,
        department: null,
        description: null,
    };

    handleOpenCreateEdit = async (departmentIp) => {
        try {
            if (departmentIp) {
                const { data } = await getById(departmentIp);
                this.selectedDepartmentIp = data;
            } else {
                this.selectedDepartmentIp = {
                    ...this.initialDepartmentIp,
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
            const { data } = await deleteHrDepartmentIp(this?.selectedDepartmentIp?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingHrDepartmentIp();

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

            await this.pagingHrDepartmentIp();
            this.listOnDelete = [];

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveDepartmentIp = async (departmentIp) => {
        try {
            const { data } = await saveDepartmentIp(departmentIp);
            toast.success("Thông tin Địa chỉ IP đã được lưu");
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");
        }
    };
}
