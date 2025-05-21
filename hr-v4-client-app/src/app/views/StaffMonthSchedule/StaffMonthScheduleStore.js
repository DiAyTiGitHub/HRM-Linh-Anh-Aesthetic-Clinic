import { getFullYear, getMonth } from "app/LocalFunction";
import localStorageService from "app/services/localStorageService";
import i18n from "i18n";
import { makeAutoObservable } from "mobx";
import { toast } from "react-toastify";
import { getTimeKeepingByMonth, createTimeKeeping, getTimeSheetByDate, checkTimeSheet, saveTimeSheet } from "./StaffMonthScheduleService";

const dataDefaultFormTimeKeep = {
    staffId: null,
    timeSheetId: null,
    workingDate: null,
    timeSheetShiftWorkPeriods: [
        {
            shiftWorkTimePeriod: null,
            timeSheet: null,
            workingFormat: null,
            note: null,
        },
    ],
}

export default class TimeKeepStore {
    isAdmin = false;
    isUser = false;
    dataTimeKeep = {
        staffId: null,
        staffName: null,
        items: []
    };
    isReload = false;
    dataEditTimeKeep = dataDefaultFormTimeKeep;
    openFormTimeKeep = false;
    selectedTimeSheet = null;

    constructor() {
        makeAutoObservable(this);
    }

    getTimeKeepByMonth = (month = new Date(), staffId) => {
        this.checkAdmin();
        getTimeKeepingByMonth({ yearReport: getFullYear(month), monthReport: getMonth(month) + 1, staffId }).then((response) => {
            if (response.data.hasOwnProperty('items')) {
                this.dataTimeKeep = response.data;
            } else {
                toast.warning(response.data)
            }
        }).catch(() => {
            toast.warning(i18n.t("toast.error"));
        })
    }

    handleOpenFormTimeKeep = (timeKeeping, workingDate, shiftWorkList) => {
        let newDataFormTimeKeep = null;
        if (timeKeeping) {
            newDataFormTimeKeep = {
                ...timeKeeping,
                timeSheetShiftWorkPeriods: shiftWorkList.map((item) => {
                    const itemShiftWork = timeKeeping.timeSheetShiftWorkPeriods.find(e => e.shiftWorkTimePeriod.id === item.shiftWorkTimePeriod.id);
                    if (itemShiftWork) {
                        return itemShiftWork;
                    } else {
                        return item;
                    }
                })
            }
        } else {
            newDataFormTimeKeep = { ...dataDefaultFormTimeKeep, workingDate, timeSheetShiftWorkPeriods: shiftWorkList };
        }
        if (this.dataTimeKeep?.staffId) {
            newDataFormTimeKeep.staffId = this.dataTimeKeep.staffId;
        }

        this.dataEditTimeKeep = newDataFormTimeKeep;
        this.openFormTimeKeep = true;
    };

    handleOpenFormTimeKeepV2 = async (obj) => {
        await getTimeSheetByDate(obj)
            .then(({ data }) => {
                this.selectedTimeSheet = data;
                this.openFormTimeKeep = true;
            })
            .catch((error) => {
                console.error(error);
                toast.error("Lấy dữ liệu có lỗi");
            })
    }

    createTimeKeeping = async (timeKeeping) => {
        try {
            await createTimeKeeping(timeKeeping);
            // toast.success(i18n.t("toast.add_success"));
            toast.success("Điểm danh thành công!");
            this.handleClosePopup();
            this.isReload = !this.isReload
        } catch (error) {
            console.log(error);
            // i18n.t("toast.error")
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau");
        }
    };

    checkAdmin = () => {
        let roles =
            localStorageService
                .getLoginUser()
                ?.user?.roles?.map((item) => item.authority) || [];
        let auth = ["HR_MANAGER", "ROLE_ADMIN"];

        if (roles.some((role) => auth.indexOf(role) !== -1)) {
            this.isAdmin = true;
            this.isUser = false;
        } else {
            this.isAdmin = false;
            this.isUser = true;
        }
    };

    handleClosePopup = () => {
        this.openFormTimeKeep = false;
    }

    // export excel
    shouldOpenImportDialog = false;
    shouldOpenImportDialogV2 = false;

    setShouldOpenImportDialog = (state) => {
        this.shouldOpenImportDialog = state;
    };

    setShouldOpenImportDialogV2 = (state) => {
        this.shouldOpenImportDialogV2 = state;
    };

    getTimeSheetByDate = (workingDate) => {
        getTimeSheetByDate(workingDate).then((response) => {
            this.selectedTimeSheet = response.data;
        }).catch(() => {
            toast.warning(i18n.t("toast.error"));
        })
    }
    handleCheckTimeSheet = async (data) => {
        try {
            await checkTimeSheet(data);
            toast.success("Điểm danh thành công!");
            this.handleClosePopup();
            this.isReload = !this.isReload
        } catch (error) {
            console.log(error);
            // i18n.t("toast.error")
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau");
        }
    };
    handleSaveTimeSheet = async (data) => {
        try {
            const response = await saveTimeSheet({
                ...data,
                staffId: data?.staff?.id
            });
            toast.success(response?.data);
            this.handleClosePopup();
            this.isReload = !this.isReload
        } catch (error) {
            console.log(error);
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau");
        }
    };
}