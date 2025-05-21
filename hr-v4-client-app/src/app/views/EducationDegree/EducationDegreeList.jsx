import React from "react";
import GlobitsTable from "../../common/GlobitsTable";
import {useStore} from "../../stores";
import {useTranslation} from "react-i18next";
import {IconButton, Icon} from "@material-ui/core";
import Config from "../../common/GlobitsConfigConst";
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

export default observer(function EducationDegreeList() {
    const {educationDegreeStore} = useStore();
    const {t} = useTranslation();

    const {
        educationDegreeList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleEditEducationDegree,
        handleSelectListEducationDegree,
    } = educationDegreeStore;

    let columns = [
        {
            title: t("general.action"),
            ...Config.tableCellConfig,
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleEditEducationDegree(rowData.id);
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
            title: t("educationDegree.code"),
            field: "code",
            ...Config.tableCellConfig,
        },
        {
            title: t("educationDegree.name"),
            field: "name",
            ...Config.tableCellConfig,
        },
    ];
    return (
        <GlobitsTable
            selection
            handleSelectList={handleSelectListEducationDegree}
            data={educationDegreeList}
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
