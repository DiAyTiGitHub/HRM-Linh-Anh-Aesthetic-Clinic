import React, { memo } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import { observer } from "mobx-react";
import LocalConstants from "app/LocalConstants";
import CheckIcon from '@material-ui/icons/Check';

function MaterialButton(props) {
    const { item, disabled = false } = props;
    return (
        <div>
            <IconButton
                disabled={disabled}
                size="small"
                onClick={() => {
                    props.onSelect(item)
                }}
                style={{
                    opacity: disabled ? 0.5 : 1
                }}
            >
                <Icon fontSize="small" color="primary">
                    edit
                </Icon>
            </IconButton>
        </div>
    );
}

function SalaryTemplateItemList() {
    const { salaryTemplateStore } = useStore();
    const { salaryTemplateItemStore } = useStore();

    const {
        handleSelectedSalaryTemplateItem,
        selectedSalaryTemplateItem
    } = salaryTemplateItemStore;

    const { t } = useTranslation();

    const {
        selectedSalaryTemplate
    } = salaryTemplateStore;

    const columns = [
        // {
        //     title: t("general.action"),
        //     minWidth: "48px",
        //     width: "15%",
        //     render: (rowData) => (
        //         <MaterialButton
        //             // disabled={rowData?.salaryItem?.calculationType !== LocalConstants.SalaryItemCalculationType.THRESHOLD.value}
        //             item={rowData}
        //             onSelect={(rowData) => {
        //                 handleSelectedSalaryTemplateItem(rowData);
        //             }}
        //         />
        //     ),
        // },

        // {
        //     title: "Tên cột",
        //     field: "displayOrder",
        //     align: "left",
        //     width: "8%",
        //     render: function (props) {
        //         console.log(props);

        //         return (
        //             <span className="flex flex-center w-100">
        //                 {index + 1}
        //             </span>
        //         );
        //     }
        // },

        {
            title: "Tên cột",
            field: "displayName",
            align: "center",
            width: "20%",
        },
        // {
        //     title: "Tham số",
        //     field: "code",
        //     align: "left",
        //     width: "20%",
        // },
        // {
        //     title: "Công thức",
        //     field: "formula",
        //     align: "left",
        //     width: "25%",
        //     render: rowData => {

        //         return (
        //             <div className="w-100 flex justify-left">
        //                 {rowData?.formula}
        //             </div>
        //         );
        //     },
        // },

        // {
        //     title: "Cách tính",
        //     field: "calculationType",
        //     align: "left",
        //     width: "20%",
        //     render: rowData => {
        //         if (rowData?.salaryItem?.calculationType)
        //             return LocalConstants.SalaryItemCalculationType.getListData().find(i => i.value == rowData?.calculationType)?.name;
        //         return "";
        //     },
        // },

        {
            title: "Mô tả",
            field: "description",
            align: "left",
            width: "40%",
        },

        {
            title: "Ẩn tại bảng lương",
            field: "hiddenOnSalaryBoard",
            align: "left",
            width: "20%",
            render: data => {
                if (data?.hiddenOnSalaryBoard)
                    return <CheckIcon fontSize="small" style={{ color: "green" }} />;
                return "";
            },
        },

        {
            title: "Ẩn tại phiếu lương",
            field: "hiddenOnPayslip",
            align: "left",
            width: "20%",
            render: data => {
                if (data?.hiddenOnPayslip)
                    return <CheckIcon fontSize="small" style={{ color: "green" }} />;
                return "";
            },
        },
    ];

    return (
        <GlobitsTable
            data={selectedSalaryTemplate?.templateItems || []}
            columns={columns}
            nonePagination={true}
        />
    );
}

export default memo(observer(SalaryTemplateItemList));
