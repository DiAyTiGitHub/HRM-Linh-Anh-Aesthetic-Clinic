import { Grid } from "@material-ui/core";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { countNumberOfPositionInDepartmentWithPositionTitle } from "../Position/PositionService";

function HrResourcePlanCurrentNumber() {
    const { t } = useTranslation();
    const { values, setFieldValue } = useFormikContext();

    useEffect(() => {
        if (values?.department && values?.positionTitle) {
            let dto = {
                departmentId: values?.department?.id,
                positionTitleId: values?.positionTitle?.id,
            };
            countNumberOfPositionInDepartmentWithPositionTitle(dto)
                .then(({ data }) => {
                    setFieldValue("currentNumber", data || 0);
                })
                .catch((err) => {
                    console.log(err);
                });
        }
    }, [values?.department, values?.positionTitle]);

    return (
        <Grid item xs={12} sm={6}>
            <GlobitsNumberInput label={t("Số lượng hiện thời")} name='currentNumber' type='number' disabled />
        </Grid>
    );
}

export default memo(observer(HrResourcePlanCurrentNumber));
