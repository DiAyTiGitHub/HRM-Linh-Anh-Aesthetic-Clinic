import React from "react";
import GlobitsTable from "../../common/GlobitsTable";
import {useStore} from "../../stores";
import {useTranslation} from "react-i18next";
import {Icon, IconButton} from "@material-ui/core";
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

export default observer(function ProfessionalDegreeList() {
    const {professionalDegreeStore} = useStore();
    const {t} = useTranslation();

    const {
        professionalDegreeList,
        totalPages,
        totalElements,
        searchObject,
        setPageSize,
        handleChangePage,
        setRowsPerPage,
        handleDelete,
        handleEditProfessionalDegree,
        handleSelectListProfessionalDegree,
    } = professionalDegreeStore;

    let columns = [
        {
            title: t("general.action"),
            minWidth: "100px",
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleEditProfessionalDegree(rowData.id);
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
            title: t("professionalDegree.code"),
            minWidth: "100px",
            field: "code",
            align: "left",
        },
        {title: t("professionalDegree.name"), minWidth: "200px", field: "name"},
    ];
    return (
        <GlobitsTable
            selection
            data={professionalDegreeList}
            handleSelectList={handleSelectListProfessionalDegree}
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
