import React from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import { observer } from "mobx-react";
import LocalConstants from "app/LocalConstants";

function MaterialButton(props) {
    const { item } = props;
    return (
        <div>
            <IconButton size="small" onClick={() => props.onSelect(item, 0)}>
                <Icon fontSize="small" color="primary">
                    edit
                </Icon>
            </IconButton>
            <IconButton size="small" onClick={() => props.onSelect(item, 1)}>
                <Icon fontSize="small" color="error">
                    delete
                </Icon>
            </IconButton>
        </div>
    );
}

export default observer(function List() {
    const { administrativeUnitStore } = useStore();
    const { t } = useTranslation();

    const {
        administrativeUnitList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleEditAdministrative,
        handleSelectListAdministrative,
    } = administrativeUnitStore;

    let columns = [
        {
            title: t("general.action"),
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleEditAdministrative(rowData.id);
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
            title: t("administrativeUnit.code"),
            field: "code",
            align: "left",
            width: "150",
        },
        { title: t("administrativeUnit.name"), field: "name", width: "150" },
        {
            title: t("administrativeUnit.level"),
            field: "parent.name",
            width: "150",
            render: (rowData) =>
                LocalConstants.AdminitractiveLevel.map((item) => {
                    if (item.value === rowData?.level) {
                        return item.name;
                    }
                    return ''
                }),
        },
        {
            title: t("administrativeUnit.parent"),
            field: "parent.name",
            width: "150",
            render: (rowData) => <span className='pr-6'>{rowData?.parent?.name}</span>,
        },
    ];
    return (
        <GlobitsTable
            selection
            data={administrativeUnitList}
            handleSelectList={handleSelectListAdministrative}
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
