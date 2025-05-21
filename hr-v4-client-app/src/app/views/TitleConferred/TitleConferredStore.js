import {makeAutoObservable} from "mobx";
import {
    checkCode,
    createTitleConferred,
    deleteTitleConferred,
    editTitleConferred,
    getTitleConferred,
    pagingTitleConferreds,
} from "./TitleConferredService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class TitleConferredStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    intactTitleConferred = {
        id: "",
        code: "",
        name: "",
        description: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    titleConferredList = [];
    selectedTitleConferred = null;
    selectedTitleConferredList = [];
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetTitleConferredStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.titleConferredList = [];
        this.selectedTitleConferred = null;
        this.selectedTitleConferredList = [];
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
            let data = await pagingTitleConferreds(searchObject);
            this.titleConferredList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            this.setLoadingInitial(false);
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
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

    handleEditTitleConferred = (id) => {
        this.getTitleConferred(id).then(() => {
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
        this.getTitleConferred(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteTitleConferred(this.selectedTitleConferred.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedTitleConferredList.length; i++) {
            try {
                await deleteTitleConferred(this.selectedTitleConferredList[i].id);
            } catch (error) {
                listAlert.push(this.selectedTitleConferredList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedTitleConferredList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getTitleConferred = async (id) => {
        if (id != null) {
            try {
                let data = await getTitleConferred(id);
                this.handleSelectTitleConferred(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectTitleConferred(null);
        }
    };

    handleSelectTitleConferred = (titleConferred) => {
        this.selectedTitleConferred = titleConferred;
    };

    handleSelectListTitleConferred = (titleConferred) => {
        this.selectedTitleConferredList = titleConferred;
    };

    createTitleConferred = async (titleConferred) => {
        try {
            let response = await checkCode(titleConferred.id, titleConferred.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createTitleConferred(titleConferred);
                toast.success("toast.add_success");
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editTitleConferred = async (titleConferred) => {
        try {
            let response = await checkCode(titleConferred.id, titleConferred.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editTitleConferred(titleConferred);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };
}
