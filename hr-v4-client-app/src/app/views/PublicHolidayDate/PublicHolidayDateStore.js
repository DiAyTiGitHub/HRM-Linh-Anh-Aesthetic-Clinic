import { makeAutoObservable } from "mobx";
import {
    pagingPublicHolidayDate,
    getPublicHolidayDateById,
    saveOrUpdatePublicHolidayDate,
    deletePublicHolidayDate,
    createPublicHolidayDateAutomatic,
    deleteMultiple
} from "./PublicHolidayDateService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { SalaryItem } from "../../common/Model/Salary/SalaryItem";
import { PublicHolidayDate } from "app/common/Model/HumanResource/PublicHolidayDate";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class PublicHolidayDateStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        //keyword: null,
        holidayType: null,
        fromDate: null,
        toDate: null
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    publicHolidayDateList = [];
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
    loadingInitial = false;

    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    openCreateEditPopup = false;
    openPopupAutomatic = false;
    listOnDelete = [];

    selectedPublicHolidayDate = null;
    selectedPublicHolidayDateList = [];
    openViewPopup = false;

    constructor() {
        makeAutoObservable(this);
    }

    handleOpenView = async (id) => {
        try {
            if (id) {
                const { data } = await getPublicHolidayDateById(id);
                this.selectedPublicHolidayDate = data;
            } else {
                this.selectedPublicHolidayDate = {
                    ... new PublicHolidayDate()
                };
            }
            this.openViewPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };


    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.publicHolidayDateList = [];
        this.openCreateEditPopup = false;
        this.selectedPublicHolidayDate = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openPopupAutomatic = false;
        this.openViewPopup = false;
    };

    handleSetSearchObject = (searchObject) => {
        this.searchObject = { ...searchObject };
    };

    pagingPublicHolidayDate = async () => {
        try {
            const payload = {
                ... this.searchObject
            };
            const data = await pagingPublicHolidayDate(payload);
            this.publicHolidayDateList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setSearchObject = async (searchObj) => {
        this.searchObject = searchObj;
    }

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.pagingPublicHolidayDate();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.pagingPublicHolidayDate();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleOpenCreateEdit = async (id) => {
        try {
            if (id) {
                const { data } = await getPublicHolidayDateById(id);
                this.selectedPublicHolidayDate = data;
            } else {
                this.selectedPublicHolidayDate = {
                    ... new PublicHolidayDate()
                };
            }
            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleOpenPopupAutomatic = async () => {
        this.selectedPublicHolidayDate = {
            ... new PublicHolidayDate()
        }
        this.openPopupAutomatic = true;
    };

    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;
        this.openConfirmDeleteListPopup = false;
        this.openViewPopup = false;
    };

    handleClosePopupAutomatic = () => {
        this.openPopupAutomatic = false;
    };


    handleDelete = (publicHolidayDate) => {
        this.selectedPublicHolidayDate = { ...publicHolidayDate };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleConfirmDelete = async () => {
        try {
            const { data } = await deletePublicHolidayDate(this.selectedPublicHolidayDate.id);
            toast.success(i18n.t("toast.delete_success"));
            await this.pagingPublicHolidayDate();
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleSelectListDelete = (publicHolidayDates) => {
        this.listOnDelete = publicHolidayDates;
    };

    savePublicHolidayDate = async (publicHolidayDate) => {
        try {
            const { data } = await saveOrUpdatePublicHolidayDate(publicHolidayDate);
            toast.success("Thông tin ngày nghỉ đã được lưu");
            this.handleClose();
        } catch (error) {
            toast.error(i18n.t("toast.error"));
        }
    };

    getPublicHolidayDate = async (id) => {
        if (id != null) {
            try {
                const { data } = await getPublicHolidayDateById(id);
                this.selectedPublicHolidayDate = data;
                this.openCreateEditPopup = true;
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.selectedPublicHolidayDate = null;
        }
    };

    handleConfirmDeleteList = async () => {
        try {
            const deleteData = [];

            for (let i = 0; i < this?.listOnDelete?.length; i++) {
                deleteData.push(this?.listOnDelete[i]?.id);
            }
            await deleteMultiple(deleteData);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingPublicHolidayDate();
            this.listOnDelete = [];

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    createPublicHolidayDateAutomatic = async (publicHolidayDate) => {
        try {
            const { data } = await createPublicHolidayDateAutomatic(publicHolidayDate);
            toast.success("Thông tin  được lưu");
            this.handleClosePopupAutomatic();
        } catch (error) {
            toast.error(i18n.t("toast.error"));
        }
    };

}
