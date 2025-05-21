import { makeAutoObservable } from "mobx";
import {
    pagingDiscipline,
    getDiscipline,
    createDiscipline,
    updateDiscipline,
    deleteDiscipline,
    checkCode,
    autoGenCode
} from "./DisciplineService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import {HttpStatus} from "../../LocalConstants";

export default class DisciplineStore {
    disciplineList = [];
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    text = "";
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    selectedDiscipline = null;
    selectedDisciplineList = [];
    shouldOpenDeleteDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
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
        this.selectedDisciplineList = [];
        var searchObject = {
            keyword: this.keyword,
            pageIndex: this.page,
            pageSize: this.rowsPerPage,
        };
        try {
            let data = await pagingDiscipline(searchObject);
            this.disciplineList = data.data.content;
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
    handleChangePage = (newPage) => {
        this.setPage(newPage);
    };
    getDiscipline = async (id) => {
        if (id != null) {
            let data = await getDiscipline(id);
            this.handleSelectDiscipline(data?.data)
        }
        else
            this.handleSelectDiscipline(null);
    }
    addDiscipline = async (obj) => {
        try {
            let responseCheckCode = await checkCode(obj.id, obj.code);
            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"))
            }
            else {
                let data = await createDiscipline(obj);
                this.disciplineList = data?.data?.content;
                this.handleCloseDisciplineDialog();
                toast.success(i18n.t("toast.add_success"));
            }
        }
        catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    }
    updateDiscipline = async (obj) => {
        try {
            let responseCheckCode = await checkCode(obj.id, obj.code);
            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"))
            }
            else {
                let data = await updateDiscipline(obj);
                this.disciplineList = data?.data?.content;
                this.handleCloseDisciplineDialog();
                toast.success(i18n.t("toast.update_success"));
            }
        }
        catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    }
    deleteDiscipline = async (id) => {
        try {
            await deleteDiscipline(id);
            this.handleCloseDisciplineDialog();
            toast.success(i18n.t("toast.delete_success"));
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    }
    handleSelectDiscipline = (discipline) => {
        this.selectedDiscipline = discipline;
    };
    handleAddDiscipline = () => {
        this.selectedDiscipline = null;
        this.shouldOpenEditorDialog = true;
    };
    handleEditDiscipline = (id) => {
        this.getDiscipline(id).then(() => {
            this.shouldOpenEditorDialog = true;
        })
    }
    handleDeleteDiscipline = (id) => {
        this.getDiscipline(id).then(() => {
            this.shouldOpenDeleteDialog = true;
        })
    }
    handleConfirmDelete = () => {
        this.deleteDiscipline(this.selectedDiscipline.id);
    }
    handleCloseDisciplineDialog = () => {
        this.shouldOpenAddDialog = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenDeleteDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;

        this.updatePageData();
    }
    handleSelectListDiscipline = (disciplines) => {
        this.selectedDisciplineList = disciplines;
    }
    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    }
    handleConfirmDeleteList = async () => {
        for (let i = 0; i < this.selectedDisciplineList.length; i++) {
            try {
                await deleteDiscipline(this.selectedDisciplineList[i].id);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.handleCloseDisciplineDialog();
        toast.success(i18n.t("toast.delete_success"));
    }

    autoGenCode = async (configKey) =>{
        const response = await autoGenCode(configKey)
        if(response.status === HttpStatus.OK){
            return response.data;
        }
    }
};
