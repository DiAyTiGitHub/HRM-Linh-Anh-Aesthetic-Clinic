import localStorageService from "app/services/localStorageService";
import i18n from "i18n";
import { makeAutoObservable } from "mobx";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import {
    deleteOrgChartData ,
    deleteMultiple ,
    getOrgChartDataById ,
    pagingOrgChartData ,
    saveOrgChartData ,
} from "./OrganizationalChartDataService";

toast.configure({
    autoClose:2000 ,
    draggable:false ,
    limit:3 ,
});

export default class OrganizationalChartDataStore {
    searchObject = {
        pageIndex:1 ,
        pageSize:10 ,
        keyword:null ,
    };
    totalElements = 0;
    totalPages = 0;
    listOrganizationalChartData = [];
    openCreateEditPopup = false;
    selectedOrgChartData = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listOnDelete = [];

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = {
            pageIndex:1 ,
            pageSize:10 ,
            keyword:null ,
        };
        this.totalElements = 0;
        this.totalPages = 0;
        this.listOrganizationalChartData = [];
        this.openCreateEditPopup = false;
        this.selectedOrgChartData = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
    }

    handleSetSearchObject = (searchObject) => {
        this.searchObject = {... searchObject};
    }

    pagingOrgChartData = async () => {
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ... this.searchObject ,
                //organizationId: loggedInStaff?.user?.org?.id
            };
            const data = await pagingOrgChartData(payload);

            this.listOrganizationalChartData = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingOrgChartData();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingOrgChartData();
    };

    handleChangePage = async (event , newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteOrgChartDatas) => {
        this.listOnDelete = deleteOrgChartDatas;
    };

    getOrgChartDataById = async (organizationalChartDataId) => {
        try {
            const {data} = await getOrgChartDataById(organizationalChartDataId);
            this.selectedOrgChartData = data;
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
    };

    handleDelete = (orgChartData) => {
        this.selectedOrgChartData = {... orgChartData};
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    initialOrganizationalChartData = {
        id:null ,
        name:null ,
        code:null
    }

    handleOpenCreateEdit = async (organizationalChartDataId) => {
        try {
            if (organizationalChartDataId) {
                const {data} = await getOrgChartDataById(organizationalChartDataId);
                this.selectedOrgChartData = data;
            } else {
                this.selectedOrgChartData = {
                    ... this.initialOrganizationalChartData
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
            const {data} = await deleteOrgChartData(this?.selectedOrgChartData?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingOrgChartData();

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

            await this.pagingOrgChartData();
            this.listOnDelete = [];

            this.handleClose();


        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveOrgChartData = async (orgChartData) => {
        try {
            const {data} = await saveOrgChartData(orgChartData);

            this.handleClose();

            toast.success("Thông tin sơ đồ tổ chức đã được lưu");
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");
        }
    };
}
