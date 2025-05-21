import React, {memo, useEffect} from "react";
import {makeStyles,} from "@material-ui/core";
import {useFormikContext} from "formik";
import {useTranslation} from "react-i18next";
import {formatDate} from "app/LocalFunction";
import GlobitsTable from "app/common/GlobitsTable";
import {observer} from "mobx-react";
import {useStore} from "app/stores";

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

function StaffAgreements() {

    const {t} = useTranslation();
    const classes = useStyles();
    const {values} = useFormikContext();
    const {staffLabourAgreementStore} = useStore();
    const {
        pagingStaffLabourAgreement,
        listStaffLabourAgreement,
        searchObject,
    } = staffLabourAgreementStore;
    const columns = [
        {
            title: t("agreements.labourAgreementNumber"),
            field: "labourAgreementNumber",
            minWidth: "150px",
            align: "left",
        },
        {
            title: t("agreements.signedDate"),
            field: "signedDate",
            align: "left",
            minWidth: "150px",
            render: data => data?.signedDate && (<span>{formatDate("DD/MM/YYYY", data?.signedDate)}</span>)
        },
        {
            title: t("agreements.startDate"),
            field: "startDate",
            align: "left",
            minWidth: "150px",
            render: data => data?.startDate && (<span>{formatDate("DD/MM/YYYY", data?.startDate)}</span>)
        },
        {
            title: t("agreements.endDate"),
            field: "endDate",
            align: "left",
            minWidth: "150px",
            render: data => data?.endDate && (<span>{formatDate("DD/MM/YYYY", data?.endDate)}</span>)
        },
        {
            title: t("agreements.contractType"),
            field: "contractType",
            align: "left",
            minWidth: "150px",
            render: data => <span>{data?.contractType?.name}</span>
        },
        {
            title: t("Mẫu bảng lương"),
            field: "salaryTemplateName",
            align: "left",
            minWidth: "150px",
            render: data => <span>{data?.salaryTemplate?.name}</span>
        },
    ];

    useEffect(() => {
        if (values?.id) {
            searchObject.staffId = values?.id;
            searchObject.staff = {id: values?.id};
            pagingStaffLabourAgreement()
        }
    }, []);
    return (
        <div className={classes.groupContainer}>
            <div className={classes.tableContainer}>
                {listStaffLabourAgreement?.length > 0 ? (
                    <GlobitsTable
                        data={listStaffLabourAgreement}
                        columns={columns}
                        nonePagination
                    />
                ) : (
                    <h5 className="text-primary n-w">
                        Không có hợp đồng nào!
                    </h5>
                )}
            </div>
        </div>
    );
};

export default memo(observer(StaffAgreements))