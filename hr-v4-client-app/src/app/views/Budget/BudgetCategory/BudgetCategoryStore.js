import {makeAutoObservable, runInAction} from "mobx";
import {
    pagingBudgetCategory,
    getBudgetCategoryById,
    deleteBudgetCategoryById,
    saveBudgetCategory,
    deleteMultiple
} from "./BudgetCategoryService"; // Changed import source to BudgetCategoryService
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";

export default class BudgetCategoryStore {
    budgetCategoryList = [];
    selectedBudgetCategory = null;
    selectedBudgetCategoryList = [];
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
            const res = await pagingBudgetCategory(searchObject); // Updated function name
            runInAction(() => {
                this.budgetCategoryList = res?.data?.content || [];
                this.totalElements = res?.data?.totalElements;
                this.totalPages = res?.data?.totalPages;
            });
            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
            toast.warning("Không thể tải dữ liệu ngân sách theo loại!");
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

    handleEditBudgetCategory = (id) => { // Updated function name
        this.getBudgetCategoryById(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleClose = (updateListOnClose) => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        if (updateListOnClose) this.updatePageData();
    };

    handleDelete = (id) => { // Updated function name
        this.getBudgetCategoryById(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            const res = await deleteBudgetCategoryById(this.selectedBudgetCategory.id); // Updated function name
            if (res?.data) {
                toast.success("Đã xoá ngân sách theo loại!");
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
        for (let i = 0; i < this.selectedBudgetCategoryList.length; i++) { // Updated list name
            listId.push(this.selectedBudgetCategoryList[i]?.id)
        }
        try {
            console.log(listId)
            await deleteMultiple(listId); // Updated function name
            toast.success("Đã xóa thành công!");
            await this.handleClose(true);
            this.handleSelectListBudgetCategory([])
        } catch (error) {
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
            await this.handleClose();
        }
    };

    getBudgetCategoryById = async (id) => { // Updated function name
        if (id != null) {
            try {
                const data = await getBudgetCategoryById(id); // Updated function name
                this.handleSelectBudgetCategory(data.data); // Updated function name
            } catch (error) {
                console.log(error);
                toast.warning("Không thể tải thông tin ngân sách theo loại!");
            }
        } else {
            this.handleSelectBudgetCategory(null); // Updated function name
        }
    };

    handleSelectBudgetCategory = (budgetCategory) => { // Updated function name
        this.selectedBudgetCategory = budgetCategory; // Updated variable name
    };

    handleSelectListBudgetCategory = (budgetCategories) => { // Updated function name
        this.selectedBudgetCategoryList = budgetCategories; // Updated variable name
        console.log(this.selectedBudgetCategoryList);
    };

    saveOrUpdateBudgetCategory = async (budgetCategory) => { // Updated function name
        try {
            const res = await saveBudgetCategory(budgetCategory); // Updated function name
            toast.success(
                budgetCategory?.id ? "Chỉnh sửa ngân sách theo loại thành công!" : "Thêm mới ngân sách theo loại thành công!"
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

    resetBudgetCategoryStore = () => { // Updated function name
        this.budgetCategoryList = [];
        this.selectedBudgetCategory = null;
        this.selectedBudgetCategoryList = [];
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
