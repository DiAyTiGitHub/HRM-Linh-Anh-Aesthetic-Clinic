import React, { useState, useEffect, memo } from "react";
import { Formik, Form, Field, useFormikContext } from "formik";
import { Grid, DialogActions, Button, DialogContent, makeStyles, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import { useStore } from "app/stores";

function StaffSocialInsuranceAutoSection() {
    const { staffSocialInsuranceStore } = useStore();
    const {
        isAdmin
    } = staffSocialInsuranceStore;

    const { values, setFieldValue } = useFormikContext();

    const updateTotalInsuranceAmount = (values, setFieldValue) => {
        const insuranceSalary = values.insuranceSalary || 0;

        // Cá nhân đóng
        const staffSocial = (insuranceSalary * (values.staffSocialInsurancePercentage || 0)) / 100;
        const staffHealth = (insuranceSalary * (values.staffHealthInsurancePercentage || 0)) / 100;
        const staffUnemp = (insuranceSalary * (values.staffUnemploymentInsurancePercentage || 0)) / 100;
        const staffTotal = staffSocial + staffHealth + staffUnemp;

        // Công ty đóng
        const orgSocial = (insuranceSalary * (values.orgSocialInsurancePercentage || 0)) / 100;
        const orgHealth = (insuranceSalary * (values.orgHealthInsurancePercentage || 0)) / 100;
        const orgUnemp = (insuranceSalary * (values.orgUnemploymentInsurancePercentage || 0)) / 100;
        const orgTotal = orgSocial + orgHealth + orgUnemp;

        const totalInsurance = staffTotal + orgTotal;

        setFieldValue("staffSocialInsuranceAmount", staffSocial);
        setFieldValue("staffHealthInsuranceAmount", staffHealth);
        setFieldValue("staffUnemploymentInsuranceAmount", staffUnemp);
        setFieldValue("staffTotalInsuranceAmount", staffTotal);

        setFieldValue("orgSocialInsuranceAmount", orgSocial);
        setFieldValue("orgHealthInsuranceAmount", orgHealth);
        setFieldValue("orgUnemploymentInsuranceAmount", orgUnemp);
        setFieldValue("orgTotalInsuranceAmount", orgTotal);

        setFieldValue("totalInsuranceAmount", totalInsurance);
    };

    return (
        <>
            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Mức lương đóng bảo hiểm xã hội"}
                    name='insuranceSalary'
                    required={values?.hasSocialIns}
                    onChange={(e) => {
                        let value = e.target.value;
                        setFieldValue("insuranceSalary", value);
                        updateTotalInsuranceAmount(
                            {
                                ...values,
                                insuranceSalary: value,
                            },
                            setFieldValue
                        );
                    }}
                />
            </Grid>

            <Grid item xs={12} className='pb-0'>
                <p className='m-0 p-0 borderThrough2'>Nhân viên đóng BHXH</p>
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Tỷ lệ cá nhân đóng BHXH(%)"}
                    name='staffSocialInsurancePercentage'
                    onChange={(e) => {
                        let value = e.target.value;
                        setFieldValue("staffSocialInsurancePercentage", value);
                        updateTotalInsuranceAmount(
                            {
                                ...values,
                                staffSocialInsurancePercentage: value,
                            },
                            setFieldValue
                        );
                    }}
                />
            </Grid>
            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Số tiền BHXH nhân viên đóng"}
                    name='staffSocialInsuranceAmount'
                    disabled
                />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Tỷ lệ đóng BHYT của nhân viên(%)"}
                    name='staffHealthInsurancePercentage'
                    onChange={(e) => {
                        let value = e.target.value;
                        setFieldValue("staffHealthInsurancePercentage", value);
                        updateTotalInsuranceAmount(
                            {
                                ...values,
                                staffHealthInsurancePercentage: value,
                            },
                            setFieldValue
                        );
                    }}
                />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Số tiền BHYT nhân viên đóng"}
                    name='staffHealthInsuranceAmount'
                    disabled
                />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Tỷ lệ đóng BHTN của nhân viên(%)"}
                    name='staffUnemploymentInsurancePercentage'
                    onChange={(e) => {
                        let value = e.target.value;
                        setFieldValue("staffUnemploymentInsurancePercentage", value);
                        updateTotalInsuranceAmount(
                            {
                                ...values,
                                staffUnemploymentInsurancePercentage: value,
                            },
                            setFieldValue
                        );
                    }}
                />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Số tiền BHTN nhân viên đóng"}
                    name='staffUnemploymentInsuranceAmount'
                    disabled
                />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Tổng tiền bảo hiểm mà nhân viên đóng"}
                    name='staffTotalInsuranceAmount'
                    disabled
                />
            </Grid>
            <Grid item xs={12} className='pb-0'>
                <p className='m-0 p-0 borderThrough2'>Công ty đóng BHXH</p>
            </Grid>
            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Tỷ lệ đơn vị đóng bảo hiểm xã hội(%)"}
                    name='orgSocialInsurancePercentage'
                    onChange={(e) => {
                        let value = e.target.value;
                        setFieldValue("orgSocialInsurancePercentage", value);
                        updateTotalInsuranceAmount(
                            {
                                ...values,
                                orgSocialInsurancePercentage: value,
                            },
                            setFieldValue
                        );
                    }}
                />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Số tiền đơn vị đóng"}
                    name='orgSocialInsuranceAmount'
                    disabled
                />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Tỷ lệ đóng BHYT của công ty(%)"}
                    name='orgHealthInsurancePercentage'
                    onChange={(e) => {
                        let value = e.target.value;
                        setFieldValue("orgHealthInsurancePercentage", value);
                        updateTotalInsuranceAmount(
                            {
                                ...values,
                                orgHealthInsurancePercentage: value,
                            },
                            setFieldValue
                        );
                    }}
                />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Số tiền BHYT công ty đóng"}
                    name='orgHealthInsuranceAmount'
                    disabled
                />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Tỷ lệ đóng BHTN của công ty(%)"}
                    name='orgUnemploymentInsurancePercentage'
                    onChange={(e) => {
                        let value = e.target.value;
                        setFieldValue("orgUnemploymentInsurancePercentage", value);
                        updateTotalInsuranceAmount(
                            {
                                ...values,
                                orgUnemploymentInsurancePercentage: value,
                            },
                            setFieldValue
                        );
                    }}
                />
            </Grid>
            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Số tiền BHTN công ty đóng"}
                    name='orgUnemploymentInsuranceAmount'
                    disabled
                />
            </Grid>
            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Tổng tiền bảo hiểm mà công ty đóng"}
                    name='orgTotalInsuranceAmount'
                    disabled
                />
            </Grid>

            <Grid item xs={12} sm={6} md={4}>
                <GlobitsVNDCurrencyInput
                    disabled
                    label={"Tổng tiền bảo hiểm"}
                    name='totalInsuranceAmount'
                />
            </Grid>
        </>
    );
}

export default memo(observer(StaffSocialInsuranceAutoSection));