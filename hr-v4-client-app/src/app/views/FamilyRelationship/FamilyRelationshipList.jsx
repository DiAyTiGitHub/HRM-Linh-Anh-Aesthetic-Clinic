import React from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import { observer } from "mobx-react";

function MaterialButton(props) {
    const { item } = props;
    return (
        <div className="w-100 flex flex-center justify-center">
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
export default observer(function FamilyRelationshipList() {
    const { familyRelationshipStore } = useStore();
    const { t } = useTranslation();

    const {
        familyRelationshipList,
        totalPages,
        totalElements,
        rowsPerPage,
        page,
        handleChangePage,
        setRowsPerPage,
        handleDelete,
        handleEditFamilyRelationship,
        handleSelectListFamilyRelationship,
    } = familyRelationshipStore;

    let columns = [
        {
            title: t("general.action"),
            width: "10%",
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleEditFamilyRelationship(rowData.id);
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
            title: t("country.code"),
            field: "code",
            align: "center",
            width: "10%",
        },
        {
            title: t("country.name"),
            align: "center",
            field: "name",
            width: "20%",
        },
        {
            title: t("country.description"),
            minWidth: "200px",
            field: "description",
            width: "150"
        },
    ];
    return (
        <GlobitsTable
            selection
            data={familyRelationshipList}
            handleSelectList={handleSelectListFamilyRelationship}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setRowsPerPage}
            pageSize={rowsPerPage}
            pageSizeOption={[10, 25, 50]}
            totalElements={totalElements}
            page={page}
        />
    );
});