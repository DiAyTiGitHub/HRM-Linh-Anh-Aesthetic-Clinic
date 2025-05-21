import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useStore } from "app/stores";
import { Grid, Button, IconButton } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import NoteIcon from "@material-ui/icons/Note";
import SearchIcon from '@material-ui/icons/Search';
import { Form, Formik } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import SalaryConfigList from "./SalaryConfigList";
import SalaryConfigCUForm from "./SalaryConfigCUForm";
import SalaryConfigIndexToolbar from "./SalaryConfigIndexToolbar";

function SalaryConfigIndex() {
  const { salaryConfigStore } = useStore();
  const { t } = useTranslation();

  const {
    handleDeleteList,
    pagingSalaryConfig,
    handleOpenCreateEdit,
    openConfirmDeleteListPopup,
    openConfirmDeletePopup,
    openCreateEditPopup,
    handleClose,
    handleConfirmDelete,
    handleConfirmDeleteList,
    searchObject,
    listOnDelete,
    resetStore,
    handleSetSearchObject
  } = salaryConfigStore;

  useEffect(() => {
    pagingSalaryConfig();

    return resetStore;
  }, []);

  async function handleFilter(values) {
    const newSearchObject = {
      ...values,
      pageIndex: 1,
    };
    handleSetSearchObject(newSearchObject);
    await pagingSalaryConfig();
  }

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb routeSegments={[{ name: t("navigation.salary") }, { name: t("navigation.salaryConfig.title") }]} />
      </div>
      <Grid className="index-card" container spacing={2}>
        <Grid item xs={12}>
          <SalaryConfigIndexToolbar />
        </Grid>

        <Grid item xs={12}>
          <SalaryConfigList />
        </Grid>
      </Grid>

      {openCreateEditPopup && (
        <SalaryConfigCUForm />
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
    </div>
  );
}

export default memo(observer(SalaryConfigIndex));
