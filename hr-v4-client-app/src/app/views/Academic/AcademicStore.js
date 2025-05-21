import { makeAutoObservable } from "mobx";
import {
    pagingAcademics,
    getAcademic,
    createAcademic,
    editAcademic,
    deleteAcademic,
    checkCode,
} from "./AcademicService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class AcademicStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    intactAcademic = {
        id: "",
        code: "",
        name: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    academicList = [];
    selectedAcademicList = [];
    selectedAcademic = null;
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetAcademicStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.academicList = [];
        this.selectedAcademicList = [];
        this.selectedAcademic = null;
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
        this.searchObject = { ...searchObject };
    };

    search = async () => {
        this.loadingInitial = true;
        const searchObject = {
            ...this.searchObject,
        };

        try {
            let data = await pagingAcademics(searchObject);
            this.academicList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

            this.setLoadingInitial(false);
        } catch (error) {
            console.error(error);
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

    handleEditAcademic = (id) => {
        this.getAcademic(id).then(() => {
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
        this.getAcademic(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleConfirmDelete = () => {
        this.deleteAcademic(this.selectedAcademic.id);
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (let i = 0; i < this.selectedAcademicList.length; i++) {
            try {
                await deleteAcademic(this.selectedAcademicList[i].id);
            } catch (error) {
                listAlert.push(this.selectedAcademicList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedAcademicList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getAcademic = async (id) => {
        if (id != null) {
            try {
                let data = await getAcademic(id);
                this.handleSelectAcademic(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectAcademic(null);
        }
    };

    handleSelectAcademic = (academic) => {
        this.selectedAcademic = academic;
    };

    handleSelectListAcademic = (academics) => {
        this.selectedAcademicList = academics;
    };

    createAcademic = async (academic) => {
        try {
            let responseCheckCode = await checkCode(academic.id, academic.code);
            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createAcademic(academic);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editAcademic = async (academic) => {
        try {
            let responseCheckCode = await checkCode(academic.id, academic.code);
            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editAcademic(academic);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    deleteAcademic = async (id) => {
        try {
            await deleteAcademic(id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };
}