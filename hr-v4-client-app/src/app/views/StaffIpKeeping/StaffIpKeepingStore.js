import {makeAutoObservable} from "mobx";

import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
import {
    enableExternalIpTimekeeping, disableExternalIpTimekeeping, pagingStaff
} from "app/views/HumanResourcesInformation/StaffService"

toast.configure({
    autoClose: 2000, draggable: false, limit: 3,
});

export default class StaffIpKeepingStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null,
        department: null,
        allowExternalIpTimekeeping: true,
        organization: null,
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    listStaffIpKeeping = [];
    openCreateEditPopup = false;
    selectedStaffIpKeeping = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    selectedListStaffIpKeeping = [];

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.listStaffIpKeeping = [];
        this.openCreateEditPopup = false;
        this.selectedStaffIpKeeping = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.selectedListStaffIpKeeping = [];
    }

    handleSetSearchObject = (searchObject) => {
        this.searchObject = {...searchObject};
    }

    pagingStaffIpKeeping = async () => {
        try {
            const payload = {
                ...this.searchObject,
                organizationId: this.searchObject?.organization?.id,
                departmentId: this.searchObject?.department?.id,
                allowExternalIpTimekeeping: true,
                department: null,
                organization: null,
            };
            const data = await pagingStaff(payload);

            this.listStaffIpKeeping = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingStaffIpKeeping();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingStaffIpKeeping();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectedList = (list) => {
        this.selectedListStaffIpKeeping = [...list];
    };

    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;
        this.openConfirmDeleteListPopup = false;
    };

    handleDelete = (obj) => {
        this.selectedStaffIpKeeping = {...obj};
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleOpenCreateEdit = async () => {
        this.openCreateEditPopup = true;
    };

    handleConfirmDelete = async () => {
        try {
            const staffList = [this.selectedStaffIpKeeping];

            await disableExternalIpTimekeeping(staffList);

            toast.success(i18n.t("toast.delete_success"));
            await this.pagingStaffIpKeeping();
            this.handleClose();

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };


    handleConfirmDeleteList = async () => {
        try {

            await disableExternalIpTimekeeping(this.selectedListStaffIpKeeping);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingStaffIpKeeping();
            this.selectedListStaffIpKeeping = [];

            this.handleClose();


        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };


    saveExternalIpTimekeeping = async (values) => {
        try {
            await enableExternalIpTimekeeping(values?.selectedListStaffIpKeeping);
            toast.success("Thêm mới nhân viên chấm công ngoài công ty thành công");

            await this.pagingStaffIpKeeping();
            this.handleClose();

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

}
