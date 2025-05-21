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

function StaffsWithChosenTemplateInPeriodSection(props) {
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
            autoNameValue += ``;
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
           
        </Grid>

    );
}

export default memo(observer(StaffsWithChosenTemplateInPeriodSection));