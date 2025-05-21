import {makeAutoObservable} from "mobx";
import {
    checkCode,
    checkName,
    deleteEducationDegree,
    editEducationDegree,
    getEducationDegree,
    pagingEducationDegrees,
    saveEducationDegree,
} from "./EducationDegreeService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
// import { withTranslation, WithTranslation } from 'react-i18next';

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class EducationDegreeStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    intactEducationDegree = {
        id: "",
        code: "",
        name: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    educationDegreeList = [];
    selectedEducationDegree = null;
    selectedEducationDegreeList = [];
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetEducationDegreeStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.educationDegreeList = [];
        this.selectedEducationDegree = null;
        this.selectedEducationDegreeList = [];
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
            let data = await pagingEducationDegrees(searchObject);
            this.educationDegreeList = data.data.content;
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
    handleEditEducationDegree = (id) => {
        this.getEducationDegree(id).then(() => {
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
        this.getEducationDegree(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteEducationDegree(this.selectedEducationDegree.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedEducationDegreeList.length; i++) {
            try {
                await deleteEducationDegree(this.selectedEducationDegreeList[i].id);
            } catch (error) {
                listAlert.push(this.selectedEducationDegreeList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedEducationDegreeList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getEducationDegree = async (id) => {
        if (id != null) {
            try {
                let data = await getEducationDegree(id);
                this.handleSelectEducationDegree(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectEducationDegree(null);
        }
    };

    handleSelectEducationDegree = (educationDegree) => {
        this.selectedEducationDegree = educationDegree;
    };

    handleSelectListEducationDegree = (educationDegrees) => {
        this.selectedEducationDegreeList = educationDegrees;
    };

    createEducationDegree = async (educationDegree) => {
        try {
            let response = await checkCode(educationDegree.id, educationDegree.code);
            let response2 = await checkName(educationDegree.id, educationDegree.name);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else if (response2.data) {
                toast.warning(i18n.t("toast.duplicate_name"));
            } else {
                await saveEducationDegree(educationDegree);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editEducationDegree = async (educationDegree) => {
        try {
            let response = await checkCode(educationDegree.id, educationDegree.code);
            let response2 = await checkName(educationDegree.id, educationDegree.name);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else if (response2.data) {
                toast.warning(i18n.t("toast.duplicate_name"));
            } else {
                await editEducationDegree(educationDegree);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };
}
