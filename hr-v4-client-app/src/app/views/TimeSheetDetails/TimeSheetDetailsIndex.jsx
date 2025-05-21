import React, { memo, useEffect } from "react";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import { toast } from "react-toastify";
import { Grid } from "@material-ui/core";
import ProjectCreatePopup from "./ProjectCreatePopup";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import StaffPopupNotDetail from "./StaffNotDetail/StaffPopupNotDetail";
import "./time-sheet-styles.scss";
import { useParams } from "react-router";
import Month from "./Tab/Month";
import Weeks from "./Tab/Weeks";
import Dates from "./Tab/Date";
import KeyboardArrowLeftIcon from "@material-ui/icons/KeyboardArrowLeft";
import KeyboardArrowRightIcon from "@material-ui/icons/KeyboardArrowRight";
import TimeSheetDetailsForm from "./TimeSheetDetailsForm";
import AutoGenerateTimeSheetSection from "./Popup/AutoGenerateTimeSheetSection";
import PopupWrapper from "./Popup/TimesheetDetailPopupV2/PopupWrapper";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

function TimeSheetDetailsIndex() {
  const { timeSheetDetailsStore, timeSheetStore, } = useStore();
  const { id } = useParams();
  const { t } = useTranslation();
  const {
    shouldOpenEditorProjectDialog,
    openPopupConfirm,
    handleConfirmDeleteTimeSheet,
    handleClosePopup,
    currentStaff,
    getStaff,
    view,
    setView
  } = timeSheetDetailsStore;

  const {
    shouldOpenPopupListStaffNotDetail,
  } = timeSheetStore;

  function renderButtonView() {
    return (
      <div className="btn-group">
        <button className={`btn-calendar ${view === 'month' && 'active'}`} onClick={() => setView('month')}>{t("general.month")}</button>
        <button className={`btn-calendar ${view === 'weeks' && 'active'}`} onClick={() => setView('weeks')}>{t("general.week")}</button>
        <button className={`btn-calendar ${view === 'day' && 'active'}`} onClick={() => setView('day')}>{t("general.day")}</button>
      </div>
    )
  }

  useEffect(() => {
    if (id) getStaff(id);
  }, [id]);

  return (
    <div className="content-index timeSheetDetailScreen">
      <div className="index-breadcrumb">
        <Grid container spacing={2} className="pt-12">
          <Grid item xs={12} md={8}>
            <AutoGenerateTimeSheetSection />
          </Grid>

          <Grid item xs={12} md={4}>
            <GlobitsBreadcrumb
              noRight
              routeSegments={[
                { name: "Công việc" },
                { name: t("timeSheet.title") + `: ${currentStaff ? currentStaff?.name : ""}` }
              ]}
            />
          </Grid>
        </Grid>
      </div>
      <Grid className="index-card mt-12" container spacing={2}>
        <Grid item xs={12}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              {view === 'month' && <Month renderButtonView={renderButtonView} staffId={id} />}
              {view === 'weeks' && <Weeks renderButtonView={renderButtonView} staffId={id} />}
              {view === 'day' && <Dates renderButtonView={renderButtonView} staffId={id} />}
            </Grid>
          </Grid>
        </Grid>

        {/* old timesheet detail form */}
        {/* <TimeSheetDetailsForm /> */}

        {/* new timesheet detail form V2 - writen by diayti */}
        <PopupWrapper />

        {shouldOpenPopupListStaffNotDetail && (
          <StaffPopupNotDetail open={shouldOpenPopupListStaffNotDetail} />
        )}

        {shouldOpenEditorProjectDialog && (
          <ProjectCreatePopup open={shouldOpenEditorProjectDialog} />
        )}

        {openPopupConfirm && (
          <GlobitsConfirmationDialog
            open={openPopupConfirm}
            onConfirmDialogClose={handleClosePopup}
            onYesClick={handleConfirmDeleteTimeSheet}
            title={t("confirm_dialog.delete.title")}
            text={t("confirm_dialog.delete.text")}
            agree={t("confirm_dialog.delete.agree")}
            cancel={t("confirm_dialog.delete.cancel")}
          />
        )}
      </Grid>
    </div >
  );
}

export default memo(observer(TimeSheetDetailsIndex));

export function RenderButtonNextDate({ preDate, nextDate, setDate }) {
  return (
    <div className="btn-group">
      <button onClick={preDate} className='btn-calendar'>
        <KeyboardArrowLeftIcon />
      </button>
      <button onClick={nextDate} className='btn-calendar'>
        <KeyboardArrowRightIcon />
      </button>
      <button onClick={() => setDate(new Date())} className="btn-calendar ml-8">
        Today
      </button>
    </div>
  )
}