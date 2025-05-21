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

export default observer(function AcademicList() {
    const {academicStore} = useStore();
    const {t} = useTranslation();

    const {
        academicList,
        totalPages,
        totalElements,
        searchObject,
        setPageSize,
        handleChangePage,
        handleDelete,
        handleEditAcademic ,
        handleSelectListAcademic,
    } = academicStore;

    let columns = [
        {
            title: t("general.action"),
            minWidth: "100px",
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleEditAcademic(rowData.id);
                        } else if (method === 1) {
                            handleDelete(rowData.id);
                        } else {
                            alert("Call Selected Here:" + rowData.id);
                        }
                    }}
                />
            ),
        },
        {title: t("academic.code"), minWidth: "100px", field: "code", align: "left"},
        {title: t("academic.name"), minWidth: "200px", field: "name"},
    ];
    return (
        <GlobitsTable
            selection
            handleSelectList={handleSelectListAcademic}
            data={academicList}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
});
