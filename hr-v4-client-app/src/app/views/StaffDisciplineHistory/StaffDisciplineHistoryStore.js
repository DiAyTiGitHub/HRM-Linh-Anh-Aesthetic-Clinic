import { makeAutoObservable } from "mobx";
import {
    pagingStaffDisciplineHistory,
    getById,
    saveStaffDisciplineHistory,
    deleteMultiple,
    deleteById,
} from "./StaffDisciplineHistoryService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { SearchStaffDisciplineHistory } from "app/common/Model/SearchObject/SearchStaffDisciplineHistory";
import { StaffDisciplineHistory } from "app/common/Model/HumanResource/StaffDisciplineHistory";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class StaffDisciplineHistoryStore {
    intactSearchObject = {
        ...new SearchStaffDisciplineHistory()
    };

    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    listStaffDisciplineHistory = [];
    openCreateEditPopup = false;
    selectedStaffDisciplineHistory = null;
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
        this.listStaffDisciplineHistory = [];
        this.openCreateEditPopup = false;
        this.selectedStaffDisciplineHistory = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openSelectMultiplePopup = false;
        this.openConfirmAssignPopup = false;
    }

    setselectedStaffDisciplineHistory = (data) => {
        this.selectedStaffDisciplineHistory = {
            ...new StaffDisciplineHistory(),
            ...data,
            staffId: data?.staff?.id,
            staff: data?.staff
        };
    };

    handleSetSearchObject = (searchObject) => {
        if (searchObject.bank == null) {
            searchObject.bankId = null;
        } else {
            searchObject.bankId = searchObject.bank.id;
        }

        if (searchObject.person == null) {
            searchObject.personId = null;
        } else {
            searchObject.personId = searchObject.person.id;
        }

        this.searchObject = { ...searchObject };
    }

    pagingStaffDisciplineHistory = async () => {
        try {
            //const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ...this.searchObject,
            };
            const data = await pagingStaffDisciplineHistory(payload);

            this.listStaffDisciplineHistory = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingStaffDisciplineHistory();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingStaffDisciplineHistory();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteByIds) => {
        this.listOnDelete = deleteByIds;
    };

    getById = async (staffDisciplineHistoryId) => {
        try {
            const { data } = await getById(staffDisciplineHistoryId);
            this.selectedStaffDisciplineHistory = data;
            this.openCreateEditPopup = true;
            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };



    handleDelete = (personBankAccount) => {
        this.selectedStaffDisciplineHistory = { ...personBankAccount };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleOpenCreateEdit = async (staffDisciplineHistoryId) => {
        try {
            if (staffDisciplineHistoryId) {
                const { data } = await getById(staffDisciplineHistoryId);
                this.selectedStaffDisciplineHistory = data;
            } else {
                this.selectedStaffDisciplineHistory = {
                    ...new StaffDisciplineHistoryStore()
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
            const { data } = await deleteById(this?.selectedStaffDisciplineHistory?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingStaffDisciplineHistory();

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

            await this.pagingStaffDisciplineHistory();
            this.listOnDelete = [];

            this.handleClose();


        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    saveStaffDisciplineHistory = async (personBankAccount) => {
        try {

            const { data } = await saveStaffDisciplineHistory(personBankAccount);

            toast.success("Thông tin Quá trình kỷ luật đã được lưu");
            this.handleClose();

            return data;
            // } catch (error) {
            //     console.error(error);
            //     toast.error(i18n.t("toast.error") + ", vui lòng kiểm tra lại thông tin nhập, đảm bảo mã không được trùng");
            // }
        } catch (error) {
            console.error(error);
            // if (error.response.status == 409) {
            //     toast.error("Mã Quá trình kỷ luật đã được sử dụng, vui lòng sử dụng mã Quá trình kỷ luật khác", {
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
