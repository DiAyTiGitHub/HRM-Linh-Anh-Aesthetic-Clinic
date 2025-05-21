import { Grid } from "@material-ui/core";
import AlarmOffIcon from "@material-ui/icons/AlarmOff";
import AlarmOnIcon from "@material-ui/icons/AlarmOn";
import GroupWorkIcon from "@material-ui/icons/GroupWork";
import SnoozeIcon from "@material-ui/icons/Snooze";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import TabsComponent from "app/common/Tab/TabComponent";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router";
import TimeSheetDetailEditForm from "../TimeSheetDetail/TimeSheetDetailEditForm";
import StaffWorkScheduleAssignForm from "./StaffWorkScheduleAssignForm";
import StaffWorkScheduleCreateForm from "./SWSCreateSingle/StaffWorkScheduleCreateForm";
import StaffWorkScheduleV2CUForm from "./StaffWorkScheduleV2CUForm";
import StaffWorkScheduleV2List from "./StaffWorkScheduleV2List";
import StaffWorkScheduleV2Toolbar from "./StaffWorkScheduleV2Toolbar";
import StaffWorkScheduleStatisticPopup from "./StaffWorkScheduleStatisticPopup";
import ReStatisticSchedulesPopup from "./ReStatisticSchedulesPopup";
import { getInitialStaffWorkScheduleFilter } from "./StaffWorkScheduleService";
import TimeSheetDetailCUForm from "../TimeSheetDetail/TimeSheetDetailCUForm";

function StaffWorkScheduleV2Index() {
  const { t } = useTranslation();
  const { id } = useParams();

  const {
    staffWorkScheduleStore,
    hrRoleUtilsStore,
    timeSheetDetailStore
  } = useStore();

  const {
    checkAllUserRoles,
    checkHasShiftAssignmentPermission
  } = hrRoleUtilsStore;

  const {
    openTimeSheetDetailCUForm
  } = timeSheetDetailStore;

  const {
    pagingStaffWorkSchedule,
    openConfirmDeleteListPopup,
    openConfirmDeletePopup,
    openCreateEditPopup,
    openFormSWSPopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    resetStore,
    openAssignForm,
    handleChangeWorkingStatus,
    handleSelectListDelete,
    setPageIndex,
    searchObject,
    openViewPopup,
    openViewStatistic,
    openConfirmLockSchedulesPopup,
    handleLockSchedulesMultiple,
    openReStatisticSchedulePopup,
    handleSetSearchObject,
    selectedStaffWorkSchedule,
    handleOpenCreateEdit
  } = staffWorkScheduleStore;

  async function handleAfterSubmitTSD() {
    if (openCreateEditPopup && selectedStaffWorkSchedule?.id) {
      handleOpenCreateEdit(selectedStaffWorkSchedule?.id);
    }
  }

  async function initalizeScreen() {
    try {
      const { data } = await getInitialStaffWorkScheduleFilter();

      handleSetSearchObject(
        {
          ...searchObject,
          ...data
        }
      );

      await pagingStaffWorkSchedule();
    } catch (error) {
      console.error(error);
    }
  }

  useEffect(() => {
    checkHasShiftAssignmentPermission();
    checkAllUserRoles();
    initalizeScreen();

    return resetStore;
  }, []);

  const tabList = [
    { icon: <GroupWorkIcon fontSize='small' />, label: "Tất cả" },
    { icon: <AlarmOnIcon fontSize='small' />, label: "Đi làm đủ" },
    { icon: <SnoozeIcon fontSize='small' />, label: "Đi làm thiếu giờ" },
    { icon: <AlarmOffIcon fontSize='small' />, label: "Không đi làm" },
  ];

  async function handleChangeTabIndex(tabIndex) {
    handleChangeWorkingStatus(tabIndex);
    handleSelectListDelete([]);
    await setPageIndex(1);
  }

  return (
    <div className='content-index'>
      <div className='index-breadcrumb py-6'>
        <GlobitsBreadcrumb routeSegments={[{ name: t("navigation.timeSheet.staffWorkSchedule") }]} />
      </div>

      <Grid container spacing={2}>
        <Grid item xs={12} className='index-card'>
          <StaffWorkScheduleV2Toolbar />
        </Grid>

        <Grid item xs={12} className='index-card'>
          <TabsComponent
            value={searchObject?.workingStatus}
            handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
            tabList={tabList}
          />

          <StaffWorkScheduleV2List />
        </Grid>
      </Grid>

      {openCreateEditPopup && <StaffWorkScheduleV2CUForm />}


      {openViewPopup && <StaffWorkScheduleV2CUForm readOnly={true} />}

      <TimeSheetDetailEditForm />

      {openFormSWSPopup && <StaffWorkScheduleCreateForm />}

      {openAssignForm && <StaffWorkScheduleAssignForm additionalFunction={pagingStaffWorkSchedule} />}

      {
        openViewStatistic && (
          <StaffWorkScheduleStatisticPopup />
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

      {openConfirmLockSchedulesPopup && (
        <GlobitsConfirmationDialog
          open={openConfirmLockSchedulesPopup}
          onConfirmDialogClose={handleClose}
          onYesClick={handleLockSchedulesMultiple}
          title={t("Chốt ca làm việc")}
          text={t("Bạn có chắc chắn muốn chốt ca làm việc đã chọn không? Sau khi chốt ca bạn sẽ không thể thay đổi ca làm việc")}
          agree={t("confirm_dialog.delete_list.agree")}
          cancel={t("confirm_dialog.delete_list.cancel")}
        />
      )}


      {openReStatisticSchedulePopup && (
        <ReStatisticSchedulesPopup />
      )}

      {openTimeSheetDetailCUForm && (
        <TimeSheetDetailCUForm
          handleAfterSubmit={handleAfterSubmitTSD}
        />
      )}

    </div>
  );
}

export default memo(observer(StaffWorkScheduleV2Index));
