import React, { useState, memo, useEffect, useRef } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent } from "@material-ui/core";
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
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import { convertToConstantFormat } from "../../../../common/CommonFunctions";
import GlobitsVNDCurrencyInput from "../../../../common/form/GlobitsVNDCurrencyInput";
import SalaryTemplateItemConfigSection from "./SalaryTemplateItemThresholds";
import InputFormula from "../SalaryTemplateCUTab/TemplateItemsTableV2/InputFormula";
import SalaryTemplateItemThresholds from "./SalaryTemplateItemThresholds";

function SalaryTemplateItemForm() {
    const { salaryTemplateItemStore, salaryTemplateStore, salaryItemStore } = useStore();
    const { selectedSalaryTemplate } = salaryTemplateStore;
    const [initialDataFormula, setInitialDataFormula] = useState([]);

    useEffect(() => {
        if (selectedSalaryTemplate?.templateItems?.length > 0) {
            const salaryItemCodes = selectedSalaryTemplate.templateItems.map((item) => item.salaryItem?.code)
            setInitialDataFormula(salaryItemCodes)
        }
    }, [selectedSalaryTemplate]);
    const { t } = useTranslation();

    const validationSchema = Yup.object().shape({
        salaryItem: Yup.object().shape({
            code: Yup.string().required(t("validation.code")).nullable(),
            name: Yup.string().required(t("validation.name")).nullable(),
            type: Yup.number().required("Trường này là bắt buộc").nullable(),
            calculationType: Yup.number().required("Trường này là bắt buộc").nullable(),
        }),

        thresholds: Yup.array().when('calculationType', {
            is: LocalConstants.SalaryItemCalculationType.THRESHOLD.value,
            then: Yup.array()
              .min(1, "Phải có ít nhất 1 mức ngưỡng")
              .of(
                Yup.object().shape({
                  operatorMinValue: Yup.number().nullable(),
                  minValue: Yup.number().nullable().when('operatorMinValue', {
                    is: (val) => val !== null,
                    then: Yup.number().required("Giá trị nhỏ nhất là bắt buộc khi có toán tử"),
                    otherwise: Yup.number().nullable()
                  }),
          
                  operatorMaxValue: Yup.number().nullable(),
                  maxValue: Yup.number().nullable().when('operatorMaxValue', {
                    is: (val) => val !== null,
                    then: Yup.number().required("Giá trị lớn nhất là bắt buộc khi có toán tử"),
                    otherwise: Yup.number().nullable()
                  }),
          
                  // 👇 Rule đảm bảo ít nhất 1 trong 2 operator không được null
                }).test(
                  'at-least-one-operator',
                  'Phải có ít nhất một toán tử nhỏ nhất hoặc lớn nhất',
                  function (value) {
                    return value?.operatorMinValue !== null || value?.operatorMaxValue !== null;
                  }
                )
              )
          })
          
    });
    
    const {
        handleClose,
        selectedSalaryTemplateItem,
        saveOrUpdateWithItemConfig,
        shouldOpenEditorDialog
    } = salaryTemplateItemStore;
    const {
        saveSalaryItem,
    } = salaryItemStore;

    function isSubset(parentArray, formula) {
        if (typeof formula !== "string" || formula.trim() === "") {
            return true;
        }

        const parentSet = new Set(parentArray);
        const valueCurrent = formula
            .split(/\s*[+\-*/()]\s*/) // Tách công thức thành các thành phần riêng biệt
            .map((value) => value.trim()) // Loại bỏ khoảng trắng
            .filter((value) => value.length > 0); // Loại bỏ phần tử rỗng

        return valueCurrent.filter((item) => isNaN(item) && !parentSet.has(item)).length === 0;
    }

    async function handleSaveForm(values) {
        const isSubmit = isSubset(initialDataFormula, values?.salaryItem?.formula);
        if (isSubmit) {
            await saveOrUpdateWithItemConfig(values);
            await saveSalaryItem(values?.salaryItem);
            await salaryTemplateStore.handleGetSalaryTemplateData(values?.salaryTemplateId);
        }
    }

    const [initialValues, setInitialValues] = useState(selectedSalaryTemplateItem);

    useEffect(function () {
        setInitialValues(selectedSalaryTemplateItem);
    }, [selectedSalaryTemplateItem?.id]);

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="lg"
            open={shouldOpenEditorDialog}
            noDialogContent
            title={(selectedSalaryTemplateItem?.salaryItem?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + "thành phần lương " + (selectedSalaryTemplateItem?.salaryItem?.allowanceId ? t("(Được cập nhật theo phụ cấp)") : '')}
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
                        setFieldValue("salaryItem.name", value);

                        if (initialValues?.code?.endsWith("_SYSTEM")) return;

                        const autoRenderedCode = convertToConstantFormat(value);
                        setFieldValue("salaryItem.code", autoRenderedCode);
                    }

                    return (
                        <Form autoComplete="off" autocomplete="off">
                            <div className="dialog-body">
                                <DialogContent className="p-12">
                                    <FormikFocusError />

                                    <Grid container spacing={2}>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                required
                                                label="Tên thành phần"
                                                name="salaryItem.name"
                                                onChange={handleAutoRenderCode}
                                                disabled={values?.allowanceId}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                required
                                                disabled={initialValues?.code?.endsWith("_SYSTEM") || values?.allowanceId}
                                                label="Mã thành phần"
                                                name="salaryItem.code"
                                                readOnly={values?.id}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsSelectInput
                                                label={"Tính chất thành phần"}
                                                name="salaryItem.type"
                                                options={LocalConstants.SalaryItemType.getListData()}
                                                hideNullOption={true}
                                                keyValue="value"
                                                disabled={values?.allowanceId}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsSelectInput
                                                label={"Cách tính giá trị"}
                                                name="salaryItem.calculationType"
                                                options={LocalConstants.SalaryItemCalculationType.getListData()}
                                                hideNullOption={true}
                                                keyValue="value"
                                                disabled={values?.allowanceId}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsSelectInput
                                                label={"Kiểu giá trị"}
                                                name="salaryItem.valueType"
                                                options={LocalConstants.SalaryItemValueType.getListData()}
                                                hideNullOption={true}
                                                keyValue="value"
                                                disabled={values?.allowanceId}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsCheckBox
                                                label={t("Có tính thuế")}
                                                name="salaryItem.isTaxable"
                                                disabled={values?.allowanceId}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsCheckBox
                                                label={t("Có tính bảo hiểm")}
                                                name="salaryItem.isInsurable"
                                                disabled={values?.allowanceId}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsCheckBox
                                                label={t("Đang được sử dụng")}
                                                name="salaryItem.isActive"
                                                disabled={values?.allowanceId}
                                            />
                                        </Grid>

                                        <Grid item xs={12}
                                        // sm={6}
                                        >
                                            {
                                                (LocalConstants.SalaryItemValueType.OTHERS.value == values?.valueType || LocalConstants.SalaryItemValueType.TEXT.value == values?.valueType) ? (
                                                    <GlobitsTextField
                                                        label={<span>
                                                            Giá trị mặc định
                                                        </span>}
                                                        name="salaryItem.defaultValue"
                                                        disabled={values?.allowanceId}
                                                    />
                                                ) : (
                                                    <GlobitsVNDCurrencyInput
                                                        label={<span>
                                                            Giá trị mặc định
                                                        </span>}
                                                        name="salaryItem.defaultValue"
                                                        disabled={values?.allowanceId}
                                                    />
                                                )
                                            }
                                        </Grid>


                                        {/* <Grid item xs={12} sm={6}>
                      <GlobitsVNDCurrencyInput
                        label={<span>
                          Mức trần
                        </span>}
                        name="maxValue"
                      />
                    </Grid> */}

                                        {
                                            (values?.calculationType === LocalConstants.SalaryItemCalculationType.THRESHOLD.value
                                            ) && (
                                                <Grid item xs={12}>
                                                    <GlobitsTextField
                                                        label={"Giá trị so sánh ngưỡng"}
                                                        name="salaryItem.formula"
                                                        multiline
                                                        rows={2}
                                                        disabled={values?.allowanceId}
                                                    />
                                                </Grid>
                                            )
                                        }
                                        {
                                            (values?.salaryItem?.calculationType === LocalConstants.SalaryItemCalculationType.USING_FORMULA.value) && (
                                                <Grid item xs={12}>
                                                    Công thức/Giá trị của thành phần
                                                    <InputFormula
                                                        name="salaryItem.formula"
                                                        valueField={values?.salaryItem?.formula || ""}
                                                        listData={initialDataFormula}
                                                    />
                                                </Grid>
                                            )
                                        }

                                        {/* <Grid item xs={12}>
                      <SalaryItemFormulaInput
                        label="Công thức/Giá trị của thành phần"
                        name="formula"
                        multiline
                        rows={2}
                      />
                    </Grid> */}

                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                label="Mô tả"
                                                name="salaryItem.description"
                                                multiline
                                                rows={3}
                                                disabled={values?.allowanceId}
                                            />
                                        </Grid>

                                        {/*{!values?.allowanceId && values?.calculationType == LocalConstants.SalaryItemCalculationType.THRESHOLD.value && (*/}
                                        {/*  <Grid item xs={12}>*/}
                                        {/*    <SalaryItemThresholdSection />*/}
                                        {/*  </Grid>*/}
                                        {/*)}*/}

                                        {!values?.allowanceId && values?.salaryItem?.calculationType == LocalConstants.SalaryItemCalculationType.THRESHOLD.value && (
                                            <Grid item xs={12}>
                                                <SalaryTemplateItemThresholds />
                                            </Grid>
                                        )}

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

                                        {(!values?.allowanceId && (
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
                                        ))}
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

export default memo(observer(SalaryTemplateItemForm));