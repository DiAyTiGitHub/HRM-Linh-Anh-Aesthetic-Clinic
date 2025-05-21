import {makeAutoObservable} from "mobx";
import {
    pagingOtherLanguages,
    getOtherLanguage,
    createOtherLanguage,
    editOtherLanguage,
    deleteOtherLanguage,
    checkCode,
} from "./OtherLanguageService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
    //etc you get the idea
});

export default class OtherLanguageStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    intactOtherLanguage = {
        id: "",
        code: "",
        name: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    otherLanguageList = [];
    selectedOtherLanguageList = [];
    selectedOtherLanguage = null;
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetOtherLanguageStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.otherLanguageList = [];
        this.selectedOtherLanguageList = [];
        this.selectedOtherLanguage = null;
        this.totalElements = 0;
        this.totalPages = 0;
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };

    handleSetSearchObject = (searchObject) => {
        this.searchObject = {...searchObject};
    };

    search = async () => {
        this.loadingInitial = true;
        const searchObject = {
            ...this.searchObject
        };
        try {
            let data = await pagingOtherLanguages(searchObject);
            this.otherLanguageList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

            this.setLoadingInitial(false);
        } catch (error) {
            console.log(error);
            this.setLoadingInitial(false);
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.search();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.search();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };
    handleEditOtherLanguage = (id) => {
        this.getOtherLanguage(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleClose = () => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.search();
    };

    handleDelete = (id) => {
        this.getOtherLanguage(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleConfirmDelete = () => {
        this.deleteOtherLanguage(this.selectedOtherLanguage.id);
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedOtherLanguageList.length; i++) {
            try {
                await deleteOtherLanguage(this.selectedOtherLanguageList[i].id);
            } catch (error) {
                listAlert.push(this.selectedOtherLanguageList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedOtherLanguageList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getOtherLanguage = async (id) => {
        if (id != null) {
            try {
                let data = await getOtherLanguage(id);
                this.handleSelectOtherLanguage(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectOtherLanguage(null);
        }
    };

    handleSelectOtherLanguage = (otherLanguage) => {
        this.selectedOtherLanguage = otherLanguage;
    };

    handleSelectListOtherLanguage = (otherLanguages) => {
        this.selectedOtherLanguageList = otherLanguages;
    };

    createOtherLanguage = async (otherLanguage) => {
        try {
            let responseCheckCode = await checkCode(
                otherLanguage.id,
                otherLanguage.code
            );
            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createOtherLanguage(otherLanguage);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editOtherLanguage = async (otherLanguage) => {
        try {
            let responseCheckCode = await checkCode(
                otherLanguage.id,
                otherLanguage.code
            );
            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editOtherLanguage(otherLanguage);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    deleteOtherLanguage = async (id) => {
        try {
            await deleteOtherLanguage(id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };
}
