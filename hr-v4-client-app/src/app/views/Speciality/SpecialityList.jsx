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

export default observer(function SpecialityList() {
    const {specialityStore} = useStore();
    const {t} = useTranslation();

    const {
        specialityList,
        totalPages,
        totalElements,
        handleChangePage,
        setPageSize,
        searchObject,
        handleDelete,
        handleEditSpeciality,
        handleSelectListSpeciality,
    } = specialityStore;

    let columns = [
        {
            title: t("general.action"),
            width: "20%",
            ...Config.tableCellConfig,
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleEditSpeciality(rowData.id);
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
            title: t("speciality.code"),
            width: "30%",
            field: "code",
            ...Config.tableCellConfig,
        },
        {
            title: t("speciality.name"),
            minWidth: "200px",
            field: "name",
            ...Config.tableCellConfig,
        },
        {
            title: t("speciality.nameEng"),
            minWidth: "200px",
            field: "nameEng",
            ...Config.tableCellConfig,
        },
        // {
        //   title: t("speciality.description"),
        //   minWidth: "200px",
        //   field: "description",
        //   ...Config.tableCellConfig,
        // },
    ];
    return (
        <GlobitsTable
            selection
            handleSelectList={handleSelectListSpeciality}
            data={specialityList}
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
