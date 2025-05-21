import {makeAutoObservable} from "mobx";
import {
    pagingEducationTypes,
    getEducationType,
    createEducationType,
    editEducationType,
    deleteEducationType,
    checkCode,
} from "./EducationTypeService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
// import { withTranslation, WithTranslation } from 'react-i18next';

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class EducationTypeStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    intactEducationType = {
        id: "",
        code: "",
        name: "",
        nameEng: "",
        description: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    educationTypeList = [];
    selectedEducationType = null;
    selectedEducationTypeList = [];
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
    resetEducationTypeStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.educationTypeList = [];
        this.selectedEducationTypeList = [];
        this.selectedEducationType = null;
        this.totalElements = 0;
        this.totalPages = 0;
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
    };
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
            let data = await pagingEducationTypes(searchObject);
            this.educationTypeList = data.data.content;
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

    handleEditEducationType = (id) => {
        this.getEducationType(id).then(() => {
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
        this.getEducationType(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteEducationType(this.selectedEducationType.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedEducationTypeList.length; i++) {
            try {
                await deleteEducationType(this.selectedEducationTypeList[i].id);
            } catch (error) {
                listAlert.push(this.selectedEducationTypeList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedEducationTypeList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getEducationType = async (id) => {
        if (id != null) {
            try {
                let data = await getEducationType(id);
                this.handleSelectEducationType(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectEducationType(null);
        }
    };

    handleSelectEducationType = (educationType) => {
        this.selectedEducationType = educationType;
    };

    handleSelectListEducationType = (educationTypes) => {
        this.selectedEducationTypeList = educationTypes;
    };

    createEducationType = async (educationType) => {
        try {
            let response = await checkCode(educationType.id, educationType.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createEducationType(educationType);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editEducationType = async (educationType) => {
        try {
            let response = await checkCode(educationType.id, educationType.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editEducationType(educationType);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };
}
