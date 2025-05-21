import { makeAutoObservable } from "mobx";
import {
    createAllowanceType ,
    deleteAllowanceType ,
    editAllowanceType ,
    getAllowanceType ,
    pagingAllowanceTypes ,
} from "./AllowanceTypeService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
// import { withTranslation, WithTranslation } from 'react-i18next';

toast.configure({
    autoClose:2000 ,
    draggable:false ,
    limit:3 ,
});

export default class AllowanceTypeStore {
    allowanceTypeList = [];
    selectedAllowanceType = null;
    selectedAllowanceTypeList = [];
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
        this.getAllowanceType(id).then(() => {
            this.openViewPopup = true;
        });
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
            let data = await pagingAllowanceTypes(searchObject);
            this.allowanceTypeList = data.data.content;
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

    handleChangePage = (event , newPage) => {
        this.setPage(newPage);
    };

    handleEditAllowanceType = (id) => {
        this.getAllowanceType(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleClose = () => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.openViewPopup = false;
        this.updatePageData();
    };

    handleDelete = (id) => {
        this.getAllowanceType(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteAllowanceType(this.selectedAllowanceType.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedAllowanceTypeList.length; i++) {
            try {
                await deleteAllowanceType(this.selectedAllowanceTypeList[i].id);
            } catch (error) {
                listAlert.push(this.selectedAllowanceTypeList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getAllowanceType = async (id) => {
        if (id != null) {
            try {
                let data = await getAllowanceType(id);
                this.handleSelectAllowanceType(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectAllowanceType(null);
        }
    };

    handleSelectAllowanceType = (allowanceType) => {
        this.selectedAllowanceType = allowanceType;
    };

    handleSelectListAllowanceType = (allowanceTypes) => {
        this.selectedAllowanceTypeList = allowanceTypes;
        console.log(this.selectedAllowanceTypeList);
    };

    createAllowanceType = async (allowanceType) => {
        try {
            // let response = await checkCode(allowanceType.id, allowanceType.code);
            // if (response.data) {
            //   toast.warning(i18n.t("toast.duplicate_code"));
            // } else {
            await createAllowanceType(allowanceType);
            toast.success(i18n.t("toast.add_success"));
            this.handleClose();
            // }
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error(i18n.t("toast.duplicate_code") , {
                    autoClose:5000 ,
                    draggable:false ,
                    limit:5 ,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }
        }
    };

    editAllowanceType = async (allowanceType) => {
        try {
            // let response = await checkCode(allowanceType.id, allowanceType.code);
            // if (response.data) {
            //   toast.warning(i18n.t("toast.duplicate_code"));
            // } else {
            await editAllowanceType(allowanceType);
            toast.success(i18n.t("toast.update_success"));
            this.handleClose();
            // }
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error(i18n.t("toast.duplicate_code") , {
                    autoClose:5000 ,
                    draggable:false ,
                    limit:5 ,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }
        }
    };
}
