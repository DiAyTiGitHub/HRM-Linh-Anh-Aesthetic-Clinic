import React, {useEffect, useState} from "react";
import {useFormikContext} from "formik";
import {useTranslation} from "react-i18next";
import {
    Icon, IconButton, makeStyles, Table, TableBody, TableCell, TableHead, TableRow, Tooltip,
} from "@material-ui/core";
import {formatDate} from "app/LocalFunction";
import {memo} from "react";
import GlobitsTable from "../../../common/GlobitsTable";
import LocalConstants from "../../../LocalConstants";
import {useStore} from "../../../stores";
import {observer} from "mobx-react";

const useStyles = makeStyles((theme) => ({
    root: {
        background: "#E4f5fc", padding: "10px 15px", borderRadius: "5px",
    }, groupContainer: {
        width: "100%", "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    }, tableContainer: {
        marginTop: "8px",
    },
}));

function StaffWorkingHistory() {
    const {t} = useTranslation();
    const classes = useStyles();
    const {values} = useFormikContext();
    const {staffWorkingHistoryStore} = useStore();
    const [staffWorkingHistory, setStaffWorkingHistory] = useState([]);

    const {
        pagingStaffWorkingHistory, staffWorkingHistoryList, resetStore,
    } = staffWorkingHistoryStore;
    const columns = [
        {
            title: t("staffWorkingHistory.startDate"),
            field: "startDate",
            align: "left",
            minWidth: "150px",
            render: (data) => (data?.startDate ? <span>{formatDate("DD/MM/YYYY", data.startDate)}</span> : null),
        },
        {
            title: t("staffWorkingHistory.endDate"),
            field: "endDate",
            align: "left",
            minWidth: "150px",
            render: (data) => (data?.endDate ? <span>{formatDate("DD/MM/YYYY", data.endDate)}</span> : ""),
        },
        {
            title: t("staffWorkingHistory.fromPosition"),
            field: "fromPosition.name",
            align: "center",
            minWidth: "150px",
            render: (data) => data?.fromPosition?.name || "",
        },
        {
            title: t("staffWorkingHistory.toPosition"),
            field: "toPosition.name",
            align: "center",
            minWidth: "150px",
            render: (data) => data?.toPosition?.name || "",
        },
        // {
        //     title: t("staffWorkingHistory.transferType"),
        //     field: "transferType",
        //     align: "center",
        //     minWidth: "150px",
        //     render: (data) => {
        //         const transferType = LocalConstants.StaffWorkingHistoryTransferType.find((item) => item.value === data?.transferType);
        //         return transferType ? transferType.name : "";
        //     },
        // },
        {
            title: t("staffWorkingHistory.note"),
            field: "note",
            align: "center",
            minWidth: "150px",
            render: (data) => data?.note || "",
        },
    ];

    useEffect(() => {
        if (staffWorkingHistoryList?.length > 0) {
            setStaffWorkingHistory(staffWorkingHistoryList.filter((item) => item.transferType !== 3));
        } else {
            setStaffWorkingHistory([]);
        }
    }, [staffWorkingHistoryList]);

    useEffect(() => {
        pagingStaffWorkingHistory({staffId: values.id});

        return resetStore;
    }, []);
    return (<div className={classes.groupContainer}>
        <div className={classes.tableContainer}>
            {staffWorkingHistory?.length > 0 ? (
                <GlobitsTable
                    data={staffWorkingHistory}
                    columns={columns}
                    maxWidth='100%'
                    nonePagination
                />) : (<h5 className="text-primary n-w">
                Không có quá trình công tác nào!
            </h5>)}
        </div>
    </div>);
};

export default memo(observer(StaffWorkingHistory))
