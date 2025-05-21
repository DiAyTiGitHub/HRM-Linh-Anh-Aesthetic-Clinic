import React, { useState, useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../../../stores";
import * as Yup from "yup";
import GlobitsAsyncAutocomplete from "../../../../common/form/GlobitsAsyncAutocomplete";
import GlobitsTextField from "../../../../common/form/GlobitsTextField";
import { getAllPositions } from "./DisciplinaryReasonService";
import { getAllDepartments } from "./DisciplinaryReasonService";
// import "./ExamCategoryStyle.scss";
import GlobitsPopup from "app/common/GlobitsPopup";
import { observer } from "mobx-react";
import CachedIcon from "@material-ui/icons/Cached";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";

function DisciplinaryReasonForm() {
  const { disciplinaryReasonStore } = useStore();
  const { t } = useTranslation();
  const {
    handleClose,
    selectedExamCategory,
    saveOrUpdate,
    updateExamCategory,
    shouldOpenEditorDialog,
  } = disciplinaryReasonStore;

  const validationSchema = Yup.object({
    code:Yup.string().nullable().required("Bạn chưa nhập mã"),
    name:Yup.string().nullable().required("Bạn chưa nhập tên"),
  });

  async function hanledFormSubmit(values) {
    saveOrUpdate(values);
  }

  return (
    <GlobitsPopupV2
      size="sm"
      open={shouldOpenEditorDialog}
      title={
        (selectedExamCategory?.id
          ? t("general.button.edit")
          : t("general.button.add")) +
        " " +
        "lý do kỉ luật"
      }
      onClosePopup={handleClose}
      noDialogContent
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={selectedExamCategory}
        onSubmit={(values, actions) => {
          hanledFormSubmit(values);
          // actions.setSubmitting(false);
        }}
      >
        {({ setFieldValue, values, isSubmitting, errors, touched }) => {
          return (
            <Form autoComplete="off">
              <div className="dialog-body">
                <DialogContent className="o-hidden p-12">
                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <GlobitsTextField
                        label={"Mã lý do kỉ luật"}
                        validate
                        name="code"
                      />
                    </Grid>
                    <Grid item xs={12}>
                      <GlobitsTextField
                        validate
                        label={"Tên lý do kỉ luật"}
                        name="name"
                      />
                    </Grid>
                    <Grid item xs={6} sm={12}>
                      <GlobitsTextField label={"Mô tả"} name="description" multiline rows={4}/>
                    </Grid>
                  </Grid>
                </DialogContent>
              </div>
              <div className="dialog-footer">
                <DialogActions className="p-0">
                  <div className="flex flex-space-between flex-middle">
                    <Button
                      startIcon={<BlockIcon />}
                      variant="contained"
                      className="mr-12 btn btn-gray d-inline-flex"
                      // color="secondary"
                      onClick={() => handleClose()}
                    >
                      {t("general.button.cancel")}
                    </Button>
                    <Button
                      startIcon={<SaveIcon />}
                      className="mr-0 btn btn-success d-inline-flex"
                      variant="contained"
                      type="submit"
                    >
                      {t("general.button.save")}
                    </Button>
                  </div>
                </DialogActions>
              </div>
            </Form>
          );
        }}
      </Formik>
      {/* <ResetPassWord open={open} setOpen={setOpen} /> */}
    </GlobitsPopupV2>
  );
}

export default memo(observer(DisciplinaryReasonForm));
