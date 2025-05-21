import React, { memo, useEffect, useState } from "react";
import { Form, Formik } from "formik";
import { Button, DialogActions, DialogContent, Grid, makeStyles } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { toast } from "react-toastify";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { pagingRecruitmentPlan } from "app/views/Recruitment/RecruitmentPlanV2/RecruitmentPlanV2Service";

function PopupExportExcelRecruitmentReports() {
    const { candidateStore } = useStore();
    const { t } = useTranslation();

    const {
        handleClose,
        openPopupExportExcelRecruitmentReports,
        searchObject,
        handleExportExcelRecruitmentReports
    } = candidateStore;

    const validationSchema = Yup.object({
        recruitmentPlan: Yup.object().required(t("validation.required")).nullable(),
    });

    async function handleSaveForm(values) {
        try {
            const updatedSearchObject = {
                ...searchObject,
                recruitmentPlan: values.recruitmentPlan,
                recruitmentPlanId: values.recruitmentPlan?.id,
            };
            await handleExportExcelRecruitmentReports(updatedSearchObject);

            toast.success("Đã tải về báo cáo tuyển dụng thành công");

            handleClose();
        } catch (error) {
            toast.error("Có lỗi xảy ra, vui lòng thử lại");

            console.error(error);
        }
    }

    const [initialValues, setInitialValues] = useState(searchObject);

    return (<GlobitsPopupV2
        size="xs"
        scroll={"body"}
        open={openPopupExportExcelRecruitmentReports}
        noDialogContent
        title={t("Xuất báo cáo tuyển dụng")}
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
                        <DialogContent className="o-hidden p-12">
                            <Grid container spacing={2}>

                                <Grid item xs={12} sm={12}>
                                    <GlobitsPagingAutocompleteV2
                                        validate
                                        required
                                        label={t("Kế hoạch tuyển dụng")}
                                        name="recruitmentPlan"
                                        api={pagingRecruitmentPlan}
                                        getOptionLabel={(option) => {
                                            return `${option?.name || ""}`
                                        }}
                                        handleChange={(_, value) => {
                                            setFieldValue("recruitmentPlan", value);
                                            setFieldValue("recruitmentPlanId", value?.id);
                                        }}
                                    />
                                </Grid>

                            </Grid>
                        </DialogContent>
                        <DialogActions className="dialog-footer px-12">
                            <div className="flex flex-space-between flex-middle">
                                <Button
                                    variant="contained"
                                    //className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                    className={`mr-12 btn-secondary d-inline-flex`}
                                    startIcon={<BlockIcon />}
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
                                    startIcon={<SaveIcon />}
                                    type="submit"
                                    disabled={isSubmitting}
                                >
                                    {t("Xuất báo cáo")}
                                </Button>
                            </div>
                        </DialogActions>
                    </Form>
                );
            }}
        </Formik>
    </GlobitsPopupV2>);
}

export default memo(observer(PopupExportExcelRecruitmentReports));
