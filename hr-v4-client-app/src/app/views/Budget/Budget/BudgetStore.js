import {makeAutoObservable, runInAction} from "mobx";
import {
    pagingBudget,
    getBudgetById,
    deleteBudgetById,
    saveBudget, getBudgetSummaryBalance, deleteMultiple,
} from "./BudgetService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "../../../../i18n";

export default class BudgetStore {
    budgetList = [];
    selectedBudget = null;
    selectBudgetSummaryBalance = null;
    selectedBudgetList = [];
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;

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
            const res = await pagingBudget(searchObject);
            runInAction(() => {
                this.budgetList = res?.data?.content || [];
                this.totalElements = res?.data?.totalElements;
                this.totalPages = res?.data?.totalPages;
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu ngân sách!");
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

    handleEditBudget = (id) => {
        this.getBudgetById(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleClose = (updateListOnClose) => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        if (updateListOnClose) this.updatePageData();
    };

    handleDelete = (id) => {
        this.getBudgetById(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            const res = await deleteBudgetById(this.selectedBudget.id);
            if (res?.data) {
                toast.success("Đã xoá ngân sách!");
            } else {
                toast.warning("Không thể xoá vì có dữ liệu ràng buộc!");
            }
            this.handleClose(true);
        } catch (error) {
            console.log(error);
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
        }
    };

    handleConfirmDeleteList = async () => {
        let listId = [];
        for (let i = 0; i < this.selectedBudgetList.length; i++) {
            listId.push(this.selectedBudgetList[i]?.id)
        }
        try {
            await deleteMultiple(listId);
            toast.success("Đã xóa thành công!");
            await this.handleClose(true);
            this.handleSelectListBudget([])
        } catch (error) {
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
            await this.handleClose();
        }
    };

    getBudgetById = async (id) => {
        if (id != null) {
            try {
                const data = await getBudgetById(id);
                this.handleSelectBudget(data.data);
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải thông tin ngân sách!");
            }
        } else {
            this.handleSelectBudget(null);
        }
    };
    getBudgetSummaryBalance = async (searchObject) => {
        if (searchObject?.budget != null && searchObject?.fromDate != null && searchObject?.toDate != null) {
            try {
                const data = await getBudgetSummaryBalance(searchObject);
                this.selectBudgetSummaryBalance = data.data;
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải thông tin!");
            }
        } else {
            this.selectBudgetSummaryBalance = null;
        }
    };

    handleSelectBudget = (budget) => {
        this.selectedBudget = budget;
    };

    handleSelectListBudget = (budgets) => {
        this.selectedBudgetList = budgets;
        console.log(this.selectedBudgetList);
    };

    saveOrUpdateBudget = async (budget) => {
        try {
            const res = await saveBudget(budget);
            toast.success(
                budget?.id ? "Chỉnh sửa ngân sách thành công!" : "Thêm mới ngân sách thành công!"
            );
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

    resetBudgetStore = () => {
        this.budgetList = [];
        this.selectedBudget = null;
        this.selectedBudgetList = [];
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
