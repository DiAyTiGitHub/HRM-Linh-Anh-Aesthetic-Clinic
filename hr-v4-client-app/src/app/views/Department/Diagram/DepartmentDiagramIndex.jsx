import { ReactFlowProvider } from "@xyflow/react";
import React, { useCallback, useEffect, useState } from "react";

import "@xyflow/react/dist/base.css";
import { DnDProvider } from "app/views/OrganizationDiagram/components/Context/DnDContext";
import DepartmentSidebar from "app/views/OrganizationDiagram/components/Toolbox/DepartmentSidebar";
import "app/views/OrganizationDiagram/styles.css";
import ProfileDiagramIndex from "app/views/profile/ProfileDiagramIndex";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { connect } from "react-redux";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";

import { t } from "app/common/CommonFunctions";
import { getChartByDepartmentId } from "app/views/OrganizationalChartData/OrganizationalChartDataService";
import * as Yup from "yup";

const initialValues = {
    position: {},
    department: {},
    positionId: "",
    numberOfLevel: 1,
};

const validationSchema = Yup.object().shape({
    position: Yup.object().required(t("validation.required")).nullable(),
    numberOfLevel: Yup.number().required(t("validation.required")).min(1, "Số nhỏ nhất là 1"),
});

const DepartmentDiagramIndex = observer(({ settings }) => {
    const { id } = useParams();
    // handle layout1 menu

    const [orgChartData, setOrgChartData] = useState({});

    // console.log("nodes", noeds)
    const handleSubmit = (values) => {
        let dto = { ...values };

        if (values?.department) {
            dto = { ...dto, departmentId: values?.department?.id };
        }
        getChartByDepartmentId(dto)
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
                    <Form autoComplete='off' style={{ height: "100%", position: "relative" }}>
                        <div style={{ height: "100%" }}>
                            <div className='reactflow-wrapper' style={{ height: "100%" }}>
                                <ProfileDiagramIndex orgChartData={orgChartData} setOrgChartData={setOrgChartData} showAll={true} />
                            </div>
                        </div>
                    </Form>
                );
            }}
        </Formik>
    );
});

export default DepartmentDiagramIndex;
