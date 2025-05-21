import React, { memo, useEffect, useState } from "react";
import { observer } from "mobx-react";
import "react-toastify/dist/ReactToastify.css";
import { Grid, makeStyles, Button, Tooltip } from "@material-ui/core";
import { useFormikContext, Field } from "formik";
import GlobitsDateTimePicker from "../../../common/form/GlobitsDateTimePicker";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import TabAccordion from "app/common/Accordion/TabAccordion";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";

function SocialInsuranceInfo() {
    const { staffSocialInsuranceStore } = useStore();

    const { values, setFieldValue } = useFormikContext();
    const { t } = useTranslation();

    useEffect(function () {
        const staffInsuranceAmount = (Number.parseFloat(values?.insuranceSalary || 0) * Number.parseFloat(values?.staffPercentage || 0) / 100);
        setFieldValue("staffInsuranceAmount", staffInsuranceAmount);
    }, [values?.insuranceSalary, values?.staffPercentage]);

    useEffect(function () {
        const orgInsuranceAmount = (Number.parseFloat(values?.insuranceSalary || 0) * Number.parseFloat(values?.orgPercentage || 0) / 100);
        setFieldValue("orgInsuranceAmount", orgInsuranceAmount);
    }, [values?.insuranceSalary, values?.orgPercentage]);

    useEffect(function () {
        const unionDuesAmount = (Number.parseFloat(values?.insuranceSalary || 0) * Number.parseFloat(values?.unionDuesPercentage || 0) / 100);
        setFieldValue("unionDuesAmount", unionDuesAmount);
    }, [values?.insuranceSalary, values?.unionDuesPercentage]);


    useEffect(function () {
        const totalInsuranceAmount = (Number.parseFloat(values?.orgInsuranceAmount || 0)
            + Number.parseFloat(values?.staffInsuranceAmount || 0))
            + Number.parseFloat(values?.unionDuesAmount || 0);
        setFieldValue("totalInsuranceAmount", totalInsuranceAmount);

    }, [values?.orgInsuranceAmount, values?.staffInsuranceAmount, values?.unionDuesAmount]);


    return (
        <TabAccordion
            title='Thông tin BHXH'
            className="pb-0 mb-0"
        >
            <Grid container spacing={2}>
                <Grid item md={4} sm={6} xs={12} className="pt-25 pl-15">
                    <GlobitsCheckBox
                        label={t("Có đóng BHXH")}
                        name="hasSocialIns"
                    />
                </Grid>

                {/* Sổ BHXH */}
                <Grid item md={4} sm={6} xs={12}>
                    <GlobitsNumberInput
                        label={t("humanResourcesInformation.socialInsuranceNumber")}
                        name="socialInsuranceNumber"
                        inputProps={{ maxLength: 10 }}
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsDateTimePicker
                        label={"Ngày bắt đầu đóng BHXH"}
                        name="insuranceStartDate"
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsDateTimePicker
                        label={"Ngày kết thúc đóng BHXH"}
                        name="insuranceEndDate"
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsVNDCurrencyInput
                        label={"Mức lương đóng BHXH (theo hợp đồng hiện thời)"}
                        name="insuranceSalary"
                        disabled
                        placeholder="Mức lương BHXH theo hợp đồng hiện tại"
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsVNDCurrencyInput
                        label={"Tỷ lệ cá nhân đóng BHXH"}
                        name="staffPercentage"
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsVNDCurrencyInput
                        label={"Số tiền BHXH cá nhân đóng"}
                        name="staffInsuranceAmount"
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsVNDCurrencyInput
                        label={"Tỷ lệ đơn vị đóng BHXH"}
                        name="orgPercentage"
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsVNDCurrencyInput
                        label={"Số tiền BHXH đơn vị đóng"}
                        name="orgInsuranceAmount"
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsVNDCurrencyInput
                        label={"Tỷ lệ đóng phí công đoàn"}
                        name="unionDuesPercentage"
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsVNDCurrencyInput
                        label={"Số tiền đóng phí công đoàn"}
                        name="unionDuesAmount"
                    />
                </Grid>

                <Grid item xs={12} sm={6} md={4}>
                    <GlobitsVNDCurrencyInput
                        disabled
                        label={"Tổng tiền bảo hiểm"}
                        name="totalInsuranceAmount"
                    />
                </Grid>

                {/* <Grid item xs={12} sm={6} md={4}>
                <GlobitsNumberInput
                    label={"Hệ số lương đóng bảo hiểm"}
                    name="salaryCoefficient"
                    value={values.salaryCoefficient}
                />
                </Grid> */}
            </Grid>
        </TabAccordion>
    );
}

export default memo(observer(SocialInsuranceInfo));