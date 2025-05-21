import { Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import { useStore } from "app/stores";
import TimeSheetDetailIndexToolbar from "./TimeSheetDetailIndexToolbar";
import TimeSheetDetailList from "./TimeSheetDetailList";
import TimeSheetDetailEditForm from "./TimeSheetDetailEditForm";
import TimeSheetDetailCheckFormV2 from "./TimeSheetDetailCheckFormV2";
import TimeSheetDetailCUForm from "./TimeSheetDetailCUForm";
import { getInitialTimesheetDetailFilter } from "./TimeSheetDetailService";
import ExportLATimekeepingDataPopup from "./ExportLATimekeepingDataPopup";

function TimeSheetDetailIndex() {
  const { t } = useTranslation();

  const { timeSheetDetailStore, hrRoleUtilsStore } = useStore();

  const {
    pagingTimeSheetDetail,
    openCreateEditPopup,
    openConfirmDeletePopup,
    openConfirmDeleteListPopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    resetStore,
    openFormTimeSheetDetailCheck,
    handleSaveTimeSheet,
    openTimeSheetDetailCUForm,
    openViewPopup,
    handleSetSearchObject,
    searchObject,
    openExportLATimekeepingDataPopup
  } = timeSheetDetailStore;

  const {
    checkAllUserRoles,
    checkHasShiftAssignmentPermission
  } = hrRoleUtilsStore;

  async function initalizeScreen() {
    try {
      const { data } = await getInitialTimesheetDetailFilter();

      handleSetSearchObject({
        ...searchObject,
        ...data
      });
      await pagingTimeSheetDetail();
    } catch (error) {
      console.error(error);
    }
  }

  useEffect(() => {
    resetStore();

    checkHasShiftAssignmentPermission();
    checkAllUserRoles();
    initalizeScreen();


    return resetStore;
  }, []);

  return (
    <div className='content-index'>
      <div className='index-breadcrumb py-6'>
        <GlobitsBreadcrumb
          routeSegments={[
            { name: t("navigation.timeKeeping.title") },
            { name: t("navigation.timeSheetDetail.title") },
          ]}
        />
      </div>

      <Grid className='index-card' container spacing={2}>
        <Grid item xs={12}>
          <TimeSheetDetailIndexToolbar />
        </Grid>

        <Grid item xs={12}>
          <TimeSheetDetailList />
        </Grid>
      </Grid>

      {openCreateEditPopup && <TimeSheetDetailEditForm />}

      {openTimeSheetDetailCUForm && <TimeSheetDetailCUForm />}

      {openViewPopup && <TimeSheetDetailCUForm readOnly={true} />}

      {openFormTimeSheetDetailCheck && (
        <TimeSheetDetailCheckFormV2
          handleSumbit={async (values) => {
            await handleSaveTimeSheet(values);
            await pagingTimeSheetDetail();
          }}
        />
      )}

      {
        openExportLATimekeepingDataPopup && (
          <ExportLATimekeepingDataPopup />
        )
      }

      {openConfirmDeletePopup && (
        <GlobitsConfirmationDialog
          open={openConfirmDeletePopup}
          onConfirmDialogClose={handleClose}
          onYesClick={handleConfirmDelete}
          title={t("confirm_dialog.delete.title")}
          text={t("confirm_dialog.delete.text")}
          agree={t("confirm_dialog.delete.agree")}
          cancel={t("confirm_dialog.delete.cancel")}
        />
      )}

      {openConfirmDeleteListPopup && (
        <GlobitsConfirmationDialog
          open={openConfirmDeleteListPopup}
          onConfirmDialogClose={handleClose}
          onYesClick={handleConfirmDeleteList}
          title={t("confirm_dialog.delete_list.title")}
          text={t("confirm_dialog.delete_list.text")}
          agree={t("confirm_dialog.delete_list.agree")}
          cancel={t("confirm_dialog.delete_list.cancel")}
        />
      )}
    </div>
  );
}

export default memo(observer(TimeSheetDetailIndex));
