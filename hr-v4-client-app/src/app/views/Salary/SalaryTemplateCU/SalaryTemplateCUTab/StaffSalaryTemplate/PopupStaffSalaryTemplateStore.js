import "react-toastify/dist/ReactToastify.css";
import { makeAutoObservable } from "mobx";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import {
    deleteById ,
    deleteMultiple ,
    getById ,
    pagingStaffSalaryTemplate ,
    saveListStaffSalaryTemplate ,
    saveStaffSalaryTemplateDto
} from "./StaffSalaryTemplateService";
import { SalaryItem } from "../../../../../common/Model/Salary/SalaryItem";
import StaffSalaryTemplate from "../../../../profile/TabContainer/StaffSalaryTemplate";


toast.configure({
    autoClose:5000 ,
    draggable:false ,
    limit:5 ,
});

export default class PopupStaffSalaryTemplateStore {
    intactSearchObject = {
        keyword:null ,
        pageIndex:1 ,
        pageSize:10 ,
        staffSalaryTemplateId:null
    }

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    defaultValuesForm = {
        staffs:[] ,
        salaryTemplate:null ,
        salaryTemplateName:null ,
        fromDate:new Date() ,
        toDate:null
    };

    totalElements = 0;
    totalPages = 0;
    listStaffSalaryTemplate = [];
    selectedStaffSalaryTemplate = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    openEdit = false;
    openCreate = false;
    listOnDelete = [];
    openViewPopup = false;

    handleOpenView = (value) => {
        this.selectedStaffSalaryTemplate = value;
        this.openViewPopup = true;
    };

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.listStaffSalaryTemplate = [];
        this.selectedStaffSalaryTemplate = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openEdit = false;
        this.openCreate = false;
        this.openViewPopup = false;

    }

    handleSetSearchObject = (searchObject) => {
        this.searchObject = {... searchObject};
    }

    handleOpenEdit = (value) => {
        this.selectedStaffSalaryTemplate = value;

        if (value == null) {
            this.selectedStaffSalaryTemplate = null;
        }

        this.openEdit = true;
    }

    handleOpenCreate = () => {
        this.openCreate = true;
    }

    pagingStaffSalaryTemplate = async () => {
        try {
            const searchData = {... this?.searchObject};

            const data = await pagingStaffSalaryTemplate(searchData);

            this.listStaffSalaryTemplate = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingStaffSalaryTemplate();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingStaffSalaryTemplate();
    };

    handleChangePage = async (event , newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteSalaryTemplates) => {
        this.listOnDelete = deleteSalaryTemplates;
    };

    getById = async (salaryTemplateId) => {
        try {
            const {data} = await getById(salaryTemplateId);
            this.selectedStaffSalaryTemplate = data; // Sửa lại tên này cho đúng
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };


    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openEdit = false;
        this.openCreate = false;
        this.openViewPopup = false;
    };

    handleDelete = (salaryTemplate) => {
        this.selectedStaffSalaryTemplate = salaryTemplate;
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleConfirmDelete = async () => {
        try {
            const {data} = await deleteById(this?.selectedStaffSalaryTemplate?.id);
            toast.success(i18n.t("toast.delete_success"));

            this.handleClose();

            await this.pagingStaffSalaryTemplate();
            return data;
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

            await deleteMultiple(deleteData);
            toast.success(i18n.t("toast.delete_success"));
            this.listOnDelete = [];

            this.handleClose();
            await this.pagingStaffSalaryTemplate();

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveListStaffSalaryTemplate = async (values) => {
        console.log(values)
        try {
            if (values?.staffs == null || values?.staffs?.length <= 0) {
                toast.warn("Phải chọn nhân viên trước khi lưu");
                return null;
            }
            if (values?.salaryTemplate == null) {
                toast.warn("Phải chọn nhân viên trước khi lưu");
                return null;
            }
            await saveListStaffSalaryTemplate(values);
            await this.pagingStaffSalaryTemplate()
            toast.success("Thông tin đã được lưu");
            this.handleClose();
        } catch (error) {
            toast.error(i18n.t("toast.error"));
            return null;
        }
    }

    handleSaveStaffSalaryTemplate = async (values) => {
        try {
            await saveStaffSalaryTemplateDto(values);
            await this.pagingStaffSalaryTemplate()
            toast.success("Thông tin đã được lưu");
            this.handleClose();
        } catch (error) {
            toast.error(i18n.t("toast.error"));
            return null;
        }
    }
}
