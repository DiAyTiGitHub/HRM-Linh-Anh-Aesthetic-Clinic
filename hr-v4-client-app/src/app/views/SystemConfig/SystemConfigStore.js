import { makeAutoObservable } from "mobx";
import {
    pagingSystemConfig,
    getById,
    saveSystemConfig,
    deleteMultiple,
    deleteById,
} from "./SystemConfigService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import LocalConstants from "app/LocalConstants";
import { getCurrentStaff } from "../profile/ProfileService";
import { SalaryItem } from "../../common/Model/Salary/SalaryItem";
import { exportHDLD } from "../StaffLabourAgreement/StaffLabourAgreementService";
import { SystemConfig } from "app/common/Model/HumanResource/SystemConfig";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class SystemConfigStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null,
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    systemConfigList = [];
    openCreateEditPopup = false;
    selectedSystemConfig = new SystemConfig();
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listChosen = [];
    openViewPopup = false;

    handleOpenView = async (systemConfig) => {
        try {
            if (systemConfig) {
                const { data } = await getById(systemConfig?.id);
                this.selectedSystemConfig = data;
            } else {
                const { data } = await getCurrentStaff();
                this.selectedSystemConfig = {
                    ... new systemConfig(),
                    requestStaff: data
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
        this.systemConfigList = [];
        this.openCreateEditPopup = false;
        this.selectedSystemConfig = new SystemConfig();
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listChosen = [];
        this.openViewPopup = false;
    }

    handleSetSearchObject = (searchObject) => {
        // if (searchObject.staff == null) {
        //     searchObject.staffId = null;
        // } else {
        //     searchObject.staffId = searchObject.staff.id;
        // }
        // if (searchObject.department == null) {
        //     searchObject.departmentId = null;
        // } else {
        //     searchObject.departmentId = searchObject.department.id;
        // }

        // if (searchObject.organization == null) {
        //     searchObject.organizationId = null;
        // } else {
        //     searchObject.organizationId = searchObject.organization.id;
        // }

        // if (searchObject.positionTitle == null) {
        //     searchObject.positionTitleId = null;
        // } else {
        //     searchObject.positionTitleId = searchObject.positionTitle.id;
        // }
        this.searchObject = { ...searchObject };
    }

    pagingSystemConfig = async () => {
        try {
            this.handleSetSearchObject(this.searchObject);

            const payload = {
                ... this.searchObject,
            };
            const data = await pagingSystemConfig(payload);

            this.systemConfigList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingSystemConfig();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingSystemConfig();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteByIds) => {
        this.listChosen = deleteByIds;
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

    handleDelete = (systemConfig) => {
        this.selectedSystemConfig = { ...systemConfig };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleOpenCreateEdit = async (systemConfig) => {
        console.log("systemConfig", systemConfig);
        
        try {
            if (systemConfig) {
                const { data } = await getById(systemConfig?.id);
                this.selectedSystemConfig = data;
            } else {
                const { data } = await getCurrentStaff();
                this.selectedSystemConfig = {
                    ... new SystemConfig(),
                    requestStaff: data
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
            const { data } = await deleteById(this?.selectedSystemConfig?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingSystemConfig();

            this.handleClose();

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        try {
            const deleteData = this.getSelectedIds();

            await deleteMultiple(deleteData);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingSystemConfig();
            this.listChosen = [];

            this.handleClose();


        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveSystemConfig = async (systemConfig) => {
        try {
            const { data } = await saveSystemConfig(systemConfig);
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
