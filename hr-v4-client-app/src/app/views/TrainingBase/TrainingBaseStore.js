import {makeAutoObservable} from "mobx";
import {
    pagingTrainingBases,
    getTrainingBase,
    createTrainingBase,
    editTrainingBase,
    deleteTrainingBase,
    checkCode,
} from "./TrainingBaseService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
// import { withTranslation, WithTranslation } from 'react-i18next';

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class TrainingBaseStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    intactTrainingBase = {
        id: "",
        code: "",
        name: "",
        nameEng: "",
        description: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    trainingBaseList = [];
    selectedTrainingBase = null;
    selectedTrainingBaseList = [];
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
    }
    resetTrainingBaseStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.trainingBaseList = [];
        this.selectedTrainingBaseList = [];
        this.selectedTrainingBase = null;
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
            let data = await pagingTrainingBases(searchObject);
            this.trainingBaseList = data.data.content;
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

    handleEditTrainingBase = (id) => {
        this.getTrainingBase(id).then(() => {
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
        this.getTrainingBase(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteTrainingBase(this.selectedTrainingBase.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedTrainingBaseList.length; i++) {
            try {
                await deleteTrainingBase(this.selectedTrainingBaseList[i].id);
            } catch (error) {
                listAlert.push(this.selectedTrainingBaseList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedTrainingBaseList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getTrainingBase = async (id) => {
        if (id != null) {
            try {
                let data = await getTrainingBase(id);
                this.handleSelectTrainingBase(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectTrainingBase(null);
        }
    };

    handleSelectTrainingBase = (trainingBase) => {
        this.selectedTrainingBase = trainingBase;
    };

    handleSelectListTrainingBase = (trainingBases) => {
        this.selectedTrainingBaseList = trainingBases;
    };

    createTrainingBase = async (trainingBase) => {
        try {
            let response = await checkCode(trainingBase.id, trainingBase.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createTrainingBase(trainingBase);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editTrainingBase = async (trainingBase) => {
        try {
            let response = await checkCode(trainingBase.id, trainingBase.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editTrainingBase(trainingBase);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };
}
