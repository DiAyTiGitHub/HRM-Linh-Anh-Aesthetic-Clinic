import React, { useState, useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent, ButtonGroup } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import SalaryResultBasicInfoSection from "./SalaryResultBasicInfoSection";
import RemoveRedEyeIcon from '@material-ui/icons/RemoveRedEye';
import { useHistory } from "react-router-dom";
import ConstantList from "app/appConfig";
import SettingsApplicationsIcon from '@material-ui/icons/SettingsApplications';

function SalaryResultCUForm() {
  const { salaryResultStore } = useStore();
  const history = useHistory();
  const { t } = useTranslation();
  const {
    handleClose,
    saveSalaryResult,
    pagingSalaryResult,
    selectedSalaryResult,
    openCreateEditPopup
  } = salaryResultStore;

  const validationSchema = Yup.object({
    code: Yup.string().required(t("validation.code")).nullable(),
    name: Yup.string().required(t("validation.name")).nullable(),
    // salaryTemplate: Yup.object().required("Chưa chọn mẫu bảng lương").nullable(),
    salaryPeriod: Yup.object().required("Chưa chọn kì lương").nullable(),
  });

  async function handleSaveOnly(values) {
    try {
      await saveSalaryResult(values);
      await pagingSalaryResult();
    }
    catch (err) {
      console.error(err);
    }
  }

  const [initialValues, setInitialValues] = useState(selectedSalaryResult);

  useEffect(function () {
    setInitialValues(selectedSalaryResult);
  }, [selectedSalaryResult, selectedSalaryResult?.id]);

  return (
    <GlobitsPopupV2
      scroll={"body"}
      size="md"
      open={openCreateEditPopup}
      noDialogContent
      title={(selectedSalaryResult?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + "bảng lương"}
      onClosePopup={handleClose}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={initialValues}
        onSubmit={handleSaveOnly}
      >
        {({ isSubmitting, values, setFieldValue, initialValues, validateForm, errors, setFieldTouched }) => {
          async function handleSaveAndGoConfig() {
            const validationErrors = await validateForm();
            if (Object.keys(validationErrors).length !== 0) {
              // Mark all fields as touched to display error messages
              Object.keys(validationErrors).forEach((field) => {
                setFieldTouched(field, true, false); // Mark the field as touched
              });

              // Handle the case where there are validation errors
              console.error("Validation Errors:", validationErrors);
              return;
            }

            try {
              const data = await saveSalaryResult(values);
              // console.log("Data saved:", data);

              // You can add further actions here
              // history.push(ConstantList.ROOT_PATH + `salary-result-detail-read-only/` + data?.id);
              if (data?.id)
                history.push(ConstantList.ROOT_PATH + `salary-result-board-config/` + data?.id);
            } catch (err) {
              console.error("Error saving data:", err);
            }
          }

          return (
            <Form autoComplete="off" autocomplete="off">
              <div className="dialog-body">
                <DialogContent className="p-12">
                  <FormikFocusError />

                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <SalaryResultBasicInfoSection />
                    </Grid>

                  </Grid>

                </DialogContent>
              </div>

              <div className="dialog-footer dialog-footer-v2 py-8 px-12">
                <DialogActions className="p-0">
                  {/* <ButtonGroup
                    className="filterButtonV4"
                    color="container"
                    aria-label="outlined primary button group"
                  >
                    <Button
                      startIcon={<BlockIcon />}
                      className="d-inline-flex py-2 px-8 btnHrStyle"
                      onClick={handleClose}
                      disabled={isSubmitting}
                    >
                      {t("general.button.cancel")}
                    </Button>

                    <Button
                      startIcon={<SaveIcon />}
                      className="d-inline-flex py-2 px-8 btnHrStyle"
                      type="submit"
                      disabled={isSubmitting}
                    >
                      {t("general.button.save")}
                    </Button>

                    <Button
                      startIcon={<RemoveRedEyeIcon />}
                      className="d-inline-flex py-2 px-8 btnHrStyle"
                      type="button"
                      disabled={isSubmitting}
                      onClick={handleSaveAndGoConfig}
                    >
                      Lưu và xem chi tiết
                    </Button>
                  
                  </ButtonGroup> */}

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
                      className="mr-12 btn btn-primary d-inline-flex"
                      variant="contained"
                      color="primary"
                      type="submit"
                      disabled={isSubmitting}
                    >
                      {t("general.button.save")}
                    </Button>

                    <Button
                      startIcon={<SettingsApplicationsIcon />}
                      className="mr-0 btn btn-success d-inline-flex"
                      variant="contained"
                      color="primary"
                      type="button"
                      disabled={isSubmitting}
                      onClick={handleSaveAndGoConfig}
                    >
                      Lưu và tới cập nhật cấu hình
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

export default memo(observer(SalaryResultCUForm));