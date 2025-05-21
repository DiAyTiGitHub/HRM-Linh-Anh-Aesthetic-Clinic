import { makeAutoObservable } from "mobx";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import LocalConstants from "app/LocalConstants";
import { getCurrentStaff } from "../profile/ProfileService";
import { SalaryItem } from "../../common/Model/Salary/SalaryItem";
import { exportHDLD } from "../StaffLabourAgreement/StaffLabourAgreementService";
import { SystemConfig } from "app/common/Model/HumanResource/SystemConfig";
import { SearchObjectStaff } from "app/common/Model/SearchObject/SearchObjectStaff";
import { exportLaborManagementBook, pagingStaffLabourManagement } from "../HumanResourcesInformation/StaffService";
import { saveAs } from "file-saver";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class StaffLabourManagementBookStore {
    intactSearchObject = {
        ...new SearchObjectStaff()
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    staffManagementBookList = [];


    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.staffManagementBookList = [];
    }

    handleSetSearchObject = (searchObject) => {
        this.searchObject = { ...searchObject };

        if (searchObject?.department) {
            this.searchObject.departmentId = searchObject.department.id;
        } else {
            this.searchObject.departmentId = null;
        }

        if (searchObject?.organization) {
            this.searchObject.organizationId = searchObject.organization.id;
        } else {
            this.searchObject.organizationId = null;
        }

        if (searchObject?.position) {
            this.searchObject.positionId = searchObject.position.id;
        } else {
            this.searchObject.positionId = null;
        }

        if (searchObject?.contractOrganization) {
            this.searchObject.contractOrganizationId = searchObject.contractOrganization.id;
        } else {
            this.searchObject.contractOrganizationId = null;
        }

        if (searchObject?.workOrganization) {
            this.searchObject.workOrganizationId = searchObject.workOrganization.id;
        } else {
            this.searchObject.workOrganizationId = null;
        }

        if (searchObject?.positionTitle) {
            this.searchObject.positionTitleId = searchObject.positionTitle.id;
        } else {
            this.searchObject.positionTitleId = null;
        }

        if (searchObject?.fixShiftWork) {
            this.searchObject.fixShiftWorkId = searchObject.fixShiftWork.id;
        } else {
            this.searchObject.fixShiftWorkId = null;
        }


    }

    pagingStaffLabourManagement = async () => {
        try {
            this.handleSetSearchObject(this.searchObject);

            const payload = {
                ... this.searchObject,
            };
            const data = await pagingStaffLabourManagement(payload);

            this.staffManagementBookList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingStaffLabourManagement();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingStaffLabourManagement();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteByIds) => {
        this.listChosen = deleteByIds;
    };

    getSelectedIds = () => {
        const ids = [];
        this?.listChosen?.forEach(function (item) {
            ids.push(item?.id);
        });

        return ids;
    }

    openConfirmExportLaborManagementBook = false;

    handleClose = () => {
        this.openConfirmExportLaborManagementBook = false;
    }

    handleOpenConfirmExportLaborManagementBook = () => {
        this.openConfirmExportLaborManagementBook = true;
    }


    exportLaborManagementBook = async () => {
        if (this.totalElements > 0) {
            try {
                const res = await exportLaborManagementBook({ ...this.searchObject, isExportExcel: true });
                toast.success(i18n.t("general.successExport"));
                let blob = new Blob([res.data], {
                    type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                });

                saveAs(blob, "SO_QUAN_LY_LAO_DONG.xlsx");
            } finally {
            }
        } else {
            toast.warning(i18n.t("general.noData"));
        }
    };
}
