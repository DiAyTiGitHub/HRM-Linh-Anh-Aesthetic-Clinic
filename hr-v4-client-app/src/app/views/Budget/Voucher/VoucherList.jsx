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

export default observer(function VoucherList() {
    const {voucherStore} = useStore();
    const {t} = useTranslation();

    const {
        voucherList,
        totalPages,
        totalElements,
        rowsPerPage,
        page,
        handleChangePage,
        setRowsPerPage,
        handleDelete,
        handleEditVoucher,
        handleSelectListVoucher,
        search
    } = voucherStore;

    useEffect(() => {
        search();
    }, []);

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
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleEditVoucher(rowData.id);
                        } else if (method === 1) {
                            handleDelete(rowData.id);
                        }
                    }}
                />
            ),
        },
        {
            title: t("voucher.voucherCode"),
            field: "voucherCode",
            width: "150",
        },
        {
            title: t("voucher.voucherType"),
            field: "voucherType",
            width: "150",
            render: (rowData) => (rowData.voucherType === 1 ? t("voucher.typeIncome") : rowData.voucherType === -1 ? t("voucher.typeExpense") : ""),
        },
        {
            title: t("voucher.voucherDate"),
            field: "voucherDate",
            width: "150",
            render: (rowData) => {
                return new Date(rowData.voucherDate).toLocaleDateString("vi-VN", {
                    day: "2-digit",
                    month: "2-digit",
                    year: "numeric"
                });
            },
        },
        {
            title: t("voucher.budgetCategory"),
            field: "budgetCategory",
            width: "150",
            render: (rowData) =>
                rowData?.voucherItems?.map(item => item?.budgetCategory?.name).join(", ")
                || rowData?.budgetCategory?.name
                || "",
        },
        {
            title: t("voucher.totalAmount"),
            field: "totalAmount",
            width: "150",
            cellStyle: {
                textAlign: "right",
                paddingRight: "5px",
            },
            render: (rowData) => {
                const totalAmount = rowData.voucherItems?.map((item) => item.amount)?.reduce((a, b) => a + b, 0);

                const color = rowData.voucherType === 1 ? "green" : rowData.voucherType === -1 ? "red" : "black";

                return (
                    <span style={{color: color}}>
                        {totalAmount.toLocaleString("vi-VN", {
                            style: "currency",
                            currency: "VND",
                        })}
                    </span>
                );
            },
        },
        {
            title: t("voucher.linkedBudget"),
            field: "budget.name",
            width: "150",
        },
    ];

    return (
        <GlobitsTable
            selection
            data={voucherList}
            handleSelectList={handleSelectListVoucher}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setRowsPerPage}
            pageSize={rowsPerPage}
            pageSizeOption={[10, 25, 50]}
            totalElements={totalElements}
            page={page}
            parentChildData={(row, rows) => rows.find((a) => a.id === row.parentId)}
            rowStyle={(rowData, index) => ({
                fontWeight: rowData.parentId ? "normal" : "bold",
            })}
            doubleSidePagination={false}
        />
    );
})
;
