import { makeAutoObservable } from "mobx";
import { toast } from "react-toastify";
import i18n from "i18n";
import {
    deleteStaffMaternityHistory,
    getStaffMaternityHistoryById,
    pagingStaffMaternityHistory,
    saveOrUpdateStaffMaternityHistory,
} from "app/services/StaffMaternityHistoryService";
import { StaffMaternityHistory } from "app/common/Model/HumanResource/StaffMaternityHistory";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class StaffMaternityHistoryStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null,
        staffId: null,
        fromDate: null,
        toDate: null,
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    initialStaffMaternityHistory = {
        ... new StaffMaternityHistory()
    };

    staffMaternityHistoryList = [];
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
    loadingInitial = false;

    openConfirmDeletePopup = false;
    openCreateEditPopup = false;
    listOnDelete = [];

    selectedStaffMaternityHistory = null;
    selectedStaffMaternityHistoryList = [];
    currentStaffId = null;

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.staffMaternityHistoryList = [];
        this.openCreateEditPopup = false;
        this.selectedStaffMaternityHistory = null;
        this.openConfirmDeletePopup = false;
        this.listOnDelete = [];
    };

    setCurrentStaffId = (staffId) => {
        this.currentStaffId = staffId;
    };

    handleOpenCreateEdit = async (staffMaternityHistoryId) => {
        try {
            if (staffMaternityHistoryId) {
                const { data } = await getStaffMaternityHistoryById(staffMaternityHistoryId);
                this.selectedStaffMaternityHistory = data;
            } else {
                this.selectedStaffMaternityHistory = {
                    ...this.initialStaffMaternityHistory,
                };
            }
            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    pagingStaffMaternityHistory = async () => {
        const payload = { ...this.searchObject };
        if (this.currentStaffId) {
            payload.staffId = this.currentStaffId;
        }
        pagingStaffMaternityHistory(payload)
            .then((data) => {
                this.staffMaternityHistoryList = data.data.content;
                this.totalElements = data.data.totalElements;
                this.totalPages = data.data.totalPages;
            })
            .catch((err) => {
                console.error(err);
                toast.error(i18n.t("toast.error"));
            });
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.pagingStaffMaternityHistory();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.pagingStaffMaternityHistory();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;
        this.pagingStaffMaternityHistory();
    };

    handleDelete = (staffMaternityHistory) => {
        this.selectedStaffMaternityHistory = { ...staffMaternityHistory };
        this.openConfirmDeletePopup = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteStaffMaternityHistory(this.selectedStaffMaternityHistory.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleSelectListDelete = (staffMaternityHistories) => {
        this.listOnDelete = staffMaternityHistories;
    };

    saveStaffMaternityHistory = async (staffMaternityHistory) => {
        try {
            await saveOrUpdateStaffMaternityHistory(staffMaternityHistory);
            toast.success("Thông tin lịch sử thai sản đã được lưu");
            this.handleClose();
        } catch (error) {
            toast.error(i18n.t("toast.error"));
        }
    };

    getStaffMaternityHistory = async (id) => {
        if (id != null) {
            try {
                const { data } = await getStaffMaternityHistoryById(id);
                this.selectedStaffMaternityHistory = data;
                this.openCreateEditPopup = true;
            } catch (error) {
                console.error(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.selectedStaffMaternityHistory = null;
        }
    };

    handleSetSearchObject = (searchObject) => {
        if (!searchObject.staff) {
            searchObject.staffId = null;
        } else {
            searchObject.staffId = searchObject.staff.id;
        }
        this.searchObject = { ...searchObject };
    };

    setOpenCreateEditPopup = (value) => {
        this.openCreateEditPopup = value;
    };

    setSelectedStaffMaternityHistory = (data) => {
        this.selectedStaffMaternityHistory = { ...this.initialStaffMaternityHistory, ...data };
    };
}
