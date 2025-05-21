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

function StaffSalaryTemplate() {

    const {t} = useTranslation();
    const classes = useStyles();
    const {values} = useFormikContext();
    const {staffSalaryTemplateStore} = useStore();
    const {
        pagingStaffSalaryTemplate,
        staffSalaryTemplateList,
        searchObject,
    } = staffSalaryTemplateStore;

    const columns = [
        {
            title: t("Mẫu bảng lương"),
            field: "salaryTemplate.name",
            minWidth: "150px",
            align: "left"
        },
        {
            title: t("Thời gian bắt đầu"),
            field: "fromDate",
            align: "left",
            minWidth: "150px",
            render: (data) => data?.fromDate && <span>{formatDate("DD/MM/YYYY", data?.fromDate)}</span>,
        },
        {
            title: t("Thời gian kết thúc"),
            field: "toDate",
            align: "left",
            minWidth: "150px",
            render: (data) => data?.toDate && <span>{formatDate("DD/MM/YYYY", data?.toDate)}</span>,
        },
    ]
    useEffect(() => {
        if (values?.id) {
            searchObject.staffId = values?.id;
            searchObject.staff = {id: values?.id};
            pagingStaffSalaryTemplate();
        }
    }, [values?.id]);
    return (
        <div className={classes.groupContainer}>
            <div className={classes.tableContainer}>
                {staffSalaryTemplateList?.length > 0 ? (
                    <GlobitsTable
                        nonePagination
                        columns={columns}
                        data={staffSalaryTemplateList || []}/>
                ) : (
                    <h5 className="text-primary n-w">
                        Chưa có mẫu bảng lương!
                    </h5>
                )}
            </div>
        </div>
    );
};

export default memo(observer(StaffSalaryTemplate))