import React, { memo, useEffect } from "react";
import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import StaffWorkSchedulePopupList from "../../StaffWorkScheduleV2/StaffWorkSchedulePopupList";
import StaffWorkingHistoryToolbar from "../../HistoryTimeSheetDetail/HistoryTimeSheetDetailToolbar";

function HistoryTimeSheetDetailPopup ({staffId}) {
  const {staffWorkScheduleStore, staffStore} = useStore ();

  const {
    searchObject,
    pagingStaffWorkSchedule,
    handleSetSearchObject,
    handleGetTotalStaffWorkSchedule,
    resetStore
  } = staffWorkScheduleStore;

  const {basicInfo, openSchedulePopup, handleSetOpenSchedulePopup} = useStore ().payrollStore;


  const {t} = useTranslation ();
  const handleFilter = async (value) => {
    const newSearchObject = {
      ... value,
      pageIndex:1,
    };
    handleSetSearchObject (newSearchObject);
    await pagingStaffWorkSchedule ();
  }

  const getSearchDto = async () => {
    const newSearchObject = {
      ... searchObject,
      staff:{
        id:staffId,
      },
      staffId:staffId,
      salaryPeriod:basicInfo?.salaryPeriod,
      fromDate:basicInfo?.salaryPeriod?.fromDate,
      toDate:basicInfo?.salaryPeriod?.toDate,
    };
    await handleFilter (newSearchObject);
    await handleGetTotalStaffWorkSchedule ();
  }

  useEffect (() => {
    getSearchDto ()
    return resetStore
  }, []);
  useEffect (() => {
    handleGetTotalStaffWorkSchedule ()
  }, [searchObject?.pageIndex, searchObject?.pageSize]);

  return (
      <GlobitsPopupV2
          scroll={"body"}
          size="xl"
          open={openSchedulePopup}
          noDialogContent
          title={"Xem thống kê chấm công chi tiết"}
          onClosePopup={() => handleSetOpenSchedulePopup (false)}
      >
        <div>
          <div className="dialog-body">
            <DialogContent className="p-12">
              <Grid className="index-card" container spacing={2}>
                <Grid item xs={12}>
                  <StaffWorkingHistoryToolbar oneStaff={true}/>
                </Grid>

                <Grid item xs={12}>
                  <StaffWorkSchedulePopupList/>
                </Grid>
              </Grid>
            </DialogContent>
          </div>

          <div className="dialog-footer dialog-footer-v2 py-8">
            <DialogActions className="p-0">
              <div className="flex flex-space-between flex-middle">
                <Button startIcon={<BlockIcon/>} variant='contained'
                        className={`btn-secondary d-inline-flex`}
                        color='secondary'
                        onClick={() => handleSetOpenSchedulePopup (false)}>
                  {t ("general.button.cancel")}
                </Button>
              </div>
            </DialogActions>
          </div>
        </div>
      </GlobitsPopupV2>
  );
}

export default memo (observer (HistoryTimeSheetDetailPopup));