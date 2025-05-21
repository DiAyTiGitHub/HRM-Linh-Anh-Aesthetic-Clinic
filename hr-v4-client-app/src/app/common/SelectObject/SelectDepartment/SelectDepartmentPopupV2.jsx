import React, { memo, useEffect, useState } from "react";
import { observer } from "mobx-react";
import { Form, Formik, useFormikContext } from "formik";
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
import * as Yup from "yup";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import SelectDepartmentListInStaff from "app/views/HumanResourcesInformation/Component/SelectDepartmentListInStaff";

function SelectDepartmentPopupV2(props) {
  const { t } = useTranslation();

  const {
    open,
    handleClosePopup,
    setOpenDepartmentPopup,
    organizationId,
    name = 'department',
    clearFields,
  } = props;

  const handleConfirmSelectDepartment = () => {
    //setOpenDepartmentPopup(false);
    handleClosePopup();
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
        <Grid container spacing={2}>
          <Grid item lg={6} md={6} sm={4} xs={4}></Grid>

          <Grid item lg={6} md={6} sm={8} xs={8}>
            <DepartmentFilters organizationId={organizationId}/>
          </Grid>

          <Grid item xs={12}>
            <SelectDepartmentListInStaff
              handleClose={handleConfirmSelectDepartment}
              organizationId={organizationId}
              name={name}
              clearFields={clearFields}
            />
          </Grid>
        </Grid>
      </DialogContent>
    </GlobitsPopupV2>

  );
}

export default memo(observer(SelectDepartmentPopupV2));
