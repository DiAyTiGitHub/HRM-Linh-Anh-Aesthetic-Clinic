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

export default observer(function TrainingBaseList() {
    const {trainingBaseStore} = useStore();
    const {t} = useTranslation();

    const {
        trainingBaseList,
        totalPages,
        totalElements,
        searchObject,
        setPageSize,
        handleChangePage,
        handleDelete,
        handleEditTrainingBase,
        handleSelectListTrainingBase,
    } = trainingBaseStore;

    let columns = [
        {
            title: t("general.action"),
            minWidth: "100px",
            ...Config.tableCellConfig,
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleEditTrainingBase(rowData.id);
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
            title: t("trainingBase.code"),
            minWidth: "100px",
            field: "code",
            ...Config.tableCellConfig,
        },
        {
            title: t("trainingBase.name"),
            minWidth: "200px",
            field: "name",
            ...Config.tableCellConfig,
        },
        {
            title: t("trainingBase.nameEng"),
            minWidth: "150px",
            field: "nameEng",
            ...Config.tableCellConfig,
        },
        {
            title: t("trainingBase.description"),
            minWidth: "200px",
            field: "description",
            ...Config.tableCellConfig,
        },
    ];
    return (
        <GlobitsTable
            selection
            handleSelectList={handleSelectListTrainingBase}
            data={trainingBaseList}
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
