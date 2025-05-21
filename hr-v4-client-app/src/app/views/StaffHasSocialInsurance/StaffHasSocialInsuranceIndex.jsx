import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import StaffHasSocialInsuranceList from "./StaffHasSocialInsuranceList";
import StaffHasSocialInsuranceIndexToolbar from "./StaffHasSocialInsuranceIndexToolbar";
import ChooseExportTypeStaffHasSIPopup from "./ChooseExportTypeStaffHasSIPopup";

function StaffHasSocialInsuranceIndex() {
  const { t } = useTranslation();
  const {
    staffHasSocialInsuranceStore, hrRoleUtilsStore
  } = useStore();

  const {
    pagingStaffLabourAgreement,
    resetStore,
    openChooseExportType
  } = staffHasSocialInsuranceStore;

  const {
    checkAllUserRoles
  } = hrRoleUtilsStore;

  useEffect(() => {
    checkAllUserRoles()
    pagingStaffLabourAgreement();
    return resetStore;
  }, []);

  return (
    <div className="content-index">
      <div className="index-breadcrumb py-6">
        <GlobitsBreadcrumb
          routeSegments={[
            { name: t("navigation.insurance.root") },
            { name: t("navigation.insurance.staffHasInsurance") }
          ]} />
      </div>

      <Grid container spacing={2}>
        <Grid item xs={12} className="index-card">
          <StaffHasSocialInsuranceIndexToolbar />
        </Grid>

        <Grid item xs={12} className="index-card">
          <StaffHasSocialInsuranceList />
        </Grid>
      </Grid>

      {
        openChooseExportType && (
          <ChooseExportTypeStaffHasSIPopup />
        )
      }

    </div>
  );
}

export default memo(observer(StaffHasSocialInsuranceIndex));