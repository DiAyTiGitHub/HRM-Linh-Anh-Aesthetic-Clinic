import { makeAutoObservable } from "mobx";
import {
    getDepartment ,
    saveDepartment ,
    deleteDepartment ,
    checkCode ,
    pagingAllDepartments ,
    pagingTreeDepartments
} from "./DepartmentService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import localStorageService from "app/services/localStorageService";

toast.configure({
    autoClose:2000 ,
    draggable:false ,
    limit:3 ,
});

export default class DepartmentStore {
    departmentList = [];
    selectDepartmentList = [
        {
            label:"" ,
            value:"" ,
            children:[] ,
        } ,
    ];
    selectedDepartment = null;
    selectedDepartmentList = [];
    totalElements = 0;
    totalPages = 0;
    page = 1;
    rowsPerPage = 10;
    keyword = "";
    organizationId = null;
    departmentId = null;
    department = null;
    organization = null;
    loadingInitial = false;
    shouldOpenEditorDialog = false;
    shouldOpenConfirmationDialog = false;
    shouldOpenConfirmationDeleteListDialog = false;
    shouldOpenImportExcelDialog = false;
    openViewPopup = false;

    handleOpenView = async (id) => {
        try {
            if (id) {
                const {data} = await getDepartment(id);
                // console.log("catched data: ", data);
                if (!data.positionTitles) {
                    data.positionTitles = [];
                }
                this.selectedDepartment = structuredClone(data);
            } else {
                this.selectedDepartment = {
                    id:"" ,
                    code:"" ,
                    name:"" ,
                    value:"" ,
                    parent:null ,
                    positionTitles:[] ,
                    children:[]
                };
            }
            this.openViewPopup = true;
        } catch (error) {
            console.error(error);
            toast.error("Lấy dữ liệu đơn vị có lỗi");
        }

    };

    constructor() {
        makeAutoObservable(this);
    }

    resetStore = () => {
        this.departmentList = [];
        this.selectDepartmentList = [
            {
                label:"" ,
                value:"" ,
                children:[] ,
            } ,
        ];
        this.selectedDepartment = null;
        // selectedParentDepartment = null;
        this.selectedDepartmentList = [];
        this.totalElements = 0;
        this.totalPages = 0;
        this.page = 1;
        this.rowsPerPage = 10;
        this.keyword = "";
        this.organizationId = null;
        this.departmentId = null;
        this.department = null;
        this.organization = null;
        this.loadingInitial = false;
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.shouldOpenImportExcelDialog = false;
        this.openViewPopup = false;
    }

