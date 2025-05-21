import { makeAutoObservable } from "mobx";
import {
    pagingSalaryPeriod ,
    getById ,
    saveSalaryPeriod ,
    deleteMultiple ,
    deleteSalaryPeriod ,
    autoGenCode
} from "./SalaryPeriodService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import { SalaryPeriod } from "app/common/Model/Salary/SalaryPeriod";
import LocalConstants, {HttpStatus} from "app/LocalConstants";
import { getLeaveTypeById } from "../../LeaveType/LeaveTypeService";

toast.configure({
    autoClose:2000 ,
    draggable:false ,
    limit:3 ,
});

export default class SalaryPeriodStore {
    searchObject = {
        pageIndex:1 ,
        pageSize:10 ,
        keyword:null ,
    };
    totalElements = 0;
    totalPages = 0;
    listSalaryPeriods = [];
    openCreateEditPopup = false;
    selectedSalaryPeriod = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listOnDelete = [];
    openViewPopup = false;

    handleOpenView = async (salaryPeriodId) => {
        try {
            if (salaryPeriodId) {
                const {data} = await getById(salaryPeriodId);
                this.selectedSalaryPeriod = {
                    ... JSON.parse(JSON.stringify(data))
                };
            } else {
                this.selectedSalaryPeriod = {
                    ... new SalaryPeriod()
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
        this.searchObject = {
            pageIndex:1 ,
            pageSize:10 ,
            keyword:null ,
        };
        this.totalElements = 0;
        this.totalPages = 0;
        this.listSalaryPeriods = [];
        this.openCreateEditPopup = false;
        this.selectedSalaryPeriod = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openViewPopup = false;
    }

    handleSetSearchObject = (searchObject) => {
        this.searchObject = {... searchObject};
    }

    pagingSalaryPeriod = async () => {
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ... this.searchObject ,
                organizationId:loggedInStaff?.user?.org?.id
            };
            const data = await pagingSalaryPeriod(payload);

            this.listSalaryPeriods = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingSalaryPeriod();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingSalaryPeriod();
    };

    handleChangePage = async (event , newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteSalaryPeriods) => {
        this.listOnDelete = deleteSalaryPeriods;
    };

    getById = async (SalaryPeriodId) => {
        try {
            const {data} = await getById(SalaryPeriodId);
            this.selectedSalaryPeriod = data;
            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openViewPopup = false;
    };

    handleDelete = (salaryPeriod) => {
        this.selectedSalaryPeriod = {... salaryPeriod};
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleOpenCreateEdit = async (SalaryPeriodId) => {
        try {
            if (SalaryPeriodId) {
                const {data} = await getById(SalaryPeriodId);
                this.selectedSalaryPeriod = {
                    ... JSON.parse(JSON.stringify(data))
                };
            } else {
                this.selectedSalaryPeriod = {
                    ... new SalaryPeriod()
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
            const {data} = await deleteSalaryPeriod(this?.selectedSalaryPeriod?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingSalaryPeriod();

            this.handleClose();

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        try {
            const deleteData = [];

            for (let i = 0; i < this?.listOnDelete?.length; i++) {
                deleteData.push(this?.listOnDelete[i]?.id);
            }

            // console.log("deleteData", deleteData)
            await deleteMultiple(deleteData);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingSalaryPeriod();
            this.listOnDelete = [];

            this.handleClose();


        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveSalaryPeriod = async (SalaryPeriod) => {
        try {
            const {data} = await saveSalaryPeriod(SalaryPeriod);
            toast.success("Thông tin kỳ lương đã được lưu");
            this.handleClose();

            return data;

        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error("Mã kỳ lương đã được sử dụng, vui lòng sử dụng mã kỳ lương khác" , {
                    autoClose:5000 ,
                    draggable:false ,
                    limit:5 ,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }

            return null;
        }
    };
    autoGenCode = async (configKey) =>{
        const response = await autoGenCode(configKey)
        if(response.status === HttpStatus.OK){
            return response.data;
        }
    }
}
