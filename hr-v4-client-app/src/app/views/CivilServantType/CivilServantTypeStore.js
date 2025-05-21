import {makeAutoObservable} from "mobx";
import {
    pagingCivilServantTypes,
    getCivilServantType,
    createCivilServantType,
    editCivilServantType,
    deleteCivilServantType,
    checkCode,
    checkName,
} from "./CivilServantTypeService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
// import { withTranslation, WithTranslation } from 'react-i18next';

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class CivilServantTypeStore {
    civilServantTypeList = [];
    selectedCivilServantType = null;
    selectedCivilServantTypeList = [];
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

    resetCivilServantTypeStore = () => {
        this.civilServantTypeList = [];
        this.selectedCivilServantType = null;
        this.selectedCivilServantTypeList = [];
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
            let data = await pagingCivilServantTypes(searchObject);
            this.civilServantTypeList = data.data.content;
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

    handleEditCivilServantType = (id) => {
        this.getCivilServantType(id).then(() => {
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
        this.getCivilServantType(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteCivilServantType(this.selectedCivilServantType.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedCivilServantTypeList.length; i++) {
            try {
                await deleteCivilServantType(this.selectedCivilServantTypeList[i].id);
            } catch (error) {
                listAlert.push(this.selectedCivilServantTypeList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getCivilServantType = async (id) => {
        if (id != null) {
            try {
                let data = await getCivilServantType(id);
                this.handleSelectCivilServantType(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectCivilServantType(null);
        }
    };

    handleSelectCivilServantType = (civilServantType) => {
        this.selectedCivilServantType = civilServantType;
    };

    handleSelectListCivilServantType = (civilServantTypes) => {
        this.selectedCivilServantTypeList = civilServantTypes;
        console.log(this.selectedCivilServantTypeList);
    };

    createCivilServantType = async (civilServantType) => {
        try {
            let response = await checkCode(
                civilServantType.id,
                civilServantType.code
            );
            let response2 = await checkName(
                civilServantType.id,
                civilServantType.name
            );
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else if (response2.data) {
                toast.warning(i18n.t("toast.duplicate_name"));
            } else {
                await createCivilServantType(civilServantType);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editCivilServantType = async (civilServantType) => {
        try {
            let response = await checkCode(
                civilServantType.id,
                civilServantType.code
            );
            let response2 = await checkName(
                civilServantType.id,
                civilServantType.name
            );
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else if (response2.data) {
                toast.warning(i18n.t("toast.duplicate_name"));
            } else {
                await editCivilServantType(civilServantType);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };
}
