import {Grid} from "@material-ui/core";
import {t} from "app/common/CommonFunctions";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import {useFormikContext} from "formik";
import {observer} from "mobx-react";
import {memo, useEffect} from "react";

function RecruitmentRequestQuantitiesSection() {
    const {
        values,
        setFieldValue
    } = useFormikContext();

    useEffect(function () {
        let totalQuantity = 0;
        if (values?.inPlanQuantity) {
            totalQuantity = Number(values?.inPlanQuantity);
        }
        if (values?.extraQuantity) {
            totalQuantity += Number(values?.extraQuantity);
        }

        setFieldValue("totalQuantity", totalQuantity);
    }, [values?.extraQuantity, values?.inPlanQuantity]);

    return (
        <Grid container spacing={2}>
            <Grid item sm={6} xs={12} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Số lượng trong định biên"}
                    name="inPlanQuantity"
                />
            </Grid>

            <Grid item sm={6} xs={12} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Số lượng tuyển lọc"}
                    name="extraQuantity"
                />
            </Grid>

            <Grid item sm={6} xs={12} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Tổng số lượng đề nghị tuyển"}
                    name="totalQuantity"
                    disabled
                />
            </Grid>

            <Grid item sm={6} xs={12} md={4}>
                <GlobitsVNDCurrencyInput
                    label={"Số lượng đăng tuyển"}
                    name="announcementQuantity"
                />
            </Grid>
        </Grid>
    );
}

export default memo(observer(RecruitmentRequestQuantitiesSection));

