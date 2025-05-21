import React, { useState, useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingAllowanceTypes } from "../AllowanceType/AllowanceTypeService";
import LocalConstants from "app/LocalConstants";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import { convertToConstantFormat } from "app/common/CommonFunctions";

function AllowanceCUForm() {
  const { allowanceStore } = useStore();
  const { t } = useTranslation();

  const {
    handleClose,
    saveAllowance,
    pagingAllowance,
    selectedAllowance,
    openCreateEditPopup
  } = allowanceStore;

  const validationSchema = Yup.object({
    name: Yup.string().required(t("validation.required")).nullable(),
    code: Yup.string().required(t("validation.required")).nullable()
  });

  async function handleSaveForm(values) {
    await saveAllowance(values);
    await pagingAllowance();
  }

  const [initialValues, setInitialValues] = useState(
    selectedAllowance
  );

  useEffect(function () {
    setInitialValues({
      ...selectedAllowance,
      salaryItem: selectedAllowance?.salaryItem || {
        type: LocalConstants.SalaryItemType.ADDITION.value,
        calculationType: LocalConstants.SalaryItemCalculationType.AUTO_SYSTEM.value,
        valueType: LocalConstants.SalaryItemValueType.MONEY.value,
        isActive: true,
      }
    });
  }, [selectedAllowance, selectedAllowance?.id]);


  return (
    <GlobitsPopupV2
      size="md"
      scroll={"body"}
      open={openCreateEditPopup}
      noDialogContent
      title={(selectedAllowance?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("navigation.allowance.title")}
      onClosePopup={handleClose}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={initialValues}
        onSubmit={handleSaveForm}
      >
        {({ isSubmitting, values, setFieldValue, initialValues }) => {
          function handleAutoRenderCode(e) {
            const value = e.target.value;
            setFieldValue("name", value);
            setFieldValue("salaryItem.name", value);

            if (initialValues?.code?.endsWith("_SYSTEM")) return;

            const autoRenderedCode = convertToConstantFormat(value);
            setFieldValue("code", autoRenderedCode);
            setFieldValue("salaryItem.code", autoRenderedCode);
          }

          function handleAutoRenderCodeSalaryItem(e) {
            const value = e.target.value;
            setFieldValue("code", value);
            setFieldValue("salaryItem.code", value);
          }

          return (
            <Form autoComplete="off">
              <DialogContent className="o-hidden p-12">
                <Grid container spacing={2}>

                  <Grid item xs={12} sm={6} md={4}>
                    <GlobitsTextField
                      validate
                      required
                      label={t("Tên phụ cấp")}
                      name="name"
                      onChange={handleAutoRenderCode}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4}>
                    <GlobitsTextField
                      validate
                      required
                      label={t("Mã phụ cấp")}
                      name="code"
                      onChange={handleAutoRenderCodeSalaryItem}
                    />
                  </Grid>
                  <Grid item xs={12} sm={6} md={4}>
                    {/* Loại hợp đồng */}
                    <GlobitsPagingAutocompleteV2
                      name="allowanceType"
                      requiredLabel
                      label={t("Loại phụ cấp")}
                      api={pagingAllowanceTypes}
                    />
                  </Grid>
                  <Grid item md={12} sm={12} xs={12}>
                    <GlobitsTextField
                      label={t("Mô tả")}
                      name="description"
                      multiline
                      rows={3}
                    />
                  </Grid>

                  <Grid item md={12} sm={12} xs={12}>
                    <p style={{ fontSize: "15px", fontWeight: "bold" }}>
                      Thành phần lương tương ứng với phụ cấp:
                    </p>
                  </Grid>

                  <Grid item xs={12} sm={6} md={6}>
                    <GlobitsTextField
                      //disabled
                      label="Tên thành phần"
                      name="salaryItem.name"
                      disabled
                    />
                  </Grid>

                  <Grid item xs={12} sm={6} md={6}>
                    <GlobitsTextField
                      //disabled
                      label="Mã thành phần"
                      name="salaryItem.code"
                      disabled
                    />
                  </Grid>

                  <Grid item xs={12} sm={6} md={4}>
                    <GlobitsSelectInput
                      label={"Tính chất thành phần"}
                      name="salaryItem.type"
                      value={values?.salaryItem?.type} 
                      options={LocalConstants.SalaryItemType.getListData()}
                      hideNullOption={true}
                      keyValue="value"
                      disabled
                    />
                  </Grid>

                  <Grid item xs={12} sm={6} md={4}>
                    <GlobitsSelectInput
                      label={"Cách tính giá trị"}
                      name="salaryItem.calculationType"
                      value={values?.salaryItem?.calculationType} 
                      options={LocalConstants.SalaryItemCalculationType.getListData()}
                      hideNullOption={true}
                      keyValue="value"
                      disabled
                    />
                  </Grid>

                  <Grid item xs={12} sm={6} md={4}>
                    <GlobitsSelectInput
                      label={"Kiểu giá trị"}
                      name="salaryItem.valueType"
                      value={values?.salaryItem?.valueType} 
                      options={LocalConstants.SalaryItemValueType.getListData()}
                      hideNullOption={true}
                      keyValue="value"
                      disabled
                    />
                  </Grid>

                  <Grid item xs={12} sm={6} md={4}>
                    <GlobitsCheckBox
                      label={t("Có tính thuế")}
                      name="salaryItem.isTaxable"
                    />
                  </Grid>

                  <Grid item xs={12} sm={6} md={4}>
                    <GlobitsCheckBox
                      label={t("Có tính bảo hiểm")}
                      name="salaryItem.isInsurable"
                    />
                  </Grid>

                  <Grid item xs={12} sm={6} md={4}>
                    <GlobitsCheckBox
                      label={t("Đang được sử dụng")}
                      value={values?.salaryItem?.valueType || ""}
                      name="salaryItem.isActive"
                      disabled
                    />
                  </Grid>

                  <Grid item xs={12}>
                    {
                      (LocalConstants.SalaryItemValueType.OTHERS.value == values?.valueType || LocalConstants.SalaryItemValueType.TEXT.value == values?.valueType) ? (
                        <GlobitsTextField
                          label={<span>
                            Giá trị mặc định
                          </span>}
                          name="salaryItem.defaultValue"
                        />
                      ) : (
                        <GlobitsVNDCurrencyInput
                          label={<span>
                            Giá trị mặc định
                          </span>}
                          name="salaryItem.defaultValue"
                        />
                      )
                    }
                  </Grid>

                  {
                    (values?.calculationType == LocalConstants.SalaryItemCalculationType.USING_FORMULA.value
                      || values?.calculationType == LocalConstants.SalaryItemCalculationType.THRESHOLD.value
                    ) && (
                      <Grid item xs={12}>
                        <GlobitsTextField
                          label={values?.calculationType == LocalConstants.SalaryItemCalculationType.THRESHOLD.value ? "Giá trị so sánh ngưỡng" : "Công thức/Giá trị của thành phần"}
                          name="salaryItem.formula"
                          multiline
                          rows={2}
                        />
                      </Grid>
                    )
                  }

                  {/* <Grid item xs={12}>
                    <GlobitsTextField
                      label="Mô tả"
                      name="salaryItem.description"
                      multiline
                      rows={3}
                    />
                  </Grid> */}

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

export default memo(observer(AllowanceCUForm));
