import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import ConfirmOvertimeList from "./ConfirmOvertimeList";
import ConfirmOvertimeToolbar from "./ConfirmOvertimeToolbar";
import AlarmOnIcon from '@material-ui/icons/AlarmOn';
import SnoozeIcon from '@material-ui/icons/Snooze';
import LocalCafeIcon from '@material-ui/icons/LocalCafe';
import AlarmOffIcon from '@material-ui/icons/AlarmOff';
import GroupWorkIcon from '@material-ui/icons/GroupWork';
import ConfirmOvertimeCUForm from "./ConfirmOvertimeCUForm";
import { getInitialStaffWorkScheduleFilter } from "../StaffWorkScheduleV2/StaffWorkScheduleService";

function ConfirmOvertimeIndex() {
  const { t } = useTranslation();
  const { confirmOvertimeStore, hrRoleUtilsStore } = useStore();

  const {
    openCreateEditPopup,
    resetStore,
    handleChangeWorkingStatus,
    handleSelectListDelete,
    setPageIndex,
    openViewPopup,
    getMinOTMinutes,
    handleSetSearchObject,
    searchObject,
    pagingWorkScheduleResult
  } = confirmOvertimeStore;

  const {
    checkAllUserRoles
  } = hrRoleUtilsStore

  async function initalizeScreen() {
    try {
      const { data } = await getInitialStaffWorkScheduleFilter();

      handleSetSearchObject({
        ...searchObject,
        ...data
      });
      await getMinOTMinutes();
      await pagingWorkScheduleResult();

    } catch (error) {
      console.error(error);
    }
  }

  useEffect(() => {
    checkAllUserRoles();

    initalizeScreen();
    return resetStore
  }, []);

  const tabList = [
    { icon: <GroupWorkIcon fontSize="small" />, label: "Tất cả" },
    { icon: <AlarmOnIcon fontSize="small" />, label: "Đi làm đủ" },
    { icon: <SnoozeIcon fontSize="small" />, label: "Đi làm thiếu giờ" },
    { icon: <LocalCafeIcon fontSize="small" />, label: "Nghỉ có phép" },
    { icon: <AlarmOffIcon fontSize="small" />, label: "Nghỉ không phép" },
    // { icon: <AlarmAddIcon fontSize="small" />, label: "Nghỉ không phép" },
    // { icon: <DirectionsRunIcon fontSize="small" />, label: "Đi làm muộn" },

  ];

  async function handleChangeTabIndex(tabIndex) {
    handleChangeWorkingStatus(tabIndex);
    handleSelectListDelete([]);
    await setPageIndex(1);
  }

  return (
    <div className="content-index">
      <div className="index-breadcrumb py-6">
        <GlobitsBreadcrumb
          routeSegments={[
            { name: t("navigation.confirmOvertime.title") }
          ]}
        />
      </div>

      <Grid container spacing={2}>
        <Grid item xs={12} className="index-card">
          <ConfirmOvertimeToolbar />
        </Grid>

        <Grid item xs={12} className="index-card">
          {/* <TabsComponent
            value={searchObject?.workingStatus}
            handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
            tabList={tabList}
          /> */}

          <ConfirmOvertimeList />
        </Grid>
      </Grid>

      {openCreateEditPopup && (
        <ConfirmOvertimeCUForm />
      )}

      {openViewPopup && (
        <ConfirmOvertimeCUForm readOnly={true} />
      )}


    </div>
  );
}

export default memo(observer(ConfirmOvertimeIndex));
