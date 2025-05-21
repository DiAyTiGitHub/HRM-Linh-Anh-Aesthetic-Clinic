import React from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Icon, IconButton } from "@material-ui/core";
import { observer } from "mobx-react";
import moment from "moment/moment";

function MaterialButton(props) {
    const { item } = props;
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

export default observer(function PersonCertificateList() {
    const { personCertificateStore } = useStore();
    const { t } = useTranslation();

    const {
        personCertificateList,
        handleDelete,
        handleEdit,
        handleSelectListPersonCertificate
    } = personCertificateStore;

    let columns = [
        // {
        //     title: t("STT"),
        //     minWidth: "80px",
        //     render: (rowData, index) => rowData?.tableData?.id + 1, // Tăng chỉ số bắt đầu từ 1
        //     cellStyle: {textAlign: "center"},
        //     headerStyle: {textAlign: "center"},
        // },
        {
            title: t("general.action"),
            minWidth: "100px",
            cellStyle: { textAlign: "center" },
            headerStyle: { textAlign: "center" },
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
            title: t("certificate.name"),
            field: "name",
            minWidth: "150px",
        },
        {
            title: t("certificate.level"),
            field: "level",
            minWidth: "150px",
        },
        {
            title: t("certificate.issueDate"),
            field: "issueDate",
            minWidth: "150px",
            render: (rowData) => rowData?.issueDate ? moment(rowData?.issueDate).format("DD/MM/YYYY") : "",
        },
    ];

    return (
        <GlobitsTable
            selection
            handleSelectList={handleSelectListPersonCertificate}
            data={personCertificateList}
            columns={columns}
            nonePagination
        />
    );
});
