import { makeAutoObservable, runInAction } from "mobx";
import {
    pagingEducationHistory,
    getEducationHistoryById,
    deleteEducationHistoryById,
    saveOrUpdateEducationHistory,
} from "./StaffEducationHistoryService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";

export default class StaffEducationHistoryStore {
    educationHistoryList = [];
    selectedEducationHistory = null;
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    openCreateEditPopup = false;
    currentStaffId = null;

    constructor() {
        makeAutoObservable(this);
    }

    setCurrentStaffId = (id) => {
        this.currentStaffId = id;
    };

    setSelectedEducationHistory = (eduHis) => {
        this.selectedEducationHistory = eduHis;
    };

    setOpenCreateEditPopup = () => {
        this.shouldOpenEditorDialog = true;
    };

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };

    updatePageData = (item) => {
        if (item != null) {
            this.page = 1;
            this.keyword = item.keyword;
            this.search();
        } else {
            this.search();
        }
    };

    search = async () => {
        this.loadingInitial = true;
        let searchObject = {
            keyword: this.keyword,
            pageIndex: this.page,
            pageSize: this.rowsPerPage,
        };

        if (this.currentStaffId) {
            searchObject = {
                ...searchObject,
                staff: {
                    id: this.currentStaffId,
                },
            };
        }

        try {
            const res = await pagingEducationHistory(searchObject);
            runInAction(() => {
                this.educationHistoryList = res?.data?.content || [];
                this.totalElements = res?.data?.totalElements;
                this.totalPages = res?.data?.totalPages;
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu lịch sử học tập!");
            this.setLoadingInitial(false);
        }
    };

    pagingEducationHistory = async (dto) => {
        this.loadingInitial = true;
        const searchObject = {
            keyword: this.keyword,
            pageIndex: this.page,
            pageSize: this.rowsPerPage,
            ...dto,
        };

        pagingEducationHistory(searchObject)
            .then((res) => {
                this.educationHistoryList = res?.data?.content || [];
                this.totalElements = res?.data?.totalElements;
                this.totalPages = res?.data?.totalPages;
                this.setLoadingInitial(false);
            })
            .catch((err) => {
                console.error(err);
                toast.warning("Không thể tải dữ liệu lịch sử học tập!");
                this.setLoadingInitial(false);
            });
    };

    setPage = (page) => {
        this.page = page;
        this.updatePageData();
    };

    setRowsPerPage = (event) => {
        this.rowsPerPage = event.target.value;
        this.page = 1;
        this.updatePageData();
    };

    handleChangePage = (event, newPage) => {
        this.setPage(newPage);
    };

    handleEditEducationHistory = (id) => {
        this.getEducationHistoryById(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleClose = (state) => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        if (state) this.updatePageData();
    };

    handleDelete = (id) => {
        this.getEducationHistoryById(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };
    handleConfirmDelete = async () => {
        try {
            const res = await deleteEducationHistoryById(this.selectedEducationHistory.id);
            if (res?.data) {
                toast.success("Đã xoá lịch sử học tập thành công!");
            } else {
                toast.warning("Không thể xoá vì có dữ liệu ràng buộc!");
            }
            this.handleClose(true);
        } catch (error) {
            console.log(error);
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
        }
    };

    getEducationHistoryById = async (id) => {
        if (id != null) {
            try {
                const data = await getEducationHistoryById(id);
                this.handleSelectEducationHistory(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải thông tin lịch sử học tập!");
            }
        } else {
            this.handleSelectEducationHistory(null);
        }
    };
    handleSelectEducationHistory = (educationHistory) => {
        this.selectedEducationHistory = educationHistory;
    };

    saveOrUpdateEducationHistory = async (educationHistory) => {
        try {
            console.log(educationHistory)
            const res = await saveOrUpdateEducationHistory(educationHistory);
            toast.success(
                educationHistory?.id ? "Chỉnh sửa lịch sử học tập thành công!" : "Thêm mới lịch sử học tập thành công!"
            );
            // await this.search();
            this.handleClose(true);
            return res?.data;
        } catch (error) {
            console.log(error);
            toast.warning("Có lỗi xảy ra khi thêm mới lịch sử học tập!");
        }
    };

    resetEducationHistoryStore = () => {
        this.educationHistoryList = [];
        this.selectedEducationHistory = null;
        this.totalElements = 0;
        this.totalPages = 0;
        this.page = 1;
        this.rowsPerPage = 10;
        this.keyword = "";
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.currentStaffId = null;
    };
}
