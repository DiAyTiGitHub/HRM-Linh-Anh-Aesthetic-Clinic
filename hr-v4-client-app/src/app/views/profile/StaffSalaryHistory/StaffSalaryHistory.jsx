import React, {memo, useEffect} from "react";
import {
    makeStyles,
    TableCell,
    Table,
    TableRow,
    TableBody,
    TableHead,
} from "@material-ui/core";
import {useFormikContext} from "formik";
import {useTranslation} from "react-i18next";
import {formatDate} from "app/LocalFunction";
import {useStore} from "../../../stores";
import GlobitsTable from "../../../common/GlobitsTable";

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

function StaffSalaryHistory() {

    const {t} = useTranslation();
    const classes = useStyles();
    const {values} = useFormikContext();
    const {staffSalaryHistoryStore} = useStore();
    const {getAllStaffSalaryHistoryByStaff, staffSalaryHistoryList, setStaffId} = staffSalaryHistoryStore;

    const columns = [
        {
            title: t("humanResourcesInformation.positionName"),
            field: "product.name",
            minWidth: "150px",
        },
        {
            title: t("humanResourcesInformation.staffTypeCode"),
            minWidth: "150px",
            render: (rowData) => formatDate("DD/MM/YYYY", rowData?.startDate),
        },
        {
            title: t("humanResourcesInformation.coefficientOverLevel"),
            field: "coefficientOverLevel",
            minWidth: "150px",
        },
        {
            title: t("humanResourcesInformation.percentage"),
            field: "percentage",
            minWidth: "150px",
        },
        {
            title: t("humanResourcesInformation.decisionCode"),
            field: "decisionCode",
            minWidth: "150px",
        },
        {
            title: t("humanResourcesInformation.decisionDate"),
            minWidth: "150px",
            render: (rowData) => formatDate("DD/MM/YYYY", rowData?.decisionDate),
        },
        {
            title: t("humanResourcesInformation.salaryIncrementType"),

           field: "salaryIncrementType",
            minWidth: "150px",
        },
    ];

    useEffect(() => {
        if (values?.id) {
            setStaffId(values?.id)
            getAllStaffSalaryHistoryByStaff(values?.id);
        }
    }, []);
    return (
        <div className={classes.groupContainer}>
            <div className={classes.tableContainer}>
                {staffSalaryHistoryList?.length > 0 ? (
                    <GlobitsTable
                        nonePagination
                        columns={columns}
                        data={staffSalaryHistoryList || []}/>
                ) : (
                    <h5 className="text-primary n-w">
                        Không có quá trình lương nào
                    </h5>
                )}
            </div>
        </div>
    );
};

export default memo(StaffSalaryHistory)