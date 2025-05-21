import React, { memo } from "react";
import { useFormikContext } from "formik";
import { Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { convertToConstantFormat } from "app/common/CommonFunctions";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingSalaryPeriod } from "../../SalaryPeriod/SalaryPeriodService";
import { pagingSalaryTemplates } from "../../SalaryTemplate/SalaryTemplateService";

function SalaryResultInfoSection() {
    const { values, setFieldValue } = useFormikContext();

    function handleAutoRenderCode(e) {
        const value = e.target.value;
        setFieldValue("name", value);
        const autoRenderedCode = convertToConstantFormat(value);
        setFieldValue("code", autoRenderedCode);
    }

    function handleAutoRenderName(_, value) {
        setFieldValue("salaryPeriod", value);

        if (value && value?.name) {
            const autoRenderedName = "Bảng lương " + value?.name + " (" + value?.code + ")";
            setFieldValue("name", autoRenderedName);

            const autoRenderedCode = convertToConstantFormat(autoRenderedName);
            setFieldValue("code", autoRenderedCode);
        }
    }

    return (
        <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
                <GlobitsPagingAutocomplete
                    label={"Kỳ lương"}
                    name="salaryPeriod"
                    required
                    api={pagingSalaryPeriod}
                    onChange={handleAutoRenderName}
                    disabled
                />
            </Grid>

            <Grid item xs={12} sm={6}>
                <GlobitsPagingAutocomplete
                    label={"Sử dụng mẫu bảng lương"}
                    name="salaryTemplate"
                    required
                    disabled
                    api={pagingSalaryTemplates}
                />
            </Grid>

            <Grid item xs={12} sm={6}>
                <GlobitsTextField
                    validate
                    label="Tên bảng lương"
                    name="name"
                    onChange={handleAutoRenderCode}
                />
            </Grid>

            <Grid item xs={12} sm={6}>
                <GlobitsTextField
                    validate
                    label="Mã bảng lương"
                    name="code"
                />
            </Grid>

            <Grid item xs={12}>
                <GlobitsTextField
                    label="Mô tả"
                    name="description"
                    multiline
                    rows={3}
                />
            </Grid>
        </Grid>

    );
}

export default memo(observer(SalaryResultInfoSection));