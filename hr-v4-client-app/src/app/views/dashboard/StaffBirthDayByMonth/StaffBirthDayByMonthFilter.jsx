import React, { useState, useEffect, memo, useMemo } from "react";
import { Formik, Form, Field } from "formik";
import { Grid, DialogActions, Button, DialogContent, makeStyles } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";


function StaffBirthDayByMonthFilter() {
    const { dashboardStore } = useStore();
    const { t } = useTranslation();
    const {
        findStaffsHaveBirthDayByMonth,
        listStaffHasBirthDayInMonth
    } = dashboardStore;

    const validationSchema = Yup.object({
        // code: Yup.string().required(t("validation.code")).nullable(),
        month: Yup.number().required(t("validation.name")).nullable(),
    });

    const currentMonth = useMemo(function () {
        const now = new Date();
        const month = now.getMonth() + 1; // Add 1 to the zero-based index
        return month;
    }, []);

    const [initialValues, setInitialValues] = useState({ month: currentMonth });

    async function handleSaveForm(values) {
        await findStaffsHaveBirthDayByMonth(values?.month);
    }

    useEffect(function () {
        findStaffsHaveBirthDayByMonth(currentMonth);
    }, [])

    return (
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={initialValues}
            onSubmit={handleSaveForm}
        >
            {({ isSubmitting, values, setFieldValue, initialValues, handleSubmit }) => {

                return (
                    <Form autoComplete="off" autocomplete="off">
                        <FormikFocusError />

                        <Grid container spacing={2}>
                            <Grid item xs={12}>
                                <GlobitsSelectInput
                                    hideNullOption={true}
                                    label={"Tháng"}
                                    name="month"
                                    options={LocalConstants.LIST_MONTH}
                                    // defaultValue={values.parent}
                                    handleChange={async (event) => {
                                        // console.log("changed month: ", event);
                                        const newMonth = event.target.value;
                                        setFieldValue("month", newMonth);
                                        await findStaffsHaveBirthDayByMonth(newMonth);
                                    }}
                                />
                            </Grid>
                        </Grid>
                    </Form>
                );
            }
            }
        </Formik>
    );
}

export default memo(observer(StaffBirthDayByMonthFilter));