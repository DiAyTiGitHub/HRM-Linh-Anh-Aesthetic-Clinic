import {makeAutoObservable} from "mobx";
import {
    pagingWorkingStatus,
    getWorkingStatus,
    deleteWorkingStatus,
    saveWorkingStatus,
    checkCodeWorkingStatus,
} from "./WorkingStatusService";
import "react-toastify/dist/ReactToastify.css";
import {toast} from "react-toastify";
import i18n from "i18n";
import {SearchObject} from "app/common/Model/SearchObject/SearchObject";
import history from "../../../history";

export default class WorkingStatusStore {
    searchWorkingStatus = new SearchObject();
    pageWorkingStatus = null;
    selectedWorkingStatusEdit = null;
    selectedWorkingStatusDelete = null;

    selectedWorkingStatusList = [];
    shouldOpenConfirmationDeleteListDialog = false;

    constructor() {
        makeAutoObservable(this);
    }

    resetWorkingStatusStore = () => {
        this.searchWorkingStatus = new SearchObject();
        this.pageWorkingStatus = null;
        this.selectedWorkingStatusEdit = null;
        this.selectedWorkingStatusDelete = null;
        this.selectedWorkingStatusList = [];
        this.shouldOpenConfirmationDeleteListDialog = false;
    }
    listWorkingStatus = [];

    getAllWorkingStatus = async () => {
        if (this.listWorkingStatus && this?.listWorkingStatus.length > 0) return;

        try {
            const searchObject = {
                pageIndex: 1,
                pageSize: 1000
            };

            const {data} = await pagingWorkingStatus(searchObject);
            this.listWorkingStatus = data?.content;
        } catch (error) {
            console.error(error);
            toast.error("Lỗi xảy ra khi lấy dữ liệu trạng thái công việc");
        }
    }

    getAllWorkingStatusInUse = async () => {
        if (this?.listWorkingStatus && this?.listWorkingStatus?.length > 0) return;

        try {
            const searchObject = {
                pageIndex: 1,
                pageSize: 1000
            };

            const {data} = await pagingWorkingStatus(searchObject);
            const toUseData = [];
            data?.content?.forEach(function (status) {
                if (status?.statusValue) {
                    toUseData.push(status);
                }
            });

            this.listWorkingStatus = toUseData;
        } catch (error) {
            console.error(error);
            toast.error("Lỗi xảy ra khi lấy dữ liệu trạng thái công việc");
        }
    }

    onPagingWorkingStatus = async () => {
        // const searchObj = {...new SearchObject(true)};
        // this.searchWorkingStatus = searchObj;

        try {
            const res = await pagingWorkingStatus(this.searchWorkingStatus);
            if (!res?.data) {
                throw new Error("Not found data");
            }

            this.pageWorkingStatus = res.data
        } catch {
            toast.warning(i18n.t("toast.error"));
        }
    }

    onChangeFormSearch = async (obj) => {
        // const value = SearchObject.checkSearchObject(this.searchWorkingStatus, obj);
        // const url = SearchObject.pushSearchToUrl(value)
        // history.push(url.pathname + url.search);
        this.searchWorkingStatus = {...this.searchWorkingStatus, ...obj}
        await this.onPagingWorkingStatus();
    }

    onOpenFormWorkingStatusEdit = async (workingStatusId) => {
        let workingStatus = null;
        if (workingStatusId) {
            workingStatus = (await getWorkingStatus(workingStatusId)).data;
        }

        if (!workingStatus) {
            workingStatus = {id: null, code: null, name: null, statusValue: null}
        }

        this.selectedWorkingStatusEdit = workingStatus;
    }

    onSaveWorkingStatus = async (workingStatus) => {
        try {
            let response = await checkCodeWorkingStatus(workingStatus.id, workingStatus.code);
            if (response.data) {
                throw new Error(i18n.t("toast.duplicate_code"))
            }

            const res = await saveWorkingStatus(workingStatus);
            if (!res.data) {
                throw new Error()
            }

            toast.success(i18n.t(workingStatus.id ? "toast.update_success" : "toast.add_success"));
            this.onClosePopup();
            this.onPagingWorkingStatus();
        } catch (error) {
            toast.warning(error?.message ? error.message : i18n.t("toast.error"));
        }
    }

    setSelectedWorkingStatusDelete = (workingStatusId) => {
        this.selectedWorkingStatusDelete = workingStatusId;
    }

    onDeleteWorkingStatus = async () => {
        try {
            const res = await deleteWorkingStatus(this.selectedWorkingStatusDelete);
            if (res.data) {
                toast.success(i18n.t("toast.delete_success"));
                this.onClosePopup();

                if (
                    this.pageWorkingStatus.content.length === 1 && this.searchWorkingStatus.pageIndex > 1 &&
                    this.pageWorkingStatus.totalPages === this.searchWorkingStatus.pageIndex
                ) {
                    this.onChangeFormSearch({pageIndex: this.searchWorkingStatus.pageIndex - 1});
                } else {
                    this.onPagingWorkingStatus();
                }
            }
        } catch (error) {
            console.log(error);
            toast.warning(i18n.t("toast.error"));
        }
    }

    handleSelectListWorkingStatus = (workingStatus) => {
        this.selectedWorkingStatusList = workingStatus;
    };

    handleDeleteList = () => {
        this.shouldOpenConfirmationDeleteListDialog = true;
    };

    handleConfirmDeleteList = async () => {
        let listAlert = [];
        for (var i = 0; i < this.selectedWorkingStatusList.length; i++) {
            try {
                await deleteWorkingStatus(this.selectedWorkingStatusList[i].id);
            } catch (error) {
                listAlert.push(this.selectedWorkingStatusList[i].name);
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
        this.selectedWorkingStatusEdit = null;
        this.selectedWorkingStatusDelete = null;
        this.shouldOpenConfirmationDeleteListDialog = false;
    }
}
