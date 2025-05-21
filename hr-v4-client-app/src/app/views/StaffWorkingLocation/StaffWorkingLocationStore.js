import { makeAutoObservable } from "mobx";
import {
    pagingStaffWorkingLocation,
    getStaffWorkingLocationById,
    saveStaffWorkingLocation,
    deleteStaffWorkingLocationById,
    deleteMultipleStaffWorkingLocation
} from "./StaffWorkingLocationService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { StaffWorkingLocation } from "app/common/Model/HumanResource/StaffWorkingLocation";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class StaffWorkingLocationStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null,
        staff: null,
        staffId: null
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));


    totalElements = 0;
    totalPages = 0;
    rowsPerPage = 10;
    keyword = "";
    loadingInitial = false;
    staffWorkingLocationList = [];

    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    openCreateEditPopup = false;
    listOnDelete = [];
    isAdmin = false;
    selectedWorkingLocation = null;
    selectedWorkingLocationList = [];

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.staffWorkingLocationList = [];
        this.openCreateEditPopup = false;
        this.selectedWorkingLocation = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
    };

    pagingStaffWorkingLocation = async (staffId) => {
        try {
            const payload = {
                ...this.searchObject,
                staffId: staffId
            };
            const data = await pagingStaffWorkingLocation(payload);
            this.staffWorkingLocationList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.pagingStaffWorkingLocation();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.pagingStaffWorkingLocation();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleOpenCreateEdit = async (staffWorkingLocationId) => {
        try {
            if (staffWorkingLocationId) {
                const { data } = await getStaffWorkingLocationById(staffWorkingLocationId);
                this.selectedWorkingLocation = data;
            } else {
                this.selectedWorkingLocation = new StaffWorkingLocation();
            }
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

    handleDelete = (staffWorkingLocation) => {
        this.selectedWorkingLocation = { ...staffWorkingLocation };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleConfirmDelete = async () => {
        try {
            const { data } = await deleteStaffWorkingLocationById(this.selectedWorkingLocation.id);
            toast.success(i18n.t("toast.delete_success"));
            await this.pagingStaffWorkingLocation();
            this.handleClose();

            return data;
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        try {
            const deleteData = [];

            for (let i = 0; i < this?.listOnDelete?.length; i++) {
                deleteData.push(this?.listOnDelete[i]?.id);
            }
            await deleteMultipleStaffWorkingLocation(deleteData);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingStaffWorkingLocation();
            this.listOnDelete = [];

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleSelectListDelete = (staffWorkingLocation) => {
        this.listOnDelete = staffWorkingLocation;
    };

    saveStaffWorkingLocation = async (staffWorkingLocation) => {
        try {
            const { data } = await saveStaffWorkingLocation(staffWorkingLocation);
            toast.success("Thông tin Địa điểm làm việc của nhân viên đã được lưu");
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
            throw new Error(i18n.t("toast.error"));
        }
    };

    getStaffWorkingLocationById = async (id) => {
        try {
            const { data } = await getStaffWorkingLocationById(id);
            this.selectedWorkingLocation = data;
            this.openCreateEditPopup = true;
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleSetSearchObject = (searchObject) => {
        if (searchObject.staff == null) {
            searchObject.staffId = null;
        }
        else {
            searchObject.staffId = searchObject.staff.id;
        }
        this.searchObject = { ...searchObject };
    };

    setOpenCreateEditPopup = (value) => {
        this.openCreateEditPopup = value;
    };

    setSelectedWorkingLocation = (data) => {
        this.selectedWorkingLocation = {
            ...new StaffWorkingLocation(), ...data
        };
    };
}
