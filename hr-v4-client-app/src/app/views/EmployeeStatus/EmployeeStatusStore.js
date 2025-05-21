import {makeAutoObservable} from "mobx";
import {
    checkCodeEmployeeStatus,
    deleteEmployeeStatus,
    getEmployeeStatus,
    pagingEmployeeStatus,
    saveEmployeeStatus,
} from "./EmployeeStatusService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
import {SearchObject} from "app/common/Model/SearchObject/SearchObject";

export default class EmployeeStatusStore {
    searchEmployeeStatus = new SearchObject();
    pageEmployeeStatus = null;
    selectedEmployeeStatusEdit = null;
    selectedEmployeeStatusDelete = null;

    selectedEmployeeStatusList = [];
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

    onPagingEmployeeStatus = async () => {
        // const searchObj = { ...new SearchObject(true) };
        // this.searchEmployeeStatus = searchObj;

        try {
            const res = await pagingEmployeeStatus(this.searchEmployeeStatus);
            if (!res?.data) {
                throw new Error("Not found data");
            }

            this.pageEmployeeStatus = res.data
        } catch {
            toast.warning(i18n.t("toast.error"));
        }
    }

    onChangeFormSearch = async (obj) => {
        // const value = SearchObject.checkSearchObject(this.searchEmployeeStatus, obj);
        // const url = SearchObject.pushSearchToUrl(value)
        // history.push(url.pathname + url.search);
        this.searchEmployeeStatus = {...this.searchEmployeeStatus, ...obj}
        await this.onPagingEmployeeStatus()
    }

    onOpenFormEmployeeStatusEdit = async (employeeStatusId) => {
        let employeeStatus = null;
        if (employeeStatusId) {
            employeeStatus = (await getEmployeeStatus(employeeStatusId)).data;
        }

        if (!employeeStatus) {
            employeeStatus = {id: null, name: null, languageKey: null, code: null}
        }

        this.selectedEmployeeStatusEdit = employeeStatus;
    }

    onSaveEmployeeStatus = async (employeeStatus) => {
        try {
            let response = await checkCodeEmployeeStatus(employeeStatus?.id, employeeStatus?.code);
            if (response.data) {
                throw new Error(i18n.t("toast.duplicate_code"))
            }

            if (employeeStatus?.active == '1') employeeStatus.active = true;
            else if (employeeStatus?.active == '0') employeeStatus.active = false;

            const res = await saveEmployeeStatus(employeeStatus);
            if (!res.data) {
                throw new Error()
            }

            toast.success(i18n.t(employeeStatus.id ? "toast.update_success" : "toast.add_success"));
            this.onClosePopup();
            this.onPagingEmployeeStatus();
        } catch (error) {
            toast.warning(error?.message ? error.message : i18n.t("toast.error"));
        }
    }

    setSelectedEmployeeStatusDelete = (employeeStatusId) => {
        this.selectedEmployeeStatusDelete = employeeStatusId;
    }

    onDeleteEmployeeStatus = async () => {
        try {
            const res = await deleteEmployeeStatus(this.selectedEmployeeStatusDelete);
            if (res.data) {
                toast.success(i18n.t("toast.delete_success"));
                this.onClosePopup();

                if (
                    this.pageEmployeeStatus.content.length === 1 && this.searchEmployeeStatus.pageIndex > 1 &&
                    this.pageEmployeeStatus.totalPages === this.searchEmployeeStatus.pageIndex
                ) {
                    this.onChangeFormSearch({pageIndex: this.searchEmployeeStatus.pageIndex - 1});
                } else {
                    this.onPagingEmployeeStatus();
                }
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    }

    handleSelectListEmployeeStatus = (listEmployeeStatus) => {
        this.selectedEmployeeStatusList = listEmployeeStatus;
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedEmployeeStatusList.length; i++) {
            try {
                await deleteEmployeeStatus(this.selectedEmployeeStatusList[i].id);
            } catch (error) {
                listAlert.push(this.selectedEmployeeStatusList[i].name);
                console.log(error);
                console.log(listAlert.toString());
                toast.warning(i18n.t("toast.error"));
            }
        }
        this.onClosePopup();
        this.onChangeFormSearch({pageIndex: 1});
        toast.success(i18n.t("toast.delete_success"));
    };

    onClosePopup = () => {
        this.selectedEmployeeStatusEdit = null;
        this.selectedEmployeeStatusDelete = null;
        this.shouldOpenConfirmationDeleteListDialog = false;
    }

    resetStore = () => {
        this.searchEmployeeStatus = new SearchObject();
        this.pageEmployeeStatus = null;
    }
}
