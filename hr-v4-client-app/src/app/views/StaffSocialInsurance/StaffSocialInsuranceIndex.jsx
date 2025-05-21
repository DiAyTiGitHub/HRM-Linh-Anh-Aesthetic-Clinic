import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import StaffSocialInsuranceList from "./StaffSocialInsuranceList";
import StaffSocialInsuranceIndexToolbar from "./StaffSocialInsuranceIndexToolbar";
import MonetizationOnIcon from '@material-ui/icons/MonetizationOn';
import MoneyOffIcon from '@material-ui/icons/MoneyOff';
import AttachMoneyIcon from '@material-ui/icons/AttachMoney';
import GroupWorkIcon from '@material-ui/icons/GroupWork';
import TabsComponent from "app/common/Tab/TabComponent";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import StaffSocialInsuranceChangePaidStatusPopup from "./StaffSocialInsuranceChangePaidStatusPopup";
import { getInitialStaffSocialInsuranceFilter } from "./StaffSocialInsuranceService";
import StaffSInsuranceCreateSinglePopup from "./StaffSInsuranceCreateSinglePopup";
import StaffSInsuranceCreateMultiplePopup from "./StaffSInsuranceCreateMultiplePopup";

const tabList = [
  { icon: <GroupWorkIcon fontSize="small" />, label: "Tất cả" },
  { icon: <MoneyOffIcon fontSize="small" />, label: "Chưa chi trả" },
  { icon: <AttachMoneyIcon fontSize="small" />, label: "Đã chi trả" },
];

function StaffSocialInsuranceIndex() {
  const { t } = useTranslation();

  const {
    staffSocialInsuranceStore,
    hrRoleUtilsStore

  } = useStore();

  const {
    checkAllUserRoles
  } = hrRoleUtilsStore;


  const {
    pagingStaffSocialInsurance,
    searchObject,
    resetStore,
    handleChangeViewPaidStatus,
    handleSelectListDelete,
    setPageIndex,
    openConfirmDeletePopup,
    openConfirmDeleteListPopup,
    handleClose,
    handleConfirmDeleteList,
    handleConfirmDelete,
    openConfirmChangeStatus,
    checkAdmin,
    handleSetSearchObject,
    openAutoCreateInsuranceTicketPopup,
    openCreateSingleInsuranceTicketPopup,
  } = staffSocialInsuranceStore;

  async function initalizeScreen() {
    try {
      const { data } = await getInitialStaffSocialInsuranceFilter();

      handleSetSearchObject(
        {
          ...searchObject,
          ...data
        }
      );

      await pagingStaffSocialInsurance();
    }
    catch (error) {
      console.error(error);
    }
  }

  useEffect(() => {
    resetStore();

    checkAllUserRoles();
    initalizeScreen();

    return resetStore;
  }, []);

  async function handleChangeTabIndex(tabIndex) {
    handleChangeViewPaidStatus(tabIndex);
    handleSelectListDelete([]);
    await setPageIndex(1);
  }

  return (
    <div className="content-index">
      <div className="index-breadcrumb py-6">
        <GlobitsBreadcrumb
          routeSegments={[
            { name: t("navigation.insurance.root") },
            { name: t("navigation.insurance.staffSocialInsurance") }
          ]} />
      </div>

      <Grid container spacing={2}>
        <Grid item xs={12} className="index-card">
          <StaffSocialInsuranceIndexToolbar />
        </Grid>

        <Grid item xs={12} className="index-card">
          <TabsComponent
            value={searchObject?.paidStatus}
            handleChange={(_, tabIndex) => handleChangeTabIndex(tabIndex)}
            tabList={tabList}
          />

          <StaffSocialInsuranceList />
        </Grid>
      </Grid>

      {openConfirmChangeStatus && (
        <StaffSocialInsuranceChangePaidStatusPopup />
      )}

      {
        openConfirmDeletePopup && (
          <GlobitsConfirmationDialog
            open={openConfirmDeletePopup}
            onConfirmDialogClose={handleClose}
            onYesClick={handleConfirmDelete}
            title={t("confirm_dialog.delete.title")}
            text={t("confirm_dialog.delete.text")}
            agree={t("confirm_dialog.delete.agree")}
            cancel={t("confirm_dialog.delete.cancel")}
          />
        )
      }

      {
        openConfirmDeleteListPopup && (
          <GlobitsConfirmationDialog
            open={openConfirmDeleteListPopup}
            onConfirmDialogClose={handleClose}
            onYesClick={handleConfirmDeleteList}
            title={t("confirm_dialog.delete_list.title")}
            text={t("confirm_dialog.delete_list.text")}
            agree={t("confirm_dialog.delete_list.agree")}
            cancel={t("confirm_dialog.delete_list.cancel")}
          />
        )
      }

      {
        openAutoCreateInsuranceTicketPopup && (
          <StaffSInsuranceCreateMultiplePopup />
        )
      }

      {
        openCreateSingleInsuranceTicketPopup && (
          <StaffSInsuranceCreateSinglePopup />
        )
      }
      ,

    </div >
  );
}

export default memo(observer(StaffSocialInsuranceIndex));