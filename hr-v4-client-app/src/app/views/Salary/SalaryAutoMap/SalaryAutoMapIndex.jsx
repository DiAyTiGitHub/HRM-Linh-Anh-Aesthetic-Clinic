import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "../../../stores";
import { Grid } from "@material-ui/core";
import GlobitsBreadcrumb from "../../../common/GlobitsBreadcrumb";
import { useTranslation } from "react-i18next";
import SalaryAutoMapList from "./SalaryAutoMapList";
import { useTheme } from "@material-ui/core/styles";
import SalaryAutoMapForm from "./SalaryAutoMapForm";

function SalaryAutoMapIndex() {
  const { salaryAutoMapStore, hrRoleUtilsStore } = useStore();
  const { t } = useTranslation();

  const {
    updatePageData,
    shouldOpenEditorDialog
  } = salaryAutoMapStore;

  const {
    checkAllUserRoles
  } = hrRoleUtilsStore;

  useEffect(() => {
    checkAllUserRoles();

    updatePageData();
  }, [updatePageData]);

  const theme = useTheme();

  return (
    <div className="content-index">
      <div className="index-breadcrumb py-6">
        <GlobitsBreadcrumb
          routeSegments={[
            { name: t("navigation.salary") },
            { name: t("salaryAutoMap.title") }]}
        />
      </div>

      <Grid className="index-card" container spacing={2}>
        {/* <Grid item md={6} xs={12}>
          <ButtonGroup
            color="container"
            aria-label="outlined primary button group"
          >
            <Button
              startIcon={<AddIcon />}
              onClick={() => handleEditSalaryAutoMap()}
            >
              {t("general.button.add")}
            </Button>
          </ButtonGroup>
        </Grid>  */}

        {shouldOpenEditorDialog && (
          <SalaryAutoMapForm
            open={shouldOpenEditorDialog}
          />
        )}

        {/* <GlobitsConfirmationDialog
          open={shouldOpenConfirmationDialog}
          onConfirmDialogClose={handleClose}
          onYesClick={handleConfirmDelete}
          title={t("confirm_dialog.delete.title")}
          text={t("confirm_dialog.delete.text")}
          agree={t("confirm_dialog.delete.agree")}
          cancel={t("confirm_dialog.delete.cancel")}
        /> */}

        {/* <GlobitsConfirmationDialog
          open={shouldOpenConfirmationDeleteListDialog}
          onConfirmDialogClose={handleClose}
          onYesClick={handleConfirmDeleteList}
          title={t("confirm_dialog.delete_list.title")}
          text={t("confirm_dialog.delete_list.text")}
          agree={t("confirm_dialog.delete_list.agree")}
          cancel={t("confirm_dialog.delete_list.cancel")}
        /> */}

        <Grid item xs={12}>
          <SalaryAutoMapList />
        </Grid>
      </Grid>
    </div>
  );
}

export default memo(observer(SalaryAutoMapIndex));
