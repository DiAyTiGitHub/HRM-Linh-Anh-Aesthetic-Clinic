import {makeAutoObservable} from "mobx";
import {
    pagingCategory,
    getCategory,
    createCategory,
    editCategory,
    deleteCategory,
    checkCode,
    checkName,
} from "./CivilServantCategoryService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
// import { withTranslation, WithTranslation } from 'react-i18next';

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class CivilServantCategoryStore {
    categoryList = [];
    selectedItem = null;
    selectedItemList = [];
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

    resetCivilServantCategoryStore = () => {
        this.categoryList = [];
        this.selectedItem = null;
        this.selectedItemList = [];
        this.totalElements = 0;
        this.totalPages = 0;
        this.page = 1;
        this.rowsPerPage = 10;
        this.keyword = "";
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
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
        var searchObject = {
            keyword: this.keyword,
            pageIndex: this.page,
            pageSize: this.rowsPerPage,
        };

        try {
            let data = await pagingCategory(searchObject);
            this.categoryList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            this.setLoadingInitial(false);
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
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

    handleEditCategory = (id) => {
        this.getCategory(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleClose = () => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.updatePageData();
    };

    handleDelete = (id) => {
        this.getCategory(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        // this.deleteCategory(this.selectedItem.id);
        try {
            await deleteCategory(this.selectedItem.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedItemList.length; i++) {
            try {
                await deleteCategory(this.selectedItemList[i].id);
            } catch (error) {
                listAlert.push(this.selectedItemList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getCategory = async (id) => {
        if (id != null) {
            try {
                let data = await getCategory(id);
                this.handleSelectItem(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectItem(null);
        }
    };

    handleSelectItem = (item) => {
        this.selectedItem = item;
    };

    handleSelectListItem = (item) => {
        this.selectedItemList = item;
        console.log(this.selectedItemList);
    };

    createCategory = async (item) => {
        try {
            let responseCheckName = await checkName(item.id, item.name);
            let responseCheckCode = await checkCode(item.id, item.code);

            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else if (responseCheckName.data) {
                toast.warning(i18n.t("toast.duplicate_name"));
            } else {
                await createCategory(item);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editCategory = async (item) => {
        console.log(item.name + " id:" + item.id);
        try {
            let responseCheckCode = await checkCode(item.id, item.code);
            let responseCheckName = await checkName(item.id, item.name);
            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else if (responseCheckName.data) {
                toast.warning(i18n.t("toast.duplicate_name"));
            } else {
                await editCategory(item);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };
}
