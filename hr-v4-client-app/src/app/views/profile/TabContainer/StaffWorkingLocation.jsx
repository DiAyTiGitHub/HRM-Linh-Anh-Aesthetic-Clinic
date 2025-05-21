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
import {pagingStaffWorkingLocation} from "../../StaffWorkingLocation/StaffWorkingLocationService";
import CheckIcon from "@material-ui/icons/Check";

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

function StaffWorkingLocation() {
    const {t} = useTranslation();
    const classes = useStyles();
    const {values} = useFormikContext();
    const {
        staffWorkingLocationStore
    } = useStore();
    const {
        searchObject,
        handleSetSearchObject,
        staffWorkingLocationList,
        pagingStaffWorkingLocation
    } = staffWorkingLocationStore;

    useEffect(() => {
        if (values?.id) {
            const payload = {
                ...searchObject,
                staffId: values?.id,
                pageIndex: 1,
                pageSize: 9999
            };
            handleSetSearchObject(payload)

            pagingStaffWorkingLocation(values?.id);
        }
    }, [values?.id]);

    const columns = [
        {
            title: t("Địa điểm làm việc"),
            field: "workingLocation",
            minWidth: "150px",
            render: row => <span className="px-4">{row?.workingLocation}</span>,
        },

        {
            title: "Địa điểm chính",
            field: "isMainLocation",
            minWidth: "150px",
            align: "center",
            render: (data) => {
                if (data?.isMainLocation) return <CheckIcon fontSize='small' style={{color: "green"}}/>;
                return "";
            },
        },
    ];

    return (<div className={classes.groupContainer}>
        <div className={classes.tableContainer}>
            {staffWorkingLocationList?.length > 0 ? (
                <GlobitsTable
                    data={staffWorkingLocationList}
                    columns={columns}
                    maxWidth='100%'
                    nonePagination
                />) : (<h5 className="text-primary n-w">
                Chưa có địa điểm làm việc nào!
            </h5>)}
        </div>
    </div>);
};

export default memo(observer(StaffWorkingLocation))
