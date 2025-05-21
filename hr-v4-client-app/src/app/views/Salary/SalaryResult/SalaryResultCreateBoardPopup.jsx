import React, { useState, useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent, ButtonGroup, Tooltip } from "@material-ui/core";
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
import LocalAtmIcon from '@material-ui/icons/LocalAtm';

function SalaryResultCUForm() {
  const { salaryResultStore } = useStore();
  const history = useHistory();
  const { t } = useTranslation();
  const {
    handleClose,
    saveSalaryResult,
    pagingSalaryResult,
    selectedSalaryResult,
    openCreateEditPopup,
    createSalaryBoardByPeriodAndTemplate
  } = salaryResultStore;

  const validationSchema = Yup.object({
    code: Yup.string().required(t("validation.code")).nullable(),
    name: Yup.string().required(t("validation.name")).nullable(),
    // salaryTemplate: Yup.object().required("Chưa chọn mẫu bảng lương").nullable(),
    salaryPeriod: Yup.object().required("Chưa chọn kì lương").nullable(),
  });

  async function handleCreateSalaryBoard(values) {
    try {
      const data = await createSalaryBoardByPeriodAndTemplate(values);

      if (data?.id) {
        history.push(ConstantList.ROOT_PATH + `payroll/` + data?.id);
      }
    } catch (err) {
      console.error("Error saving data:", err);
    }
  }

  const [initialValues, setInitialValues] = useState(selectedSalaryResult);

  useEffect(function () {
    setInitialValues(selectedSalaryResult);
  }, [selectedSalaryResult, selectedSalaryResult?.id]);

  return (
    <GlobitsPopupV2
      scroll={"body"}
      size="sm"
      open={openCreateEditPopup}
      noDialogContent
      title={(selectedSalaryResult?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + "bảng lương"}
      onClosePopup={handleClose}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={initialValues}
        onSubmit={handleCreateSalaryBoard}
      >
        {({ isSubmitting, values, setFieldValue, initialValues, validateForm, errors, setFieldTouched }) => {

          return (
            <Form autoComplete="off" autocomplete="off">
              <div className="dialog-body">
                <DialogContent className="p-12">
                  <FormikFocusError />

                  <Grid container spacing={2}>
                    <Grid item xs={12}>
                      <SalaryResultBasicInfoSection />
                    </Grid>

                    {/* <Grid item xs={12}> 
                      <StaffsWithChosenTemplateInPeriodSection/>
                    </Grid> */}

                  </Grid>

                </DialogContent>
              </div>

              <div className="dialog-footer dialog-footer-v2 py-8 px-12">
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

                    <Tooltip placement="top" arrow title="Tổng hợp bảng lương từ các phiếu lương của nhân viên">
                      <Button
                        startIcon={<LocalAtmIcon />}
                        className="btn bgc-lighter-dark-blue d-inline-flex"
                        variant="contained"
                        color="primary"
                        type="submit"
                        disabled={isSubmitting}
                      >
                        Tạo bảng lương
                      </Button>
                    </Tooltip>

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