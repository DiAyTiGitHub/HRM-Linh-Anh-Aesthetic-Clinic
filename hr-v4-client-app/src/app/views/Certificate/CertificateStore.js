import {makeAutoObservable} from "mobx";
import {
    checkCode,
    createCertificate,
    deleteCertificate,
    editCertificate,
    getCertificate,
    pagingCertificates,
} from "./CertificateService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
import LocalConstants from "app/LocalConstants";
// import { withTranslation, WithTranslation } from 'react-i18next';

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class CertificateStore {
    certificateList = [];
    selectedCertificate = null;
    selectedCertificateList = [];
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

    resetCertificateStore = () => {
        this.certificateList = [];
        this.selectedCertificate = null;
        this.selectedCertificateList = [];
        this.totalElements = 0;
        this.totalPages = 0;
        this.page = 1;
        this.rowsPerPage = 10;
        this.keyword = "";
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
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
            let data = await pagingCertificates(searchObject);
            this.certificateList = data.data.content;
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

    handleChangePage = (event, newPage) => {
        this.setPage(newPage);
    };

    handleEditCertificate = (id) => {
        this.getCertificate(id).then(() => {
            this.shouldOpenEditorDialog = true;
        });
    };

    handleClose = () => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.updatePageData();
    };

    handleDelete = (id) => {
        this.getCertificate(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteCertificate(this.selectedCertificate.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedCertificateList.length; i++) {
            try {
                await deleteCertificate(this.selectedCertificateList[i].id);
            } catch (error) {
                listAlert.push(this.selectedCertificateList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getCertificate = async (id) => {
        if (id != null) {
            try {
                let data = await getCertificate(id);
                this.handleSelectCertificate(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectCertificate(null);
        }
    };

    handleSelectCertificate = (certificate) => {
        this.selectedCertificate = certificate;
    };

    handleSelectListCertificate = (certificates) => {
        this.selectedCertificateList = certificates;
        console.log(this.selectedCertificateList);
    };

    createCertificate = async (certificate) => {
        try {
            let response = await checkCode(certificate.id, certificate.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await createCertificate(certificate);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    editCertificate = async (certificate) => {
        try {
            let response = await checkCode(certificate.id, certificate.code);
            if (response.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await editCertificate(certificate);
                toast.success(i18n.t("toast.update_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    getCertificateType = value => {
        const data = LocalConstants.CertificateType.getListData();

        let res = "";

        data?.some((item) => {
            if (item?.value === value) {
                res = item?.name;
                return true; // Dừng lại khi tìm thấy kết quả
            }
            return false; // Tiếp tục nếu chưa tìm thấy
        });

        return res; // Trả về kết quả đúng
    }
}
