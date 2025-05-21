import {makeAutoObservable} from "mobx";
import {
    pagingProfessions,
    getProfession,
    createProfession,
    editProfession,
    deleteProfession,
    checkCode,
} from "./ProfessionService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class ProfessionStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    professionList = [];
    selectedProfession = null;
    selectedProfessionList = [];
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
    }
    resetProfessionStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.professionList = [];
        this.selectedProfession = null;
        this.selectedProfessionList = [];
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
            let data = await pagingProfessions(searchObject);
            this.professionList = data.data.content;
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
        this.search();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleEditProfession = (id) => {
        this.getProfession(id).then(() => {
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
        this.getProfession(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteProfession(this.selectedProfession.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedProfessionList.length; i++) {
            try {
                await deleteProfession(this.selectedProfessionList[i].id);
            } catch (error) {
                listAlert.push(this.selectedProfessionList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getProfession = async (id) => {
        if (id != null) {
            try {
                let data = await getProfession(id);
                this.handleSelectProfession(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectProfession(null);
        }
    };

    handleSelectProfession = (profession) => {
        this.selectedProfession = profession;
    };

    handleSelectListProfession = (professions) => {
        this.selectedProfessionList = professions;
    };

    createProfession = async (profession) => {
        try {
            let response = await checkCode(profession.id, profession.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createProfession(profession);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editProfession = async (profession) => {
        try {
            let response = await checkCode(profession.id, profession.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editProfession(profession);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };
}
