import {makeAutoObservable, runInAction} from "mobx";
import {
    pagingKPI,
    getKPIById,
    deleteKPIById,
    saveOrUpdate,
} from "./KPIService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "../../../../i18n";

export default class KPIStore {
    kpiList = [];
    selectedKpi = null;
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
            const res = await pagingKPI(searchObject);
            runInAction(() => {
                this.kpiList = res?.data?.content || [];
                this.totalElements = res?.data?.totalElements;
                this.totalPages = res?.data?.totalPages;
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu của KPI!");
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

    handleEditKPI = (id) => {
        this.getKPIById(id).then(() => {
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
        this.getKPIById(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };
    handleConfirmDelete = async () => {
        try {
            const res = await deleteKPIById(this.selectedKpi.id);
            if (res?.data) {
                toast.success("Đã xoá KPI thành công!");
            } else {
                toast.warning("Không thể xoá vì có dữ liệu ràng buộc!");
            }
            await this.search();
            this.handleClose(true);
        } catch (error) {
            console.log(error);
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
        }
    };

    getKPIById = async (id) => {
        if (id != null) {
            try {
                const data = await getKPIById(id);
                this.handleSelectKpi(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải thông tin KPI!");
            }
        } else {
            this.handleSelectKpi(null);
        }
    };
    handleSelectKpi = (kpi) => {
        this.selectedKpi = kpi;
    };

    saveOrUpdateKpi = async (kpi) => {
        try {
            const res = await saveOrUpdate(kpi);
            toast.success(
                kpi?.id ? "Chỉnh sửa KPI thành công!" : "Thêm mới KPI thành công!"
            );
            await this.search();
            this.handleClose(true);
            return res?.data;
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error(i18n.t("toast.duplicate_code"), {
                    autoClose: 5000,
                    draggable: false,
                    limit: 5,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }
        }
    };

    resetKpiStore = () => {
        this.kpiList = [];
        this.selectedKpi = null;
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
