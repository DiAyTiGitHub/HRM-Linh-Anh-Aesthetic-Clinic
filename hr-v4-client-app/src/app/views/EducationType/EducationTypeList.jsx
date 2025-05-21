import React from "react";
import GlobitsTable from "../../common/GlobitsTable";
import {useStore} from "../../stores";
import {useTranslation} from "react-i18next";
import {Icon, IconButton} from "@material-ui/core";
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

export default observer(function EducationTypeList() {
    const {educationTypeStore} = useStore();
    const {t} = useTranslation();

    const {
        educationTypeList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleEditEducationType,
        handleSelectListEducationType,
    } = educationTypeStore;

    let columns = [
        {
            title: t("general.action"),
            width: "40%",
            ...Config.tableCellConfig,
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleEditEducationType(rowData.id);
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
            title: t("educationType.code"),
            width: "40%",
            field: "code",
            ...Config.tableCellConfig,
        },
        {
            title: t("educationType.name"),
            width: "40%",
            field: "name",
            ...Config.tableCellConfig,
        },
        {
            title: t("educationType.nameEng"),
            width: "40%",
            field: "nameEng",
            ...Config.tableCellConfig,
        },
        {
            title: t("educationType.description"),
            // minWidth: "200px",
            field: "description",
            ...Config.tableCellConfig,
        },
    ];
    return (
        <GlobitsTable
            selection
            handleSelectList={handleSelectListEducationType}
            data={educationTypeList}
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
