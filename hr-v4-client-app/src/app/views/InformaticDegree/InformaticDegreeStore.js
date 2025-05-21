import {makeAutoObservable} from "mobx";
import {
    pagingInformaticDegree,
    getInformaticDegree,
    createInformaticDegree,
    editInformaticDegree,
    deleteInformaticDegree,
    checkCode,
} from "./InformaticDegreeService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";

toast.configure({
    autoClose: 2000, draggable: false, limit: 3,
});

export default class InformaticDegreeStore {
    intactSearchObject = {
        pageIndex: 1, pageSize: 10, keyword: "",
    };
    intactInformaticDegree = {
        id: "",
        code: "",
        name: "",
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    informaticDegreeList = [];
    selectedInformaticDegree = null;
    selectedInformaticDegreeList = [];
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetInformaticDegreeStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.informaticDegreeList = [];
        this.selectedInformaticDegree = null;
        this.selectedInformaticDegreeList = [];
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
            let data = await pagingInformaticDegree(searchObject);
            this.informaticDegreeList = data.data.content;
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

    handleEditInformaticDegree = (id) => {
        this.getInformaticDegree(id).then(() => {
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
        this.getInformaticDegree(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteInformaticDegree(this.selectedInformaticDegree.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedInformaticDegreeList.length; i++) {
            try {
                await deleteInformaticDegree(this.selectedInformaticDegreeList[i].id);
            } catch (error) {
                listAlert.push(this.selectedInformaticDegreeList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.selectedInformaticDegreeList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getInformaticDegree = async (id) => {
        if (id != null) {
            try {
                let data = await getInformaticDegree(id);
                this.handleSelectInformaticDegree(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectInformaticDegree(null);
        }
    };

    handleSelectInformaticDegree = (informaticDegree) => {
        this.selectedInformaticDegree = informaticDegree;
    };

    handleSelectListInformaticDegree = (informaticDegree) => {
        this.selectedInformaticDegreeList = informaticDegree;
        console.log(this.selectedInformaticDegreeList);
    };

    createInformaticDegree = async (informaticDegree) => {
        try {
            let response = await checkCode(informaticDegree.id, informaticDegree.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createInformaticDegree(informaticDegree);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editInformaticDegree = async (informaticDegree) => {
        try {
            let response = await checkCode(informaticDegree.id, informaticDegree.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editInformaticDegree(informaticDegree);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };
}
