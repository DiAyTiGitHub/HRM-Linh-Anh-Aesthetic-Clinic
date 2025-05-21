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
function SelectDepartmentList({ handleSelect }) {
    const { departmentStore } = useStore();
    const { t } = useTranslation();

    const classes = useStyles();
    const {
        departmentList,
        totalPages,
        totalElements,
        rowsPerPage,
        page,
        handleChangePage,
        setRowsPerPage,
        updatePageData,
    } = departmentStore;

    function handleSelectItem(_, department) {
        handleSelect(department);
    }

    const columns = [
        {
            title: t("general.popup.select"),
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
            title: t("department.code"),
            field: "code",
            ...Config.tableCellConfig,
        },
        { title: t("department.name"), field: "name", ...Config.tableCellConfig },
        {
            title: t("department.description"),
            field: "description",
            ...Config.tableCellConfig,
        },
    ];

    useEffect(function () {
        const initializeSearchObject = {
            pageSize: 10,
            pageIndex: 1,
        };

        updatePageData(initializeSearchObject);
    }, []);

    return (
        <div className={classes.globitsTableWraper}>
            <MaterialTable
                data={departmentList}
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
                setRowsPerPage={setRowsPerPage}
                pageSize={rowsPerPage}
                pageSizeOption={[10, 25, 50]}
                totalElements={totalElements}
                page={page}
            />
        </div>
    );
}

export default memo(observer(SelectDepartmentList));
