import React from "react";
import GlobitsTable from "../../common/GlobitsTable";
import {useStore} from "../../stores";
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

export default observer(function EducationalManagementLevelList() {
    const {educationalManagementLevelStore} = useStore();
    const {t} = useTranslation();

    const {
        educationalManagementLevelList,
        totalPages,
        totalElements,
        handleChangePage,
        setPageSize,
        searchObject,
        handleDelete,
        handleEditEducationalManagementLevel,
        handleSelectListEducationalManagementLevel,
    } = educationalManagementLevelStore;

    let columns = [
        {
            title: t("general.action"),
            minWidth: "100px",
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleEditEducationalManagementLevel(rowData.id);
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
            title: t("educationalManagementLevel.code"),
            minWidth: "100px",
            field: "code",
            align: "left",
            width: "150",
        },
        {
            title: t("educationalManagementLevel.name"),
            minWidth: "200px",
            field: "name",
            width: "150",
        },
    ];
    return (
        <GlobitsTable
            selection
            data={educationalManagementLevelList}
            handleSelectList={handleSelectListEducationalManagementLevel}
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
