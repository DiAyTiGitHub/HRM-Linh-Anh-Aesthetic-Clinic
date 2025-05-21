import { makeAutoObservable } from "mobx";
// import {
//     deleteHrResourcePlan ,
//     deleteMultiple ,
//     getHrResourcePlanById ,
//     pagingHrResourcePlan ,
//     saveHrResourcePlan ,
// } from "./HrResourcePlanService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { getDepartmentResourcePlan, getDepartmentResourcePlanTree, getDepartmentResourcePlanTreeBySpreadLevel } from "../HrResourcePlan/HrResourcePlanService";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class HrResourcePlanReportStore {
    intactSearchObject = {
        pageIndex: 1,
        pageSize: 10,
        keyword: null,
        positionTitle: null,
        positionTitleId: null,
        department: null,
        departmentId: null,
        organization: null,
        organizationId: null,
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    tabCU = 0;
    initialHrResourcePlan = {
        id: null,
        department: null,
        planNumber: null,
        currentNumber: null,
        positionTitle: null,
    };

    hrResourcePlanReportList = [];
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
    loadingInitial = false;
    openConfirmAddHrResourcePlanPopup = true;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    openCreateEditPopup = false;
    openAggregateCreateEditPopup = false;
    listOnDelete = [];
    listForSelect = [];

    selectedHrResourcePlan = null;
    selectedHrResourcePlanList = [];
    openViewPopup = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.hrResourcePlanReportList = [];
        this.openCreateEditPopup = false;
        this.selectedHrResourcePlan = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.openAggregateCreateEditPopup = false;
        this.listOnDelete = [];
        this.listForSelect = [];
        this.openViewPopup = false;
    };

    resetStoreNonClose = () => {
        this.totalElements = 0;
        this.totalPages = 0;
        this.listOnDelete = [];
    };

    handleCloseConfirmAddHrResourcePlanPopup = () => {
        this.openConfirmAddHrResourcePlanPopup = false;
    };

    handleOpenConfirmAddHrResourcePlanPopup = () => {
        this.openConfirmAddHrResourcePlanPopup = true;
    };

    setTabCU = (tab) => {
        this.tabCU = tab;
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;
        await this.pagingHrResourcePlan();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;
        await this.pagingHrResourcePlan();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;
        this.openConfirmDeleteListPopup = false;
        this.openAggregateCreateEditPopup = false;
        this.openViewPopup = false;
    };


    handleSelectHrResourcePlan = (dto) => {
        this.selectedHrResourcePlan = dto;
    };

    handleSetSearchObject = (searchObject) => {
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

        this.searchObject = { ...searchObject };
    };

    getDepartmentResourcePlan = async () => {
        try {
            const payload = {
                ... this.searchObject,
            };
            const data = await getDepartmentResourcePlan(payload);
            // const data = await getDepartmentResourcePlanTree(payload);
            // const data = await getDepartmentResourcePlanTreeBySpreadLevel(payload);

            this.hrResourcePlanReportList = data.data;
            // console.log(this.hrResourcePlanReportList);
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };
}
