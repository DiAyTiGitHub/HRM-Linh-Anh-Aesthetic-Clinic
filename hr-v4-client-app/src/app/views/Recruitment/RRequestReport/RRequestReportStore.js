import { makeAutoObservable } from "mobx";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import {
    exportRecruitmentRequestReport,
    pagingRecruitmentRequestReport
} from "../RecruitmentRequestV2/RecruitmentRequestV2Service";
import { saveAs } from "file-saver";
import { SearchRecruitment } from "app/common/Model/SearchObject/SearchRecruitment";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class RRequestReportStore {
    intactSearchObject = {
        ...new SearchRecruitment()
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    requestReportList = [];


    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.requestReportList = [];
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

    pagingRecruitmentRequestReport = async () => {
        try {
            this.handleSetSearchObject(this.searchObject);

            const payload = {
                ...this.searchObject,
            };
            const data = await pagingRecruitmentRequestReport(payload);

            this.requestReportList = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingRecruitmentRequestReport();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingRecruitmentRequestReport();
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

    openConfirmexportRecruitmentRequestReport = false;

    handleClose = () => {
        this.openConfirmexportRecruitmentRequestReport = false;
    }

    handleOpenConfirmexportRecruitmentRequestReport = () => {
        this.openConfirmexportRecruitmentRequestReport = true;
    }


    exportRecruitmentRequestReport = async () => {
        if (this.totalElements > 0) {
            try {
                const res = await exportRecruitmentRequestReport({ ...this.searchObject, isExportExcel: true });
                toast.success(i18n.t("general.successExport"));
                let blob = new Blob([res.data], {
                    type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                });

                saveAs(blob, "BAO_CAO_THEO_YC_TUYEN_DUNG.xlsx");
            } finally {
            }
        } else {
            toast.warning(i18n.t("general.noData"));
        }
    };
}
