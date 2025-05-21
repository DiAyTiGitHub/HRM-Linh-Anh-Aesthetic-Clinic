import { makeAutoObservable } from "mobx";
import {
    checkCode ,
    deleteSalaryIncrement ,
    getSalaryIncrement ,
    pagingSalaryIncrement ,
    saveSalaryIncrement ,
} from "./SalaryIncrementService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";

toast.configure({
    autoClose:2000 ,
    draggable:false ,
    limit:3 ,
});

export default class SalaryIncrementStore {
    salaryIncrementList = [];
    selectedSalaryIncrement = null;
    selectedSalaryIncrementList = [];
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;
    openViewPopup = false;

    handleOpenView = async (id) => {
        try {
            this.getSalaryIncrement(id).then(() => {
                this.openViewPopup = true;
            });
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

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
        var searchObject = {
            keyword:this.keyword ,
            pageIndex:this.page ,
            pageSize:this.rowsPerPage ,
        };

        try {
            let data = await pagingSalaryIncrement(searchObject);
            this.salaryIncrementList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            this.setLoadingInitial(false);
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
            this.setLoadingInitial(false);
        }
    };


    getSalaryIncrement = async (id) => {
        if (id != null) {
            try {
                let data = await getSalaryIncrement(id);
                this.handleSelectSalaryIncrement(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.get_fail"));
            }
        } else {
            this.handleSelectSalaryIncrement(null);
        }
    };

    saveSalaryIncrement = async (salaryIncrement) => {
        try {
            let responseCheckCode = await checkCode(
                salaryIncrement.id ,
                salaryIncrement.code
            );
            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
                //this.handleClose();
            } else {
                await saveSalaryIncrement(salaryIncrement);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleSelectSalaryIncrement = (salaryIncrement) => {
        this.selectedSalaryIncrement = salaryIncrement;
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleClose = () => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.openViewPopup = false;
        this.updatePageData();
    };

    handleConfirmDelete = async () => {
        // this.deletePosition(this.selectedPositionTitle.id);
        try {
            await deleteSalaryIncrement(this.selectedSalaryIncrement.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedSalaryIncrementList.length; i++) {
            try {
                await deleteSalaryIncrement(this.selectedSalaryIncrementList[i].id);
            } catch (error) {
                listAlert.push(this.selectedSalaryIncrementList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    handleSelectListSalaryIncrement = (position) => {
        this.selectedSalaryIncrementList = position;
    };

    handleChangePage = (event , newPage) => {
        this.setPage(newPage);
    };

    setRowsPerPage = (event) => {
        this.rowsPerPage = event.target.value;
        this.page = 1;
        this.updatePageData();
    };

    handleEditSalaryIncrement = (id) => {
        this.getSalaryIncrement(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };
    handleDelete = (id) => {
        this.getSalaryIncrement(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };
}
