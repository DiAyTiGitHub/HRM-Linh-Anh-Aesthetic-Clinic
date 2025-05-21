import {makeAutoObservable} from "mobx";
import {
    pagingEducationalManagementLevels,
    getEducationalManagementLevel,
    createEducationalManagementLevel,
    editEducationalManagementLevel,
    deleteEducationalManagementLevel,
    checkCode,
} from "./EducationalManagementLevelService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class EducationalManagementLevelStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    intactEducationalManagementLevel = {
        id: "",
        code: "",
        name: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));

    educationalManagementLevelList = [];
    selectedEducationalManagementLevel = null;
    selectedEducationalManagementLevelList = [];
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetEducationalManagementLevelStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.educationalManagementLevelList = [];
        this.selectedEducationalManagementLevel = null;
        this.selectedEducationalManagementLevelList = [];
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
            let data = await pagingEducationalManagementLevels(searchObject);
            this.educationalManagementLevelList = data.data.content;
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

    handleEditEducationalManagementLevel = (id) => {
        this.getEducationalManagementLevel(id).then(() => {
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
        this.getEducationalManagementLevel(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteEducationalManagementLevel(
                this.selectedEducationalManagementLevel.id
            );
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedEducationalManagementLevelList.length; i++) {
            try {
                await deleteEducationalManagementLevel(
                    this.selectedEducationalManagementLevelList[i].id
                );
            } catch (error) {
                listAlert.push(this.selectedEducationalManagementLevelList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedEducationalManagementLevelList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getEducationalManagementLevel = async (id) => {
        if (id != null) {
            try {
                let data = await getEducationalManagementLevel(id);
                this.handleSelectEducationalManagementLevel(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectEducationalManagementLevel(null);
        }
    };

    handleSelectEducationalManagementLevel = (educationalManagementLevel) => {
        this.selectedEducationalManagementLevel = educationalManagementLevel;
    };

    handleSelectListEducationalManagementLevel = (educationalManagementLevel) => {
        this.selectedEducationalManagementLevelList = educationalManagementLevel;
    };

    createEducationalManagementLevel = async (educationalManagementLevel) => {
        try {
            let response = await checkCode(
                educationalManagementLevel.id,
                educationalManagementLevel.code
            );
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createEducationalManagementLevel(educationalManagementLevel);
                toast.success("toast.add_success");
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editEducationalManagementLevel = async (educationalManagementLevel) => {
        try {
            let response = await checkCode(
                educationalManagementLevel.id,
                educationalManagementLevel.code
            );
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editEducationalManagementLevel(educationalManagementLevel);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };
}
