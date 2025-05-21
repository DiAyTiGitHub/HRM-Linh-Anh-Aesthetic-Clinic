import React, {useEffect} from "react";
import GlobitsTable from "../../../common/GlobitsTable";
import {useStore} from "../../../stores";
import {useTranslation} from "react-i18next";
import {IconButton, Icon} from "@material-ui/core";
import {observer} from "mobx-react";

function MaterialButton(props) {
    const {item} = props;
    return (
        <div>
            <IconButton size='small' onClick={() => props.onSelect(item, 0)}>
                <Icon fontSize='small' color='primary'>
                    edit
                </Icon>
            </IconButton>
            <IconButton size='small' onClick={() => props.onSelect(item, 1)}>
                <Icon fontSize='small' color='error'>
                    delete
                </Icon>
            </IconButton>
        </div>
    );
}

export default observer(function BudgetCategoryList() {
    const {budgetCategoryStore} = useStore();
    const {t} = useTranslation();

    const {
        budgetCategoryList,
        totalPages,
        totalElements,
        rowsPerPage,
        page,
        handleChangePage,
        setRowsPerPage,
        handleDelete,
        handleEditBudgetCategory,
        handleSelectListBudgetCategory,
    } = budgetCategoryStore;

    let columns = [
        {
            title: t("STT"),
            width: "80",
            render: (rowData, index) => rowData?.tableData?.id + 1, // Tăng chỉ số bắt đầu từ 1
            cellStyle: {textAlign: "center"},
            headerStyle: {textAlign: "center"},
        },
        {
            title: t("general.action"),
            cellStyle: {textAlign: "center"},
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleEditBudgetCategory(rowData.id);
                        } else if (method === 1) {
                            handleDelete(rowData.id);
                        } else {
                            alert("Call Selected Here:" + rowData.id);
                        }
                    }}
                />
            ),
        },
        {
            title: t("budgetCategory.code"), // Sử dụng key dịch
            field: "code",
            width: "150",
            // cellStyle: (index, rowData) => ({
            //     fontWeight: rowData?.hasChildren && "bold",
            //     fontSize: !rowData?.parent && "20px",
            // }),
        },
        {
            title: t("budgetCategory.name"), // Sử dụng key dịch
            field: "name",
            width: "150",
            // cellStyle: (index, rowData) => ({
            //     fontWeight: rowData?.hasChildren && "bold",
            //     fontSize: !rowData?.parent && "20px",
            // }),
        },
        {
            title: t("budgetCategory.description"), // Sử dụng key dịch
            field: "description",
            width: "150",
            // cellStyle: (index, rowData) => ({
            //     fontWeight: rowData?.hasChildren && "bold",
            //     fontSize: !rowData?.parent && "20px",
            // }),
        },
    ];

    return (
        <GlobitsTable
            selection
            data={budgetCategoryList}
            handleSelectList={handleSelectListBudgetCategory}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setRowsPerPage}
            pageSize={rowsPerPage}
            pageSizeOption={[10, 25, 50]}
            totalElements={totalElements}
            page={page}
            parentChildData={(row, rows) => rows.find((a) => a.id === row.parentId)}
            rowStyle={(rowData, index) => ({fontWeight: rowData.parentId ? "normal" : "bold"})}
            doubleSidePagination={false}
        />
    );
});
