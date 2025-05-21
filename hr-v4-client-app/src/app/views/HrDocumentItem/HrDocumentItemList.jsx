import React from "react";
import {useTranslation} from "react-i18next";
import {Icon, IconButton} from "@material-ui/core";
import {observer} from "mobx-react";
import {useStore} from "app/stores";
import GlobitsTable from "app/common/GlobitsTable";

function MaterialButton(props) {
    const {item} = props;
    return (
        <div className="flex flex-center justify-center">
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

export default observer(function HrDocumentItemList() {
    const {hrDocumentItemStore} = useStore();
    const {t} = useTranslation();

    const {
        hrDocumentItemList,
        totalPages,
        totalElements,
        rowsPerPage,
        page,
        handleChangePage,
        setRowsPerPage,
        handleDelete,
        handleEditHrDocumentItem,
        handleSetSelectedListHrDocumentItem
    } = hrDocumentItemStore;

    let columns = [
        {
            title: t("general.action"),
            align: "center",
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleEditHrDocumentItem(rowData.id);
                        } else if (method === 1) {
                            handleDelete(rowData.id);
                        } else {
                            alert(t("general.alert.callSelected") + rowData.id);
                        }
                    }}
                />
            ),
        },

        {
            title: t("hrDocumentItem.code"),
            field: "code",
            align: "center",
            width: "10%",
        },
        {
            title: t("hrDocumentItem.name"),
            field: "name",
            align: "center",
            width: "30%",
        },

        // {
        //     title: t("hrDocumentItem.displayOrder"),
        //     field: "displayOrder",
        //     align: "center",
        //     width: "20%",
        // },

        {
            title: t("hrDocumentItem.description"),
            field: "description",
            width: "50%",
        },

    ];

    return (
        <GlobitsTable
            selection
            handleSelectList={handleSetSelectedListHrDocumentItem}
            data={hrDocumentItemList}
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