    setLoadingInitial = (state) => {
        this.loadingInitial = state;
    };
    handleSetSearchObject = (searchObject) => {
        this.keyword = searchObject?.keyword ?? "";
        this.department = searchObject?.department ?? null;
        this.organization = searchObject?.organization ?? null;
    }
    updatePageData = (item) => {
        if (item != null) {
            this.page = 1;
            this.keyword = item.keyword;
            this.organizationId = item.organization?.id || null;
            this.departmentId = item.department?.id || null;
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
        searchObject.pageSize = this.rowsPerPage;
        searchObject.organizationId = this.organization?.id || this.organizationId;
        searchObject.departmentId = this.department?.id;
        try {
            let data = await pagingAllDepartments(searchObject);
            var treeValues = [];

            let itemListClone = data.data.content;

            itemListClone.forEach((item) => {
                var items = this.getListItemChild(item).map(child => ({
                    ... child ,
                    organization:item?.organization
                }));
                treeValues.push(... items);
            });

            this.departmentList = treeValues;

            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;
            this.setLoadingInitial(false);
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
            this.setLoadingInitial(false);
        }
    };

    searchSelectListDepartment = async () => {
        var searchObject = {};
        searchObject.keyword = this.keyword;
        searchObject.pageIndex = this.page;
        searchObject.pageSize = this.rowsPerPage;
        let data = await pagingTreeDepartments(searchObject);
        var treeValues = [];
        let itemListClone = data.data.content;
        itemListClone.forEach((item) => {
            var selectList = this.getSelectListDepartment(item);
            treeValues.push(... selectList);
        });
        this.selectDepartmentList = treeValues;
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

    handleChangePage = (event , newPage) => {
        this.setPage(newPage);
    };

    handleEditDepartment = async (id) => {
        try {
            if (id) {
                const {data} = await getDepartment(id);
                // console.log("catched data: ", data);
                if (!data.positionTitles) {
                    data.positionTitles = [];
                }
                this.selectedDepartment = structuredClone(data);
            } else {
                this.selectedDepartment = {
                    id:"" ,
                    code:"" ,
                    name:"" ,
                    value:"" ,
                    parent:null ,
                    positionTitles:[] ,
                    children:[]
                };
            }
            this.shouldOpenEditorDialog = true;
        } catch (error) {
            console.error(error);
            toast.error("Lấy dữ liệu đơn vị có lỗi");
        }

    };

    handleClose = () => {
        this.shouldOpenEditorDialog = false;
        this.shouldOpenConfirmationDialog = false;
        this.shouldOpenConfirmationDeleteListDialog = false;
        this.shouldOpenImportExcelDialog = false;
        this.openViewPopup = false;
        this.updatePageData();
    };

    handleDelete = (id) => {
        this.getDepartment(id).then(() => {
            this.shouldOpenConfirmationDialog = true;
        });
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDelete = async () => {
        try {
            await deleteDepartment(this.selectedDepartment.id);
            toast.success(i18n.t("toast.delete_success"));
            this.handleClose();
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedDepartmentList.length; i++) {
            try {
                await deleteDepartment(this.selectedDepartmentList[i].id);
            } catch (error) {
                listAlert.push(this.selectedDepartmentList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.handleClose();
        toast.success(i18n.t("toast.delete_success"));
    };

    getDepartment = async (id) => {
        if (id != null) {
            try {
                let data = await getDepartment(id);
                this.handleSelectDepartment(data.data);
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            this.handleSelectDepartment(null);
        }
    };

    returnDepartmentById = async (id) => {
        if (id != null) {
            try {
                let data = await getDepartment(id);
                return data.data;
            } catch (error) {
                console.log(error);
                toast.warning(i18n.t("toast.error"));
            }
        } else {
            return null;
        }
    };
    handleSelectDepartment = (department) => {
        this.selectedDepartment = department;
    };

    getSelectListDepartment(item) {
        var result = [];
        var children = [];
        var root = {};
        root.label = item.name;
        root.value = item.id;
        result.push(root);
        if (item.children) {
            item.children.forEach((child) => {
                var childs = this.getSelectListDepartment(child);
                children.push(... childs);
            });

            root.children = children;
        }

        return result;
    }

    getListItemChild(item) {
        var result = [];
        var root = {};
        root.name = item.name;
        root.code = item.code;
        root.id = item.id;
        root.description = item.description;
        root.displayOrder = item.displayOrder;
        root.foundedDate = item.foundedDate;
        root.parentId = item.parentId;
        root.industryBlock = item.industryBlock;
        root.foundedNumber = item.foundedNumber;
        root.shortName = item.shortName;
        root.sortNumber = item.sortNumber;
        root.hrDepartmentType = item.hrDepartmentType;
        root.func = item.func;
        root.children = item.children;
        result.push(root);
        if (item.children) {
            item.children.forEach((child) => {
                var childs = this.getListItemChild(child);
                result.push(... childs);
            });
        }
        return result;
    }

    handleSelectListDepartment = (department) => {
        this.selectedDepartmentList = department;
    };

    saveDepartment = async (department) => {
        try {
            let responseCheckCode = await checkCode(department.id , department.code);
            if (responseCheckCode.data) {
                toast.warning(i18n.t("toast.duplicate_code"));
            } else {
                await saveDepartment(department);
                toast.success(i18n.t("toast.add_success"));
                this.handleClose();
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    };

    importExcel = () => {
        this.shouldOpenImportExcelDialog = true;
    };


    //handle for choosing position title in department
    openChoosePSPopup = false;

    setOpenChoosePSPopup = (state) => {
        this.openChoosePSPopup = state;
        ;
    }


    //HANDLE FOR CHOOSING SUB DEPARTMENTS IN A DEPARTMENT
    searchObjectSubDepartments = {
        pageIndex:1 ,
        pageSize:10 ,
        keyword:null ,
        organizationId:null ,
    };
    totalElementsSubDepartments = 0;
    totalPagesSubDepartments = 0;
    listSubDepartments = [];
    openChooseSubDpmPopup = false;

    setOpenChooseSubDpmPopup = state => {
        this.openChooseSubDpmPopup = state;
    }
    resetStoreSubDepartments = () => {
        this.searchObjectSubDepartments = {
            pageIndex:1 ,
            pageSize:10 ,
            keyword:null ,
            organizationId:null
        };
        this.totalElementsSubDepartments = 0;
        this.totalPagesSubDepartments = 0;
        this.listSubDepartments = [];
        // this.openChooseSubDpmPopup = false;
    }


    pagingSubDeparments = async () => {
        try {
            const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ... this.searchObjectSubDepartments ,
                // organizationId: loggedInStaff?.user?.org?.id
            };
            const data = await pagingAllDepartments(payload);

            this.listSubDepartments = data.data.content;
            this.totalElementsSubDepartments = data.data.totalElements;
            this.totalPagesSubDepartments = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndexSubDepartments = async (page) => {
        this.searchObjectSubDepartments.pageIndex = page;

        await this.pagingSubDeparments();
    };

    setPageSizeSubDeparments = async (event) => {
        this.searchObjectSubDepartments.pageSize = event.target.value;
        this.searchObjectSubDepartments.pageIndex = 1;

        await this.pagingSubDeparments();
    };

    handleSetSearchObjectSubDepartments = (searchObject) => {
        this.searchObjectSubDepartments = {... searchObject};
    }

    handleChangePageSubDepartments = async (event , newPage) => {
        await this.setPageIndexSubDepartments(newPage);
    };

}
