/* eslint-disable react-hooks/exhaustive-deps */
import { Grid } from "@material-ui/core";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import { getCurrentStaff } from "../profile/ProfileService";
import TimekeepingReportBoard from "./TimekeepingReportBoard/TimekeepingReportBoard";
import TimekeepingReportToolbar from "./TimekeepingReportToolbar";
import SchedulesInDayPopup from "./SchedulesInDay/SchedulesInDayPopup";
import StaffWorkScheduleStatisticPopup from "../StaffWorkScheduleV2/StaffWorkScheduleStatisticPopup";
import { getInitialTimekeepingReportFilter } from "../StaffWorkScheduleCalendar/StaffWorkScheduleCalendarService";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";


function TimekeepingReportIndex() {
  const { t } = useTranslation();
  const location = useLocation();

  const {
    timekeepingReportStore,
    hrRoleUtilsStore,
    staffWorkScheduleStore
  } = useStore();

  const {
    searchObject,
    getTimekeepingReportByFitler,
    resetStore,
    handleSetSearchObject,
    openScheduleInDayPopup,
    handleClose
  } = timekeepingReportStore;

  const {
    openViewStatistic,
    handleCloseViewStatisticPopup,
    selectedStaffWorkSchedule,
    openConfirmDeletePopup,
    handleClose: handleCloseSchedule,
    handleConfirmDelete
  } = staffWorkScheduleStore;

  async function actionConfirmDelete() {
    try {
      await handleConfirmDelete();
      handleClose();
      getTimekeepingReportByFitler();
    }
    catch (error) {

    }
  }

  const {
    checkAllUserRoles,
    checkHasShiftAssignmentPermission
  } = hrRoleUtilsStore;

  async function initalizeScreen() {
    try {
      const { data } = await getInitialTimekeepingReportFilter();

      handleSetSearchObject(
        {
          ...searchObject,
          ...data
        }
      );

      await getTimekeepingReportByFitler();
    }
    catch (error) {
      console.error(error);
    }
  }

  useEffect(() => {
    resetStore();

    checkAllUserRoles();

    checkHasShiftAssignmentPermission();
    initalizeScreen();

    return resetStore;
  }, []);

  return (
    <div className="content-index pb-48">
      <div className="index-breadcrumb py-6">
        <GlobitsBreadcrumb routeSegments={[{ name: t("navigation.timekeepingReport.title") }]} />
      </div>

      <Grid container spacing={2}>
        <Grid item xs={12} className="index-card">
          <TimekeepingReportToolbar />
        </Grid>

        {
          searchObject?.fromDate && searchObject?.toDate && (
            <Grid item xs={12} className="index-card mt-8">
              <TimekeepingReportBoard />
            </Grid>
          )
        }

      </Grid>

      {
        openScheduleInDayPopup && (
          <SchedulesInDayPopup />
        )
      }

      {
        openViewStatistic && (
          <StaffWorkScheduleStatisticPopup
            openViewPopup={openViewStatistic}
            staffWorkSchedule={selectedStaffWorkSchedule}
            handleClose={handleCloseViewStatisticPopup}
          />
        )
      }

      {openConfirmDeletePopup && (
        <GlobitsConfirmationDialog
          open={openConfirmDeletePopup}
          onConfirmDialogClose={handleCloseSchedule}
          onYesClick={actionConfirmDelete}
          title={t("confirm_dialog.delete.title")}
          text={t("confirm_dialog.delete.text")}
          agree={t("confirm_dialog.delete.agree")}
          cancel={t("confirm_dialog.delete.cancel")}
        />
      )}
    </div>
  );
};

export default memo(observer(TimekeepingReportIndex));