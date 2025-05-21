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

function StaffIntroduceCost() {
    const {t} = useTranslation();
    const classes = useStyles();
    const {values} = useFormikContext();

    const {hrIntroduceCostStore} = useStore();
    const {
        pagingHrIntroduceCost,
        searchObject,
        handleSetSearchObject,
        resetStore,
        hrIntroduceCostList
    } = hrIntroduceCostStore;

    useEffect(() => {
        if (values?.id) {
            const payload = {
                ...searchObject,
                staffId: values?.id,
                pageIndex: 1,
                pageSize: 9999
            };
            handleSetSearchObject(payload)

            pagingHrIntroduceCost(values?.id);
        }
        return resetStore
    }, [values?.id]);

    const columns = [
        {
            title: t("Thứ tự nhập xuất excel"),
            field: "periodOrder",
            minWidth: "150px",
            render: (row) => <span>{row?.periodOrder}</span>,
        },
        {
            title: t("Tháng tính giới thiệu"),
            field: "introducePeriod",
            minWidth: "150px",
            render: row => <span>{formatDate("DD/MM/YYYY", row?.introducePeriod)}</span>,
        },
        {
            title: t("Chi phí được hưởng"),
            field: "cost",
            minWidth: "150px",
            render: (data) => (
                <span>
          {typeof data?.cost === "number"
              ? new Intl.NumberFormat("vi-VN", {
                  style: "currency",
                  currency: "VND",
              }).format(data.cost)
              : data?.cost}
        </span>
            ),
        },
    ];

    return (<div className={classes.groupContainer}>
        <div className={classes.tableContainer}>
            {hrIntroduceCostList?.length > 0 ? (
                <GlobitsTable
                    data={hrIntroduceCostList}
                    columns={columns}
                    maxWidth='100%'
                    nonePagination
                />) : (<h5 className="text-primary n-w">
                Chưa có chi phí giới thiệu nào!
            </h5>)}
        </div>
    </div>);
};

export default memo(observer(StaffIntroduceCost))
