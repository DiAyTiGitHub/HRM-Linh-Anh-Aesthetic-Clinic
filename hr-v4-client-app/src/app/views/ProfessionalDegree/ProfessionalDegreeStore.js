import {makeAutoObservable} from "mobx";
import {
    checkCode,
    createProfessionalDegree,
    deleteProfessionalDegree,
    editProfessionalDegree,
    getProfessionalDegree,
    pagingProfessionalDegrees,
} from "./ProfessionalDegreeService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class ProfessionalDegree {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: "",
    };
    intactProfessional = {
        id: "",
        code: "",
        name: "",
        description: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    professionalDegreeList = [];
    selectedProfessionalDegree = null;
    selectedProfessionalDegreeList = [];
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetProfessionalStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.professionalDegreeList = [];
        this.selectedProfessionalDegreeList = [];
        this.selectedProfessionalDegree = null;
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
            let data = await pagingProfessionalDegrees(searchObject);
            this.professionalDegreeList = data.data.content;
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
    handleEditProfessionalDegree = (id) => {
        this.getProfessionalDegree(id).then(() => {
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
        this.getProfessionalDegree(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteProfessionalDegree(this.selectedProfessionalDegree.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedProfessionalDegreeList.length; i++) {
            try {
                await deleteProfessionalDegree(
                    this.selectedProfessionalDegreeList[i].id
                );
            } catch (error) {
                listAlert.push(this.selectedProfessionalDegreeList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedProfessionalDegreeList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getProfessionalDegree = async (id) => {
        if (id != null) {
            try {
                let data = await getProfessionalDegree(id);
                this.handleSelectProfessionalDegree(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectProfessionalDegree(null);
        }
    };

    handleSelectProfessionalDegree = (professionalDegree) => {
        this.selectedProfessionalDegree = professionalDegree;
    };

    handleSelectListProfessionalDegree = (professionalDegrees) => {
        this.selectedProfessionalDegreeList = professionalDegrees;
    };

    createProfessionalDegree = async (professionalDegree) => {
        try {
            let response = await checkCode(
                professionalDegree.id,
                professionalDegree.code
            );
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createProfessionalDegree(professionalDegree);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editProfessionalDegree = async (professionalDegree) => {
        try {
            let response = await checkCode(
                professionalDegree.id,
                professionalDegree.code
            );
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editProfessionalDegree(professionalDegree);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };
}
