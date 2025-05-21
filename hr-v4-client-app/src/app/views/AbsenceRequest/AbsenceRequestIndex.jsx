import { Grid } from "@material-ui/core";
import { DoneAll, GroupWork, ThumbDown } from "@material-ui/icons";
import CheckBoxIcon from '@material-ui/icons/CheckBox';
import CloseIcon from '@material-ui/icons/Close';
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import TabsComponent from "app/common/Tab/TabComponent";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import AbsenceRequestListUpdatePopup from "./AbsenceRequestListUpdatePopup";
import AbsenceRequestForm from "./AbsenceRequestForm";
import AbsenceRequestList from "./AbsenceRequestList";
import AbsenceRequestIndexToolbar from "./AbsenceRequestIndexToolbar";

const tabList = [
  { icon: <GroupWork fontSize="small" />, label: "Tất cả" },
  { icon: <CloseIcon fontSize="small" />, label: "Chưa duyệt" },
  { icon: <DoneAll fontSize="small" />, label: "Đã duyệt" },
  { icon: <ThumbDown fontSize="small" />, label: "Không duyệt" }
];

function AbsenceRequestIndex() {
  const { absenceRequestStore } = useStore();
  const { t } = useTranslation();

  const {
    pagingAbsenceRequest,
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
    
  } = absenceRequestStore;

  useEffect(() => {
    pagingAbsenceRequest();

    return resetStore;
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
            { name: t("navigation.absenceRequest.title") },
          ]}
        />
      </div>

      <Grid container spacing={2}> 
        <Grid item xs={12} className="index-card">
          <AbsenceRequestIndexToolbar /> 
        </Grid>

        <Grid item xs={12} className="index-card">
          <TabsComponent
            value={searchObject?.approvalStatus}
            handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
            tabList={tabList}
          />

          <AbsenceRequestList />
        </Grid>
      </Grid>

      {openCreateEditPopup && (
        <AbsenceRequestForm />
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
        <AbsenceRequestListUpdatePopup />
      )}
    </div>
  );
}

export default memo(observer(AbsenceRequestIndex));
