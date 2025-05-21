import React, { memo, useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate, formatVNDMoney } from "app/LocalFunction";
import { getDate } from "date-fns";
import CheckIcon from "@material-ui/icons/Check";
import PreviewFile from "app/views/StaffDocumentItem/PreviewFile";

function SalaryValueHistoriesList() {
    const { staffSalaryItemValueStore, hrRoleUtilsStore } = useStore();

    const { t } = useTranslation();

    const { listValueHistories } = staffSalaryItemValueStore;

    const { isAdmin, isManager, checkAllUserRoles, isCompensationBenifit } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();
    }, []);

    const columns = [
        // {
        //     title: "Mã thành phần",
        //     field: "salaryItem.code",
        //     width: "15%",
        //     align: "center",
        //     render: (rowData) => <span className='px-6'>{rowData?.salaryItem?.code}</span>,
        // },

        // {
        //     title: "Thành phần lương",
        //     field: "salaryItem.name",
        //     align: "center",
        //     minWidth: "120px",
        //     width: "30%",
        //     render: (rowData) => <span className='px-6'>{rowData?.salaryItem?.name}</span>,
        // },

        {
            title: "Từ ngày",
            field: "fromDate",
            align: "center",
            render: (row) => <span className='px-2'>{formatDate("DD/MM/YYYY", row?.fromDate)}</span>,
        },

        {
            title: "Đến ngày",
            field: "toDate",
            align: "center",
            render: (row) => (
                <span className='px-2'>
                    {row?.toDate ? formatDate("DD/MM/YYYY", row?.toDate) : row?.isCurrent ? "Nay" : ""}
                </span>
            ),
        },

        {
            title: "Giá trị",
            field: "value",
            minWidth: "120px",
            align: "center",
            render: (rowData) => (
                <span className='px-6'>
                    <strong>{formatVNDMoney(rowData?.value)}</strong>
                </span>
            ),
        },
        {
            title: "Hiện thời",
            field: "isCurrent",
            align: "center",
            render: (rowData) => (rowData?.isCurrent ? <CheckIcon fontSize='small' style={{ color: "green" }} /> : ""),
        },
    ];

    return <GlobitsTable data={listValueHistories} columns={columns} nonePagination />;
}

export default memo(observer(SalaryValueHistoriesList));
