import React from "react";
import GlobitsTable from "app/common/GlobitsTable";
import {useStore} from "app/stores";
import {useTranslation} from "react-i18next";
import {Icon, IconButton} from "@material-ui/core";
import {observer} from "mobx-react";
import {formatDate} from "app/LocalFunction";

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

export default observer(function PersonCertificateList() {
    const {staffRewardHistoryStore} = useStore();
    const {t} = useTranslation();

    const {
        staffRewardHistoryList,
        handleDelete,
        handleEdit,
        handleSelectListStaffRewardHistory
    } = staffRewardHistoryStore;

    let columns = [
        {
            title: t("general.action"),
            minWidth: "100px",
            cellStyle: {textAlign: "center"},
            headerStyle: {textAlign: "center"},
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
            title: t("rewardHistory.organization"),
            minWidth: "150px",
            field: "organization.name"
        },
        {
            title: t("rewardHistory.department"),
            minWidth: "150px",
            field: "department.name"
        },
        {
            title: t("rewardHistory.rewardDate"),
            field: "rewardDate",
            minWidth: "150px",
            render: (value) => formatDate("DD/MM/YYYY", value.rewardDate),
        },
        {
            title: t("rewardHistory.rewardType"),
            field: "rewardType.name",
            minWidth: "150px",
        },
    ];

    return (
        <GlobitsTable
            selection
            handleSelectList={handleSelectListStaffRewardHistory}
            data={staffRewardHistoryList}
            columns={columns}
            nonePagination
        />
    );
});
