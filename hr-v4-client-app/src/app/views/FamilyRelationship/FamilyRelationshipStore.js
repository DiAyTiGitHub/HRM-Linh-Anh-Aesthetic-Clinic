import {makeAutoObservable} from "mobx";
import {
    getFamilyRelationship,
    createFamilyRelationship,
    editFamilyRelationship,
    deleteFamilyRelationship,
    pagingFamilyRelationship,
    checkCode,
} from "./FamilyRelationshipService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class FamilyRelationshipStore {
    familyRelationshipList = [];
    selectedFamilyRelationship = {
        code: null,
        name: null,
    };
    selectedFamilyRelationshipList = [];
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
            keyword: this.keyword,
            pageIndex: this.page,
            pageSize: this.rowsPerPage,
        };
        try {
            let data = await pagingFamilyRelationship(searchObject);
            this.familyRelationshipList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            this.setLoadingInitial(false);
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
            this.setLoadingInitial(false);
        }
    };

    handleEditFamilyRelationship = (id) => {
        this.getFamilyRelationship(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    getFamilyRelationship = async (id) => {
        if (id != null) {
            try {
                let data = await getFamilyRelationship(id);
                this.handleSelectFamilyRelationship(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        }
    };

    handleSelectFamilyRelationship = (familyRelationship) => {
        this.selectedFamilyRelationship = familyRelationship;
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    createFamilyRelationship = async (familyRelationship) => {
        try {
            let responseCheckCode = await checkCode(
                familyRelationship.id,
                familyRelationship.code
            );
            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createFamilyRelationship(familyRelationship);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editFamilyRelationship = async (familyRelationship) => {
        try {
            let responseCheckCode = await checkCode(
                familyRelationship.id,
                familyRelationship.code
            );
            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editFamilyRelationship(familyRelationship);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleClose = () => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.updatePageData();
        this.selectedFamilyRelationship = {
            code: null,
            name: null,
        }
    };

    handleConfirmDelete = async () => {
        try {
            let check = await deleteFamilyRelationship(
                this.selectedFamilyRelationship.id
            );
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);

            if (error?.response?.status === 409) {
                toast.error("Không thể xóa quan hệ nhân thân vì đang được sử dụng trong hệ thống.");
            } else {
                toast.warning(i18n.t("toast.error"));

            }
        }
    }

    handleDelete = (id) => {
        this.getFamilyRelationship(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedFamilyRelationshipList.length; i++) {
            try {
                await deleteFamilyRelationship(
                    this.selectedFamilyRelationshipList[i].id
                );
            } catch (error) {
                listAlert.push(this.selectedFamilyRelationshipList[i].name);

                if (error?.response?.status === 409) {
                    toast.error("Không thể xóa quan hệ nhân thân vì đang được sử dụng trong hệ thống.");
                } else {
                    toast.warning(i18n.t("toast.error"));

                }
            }
        }
        this.selectedFamilyRelationshipList = [];
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
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

    handleChangePage = (event, newPage) => {
        this.setPage(newPage);
    };


    handleSelectListFamilyRelationship = (familyRelationship) => {
        this.selectedFamilyRelationshipList = familyRelationship;
    };
}
