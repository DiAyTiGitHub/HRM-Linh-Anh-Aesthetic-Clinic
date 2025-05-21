import { makeAutoObservable } from "mobx";
import {
    pagingPersonCertificate,
    getPersonCertificateById,
    saveOrUpdatePersonCertificate,
    deleteMultiplePersonCertificate,
    deletePersonCertificateById,
} from "../HumanResourcesInformation/PersonCertificate/PersonCertificateService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import LocalConstants from "app/LocalConstants";
import { getCurrentStaff } from "../profile/ProfileService";
import { SalaryItem } from "../../common/Model/Salary/SalaryItem";
import { exportHDLD } from "../StaffLabourAgreement/StaffLabourAgreementService";
import { SearchPersonCertificate } from "app/common/Model/SearchObject/SearchPersonCertificate";
import { PersonCertificate } from "app/common/Model/HumanResource/PersonCertificate";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class StaffCertificateStore {
    intactSearchObject = {
        ... new SearchPersonCertificate()
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    staffCertificateList = [];
    openCreateEditPopup = false;
    selectedStaffCetificate = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listChosen = [];
    openViewPopup = false;

    handleOpenView = async (staffCertificate) => {
        try {
            if (staffCertificate) {
                const { data } = await getPersonCertificateById(staffCertificate?.id);
                this.selectedStaffCetificate = data;
            } else {
                this.selectedStaffCetificate = {
                    ... new PersonCertificate(),
                };
            }
            this.openViewPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.staffCertificateList = [];
        this.openCreateEditPopup = false;
        this.selectedStaffCetificate = new PersonCertificate();
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listChosen = [];
        this.openViewPopup = false;
    }

    handleSetSearchObject = (searchObject) => {
        if (searchObject.staff == null) {
            searchObject.staffId = null;
        } else {
            searchObject.staffId = searchObject.staff.id;
        }
        if (searchObject.department == null) {
            searchObject.departmentId = null;
        } else {
            searchObject.departmentId = searchObject.department.id;
        }

        if (searchObject.organization == null) {
            searchObject.organizationId = null;
        } else {
            searchObject.organizationId = searchObject.organization.id;
        }

        if (searchObject.positionTitle == null) {
            searchObject.positionTitleId = null;
        } else {
            searchObject.positionTitleId = searchObject.positionTitle.id;
        }

        if (searchObject.certificate == null) {
            searchObject.certificateId = null;
        } else {
            searchObject.certificateId = searchObject.certificate.id;
        }

        this.searchObject = { ...searchObject };
    }

    pagingPersonCertificate = async () => {
        try {
            this.handleSetSearchObject(this.searchObject);

            const payload = {
                ... this.searchObject,
            };
            const data = await pagingPersonCertificate(payload);

            this.staffCertificateList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingPersonCertificate();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingPersonCertificate();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deletePersonCertificateByIds) => {
        this.listChosen = deletePersonCertificateByIds;
    };

    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;
        this.openConfirmUpdateStatusPopup = false;
        this.openConfirmDeleteListPopup = false;
        this.onUpdateStatus = null;
        this.listChosen = [];
        this.openViewPopup = false;
    };

    handleDelete = (staffCertificate) => {
        this.selectedStaffCetificate = { ...staffCertificate };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleOpenCreateEdit = async (staffCertificate) => {
        console.log("staffCertificate", staffCertificate);

        try {
            if (staffCertificate) {
                const { data } = await getPersonCertificateById(staffCertificate?.id);
                this.selectedStaffCetificate = data;
            } else {
                const staff = this?.searchObject?.staff;

                this.selectedStaffCetificate = {
                    ... new PersonCertificate(),
                    staff,
                };
            }
            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmDelete = async () => {
        try {
            const { data } = await deletePersonCertificateById(this?.selectedStaffCetificate?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingPersonCertificate();

            this.handleClose();

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        try {
            const deleteData = this.getSelectedIds();

            await deleteMultiplePersonCertificate(deleteData);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingPersonCertificate();
            this.listChosen = [];

            this.handleClose();


        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveOrUpdatePersonCertificate = async (staffCertificate) => {
        try {
            const { data } = await saveOrUpdatePersonCertificate(staffCertificate);
            toast.success("Thông tin Cấu hình đã được lưu");
            this.handleClose();

            return data;
        } catch (error) {
            // console.error(error);
            // // toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");
            // if (error.response.status == 409) {
            //     toast.error("Mã yêu cầu đã được sử dụng, vui lòng sử dụng mã yêu cầu khác", {
            //         autoClose: 5000,
            //         draggable: false,
            //         limit: 5,
            //     });
            // } else {
            //     toast.error(i18n.t("toast.error"));
            // }
            // throw new Error(i18n.t("toast.error"));
            // // return null;
            toast.error(i18n.t("toast.error"));
        }
    };

    getSelectedIds = () => {
        const ids = [];
        this?.listChosen?.forEach(function (item) {
            ids.push(item?.id);
        });

        return ids;
    }
}
