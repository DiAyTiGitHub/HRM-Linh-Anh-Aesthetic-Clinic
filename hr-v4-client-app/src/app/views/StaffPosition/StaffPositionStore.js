import {makeAutoObservable} from "mobx";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
import {
    deleteStaffPositionById,
    getStaffPositionById,
    pagingStaffPosition,
    saveStaffPosition
} from "./StaffPositionService";

toast.configure({
    autoClose: 2000, draggable: false, limit: 3,
});

export default class StaffPositionStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null,
        staff: null,
        position: null,
        department: null,
        supervisor: null,
        relationshipType: null
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    initialStaffPosition = {
        staff: null,
        position: null,
        department: null,
        fromDate: new Date(),
        toDate: null,
        supervisor: null
    };

    staffPositionList = [];
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    openCreateEditPopup = false;
    selectedStaffPosition = null;

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.staffPositionList = [];
        this.totalElements = 0;
        this.totalPages = 0;
        this.loadingInitial = false;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.openCreateEditPopup = false;
        this.selectedStaffPosition = null;
    };

    pagingStaffPosition = async () => {
        try {
            const payload = {
                ...this.searchObject,
                staff: null,
                position: null,
                department: null,
                supervisor: null,
            };
            const data = await pagingStaffPosition(payload);
            this.staffPositionList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.pagingStaffPosition();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.pagingStaffPosition();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };
    handleOpenCreateEdit = async (staffPositionId) => {
        try {
            if (staffPositionId) {
                const {data} = await getStaffPositionById(staffPositionId);
                console.log(data)
                this.selectedStaffPosition = data;
            } else {
                this.selectedStaffPosition = this.initialStaffPosition
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

    handleDelete = (staffPosition) => {
        this.selectedStaffPosition = staffPosition;
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteStaffPositionById(this.selectedStaffPosition?.id);
            toast.success(i18n.t("toast.delete_success"));
            await this.pagingStaffPosition();
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleSelectStaffPosition = (staffPosition) => {
        this.selectedStaffPosition = staffPosition;
    };

    saveStaffPosition = async (staffPosition) => {
        try {
            await saveStaffPosition(staffPosition);
            toast.success("Thông tin chính sách phụ cấp đã được lưu");
            this.pagingStaffPosition()
            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
            throw new Error(i18n.t("toast.error"));
        }
    };
    handleSetSearchObject = (searchObject) => {
        this.searchObject = {...searchObject};
    };
}
