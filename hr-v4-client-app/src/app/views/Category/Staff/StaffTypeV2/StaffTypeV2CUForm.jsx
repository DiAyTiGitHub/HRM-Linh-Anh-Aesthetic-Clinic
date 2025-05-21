import React, { useState, useEffect, memo } from "react";
import { Formik, Form, Field } from "formik";
import { Grid, DialogActions, Button, DialogContent, makeStyles, Tooltip } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import { convertToConstantFormat } from "app/common/CommonFunctions";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";

function StaffTypeV2CUForm() {
  const { staffTypeStore } = useStore();
  const { t } = useTranslation();
  const {
    handleClose,
    saveStaffType,
    pagingStaffType,
    selectedStaffType,
    openCreateEditPopup
  } = staffTypeStore;

  const validationSchema = Yup.object({
    code: Yup.string().required(t("validation.code")).nullable(),
    name: Yup.string().required(t("validation.name")).nullable(),
    // type: Yup.number().required("Trường này là bắt buộc").nullable(),
    // calculationType: Yup.number().required("Trường này là bắt buộc").nullable(),
  });

  async function handleSaveForm(values) {
    await saveStaffType(values);
    await pagingStaffType();
  }

  const [initialValues, setInitialValues] = useState(selectedStaffType);

  useEffect(function () {
    setInitialValues(selectedStaffType);
  }, [selectedStaffType, selectedStaffType?.id]);

  return (
    <GlobitsPopupV2
      scroll={"body"}
      size="sm"
      open={openCreateEditPopup}
      noDialogContent
      title={(selectedStaffType?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + "loại nhân viên"}
      onClosePopup={handleClose}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={initialValues}
        onSubmit={handleSaveForm}
      >
        {({ isSubmitting, values, setFieldValue, initialValues }) => {

          return (
            <Form autoComplete="off" autocomplete="off">
              <div className="dialog-body">
                <DialogContent className="p-12">
                  <FormikFocusError />

                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <GlobitsTextField
                        label={"Mã loại nhân viên"}
                        validate
                        name="code"
                      />
                    </Grid>
                    <Grid item xs={12}>
                      <GlobitsTextField
                        validate
                        label={"Tên loại nhân viên"}
                        name="name"
                      />
                    </Grid>
                    <Grid item xs={6} sm={12}>
                      <GlobitsTextField
                        label={"Mô tả"}
                        name="description"
                        multiline
                        rows={4}
                      />
                    </Grid>
                  </Grid>

                </DialogContent>
              </div>

              <div className="dialog-footer dialog-footer-v2 py-8">
                <DialogActions className="p-0">
                  <div className="flex flex-space-between flex-middle">
                    <Button
                      startIcon={<BlockIcon />}
                      variant="contained"
                      className="mr-12 btn btn-secondary d-inline-flex"
                      color="secondary"
                      onClick={handleClose}
                      disabled={isSubmitting}
                    >
                      {t("general.button.cancel")}
                    </Button>
                    <Button
                      startIcon={<SaveIcon />}
                      className="mr-0 btn btn-primary d-inline-flex"
                      variant="contained"
                      color="primary"
                      type="submit"
                      disabled={isSubmitting}
                    >
                      {t("general.button.save")}
                    </Button>
                  </div>
                </DialogActions>
              </div>
            </Form>
          );
        }
        }
      </Formik>
    </GlobitsPopupV2>
  );
}

export default memo(observer(StaffTypeV2CUForm));