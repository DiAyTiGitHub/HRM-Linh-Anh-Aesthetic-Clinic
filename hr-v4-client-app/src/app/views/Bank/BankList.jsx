import React from "react";
import GlobitsTable from "app/common/GlobitsTable";
import {useStore} from "app/stores";
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

export default observer(function BankList() {
    const {bankStore} = useStore();
    const {t} = useTranslation();

    const {
        bankList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleEdit,
        handleSelectListBank
    } = bankStore;

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
                            handleEdit(rowData.id);
                        } else if (method === 1) {
                            handleDelete(rowData.id);
                        } else {
                            alert(t("general.alert.callSelected") + rowData.id); // Dịch thông báo
                        }
                    }}
                />
            ),
        },
        {
            title: t("Tên ngân hàng"),
            field: "name",
            width: "150",
        },
        {
            title: t("Mã ngân hàng"),
            field: "code",
            width: "150",
        },
        {
            title: t("Mô tả mẫu ngân hàng"),
            field: "description",
            width: "150",
        },
    ];

    return (
        <GlobitsTable
            selection
            handleSelectList={handleSelectListBank}
            data={bankList}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 25, 50]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
});
