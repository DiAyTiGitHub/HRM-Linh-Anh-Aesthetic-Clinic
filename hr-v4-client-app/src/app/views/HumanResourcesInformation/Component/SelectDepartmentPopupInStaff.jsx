import React, { useEffect, useState } from "react";
import { observer } from "mobx-react";
import { useFormikContext } from "formik";
import Draggable from "react-draggable";
import {
  Dialog,
  DialogTitle,
  Icon,
  IconButton,
  DialogContent,
  Grid,
  DialogActions,
  Button,
} from "@material-ui/core";
import Paper from "@material-ui/core/Paper";
import { useTranslation } from "react-i18next";
import DepartmentFilters from "app/views/Department/DepartmentFilters";
import SelectDepartmentListInStaff from "./SelectDepartmentListInStaff";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";

function SelectDepartmentPopupInStaff(props) {
  const { t } = useTranslation();
  const {
    open,
    handleClosePopup,
    setOpenDepartmentPopup,
    organizationId,
    name = 'department',
    clearFields
  } = props;

  const handleConfirmSelectDepartment = () => {
    setOpenDepartmentPopup(false);
  };

  return (
    <GlobitsPopupV2
      size="md"
      scroll={"body"}
      open={open}
      noDialogContent
      title={t("Lựa chọn đơn vị")}
      onClosePopup={handleClosePopup}
      popupId={"popupselectdep"}
    >
      <DialogContent className="o-hidden p-12">
        <Grid container className="mb-16">
          <Grid item lg={6} md={6} sm={4} xs={4}></Grid>
          <Grid item lg={6} md={6} sm={8} xs={8}>
            <DepartmentFilters />
          </Grid>
        </Grid>
        <Grid item xs={12}>
          <SelectDepartmentListInStaff
            handleClose={handleConfirmSelectDepartment}
            organizationId={organizationId}
            name={name}
            clearFields={clearFields}
          />
        </Grid>
      </DialogContent>
    </GlobitsPopupV2>
  );
}

export default observer(SelectDepartmentPopupInStaff);
