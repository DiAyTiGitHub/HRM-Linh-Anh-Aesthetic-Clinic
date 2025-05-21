import React, { useState, useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import * as Yup from "yup";
import moment from "moment";
import { observer } from "mobx-react";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingAllowance } from "../Allowance/AllowanceService";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import { pagingAllowancePolicy } from "../AllowancePolicy/AllowancePolicyService";

function StaffAllowanceFilterCUForm() {
  const { t } = useTranslation();
  const { staffAllowanceStore } = useStore();

  const {
    handleClose,
    saveStaffAllowance,
    pagingStaffAllowance,
    selectedStaffAllowance,
    openCreateEditPopup,
    isAdmin
  } = staffAllowanceStore;

  const validationSchema = Yup.object({
    startDate: Yup.date()
        .test("is-greater", "Ngày bắt đầu phải lớn thiết lập", function (value) {
          const { signedDate } = this.parent;
          if (signedDate && value) {
            return moment(value).isAfter(moment(signedDate), "date");
          }
          return true;
        })
        .transform(function transformDate(castValue, originalValue) {
          return originalValue ? new Date(originalValue) : castValue;
        })
        .required(t("validation.required"))
        .typeError("Ngày bắt đầu không đúng định dạng")
        .nullable(),
  
      endDate: Yup.date()
        .test(
          "is-greater",
          "Ngày kết thúc phải lớn ngày bắt đầu",
          function (value) {
            const { startDate } = this.parent;
            if (startDate && value) {
              return moment(value).isAfter(moment(startDate), "date");
            }
            return true;
          }
        )
        .transform(function transformDate(castValue, originalValue) {
          return originalValue ? new Date(originalValue) : castValue;
        })
        // .required(t("validation.required"))
        .typeError("Ngày kết thúc không đúng định dạng")
        .nullable()
  });

  async function handleSaveForm(values) {
    await saveStaffAllowance(values);
    await pagingStaffAllowance();
  }

  const [initialValues, setInitialValues] = useState(
    selectedStaffAllowance
  );

  useEffect(function () {
    setInitialValues({
      ...selectedStaffAllowance,
    });
  }, [selectedStaffAllowance, selectedStaffAllowance?.id]);


  return (
    <GlobitsPopupV2
      size="md"
      scroll={"body"}
      open={openCreateEditPopup}
      noDialogContent
      title={(selectedStaffAllowance?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("navigation.staffAllowance.title")}
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
            <Form autoComplete="off">
              <DialogContent className="dialog-body p-12">
                <Grid container spacing={2}>
                  <Grid item xs={12} sm={6} md={4}>
                    <ChooseUsingStaffSection
                      required
                      label={"Nhân viên áp dụng"}
                      disabled={values?.allowancePolicy?.id}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6} md={4}>
                    <GlobitsPagingAutocompleteV2
                      required
                      name="allowance"
                      label={t("Phụ cấp")}
                      api={pagingAllowance}
                      disabled={values?.allowancePolicy?.id}
                    />
                  </Grid>

                  <Grid item xs={12} sm={6} md={4}>
                    <GlobitsPagingAutocompleteV2
                      name="allowancePolicy"
                      label={t("Nằm trong chính sách")}
                      api={pagingAllowancePolicy}
                      handleChange={(_, value) => {
                        setFieldValue("allowancePolicy", value);
                        setFieldValue("allowance", value?.allowance);
                        setFieldValue("startDate", value?.startDate);
                        setFieldValue("endDate", value?.endDate);
                        setFieldValue("usingFormula", value?.formula);
                      }}
                      disabled
                    />
                  </Grid>

                  <Grid item sm={6} xs={12} md={6}>
                    <GlobitsDateTimePicker
                      label={"Ngày bắt đầu"}
                      name="startDate"
                      disabled={values?.allowancePolicy?.id}
                    />
                  </Grid>

                  <Grid item sm={6} xs={12} md={6}>
                    <GlobitsDateTimePicker
                      label={"Ngày kết thúc"}
                      name="endDate"
                      disabled={values?.allowancePolicy?.id}
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <GlobitsTextField
                      label="Công thức/Giá trị tính toán"
                      name="usingFormula"
                      multiline
                      rows={2}
                      disabled={values?.allowancePolicy?.id}
                    />
                  </Grid>
                </Grid>
              </DialogContent> 

              <DialogActions className="dialog-footer px-12">
                <div className="flex flex-space-between flex-middle">
                  <Button
                    variant="contained"
                    className="mr-12 btn btn-secondary d-inline-flex"
                    color="secondary"
                    disabled={isSubmitting}
                    onClick={handleClose}
                  >
                    {t("general.button.close")}
                  </Button>
                  <Button
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
            </Form>
          );
        }
        }
      </Formik>
    </GlobitsPopupV2>
  );
}

export default memo(observer(StaffAllowanceFilterCUForm));
