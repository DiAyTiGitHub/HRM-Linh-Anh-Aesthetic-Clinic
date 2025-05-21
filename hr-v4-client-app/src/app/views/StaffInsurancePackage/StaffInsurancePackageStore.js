import { makeAutoObservable } from "mobx";
import {
    pagingStaffInsurancePackage,
    getById,
    saveStaffInsurancePackage,
    deleteMultiple,
    deleteById,
} from "./StaffInsurancePackageService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { SearchStaffInsurancePackage } from "app/common/Model/SearchObject/SearchStaffInsurancePackage";
import { StaffInsurancePackage } from "app/common/Model/HumanResource/StaffInsurancePackage";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class StaffInsurancePackageStore {
    intactSearchObject = {
        ...new SearchStaffInsurancePackage()
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    listStaffInsurancePackages = [];
    openCreateEditPopup = false;
    selectedStaffInsurancePackage = null;
    openConfirmDeletePopup = false;
    openConfirmDeleteListPopup = false;
    listOnDelete = [];
    openSelectMultiplePopup = false;
    openConfirmAssignPopup = false;
    openConfirmRemoveFromPosPopup = false;

    constructor() {
        makeAutoObservable(this);
    }

    handleClose = () => {
        this.openConfirmDeletePopup = false;
        this.openCreateEditPopup = false;
        this.openConfirmDeleteListPopup = false;
        this.openSelectMultiplePopup = false;
        this.openConfirmRemoveFromPosPopup = false;
    };

    resetStore = () => {
        this.searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
        this.totalElements = 0;
        this.totalPages = 0;
        this.listStaffInsurancePackages = [];
        this.openCreateEditPopup = false;
        this.selectedStaffInsurancePackage = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openSelectMultiplePopup = false;
        this.openConfirmAssignPopup = false;
    }

    setSelectedStaffInsurancePackage = (data) => {
        this.selectedStaffInsurancePackage = {
            ...new StaffInsurancePackage(),
            ...data,
            staffId: data?.staff?.id
        };
    };

    handleSetSearchObject = (searchObject) => {
        if (searchObject.insurancePackage == null) {
            searchObject.insurancePackageId = null;
        } else {
            searchObject.insurancePackageId = searchObject.insurancePackage.id;
        }

        if (searchObject.staff == null) {
            searchObject.staffId = null;
        } else {
            searchObject.staffId = searchObject.staff.id;
        }

        this.searchObject = { ...searchObject };
    }

    pagingStaffInsurancePackage = async () => {
        try {
            //const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ...this.searchObject,
            };
            const data = await pagingStaffInsurancePackage(payload);

            this.listStaffInsurancePackages = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingStaffInsurancePackage();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingStaffInsurancePackage();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteByIds) => {
        this.listOnDelete = deleteByIds;
    };

    getById = async (staffInsurancePackageId) => {
        try {
            const { data } = await getById(staffInsurancePackageId);
            this.selectedStaffInsurancePackage = data;
            this.openCreateEditPopup = true;
            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };



    handleDelete = (staffInsurancePackageId) => {
        this.selectedStaffInsurancePackage = { ...staffInsurancePackageId };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleOpenCreateEdit = async (staffInsurancePackageId) => {
        try {
            if (staffInsurancePackageId) {
                const { data } = await getById(staffInsurancePackageId);
                this.selectedStaffInsurancePackage = data;
            } else {
                this.selectedStaffInsurancePackage = {
                    ...new StaffInsurancePackage()
                };
            }

            this.openCreateEditPopup = true;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setOpenCreateEditPopup = (value) => {
        this.openCreateEditPopup = value;
    };

    handleConfirmDelete = async () => {
        try {
            const { data } = await deleteById(this?.selectedStaffInsurancePackage?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingStaffInsurancePackage();

            this.handleClose();

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

            // console.log("deleteData", deleteData)
            await deleteMultiple(deleteData);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingStaffInsurancePackage();
            this.listOnDelete = [];

            this.handleClose();


        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveStaffInsurancePackage = async (staffInsurancePackage) => {
        try {

            const { data } = await saveStaffInsurancePackage(staffInsurancePackage);

            toast.success("Thông tin gói bảo hiểm đã được lưu");
            this.handleClose();

            return data;
            // } catch (error) {
            //     console.error(error);
            //     toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");
            // }
        } catch (error) {
            console.error(error);
            // if (error.response.status == 409) {
            //     toast.error("Mã Tài khoản ngân hàng đã được sử dụng, vui lòng sử dụng mã Tài khoản ngân hàng khác", {
            //         autoClose: 5000,
            //         draggable: false,
            //         limit: 5,
            //     });
            // } else {
            toast.error(i18n.t("toast.error"));
            // }
        }
    };

    getSelectedIds = () => {
        const ids = [];
        this?.listOnDelete?.forEach(function (item) {
            ids.push(item?.id);
        });

        return ids;
    }
}
