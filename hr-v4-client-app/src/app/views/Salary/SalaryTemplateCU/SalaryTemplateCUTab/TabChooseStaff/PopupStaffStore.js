import {makeAutoObservable} from "mobx";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
import {pagingStaff} from "../../../../HumanResourcesInformation/StaffService";

toast.configure({
    autoClose: 2000, draggable: false, limit: 3,
});

export default class PopupStaffStore {
    intactSearchObject = {
        pageIndex: 1, pageSize: 10, keyword: null, staff: null, position: null, department: null, organization: null,
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    staffList = [];
    selectedStaffs = [];
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    openCreateEditPopup = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.staffList = [];
        this.selectedStaffs = [];
        this.totalPages = 0;
        this.loadingInitial = false;
        this.openCreateEditPopup = false;
    };

    pagingStaff = async () => {
        try {
            const payload = {
                ...this.searchObject,
                staffId: this.searchObject?.staff?.id || null,
                positionId: this.searchObject?.position?.id || null,
                departmentId: this.searchObject?.department?.id || null,
                organizationId: this.searchObject?.organization?.id || null,
                staff: null,
                position: null,
                department: null,
                organization: null,
            };
            const data = await pagingStaff(payload);
            this.staffList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.pagingStaff();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.pagingStaff();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };
    handleClose = () => {
        this.openCreateEditPopup = false;
    };

    handleOpen = () => {
        this.openCreateEditPopup = true;
    }

    handleSetSearchObject = (searchObject) => {
        this.searchObject = {...searchObject};
    };
}
