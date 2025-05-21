import React, { memo, useEffect } from "react";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    makeStyles, Tooltip, IconButton, Icon,
} from "@material-ui/core";
import moment from "moment";
import { useTranslation } from "react-i18next";
import { useFormikContext } from "formik";
import GlobitsTable from "../../../common/GlobitsTable";
import { observer } from "mobx-react";
import { formatDate, formatMoney, formatVNDMoney } from "../../../LocalFunction";
import { useStore } from "../../../stores";

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

function StaffInsuranceHistory() {
    const { t } = useTranslation();
    const { values } = useFormikContext();
    const { staffSocialInsuranceStore } = useStore();

    const classes = useStyles();
    
    const {
        pagingStaffSocialInsurance,
        listStaffSocialInsurance,
        searchObject,
    } = staffSocialInsuranceStore;

    const columns = [
        {
            title: t("Ngày bắt đầu"),
            field: "startDate",
            minWidth: "150px",
            render: (data) => (
                <span style={{ padding: "0 16px" }}>
                    {data?.startDate ? formatDate("DD/MM/YYYY", data?.startDate) : ""}
                </span>
            ),
        },
        {
            title: t("Ngày kết thúc"),
            field: "endDate",
            minWidth: "150px",
            render: (data) => (
                <span style={{ padding: "0 16px" }}>{data?.endDate ? formatDate("DD/MM/YYYY", data?.endDate) : ""}</span>
            ),
        },
        {
            title: t("humanResourcesInformation.insuranceSalary"),
            field: "insuranceSalary",
            minWidth: "150px",
            render: (data) => <span style={{ padding: "0 16px" }}>{formatMoney(data?.insuranceSalary)}</span>,
        },
        {
            title: t("humanResourcesInformation.staffPercentage"),
            field: "staffPercentage",
            minWidth: "150px",
            render: (data) => <span style={{ padding: "0 16px" }}>{`${formatVNDMoney(data?.staffPercentage)}%`}</span>,
        },
        {
            title: t("humanResourcesInformation.orgPercentage"),
            field: "orgPercentage",
            minWidth: "150px",
            render: (data) => <span style={{ padding: "0 16px" }}>{`${formatVNDMoney(data?.orgPercentage)}%`}</span>,
        },
        {
            title: t("humanResourcesInformation.staffInsuranceAmount"),
            field: "staffInsuranceAmount",
            minWidth: "150px",
            render: (data) => <span style={{ padding: "0 16px" }}>{formatMoney(data?.staffInsuranceAmount)}</span>,
        },
        {
            title: t("humanResourcesInformation.orgInsuranceAmount"),
            field: "orgInsuranceAmount",
            minWidth: "150px",
            render: (data) => <span style={{ padding: "0 16px" }}>{formatMoney(data?.orgInsuranceAmount)}</span>,
        },
        {
            title: t("humanResourcesInformation.totalInsuranceAmount"),
            field: "totalInsuranceAmount",
            minWidth: "150px",
            render: (data) => <span style={{ padding: "0 16px" }}>{formatMoney(data?.totalInsuranceAmount)}</span>,
        },
    ];

    useEffect(() => {
        if (values?.id) {
            searchObject.staffId = values?.id;
            searchObject.staff = { id: values?.id };
            pagingStaffSocialInsurance();
        }
    }, []);

    return (
        <div className={classes.groupContainer}>
            <div className={classes.tableContainer}>
                {listStaffSocialInsurance?.length > 0 ? (
                    <GlobitsTable
                        data={listStaffSocialInsurance}
                        columns={columns}
                        maxWidth='100%'
                        nonePagination
                    />
                ) : (
                    <h5 className="text-primary n-w">
                        Không có quá trình đóng BHXH!
                    </h5>
                )}
            </div>
        </div>
    );
};

export default memo(observer(StaffInsuranceHistory))