import {makeAutoObservable} from "mobx";
import {
    pagingGrade,
    getGrade,
    saveGrade,
    deleteGrade,
    editGrade,
    checkCode,
} from "./GradeService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
// import { withTranslation, WithTranslation } from 'react-i18next';

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class GradeStore {
    gradeList = [];
    selectedGrade = null;
    selectedGradeList = [];
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

    resetGradeStore = () => {
        this.gradeList = [];
        this.selectedGrade = null;
        this.selectedGradeList = [];
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
        var searchObject = {};
        searchObject.keyword = this.keyword;
        searchObject.pageIndex = this.page;
        searchObject.pageSize = this.rowsPerPage;

        try {
            let data = await pagingGrade(searchObject);
            this.gradeList = data.data.content;
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

    handleEditGrade = (id) => {
        this.getGrade(id).then(() => {
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
        this.getGrade(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        // this.deleteGrade(this.selectedGrade.id);
        try {
            await deleteGrade(this.selectedGrade.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedGradeList.length; i++) {
            try {
                await deleteGrade(this.selectedGradeList[i].id);
            } catch (error) {
                listAlert.push(this.selectedGradeList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getGrade = async (id) => {
        if (id != null) {
            try {
                let data = await getGrade(id);
                this.handleSelectGrade(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectGrade(null);
        }
    };

    handleSelectGrade = (position) => {
        this.selectedGrade = position;
    };

    handleSelectListGrade = (position) => {
        this.selectedGradeList = position;
    };

    createGrade = async (position) => {
        try {
            let responseCheckCode = await checkCode(position.id, position.code);
            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await saveGrade(position);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editGrade = async (position) => {
        try {
            let responseCheckCode = await checkCode(position.id, position.code);
            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editGrade(position);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    // deleteGrade = async (id) => {
    //   try {
    //     await deleteGrade(id);
    //     toast.success("toast.delete_success");
    //     this.handleClose();
    //   } catch (error) {
    //     console.log(error);
    //     toast.warning("toast.delete_fail");
    //   }
    // };
}
