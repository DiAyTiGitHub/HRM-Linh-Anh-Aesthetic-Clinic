import localStorageService from "app/services/localStorageService";
import i18n from "i18n";
import { makeAutoObservable } from "mobx";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import {
    deleteDepartmentType ,
    deleteMultiple ,
    downloadDepartmentTypeTemplate ,
    getById ,
    importDepartmentType ,
    pagingDepartmentType ,
    saveDepartmentType,
    autoGenCode
} from "./DepartmentTypeService";
import { saveAs } from "file-saver";
import {HttpStatus} from "../../LocalConstants";

toast.configure({
    autoClose:2000 ,
    draggable:false ,
    limit:3 ,
});

export default class DepartmentTypeStore {
    searchObject = {
        pageIndex:1 ,
        pageSize:10 ,
        keyword:null ,
    };
    totalElements = 0;
    totalPages = 0;
    listDepartmentType = [];
    openCreateEditPopup = false;
    selectedDeparmentType = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listOnDelete = [];
    openViewPopup = false;

    handleOpenView = async (departmentTypeId) => {
        try {
            if (departmentTypeId) {
                const {data} = await getById(departmentTypeId);
                this.selectedDeparmentType = data;
            } else {
                this.selectedDeparmentType = {
                    ... this.initialDepartmentType
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
        this.listDepartmentType = [];
        this.openCreateEditPopup = false;
        this.selectedDeparmentType = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openViewPopup = false;
    }

    handleSetSearchObject = (searchObject) => {
        this.searchObject = {... searchObject};
    }

    pagingDepartmentType = async () => {
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ... this.searchObject ,
                organizationId:loggedInStaff?.user?.org?.id
            };
            const data = await pagingDepartmentType(payload);

            this.listDepartmentType = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingDepartmentType();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingDepartmentType();
    };

    handleChangePage = async (event , newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteDepartmentTypes) => {
        this.listOnDelete = deleteDepartmentTypes;
    };

    getById = async (departmentTypeId) => {
        try {
            const {data} = await getById(departmentTypeId);
            this.selectedDeparmentType = data;
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
        this.openViewPopup = false;
    };

    handleDelete = (salareyArea) => {
        this.selectedDeparmentType = {... salareyArea};
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    initialDepartmentType = {
        id:null ,
        name:null ,
        sortNumber:null ,
        otherName:null ,
        shortName:null ,
        description:null ,
    }

    handleOpenCreateEdit = async (departmentTypeId) => {
        try {
            if (departmentTypeId) {
                const {data} = await getById(departmentTypeId);
                this.selectedDeparmentType = data;
            } else {
                this.selectedDeparmentType = {
                    ... this.initialDepartmentType
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
            const {data} = await deleteDepartmentType(this?.selectedDeparmentType?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingDepartmentType();

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

            await this.pagingDepartmentType();
            this.listOnDelete = [];

            this.handleClose();


        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveDepartmentType = async (salareyArea) => {
        try {
            const {data} = await saveDepartmentType(salareyArea);
            toast.success("Thông tin Loại phòng ban đã được lưu");
            this.handleClose();
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error("Mã loại phòng ban đã được sử dụng, vui lòng sử dụng mã loại phòng ban khác" , {
                    autoClose:5000 ,
                    draggable:false ,
                    limit:5 ,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }

            throw new Error(i18n.t("toast.error"));
        }
    };

    uploadFileExcel = async (event) => {
        const file = event.target.files[0];
        importDepartmentType(file).then(() => {
            toast.success("Nhập excel thành công")
            this.searchObject = {
                ... this.searchObject ,
                pageIndex:1
            }
            this.pagingDepartmentType();
        }).catch(() => {
            toast.error("Nhập excel thất bại")
        }).finally(() => {
            this.handleClose();
        })
        event.target.value = null;
    };

    handleDownloadDepartmentTypeTemplate = async () => {
        try {
            const res = await downloadDepartmentTypeTemplate();
            let blob = new Blob([res.data] , {
                type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ,
            });
            saveAs(blob , "Mẫu nhập dữ liệu loại phòng ban.xlsx");
            toast.success(i18n.t("general.successExport"));
        } catch (error) {
            console.error("Error downloading timesheet detail template:" , error);
        }
    };

    autoGenCode = async (configKey) =>{
        const response = await autoGenCode(configKey)
        if(response.status === HttpStatus.OK){
            return response.data;
        }
    }
}
