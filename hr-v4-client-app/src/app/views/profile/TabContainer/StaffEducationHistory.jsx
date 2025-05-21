import React, {memo, useEffect, useState} from "react";
import {
    makeStyles,
    TableCell,
    Table,
    TableRow,
    TableBody,
    TableHead, Tooltip, IconButton, Icon,
} from "@material-ui/core";
import {useFormikContext} from "formik";
import {useTranslation} from "react-i18next";
import {useStore} from "../../../stores";
import GlobitsTable from "../../../common/GlobitsTable";
import {formatDate} from "../../../LocalFunction";
import {observer} from "mobx-react";

const useStyles = makeStyles((theme) => ({
    root: {
        background: "#E4f5fc",
        padding: "10px 15px",
        borderRadius: "5px",
    },
    groupContainer: {
        width: "100%",
        "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    },
    tableContainer: {
        marginTop: "8px",
    },
}));

function StaffEducationHistory() {
    const {t} = useTranslation();
    const classes = useStyles();
    const {values} = useFormikContext();
    const {staffEducationHistoryStore} = useStore();

    const {
        pagingEducationHistory,
        educationHistoryList,
        resetEducationHistoryStore,
    } = staffEducationHistoryStore;

    const columns = [
        {
            title: t("educationHistory.startDate"),
            field: "startDate",
            align: "left",
            minWidth: "150px",
            render: (data) => (data?.startDate ? <span>{formatDate("DD/MM/YYYY", data.startDate)}</span> : null),
        },
        {
            title: t("educationHistory.endDate"),
            field: "endDate",
            align: "left",
            minWidth: "150px",
            render: (data) => (data?.endDate ? <span>{formatDate("DD/MM/YYYY", data.endDate)}</span> : null),
        },
        {
            title: t("educationHistory.educationalInstitution"),
            field: "educationalInstitution.name",
            align: "left",
            minWidth: "150px",
            render: (data) => data?.educationalInstitution?.name || "",
        },
        {
            title: t("educationHistory.country"),
            field: "country.name",
            align: "left",
            minWidth: "150px",
            render: (data) => data?.country?.name || "",
        },
        {
            title: t("educationHistory.speciality"),
            field: "speciality.name",
            align: "left",
            minWidth: "150px",
            render: (data) => data?.speciality?.name || "",
        },
        {
            title: t("educationHistory.formsOfTraining"),
            field: "educationType.name",
            align: "left",
            minWidth: "150px",
            render: (data) => data?.educationType?.name || "",
        },
        {
            title: t("educationHistory.degree"),
            field: "educationDegree.name",
            align: "left",
            minWidth: "150px",
            render: (data) => data?.educationDegree?.name || "",
        },
        {
            title: t("educationHistory.note"),
            field: "description",
            align: "left",
            minWidth: "150px",
            render: (data) => data?.description || "",
        },
    ];
    useEffect(() => {
        if (values?.id) {
            pagingEducationHistory({staffId: values?.id})
        }
        return resetEducationHistoryStore;
    }, []);

    return (
        <div className={classes.groupContainer}>
            <div className={classes.tableContainer}>
                {educationHistoryList?.length > 0 ? (
                    <GlobitsTable
                        data={educationHistoryList}
                        columns={columns}
                        nonePagination
                    />
                ) : (
                    <h5 className="text-primary n-w">
                        Không có quá trình đào tạo nào!
                    </h5>
                )}
            </div>
        </div>
    );
};

export default memo(observer(StaffEducationHistory))