import React, {memo, useEffect, useState} from "react";
import {
    Icon,
    IconButton,
    makeStyles,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow, Tooltip,
} from "@material-ui/core";
import {useTranslation} from "react-i18next";
import {useFormikContext} from "formik";
import {formatDate} from "app/LocalFunction";
import {observer} from "mobx-react";
import GlobitsTable from "../../../common/GlobitsTable";
import moment from "moment/moment";
import {useStore} from "../../../stores";

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


function StaffMaternityHistory() {
    const {t} = useTranslation();
    const classes = useStyles();
    const {values} = useFormikContext();
    const {staffMaternityHistoryStore} = useStore();
    const {
        staffMaternityHistoryList,
        pagingStaffMaternityHistory,
        resetStore,
        setCurrentStaffId,
    } = staffMaternityHistoryStore;
    const columns = [
        {
            title: t("maternityHistory.startDate"),
            field: "startDate",
            minWidth: "150px",
            render: (rowData) => (rowData.startDate ? moment(rowData.startDate).format("DD/MM/YYYY") : ""),
        },
        {
            title: t("maternityHistory.endDate"),
            field: "endDate",
            minWidth: "150px",
            render: (rowData) => (rowData.endDate ? moment(rowData.endDate).format("DD/MM/YYYY") : ""),
        },
        {
            title: t("maternityHistory.birthNumber"),
            field: "birthNumber",
            minWidth: "150px",
            render: (rowData) => rowData.birthNumber || "",
        },
        {
            title: t("maternityHistory.note"),
            field: "note",
            align: "left",
            minWidth: "150px",
            render: (rowData) => rowData.note || "",
        },
    ];

    useEffect(() => {
        if (values?.id) {
            setCurrentStaffId(values?.id);
            pagingStaffMaternityHistory();
        }
        return resetStore;
    }, []);
    return (
        <div className={classes.groupContainer}>
            <div className={classes.tableContainer}>
                {staffMaternityHistoryList?.length > 0 ? (
                    <GlobitsTable
                        nonePagination
                        columns={columns}
                        data={staffMaternityHistoryList || []}/>
                ) : (
                    <h5 className="text-primary n-w">
                        Không có quá trình thai sản nào!
                    </h5>
                )}
            </div>
        </div>
    );
};

export default memo(observer(StaffMaternityHistory));