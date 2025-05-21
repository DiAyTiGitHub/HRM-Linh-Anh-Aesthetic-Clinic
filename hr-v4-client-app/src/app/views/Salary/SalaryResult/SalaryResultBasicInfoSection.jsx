import React, { memo, useEffect } from "react";
import { useFormikContext } from "formik";
import { Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { convertToConstantFormat } from "app/common/CommonFunctions";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingSalaryPeriod } from "../SalaryPeriod/SalaryPeriodService";
import { pagingSalaryTemplates } from "../SalaryTemplate/SalaryTemplateService";
import { findBySalaryTemplatePeriod } from "app/views/HumanResourcesInformation/StaffService";
import { formatDate } from "app/LocalFunction";

function SalaryResultBasicInfoSection(props) {
    const { isDisabled } = props;
    const { values, setFieldValue } = useFormikContext();

    useEffect(function () {
        let autoNameValue = "";
        let autoCodeValue = "";

        if (values?.salaryPeriod?.id) {
            autoNameValue += `Bảng lương ${values?.salaryPeriod?.name}`;
        }

        if (values?.salaryTemplate?.id) {
            // autoNameValue += ` (Mẫu ${values?.salaryTemplate?.code})`;
            autoNameValue += ` theo ${values?.salaryTemplate?.name}`;
        }

        autoNameValue = autoNameValue.trim();
        if (autoNameValue.length > 0) {
            autoCodeValue = convertToConstantFormat(autoNameValue);
        }

        setFieldValue("name", autoNameValue);
        setFieldValue("code", autoCodeValue);

    }, [values?.salaryPeriod?.id, values?.salaryTemplate?.id]);


    // // nếu thêm mới; lấy mặc định ds nhân viên có mẫu bảng lương và chưa có kỳ lương này
    // useEffect(function () {
    //     if (!values?.id) {
    //         if (values?.salaryPeriod?.id && values?.salaryTemplate?.id) {
    //             findBySalaryTemplatePeriod({
    //                 salaryPeriodId: values?.salaryPeriod?.id,
    //                 salaryTemplateId: values?.salaryTemplate?.id
    //             }).then(({data}) => {
    //                 setFieldValue("staffs", data || []);
    //             }).catch((err) => {
    //                 console.error(err);
    //             })
    //         }
    //     }
    // }, [values?.salaryPeriod?.id, values?.salaryTemplate?.id, values?.id]);

    return (
        <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
                <GlobitsPagingAutocomplete
                    label={"Kỳ lương"}
                    name="salaryPeriod"
                    required
                    api={pagingSalaryPeriod}
                    disabled={isDisabled}
                    getOptionLabel={(option) =>
                        option?.name && option?.code
                            ? `${option.name} - ${option.code} (${formatDate("DD/MM/YYYY", option.fromDate)} - ${formatDate("DD/MM/YYYY", option.toDate)})`
                            : `${option?.name || option?.code || ""}`
                    }
                    
                />
            </Grid>

            <Grid item xs={12} sm={6}>
                <GlobitsPagingAutocomplete
                    label={"Sử dụng mẫu bảng lương"}
                    name="salaryTemplate"
                    // required
                    disabled={isDisabled}
                    api={pagingSalaryTemplates}
                />
            </Grid>

            <Grid item xs={12}>
                <GlobitsTextField
                    validate
                    label="Tên bảng lương"
                    name="name"
                    disabled={isDisabled}
                />
            </Grid>

            <Grid item xs={12}>
                <GlobitsTextField
                    validate
                    label="Mã bảng lương"
                    disabled={isDisabled}
                    name="code"
                />
            </Grid>

            <Grid item xs={12}>
                <GlobitsTextField
                    label="Mô tả"
                    disabled={isDisabled}
                    name="description"
                    multiline
                    rows={3}
                />
            </Grid>
        </Grid>

    );
}

export default memo(observer(SalaryResultBasicInfoSection));