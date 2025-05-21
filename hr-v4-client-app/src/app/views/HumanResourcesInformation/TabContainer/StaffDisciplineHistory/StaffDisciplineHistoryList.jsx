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

export default observer(function StaffDisciplineHistoryList() {
    const {staffDisciplineHistoryStore} = useStore();
    const {t} = useTranslation();

    const {
        staffDisciplineHistoryList,
        handleDelete,
        handleEdit,
        handleSelectListStaffDisciplineHistory
    } = staffDisciplineHistoryStore;
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
            title: t("disciplineHistory.organization"),
            minWidth: "150px",
            field: "organization.name"
        },
        {
            title: t("disciplineHistory.department"),
            minWidth: "150px",
            field: "department.name"
        },
        {
            title: t("disciplineHistory.disciplineDate"),
            field: "disciplineDate",
            minWidth: "150px",
            render: (value) => value?.disciplineDate ? formatDate("DD/MM/YYYY", value?.disciplineDate) : "",
        },
        {
            title: t("disciplineHistory.disciplineType"),
            field: "discipline.name",
            minWidth: "150px",
        },
    ];

    return (
        <GlobitsTable
            selection
            handleSelectList={handleSelectListStaffDisciplineHistory}
            data={staffDisciplineHistoryList}
            columns={columns}
            nonePagination
        />
    );
});
