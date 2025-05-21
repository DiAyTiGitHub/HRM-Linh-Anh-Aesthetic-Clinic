import {makeAutoObservable, runInAction} from "mobx";
import {
    pagingKPIResult,
    getKPIResultById,
    deleteKPIResultById,
    saveOrUpdate,
} from "./KPIResultService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";

export default class KPIResultStore {
    kpiResultList = [];
    selectedKpiResult = null;
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

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
        const searchObject = {
            keyword: this.keyword,
            pageIndex: this.page,
            pageSize: this.rowsPerPage,
        };

        try {
            const res = await pagingKPIResult(searchObject);
            runInAction(() => {
                this.kpiResultList = res?.data?.content || [];
                this.totalElements = res?.data?.totalElements;
                this.totalPages = res?.data?.totalPages;
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải kết quả KPI!");
            this.setLoadingInitial(false);
        }
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

    handleEditKPIResult = (id) => {
        this.getKPIResultById(id).then(() => {
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
        this.getKPIResultById(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };
    handleConfirmDelete = async () => {
        try {
            const res = await deleteKPIResultById(this.selectedKpiResult.id);
            if (res?.data) {
                toast.success("Đã xoá kết quả KPI!");
                await this.search();
            } else {
                toast.warning("Không thể xoá vì có dữ liệu ràng buộc!");
            }
            this.handleClose(true);
        } catch (error) {
            console.log(error);
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
        }
    };

    getKPIResultById = async (id) => {
        if (id != null) {
            try {
                const data = await getKPIResultById(id);
                this.handleSelectKPIResult(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải thông tin kết quả KPI!");
            }
        } else {
            this.handleSelectKPIResult(null);
        }
    };
    handleSelectKPIResult = (kpiResult) => {
        this.selectedKpiResult = kpiResult;
    };

    saveOrUpdateKPIResult = async (kpiResult) => {
        try {
            const res = await saveOrUpdate(kpiResult);
            toast.success(
                kpiResult?.id ? "Chỉnh sửa kết quả KPI thành công!" : "Thêm mới kết quả KPI thành công!"
            );
            await this.search();
            this.handleClose(true);
            return res?.data;
        } catch (error) {
            console.log(error);
            toast.warning("Có lỗi xảy ra khi thêm mới KPI Result!");
        }
    };

    resetKpiStore = () => {
        this.kpiResultList = [];
        this.selectedKpiResult = null;
        this.totalElements = 0;
        this.totalPages = 0;
        this.page = 1;
        this.rowsPerPage = 10;
        this.keyword = "";
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
    };
}
