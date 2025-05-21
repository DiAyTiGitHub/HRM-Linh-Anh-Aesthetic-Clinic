import React, { memo, useEffect } from "react";
import { observer } from "mobx-react";
import GlobitsPagination from "app/common/GlobitsPagination";
import MaterialTable from "material-table";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { makeStyles, Radio } from "@material-ui/core";
import Config from "app/common/GlobitsConfigConst";

const useStyles = makeStyles((theme) => ({
    globitsTableWraper: {
        width: "100%",
        "& td": {
            // borderBottom: 'unset !important',
            border: "1px solid #ccc",
            paddingLeft: "4px",
        },
        "& th": {
            // borderBottom: 'unset !important',
            border: "1px solid #ccc",
            fontWeight: "600",
            color: "#000000",
        },
        border: "0 !important",
        // borderRadius: "5px",
        overflow: "hidden",
        backgroundColor: "white",

        "& .MuiCheckbox-root": {
            display: "flex",
            justifyContent: "center",
            margin: 0,
        },

        "& .MuiPaper-elevation2": {
            boxShadow: "none",
        },

        "& .mat-mdc-row:hover": {
            backgroundColor: "red",
        },
    },
}));
function SelectOrganizationList({ handleSelect }) {
    const { organizationStore } = useStore();
    const { t } = useTranslation();

    const { listOrganizations, totalPages, totalElements, searchObject, handleChangePage, setPageSize } =
        organizationStore;

    function handleSelectItem(_, organization) {
        handleSelect(organization);
    }

    const columns = [
        {
            title: t("general.popup.select"),
            align: "center",
            sorting: false,
            render: (rowData) => (
                <Radio
                    id={`radio${rowData?.id}`}
                    name='radSelected'
                    value={rowData?.id}
                    onClick={(event) => handleSelectItem(event, rowData)}
                />
            ),
        },

        {
            title: "Mã đơn vị",
            field: "code",
            align: "left",
        },
        {
            title: "Tên đơn vị",
            field: "name",
            align: "left",
        },
        {
            title: "Website",
            field: "website",
        },
    ];

    const classes = useStyles();
    return (
        <div className={classes.globitsTableWraper}>
            <MaterialTable
                data={listOrganizations}
                columns={columns}
                parentChildData={(row, rows) => {
                    var list = rows.find((a) => a?.id === row?.parentId);
                    return list;
                }}
                options={{
                    selection: false,
                    sorting: false,
                    actionsColumnIndex: -1,
                    paging: false,
                    search: false,
                    toolbar: false,
                    draggable: false,
                    headerStyle: {
                        color: "#000",
                        paddingLeft: "4px",
                        paddingTop: "8px",
                        paddingBottom: "8px",
                        fontSize: "14px",
                        // "& nth-child(0)": {
                        //   textAlign: "center",
                        // },
                        textAlign: "center",
                    },
                    rowStyle: (rowData, index) => {
                        return {
                            backgroundColor: !(index % 2 === 1) ? "#fbfcfd" : "#ffffff",
                            textAlign: "center",
                            color: "red",
                        };
                    },
                    filterCellStyle: (row, index) => {
                        // console.log("filterCellStyle", row);
                        // console.log("filterCellStyle", index);
                    },
                }}
                onSelectionChange={(rows) => {
                    this.data = rows;
                }}
            />
            <GlobitsPagination
                totalPages={totalPages}
                handleChangePage={handleChangePage}
                setRowsPerPage={setPageSize}
                pageSize={searchObject?.pageSize}
                pageSizeOption={[10, 25, 50]}
                totalElements={totalElements}
                page={searchObject?.pageIndex}
            />
        </div>
    );
}

export default memo(observer(SelectOrganizationList));
