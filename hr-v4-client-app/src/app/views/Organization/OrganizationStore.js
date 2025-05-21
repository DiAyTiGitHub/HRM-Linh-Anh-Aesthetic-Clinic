import { makeAutoObservable } from "mobx";
import {
    pagingOrganization ,
    getById ,
    saveOrganization ,
    deleteMultiple ,
    deleteOrganization ,
    getCurrentOrganizationOfCurrentUser ,
    uploadImage , downloadOrganizationTemplate , importOrganization ,
    exportExcelOrgData,
    autoGenCode
} from "./OrganizationService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";
import { saveAs } from "file-saver";
import { HrOrganization } from "app/common/Model/HumanResource/HrOrganization";
import { getHrResourcePlanById } from "../HrResourcePlan/HrResourcePlanService";
import {HttpStatus} from "../../LocalConstants";

toast.configure({
    autoClose:2000 ,
    draggable:false ,
    limit:3 ,
});

export default class OrganizationStore {
    searchObject = {
        pageIndex:1 ,
        pageSize:10 ,
        keyword:null ,
    };
    totalElements = 0;
    totalPages = 0;
    loadingInitial = false;
    listOrganizations = [];
    openCreateEditPopup = false;
    selectedOrganization = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listOnDelete = [];
    openViewPopup = false;

    handleOpenView = async (organizationId) => {
        try {
            if (organizationId) {
                const {data} = await getById(organizationId);
                this.selectedOrganization = data;
            } else {
                this.selectedOrganization = {
                    ... new HrOrganization()
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

    uploadFileExcel = async (event) => {
        const file = event.target.files[0];
        importOrganization(file).then(() => {
            toast.success("Nhập excel thành công")
            this.searchObject = {
                ... this.searchObject ,
                pageIndex:1
            }
            this.pagingOrganization()
        }).catch(() => {
            toast.error("Nhập excel thất bại")
        }).finally(() => {
            this.handleClose();
        })
        event.target.value = null;
    };


    handleDownloadOrganizationTemplate = async () => {
        try {
            const res = await downloadOrganizationTemplate();
            let blob = new Blob([res.data] , {
                type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ,
            });
            saveAs(blob , "Mẫu nhập dữ liệu đơn vị.xlsx");
            toast.success("Đã tải mẫu nhập đơn vị");
        } catch (error) {
            console.error("Error downloading timesheet detail template:" , error);
        }
    };


    handlExportExcelOrgData = async () => {
        if (this.totalElements > 0) {
            try {
                const res = await exportExcelOrgData({... this.searchObject});
                toast.success(i18n.t("general.successExport"));
                let blob = new Blob([res.data] , {
                    type:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" ,
                });

                saveAs(blob , "DuLieuDonVi.xlsx");
            } finally {

            }
        } else {
            toast.warning(i18n.t("general.noData"));
        }
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };

    resetStore = () => {
        this.searchObject = {
            pageIndex:1 ,
            pageSize:10 ,
            keyword:null ,
        };
        this.totalElements = 0;
        this.totalPages = 0;
        this.listOrganizations = [];
        this.openCreateEditPopup = false;
        this.selectedOrganization = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openViewPopup = false;
    };

    handleSetSearchObject = (searchObject) => {
        this.searchObject = {... searchObject};
    };

    setRowsPerPage = (event) => {
        this.pageSize = event.target.value;
        this.page = 1;
        this.updatePageData();
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
        var searchObject = {};
        searchObject.keyword = this.keyword;
        searchObject.pageIndex = this.page;
        searchObject.pageSize = this.pageSize || 10;

        try {
            let data = await pagingOrganization(searchObject);

            var treeValues = [];

            let itemListClone = data.data.content;

            itemListClone.forEach((item) => {
                var items = this.getSelectListOrg(item);

                treeValues.push(... items);
            });

            console.log("treeValues org" , treeValues);
            this.listOrganizations = treeValues;

            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            this.setLoadingInitial(false);
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
            this.setLoadingInitial(false);
        }
    };

    pagingOrganization = async () => {
        try {
            let data = await pagingOrganization(this?.searchObject);
            var treeValues = [];

            let itemListClone = data.data.content;

            itemListClone.forEach((item) => {
                var items = this.getSelectListOrg(item);

                treeValues.push(... items);
            });

            console.log("treeValues org" , treeValues);
            this.listOrganizations = treeValues;
            // this.listOrganizations = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingOrganization();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingOrganization();
    };

    handleChangePage = async (event , newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteOrganization) => {
        this.listOnDelete = deleteOrganization;
    };

    getById = async (organizationId) => {
        try {
            const {data} = await getById(organizationId);
            this.selectedOrganization = data;
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

    handleDelete = (organizationBranch) => {
        this.selectedOrganization = {... organizationBranch};
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    getSelectListOrg(item , parentId = null) {
        var result = [];
        var root = {
            // name: item.name,
            // code: item.code,
            // website: item.website,
            // id: item.id,
            parentId:parentId , // Truyền parentId từ tham số
            ... item
        };


        result.push(root);

        if (item.subOrganizations) {
            item.subOrganizations.forEach((child) => {
                var childs = this.getSelectListOrg(child , item.id); // Truyền item.id làm parentId cho con
                result.push(... childs);
            });
        }

        return result;
    }

    handleOpenCreateEdit = async (organizationId) => {
        try {
            if (organizationId) {
                const {data} = await getById(organizationId);
                this.selectedOrganization = data;
            } else {
                this.selectedOrganization = {
                    ... new HrOrganization()
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
            const {data} = await deleteOrganization(this?.selectedOrganization?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingOrganization();

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

            await deleteMultiple(deleteData);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingOrganization();
            this.listOnDelete = [];

            this.handleClose();
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveOrganization = async (organizationBranch) => {
        try {
            await saveOrganization(organizationBranch);

            await this.pagingOrganization();
            this.handleClose();
            toast.success("Thông tin công ty đã được lưu");
        } catch (error) {
            console.error(error);
            if (error.response.status == 409) {
                toast.error("Mã đơn vị đã được sử dụng, vui lòng sử dụng mã đơn vị khác" , {
                    autoClose:5000 ,
                    draggable:false ,
                    limit:5 ,
                });
            } else {
                toast.error(i18n.t("toast.error"));
            }
        }
    };

    uploadImage = async (file) => {
        try {
            if (file != null) {
                const formData = new FormData();
                formData.append("uploadfile" , file);
                let newObj = {
                    formData:formData ,
                    // id: this.currentStaff.id,
                };
                return await uploadImage(newObj);
            }
            toast.success(i18n.t("toast.add_success"));
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    getCurrentOrganizationOfCurrentUser = async () => {
        try {
            const {data} = await getCurrentOrganizationOfCurrentUser();

            this.selectedOrganization = data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    // AUTHORITY TO CREATE, UPDATE, EDIT
    canCreateUpdateDelete = () => {
        const roles = localStorageService.getLoginUser()?.user?.roles?.map((item) => item.authority) || [];

        let hasAuthToCreateDelete = ["HR_MANAGER" , "ROLE_ADMIN" , "ROLE_SUPER_ADMIN"];

        if (roles.some((role) => hasAuthToCreateDelete.indexOf(role) !== -1)) return true;

        return false;
    };

    autoGenCode = async (configKey) =>{
        const response = await autoGenCode(configKey)
        if(response.status === HttpStatus.OK){
            return response.data;
        }
    }
}
