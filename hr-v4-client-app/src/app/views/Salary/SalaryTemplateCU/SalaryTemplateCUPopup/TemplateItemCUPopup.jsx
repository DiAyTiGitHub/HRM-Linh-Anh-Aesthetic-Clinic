import React, { memo, useEffect, useState } from "react";
import { Form, Formik } from "formik";
import { Button, DialogActions, DialogContent, Grid, Tooltip } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import SalaryTemplateItemThresholds from "../SalaryTemplateItem/SalaryTemplateItemThresholds";
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import { useStore } from "app/stores"

function TemplateItemCUPopup(props) {
    const { hrRoleUtilsStore } = useStore()
    const {
        isOpen,
        selectedItem,
        handleConfirmCUTemplateItem,
        canSelectItemGroups,
        handleCloseCUTemplateItem
    } = props;

    const {
        isAdmin,
        isManager
    } = hrRoleUtilsStore;

    const { t } = useTranslation();

    const validationSchema = Yup.object({
        displayName: Yup.string().required("Tên cột hiển thị không được trống").nullable(),
        salaryItem: Yup.object().required("Phải có thành phần lương cấu thành").nullable(),
        displayOrder: Yup.number().required("Chưa có thứ tự hiển thị").nullable(),
        calculationType: Yup.number().required("Chưa chọn cách tính").nullable(),
        valueType: Yup.number().required("Chưa chọn kiểu giá trị hiển thị").nullable(),
    });

    const [initialValues, setInitialValues] = useState(selectedItem);

    useEffect(function () {
        setInitialValues(selectedItem);
    }, [selectedItem, selectedItem?.id]);

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="md"
            open={isOpen}
            noDialogContent
            title={(!selectedItem?.isNew ? t("general.button.edit") : t("general.button.add")) + ' ' + "thành phần trong mẫu bảng lương"}
            onClosePopup={handleCloseCUTemplateItem}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={{
                    ...initialValues,
                }}
                onSubmit={handleConfirmCUTemplateItem}
            >
                {({ isSubmitting, values, setFieldValue, initialValues, submitForm }) => {

                    return (
                        <Form autoComplete="off" autocomplete="off">
                            <div className="dialog-body">
                                <DialogContent className="p-12">
                                    <FormikFocusError />

                                    <Grid container spacing={2}>
                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Thông tin thành phần lương
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsPagingAutocomplete
                                                required
                                                disabled
                                                label="Thành phần lương"
                                                name="salaryItem"
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsTextField
                                                required
                                                disabled
                                                label="Mã TP lương/Tham số tính toán"
                                                name="code"
                                            />
                                        </Grid>


                                        <Grid item xs={12} className="pb-0">
                                            <p className="m-0 p-0 borderThrough2">
                                                Cài đặt tính toán trong mẫu
                                            </p>
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsTextField
                                                required
                                                label="Tên cột hiển thị"
                                                name="displayName"
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsSelectInput
                                                label="Nhóm thành phần"
                                                name="templateItemGroupId"
                                                keyValue="id"
                                                options={canSelectItemGroups}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsSelectInput
                                                label={"Cách tính"}
                                                //value={values?.salaryItem?.calculationType}
                                                name="calculationType"
                                                keyValue="value"
                                                options={LocalConstants.SalaryItemCalculationType.getListData()}
                                                hideNullOption
                                                // Không được phép chính sửa khi cách tính là giá trị cố định
                                                required
                                                disabled={values?.calculationType == LocalConstants.SalaryItemCalculationType.FIX.value}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6} md={4}>
                                            <GlobitsSelectInput
                                                label={"Kiểu giá trị"}
                                                name="valueType"
                                                options={LocalConstants.SalaryItemValueType.getListData()}
                                                hideNullOption={true}
                                                required
                                                keyValue="value"
                                            // disabled={values?.allowanceId}
                                            />
                                        </Grid>
                                        {/* <Grid item xs={12}>
                                            <Grid container spacing={2}> */}
                                        <Grid item xs={6} md={4} className="mt-16">
                                            <GlobitsCheckBox
                                                label='Ẩn trong phiếu lương'
                                                name='hiddenOnPayslip'
                                            />
                                        </Grid>

                                        <Grid item xs={6} md={4} className="mt-16">
                                            <GlobitsCheckBox
                                                label='Ẩn trong bảng lương'
                                                name='hiddenOnSalaryBoard'
                                            />
                                        </Grid>
                                        {/* </Grid>
                                        </Grid> */}

                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                label="Mô tả"
                                                name="description"
                                                multiline
                                                rows={3}
                                            />
                                        </Grid>


                                        {
                                            values?.calculationType == LocalConstants.SalaryItemCalculationType.USING_FORMULA.value && (
                                                <>
                                                    <Grid item xs={12} className="pb-0">
                                                        <p className="m-0 p-0 borderThrough2">
                                                            Công thức/Giá trị tính toán
                                                        </p>
                                                    </Grid>

                                                    <Grid item xs={12}>
                                                        <GlobitsTextField
                                                            label="Công thức"
                                                            name="formula"
                                                            multiline
                                                            rows={2}
                                                        />
                                                    </Grid>
                                                </>

                                            )
                                        }

                                        {
                                            values?.calculationType == LocalConstants.SalaryItemCalculationType.THRESHOLD.value && (
                                                <>
                                                    <Grid item xs={12} className="pb-0">
                                                        <p className="m-0 p-0 borderThrough2">
                                                            Đầu vào so sánh ngưỡng
                                                        </p>
                                                    </Grid>

                                                    <Grid item xs={12}>
                                                        <GlobitsTextField
                                                            label="Đầu vào tính ngưỡng"
                                                            name="formula"
                                                            multiline
                                                            rows={2}
                                                        />
                                                    </Grid>
                                                </>

                                            )
                                        }

                                        {values?.calculationType == LocalConstants.SalaryItemCalculationType.THRESHOLD.value && (
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
                                            onClick={handleCloseCUTemplateItem}
                                            disabled={isSubmitting}
                                        >
                                            {t("general.button.cancel")}
                                        </Button>

                                        <Tooltip
                                            placement="top"
                                            arrow
                                            title="Hoàn tác các thay đổi"
                                        >
                                            <Button
                                                startIcon={<RotateLeftIcon />}
                                                className="mr-12 btn btn-primary d-inline-flex"
                                                variant="contained"
                                                color="primary"
                                                // onClick={() => handleConfirmCUTemplateItem(values)}
                                                type="reset"
                                                disabled={isSubmitting}
                                            >
                                                Nhập lại
                                            </Button>
                                        </Tooltip>

                                        {(isAdmin || isManager) && (
                                            <Tooltip
                                                placement="top"
                                                arrow
                                                title="Cập nhật sửa đổi vào mẫu"
                                            >
                                                <Button
                                                    startIcon={<SaveIcon />}
                                                    className="mr-0 btn bgc-lighter-dark-blue d-inline-flex"
                                                    variant="contained"
                                                    color="primary"
                                                    // onClick={() => handleConfirmCUTemplateItem(values)}
                                                    type="submit"
                                                    disabled={isSubmitting}
                                                >

                                                    {t("general.button.save")}
                                                </Button>
                                            </Tooltip>
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

export default memo(observer(TemplateItemCUPopup));