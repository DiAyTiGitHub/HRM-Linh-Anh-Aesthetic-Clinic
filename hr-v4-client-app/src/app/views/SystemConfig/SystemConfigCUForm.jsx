import React, { memo, useEffect, useState } from "react";
import { Form, Formik } from "formik";
import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsNumberInput from "../../common/form/GlobitsNumberInput";

function SystemConfigCUForm(props) {
    const { t } = useTranslation();

    const { 
        readOnly = false

     } = props;

    const { 
        systemConfigStore 

    } = useStore();

    const {
        handleClose,
        saveSystemConfig,
        pagingSystemConfig,
        selectedSystemConfig,
        openCreateEditPopup,
        openViewPopup,
    } = systemConfigStore;

    const validationSchema = Yup.object({
        configKey: Yup.string().required("Trường này là bắt buộc").nullable(),
        configValue: Yup.string().required(t("validation.name")).nullable(),
    });

    async function handleSaveForm(values) {
        await saveSystemConfig(values);
        await pagingSystemConfig();
    }

    const [initialValues, setInitialValues] = useState(selectedSystemConfig);

    useEffect(function () {
        setInitialValues(selectedSystemConfig);
    }, [selectedSystemConfig, selectedSystemConfig?.id]);

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="xs"
            open={openCreateEditPopup || openViewPopup}
            noDialogContent
            title={
                (openViewPopup && t("Xem chi tiết") + " Cấu hình hệ thống ") ||
                ((selectedSystemConfig?.id
                    ? t("general.button.edit")
                    : t("general.button.add")) + ' ' + "Cấu hình hệ thống " +
                    (selectedSystemConfig?.allowanceId
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
                {({ isSubmitting, values, setFieldValue, initialValues }) => {

                    return (
                        <Form autoComplete="off" autocomplete="off">
                            <div className="dialog-body">
                                <DialogContent className="p-12">
                                    <FormikFocusError />

                                    <Grid container spacing={2}>
                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                required
                                                label="Mã cấu hình"
                                                name="configKey"
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                required
                                                label="Giá trị cấu hình"
                                                name="configValue"
                                                readOnly={readOnly}
                                                multiline
                                                rows={4}
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <GlobitsNumberInput
                                                label="Số lượng số 0"
                                                name="numberOfZero"
                                            />
                                        </Grid>
                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                label="Ghi chú"
                                                name="note"
                                                multiline
                                                rows={4}
                                                readOnly={readOnly}
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
                                            className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                            color="secondary"
                                            onClick={handleClose}
                                            disabled={isSubmitting}
                                        >
                                            {t("general.button.cancel")}
                                        </Button>

                                        {(!readOnly && (
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

export default memo(observer(SystemConfigCUForm));