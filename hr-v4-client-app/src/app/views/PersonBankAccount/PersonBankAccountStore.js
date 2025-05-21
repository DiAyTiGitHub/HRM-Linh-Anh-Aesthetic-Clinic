import { makeAutoObservable } from "mobx";
import {
    pagingPersonBankAccount,
    getById,
    savePersonBankAccount,
    deleteMultiple,
    deleteById,
} from "./PersonBankAccountService";
import "react-toastify/dist/ReactToastify.css";
import { toast } from "react-toastify";
import i18n from "i18n";
import { SearchPersonBankAccount } from "app/common/Model/SearchObject/SearchPersonBankAccount";
import { PersonBankAccount } from "app/common/Model/HumanResource/PersonBankAccount";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default class PersonBankAccountStore {
    intactSearchObject = {
        ...new SearchPersonBankAccount()
    };
    searchObject = JSON.parse(JSON.stringify(this.intactSearchObject));
    totalElements = 0;
    totalPages = 0;
    listPersonBankAccounts = [];
    openCreateEditPopup = false;
    selectedPersonBankAccount = null;
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
        this.listPersonBankAccounts = [];
        this.openCreateEditPopup = false;
        this.selectedPersonBankAccount = null;
        this.openConfirmDeletePopup = false;
        this.openConfirmDeleteListPopup = false;
        this.listOnDelete = [];
        this.openSelectMultiplePopup = false;
        this.openConfirmAssignPopup = false;
    }

    setSelectedPersonBankAccount = (data) => {
        this.selectedPersonBankAccount = {
            ...new PersonBankAccount(),
            ...data,
            personId: data?.person?.id
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

    pagingPersonBankAccount = async () => {
        try {
            //const loggedInStaff = localStorageService.getLoginUser();
            const payload = {
                ...this.searchObject,
            };
            const data = await pagingPersonBankAccount(payload);

            this.listPersonBankAccounts = data.data.content;
            this.totalElements = data.data.totalElements;
            this.totalPages = data.data.totalPages;

        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    setPageIndex = async (page) => {
        this.searchObject.pageIndex = page;

        await this.pagingPersonBankAccount();
    };

    setPageSize = async (event) => {
        this.searchObject.pageSize = event.target.value;
        this.searchObject.pageIndex = 1;

        await this.pagingPersonBankAccount();
    };

    handleChangePage = async (event, newPage) => {
        await this.setPageIndex(newPage);
    };

    handleSelectListDelete = (deleteByIds) => {
        this.listOnDelete = deleteByIds;
    };

    getById = async (personBankAccountId) => {
        try {
            const { data } = await getById(personBankAccountId);
            this.selectedPersonBankAccount = data;
            this.openCreateEditPopup = true;
            return data;
        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };



    handleDelete = (personBankAccount) => {
        this.selectedPersonBankAccount = { ...personBankAccount };
        this.openConfirmDeletePopup = true;
    };

    handleDeleteList = () => {
        this.openConfirmDeleteListPopup = true;
    };

    handleOpenCreateEdit = async (personBankAccountId) => {
        try {
            if (personBankAccountId) {
                const { data } = await getById(personBankAccountId);
                this.selectedPersonBankAccount = data;
            } else {
                this.selectedPersonBankAccount = {
                    ...new PersonBankAccount()
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
            const { data } = await deleteById(this?.selectedPersonBankAccount?.id);
            toast.success(i18n.t("toast.delete_success"));

            await this.pagingPersonBankAccount();

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

            await this.pagingPersonBankAccount();
            this.listOnDelete = [];

            this.handleClose();


        } catch (error) {
            console.error(error);
            toast.error(i18n.t("toast.error"));
        }
    };

    savePersonBankAccount = async (personBankAccount) => {
        try {

            const { data } = await savePersonBankAccount(personBankAccount);

            toast.success("Thông tin Tài khoản ngân hàng đã được lưu");
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
