import React from "react";
import GlobitsTable from "../../../common/GlobitsTable";
import {useStore} from "../../../stores";
import {useTranslation} from "react-i18next";
import {Icon, IconButton} from "@material-ui/core";
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

export default observer(function BudgetList() {
    const {budgetStore} = useStore();
    const {t} = useTranslation();

    const {
        budgetList,
        totalPages,
        totalElements,
        rowsPerPage,
        page,
        handleChangePage,
        setRowsPerPage,
        handleDelete,
        handleEditBudget,
        handleSelectListBudget,
    } = budgetStore;


    let columns = [
            {
                title: t("STT"),
                width: "80",
                render: (rowData, index) => rowData?.tableData?.id + 1, // Tăng chỉ số bắt đầu từ 1
                cellStyle: {textAlign: "center"},
                headerStyle: {textAlign: "center"},
            },
            {
                title: t("general.action"), // Dịch "Hành động"
                render: (rowData) => (
                    <MaterialButton
                        item={rowData}
                        onSelect={(rowData, method) => {
                            if (method === 0) {
                                handleEditBudget(rowData.id);
                            } else if (method === 1) {
                                handleDelete(rowData.id);
                            } else {
                                alert(t("general.alert.callSelected") + rowData.id); // Dịch thông báo
                            }
                        }}
                    />
                ),
                cellStyle: {textAlign: "center"},
            },
            {
                title: t("budget.code"),
                field: "code",
                cellStyle: (index, rowData) => ({
                    fontWeight: rowData?.hasChildren && "bold",
                    fontSize: !rowData?.parent && "20px",
                }),
            },
            {
                title: t("budget.name"), // Dịch "Tên"
                field: "name",
                cellStyle: (index, rowData) => ({
                    fontWeight: rowData?.hasChildren && "bold",
                    fontSize: !rowData?.parent && "20px",
                }),
            },
            {
                title: t("budget.currency"), // Dịch "Tiền tệ"
                field: "currency",
                render: (rowData) => rowData.currency || "VND", // Hiển thị mặc định 'VND' nếu không có thông tin
            },
            {
                title: t("budget.openingBalance"), // Dịch "Số dư đầu kỳ"
                field: "openingBalance",
                render: (rowData) =>
                    rowData.openingBalance.toLocaleString("vi-VN", {
                        style: "currency",
                        currency: "VND",
                    }),
                cellStyle: {
                    textAlign: "right",
                    paddingRight: "10px"
                },
            },
            {
                title: t("budget.endingBalance"), // Dịch "Số dư cuối kỳ"
                field: "endingBalance",
                render: (rowData) => {
                    const color = rowData?.endingBalance > 0 ? "green" : "red";

                    return (
                        <span style={{color: color}}>
                        {rowData?.endingBalance.toLocaleString("vi-VN", {
                            style: "currency",
                            currency: "VND",
                        })}
                  </span>
                    )
                },
                cellStyle: {
                    textAlign: "right",
                    paddingRight: "10px"
                }
                ,
            },
        ]
    ;

    return (
        <GlobitsTable
            selection
            data={budgetList}
            handleSelectList={handleSelectListBudget}
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
})
;
