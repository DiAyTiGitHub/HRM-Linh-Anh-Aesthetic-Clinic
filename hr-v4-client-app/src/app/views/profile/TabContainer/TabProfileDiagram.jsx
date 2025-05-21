/* eslint-disable react-hooks/exhaustive-deps */
import { Grid } from "@material-ui/core";
import { t } from "app/common/CommonFunctions";
import { getChartByPositionId } from "app/views/OrganizationalChartData/OrganizationalChartDataService";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import ProfileDiagramIndex from "app/views/profile/ProfileDiagramIndex";
import ProfileToolBar from "app/views/profile/components/ProfileToolBar";
// import PositionToolbar from "./components/Filter/PositionToolbar";

const initialValues = {
    position: {},
    positionId: "",
    numberOfLevel: 1,
};

const validationSchema = Yup.object().shape({
    // position: Yup.object().required(t("validation.required")).nullable(),
    // numberOfLevel: Yup.number().required(t("validation.required")).min(1, "Số nhỏ nhất là 1"),
});

function TabProfileDiagram(props) {
    const { t } = useTranslation();

    const [orgChartData, setOrgChartData] = useState({});
    const handleSubmit = (values) => {
        let dto = { ...values };

        if (values?.position) {
            dto = { ...dto, positionId: values?.position?.id };
        }
        console.log(values);
        getChartByPositionId(dto)
            .then(({ data }) => {
                console.log(data);
                setOrgChartData(data);
            })
            .catch((err) => {
                console.error(err);
            });
    };

    return (
        <Formik
            enableReinitialize
            initialValues={initialValues}
            validationSchema={validationSchema}
            onSubmit={handleSubmit}>
            {({ resetForm, values, setFieldValue, setValues }) => {
                return (
                    <Form autoComplete='off'>
                        <Grid container spacing={2} className='h-100'>
                            <Grid item xs={12} className='h-100'>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <ProfileToolBar setOrgChartData={setOrgChartData} />
                                    </Grid>

                                    <Grid item xs={12} style={{ height: "500px" }}>
                                        <ProfileDiagramIndex orgChartData={orgChartData} />
                                    </Grid>
                                </Grid>
                            </Grid>
                        </Grid>
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(TabProfileDiagram));
