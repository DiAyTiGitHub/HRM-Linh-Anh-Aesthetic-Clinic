import React, { useState, useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, Button, DialogContent, ButtonGroup } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { pagingSalaryPeriod } from "../../SalaryPeriod/SalaryPeriodService";
import { pagingSalaryTemplates } from "../../SalaryTemplate/SalaryTemplateService";
import TableChartIcon from '@material-ui/icons/TableChart';
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import PayrollBoard from "./PayrollBoard";

function ChooseAndViewSalaryBoard() {
    const { t } = useTranslation();

    const { salaryStaffPayslipStore } = useStore();
    const {
        handleClose,
        openViewSalaryBoard,
        viewSalaryBoard,
        onViewSalaryBoard,
        searchPayrollObject,
        handleSetSearchPayrollObject
    } = salaryStaffPayslipStore;

    const validationSchema = Yup.object({
        salaryTemplate: Yup.object().required("Trường này là bắt buộc").nullable(),
        salaryPeriod: Yup.object().required("Trường này là bắt buộc").nullable(),
    });

    async function handleSaveForm(values) {
        // await saveSalaryItem(values);
        // await pagingSalaryItem();
        handleSetSearchPayrollObject(values);

        await viewSalaryBoard(values);
    }

    useEffect(function () {
        handleSetSearchPayrollObject({
            salaryTemplate: null,
            salaryResult: null
        });
    }, []);

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="lg"
            open={openViewSalaryBoard}
            noDialogContent
            title={"Xem bảng lương"}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={searchPayrollObject}
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
                                            <Grid container spacing={2}>
                                                <Grid item xs={12} sm={6} md={4}>
                                                    <GlobitsPagingAutocomplete
                                                        label={"Kỳ lương"}
                                                        name="salaryPeriod"
                                                        api={pagingSalaryPeriod}
                                                    />
                                                </Grid>

                                                <Grid item xs={12} sm={6} md={4}>
                                                    <GlobitsPagingAutocomplete
                                                        label={"Mẫu bảng lương"}
                                                        name="salaryTemplate"
                                                        api={pagingSalaryTemplates}
                                                    />
                                                </Grid>

                                                <Grid item xs={12}>
                                                    <div className="flex justify-end" >
                                                        <ButtonGroup
                                                            color="container"
                                                            aria-label="outlined primary button group"
                                                        >
                                                            <Button
                                                                startIcon={<TableChartIcon />}
                                                                type="submit"
                                                            >
                                                                Xem bảng lương
                                                            </Button>
                                                        </ButtonGroup>
                                                    </div>
                                                </Grid>

                                            </Grid>
                                        </Grid>

                                        {onViewSalaryBoard && (
                                            <Grid item xs={12}>
                                                <PayrollBoard />
                                            </Grid>
                                        )}

                                    </Grid>

                                </DialogContent>
                            </div>
                        </Form>
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(ChooseAndViewSalaryBoard));