import React , { memo , useEffect , useState } from "react";
import { Form , Formik } from "formik";
import { Button , DialogActions , DialogContent , Grid } from "@material-ui/core";
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
import SalaryTemplateItemConfigSection from "./SalaryTemplateItemConfigSection";

function SalaryItemV2CUForm(props) {
    const {t} = useTranslation();
    const {readOnly = false} = props;
    const {salaryItemStore} = useStore();
    const {
        handleClose ,
        saveSalaryItem ,
        pagingSalaryItem ,
        selectedSalaryItem ,
        openCreateEditPopup ,
        openViewPopup ,
    } = salaryItemStore;

    const validationSchema = Yup.object({
        code:Yup.string().required(t("validation.code")).nullable() ,
        name:Yup.string().required(t("validation.name")).nullable() ,
        type:Yup.number().required("Trường này là bắt buộc").nullable() ,
        calculationType:Yup.number().required("Trường này là bắt buộc").nullable() ,
    });

    async function handleSaveForm(values) {
        await saveSalaryItem(values);
        await pagingSalaryItem();
    }

    const [initialValues , setInitialValues] = useState(selectedSalaryItem);

    useEffect(function () {
        setInitialValues(selectedSalaryItem);
    } , [selectedSalaryItem , selectedSalaryItem?.id]);

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="sm"
            open={openCreateEditPopup || openViewPopup}
            noDialogContent
            title={
                (openViewPopup && t("Xem chi tiết") + " thành phần lương ") ||
                ((selectedSalaryItem?.id
                        ? t("general.button.edit")
                        : t("general.button.add")) + ' ' + "thành phần lương " +
                    (selectedSalaryItem?.allowanceId
                        ? t("(Được cập nhật theo phụ cấp)")
                        : ''))
            }
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSaveForm}
            >
                {({isSubmitting , values , setFieldValue , initialValues}) => {
                    function handleAutoRenderCode(e) {
                        const value = e.target.value;
                        setFieldValue("name" , value);

                        if (initialValues?.code?.endsWith("_SYSTEM")) return;

                        const autoRenderedCode = convertToConstantFormat(value);
                        setFieldValue("code" , autoRenderedCode);
                    }

                    return (
                        <Form autoComplete="off" autocomplete="off">
                            <div className="dialog-body">
                                <DialogContent className="p-12">
                                    <FormikFocusError/>

                                    <Grid container spacing={2}>
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                required
                                                label="Tên thành phần"
                                                name="name"
                                                onChange={handleAutoRenderCode}
                                                disabled={values?.allowanceId}
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                required
                                                disabled={initialValues?.code?.endsWith("_SYSTEM") || values?.allowanceId}
                                                label="Mã thành phần"
                                                name="code"
                                                readOnly={values?.id | readOnly}
                                            />
                                        </Grid>

                                        {/* <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsSelectInput
                                                label={"Tính chất thành phần"}
                                                name="type"
                                                options={LocalConstants.SalaryItemType.getListData()}
                                                hideNullOption={true}
                                                keyValue="value"
                                                disabled={values?.allowanceId}
                                            />
                                        </Grid> */}

                                        <Grid
                                            item
                                            xs={12}
                                            sm={6}
                                            // md={4}
                                        >
                                            <GlobitsSelectInput
                                                label={"Cách tính giá trị"}
                                                name="calculationType"
                                                options={LocalConstants.SalaryItemCalculationType.getListData()}
                                                hideNullOption={true}
                                                keyValue="value"
                                                disabled={values?.allowanceId}
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid
                                            item
                                            xs={12}
                                            sm={6}
                                            // md={4}
                                        >
                                            <GlobitsSelectInput
                                                label={"Kiểu giá trị"}
                                                name="valueType"
                                                options={LocalConstants.SalaryItemValueType.getListData()}
                                                hideNullOption={true}
                                                keyValue="value"
                                                disabled={values?.allowanceId}
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        {/* <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsCheckBox
                                                label={t("Có tính thuế")}
                                                name="isTaxable"
                                                disabled={values?.allowanceId}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsCheckBox
                                                label={t("Có tính bảo hiểm")}
                                                name="isInsurable"
                                                disabled={values?.allowanceId}
                                            />
                                        </Grid> */}

                                        {/* <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsCheckBox
                                                label={t("Đang được sử dụng")}
                                                name="isActive"
                                                disabled={values?.allowanceId}
                                                readOnly={readOnly}
                                            />
                                        </Grid> */}

                                        <Grid item xs={12}
                                            // sm={6}
                                        >
                                            {
                                                (LocalConstants.SalaryItemValueType.OTHERS.value == values?.valueType || LocalConstants.SalaryItemValueType.TEXT.value == values?.valueType) ? (
                                                    <GlobitsTextField
                                                        label={<span>
                                                            Giá trị mặc định
                                                        </span>}
                                                        name="defaultValue"
                                                        disabled={values?.allowanceId}
                                                        readOnly={readOnly}
                                                    />
                                                ) : (
                                                    <GlobitsVNDCurrencyInput
                                                        label={<span>
                                                            Giá trị mặc định
                                                        </span>}
                                                        name="defaultValue"
                                                        disabled={values?.allowanceId}
                                                        readOnly={readOnly}
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

                                        {/* {
                      (values?.calculationType == LocalConstants.SalaryItemCalculationType.USING_FORMULA.value
                        || values?.calculationType == LocalConstants.SalaryItemCalculationType.THRESHOLD.value
                      ) && (
                        <Grid item xs={12}>
                          <GlobitsTextField
                            label={values?.calculationType == LocalConstants.SalaryItemCalculationType.THRESHOLD.value ? "Giá trị so sánh ngưỡng" : "Công thức/Giá trị của thành phần"}
                            name="formula"
                            multiline
                            rows={2}
                            disabled={values?.allowanceId}
                          />
                        </Grid>
                      )
                    } */}

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
                                                name="description"
                                                multiline
                                                rows={3}
                                                disabled={values?.allowanceId}
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        {/*{!values?.allowanceId && values?.calculationType === LocalConstants.SalaryItemCalculationType.THRESHOLD.value && (*/}
                                        {/*  <Grid item xs={12}>*/}
                                        {/*    <SalaryItemThresholdSection />*/}
                                        {/*  </Grid>*/}
                                        {/*)}*/}

                                        {!values?.allowanceId && values?.calculationType === LocalConstants.SalaryItemCalculationType.THRESHOLD.value && (
                                            <Grid item xs={12}>
                                                <SalaryTemplateItemConfigSection readOnly={readOnly}/>
                                            </Grid>
                                        )}

                                    </Grid>

                                </DialogContent>
                            </div>

                            <div className="dialog-footer dialog-footer-v2 py-8">
                                <DialogActions className="p-0">
                                    <div className="flex flex-space-between flex-middle">
                                        <Button
                                            startIcon={<BlockIcon/>}
                                            variant="contained"
                                            className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                            color="secondary"
                                            onClick={handleClose}
                                            disabled={isSubmitting}
                                        >
                                            {t("general.button.cancel")}
                                        </Button>
                                        {(!values?.allowanceId && !readOnly && (
                                            <Button
                                                startIcon={<SaveIcon/>}
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

export default memo(observer(SalaryItemV2CUForm));