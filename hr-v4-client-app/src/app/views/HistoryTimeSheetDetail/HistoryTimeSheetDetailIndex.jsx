import { observer } from "mobx-react";
import React, { useEffect } from "react";
import { useStore } from "../../stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import StaffWorkingHistoryToolbar from "./HistoryTimeSheetDetailToolbar";
import StaffWorkSchedulePopupList from "./StaffWorkSchedulePopupList";
import StaffWorkScheduleV2CUForm from "../StaffWorkScheduleV2/StaffWorkScheduleV2CUForm";
import StaffWorkScheduleStatisticPopup from "../StaffWorkScheduleV2/StaffWorkScheduleStatisticPopup";
import "../TimekeepingReport/TimekeepingReportBoard/TimekeepingReportBoardStyles.scss";
import { getInitialStaffWorkScheduleFilter } from "../StaffWorkScheduleV2/StaffWorkScheduleService";

export default observer (function StaffWorkingHistoryIndex () {
  const {staffWorkScheduleStore, hrRoleUtilsStore} = useStore ();
  const {
    pagingStaffWorkSchedule,
    handleGetTotalStaffWorkSchedule,
    openViewStatistic,
    openViewPopup,
    openCreateEditPopup,
    resetStore,
    searchObject,
    handleSetSearchObject,
  } = staffWorkScheduleStore;

  const {checkAllUserRoles} = hrRoleUtilsStore;
  const {t} = useTranslation ();

  async function initalizeScreen () {
    try {
      const {data} = await getInitialStaffWorkScheduleFilter ();

      handleSetSearchObject ({
        ... searchObject,
        ... data
      });
      await checkAllUserRoles ()
      await pagingStaffWorkSchedule ();
      await handleGetTotalStaffWorkSchedule ()

    } catch (error) {
      console.error (error);
    }
  }

  useEffect (() => {
    initalizeScreen ();
    return resetStore;
  }, []);

  useEffect (() => {
    if (!openCreateEditPopup) {
      pagingStaffWorkSchedule ()
      handleGetTotalStaffWorkSchedule ()
    }
  }, [openCreateEditPopup]);

  useEffect (() => {
    handleGetTotalStaffWorkSchedule ()
  }, [searchObject?.pageIndex, searchObject?.pageSize]);

  return (
      <div className="content-index">
        <div className="index-breadcrumb">
          <GlobitsBreadcrumb
              routeSegments={[
                {name:t ("navigation.historyTimeSheetDetail.title")},
              ]}
          />
        </div>

        <Grid className="index-card" container spacing={2}>
          <Grid item xs={12}>
            <StaffWorkingHistoryToolbar/>
          </Grid>
          <Grid item xs={12}>
            <StaffWorkSchedulePopupList/>
          </Grid>
        </Grid>

        {openCreateEditPopup && <StaffWorkScheduleV2CUForm/>}

        {openViewPopup && <StaffWorkScheduleV2CUForm readOnly={true}/>}

        {openViewStatistic && (<StaffWorkScheduleStatisticPopup/>)}
      </div>
  );
});
