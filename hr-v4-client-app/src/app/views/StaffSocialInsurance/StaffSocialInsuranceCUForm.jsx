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
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingSalaryPeriod } from "../Salary/SalaryPeriod/SalaryPeriodService";
import { pagingSalaryResult } from "../Salary/SalaryResult/SalaryResultService";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import StaffSocialInsuranceAutoSection from "./StaffSocialInsuranceAutoSection";

function StaffSocialInsuranceCUForm () {
  const {staffSocialInsuranceStore, hrRoleUtilsStore} = useStore ();
  const {t} = useTranslation ();
  const {
    handleClose,
    saveStaffSocialInsurance,
    pagingStaffSocialInsurance,
    selectedStaffSocialInsurance,
    openCreateEditPopup,
  } = staffSocialInsuranceStore;

  const validationSchema = Yup.object ({
    staff:Yup.object ().required (t ("validation.required")).nullable (),
  });

  async function handleSaveForm (values) {
    await saveStaffSocialInsurance (values);
    await pagingStaffSocialInsurance ();
  }

  const [initialValues, setInitialValues] = useState (selectedStaffSocialInsurance);

  useEffect (function () {
    setInitialValues (selectedStaffSocialInsurance);
  }, [selectedStaffSocialInsurance, selectedStaffSocialInsurance?.id]);

  const {isAdmin} = hrRoleUtilsStore;
  return (
      <GlobitsPopupV2
          scroll={"body"}
          size="md"
          open={openCreateEditPopup}
          noDialogContent
          title={(selectedStaffSocialInsurance?.id? t ("general.button.edit") : t ("general.button.add")) + ' ' + t ("navigation.insurance.staffSocialInsurance")}
          onClosePopup={handleClose}
      >
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={initialValues}
            onSubmit={handleSaveForm}
        >
          {({isSubmitting, values, setFieldValue, initialValues}) => {

            return (
                <Form autoComplete="off" autocomplete="off">
                  <div className="dialog-body">
                    <DialogContent className="p-12">
                      <FormikFocusError/>

                      <Grid container spacing={2}>
                        <Grid item xs={12} sm={6} md={4}>
                          <ChooseUsingStaffSection
                              required
                              label={"Nhân viên đóng BHXH"}
                              disabled={!isAdmin}
                          />
                        </Grid>

                        <Grid item xs={12} sm={6} md={4}>
                          <GlobitsPagingAutocomplete
                              label={"Kỳ lương"}
                              name="salaryPeriod"
                              required
                              api={pagingSalaryPeriod}
                              disabled={!isAdmin}
                          />
                        </Grid>

                        <Grid item xs={12} sm={6} md={4}>
                          <GlobitsDateTimePicker
                              label={"Ngày bắt đầu"}
                              name="startDate"
                              value={values.startDate}
                              disabled={!isAdmin}
                          />
                        </Grid>

                        <Grid item xs={12} sm={6} md={4}>
                          <GlobitsDateTimePicker
                              label={"Ngày kết thúc"}
                              name="endDate"
                              value={values.endDate}
                              disabled={!isAdmin}
                          />
                        </Grid>

                        {/* <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsPagingAutocomplete
                                                label={"Tính từ bảng lương"}
                                                name="salaryResult"
                                                placeHolder="Không được tự động lấy"
                                                api={pagingSalaryResult}
                                                disabled={!isAdmin}
                                            />
                                        </Grid> */}

                        <StaffSocialInsuranceAutoSection/>


                        {/* <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsNumberInput
                                                label={"Hệ số lương đóng bảo hiểm"}
                                                name="salaryCoefficient"
                                                value={values.salaryCoefficient}
                                                disabled={!isAdmin}
                                            />
                                        </Grid> */}


                        <Grid item xs={12} sm={6} md={4}>
                          <GlobitsSelectInput
                              label={"Trạng thái chi trả"}
                              name="paidStatus"
                              options={LocalConstants.StaffSocialInsurancePaidStatus.getListData ()}
                              disabled={!isAdmin}
                          />
                        </Grid>

                        <Grid item xs={12}>
                          <GlobitsTextField
                              label="Ghi chú"
                              name="note"
                              multiline
                              rows={3}
                              disabled={!isAdmin}
                          />
                        </Grid>
                      </Grid>

                    </DialogContent>
                  </div>

                  <div className="dialog-footer dialog-footer-v2 py-8">
                    <DialogActions className="p-0">
                      <div className="flex flex-space-between flex-middle">
                        <Button
                            startIcon={<BlockIcon/>}
                            variant="contained"
                            className="mr-12 btn btn-secondary d-inline-flex"
                            color="secondary"
                            onClick={handleClose}
                            disabled={isSubmitting}
                        >
                          {t ("general.button.cancel")}
                        </Button>
                        {(isAdmin &&
                            <Button
                                startIcon={<SaveIcon/>}
                                className="mr-0 btn btn-primary d-inline-flex"
                                variant="contained"
                                color="primary"
                                type="submit"
                                disabled={isSubmitting}
                            >
                              {t ("general.button.save")}
                            </Button>
                        )}
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

export default memo (observer (StaffSocialInsuranceCUForm));