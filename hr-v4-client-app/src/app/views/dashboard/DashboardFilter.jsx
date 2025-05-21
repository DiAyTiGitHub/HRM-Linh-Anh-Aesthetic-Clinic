import React, { useEffect } from "react";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import { Grid } from "@material-ui/core";
import LocalConstants from "../../LocalConstants";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import { useFormikContext } from "formik";

export default observer(function DashboardFilter() {
    const { dashboardStore } = useStore();

    const { handleFilters } = dashboardStore;

    const { values } = useFormikContext();

    useEffect(() => {
        if (values) {
            let searchObject = {
                timeReport: values.timeReport,
                yearReport: values.yearReport,
                monthReport: values.monthReport,
                weekReport: values.weekReport,
            };
            handleFilters(searchObject);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [values]);

    return (
        <Grid
            container
            spacing={2}
            style={{ fontSize: "13px", color: "rgb(113, 114, 118)" }}
            className="justify-end"
        >
            <Grid item md={3} xs={12}>
                <GlobitsSelectInput
                    label="Lọc theo"
                    name="timeReport"
                    keyValue="value"
                    options={LocalConstants.ListSortItem}
                />
            </Grid>

            {values?.timeReport === 3 && (
                <Grid item md={3} xs={12}>
                    <GlobitsTextField
                        label="Năm"
                        name="yearReport"
                        type="number"
                    />
                </Grid>
            )}

            {values?.timeReport === 2 && (
                <>
                    <Grid item md={3} xs={12}>
                        <GlobitsTextField label="Năm" name="yearReport" type="number" />
                    </Grid>
                    <Grid item md={3} xs={12}>
                        <GlobitsSelectInput
                            label="Tháng"
                            name="monthReport"
                            keyValue="value"
                            options={LocalConstants.ListMonth}
                        />
                    </Grid>
                </>
            )}

            {values?.timeReport === 1 && (
                <>
                    <Grid item md={3} xs={12}>
                        <GlobitsTextField label="Năm" name="yearReport" type="number" />
                    </Grid>
                    <Grid item md={3} xs={12}>
                        <GlobitsSelectInput
                            label="Tháng"
                            name="monthReport"
                            keyValue="value"
                            options={LocalConstants.ListMonth}
                        />
                    </Grid>
                    <Grid item md={3} xs={12}>
                        <GlobitsSelectInput
                            label="Tuần"
                            name="weekReport"
                            keyValue="value"
                            options={LocalConstants.ListFourWeek}
                        />
                    </Grid>
                </>
            )}
        </Grid>
    );
});
