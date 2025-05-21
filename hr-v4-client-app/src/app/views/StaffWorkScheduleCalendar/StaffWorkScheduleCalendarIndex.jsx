/* eslint-disable react-hooks/exhaustive-deps */
import { Grid } from "@material-ui/core";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import StaffWorkScheduleAssignForm from "../StaffWorkScheduleV2/StaffWorkScheduleAssignForm";
import StaffWorkScheduleStatisticPopup from "../StaffWorkScheduleV2/StaffWorkScheduleStatisticPopup";
import StaffWorkScheduleV2CUForm from "../StaffWorkScheduleV2/StaffWorkScheduleV2CUForm";
import StaffWorkScheduleCalendarToolbar from "./StaffWorkScheduleCalendarToolbar";
import WorkScheduleByDays from "./WorkSchedule/WorkScheduleByDays";
import StaffWorkScheduleMultipleForm from "./WorkSchedule/StaffWorkScheduleMultipleForm";
import { getInitialTimekeepingReportFilter } from "./StaffWorkScheduleCalendarService";

function StaffWorkScheduleCalendarIndex() {
    const { t } = useTranslation();
    const location = useLocation();

    const {
        staffWorkScheduleCalendarStore,
        staffWorkScheduleStore,
        hrRoleUtilsStore

    } = useStore();

    const {
        searchObject,
        getWorkingScheduleByFilter,
        resetStore,
        handleSetSearchObject
    } = staffWorkScheduleCalendarStore;

    const {
        checkAllUserRoles,
        checkHasShiftAssignmentPermission,
        getCurrentLoginUser

    } = hrRoleUtilsStore;

    const {
        openConfirmDeletePopup,
        handleClose,
        openCreateEditPopup,
        handleConfirmDelete,
        openAssignForm,
        openViewPopup,
        openViewStatistic

    } = staffWorkScheduleStore;

    async function fetchScreenData() {
        try {
            const { data } = await getInitialTimekeepingReportFilter();

            handleSetSearchObject({
                ...searchObject,
                ...data
            });

            
            await getWorkingScheduleByFilter();
            
            const response = await checkHasShiftAssignmentPermission();
            await checkAllUserRoles();

        } catch (error) {
            console.error(error);
        }
    }

    useEffect(() => {
        resetStore();

        fetchScreenData();

        return resetStore;
    }, []);

    return (
        <div className='content-index'>
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb routeSegments={[{ name: t("Bảng phân ca làm việc") }]} />
            </div>

            <Grid container spacing={2}>
                <Grid item xs={12} className='index-card'>
                    <StaffWorkScheduleCalendarToolbar />
                </Grid>

                {searchObject?.fromDate && searchObject?.toDate && (
                    <Grid item xs={12} className='index-card mt-8'>
                        <WorkScheduleByDays />
                    </Grid>
                )}
            </Grid>

            {openAssignForm && <StaffWorkScheduleAssignForm additionalFunction={getWorkingScheduleByFilter} />}

            <StaffWorkScheduleMultipleForm handleAfterSubmit={getWorkingScheduleByFilter} />

            {openCreateEditPopup && <StaffWorkScheduleV2CUForm pagingAfterEdit={getWorkingScheduleByFilter} />}

            {openViewStatistic && <StaffWorkScheduleStatisticPopup />}

            {openViewPopup && <StaffWorkScheduleV2CUForm readOnly={openViewPopup} />}

            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={() => {
                        handleConfirmDelete();
                        getWorkingScheduleByFilter();
                    }}
                    title={t("confirm_dialog.delete.title")}
                    text={"Bạn có chắc muốn xóa lịch làm việc này?"}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}
        </div>
    );
}

export default observer(StaffWorkScheduleCalendarIndex);
