import { Grid } from "@material-ui/core";
import { DoneAll, GroupWork } from "@material-ui/icons";
import CloseIcon from '@material-ui/icons/Close';
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import TabsComponent from "app/common/Tab/TabComponent";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import OvertimeRequestListUpdatePopup from "./OvertimeRequestListUpdatePopup";
import OvertimeRequestForm from "./OvertimeRequestForm";
import OvertimeRequestList from "./OvertimeRequestList";
import OvertimeRequestIndexToolbar from "./OvertimeRequestIndexToolbar";
import HourglassEmptyIcon from '@material-ui/icons/HourglassEmpty';
import { useStore } from "app/stores";
import { getInitialOvertimeRequestFilter } from "./OvertimeRequestService";

const tabList = [
  { icon: <GroupWork fontSize="small" />, label: "Tất cả" },
  { icon: <HourglassEmptyIcon fontSize="small" />, label: "Chưa duyệt" },
  { icon: <DoneAll fontSize="small" />, label: "Đã duyệt" },
  { icon: <CloseIcon fontSize="small" />, label: "Không duyệt" }
];

function OvertimeRequestIndex() {

  const { overtimeRequestStore, hrRoleUtilsStore } = useStore();
  const { t } = useTranslation();

  const {
    pagingOvertimeRequest,
    openConfirmDeleteListPopup,
    openConfirmDeletePopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    resetStore,
    handleChangePagingStatus,
    searchObject,
    openCreateEditPopup,
    setPageIndex,
    handleSelectListDelete,
    openConfirmUpdateStatusPopup,
    openViewPopup,
    handleSetSearchObject
  } = overtimeRequestStore;

  const { checkAllUserRoles } = hrRoleUtilsStore;

  async function initalizeScreen() {
    try {
      const { data } = await getInitialOvertimeRequestFilter();

      handleSetSearchObject({
        ...searchObject,
        ...data
      });
      await pagingOvertimeRequest();

    } catch (error) {
      console.error(error);
    }
  }

  useEffect(() => {
    checkAllUserRoles()

    initalizeScreen();
    return resetStore
  }, []);

  async function handleChangeTabIndex(tabIndex) {
    handleChangePagingStatus(tabIndex);
    handleSelectListDelete([]);
    await setPageIndex(1);
  }

  return (
    <div className="content-index">
      <div className="index-breadcrumb py-6">
        <GlobitsBreadcrumb
          routeSegments={[
            { name: t("navigation.timeKeeping.title") },
            { name: t("navigation.overtimeRequest.title") },
          ]}
        />
      </div>

      <Grid container spacing={2}>
        <Grid item xs={12} className="index-card">
          <OvertimeRequestIndexToolbar />
        </Grid>

        <Grid item xs={12} className="index-card">
          <TabsComponent
            value={searchObject?.approvalStatus}
            handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
            tabList={tabList}
          />

          <OvertimeRequestList />
        </Grid>
      </Grid>

      {openCreateEditPopup && (
        <OvertimeRequestForm />
      )}
      {openViewPopup && (
        <OvertimeRequestForm readOnly={true} />
      )}

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

      {openConfirmUpdateStatusPopup && (
        <OvertimeRequestListUpdatePopup />
      )}
    </div>
  );
}

export default memo(observer(OvertimeRequestIndex));
