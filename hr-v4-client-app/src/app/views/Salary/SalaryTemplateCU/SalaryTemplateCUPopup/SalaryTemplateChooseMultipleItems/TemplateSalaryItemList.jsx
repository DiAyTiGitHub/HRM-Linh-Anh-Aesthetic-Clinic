import React, { memo, useMemo } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import CheckIcon from '@material-ui/icons/Check';
import { Radio } from "@material-ui/core";

function MaterialButton(props) {
    const { item } = props;
    return (
        <div className="flex align-center justify-center">
            <IconButton size="small" onClick={() => props.onSelect(item, 0)}>
                <Icon fontSize="small" color="primary">
                    edit
                </Icon>
            </IconButton>
            <IconButton size="small" onClick={() => props.onSelect(item, 1)}>
                <Icon fontSize="small" color="secondary">
                    delete
                </Icon>
            </IconButton>
        </div>
    );
}

function ResultSalaryItemList(props) {
    const { t } = useTranslation();

    const { salaryItemStore } = useStore();

    const {
        listSalaryItem,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleSelectListDelete,
        handleOpenCreateEdit,
        getSalaryItemCalculationTypeName,
        getSalaryItemTypeName,
        getSalaryItemValueTypeName
    } = salaryItemStore;

    const { salaryTemplateStore } = useStore();
    const {
        handleChooseItem,
        chosenItemIds
    } = salaryTemplateStore;

    const columns = [
        {
            title: "Chọn",
            align: "center",
            cellStyle: {
                textAlign: "center",
            },
            render: function (rowData) {
                let isChecked = false;
                if (chosenItemIds?.length > 0 && Array.from(chosenItemIds).includes(rowData?.id)) {
                    isChecked = true;
                }

                return (
                    <Tooltip title="Chọn sử dụng" placement="top">
                        <Radio
                            className="pr-16"
                            id={`radio${rowData?.id}`}
                            name="radSelected"
                            value={rowData.id}
                            checked={isChecked}
                            onClick={(event) => handleChooseItem(rowData)}
                        />
                    </Tooltip>
                );
            }
        },
        {
            title: t("general.action"),
            width: "10%",
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleOpenCreateEdit(rowData.id);
                        } else if (method === 1) {
                            handleDelete(rowData);
                        } else {
                            alert("Call Selected Here:" + rowData.id);
                        }
                    }}
                />
            ),
        },
        {
            title: t("salaryItem.code"),
            width: "20%",
            field: "code",
            align: "center",
        },

        {
            title: t("salaryItem.name"),
            width: "20%",
            field: "name",
            align: "center",
        },

        // {
        //     title: t("salaryItem.type"),
        //     width: "20%",
        //     align: "left",
        //     render: data => {
        //         if (data?.type)
        //             return getSalaryItemTypeName(data?.type);
        //         return "";
        //     },
        // },

        {
            title: "Kiểu giá trị",
            width: "20%",
            align: "center",
            render: data => {
                if (data?.valueType)
                    return (
                        <span className="px-6 text-center">
                            {getSalaryItemValueTypeName(data?.valueType)}
                        </span>
                    );

                return "";
            },
        },

        {
            title: "Cách tính giá trị",
            width: "20%",
            align: "center",
            render: data => {
                if (data?.calculationType)
                    return (
                        <span className="px-6 text-center">
                            {getSalaryItemCalculationTypeName(data?.calculationType)}
                        </span>
                    );
                return "";
            },
        },

        // {
        //     title: "Cách tính giá trị",
        //     width: "20%",
        //     align: "left",
        //     render: data => {
        //         if (data?.calculationType)
        //             return getSalaryItemCalculationTypeName(data?.calculationType);
        //         return "";
        //     },
        // },

        // {
        //     title: "Tính thuế",
        //     field: "isTaxable",
        //     width: "10%",
        //     align: "center",
        //     render: data => {
        //         if (data?.isInsurable)
        //             return <CheckIcon fontSize="small" style={{ color: "green" }} />;
        //         return "";
        //     },
        // },

        // {
        //     title: "Tính bảo hiểm",
        //     field: "isInsurable",
        //     width: "10%",
        //     align: "center",
        //     render: data => {
        //         if (data?.isInsurable)
        //             return <CheckIcon fontSize="small" style={{ color: "green" }} />;
        //         return "";
        //     },
        // },

        // {
        //     title: "Đang sử dụng",
        //     field: "isActive",
        //     width: "10%",
        //     align: "center",
        //     render: data => {
        //         if (data?.isActive)
        //             return <CheckIcon fontSize="small" style={{ color: "green" }} />;
        //         return "";
        //     },
        // },
    ];

    return (
        <GlobitsTable
            // selection
            data={listSalaryItem}
            handleSelectList={handleSelectListDelete}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 15, 25, 50, 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
}

export default memo(observer(ResultSalaryItemList));
