// ReusableSelectList.js
import React, { memo } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import { Checkbox, Radio, Tooltip } from "@material-ui/core";

const ReusableSelectList = (props) => {
    const { t } = useTranslation();

    const {
        multipleSelect = false,
        // Data
        data = [],
        selectedItems = [],

        // Pagination
        totalPages = 0,
        totalElements = 0,
        handleChangePage = () => {},
        setPageSize = () => {},
        pageSize = 10,
        page = 0,
        pageSizeOption = [10, 15, 25, 50, 100],

        // Selection
        handleSelectItems = () => {},
        idField = "id",

        // Columns
        columns = [],
        showCheckboxColumn = true,
        checkboxColumnTitle = "Lựa chọn",
        selectLabel = "Chọn",
        unselectLabel = "Bỏ chọn",
    } = props;

    function handleSelectItem(item) {
        console.log("handleSelectItem called with item:", item);
        console.log("multipleSelect value:", multipleSelect);
        console.log("Current selectedItems:", selectedItems);
        let newSelectedItems = [];

        if (multipleSelect) {
            // Chế độ chọn nhiều (checkbox)
            newSelectedItems = [...selectedItems];
            const isSelected = selectedItems.some((selectedItem) => selectedItem[idField] === item[idField]);

            if (isSelected) {
                newSelectedItems = selectedItems.filter((selectedItem) => selectedItem[idField] !== item[idField]);
            } else {
                newSelectedItems.push(item);
            }
        } else {
            // Chế độ chọn đơn (radio)
            newSelectedItems = [item]; // Chỉ giữ lại item mới chọn
        }

        handleSelectItems(newSelectedItems);
    }

    const selectionColumn = {
        title: checkboxColumnTitle,
        sorting: false,
        align: "center",
        width: "10%",
        cellStyle: {
            textAlign: "center",
        },
        render: (rowData) => {
            const isChecked = selectedItems?.some((item) => item[idField] === rowData[idField]);

            if (multipleSelect) {
                // Hiển thị checkbox nếu là chế độ chọn nhiều
                return (
                    <Tooltip title={isChecked ? unselectLabel : selectLabel} placement='top'>
                        <Checkbox
                            className='pr-16'
                            id={`checkbox-${rowData[idField]}`}
                            name='selected'
                            value={rowData[idField]}
                            checked={isChecked}
                            onClick={() => handleSelectItem(rowData)}
                        />
                    </Tooltip>
                );
            } else {
                // Hiển thị radio button nếu là chế độ chọn đơn
                return (
                    <Tooltip title={isChecked ? unselectLabel : selectLabel} placement='top'>
                        <Radio
                            className='pr-16'
                            id={`radio-${rowData[idField]}`}
                            name='selected'
                            value={rowData[idField]}
                            checked={isChecked}
                            onClick={() => handleSelectItem(rowData)}
                        />
                    </Tooltip>
                );
            }
        },
    };

    const tableColumns = showCheckboxColumn ? [selectionColumn, ...columns] : columns;

    return (
        <GlobitsTable
            data={data}
            columns={tableColumns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={pageSize}
            pageSizeOption={pageSizeOption}
            totalElements={totalElements}
            page={page}
        />
    );
};

export default memo(observer(ReusableSelectList));