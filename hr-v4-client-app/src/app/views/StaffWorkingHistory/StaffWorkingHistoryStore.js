import { makeAutoObservable } from "mobx";
import {
    pagingStaffWorkingHistory,
    getStaffWorkingHistoryById,
    saveStaffWorkingHistory,
    deleteStaffWorkingHistory,
} from "./StaffWorkingHistoryService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class StaffWorkingHistoryStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 999,
        keyword: null,
        workingHistory: null,
        workingHistoryId: null,
        staff: null,
        staffId: null,
        startDate: null,
        endDate: null,
        fromOrganization: null,
        fromDepartment: null,
        fromPosition: null,
        toOrganization: null,
        toDepartment: null,
        toPosition: null,
    };
    searchObject = JSON.parse(JSON.stringify({ ...this.intactSearchObject }));

    initialStaffWorkingHistory = {
        id: null,
        name: null,
        code: null,
        description: null,
        staff: null,
        position: null,
        department: null,
        startDate: new Date(),
        endDate: null,
    };

    staffWorkingHistoryList = [];
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
    loadingInitial = false;
    currentStaffId = null;

    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    openCreateEditPopup = false;
    listOnDelete = [];
    isAdmin = false;
    selectedStaffWorkingHistory = null;
    selectedStaffWorkingHistoryList = [];
    typeOfForm = null;

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.staffWorkingHistoryList = [];
        this.openCreateEditPopup = false;
        this.selectedStaffWorkingHistory = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.currentStaffId = null;
        this.selectedStaffWorkingHistory = null;
        this.typeOfForm = null;
    };

    setCurrentStaffId = (id) => {
        this.currentStaffId = id;
    };

    setOpenCreateEditPopup = () => {
        this.openCreateEditPopup = true;
    };

    pagingStaffWorkingHistory = async (dto) => {
        try {
            const payload = {
                ...this.searchObject,
                ...dto,
            };
            if (this.currentStaffId) {
                payload.staffId = this.currentStaffId;
            }
            const data = await pagingStaffWorkingHistory(payload);
            this.staffWorkingHistoryList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.pagingStaffWorkingHistory();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.pagingStaffWorkingHistory();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleOpenCreateEdit = async (workingHistoryId, typeOfForm) => {
        /// typeOfForm = 1, 2: điều chuyển , 3: Tạm nghỉ
        this.typeOfForm = typeOfForm;
        try {
            if (workingHistoryId) {
                const { data } = await getStaffWorkingHistoryById(workingHistoryId);
                this.selectedStaffWorkingHistory = data;
                this.typeOfForm = data?.transferType;
            } else {
                this.selectedStaffWorkingHistory = {
                    ...this.initialStaffWorkingHistory,
                };
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

        this.pagingStaffWorkingHistory();
    };

    handleDelete = (workingHistory) => {
        console.log(workingHistory);
        this.selectedStaffWorkingHistory = { ...workingHistory };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleConfirmDelete = async () => {
        try {
            console.log(this.selectedStaffWorkingHistory);
            const { data } = await deleteStaffWorkingHistory(this.selectedStaffWorkingHistory.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleSelectListDelete = (workingHistory) => {
        this.listOnDelete = workingHistory;
    };
    saveStaffWorkingHistory = async (workingHistory) => {
        try {
            const result = await this.onlySave(workingHistory);
            this.handleClose(); // Đóng form nếu lưu thành công
            return result; // Trả về kết quả nếu cần
        } catch (err) {
            // Không đóng form nếu có lỗi, lỗi đã được xử lý trong onlySave
        }
    };

    onlySave = (workingHistory) => {
        return saveStaffWorkingHistory(workingHistory)
            .then((data) => {
                // Kiểm tra nếu có lỗi từ server (nằm trong note)
                if (data?.data?.note?.startsWith("ERROR:")) {
                    toast.error(data?.data?.note); // Hiển thị lỗi từ server
                    return;
                    // throw new Error("Lỗi từ server"); // Ném lỗi để ngăn đóng form
                }
                toast.success("Thông tin lịch sử làm việc của nhân viên đã được lưu");
                return data; // Trả về dữ liệu để chuỗi promise tiếp tục
            })
            .catch((err) => {
                console.error(err);
                toast.error(i18n.t("toast.error"));
                throw err; // Ném lại lỗi để thông báo lên cấp trên
            });
    };
    getStaffWorkingHistory = async (id) => {
        if (id != null) {
            try {
                const { data } = await getStaffWorkingHistoryById(id);
                this.selectedStaffWorkingHistory = data;
                this.openCreateEditPopup = true;
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectWorkingHistory(null);
        }
    };

    handleSetSearchObject = (searchObject) => {
        if (searchObject.workingHistory == null) {
            searchObject.workingHistoryId = null;
        } else {
            searchObject.workingHistoryId = searchObject.workingHistory.id;
        }
        this.searchObject = { ...searchObject };
    };
}
