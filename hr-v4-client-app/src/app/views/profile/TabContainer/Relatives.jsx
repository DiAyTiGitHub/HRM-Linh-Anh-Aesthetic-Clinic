import { makeStyles, } from "@material-ui/core";
import { useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import React, { memo, useEffect } from "react";
import CheckIcon from '@material-ui/icons/Check';
import { useStore } from "../../../stores";
import moment from "moment";
import GlobitsTable from "../../../common/GlobitsTable";
import { observer } from "mobx-react";
import { getDate } from "app/LocalFunction";

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

function Relatives() {

    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();
    const { staffFamilyRelationshipStore } = useStore();
    const { t } = useTranslation();

    const {
        staffFamilyRelationshipList,
        getAllStaffFamilyRelationshipByStaffId,
        setStaffId
    } = staffFamilyRelationshipStore;

    let columns = [
        {
            title: t("relatives.name"),
            field: "fullName",
            minWidth: "150px",
        },
        {
            title: t("relatives.dob"),
            field: "birthDate",
            minWidth: "150px",
            render: (rowData) => rowData?.birthDate ? moment(rowData?.birthDate).format("DD/MM/YYYY") : "",
        },
        {
            title: t("relatives.relative"),
            field: "relative",
            minWidth: "150px",
            render: (rowData) => rowData?.familyRelationship?.name ? rowData?.familyRelationship?.name : "",

        },
        {
            title: t("relatives.taxCode"),
            field: "taxCode",
            minWidth: "150px",
        },
        {
            title: t("relatives.dependent"),
            field: "issueDate",
            minWidth: "150px",
            cellStyle: { textAlign: "center" },
            headerStyle: { textAlign: "center" },
            render: (rowData) => rowData?.isDependent ? <CheckIcon fontSize="small" style={{ color: "green" }} /> : "",
        },
        {
            title: t("relatives.dependentDeductionFromDate"),
            field: "dependentDeductionFromDate",
            minWidth: "150px",
            cellStyle: { textAlign: "center" },
            headerStyle: { textAlign: "center" },
            render: (rowData) => getDate(rowData?.dependentDeductionFromDate),
        },
        {
            title: t("relatives.dependentDeductionToDate"),
            field: "dependentDeductionToDate",
            minWidth: "150px",
            cellStyle: { textAlign: "center" },
            headerStyle: { textAlign: "center" },
            render: (rowData) => getDate(rowData?.dependentDeductionToDate),
        },
    ];

    useEffect(() => {
        setStaffId(values?.id)
        if (values?.id) {
            getAllStaffFamilyRelationshipByStaffId();
        }
    }, []);
    return (
        <div className={classes.groupContainer}>
            <div className={classes.tableContainer}>
                {staffFamilyRelationshipList?.length > 0 ? (
                    <GlobitsTable
                        data={staffFamilyRelationshipList}
                        columns={columns}
                        nonePagination
                    />
                ) : (
                    <h5 className="text-primary n-w">
                        Không có quan hệ thân nhân !
                    </h5>
                )}
            </div>
        </div>
    )
};

export default memo(observer(Relatives))